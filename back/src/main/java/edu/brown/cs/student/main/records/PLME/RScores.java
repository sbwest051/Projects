package edu.brown.cs.student.main.records.PLME;

import java.util.Map;

public record RScores(String rvResult, Map<String, Double> reliability, Map<String, Double> tfList,
                      Map<String, Map<String, Double>> tfMap, String message) {

}
