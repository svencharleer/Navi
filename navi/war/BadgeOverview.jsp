<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>

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
		function showAll()
		{
			$('.indi_badges').show();
			$('.group_badges').show();
		}
		
		function showIndi()
		{
			$('.indi_badges').show();
			$('.group_badges').hide();

		}
		
		function showGroup()
		{
			$('.indi_badges').hide();
			$('.group_badges').show();
		}
		
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
	<div id="filter">View 
		<a id="showAll" href="javascript:showAll();">All</a> /
		<a id="showIndi" href="javascript:showIndi();">Individual</a> /
		<a id="showGroup" href="javascript:showGroup();">Group</a>
	</div>
	<div id="badgedetail">Select a badge to view its details</div>
</div>
<div id="badgeoverview" >

	<%
	
		Collection<BadgeForDisplay> positive_indi_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("positive_indi_badges");
		Collection<BadgeForDisplay> positive_group_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("positive_group_badges");
		Collection<BadgeForDisplay> negative_indi_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("negative_indi_badges");
		Collection<BadgeForDisplay> negative_group_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("negative_group_badges");
		Collection<BadgeForDisplay> neutral_indi_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("neutral_indi_badges");
		Collection<BadgeForDisplay> neutral_group_badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("neutral_group_badges");
		
	%>
		
		
		
		


	<%
		
		Iterator<BadgeForDisplay> it = positive_indi_badges.iterator();
		int i = 0;
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>

				<div id="img<%= badge.GUID %>" class="indi_badges badgeicon badgePositive">
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>


				
	
	<%  
		i++;
	    }

	

		
		it = negative_indi_badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>
	
				<div id="img<%= badge.GUID %>" class="indi_badges badgeicon badgeNegative">
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>
	
	<%  
	    }
	
		

		
		it = neutral_indi_badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>
	
				<div id="img<%= badge.GUID %>" class="indi_badges badgeicon badgeNeutral">
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>
	
	<%  
	    }
	
	%>
	
		
	
	
	<%
		it = positive_group_badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>
	
				<div id="img<%= badge.GUID %>" class="group_badges badgeicon badgePositive" >
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>
	
	<%  
	    }
		
		it = negative_group_badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>
	
				<div id="img<%= badge.GUID %>" class="group_badges badgeicon badgeNegative">
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>
	
	<%  
	    }
		
		it = neutral_group_badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
			
	%>
	
				<div id="img<%= badge.GUID %>" class="group_badges badgeicon badgeNeutral">
					<a href="javascript:showBadgeData('<%= badge.GUID %>')">
						<img class="iconitself" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
					</a>
				</div>
				<div id="<%= badge.GUID %>" style="display:none;">
					<h2><%= badge.name %> </h2><p><%= badge.description %></p>	
				</div>
	
	<%  
	    }
		
	%>
	
		
		
</div>
<%@ include file="/WEB-INF/includes/footer.jsp" %>
</body>
</html>