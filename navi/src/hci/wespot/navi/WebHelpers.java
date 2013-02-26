package hci.wespot.navi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebHelpers {
	static public String post(String urlName, String params)
	
	{
		try 
		{
			URL url = new URL(urlName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			
			OutputStream os = connection.getOutputStream();
			
			os.write(params.getBytes());
			os.close();
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
	
	static public String get(String urlName)
	{
		try 
		{
			URL url = new URL(urlName + "#"+System.currentTimeMillis());
			
				
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
