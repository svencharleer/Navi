<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css" />
	<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>

<!-- <link rel='stylesheet' id='style-css'  href='/mobile.css' type='text/css' media='all' />  -->
</head>
  <body>
  <div data-role="page" data-theme="b">

	<div data-role="header">
	<h1>Navi HCI</h1>
	</div><!-- /header -->
	<div data-role="content">
<form action=/login method="POST">
<div data-role="fieldcontain" class="ui-hide-label">
<label for="username">OpenBadge Login:</label> <input type="text" id="username" placeholder="OpenBadge ID" name="username">

</div>
<input type="submit" value="Submit" />
</form>

</div>

<!--  <img src="navi_anim.gif"/ style="margin-left:auto;margin-right:auto;display:block;margin-top:100px;">  -->
</body>
</html>