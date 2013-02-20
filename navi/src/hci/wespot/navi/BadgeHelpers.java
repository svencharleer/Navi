package hci.wespot.navi;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.logging.Level;

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
}
