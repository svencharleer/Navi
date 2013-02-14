package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.Type;



@SuppressWarnings("serial")
public class BadgeOverviewServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		//GET call
		String returnValue = WebHelpers.get("http://openbadges-hci.appspot.com/rest/getinfo/chibadges");
		//String returnValue = get("http://navi-hci.appspot.com/BADGES.JSON");

		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		Collection<JoseBadge> badges = (Collection<JoseBadge>)json.fromJson(returnValue, returnType);
		
		//Iterate and generate display badges
		Iterator<JoseBadge> it = badges.iterator();
		Collection<BadgeForDisplay> outbadges = new ArrayList<BadgeForDisplay>();
		while(it.hasNext())
		{
			JoseBadge badge = (JoseBadge)it.next();
			BadgeForDisplay displayBadge = new BadgeForDisplay();
			displayBadge.description = badge.badge.description;
			displayBadge.imageUrl =  "http://openbadges-hci.appspot.com"+badge.badge.image;
			displayBadge.name = badge.badge.name;
			displayBadge.url = "http://openbadges-hci.appspot.com/rest/getinfo/id/" + badge.id;
			outbadges.add(displayBadge);		
		}
	
		//add to session and bail
		req.getSession().setAttribute("badges", outbadges);
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeOverview.jsp");
		if(dispatch != null)
			dispatch.forward(req, resp);
    	
		
	}
	
	
	
	
	
}
