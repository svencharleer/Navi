package hci.wespot.navi;

import java.io.Serializable;
import java.util.UUID;

public class BadgeForDisplay implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UUID GUID;
	public String imageUrl;
	public String url;
	public String name;
	public String description;
}
