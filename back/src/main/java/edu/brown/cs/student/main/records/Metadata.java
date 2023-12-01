package edu.brown.cs.student.main.records;

import java.util.List;
import java.util.Map;

public record Metadata(String type, List<String> questions, String rawResponse, String answer,
                       Map<String, String> keywordMap) {
  public Metadata(List<String> questions, String rawResponse, String answer,
      Map<String, String> keywordMap){
    this("metadata", questions, rawResponse, answer, keywordMap);
  }
}
