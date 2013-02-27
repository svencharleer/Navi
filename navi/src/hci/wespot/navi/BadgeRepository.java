package hci.wespot.navi;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BadgeRepository implements Serializable {

	private static final Logger log = Logger.getLogger(BadgeRepository.class.getName());
	
	private static final long serialVersionUID = 1L;
	private Collection<BadgeForDisplay> badgeDefinitions;
	private Map<String, Collection<BadgeForDisplay>> awardedBadgesByStudent;
	private Collection<BadgeForDisplay> awardedBadges;
	private Map<Long, Map<String,Collection<BadgeForDisplay>>> badgeCalendar;
	private List<JoseStudent> students;
	private Collection<BadgeForDisplay> biweeklyBadges;
	private Collection<BadgeForDisplay> globalBadges;
	
	static public BadgeRepository getRepository()
	{
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    BadgeRepository rep = (BadgeRepository) syncCache.get("badgeRepository");
		if(rep == null)
		{
			//log.log(Level.WARNING, "Cache not found");
			rep = new BadgeRepository();
			syncCache.put("badgeRepository", rep,Expiration.byDeltaSeconds(900));
		}
		return rep;
	}
	
	// DO NOT USE, ONLY FOR CACHING PURPOSES PUBLIC
	public BadgeRepository()
	{
		badgeDefinitions = new ArrayList<BadgeForDisplay>();
		awardedBadges = new ArrayList<BadgeForDisplay>();
		awardedBadgesByStudent = new HashMap<String, Collection<BadgeForDisplay>>();
		badgeCalendar = new HashMap<Long, Map<String, Collection<BadgeForDisplay>>>();
		students = new ArrayList<JoseStudent>();
		biweeklyBadges = new ArrayList<BadgeForDisplay>();
		globalBadges = new ArrayList<BadgeForDisplay>();
		reload();
	}
	
	public void reload()
	{
		reloadBadgeDefinitions();
		reloadStudents();
		reloadAwardedBadges();
		reloadBadgeCalendar();
		reloadBadgeDefinitionsPerWeek();
	}
	
	public Collection<JoseStudent> getStudents()
	{
		return students;
	}
	
	public Collection<BadgeForDisplay> getBadgesForStudent(String studentName)
	{
		return awardedBadgesByStudent.get(studentName);
	}
	
	public Collection<BadgeForDisplay> getAwardedBadgesForStudent(String studentName) {
		return awardedBadgesByStudent.get(studentName);
	}
	
	public Collection<BadgeForDisplay> getBadgesDefinitions() {
		return badgeDefinitions;
	}

	public Collection<BadgeForDisplay> getBiWeeklyBadges() {
		return biweeklyBadges;
	}
	
	public Collection<BadgeForDisplay> getGlobalBadges() {
		return globalBadges;
	}
	
	
	public TreeMap<Long,Collection<BadgeForDisplay>> getBadgesForDateRangeWithBadgeName(DateTime startDate, DateTime endDate, String badgeName)
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
		
		TreeMap<Long, Collection<BadgeForDisplay>> badgesPerDay = new TreeMap<Long, Collection<BadgeForDisplay>>();
		
		Iterator<Long> itr = dates.iterator();
		while(itr.hasNext())
		{
			long day = (long)itr.next();
			Boolean badgeAdded = false;
			if(badgeCalendar.containsKey(day))
			{
				Map<String, Collection<BadgeForDisplay>> allBadgesOfDay = badgeCalendar.get(day);
				if(allBadgesOfDay != null && allBadgesOfDay.containsKey(badgeName))
				{
					badgesPerDay.put(day, new ArrayList<BadgeForDisplay>(allBadgesOfDay.get(badgeName)));
					badgeAdded = true;
				}
			}
			if(!badgeAdded)
				badgesPerDay.put(day, new ArrayList<BadgeForDisplay>());
		}
		return badgesPerDay;
	}
	
	
	static public BadgeForDisplay convertToBadgeForDisplay(JoseBadge badge)
	{
		BadgeForDisplay displayBadge = new BadgeForDisplay();
		displayBadge.GUID = UUID.randomUUID();
		displayBadge.description = badge.badge.description;
		displayBadge.imageUrl =  "http://openbadges-hci.appspot.com"+badge.badge.image;
		displayBadge.name = badge.badge.name;
		displayBadge.url = "http://openbadges-hci.appspot.com/rest/getinfo/id/" + badge.id;
		displayBadge.connotation = badge.connotation;
		displayBadge.type = badge.type;
		displayBadge.timestamp = badge.timestamp;
		displayBadge.recipient = badge.recipient;
		return displayBadge;
	}
	
	static public Collection<BadgeForDisplay> converttoBadgeForDisplayCollection(Collection<JoseBadge> badges)
	{
		Collection<BadgeForDisplay> returnBadges = new ArrayList<BadgeForDisplay>();
		Iterator<JoseBadge> itr = badges.iterator();
		while(itr.hasNext())
		{
			JoseBadge badge = itr.next();
			returnBadges.add(BadgeRepository.convertToBadgeForDisplay(badge));
		}
		return returnBadges;
	}
	
	//PRIVATE
	private void reloadBadgeDefinitions()
	{
		String returnData = WebHelpers.get("http://openbadges-hci.appspot.com/rest/getinfo/chibadges");
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseBadge>>(){}.getType();
		Collection<JoseBadge> joseBadges = (Collection<JoseBadge>)json.fromJson(returnData, returnType);
		badgeDefinitions = BadgeRepository.converttoBadgeForDisplayCollection(joseBadges);
	}
	
	private void reloadBadgeDefinitionsPerWeek()
	{
		Iterator<BadgeForDisplay> iterator = badgeDefinitions.iterator();
		while(iterator.hasNext())
		{
			BadgeForDisplay badge = iterator.next();
			if(badge.description.contains("biweekly"))
			{
				for(int i =0; i < 7;i++)
				{
					BadgeForDisplay b = new BadgeForDisplay(badge);
					b.name = badge.name + i;
					b.biweek = i;
					biweeklyBadges.add(b);
				}
			}
			else
			{
				badge.biweek = -1;
				globalBadges.add(badge);
			}
		}
	}
	
	private void reloadAwardedBadges()
	{
		/*Iterator<JoseStudent> itr = students.iterator();
		while(itr.hasNext())
		{
			JoseStudent student = (JoseStudent)itr.next();
			reloadBadgeDataForStudent(student.username);
		}*/
		String returnValue =  WebHelpers.get("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getChiBadges");
		//log.log(Level.WARNING, returnValue);
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseReturn>>(){}.getType();
		Collection<JoseReturn> response = (Collection<JoseReturn>)json.fromJson(returnValue, returnType);
		
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
			
			BadgeForDisplay badge = BadgeRepository.convertToBadgeForDisplay(responseValueWithBadgeData.originalrequest);
			awardedBadges.add(badge);
			
			String studentName = responseValueWithBadgeData.username;
			if(!awardedBadgesByStudent.containsKey(studentName))
				awardedBadgesByStudent.put(studentName, new ArrayList<BadgeForDisplay>());
			awardedBadgesByStudent.get(studentName).add(badge);
		}
	}
	
	private void reloadStudents() {
		String returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/chikul13", "{\"pag\" : \"0\"}");
		
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<List<JoseStudent>>(){}.getType();
		students = (List<JoseStudent>)json.fromJson(returnValue, returnType);
		Collections.sort(students);
		
	}
	
	private void reloadBadgeDataForStudent(String studentName) {
		if(studentName == null || studentName.compareTo("") == 0) return;
		String returnValue = WebHelpers.post("http://ariadne.cs.kuleuven.be/wespot-dev-ws/rest/getCourses/chikul13/" + studentName + "/awarded", "{\"pag\" : \"0\"}");
		
		//JSON conversion
		Gson json = new Gson();
		Type returnType = new TypeToken<Collection<JoseReturn>>(){}.getType();
		Collection<JoseReturn> response = (Collection<JoseReturn>)json.fromJson(returnValue, returnType);
		
		Collection<BadgeForDisplay> badges = new ArrayList<BadgeForDisplay>();
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
			badges.add(BadgeRepository.convertToBadgeForDisplay(responseValueWithBadgeData.originalrequest));
			
		}
		awardedBadgesByStudent.put(studentName, badges);
		awardedBadges.addAll(badges);
	}
	
	
	
	private void reloadBadgeCalendar()
	{

		Iterator<BadgeForDisplay> itr = awardedBadges.iterator();
		while(itr.hasNext())
		{
			BadgeForDisplay badge = itr.next();
			if(!badgeCalendar.containsKey(badge.timestamp))
			{
				badgeCalendar.put(badge.timestamp, new HashMap<String, Collection<BadgeForDisplay>>());
			}
			if(!badgeCalendar.get(badge.timestamp).containsKey(badge.name))
			{
				badgeCalendar.get(badge.timestamp).put(badge.name, new ArrayList<BadgeForDisplay>());
			}
			Collection<BadgeForDisplay> badges = badgeCalendar.get(badge.timestamp).get(badge.name);
			badges.add(badge);
			badgeCalendar.get(badge.timestamp).put(badge.name, badges);
			
		}
		
	
	}
	
	
	
	
}
