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
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css" />
	<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>
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
</head>
<body>
<div data-role="dialog" data-theme="b">


	<div data-role="content" style="background:#fff;">
	<%
	
	
		BadgeForDisplay badge = (BadgeForDisplay)request.getSession().getAttribute("badge");
		String backLink = (String)request.getSession().getAttribute("backLink");
	%> 
		<img style="width:20%;float:right;" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
		<div>
			<h2><%= badge.name %></h2>
			<p>
				<%= badge.description %>
			</p>
 		</div>
 		<a data-role="button" data-theme="a" data-icon="star" href="javascript:OpenBadges.issue('<%= badge.url %>');">Add to Backpack</a>
 		<a data-role="button" href="javascript:history.back();">Close</a>
 	
	</div>
</div>
</body>
</html>