package edu.brown.cs.student.main.records.PLME.request;

import java.util.List;
import java.util.Map;

public record MDCInput(String title, String question, List<String> keywordList, Map<String,
    List<String>> keywordMap) {}
