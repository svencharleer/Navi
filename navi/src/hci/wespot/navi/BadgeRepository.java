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
	private Collection<BadgeForDisplay> shortBadgeOverview;
	private Map<String, Collection<BadgeForDisplay>> awardedBadgesByStudent;
	private Collection<BadgeForDisplay> awardedBadges;
	private Map<Long, Map<String,Collection<BadgeForDisplay>>> badgeCalendar;
	private List<JoseStudent> students;
	private List<BadgeForDisplay> biweeklyBadges;
	private List<BadgeForDisplay> globalBadges;
	private TreeMap<Integer, List<BadgeForDisplay>> allBadgeDefinitionsByPeriod;
	
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
		shortBadgeOverview = new ArrayList<BadgeForDisplay>();
		awardedBadges = new ArrayList<BadgeForDisplay>();
		awardedBadgesByStudent = new HashMap<String, Collection<BadgeForDisplay>>();
		badgeCalendar = new HashMap<Long, Map<String, Collection<BadgeForDisplay>>>();
		students = new ArrayList<JoseStudent>();
		biweeklyBadges = new ArrayList<BadgeForDisplay>();
		globalBadges = new ArrayList<BadgeForDisplay>();
		allBadgeDefinitionsByPeriod = new TreeMap<Integer, List<BadgeForDisplay>>();
		reload();
	}
	
	public void reload()
	{
		reloadBadgeDefinitions();
		reloadStudents();
		
		reloadBadgeDefinitionsPerWeek();
		reloadAwardedBadges();
		reloadBadgeCalendar();
		
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

	public Collection<BadgeForDisplay> getShortBadgeOverview() {
		return shortBadgeOverview;
	}

	public List<BadgeForDisplay> getBiWeeklyBadges() {
		return biweeklyBadges;
	}
	
	public TreeMap<Integer, List<BadgeForDisplay>> getAllBadgesByPeriodType()
	{
		return allBadgeDefinitionsByPeriod;
		
	}
	
	public List<BadgeForDisplay> getGlobalBadges() {
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
	
	
	static public BadgeForDisplay convertToBadgeForDisplay(JoseBadge badge, String username)
	{
		BadgeForDisplay displayBadge = new BadgeForDisplay();
		displayBadge.GUID = UUID.randomUUID();
		displayBadge.description = badge.badge.description;
		displayBadge.imageUrl =  badge.badge.image;
		displayBadge.name = badge.badge.name;
		displayBadge.url = "http://openbadges-hci.appspot.com/rest/getinfo/id/" + badge.id;
		displayBadge.connotation = badge.connotation;
		displayBadge.type = badge.type;
		displayBadge.timestamp = badge.timestamp;
		displayBadge.recipient = badge.recipient;
		displayBadge.username = username;
		return displayBadge;
	}
	
	static public Collection<BadgeForDisplay> converttoBadgeForDisplayCollection(Collection<JoseBadge> badges)
	{
		Collection<BadgeForDisplay> returnBadges = new ArrayList<BadgeForDisplay>();
		Iterator<JoseBadge> itr = badges.iterator();
		while(itr.hasNext())
		{
			JoseBadge badge = itr.next();
			returnBadges.add(BadgeRepository.convertToBadgeForDisplay(badge, ""));
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
		shortBadgeOverview = BadgeRepository.converttoBadgeForDisplayCollection(joseBadges); //need to optimize/get rid of this
	}
	
	private void reloadBadgeDefinitionsPerWeek()
	{
		Collection<BadgeForDisplay> toAddToGlobalDefs = new ArrayList<BadgeForDisplay>();
		Iterator<BadgeForDisplay> iterator = badgeDefinitions.iterator();
		//init allBadgeDefinitionsByPeriod
		allBadgeDefinitionsByPeriod.put(-1, new ArrayList<BadgeForDisplay>());
		for(int i=0; i < 7; i++)
		{
			allBadgeDefinitionsByPeriod.put(i, new ArrayList<BadgeForDisplay>());
		}
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
					allBadgeDefinitionsByPeriod.get(i).add(b);
					toAddToGlobalDefs.add(b);
				}
			}
			else
			{
				BadgeForDisplay b = new BadgeForDisplay(badge);
				b.biweek = -1;
				globalBadges.add(b);
				allBadgeDefinitionsByPeriod.get(-1).add(b);
				toAddToGlobalDefs.add(b);
			}
			iterator.remove();
		}
		badgeDefinitions.addAll(toAddToGlobalDefs);
		//for(int i = -1; i < 7; i++)
		//	Collections.sort((List<BadgeForDisplay>)(allBadgeDefinitionsByPeriod.get(i)));
		
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
			
			BadgeForDisplay badge = BadgeRepository.convertToBadgeForDisplay(responseValueWithBadgeData.originalrequest, responseValueWithBadgeData.username);
			awardedBadges.add(badge);
			
			String studentName = responseValueWithBadgeData.username;
			if(!awardedBadgesByStudent.containsKey(studentName))
				awardedBadgesByStudent.put(studentName, new ArrayList<BadgeForDisplay>());
			awardedBadgesByStudent.get(studentName).add(badge);
			
			//add it to the def list (to keep track of lots of things actually.. hm)
			addRewardedBadgesToDefinitions(badge);
		}
	}
	
	private void addRewardedBadgesToDefinitions(BadgeForDisplay badge)
	{
		Iterator<BadgeForDisplay> itr = badgeDefinitions.iterator();
		while(itr.hasNext())
		{
			BadgeForDisplay badgeDef = itr.next();
			if(badgeDef.name.compareTo(badge.name) == 0)
			{
				//add it to definition
				badgeDef.awardedBadges.add(badge);
				return;
				
			}
			
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
