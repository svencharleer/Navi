<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLEncoder" %>

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

<div id="header">
	<div id="globalheader">
		<h2>CHI13 Badge Board</h2>
	</div>
	<div id="filter">
	<a href="/badgeboard">Back</a>
	</div>
	<div id="badgedetail">Select a badge to view its details</div>
</div>
<!-- /header -->

<div id="badgeoverview" >
	
	<%
	
	
		Collection<BadgeForDisplay> badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("badges");
		if(badges.size() == 0)
		{
			%>
			
			<%
		}
		else
		{
	
	
			Iterator it = badges.iterator();
			while(it.hasNext())
			{
				BadgeForDisplay badge = (BadgeForDisplay)it.next();
		%>
			<div id="img<%= badge.GUID %>" class="indi_badges badgeicon badgePositive">
				<a href="javascript:showBadgeData('<%= badge.GUID %>')">
					<img src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
				</a>
			</div>
					
			<div id="<%= badge.GUID %>" style="display:none;">
				<a id="backpack" href="javascript:OpenBadges.issue('<%= badge.url %>');"><strong>+</strong> Add to Backpack</a><h2><%= badge.name %> </h2><p><%= badge.description %></p>	
	
			</div>
	 		
	
	<%  
	    	}
		}
		badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("notYetAchievedBadges");
		Iterator it = badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
	%>
		<div id="img<%= badge.GUID %>" class="indi_badges badgeicon badgePositive notYetAchievedBadge">
			<a href="javascript:showBadgeData('<%= badge.GUID %>')">
				<img src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
			</a>
		</div>
				
		<div id="<%= badge.GUID %>" style="display:none;">
			<h2><%= badge.name %> </h2><p><%= badge.description %></p>
		</div>
 		

<%  
    	}
		
	%>
	
	
</div>

</body>
</html>