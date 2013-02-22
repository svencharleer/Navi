<%@page import="com.google.apphosting.api.ApiBasePb.Integer32Proto"%>
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
	<script type="text/javascript" src="d3.v3/d3.v3.js"></script>
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


<%

	BadgeForDisplay badge = (BadgeForDisplay)request.getSession().getAttribute("badge");
	String backLink = (String)request.getSession().getAttribute("backLink");
	System.out.println("hm");
	Map<Long, Collection<JoseBadge>> badgeStats = (Map<Long, Collection<JoseBadge>>)request.getSession().getAttribute("badgeStats");
	int nrOfStudents = (Integer) request.getSession().getAttribute("nrOfStudents");
%> 
	<div id="header">
		<div id="globalheader">
			<h2>CHI13 Badge Board</h2>
		</div>
		<div id="filter">
			<a href="<%= backLink %>">Back</a>
		</div>
		<div id="badgedetail">
		<!--  <img style="height:100px;float:right;" src="<%= badge.imageUrl %>" alt="<%= badge.name %>"/>  -->
			<h2><%= badge.name %> </h2>
			<p><%= badge.description %></p>
		</div>
	</div>
	<div id="badgeoverview" >
	<script type="text/javascript">
	var data = [
	            
	<%
	
		
			
			long total = 0;
			Iterator it2 = badgeStats.entrySet().iterator();
			while(it2.hasNext())
			{
				Map.Entry entry3 = (Map.Entry)it2.next();
				total += ((Collection<JoseBadge>)entry3.getValue()).size();
				%>
				{
				date: <%= ((Long)entry3.getKey()).toString() %>,
				count: "<%= total %>" 
				},
				<%
				
			}
		
	%>
	];
	for(var i = 0; i < data.length;i++)
	{
		data[i].date = new Date(data[i].date);
	}
	
	
	var height = 400;
	var width = 700;
	var padding = 40;
	var svg = d3.select("#badgeoverview")
				.append("svg")
				.attr("width",width)
				.attr("height",height);
	
	
	var xscale = d3.time.scale()
						.domain([
						         d3.min(data, function(d) {return d.date;}), 
						         d3.max(data, function(d) {return d.date;})
						         ])
						.range([padding,width - 2*padding]);
	var yscale = d3.scale.linear()
						.domain([0, <%= nrOfStudents %>])
						.range([height - padding,padding]);
	
	var lineFunction = d3.svg.line()
	 .x(function(d) { return xscale(d.date); })
	 .y(function(d) { return yscale(d.count); })
 	 .interpolate("linear");
	
	

	var lineGraph = svg.append("path")
	                            .attr("d", lineFunction(data))
	                            .attr("stroke", "blue")
	                            .attr("stroke-width", 2)
	                            .attr("fill", "none");
	
	svg.append("g")
    .call(d3.svg.axis()
    			
                .scale(xscale)
                .orient("bottom")
                .tickFormat(d3.time.format('%d/%m'))
                .ticks(data.length/2))
                .attr("transform", "translate(0," + (height - padding) + ")")
                .attr("class", "axis");
	svg.append("g")
    .call(d3.svg.axis()
                .scale(yscale)
                .orient("left")
                .ticks(10))
                .attr("transform", "translate(" + padding + ",0)")
                .attr("class", "axis");;
	
	</script>
	</div>	
	
	


</body>
</html>