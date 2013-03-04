<%@page import="com.google.apphosting.api.ApiBasePb.Integer32Proto"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.Collection" %>
<%@ page import="hci.wespot.navi.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.joda.time.DateTime" %>
<%@ page import="java.util.Map.*" %>

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
	<script type="text/javascript">
		function submitDateRange(tmp)
		{
			
			var startDate = new Date($("#datePicker_start").val()).valueOf();
			var endDate = new Date($("#datePicker_end").val()).valueOf();
			
			window.location.href ="badgeboard?username=<%= request.getParameter("username") %>&week=<%= request.getParameter("week") %>&startdate="+ startDate + "&enddate="+  endDate;
		}
	</script>

<%
	List<FoundBadgeInfo> badgeInfos = (List<FoundBadgeInfo>)request.getSession().getAttribute("badgeInfos");
	String backLink = (String)request.getSession().getAttribute("backLink");
	TreeMap<String,TreeMap<Long, Collection<BadgeForDisplay>>> badgeStats = (TreeMap<String,TreeMap<Long, Collection<BadgeForDisplay>>>)request.getSession().getAttribute("badgeStats");
	int nrOfStudents = (Integer) request.getSession().getAttribute("nrOfStudents");
	List<BadgeForDisplay> badges = (List<BadgeForDisplay>)request.getSession().getAttribute("badges");
	String username = request.getParameter("username");
	Iterator<BadgeForDisplay> it = badges.iterator();
