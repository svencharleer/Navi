package hci.wespot.navi;

import java.io.Serializable;

public class FoundBadgeInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BadgeForDisplay badge;
	public BadgeForDisplay studentBadge;
	public boolean studentHasBadge = false;
}
