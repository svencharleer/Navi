package hci.wespot.navi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.lang.reflect.Type;

class OpenBadges_ConvertEmailReply
{
	public String status;
	public String email;
	public int userId;
}

class OpenBadges_Group
{
	public int groupId;
	public String name;
	public Object badges;
	//public JSONArray badges; 
}

class OpenBadges_Groups
{
	public int userId;
	Collection<OpenBadges_Group> groups;
}

class OpenBadges_Badge
{
	String version;
	String description;
	String name;
	String criteria;
	String image;
	Object issuer;
	String recipient;
}

class JoseBadge
{
	String id; 
	OpenBadges_Badge badge;
	String recipient;
}

class OpenBadges_BadgeAssertion
{
	String salt;
	OpenBadges_Badge badge;
	String recipient;
}

class OpenBadges_BadgeData
{
	String lastValidated;
	String assertionType;
	String hostedUrl;
	//Object assertion;
	//ArrayList<OpenBadges_BadgeAssertion> assertion; 
	OpenBadges_BadgeAssertion assertion;
	String imageUrl;
}



@SuppressWarnings("serial")
public class NaviServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		doPost(req,resp);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		//UserService userService = UserServiceFactory.getUserService();
        //User user = userService.getCurrentUser();
		String userName = (String) req.getSession().getAttribute("username");
		if(userName == null || userName == "")
		{
			resp.sendRedirect("/login.jsp");
			return;
		}
		 
		 
		
		Gson json = new Gson();
		String returnValue = get("http://openbadges-hci.appspot.com/rest/getinfo/badgesusers");
		
		
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		Collection<JoseBadge> badges = (Collection<JoseBadge>)json.fromJson(returnValue, returnType);
		
			
		
		
		
		Iterator<JoseBadge> it = badges.iterator();
		Map<String, Collection<BadgeForDisplay>> badgesPerName = new HashMap<String,Collection<BadgeForDisplay>>(); 
		while(it.hasNext())
		{
			
			JoseBadge badge = (JoseBadge)it.next();
			if(badge != null && badge != null && badge.recipient.compareToIgnoreCase(userName)==0)
			{
				if(!badgesPerName.containsKey(badge.badge.name))
				{
					badgesPerName.put(badge.badge.name, new ArrayList<BadgeForDisplay>());
				}
				BadgeForDisplay displayBadge = new BadgeForDisplay();
				displayBadge.description = badge.badge.description;
				displayBadge.imageUrl =  "http://openbadges-hci.appspot.com"+badge.badge.image;
				displayBadge.name = badge.badge.name;
				displayBadge.url = "http://openbadges-hci.appspot.com/rest/getinfo/id/" + badge.id;
				badgesPerName.get(badge.badge.name).add(displayBadge);
			
			}
			
		}
			
			
		
		req.getSession().setAttribute("badges", badgesPerName);
		
		//req.getSession().setAttribute("username", userName);
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/navi.jsp");
		if(dispatch != null)
			dispatch.forward(req, resp);

        
	}
	
	private String post(String urlName, String params)
	
	{
		try 
		{
			URL url = new URL(urlName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			
			writer.write(params);
			writer.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                String test = "";
                while ((line = reader.readLine()) != null) {
                	test += line;
                	
                }
                
                reader.close();
                return test;
            } else {
                // Server returned HTTP error code.
            }
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
	}
	
private String get(String urlName)
	
	{
		try 
		{
			URL url = new URL(urlName);
			
				
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = "";
                String test = "";
                while ((line = reader.readLine()) != null) {
                	test += line;
                	
                }    
                reader.close();
                return test;
           
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
	}
	
	
	
}
