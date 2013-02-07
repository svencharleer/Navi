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

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css" />
	<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>
	<script src="http://beta.openbadges.org/issuer.js"></script>
<!-- <link rel='stylesheet' id='style-css'  href='/mobile.css' type='text/css' media='all' />  -->
</head>
  <body>
  <div data-role="page" data-theme="b">

	<div data-role="header">
		<%
	String username = (String) request.getSession().getAttribute("username");
    if (username == null || username == "") 
    {
    		response.sendRedirect("/login.jsp");
			return;
    }
    else
    {
    	
      
    	  
%>
<h1><%=username %></h1><a data-role="button" data-theme="b" href="/login?do=logout">Logout</a>

	</div><!-- /header -->
	<div data-role="content">
  <ul data-role="listview" data-inset="true" data-filter="true">
	



<%

	Map<String,Collection<BadgeForDisplay>> badges = (Map<String,Collection<BadgeForDisplay>>)request.getSession().getAttribute("badges");
	Iterator it = badges.entrySet().iterator();
	int i = 0;
	int index = 0;
	Collection<BadgeForDisplay> detailedBadgesList = new ArrayList<BadgeForDisplay>();
	while(it.hasNext())
	{
		Map.Entry m = (Map.Entry)it.next();
		Collection<BadgeForDisplay> sameBadges = (Collection<BadgeForDisplay>)m.getValue();
		int count = sameBadges.size();
		
		BadgeForDisplay badge = (BadgeForDisplay)sameBadges.iterator().next();
		detailedBadgesList.add(badge);
	%>
 		<li  data-icon="false"><a href="/detailedbadge.jsp?id=<%= index %>" data-rel="dialog" data-transition="slidedown"><img  src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/><%= badge.name %> 
 		
 		<% if(count > 1){ %>
 		
 		<i>x<%= count %></i>
 		<% } %>
 		</a></li>
 	<% 
 	index++;
 	i = (i+1)%2;
	}

	request.getSession().setAttribute("detailedBadgesList",detailedBadgesList);
    }
%>
</ul>
</div>
</div>
  </body>
</html>