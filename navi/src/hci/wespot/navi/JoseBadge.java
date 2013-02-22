package hci.wespot.navi;

import java.io.Serializable;

public class JoseBadge  implements Serializable
{
	private static final long serialVersionUID = 1L;
	String id; 
	OpenBadges_Badge badge;
	String recipient;
	String connotation;
	String type;
	long timestamp;
}