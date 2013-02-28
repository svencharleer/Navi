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
		if(pGUID != null && pGUID.compareTo("") != 0)
		{
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			boolean studentHasBadge = false;
			BadgeForDisplay foundBadge = null;
			
			Collection<BadgeForDisplay> badges = repository.getBadgesDefinitions();
			Iterator<BadgeForDisplay> itr = badges.iterator();
			while(itr.hasNext()){
				BadgeForDisplay b = itr.next();
				if(b != null && b.GUID.toString().compareTo(pGUID) == 0)
				{
					//found correct badge, but does student have badge?
					foundBadge = b;
					Iterator<BadgeForDisplay> subIt = b.awardedBadges.iterator();
					while(subIt.hasNext())
					{
						BadgeForDisplay subB = subIt.next();
						if(subB.username.compareTo(pUserName) == 0)
						{
							//student found! he has badge!
							studentHasBadge = true;
							foundBadge = subB;
						}
					}
					break;
				}
				
			}
			
			
			
			 if(foundBadge != null)
			 {
				 req.getSession().setAttribute("awarded", studentHasBadge);
				 req.getSession().setAttribute("badge", foundBadge);
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
				 
				 TreeMap<Long, Collection<BadgeForDisplay>> badgeStatistics = repository.getBadgesForDateRangeWithBadgeName(startDate, endDate, foundBadge.name);
				 req.getSession().setAttribute("badgeStats", badgeStatistics);
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

	
	
	
	
	
	
	private Map<String, BadgeForDisplay> getBadgesByName(Collection<BadgeForDisplay> badges)
	{
		Map<String, BadgeForDisplay> badgesByName = new HashMap<String, BadgeForDisplay>();
		Iterator<BadgeForDisplay> itr = badges.iterator();
		while(itr.hasNext())
		{
			BadgeForDisplay badge = itr.next();
			
			badgesByName.put(badge.name, badge);
		}
		return badgesByName;
	}
	
	
	
	
}
