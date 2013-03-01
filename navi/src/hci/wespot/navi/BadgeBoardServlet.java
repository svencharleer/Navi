package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.*;
import javax.servlet.http.*;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;



@SuppressWarnings("serial")
public class BadgeBoardServlet extends HttpServlet {
	


		
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		BadgeRepository repository = BadgeRepository.getRepository();
	
		
		
		Collection<JoseStudent> students = repository.getStudents();
		
		String pUserName = req.getParameter("username");
		String pGUID = req.getParameter("badgeid");
		String pGUID2 = req.getParameter("badgeid2");
		if(pGUID != null && pGUID.compareTo("") != 0)
		{
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			
			List<String> GUIDs = new ArrayList<String>();
			GUIDs.add(pGUID);
			if(pGUID2 != null)
				GUIDs.add(pGUID2);
			List<FoundBadgeInfo> badgeInfos = getBadgeInfo(repository, pUserName, GUIDs);
			
			
			
			 if(badgeInfos != null && badgeInfos.size() > 0)
			 {
				 req.getSession().setAttribute("badgeInfos", badgeInfos);
				 req.getSession().setAttribute("nrOfStudents", students.size());
				 req.getSession().setAttribute("backLink", "/badgeboard?username=" + pUserName);
				 
				 //start/end date
				 String strStartDate = req.getParameter("startdate");
				 String strEndDate = req.getParameter("enddate");
				 DateTime startDate;
				 DateTime endDate;
				 if(strStartDate == null || strEndDate == null || strStartDate.compareTo("") == 0 || strEndDate.compareTo("") == 0)
				 {
					 startDate = DateTime.now().minusDays(21);
					 endDate = DateTime.now();
				 }
				 else
				 {
					 try{
						 startDate = new DateTime(Long.parseLong(strStartDate));
						 endDate = new DateTime(Long.parseLong(strEndDate));
					 }
					 catch(Exception exc)
					 {
						 startDate = DateTime.now().minusDays(21);
						 endDate = DateTime.now();
					 }
				 }
				 //get the badges in that range
				 TreeMap<String,TreeMap<Long, Collection<BadgeForDisplay>>> badgeStats = new TreeMap<String,TreeMap<Long, Collection<BadgeForDisplay>>>();
				 Iterator<FoundBadgeInfo> badgeInfoItr = badgeInfos.iterator();
				 while(badgeInfoItr.hasNext())
				 {
					 FoundBadgeInfo binfo = badgeInfoItr.next();
					 TreeMap<Long, Collection<BadgeForDisplay>> badgeStatistics = repository.getBadgesForDateRangInBadgeCollection(startDate, endDate, binfo.badge.awardedBadges);
					 badgeStats.put(binfo.badge.GUID.toString(),badgeStatistics);
					 
				 }
				 req.getSession().setAttribute("badgeStats", badgeStats);
				 
				 req.getSession().setAttribute("startdate", startDate);
				 req.getSession().setAttribute("enddate", endDate);
				 
				 RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeBoard_BadgeDetail.jsp");
					if(dispatch != null)
						dispatch.forward(req, resp);
			 }
			 else
			 {
				 resp.sendRedirect("/badgeboard");
				
			 }
			 
		}
		else if(pUserName != null && pUserName.compareTo("") != 0)
		{
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			
			
			//wondering how this will hold up. it's everyone's badges. everyone ,... that's a lot. might wanna filter one way or another
			req.getSession().setAttribute("badges",repository.getAllBadgesByPeriodType());
			req.getSession().setAttribute("name", pUserName);
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeBoard_User.jsp");
			if(dispatch != null)
				dispatch.forward(req, resp);
		
		}
		else
		{
			//display user name list
			//Iterate and generate display badges
			Iterator<JoseStudent> it = students.iterator();
			Collection<String> userNames = new ArrayList<String>();
			while(it.hasNext())
			{
				JoseStudent student = (JoseStudent)it.next();
				if(student.username.compareTo("") != 0 && !userNames.contains(student.username))
				{
					userNames.add(student.username);		
					
				}
			}
		
			//add to session and bail
			req.getSession().setAttribute("userNames", userNames);
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeBoard.jsp");
			if(dispatch != null)
				dispatch.forward(req, resp);
		}
		
	}


	private List<FoundBadgeInfo> getBadgeInfo(BadgeRepository repository, String pUserName, List<String> GUID) 
	{
		List<FoundBadgeInfo> badgeInfos = new ArrayList<FoundBadgeInfo>();
		Collection<BadgeForDisplay> badges = repository.getBadgesDefinitions();
		Iterator<BadgeForDisplay> itr = badges.iterator();
		while(itr.hasNext()){
			BadgeForDisplay b = itr.next();
			if(b != null && GUID.contains(b.GUID.toString()))
			{
				//found correct badge, but does student have badge?
				FoundBadgeInfo badgeInfo = new FoundBadgeInfo();
				badgeInfo.badge = b;
				Iterator<BadgeForDisplay> subIt = b.awardedBadges.iterator();
				while(subIt.hasNext())
				{
					BadgeForDisplay subB = subIt.next();
					if(subB.username.compareTo(pUserName) == 0)
					{
						//student found! he has badge!
						badgeInfo.studentHasBadge = true;
						badgeInfo.studentBadge = subB;
					}
				}
				badgeInfos.add(badgeInfo);
				//break;
			}
		}
		return badgeInfos;
	}

	
	
	
	
	
	
	
	
	
	
	
}
