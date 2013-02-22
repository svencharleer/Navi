package hci.wespot.navi;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.Type;



@SuppressWarnings("serial")
public class BadgeOverviewServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		BadgeRepository repository = BadgeRepository.getRepository();
		
		//GET call
		Collection<BadgeForDisplay> badges = repository.getBadgesDefinitions();
		
		//Iterate and generate display badges
		Iterator<BadgeForDisplay> it = badges.iterator();
		Collection<BadgeForDisplay> positive_indi_badges = new ArrayList<BadgeForDisplay>();
		Collection<BadgeForDisplay> positive_group_badges = new ArrayList<BadgeForDisplay>();
		Collection<BadgeForDisplay> negative_indi_badges = new ArrayList<BadgeForDisplay>();
		Collection<BadgeForDisplay> negative_group_badges = new ArrayList<BadgeForDisplay>();
		Collection<BadgeForDisplay> neutral_indi_badges = new ArrayList<BadgeForDisplay>();
		Collection<BadgeForDisplay> neutral_group_badges = new ArrayList<BadgeForDisplay>();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
			
			if(badge.type.compareTo("group") == 0)
			{
				if(badge.connotation.compareTo("positive") == 0)
				{
					positive_group_badges.add(badge);
				}
				else if(badge.connotation.compareTo("negative") == 0)
				{
					negative_group_badges.add(badge);
				}
				else
				{
					neutral_group_badges.add(badge);
				}
			}
			else
			{
				if(badge.connotation.compareTo("positive") == 0)
				{
					positive_indi_badges.add(badge);
				}
				else if(badge.connotation.compareTo("negative") == 0)
				{
					negative_indi_badges.add(badge);
				}
				else
				{
					neutral_indi_badges.add(badge);
				}
			}
			
				
		}
	
		//add to session and bail
		req.getSession().setAttribute("positive_indi_badges", positive_indi_badges);
		req.getSession().setAttribute("positive_group_badges", positive_group_badges);
		req.getSession().setAttribute("negative_indi_badges", negative_indi_badges);
		req.getSession().setAttribute("negative_group_badges", negative_group_badges);
		req.getSession().setAttribute("neutral_indi_badges", neutral_indi_badges);
		req.getSession().setAttribute("neutral_group_badges", neutral_group_badges);
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/BadgeOverview.jsp");
		if(dispatch != null)
			dispatch.forward(req, resp);
    	
		
	}

	
	
	
	
	
	
}
