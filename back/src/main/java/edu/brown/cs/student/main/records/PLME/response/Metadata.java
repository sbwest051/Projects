package edu.brown.cs.student.main.records.PLME.response;

import java.util.List;
import java.util.Map;

public record Metadata(String result, String rawResponse, Map<String, Double[]> data,
                       String message) {

}
