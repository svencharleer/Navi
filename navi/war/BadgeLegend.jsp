
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLEncoder" %>

<!--  NEED it AND username -->
<%


//LOOP OVER BADGES
	
	while(it.hasNext())
	{
		BadgeForDisplay badge = (BadgeForDisplay)it.next();	
		//FIGURE OUT IF USER HAS BADGE, IF SO, SHOW COLORED BADGE
		boolean awarded = false;
		Iterator<BadgeForDisplay> subIt = badge.awardedBadges.iterator();
		int count = badge.awardedBadges.size();
		while(subIt.hasNext())
		{
			 
			BadgeForDisplay awardedBadge = subIt.next();
			
			if(awardedBadge.username.compareTo(username) == 0)
				awarded = true;
		}
		String cssClass;
		String button;
		if(awarded)
		{
			 cssClass = "badgeicon";
			 button = "<a id=\"backpack\" href=\"javascript:OpenBadges.issue('" + badge.url +"');\"><strong>+</strong> Add to Backpack</a>";
		}
		else
		{
			 cssClass = "badgeicon notYetAchievedBadge";
			 button = "";
		}
		%>
		<a href="javascript:enableDisableGraph('<%= badge.GUID %>')">
			<div id="img<%= badge.GUID %>" class="<%= cssClass %>" data-enabled="true">
				
				<div>
				
				<div class="badgeInfo"><!-- <span><%= count %></span><img src="person.png"/><br/>--><%= badge.name %></div>
					<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					
				
				</div>
				
			</div>
			</a>
					
			<div id="<%= badge.GUID %>" style="display:none;">
				<h2><%= badge.name %> </h2><p><%= badge.description %></p> <%= button %>
				<p><%= count %> people have been awarded this badge.</p>
			</div>
	 	<%  
   	}
   	%>