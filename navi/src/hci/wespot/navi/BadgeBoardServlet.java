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
			Collection<BadgeForDisplay> displayBadges = repository.getBadgesForStudent(pUserName);
					
			
			
			 BadgeForDisplay foundBadge = null;
			 boolean studentHasBadge = false;
			 
			 if(displayBadges != null)
			 {
				 Iterator<BadgeForDisplay> it = displayBadges.iterator();
				 
				 while(it.hasNext())
				 {
					 BadgeForDisplay displayBadge = it.next();
					 if(displayBadge != null && displayBadge.GUID.toString().compareTo(pGUID) == 0)
					 {
						 foundBadge = displayBadge;
						 studentHasBadge = true;
						 break;
					 }
				 }
			 }
			 if(foundBadge == null)
			 {
				 displayBadges = repository.getBiWeeklyBadges();
				 displayBadges.addAll(repository.getGlobalBadges());
				 Iterator<BadgeForDisplay> it = displayBadges.iterator();
				 foundBadge = null;
				 while(it.hasNext())
				 {
					 BadgeForDisplay displayBadge = it.next();
					 if(displayBadge != null && displayBadge.GUID.toString().compareTo(pGUID) == 0)
					 {
						 foundBadge = displayBadge;
						 break;
					 }
				 }
			 }
			 
			 if(foundBadge != null)
			 {
				 if(studentHasBadge)
					 req.getSession().setAttribute("awarded", true);
				 else
					 req.getSession().setAttribute("awarded", false);	
				 req.getSession().setAttribute("badge", foundBadge);
				 req.getSession().setAttribute("nrOfStudents", students.size());
				 req.getSession().setAttribute("backLink", "/badgeboard?username=" + pUserName);
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
				 RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeBoard.jsp");
					if(dispatch != null)
						dispatch.forward(req, resp);
			 }
			 
		}
		else if(pUserName != null && pUserName.compareTo("") != 0)
		{
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			Collection<BadgeForDisplay> badges = repository.getBadgesForStudent(pUserName);
			
			
			//get all the badges
			Collection<BadgeForDisplay> allBadges = repository.getBiWeeklyBadges();
			allBadges.addAll(repository.getGlobalBadges());
			Map<String, BadgeForDisplay> notYetAchievedBadges = getBadgesByName(allBadges);
			Collection<BadgeForDisplay> displayBadges = new ArrayList<BadgeForDisplay>();
			//Iterate and generate display badges
			if(badges != null)
			{
				Iterator<BadgeForDisplay> it = badges.iterator();
				
				while(it.hasNext())
				{
					BadgeForDisplay badge = (BadgeForDisplay)it.next();
					if(badge.recipient == null)
						continue;
					//if(badge.recipient != null && badge.recipient.compareTo(pUserName) != 0)
					//	continue;
		
					
					displayBadges.add(badge);
					
					//remove badge from notYetAchievedBadges
					notYetAchievedBadges.remove(badge.name);
					
				}
			}
			//add to session and bail
			req.getSession().setAttribute("name", pUserName);
			req.getSession().setAttribute("badges", displayBadges);
			Collection<BadgeForDisplay> notYetAchievedBadgesForReturn = new ArrayList<BadgeForDisplay>(notYetAchievedBadges.values());
			req.getSession().setAttribute("notYetAchievedBadges", notYetAchievedBadgesForReturn);
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
