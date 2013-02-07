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
import java.util.Iterator;
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
		String userId = (String) req.getAttribute("userId");
		if(userId == null || userId == "")
		{
			resp.sendRedirect("/login.jsp");
			return;
		}
		 
		 
		
			Gson json = new Gson();
            String returnValue = get("http://beta.openbadges.org/displayer/"+ userId + "/groups.json");
            Type returnType = new TypeToken<OpenBadges_Groups>(){}.getType();
            OpenBadges_Groups tmp2 = (OpenBadges_Groups)json.fromJson(returnValue, returnType);
            
            req.getSession().setAttribute("userId", userId);
            req.getSession().setAttribute("username", req.getAttribute("username"));
            //req.getSession().setAttribute("username", userId);
            
            Collection<BadgeForDisplay> badgesForDisplay = new ArrayList<BadgeForDisplay>();
            
            Iterator<OpenBadges_Group> it = tmp2.groups.iterator();
            while(it.hasNext())
            {
            	OpenBadges_Group grp = (OpenBadges_Group) it.next();
            	if(grp == null) break;
            	returnValue = get("http://beta.openbadges.org/displayer/"+ userId + "/group/" + grp.groupId  + ".json");
            	returnType = new TypeToken<OpenBadges_Group>(){}.getType();
            	OpenBadges_Group tmp3 = (OpenBadges_Group)json.fromJson(returnValue, returnType);
            	JsonElement el = json.toJsonTree(tmp3.badges); 
            	JsonArray r = el.getAsJsonArray();
            	
            	
            	Iterator<JsonElement> it2 = r.iterator();
            	
            	while(it2.hasNext())
            	{
            		
            		returnType = new TypeToken<OpenBadges_BadgeData>(){}.getType();
            		OpenBadges_BadgeData tmp4 = (OpenBadges_BadgeData)json.fromJson(it2.next().toString(), returnType);
            		
            		//json.toJson(it2.next());
            		//OpenBadges_BadgeData d = it2.next();
            		//resp.getWriter().println("<img src='" +
            		//		tmp4.imageUrl + "'/>");
            		BadgeForDisplay badge = new BadgeForDisplay();
            		badge.description = tmp4.assertion.badge.description;
            		badge.url = tmp4.imageUrl;
            		badge.name = tmp4.assertion.badge.name;
            		badgesForDisplay.add(badge);
            		
            		
            	}
            	
            	
            }
            req.getSession().setAttribute("badges", badgesForDisplay);
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
