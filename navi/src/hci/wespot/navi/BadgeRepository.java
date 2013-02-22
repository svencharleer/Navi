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

public class BadgeRepository {

	Collection<JoseBadge> badgeDefinitions;
	Map<String, Collection<JoseBadge>> awardedBadgesByStudent;
	Collection<JoseBadge> awardedBadges;
	Map<Long, Map<String,Collection<JoseBadge>>> badgeCalendar;
	Collection<JoseStudent> students;
	
	static public BadgeRepository getRepository()
	{
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    BadgeRepository rep = (BadgeRepository) syncCache.get("badgeRepository");
		if(rep == null)
		{
			rep = new BadgeRepository();
			syncCache.put("badgeRepository", rep,Expiration.byDeltaSeconds(900));
		}
		return rep;
	}
	
	private BadgeRepository()
	{
		reload();
	}
	
	public void reload()
	{
		reloadBadgeDefinitions();
		reloadStudents();
		reloadAwardedBadges();
		reloadBadgeCalendar();
	}
	
	public Collection<JoseStudent> getStudents()
	{
		return students;
	}
	
	public Collection<JoseBadge> getBadgesForStudent(String studentName)
	{
		return awardedBadgesByStudent.get(studentName);
	}
	
	public Collection<JoseBadge> getAwardedBadgesForStudent(String studentName) {
		return awardedBadgesByStudent.get(studentName);
	}
	
	public Collection<JoseBadge> getBadgesDefinitions() {
		return badgeDefinitions;
	}

	public Map<Long,Collection<JoseBadge>> getBadgesForDateRangeWithBadgeName(DateTime startDate, DateTime endDate, String badgeName)
	{
		//convert date range to list of days
		Collection<Long> dates = new ArrayList<Long>();
		long start = startDate.toDateMidnight().getMillis();
		dates.add(start);
		while(start <= endDate.toDateMidnight().getMillis())
		{
			startDate = startDate.plusDays(1);
			start = startDate.toDateMidnight().getMillis();
			dates.add(start);
		}
		
		//for every day check how many badges
		
		Map<Long, Collection<JoseBadge>> badgesPerDay = new HashMap<Long, Collection<JoseBadge>>();
		
		Iterator<Long> itr = dates.iterator();
		while(itr.hasNext())
		{
			long day = (long)itr.next();
			Boolean badgeAdded = false;
			if(badgeCalendar.containsKey(day))
			{
				Map<String, Collection<JoseBadge>> allBadgesOfDay = badgeCalendar.get((long)itr.next());
				if(allBadgesOfDay.containsKey(badgeName))
				{
					badgesPerDay.put(day, new ArrayList<JoseBadge>(allBadgesOfDay.get(badgeName)));
					badgeAdded = true;
				}
			}
			if(!badgeAdded)
				badgesPerDay.put(day, new ArrayList<JoseBadge>());
		}
		return null;
	}
	
	//PRIVATE
	private void reloadBadgeDefinitions()
	{
		String returnData = WebHelpers.get("http://openbadges-hci.appspot.com/rest/getinfo/chibadges");
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		badgeDefinitions = (Collection<JoseBadge>)json.fromJson(returnData, returnType);
	}
	
	private void reloadAwardedBadges()
	{
		Iterator<JoseStudent> itr = students.iterator();
		while(itr.hasNext())
		{
			JoseStudent student = (JoseStudent)itr.next();
			reloadBadgeDataForStudent(student.username);
		}
	}
	
	private void reloadStudents() {
		String returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/openBadges", "{\"pag\" : \"0\"}");
		
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseStudent>>(){}.getType();
		students = (Collection<JoseStudent>)json.fromJson(returnValue, returnType);
	}
	
	private void reloadBadgeDataForStudent(String studentName) {
		
		String returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/openBadges/" + studentName + "/awarded", "{\"pag\" : \"0\"}");
		
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
			responseValueWithBadgeData.originalrequest.timestamp =  dt.toDateMidnight().getMillis();
			badges.add(responseValueWithBadgeData.originalrequest);
			
		}
		awardedBadgesByStudent.put(studentName, badges);
		awardedBadges.addAll(badges);
	}
	
	
	
	private void reloadBadgeCalendar()
	{

		Iterator<JoseBadge> itr = awardedBadges.iterator();
		while(itr.hasNext())
		{
			JoseBadge badge = itr.next();
			if(!badgeCalendar.containsKey(badge.timestamp))
			{
				badgeCalendar.put(badge.timestamp, new HashMap<String, Collection<JoseBadge>>());
			}
			if(!badgeCalendar.get(badge.timestamp).containsKey(badge.badge.name))
			{
				badgeCalendar.get(badge.timestamp).put(badge.badge.name, new ArrayList<JoseBadge>());
			}
			Collection<JoseBadge> badges = badgeCalendar.get(badge.timestamp).get(badge.badge.name);
			badges.add(badge);
			badgeCalendar.get(badge.timestamp).put(badge.badge.name, badges);
			
		}
		
	
	}
	
	
	
	
}
