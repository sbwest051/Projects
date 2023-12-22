package edu.brown.cs.student.main.records.PLME;

import java.util.Map;

/**
 * Temporary data structure to house RScore data before every file has been run.
 * @param rvResult either "success" or "error" to denote whether or not relevance data was
 *                 retrieved successfully.
 * @param reliability Map from keyword to reliabilty score.
 * @param tfList Map from Keyword to term frequency if a List of strings was inputted.
 * @param tfMap Map from Key to Map of Keywords to Term frequency if a Map of string to list of
 *              strings was inputted.
 * @param message Error message if there was a problem retrieving rScore data.
 */
public record RScores(String rvResult, Map<String, Double> reliability, Map<String, Double> tfList,
                      Map<String, Map<String, Double>> tfMap, String message) {

}