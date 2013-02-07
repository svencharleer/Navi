package hci.wespot.navi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.io.OutputStreamWriter;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;


import java.lang.reflect.Type;



@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String action = (String)req.getParameter("do");
		if(action != null && action.compareTo("logout") == 0)
		{
			req.removeAttribute("userId");
			req.removeAttribute("username");
		}
		resp.sendRedirect("/login.jsp");
		return;
		
		}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		String username = (String) req.getParameter("username");
		if(username == null || username == "")
		{
			resp.sendRedirect("/login.jsp");
			return;
		}
		String message = URLEncoder.encode(username, "UTF-8");
    	String params = "email=" + message;
    	String returnValue = post("http://beta.openbadges.org/displayer/convert/email", params);
    	Type returnType = new TypeToken<OpenBadges_ConvertEmailReply>(){}.getType();
        Gson json = new Gson();
        OpenBadges_ConvertEmailReply tmp = (OpenBadges_ConvertEmailReply)json.fromJson(returnValue, returnType);
        if(tmp != null  && tmp.status.compareTo("okay")==0)
        {
        	req.setAttribute("userId", ""+ tmp.userId + "");
        	req.setAttribute("username", username);
        	RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/navi");
        	if(dispatch != null)
        		dispatch.forward(req, resp);
        }
        else
        {
        	RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/login.jsp");
        	if(dispatch != null)
        		dispatch.forward(req, resp);
        }
    	
		
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
