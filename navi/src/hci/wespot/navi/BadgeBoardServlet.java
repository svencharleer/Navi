package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

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
			
					
			
			 Collection<BadgeForDisplay> displayBadges = (Collection<BadgeForDisplay>)req.getSession().getAttribute("badges");
			 Iterator<BadgeForDisplay> it = displayBadges.iterator();
			 BadgeForDisplay foundBadge = null;
			 while(it.hasNext())
			 {
				 BadgeForDisplay displayBadge = it.next();
				 if(displayBadge != null && displayBadge.GUID.toString().compareTo(pGUID) == 0)
				 {
					 foundBadge = displayBadge;
					 break;
				 }
			 }
			 if(foundBadge != null)
			 {
				 req.getSession().setAttribute("badge", foundBadge);
				 req.getSession().setAttribute("backLink", "/BadgeBoard_User.jsp?username=" + pUserName);
				 
				 Map<Long, Collection<BadgeForDisplay>> badgeStatistics = repository.getBadgesForDateRangeWithBadgeName(DateTime.now().minusDays(30), DateTime.now(), foundBadge.name);
				 req.getSession().setAttribute("badgeStats", badgeStatistics);
				 
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
			Collection<BadgeForDisplay> badges = repository.getBadgesForStudent(pUserName);
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			
			//get all the badges
			Collection<BadgeForDisplay> allBadges = repository.getBadgesDefinitions();
			Map<String, BadgeForDisplay> notYetAchievedBadges = getBadgesByName(allBadges);
			
			//Iterate and generate display badges
			Iterator<BadgeForDisplay> it = badges.iterator();
			Collection<BadgeForDisplay> displayBadges = new ArrayList<BadgeForDisplay>();
			while(it.hasNext())
			{
				BadgeForDisplay badge = (BadgeForDisplay)it.next();
				if(badge.recipient == null)
					continue;
				if(badge.recipient != null && badge.recipient.compareTo(pUserName) != 0)
					continue;
	
				
				displayBadges.add(badge);
				
				//remove badge from notYetAchievedBadges
				notYetAchievedBadges.remove(badge.name);
				
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
