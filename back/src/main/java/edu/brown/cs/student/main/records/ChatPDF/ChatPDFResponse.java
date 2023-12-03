package edu.brown.cs.student.main.records.ChatPDF;

import edu.brown.cs.student.main.records.PLME.response.reference;
import java.util.List;

public record ChatPDFResponse(String sourceId, String content, List<reference> references) {

}
