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
<div data-role="page" data-theme="b">
	<div data-role="header">
		CHI13 Badge Board
	</div>
<!-- /header -->

	<div data-role="content" style="background:#fff;">
	<ul data-role="listview" data-inset="true" data-filter="true">
	<%
	
	
		Collection<BadgeForDisplay> badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("badges");
		Iterator it = badges.iterator();
		while(it.hasNext())
		{
			BadgeForDisplay badge = (BadgeForDisplay)it.next();
	%>
		<li data-icon="false">
			<a href="/detailedbadge.jsp?id=" data-rel="dialog" data-transition="slidedown">
				<img  src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>
				<%= badge.name %>
			</a>
		</li>
 		
	
	<%  
	    }
	%>
	</ul>
	</div>
</div>
</body>
</html>