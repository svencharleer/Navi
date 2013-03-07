package hci.wespot.navi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BadgeForDisplay implements Serializable, Comparable<BadgeForDisplay>{
	/**
	 * 
	 */
	public BadgeForDisplay(BadgeForDisplay badge)
	{
		this.GUID = UUID.randomUUID();
		this.imageUrl = badge.imageUrl;
		this.url = badge.url;
		this.name = badge.name;
		this.description = badge.description;
		this.connotation = badge.connotation;
		this.type = badge.type;
		this.timestamp = badge.timestamp;
		this.recipient = badge.recipient;
		this.biweek = badge.biweek;
		this.username = badge.username;
		awardedBadges = new ArrayList<BadgeForDisplay>();
		this.awardedBadges.addAll(badge.awardedBadges);
	}
	
	public BadgeForDisplay()
	{
		awardedBadges = new ArrayList<BadgeForDisplay>();
	}
	private static final long serialVersionUID = 1L;
	public UUID GUID;
	public String imageUrl;
	public String url;
	public String name;
	public String description;
	public String connotation;
	public String type;
	public long timestamp;
	public String recipient;
	public int biweek;
	public int iteration;
	public String username;
	
	//only used for definition. should split these up i guess
	public List<BadgeForDisplay> awardedBadges;
	
	
	@Override
	public int compareTo(BadgeForDisplay o) {
		int typeCompare = this.type.compareTo(o.type);
		if(typeCompare != 0) return typeCompare;
		return this.name.compareTo(o.name);
	}
}
