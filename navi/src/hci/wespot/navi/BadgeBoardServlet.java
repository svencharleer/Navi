package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.Type;



@SuppressWarnings("serial")
public class BadgeBoardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		//GET call
		String returnValue = WebHelpers.get("http://openbadges-hci.appspot.com/rest/getinfo/badgesusers");
		//String returnValue = get("http://navi-hci.appspot.com/BADGES.JSON");

		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		Collection<JoseBadge> badges = (Collection<JoseBadge>)json.fromJson(returnValue, returnType);
		
		//TODO: guess we should cache this. gapp has something to cache no?
		
		//Iterate and generate display badges
		Iterator<JoseBadge> it = badges.iterator();
		Collection<String> userNames = new ArrayList<String>();
		while(it.hasNext())
		{
			JoseBadge badge = (JoseBadge)it.next();
			userNames.add(badge.recipient);		
		}
	
		//add to session and bail
		req.getSession().setAttribute("badges", userNames);
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeOverview.jsp");
		if(dispatch != null)
			dispatch.forward(req, resp);
    	
		
	}
	
	
	
	
	
}
