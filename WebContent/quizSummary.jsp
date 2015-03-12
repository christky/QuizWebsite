<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import = "com.accounts.*"%>
<%@ page import = "com.quizzes.*"%>
<%@ page import = "com.util.*"%>
<%@ page import = "java.util.*"%>
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Quiz Summary</title>
</head>
<body>
<%
// Get quiz name and instantiate a Quiz object.
String quizName = (String)request.getParameter(Constants.QUIZ_NAME);
Util.validateString(quizName);
Quiz quiz = QuizManager.getQuiz(quizName);

// Get logged in user.
String loggedInUser = (String)getServletContext().getAttribute("session_user");
Util.validateString(loggedInUser);
Account user = AccountManager.getAccount(loggedInUser);
%>


<!-- Print Page title and quiz name -->
<h1>Quiz Profile</h1>
<h2><%= quiz.getName() %></h2>


<!-- Print creator with link -->
<h5>created by: <a href="accountProfile.jsp?friend_id=<%=quiz.getCreator().getUserName()%>&username=<%=loggedInUser%>"><%= quiz.getCreator().getUserName() %></a></h5>


<!-- Print quiz description -->
<h3>Description: </h3>
<p> <%=quiz.getDescription()%>

<%
// Get Past Performance.
List<Record> pastPerformance = user.getPastPerformance(0);
%>

<!--  Past performance table -->
<h4>Past Performance:</h4>
<table id="pastPerformanceTable">
		<tr>
			<th>Date</th>
			<th>Score</th>
			<th>Elapsed Time</th>
		</tr>
		
		<%
			for (Record record : pastPerformance) {
				out.println("<td>" + record.getDate() + "</td>" 
				+ "<td>" + record.getScore() + "</td>" 
				+ "<td>" + record.getElapsedTime() + "</td>");
			}		
		%>
</table>


<%
// Get top scorers
List<Record> topScorers = quiz.getTopPerformers(0);
%>


<!-- Top Scorers table -->
<h4>Top Scorers:</h4>
<table id="topScorers">
		<tr>
			<th>Username</th>
			<th>Date</th>
			<th>Score</th>
			<th>Elapsed Time</th>
		</tr>
		
		<%
			for (Record record : topScorers) {
				out.println("<td>" + record.getUser().getUserName() + "</td>"
				+ "<td>" + record.getDate() + "</td>" 
				+ "<td>" + record.getScore() + "</td>" 
				+ "<td>" + record.getElapsedTime() + "</td>");
			}		
		%>
</table>


<%
// Get top scorers in the past 15 minutes.
List<Record> recentTopScorers = quiz.topScorersWithinTimePeriod(0.25);
%>

<!-- Recent Top Scorers table -->
<h4>Recent Top Scorers:</h4>
<table id="recentTopScorers">
		<tr>
			<th>Username</th>
			<th>Date</th>
			<th>Score</th>
			<th>Elapsed Time</th>
		</tr>
		
		<%
			for (Record record : recentTopScorers) {
				out.println("<td>" + record.getUser().getUserName() + "</td>"
				+ "<td>" + record.getDate() + "</td>" 
				+ "<td>" + record.getScore() + "</td>" 
				+ "<td>" + record.getElapsedTime() + "</td>");
			}		
		%>
</table>


<%
// Get recent scores of all users.
List<Record> recentScores = quiz.getRecentPerformance(0);
%>

<!-- Recent Performance of All Users -->
<h4>Recent Performance of All Users:</h4>
<table id="recentPeformance">
		<tr>
			<th>Username</th>
			<th>Date</th>
			<th>Score</th>
			<th>Elapsed Time</th>
		</tr>
		
		<%
			for (Record record : recentTopScorers) {
				out.println("<td>" + record.getUser().getUserName() + "</td>"
				+ "<td>" + record.getDate() + "</td>" 
				+ "<td>" + record.getScore() + "</td>" 
				+ "<td>" + record.getElapsedTime() + "</td>");
			}		
		%>
</table>


<%
// Get average statistics for this quiz.
Map<String, Double> average = quiz.getAveragePerformance();
%>

<!-- Average performance table -->
<h4>Average Performance:</h4>
<table id="averagePerformance">
		<tr>
			<th>Score</th>
			<th>Elapsed Time</th>
		</tr>
		
		<%
			out.println("<td>" + average.get(Constants.SCORE) + "</td>"
				+ "<td>" + average.get(Constants.ELAPSED_TIME) + "</td>"); 
		%>
</table>


<!-- Button to take quiz -->
<a href="quizHome.html?user=<%=loggedInUser%>&quiz=<%=quizName%>"><button type="button">Take Quiz</button></a>

<!-- Button to Edit Quiz -->
<form action="EditQuizInit" method="POST">
<input type="hidden" name="quiz" value="<%=quizName%>">
<input type="submit" value="Edit Quiz">
</form>

<!-- Button to go back to Homepage -->
<a href="homepage.jsp"><button type="button">Back Home</button></a>
</body>
</html>