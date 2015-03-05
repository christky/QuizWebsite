package com.quizzes;

import java.util.Map;

import com.util.Util;

public class Matching extends Question {
	
	private int id;
	private Map<String, String> matches;

	/**
	 * Constructor
	 * @param quizName
	 * @param question
	 * @param id unique indentifier for this Matching question.
	 * each matching question within the same quiz should have one.
	 * @param matches maps pairs of matching items.
	 */
	public Matching(String quizName, String question, int id, Map<String, String> matches) {
		super(quizName, question);
		
		Util.validateObject(matches);
		this.id = id;
		this.matches = matches;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the matches
	 */
	public Map<String, String> getMatches() {
		return matches;
	}

	/**
	 * @param matches the matches to set
	 */
	public void setMatches(Map<String, String> matches) {
		Util.validateObject(matches);
		this.matches = matches;
	}
	
	
	/**
	 * Adds the passed match to the question.
	 * @param left item on one side
	 * @param right item on the other
	 */
	public void addMatch(String left, String right) {
		Util.validateString(left);
		Util.validateString(right);
		matches.put(left, right);
	}
	
	
	/**
	 * Removes the passed match and its counterpart from the question.
	 * @param match
	 */
	public void removeMatch(String match) {
		Util.validateString(match);
		if (!matches.containsKey(match)) {
			throw new IllegalArgumentException(match + " is not currently in this question");
		}
		matches.remove(match);
	}

}
