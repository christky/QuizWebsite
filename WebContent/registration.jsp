<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="css//main.css" ></link>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Register</title>
</head>
<body>
	<%String errMsg = (String)request.getAttribute("errMsg");%>
	<%if (errMsg == null) { %>
		<%="<h1>Enter your desired username and password!</h1>"%>
	<%} else {%>
		<%= errMsg%>
	<%}%>
	<form action = "RegistrationServlet" method = "POST">
		<p>User: <input type = "text" name = "user"/></p>
		<p>Password:<input type = "password" name = "password"/></p>
		<input type="checkbox" name="isAdministrator" value="Administrator"> Administrator<BR><BR>
		<input type = "submit"/>
	</form>
	<p>Already have an Account? <a href = "login.jsp">Login here!</a></p>
	
</body>
</html>

