package hci.wespot.navi;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BadgeHelpers {

	static public Collection<JoseBadge> getAllBadges() {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    String returnValue = (String) syncCache.get("badgesoverall");
		if(returnValue == null || returnValue.compareTo("") == 0)
		{
			returnValue = WebHelpers.get("http://openbadges-hci.appspot.com/rest/getinfo/chibadges");
			syncCache.put("badgesoverall", returnValue,Expiration.byDeltaSeconds(3000));
		}
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		Collection<JoseBadge> badges = (Collection<JoseBadge>)json.fromJson(returnValue, returnType);
		return badges;
	}
	
	static public void populateCountAndDatesPerBadge(String username)
	{
		Collection<JoseBadge> badgesOfUser = BadgeHelpers.getBadgeData(username);
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Map<String, HashMap<Date, Integer>> badgesByName = (Map<String, HashMap<Date, Integer>>) syncCache.get("badgesPerNameAndDate");
		if(badgesByName == null)
			badgesByName = new HashMap<String, HashMap<Date, Integer>>();
		Iterator<JoseBadge> itr = badgesOfUser.iterator();
		while(itr.hasNext())
		{
			JoseBadge badge = itr.next();
			if(!badgesByName.containsKey(badge.badge.name))
			{
				badgesByName.put(badge.badge.name, new HashMap<Date, Integer>());
			}
			if(!badgesByName.get(badge.badge.name).containsKey(badge.timestamp))
			{
				badgesByName.get(badge.badge.name).put(badge.timestamp, 0);
			}
			badgesByName.get(badge.badge.name).put(badge.timestamp,badgesByName.get(badge.badge.name).get(badge.timestamp)+1);
			
		}
		
		
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    syncCache.put("badgesPerNameAndDate", badgesByName,Expiration.byDeltaSeconds(300));
		
		
	}
	
	static public Map<String, HashMap<Date, Integer>> getCountAndDatesPerBadge()
	{
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		return (Map<String, HashMap<Date, Integer>>) syncCache.get("badgesPerNameAndDate");
	}
	
	static public Collection<JoseBadge> getBadgeData(String studentName) {
		//GET call
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    String returnValue = (String) syncCache.get(studentName);
		if(returnValue == null)
		{
			returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/openBadges/" + studentName + "/awarded", "{\"pag\" : \"0\"}");
			syncCache.put(studentName, returnValue,Expiration.byDeltaSeconds(300));
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
			
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
			DateTime dt = formatter.parseDateTime(responseValueWithBadgeData.starttime);
			responseValueWithBadgeData.originalrequest.timestamp =  dt.toDate();
			badges.add(responseValueWithBadgeData.originalrequest);
			
		}
		
		
		return badges;
	}
}
