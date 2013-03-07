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
	function showPeriod(value)
	{
		$('div[id^="period"]').attr("style","display:none;")
		$('#period'+value).attr("style","");
	}
	
	function showIteration(value)
	{
		$('div[id^="iteration"]').attr("style","display:none;")
		
		$('#iteration'+value).attr("style","");
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
	int lastPeriod = -1;
	int lastIteration = -1;
	
	
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
					<div id="global" class="badgesection">
					<h2>Global Badges - <a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=-1">Stats</a></h2> 
					 <% 
				}
				else if(entry.getKey() < 100)
				{
					%>
					<div id="period<%= entry.getKey() %>" class="badgesection" style="display:none;">
					<h2> Period <span class="colorOrange" ><%= entry.getKey()+1 %></span> - <a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=<%= entry.getKey() %>">Stats</a>
					<% if(entry.getKey() > 0) { %>
					[<a href="javascript:showPeriod(<%= entry.getKey() -1 %>)">prev</a>/
					<% } 
					else
					{%>
					[prev/
					<% 
					} %>
					
					<% if(entry.getKey() < 6) { %>
					<a href="javascript:showPeriod(<%= entry.getKey() +1 %>)">next</a>]
					<% } 
					else
					{%>
					next]
					<% 
					} %>
					
					</h2>
					<%
				}	
				else
				{
					%>
					<div id="iteration<%= entry.getKey() %>" class="" style="display:none;">
					<h2> Iteration <span class="colorOrange" ><%= entry.getKey()+1-100 %></span> - <a href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&iteration=<%= entry.getKey() %>">Stats</a>
					<% if(entry.getKey() > 100) { %>
					[<a href="javascript:showIteration(<%= entry.getKey()  -1 %>)">prev</a>/
					<% } 
					else
					{%>
					[prev/
					<% 
					} %>
					
					<% if(entry.getKey() < 103) { %>
					<a href="javascript:showIteration(<%= entry.getKey() +1 %>)">next</a>]
					<% } 
					else
					{%>
					next]
					<% 
					} %>
					
					</h2>
					
					
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
			 button = "<a class=\"topbutton\" href=\"javascript:OpenBadges.issue('" + badge.url +"');\"><strong>+</strong> Add to Backpack</a>";
			
				
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
				else
				{
					 int counter = entry.getKey();
						if(counter >= 0 && counter < 100)
						{
							lastPeriod = counter;
						}
						else if(counter >= 100)
						{
							lastIteration = counter;
						}
				}
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
				
				<% if(showStatLink) { %>
					<a class="topbutton" href="/badgeboard?username=<%= URLEncoder.encode(username, "UTF-8") %>&week=<%= badge.biweek %>&badgeid=<%= badge.GUID.toString()%>">View stats</a>	
				<% } %>
			</div>
	 	<%  
   	}
   	%>
				
			
				</div>
				 <%
				
			}
		}
	%>
	
	
</div>

<script type="text/javascript">
	<% if(lastPeriod != -1) { %>
		showPeriod(<%= lastPeriod %>);
	<% } else { %>
		showPeriod(0);
	<%
	}
	if(lastIteration != -1) { %>
	showIteration(<%= lastIteration %>);
	<% } else { %>
	showIteration(100);
	<% } %>
</script>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
</body>
</html>