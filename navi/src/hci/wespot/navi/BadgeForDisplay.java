package hci.wespot.navi;

import java.io.Serializable;
import java.util.UUID;

public class BadgeForDisplay implements Serializable{
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
	}
	
	public BadgeForDisplay()
	{
		
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
}
