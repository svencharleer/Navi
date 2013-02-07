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
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
<link rel='stylesheet' id='style-css'  href='/mobile.css' type='text/css' media='all' />
</head>
  <body>
<div id="greeting">
<%
	String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null || userId == "") 
    {
    		response.sendRedirect("/login.jsp");
			return;
    }
    else
    {
    	String username = (String) request.getSession().getAttribute("username");
      
    	  
%>
<p><strong><%=username %></strong> <br/>[<a href="/login?do=logout">sign out</a>]</p>
</div>
<div id="badges">
<%

	Collection<BadgeForDisplay> badges = (Collection<BadgeForDisplay>)request.getSession().getAttribute("badges");
	Iterator<BadgeForDisplay> it = badges.iterator();
	int i = 0;
	while(it.hasNext())
	{
		
	%>
 		<img class="badge<%= i %>" src="<%= ((BadgeForDisplay)it.next()).url.toString() %>"/>
 	<% 
 	i = (i+1)%2;
	}


    }
%>
</div>
  </body>
</html>