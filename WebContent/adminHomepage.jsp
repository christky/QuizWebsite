<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page import = "com.accounts.*"%>
	<%@ page import = "java.util.*"%>
	<%@ page import = "javax.swing.*" %>

	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- <link rel="stylesheet" href="css//main.css" ></link>
 -->
 <link rel="stylesheet" href="homepage.css" ></link>
 
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%String name = (String)getServletContext().getAttribute("session_user");
Account acct = AccountManager.getAccount(name);
String sel_type = (String)request.getParameter("choice");


%>
<title>Welcome <%=name%></title>
</head>
	
<body>
	<%String errMsg = (String)request.getAttribute("errMsg");%>
	<%if (errMsg != null) {%>
		<%= errMsg%>
	<%}%>
	<table id="header">
			<tr>
				<%/*if(acct.isAdmin()) {
					out.println("<th<a href = \"adminHomepage.jsp\">Homepage</a></th>");
				} else {
					out.println("<th<a href = \"homepage.jsp\">Homepage</a></th>");
				}
				
					*/%> 
					
				<th><a href = <%="\"adminHomepage.jsp?id="+name+"\""%>>Homepage</a></th>
				<th><a href = "showAnnouncements.jsp">Announcements</a></th>
				<th>My Achievements</th>
				<th>My Messages 
					<form action = <%="\"showMessage.jsp?id="+name+"\""%>>					
						<select name = "choice">
							<option>Received Messages</option>
							<option>Sent Messages</option>
							<option>Send A Message</option>
						</select>
						<input name="choice" type="hidden" value=<%=(String)request.getParameter("choice")%>>
						<input name="id" type="hidden" value=<%=name%>>
						<input type = "submit" value = "Go">
					</form>
				</th>
				<th> <a href="searchFriends.jsp?id=<%=name%>"> Lookup Users</a> </th>
				<th>Quizzes</th>
				<th><a href = <%="\"login.jsp?errMsg=\"LoggedOut\""%>>Logout</a></th>
			</tr>
	</table>
	<h2>Nice to see you, <%=name %></h2>
	<table id = "admin_content">
		<tr>
			<td>
				<div id = "addAnnouncement">
					Add an Announcement<br></br>
					<form action = "AdminServlet" method = "POST">
						<input type = "hidden" name = "id" value = "<%=name %>">
						<textarea rows = "4" cols = "50" name = "new_ancmnt"></textarea>
						<input type = "submit"></input>
					</form>
				</div>
				<div id = "siteStats">
					Site Statistics<br></br>	
				</div>	
			</td>
		</tr>
		<tr>
			<td>
					<div id = "removeAcct">
						Remove Account<br></br>
						<form action = "AdminServlet" method = "POST">
							Account id: <input type = "text" name = "remove_acct" value = <% 
							String removed_id = request.getParameter("remove_acct");
							if (removed_id != null ) {
								out.println("\"Account "+removed_id+" removed.\"");
							} else {
								out.println("\"No Account Removed\"");
							}
							%>></input>
							Re-enter account id:<input type = "text" name = "conf_remove_acct"></input>	
							<input type = "hidden" name = "id" value = <%="\""+name+"\"" %>>					
							<input type = "submit"></input>
						</form>
					</div>									
			</td>
		</tr>
	</table>
	
	
	<table id = "content">
		<tr>
			<%//Displays any admin announcements as a list%>
			<%
			ArrayList<String> admin_anmts = (ArrayList<String>)getServletContext().getAttribute("announcements");
			%>
			<td><div id="announcements"><a href = "showAnnouncements.jsp">Announcements</a>
				<%if (admin_anmts != null) { 
					out.println("<ul>");
					int anmts_len = admin_anmts.size();
					for (int i = anmts_len - 1; i > -1; i--) { 
						out.println("<li>"+admin_anmts.get(i)+"</li>");
						if (i == anmts_len - 5) i = -1;
					} 
					out.println("</ul>");
				} else {
					out.println("<br></br>Hello, no new announcements.");
				}
				%>	
			</div></td>
			<td>
				<div id = "send_messages">
					Send A Message
					<form action = <%="\"MainMessageServlet?id="+name+"\""%>>
						<input type = "text" name = "friend_id" value = "Enter Friends Username"></input>
						<input type = "radio" name = "msg_type" value = "Add Friend" >Add Friend<br></input>
						<input type = "text" name = "quiz_name" value = "Enter Quiz Name"></input>
						<input type = "radio" name = "msg_type" value = "Challenge">Challenge<br></input>
						<textarea rows="4" cols="20" name = "msg_content"></textarea>
						<input type = "radio" name = "msg_type" value = "Send Note">Note<br></input>
						<input type = "submit" value = "Send Message">
					</form>
				</div>
				<div id="read_messages">
					<form action = <%="\"adminHomepage.jsp?id="+name+"\""%>>
						<select name = "choice">
							<option>Received Messages</option>
							<option>Sent Messages</option>
						</select>
						<input type = "submit" value = "Display Messages">
					</form>
					<%			
						//TODO: The ReceivedMessages is buggy.						
						List<Message> messages = acct.getReceivedMessages();	
						out.println("<a href =\"showMessage.jsp?choice="+sel_type+"&id="+name+"\""+">View All Messages</a>");
						if (messages.size() > 0) {
							out.println("<table>");
							for ( int i = messages.size() -1; i > -1;i-- ) {
								out.println("<tr>");
								Message msg = messages.get(i);
								out.println("<td>"+msg.getSender()+"</td>");
								out.println("<td>"+msg.getDate()+"</td>");
								out.println("<td>"+msg.getType()+"</td>");
								if (i == messages.size() -5) i = -1;
							}
							out.println("</table>");
						}
						
					//TODO::Extension to add Sorting mechanisms to table cols					
					%>

					
					
				</div>
			</td>
		</tr>
		<tr>
			<td><div id="achievements">Achievements</div></td>
			<td><div id="friendsAchievements">Recent Friends
					Achievements</div></td>
		</tr>
		<tr>
			<td><div id="createdQuizzes">My created Quizzes</div></td>
			<td><div id="popularQuizzes">Popular Quizzes</div></td>
		</tr>
		<tr>
			<td><div id="recentQuizzes">Recent Quizzes</div></td>
			<td><div id="myHistory">My History</div></td>		
		</tr>
	</table>
</body>
</html>
