package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import com.google.appengine.api.memcache.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;



@SuppressWarnings("serial")
public class BadgeBoardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		Collection<JoseStudent> students = getUsers();
		
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
			Collection<JoseBadge> badges = getBadgeData(pUserName);
			pUserName = URLDecoder.decode(pUserName, "UTF-8");
			//Iterate and generate display badges
			Iterator<JoseBadge> it = badges.iterator();
			Collection<BadgeForDisplay> displayBadges = new ArrayList<BadgeForDisplay>();
			while(it.hasNext())
			{
				JoseBadge badge = (JoseBadge)it.next();
				if(badge.recipient == null)
					continue;
				if(badge.recipient != null && badge.recipient.compareTo(pUserName) != 0)
					continue;
	
				//Add badge to list
				BadgeForDisplay displayBadge = new BadgeForDisplay();
				displayBadge.GUID = UUID.randomUUID();
				displayBadge.description = badge.badge.description;
				displayBadge.imageUrl =  "http://openbadges-hci.appspot.com"+badge.badge.image;
				displayBadge.name = badge.badge.name;
				displayBadge.url = "http://openbadges-hci.appspot.com/rest/getinfo/id/" + badge.id;
				displayBadge.connotation = badge.connotation;
				displayBadge.type = badge.type;
				displayBadges.add(displayBadge);
				
			}
			//add to session and bail
			req.getSession().setAttribute("name", pUserName);
			req.getSession().setAttribute("badges", displayBadges);
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
					userNames.add(student.username);		
			}
		
			//add to session and bail
			req.getSession().setAttribute("userNames", userNames);
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeBoard.jsp");
			if(dispatch != null)
				dispatch.forward(req, resp);
		}
		
	}

	private Collection<JoseBadge> getBadgeData(String studentName) {
		//GET call
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    String returnValue = (String) syncCache.get(studentName);
		if(returnValue == null)
		{
			returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/openBadges/" + studentName + "/awarded", "{\"pag\" : \"0\"}");
			syncCache.put("", returnValue,Expiration.byDeltaSeconds(300));
		}
		
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseReturn>>(){}.getType();
		Collection<JoseReturn> response = (Collection<JoseReturn>)json.fromJson(returnValue, returnType);
		
		Collection<JoseBadge> badges = new ArrayList<JoseBadge>();
		//in the originalrequest field, we have again the same data. then within that originalrequest we have the badge
		//weird.. but jose knows ;)
		Iterator<JoseReturn> it = response.iterator();
		while(it.hasNext())
		{
			JoseReturn responseValue = it.next();
			Type responseType = new TypeToken<JoseReturnWithBadgeData>(){}.getType();
			JoseReturnWithBadgeData responseValueWithBadgeData  = (JoseReturnWithBadgeData) json.fromJson((String)responseValue.originalrequest, responseType);
			
			badges.add(responseValueWithBadgeData.originalrequest);
			
		}
		
		
		return badges;
	}
	
	private Collection<JoseStudent> getUsers() {
		//GET call
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    String returnValue = (String) syncCache.get("badgestudents");
		if(returnValue == null)
		{
			returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/openBadges", "{\"pag\" : \"0\"}");
			syncCache.put("badgestudents", returnValue,Expiration.byDeltaSeconds(300));
		}
		
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseStudent>>(){}.getType();
		Collection<JoseStudent> students = (Collection<JoseStudent>)json.fromJson(returnValue, returnType);
		return students;
	}
	
	
	
	
	
}
