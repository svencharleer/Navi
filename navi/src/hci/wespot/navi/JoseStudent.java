package hci.wespot.navi;
import java.io.Serializable;


class JoseStudent  implements Serializable, Comparable<JoseStudent>
{
	private static final long serialVersionUID = 1L;
	String username;
	String full_count;
	
	@Override
	public int compareTo(JoseStudent o) {
		return this.username.compareTo((o).username);
	}
}