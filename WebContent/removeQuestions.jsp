<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@ page import = "com.accounts.*"%>
<%@ page import = "com.quizzes.*"%>
<%@ page import = "com.util.*"%>
<%@ page import = "java.util.*"%>
<%@ page import = "javax.swing.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="css//style.css" ></link>
</head>
<body>
	<ul>
	<nav>
	
		<header>
			<table id="header">
				<tr>			
					<%
						// get questions
				      	String quizName;
						if (request.getAttribute(Constants.QUIZ_NAME) != null) {
							quizName = (String) request.getAttribute(Constants.QUIZ_NAME);
						} else {
							quizName = request.getParameter("quizName");	
						}
						Quiz quiz = QuizManager.getQuiz(quizName);
						String name = (String)getServletContext().getAttribute("session_user");
						Account acct = AccountManager.getAccount(name);
						if (acct.isAdmin()) {
							out.println("<th class = \"btn\"><a href = \"adminHomepage.jsp\">Homepage</a></th>");
						} else {
							out.println("<th class = \"btn\"><a href = \"homepage.jsp\">Homepage</a></th>");
						}
						
						out.println("<th class = \"btn\"><a href = \"EditQuizInit?quiz=" + quizName + "\">Edit Quiz</a></th>");
					%>
				</tr>
			</table>
		</header>
    	
		<br>
    	<h2>Remove Questions</h2>
    	<br>
    
      <%
   		// get questions
		List<Question> questions = quiz.getQuestions();

		// remove question
		if (request.getParameter("index") != null) {
			int index = Integer.parseInt(request.getParameter("index"));
			quiz.removeQuestion(questions.get(index));
			questions.remove(index);
		}
		
		// remove quiz if it has no questions
		questions = quiz.getQuestions();
	%>
	<ul>
	<% 
		// print all remaining questions 
		if (questions != null) {
			for (int i=0; i< questions.size(); i++) {
				int question_num = i+1;
				out.println("<li>" + question_num + ". " + questions.get(i).getQuestion() + "    <a href = \"removeQuestions.jsp?quizName=" + quizName + "&index=" + i + "\">Remove</a>");
			}
		}
	%>
	</ul>
</body>
</html>