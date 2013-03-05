<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Map.*" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<title>CHI13 Badge Overview</title>
	<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="mobile.css" />
	<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
	<script src="http://beta.openbadges.org/issuer.js"></script>
	
	<script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-38498955-1']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
	</script>
	<script type="text/javascript">
	function showBadgeData(value)
		{
			$('.badgeicon').removeClass('badgeiconSelected');
			$('#img'+value).addClass('badgeiconSelected');
			
			$('#badgedetail').html($('#'+value).html());


			
		}
	</script>
</head>
<body>
<%
String username = request.getParameter("username");
%>
<div id="header">
	<div id="globalheader">
		<h2>CHI13 Badge Board - <%= username %></h2>
	</div>
	<div id="filter">
	<a href="/badgeboard">Back</a>
	</div>
	<div id="badgedetail">Select a badge to view its details</div>
</div>
<!-- /header -->

<div id="badgeoverview" >
	
	<%
	
	
		TreeMap<Integer,List<BadgeForDisplay>> badges = (TreeMap<Integer,List<BadgeForDisplay>>)request.getSession().getAttribute("badges");
		
		if(badges.size() == 0)
		{
			%>
			
			<%
		}
		else
		{
			//iterate over weeks (start with global)
			Iterator<Entry<Integer, List<BadgeForDisplay>>> itr = badges.entrySet().iterator();
			while(itr.hasNext()){
				Entry<Integer, List<BadgeForDisplay>> entry = itr.next();
				//DRAW WEEK/GLOBAL
				if(entry.getKey() == -1)
				{
					%>
					<h2>Global Badges - <a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=-1">Stats</a></h2> 
					 <% 
				}
				else
				{
					%>
					<h2> Period <%= entry.getKey()+1 %> - <a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=<%= entry.getKey() %>">Stats</a></h2>
					<%
				}
				//LOOP OVER BADGES
				Iterator<BadgeForDisplay> it = entry.getValue().iterator();
				boolean showStatLink = true;
				%>
				<%


//LOOP OVER BADGES
	
	while(it.hasNext())
	{
		BadgeForDisplay badge = (BadgeForDisplay)it.next();	
		//FIGURE OUT IF USER HAS BADGE, IF SO, SHOW COLORED BADGE
		boolean awarded = false;
		Iterator<BadgeForDisplay> subIt = badge.awardedBadges.iterator();
		int count = badge.awardedBadges.size();
		String studentNames = "";
		while(subIt.hasNext())
		{
			 
			BadgeForDisplay awardedBadge = subIt.next();
			
			if(awardedBadge.username.compareTo(username) == 0)
				awarded = true;
			studentNames += " " + awardedBadge.username;
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
			<div title="<%= studentNames %>" id="img<%= badge.GUID %>" class="<%= cssClass %>">
			<%
				String countStyle = "";
				if(count == 0)
					countStyle = "color:rgb(213, 212, 208)";
			%>
			
				<div class="badgeCount"><img src="person.png"/><span style="<%= countStyle %>"><%= count %></span></div>
				<div>
				<a href="javascript:showBadgeData('<%= badge.GUID %>')">
					<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
				</a>
				</div>
				
			</div>
					
			<div id="<%= badge.GUID %>" style="display:none;">
				<h2><%= badge.name %> </h2><p><%= badge.description %></p> <%= button %>
				<p><%= count %> people have been awarded this badge.</p>
				<% if(showStatLink) { %>
					<a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=<%= badge.biweek %>&badgeid=<%= badge.GUID.toString()%>">View stats</a>	
				<% } %>
			</div>
	 	<%  
   	}
   	%>
				
				<hr/> <%
				
			}
		}
	%>
	
	
</div>
<%@ include file="/WEB-INF/includes/footer.jsp" %>
</body>
</html>