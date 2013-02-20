package hci.wespot.navi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.Blob;

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
	
	static public byte[] getImage(String urlName)
	{
		try 
		{
			URL url = new URL(urlName);
			
			InputStreamReader rdr = new InputStreamReader(url.openStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			while(rdr.read())
				//https://dev.twitter.com/docs/api/1/get/users/profile_image/%3Ascreen_name
				//wasting too much time on this. other time maybe
			
           
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
