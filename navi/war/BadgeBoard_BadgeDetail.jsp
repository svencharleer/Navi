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
	<script type="text/javascript" src="d3/d3.v3.js"></script>
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
</head>
<body>

<div>
<%

	BadgeForDisplay badge = (BadgeForDisplay)request.getSession().getAttribute("badge");
	String backLink = (String)request.getSession().getAttribute("backLink");
	
	Map<String, HashMap<Date, Integer>> badgeStats = (Map<String, HashMap<Date, Integer>>)request.getSession().getAttribute("badgeStats");
	
	
%> 
	<div id="header">
		<div id="globalheader">
			<h2>CHI13 Badge Board</h2>
		</div>
		<div id="filter">
		<a href="<%= backLink %>">Back</a>
		</div>
		<div id="badgedetail">
		<img style="height:100px;float:right;" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
			<h2><%= badge.name %> </h2>
			<p><%= badge.description %></p>
		</div>
	</div>
	<div id="badgeoverview" >
	<%
		Iterator it = badgeStats.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			%>
			<%= (String)entry.getKey() %>:
			<%
			Map<Date,Integer> entry2 = (Map<Date,Integer>)entry.getValue();
			Iterator it2 = entry2.entrySet().iterator();
			while(it2.hasNext())
			{
				Map.Entry entry3 = (Map.Entry)it2.next();
				%>
				<%= ((Date)entry3.getKey()).toString() %> / <%= entry3.getValue().toString() %> 
				<%
			}
		}
	%>
	</div>	
	
	
</div>

</body>
</html>