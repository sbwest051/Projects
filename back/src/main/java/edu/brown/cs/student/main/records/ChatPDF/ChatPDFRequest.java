package edu.brown.cs.student.main.records.ChatPDF;

import java.util.List;

/**
 * Data structure for a ChatPDFRequest to be serialized to the ChatPDF API.
 * @param ReferenceSources boolean for whether the response should include sources.
 * @param sourceId string corresponding to the pdf loaded in.
 * @param messages list of Messages.
 */
public record ChatPDFRequest(Boolean ReferenceSources, String sourceId, List<Message> messages) {
}
