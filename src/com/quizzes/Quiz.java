package com.quizzes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.accounts.Account;
import com.accounts.AccountManager;
import com.dbinterface.Database;
import com.util.Constants;
import com.util.Util;

/**
 * Contains Quiz information. Talks to the database interface layer
 * to obtain and set fields.
 * 
 * @author Sam
 *
 */
public class Quiz implements Constants {
	
	private String name;
	
	
	/**
	 * Constructs a new Quiz object and adds the quiz info to the database.
	 */
	public Quiz(String name, Account creator, String description, String date, 
			boolean isRandom, boolean isOnePage, boolean isImmediate) {
		
		Util.validateString(name);
		Util.validateObject(creator);
		Util.validateString(description);
		Util.validateString(date);
		
		this.name = name;
		
		// Ensure quiz doesn't already exists.
		if (Database.getValues(QUIZZES, QUIZ_NAME, name, QUIZ_NAME) != null) {
			throw new IllegalArgumentException("Quiz " + name + "already exists");
		}
		
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(QUIZ_NAME, name);
		row.put(CREATOR, creator.getUserName());
		row.put(DESCRIPTION, description);
		row.put(DATE_CREATED, date);
		row.put(IS_RANDOM, isRandom);
		row.put(IS_ONE_PAGE, isOnePage);
		row.put(IS_IMMEDIATE, isImmediate);
		Database.addRow(QUIZZES, row);
	}
	
	
	/**
	 * Provides an interface to interact with the Quizzes table of the database.
	 * Expects the passed quiz name to already exist in the database. Throws an exception
	 * if it doesn't. 
	 * @param userName
	 */
	protected Quiz(String name) {
		Util.validateString(name);
		
		// Make sure quiz name exists in DB.
		if (Database.getValues(QUIZZES, QUIZ_NAME, name, QUIZ_NAME) == null) {
			throw new IllegalArgumentException("Cannot find quiz " + name);
		}
		this.name = name;
	}
	
	
	/**
	 * Removes the quiz from the database and sets the name to null
	 * so it can no longer be used.
	 */
	public void removeQuiz() {
		int removed = Database.removeRows(QUIZZES, QUIZ_NAME, name);
		
		// Only 1 row should be removed.
		if (removed != 1) {
			throw new RuntimeException("Problem removing rows. Removed " + removed + " rows");
		}
		
		// Remove questions for this quiz.
		for (String tableName : QUESTION_TYPES) {
			Database.removeRows(tableName, QUIZ_NAME, name);
		}
		
		name = null;
	}
	
	
	/**
	 * @return the quiz name for this Quiz.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Returns all question Objects.
	 */
	public List<Question> getQuestions() {
		List<Question> result = new ArrayList<Question>();
		Map<String, Question> questions = new LinkedHashMap<String, Question>();
		
		for (String questionType : QUESTION_TYPES) {
			// Get all rows for each question table for this quiz.
			List<Map<String, Object>> rows = Database.getRows(questionType, QUIZ_NAME, name);
			
			// Move on to next table if there are no questions here.
			if (rows == null) continue;
			
			for (Map<String, Object> row : rows) {
				String question = (String) row.get(QUESTION);
				
				if (questions.containsKey(question)) {
					addToQuestion(questions.get(question), row);
					
				} else {
					Question newQuestion = questionFactory(questionType, row);
					questions.put(question, newQuestion);
				}
			}
		}
		
		for (String question : questions.keySet()) {
			result.add(questions.get(question));
		}
		
		if (result.size() == 0) return null;
		return result;
	}
	
	
	/**
	 * Returns all the current questions for this quiz in string form.
	 * See getQuestions to get the Question Objects.
	 */
	public Set<String> getQuestionsAsStrings() {
		Set<String> result = new HashSet<String>();
		
		for (String tableName : QUESTION_TYPES) {
			List<Object> questions = Database.getValues(tableName, QUIZ_NAME, name, QUESTION);
			for (Object question : questions) {
				String cur = (String) question;
				result.add(cur);
			}
		}
		return result;
	}
	
	
	/**
	 * Adds the passed question object to the database.
	 * @param question
	 */
	public void addQuestion(Question question) {
		Util.validateObject(question);
		
		// No duplicate questions.
		if (getQuestionsAsStrings().contains(question.getQuestion())) {
			throw new IllegalArgumentException(question.getQuestion() + " already exists in " + name);
		}
		
		if (question instanceof Response) addResponse((Response) question);
		else if (question instanceof FillBlank) addFillBlank((FillBlank) question);
		else if (question instanceof MultipleChoice) addMultipleChoice((MultipleChoice) question);
		else if (question instanceof Picture) addPicture((Picture) question);
		else if (question instanceof MultiResponse) addMultiResponse((MultiResponse) question);
		else if (question instanceof Matching) addMatching((Matching) question);
		else {
			throw new IllegalArgumentException("Passed question is invalid");
		}
	}
	
	
	/**
	 * Edits a question from the old state to the new state.
	 * Takes the old state of a question before it was modified and the new state after being
	 * modified. Looks for the old state in the database and replaces it with the new one.
	 * @param oldQuestion the question object before modification
	 * @param newQuestion the question object after modification
	 */
	public void editQuestion(Question oldQuestion, Question newQuestion) {
		Util.validateObject(oldQuestion);
		Util.validateObject(newQuestion);
		
		// Ensure question belongs to this quiz.
		if (!oldQuestion.getQuizName().equals(name)) {
			throw new IllegalArgumentException("The question is not in this quiz.");
		}
		
		// Check type consistency
		if (oldQuestion.getClass() != newQuestion.getClass()) {
			throw new IllegalArgumentException("Both questions have to be the same type");
		}
		
		// Quiz name cannot be modified in a question.
		if (!oldQuestion.getQuizName().equals(newQuestion.getQuizName())) {
			throw new IllegalArgumentException("The quiz name in a question cannot be modified");
		}
		
		// If the question prompt was modified, ensure it is not duplicate.
		if (!oldQuestion.getQuestion().equals(newQuestion.getQuestion())) {
			if (getQuestionsAsStrings().contains(newQuestion.getQuestion())) {
				throw new IllegalArgumentException("The question already exists in" 
						+ newQuestion.getQuizName());
			}
		}
		
		// Remove old and add new.
		removeQuestion(oldQuestion);
		addQuestion(newQuestion);
	}
	
	
	/**
	 * Removes the passed question object from the Quiz.
	 */
	public void removeQuestion(Question question) {
		Util.validateObject(question);
		for (String tableName : QUESTION_TYPES) {
			Database.removeRows(tableName, QUIZ_NAME, name, QUESTION, question);
		}
	}
	
	
	/**
	 * Returns the top scorers for the quiz.
	 * @param numRecords number of top scorers to return. (all if numRecords = 0)
	 * @return a list of top scorers as Record Objects.
	 */
	public List<Record> getTopScorers(int numRecords) {
		if (numRecords < 0) {
			throw new IllegalArgumentException(numRecords + " cannot be less than 0");
		}
		
		List<Record> result = new ArrayList<Record>();
		List<Map<String, Object>> rows = Database.getSortedRows(HISTORY, QUIZ_NAME, 
				name, SCORE, true, ELAPSED_TIME, false);
		
		if (rows == null) return null;
		
		// Get all records.
		if (numRecords == 0) {
			for (Map<String, Object> row : rows) {
				result.add(new Record(
						(String) row.get(QUIZ_NAME), 
						AccountManager.getAccount((String) row.get(USERNAME)),
						(Double) row.get(SCORE), (String) row.get(DATE), 
						(String) row.get(ELAPSED_TIME)));
			}
			
		} else {
			for (int i = 0; i < Math.min(numRecords, rows.size()); i++) {
				Map<String, Object> row = rows.get(i);
				result.add(new Record(
						(String) row.get(QUIZ_NAME), 
						AccountManager.getAccount((String) row.get(USERNAME)),
						(Double) row.get(SCORE), (String) row.get(DATE), 
						(String) row.get(ELAPSED_TIME)));
			}
		}
		return result;
	}
	
	
	
