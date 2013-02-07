<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.Iterator" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css" />
	<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>

<!-- <link rel='stylesheet' id='style-css'  href='/mobile.css' type='text/css' media='all' />  -->
</head>
  <body>
  <div data-role="dialog" data-theme="b">

	
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


	<div data-role="content">
  


<%

	Collection<BadgeForDisplay> badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("badges");
	int index = Integer.parseInt(request.getParameter("id"));	
	BadgeForDisplay badge = (BadgeForDisplay) badges.toArray()[index];
	
	%> 
	<img style="width:20%;float:right;" src="<%= badge.url %>" alt="<%= badge.name %>"/>
	<div><h2><%= badge.name %></h2>
	<p><%= badge.description %></p>
 	</div>
 	<a data-role="button" href="/navi.jsp">Close</a>
 	<%
    }
 	%>

</div>
  </body>
</html>