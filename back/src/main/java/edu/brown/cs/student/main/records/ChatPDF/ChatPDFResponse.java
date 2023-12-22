package edu.brown.cs.student.main.records.ChatPDF;

import java.util.List;

/**
 * Data structure containing the content of a ChatPDFResponse to be converted into from the json
 * body.
 * @param sourceId for retrieving sourceID for addURL and addFile.
 * @param content for retrieving the answer to a question.
 * @param references for retreiving the list of references in the answer.
 */
public record ChatPDFResponse(String sourceId, String content, List<reference> references) {

}
