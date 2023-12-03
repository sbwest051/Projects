package edu.brown.cs.student.main.records.ChatPDF;

import edu.brown.cs.student.main.records.PLME.response.Message;
import java.util.List;

public record ChatPDFRequest(Boolean ReferenceSources, String sourceId, List<Message> messages) {
}