%>
	
	<script type="text/javascript">
	var svg;
	var xscale;
	var yscale;
	
	function initSVG()
	{
		var height = 400;
		var width = 800;
		var padding = 20;
		var margin = 20;
		svg = d3.select("#graphs")
						.append("svg")
						.attr("class", "svgChart")
						.attr("viewBox", "0 0 " + width + " " + height)
						.attr("preserveAspectRatio", "xMinYMin meet");
		
		var data = [
		            
		<%
				Iterator<Entry<String, TreeMap<Long,Collection<BadgeForDisplay>>>> badgeStatsItr = badgeStats.entrySet().iterator();
				
				long total = 0;
				Iterator<Entry<Long, Collection<BadgeForDisplay>>> it2 = badgeStatsItr.next().getValue().entrySet().iterator();
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
		
		xscale = d3.time.scale()
							.domain([
							         d3.min(data, function(d) {return d.date;}), 
							         d3.max(data, function(d) {return d.date;})
							         ])
							.range([padding+margin,width - 2*padding - margin]);
		yscale = d3.scale.linear()
							.domain([0, <%= nrOfStudents %>])
							.range([height - padding -2*margin,padding]);
		
		
		drawAxes(svg, xscale, yscale, data, height, width,margin,padding);
	}
	
	/* D3 GRAPH DRAWING */
	function drawCurve(svg, data, xscale, yscale, awarded, timestamp, red, green, blue)
	{
		var personalBadge ={date: "",count: 0};
		
		if(awarded == true)
		{
			personalBadge.date = timestamp;
		}
	
		for(var i = 0; i < data.length;i++)
		{
			if(personalBadge.date == data[i].date)
			{
				personalBadge.count = data[i].count;
			}
			data[i].date = new Date(data[i].date);
			
		}
		if(awarded == true)
		{
			personalBadge.date = new Date(personalBadge.date);
		}
		
		var lineFunction = d3.svg.line()
		 .x(function(d) { return xscale(d.date); })
		 .y(function(d) { return yscale(d.count); })
	 	 .interpolate("linear");

	 	var rgb = "rgb("+red+","+green+","+blue+")";
		var lineGraph = svg.append("path")
		                            .attr("d", lineFunction(data))
		                            .attr("class", "linecolor")
		                            .attr("stroke", rgb)
		                            ;
		
		if(awarded == true)
		{
			
			svg 
		  		.append("circle")
		  		.attr("class","yourbadge")
		    	.attr("cx", function(d) {return xscale(personalBadge.date);})
		    	.attr("cy", function(d) {return yscale(personalBadge.count);})
		    	.attr("r",5)
		    	.attr("stroke", rgb);
		}
	}
	
	function enableDisableGraph(guid)
	{
		if($("#img"+guid).attr("data-enabled") == "true")
		{
			$("#img"+guid).attr("data-enabled","false");
			$("#img"+guid).attr("style","border:solid 1px white;");
			
			d3.select("svg").remove();
			initSVG();
			drawEnabledGraphs();
		}
		else
		{
			$("#img"+guid).attr("data-enabled","true");
			d3.select("svg").remove();
			initSVG();
			drawEnabledGraphs();
		}
	}
	
	function drawAxes(svg, xscale, yscale, data, height, width,margin,padding)
	{
		var xAxis = d3.svg.axis()
						.scale(xscale)
					    .orient("bottom")
					    .tickFormat(d3.time.format('%d/%m'))
					    .ticks(data.length/2);
		svg.append("text")      // text label for the x axis
	        	.attr("x", width/2 )
	        	.attr("y", height -margin )
	        	.style("text-anchor", "middle")
	        	.text("Date");
	
		var yAxis = d3.svg.axis()
					    .scale(yscale)
					    .orient("left")
					    .ticks(10);
		svg.append("text")
		        //
		        .attr("transform", "rotate(-90)")
		        .attr("x",  -height/2)
		        .attr("y", 0)
	
		        .attr("dy", "1em")
		        .style("text-anchor", "middle")
		        .text("# students with badge");
		
		svg.append("g")
	    			.call(xAxis)
	                .attr("transform", "translate("+ 0 +"," + (height - padding - 2*margin) + ")")
	                .attr("class", "axis")
	                .selectAll("text")
	                
	                ;
		svg.append("g")
	    			.call(yAxis)
	                .attr("transform", "translate(" + (padding + margin) + ",0)")
	                .attr("class", "axis")
	                ;
	                
	}
	
	function drawEnabledGraphs()
	{
	var i = 0;
	//iterate over different stats
	<%  
		Iterator<FoundBadgeInfo> fbiitr = badgeInfos.iterator();
		
		while(fbiitr.hasNext())
		{

			FoundBadgeInfo fbi = fbiitr.next();
	%>
	var awarded = <%= fbi.studentHasBadge %>;
	<% 
	if(fbi.studentHasBadge) 
	{
	%>
	var	timestamp = <%= fbi.studentBadge.timestamp %>;
	<% 
	} else 
	{%>
	var	timestamp = 0;
	<%}%>
	
		data = [
	            
	        	<%
	        			
	        			
	        			total = 0;
	        			it2 = badgeStats.get(fbi.badge.GUID.toString()).entrySet().iterator();
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
	
	var frequency = 1.7;
	red   = Math.floor(Math.sin(frequency*i + 0) * 127 + 128);
   	green = Math.floor(Math.sin(frequency*i + 2) * 127 + 128);
   	blue  = Math.floor(Math.sin(frequency*i + 4) * 127 + 128);	
   	if($("#img<%=fbi.badge.GUID.toString()%>").attr("data-enabled") == "true")
   	{
   		drawCurve(svg, data, xscale, yscale, awarded, timestamp,red, green, blue);
		setColorOfLegend('<%= fbi.badge.GUID.toString() %>', red, green, blue);
   	}
	i++;
	<% 

		}
	%>
	}
	
	function  setColorOfLegend(guid, red, green, blue)
	{
		$("#img"+guid).attr("style", "border: solid 1px rgb("+red+","+green+","+blue+")");
	}
	
	function showHideLegend()
	{
		if($("#options").attr("style") == "display:none")
		{
			$("#filterLink").text("Hide Filter Options");
			$("#options").attr("style","display:visible");
		}
		else
		{
			$("#filterLink").text("Show Filter Options");
			$("#options").attr("style","display:none");	
		}
		
	}
	</script>
</head>
<body>


	<div id="header">
		<div id="globalheader">
			<h2>CHI13 Badge Board</h2>
		</div>
		<div id="filter">
			<a href="<%= backLink %>">Back</a> -- <a href="javascript:showHideLegend();" id="filterLink">Show Filter Options</a>
		</div>
	</div>
	
	
	<div id="badgegraph" >
	<div id="options" style="display:none">
		<div id="graphoptions">
			
			
		<!-- DATE PICKERS -->
		<%
			DateTime startDate = (DateTime)request.getSession().getAttribute("startdate");
			DateTime endDate = (DateTime)request.getSession().getAttribute("enddate");
		%>
		<label for="startdate">Between</label> <input type="date" name="startdate" id="datePicker_start" value="<%= startDate.getYear() %>-<%= String.format("%02d",startDate.getMonthOfYear()) %>-<%= String.format("%02d", startDate.getDayOfMonth()) %>">
		<label for="enddate">and</label> <input type="date" name="enddate" id="datePicker_end" value="<%= endDate.getYear() %>-<%= String.format("%02d",endDate.getMonthOfYear()) %>-<%= String.format("%02d", endDate.getDayOfMonth()) %>"> <a href="javascript:submitDateRange();">Go</a> <br/>
		</div>
	
		<div id="badgelegend">
		
			<%@ include file="BadgeLegend.jsp" %>
		</div>
	</div>
	<div id="graphs">
	</div>
	
	<script type="text/javascript">
	initSVG();
	
	drawEnabledGraphs();
	
	</script>
	</div>	
	
	


</body>
</html>