	//--------------------------- Helper Methods -------------------------------//
	
	
	// Adds a Response question to the database.
	private void addResponse(Response response) {
		// Add a row for every answer in the question.
		for (String answer : response.getAnswers()) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(QUIZ_NAME, name);
			row.put(QUESTION, response.getQuestion());
			row.put(ANSWER, answer);
			
			// If row already exists, do nothing.
			if (Database.getRows(RESPONSE, row) != null) {
				return;
			}
			
			Database.addRow(RESPONSE, row);
		}
	}
	
	
	// Adds a FillBlank question to the database.
	private void addFillBlank(FillBlank fillBlank) {
		// Iterate over every blank.
		for (String blank : fillBlank.getBlanksAndAnswers().keySet()) {
			// Iterate over every answer per each blank.
			for (String answer : fillBlank.getBlanksAndAnswers().get(blank)) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put(QUIZ_NAME, name);
				row.put(QUESTION, fillBlank.getQuestion());
				row.put(BLANK, blank);
				row.put(ANSWER, answer);
				
				// If row already exists, do nothing.
				if (Database.getRows(FILL_BLANK, row) != null) {
					return;
				}
				
				Database.addRow(FILL_BLANK, row);
			}
		}
	}
	
	
	// Adds a MultipleChoice question to the database.
	private void addMultipleChoice(MultipleChoice multipleChoice) {
		// Add a row for every answer in the question.
		for (String option : multipleChoice.getOptions().keySet()) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(QUIZ_NAME, name);
			row.put(QUESTION, multipleChoice.getQuestion());
			row.put(OPTION, option);
			row.put(IS_ANSWER, multipleChoice.getOptions().get(option));
			
			// If row already exists, do nothing.
			if (Database.getRows(MULTIPLE_CHOICE, row) != null) {
				return;
			}
			
			Database.addRow(MULTIPLE_CHOICE, row);
		}
	}


	// Adds a Picture question to the database.
	private void addPicture(Picture picture) {
		// Add a row for every answer in the question.
		for (String answer : picture.getAnswers()) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(QUIZ_NAME, name);
			row.put(QUESTION, picture.getQuestion());
			row.put(PICTURE_URL, picture.getPictureUrl());
			row.put(ANSWER, answer);
			
			// If row already exists, do nothing.
			if (Database.getRows(PICTURE, row) != null) {
				return;
			}
			
			Database.addRow(PICTURE, row);
		}
	}


	// Adds a MultiResponse question to the database.
	private void addMultiResponse(MultiResponse multiResponse) {
		// Add a row for every answer in the question.
		for (int i = 0; i < multiResponse.getAnswers().size(); i++) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(QUIZ_NAME, name);
			row.put(QUESTION, multiResponse.getQuestion());
			row.put(ANSWER, multiResponse.getAnswers().get(i));
			row.put(IS_ORDERED, multiResponse.isOrdered());
			row.put(ORDER, i);
			
			// If row already exists, do nothing.
			if (Database.getRows(MULTI_RESPONSE, row) != null) {
				return;
			}
			
			Database.addRow(MULTI_RESPONSE, row);
		}
	}


	// Adds a Matching question to the database.
	private void addMatching(Matching matching) {
		// Add a row for every answer in the question.
		for (String left : matching.getMatches().keySet()) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(QUIZ_NAME, name);
			row.put(QUESTION, matching.getQuestion());
			row.put(LEFT, left);
			row.put(RIGHT, matching.getMatches().get(left));
			
			// If row already exists, do nothing.
			if (Database.getRows(MATCHING, row) != null) {
				return;
			}
			
			Database.addRow(MATCHING, row);
		}
	}
	
	
	/*
	 * Given a question type and a database row in the form of a Map<String, Object>,
	 * returns a Question Object.
	 */
	private Question questionFactory(String questionType, Map<String, Object> row) {
		Util.validateString(questionType);
		Util.validateObject(row);
		
		String question = (String) row.get(QUESTION);
		
		if (questionType.equals(RESPONSE)) {
			Set<String> answers = new HashSet<String>();
			answers.add((String) row.get(ANSWER));
			return new Response(name, question, answers);
			
		} else if (questionType.equals(FILL_BLANK)) {
			Set<String> answers = new HashSet<String>();
			answers.add((String) row.get(ANSWER));
			Map<String, Set<String>> blanksAndAnswers = new HashMap<String, Set<String>>();
			blanksAndAnswers.put((String) row.get(BLANK), answers);
			return new FillBlank(name, question, blanksAndAnswers);
			
		} else if (questionType.equals(MULTIPLE_CHOICE)) {
			Map<String, Boolean> options = new HashMap<String, Boolean>();
			options.put((String) row.get(OPTION), (Boolean) row.get(IS_ANSWER));
			return new MultipleChoice(name, question, options);
		
		} else if (questionType.equals(PICTURE)) {
			Set<String> answers = new HashSet<String>();
			answers.add((String) row.get(ANSWER));
			return new Picture(name, question, (String) row.get(PICTURE_URL), answers);
			
		} else if (questionType.equals(MULTI_RESPONSE)) {
			TreeMap<Integer, String> answers = new TreeMap<Integer, String>();
			answers.put((Integer) row.get(ORDER), (String) row.get(ANSWER));
			boolean isOrdered = (Boolean) row.get(IS_ORDERED);
			return new MultiResponse(name, question, answers, isOrdered);
			
		} else if (questionType.equals(MATCHING)) {
			Map<String, String> matches = new HashMap<String, String>();
			matches.put((String) row.get(LEFT), (String) row.get(RIGHT));
			return new Matching(name, question, matches);
		}
		
		throw new IllegalArgumentException(questionType + " is not a valid question type.");
	}
	
	
	/*
	 * Adds the passed row to the passed Question object.
	 */
	private void addToQuestion(Question question, Map<String, Object> row) {
		if (question instanceof Response) {
			Response response = (Response) question;
			response.addAnswer((String) row.get(ANSWER));
		 	
		} else if (question instanceof FillBlank) {
			FillBlank fillBlank = (FillBlank) question;
			fillBlank.addBlank((String) row.get(BLANK), (String) row.get(ANSWER));
			
		} else if (question instanceof MultipleChoice) {
			MultipleChoice multChoice = (MultipleChoice) question;
			multChoice.addOption((String) row.get(OPTION), (Boolean) row.get(IS_ANSWER));
			
		} else if (question instanceof Picture) {
			Picture picture = (Picture) question;
			picture.addAnswer((String) row.get(ANSWER));
			
		} else if (question instanceof MultiResponse) {
			MultiResponse multResponse = (MultiResponse) question;
			multResponse.addAnswer((Integer) row.get(ORDER), (String) row.get(ANSWER));
			
		} else if (question instanceof Matching) {
			Matching matching = (Matching) question;
			matching.addMatch((String) row.get(LEFT), (String) row.get(RIGHT));
		
		} else {
			throw new IllegalArgumentException("Invalid question object.");
		}
	}
	
}
