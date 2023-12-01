package edu.brown.cs.student.main.records;

import java.util.List;

public record ChatPDFResponse(String sourceId, String content, List<reference> references) {

}
