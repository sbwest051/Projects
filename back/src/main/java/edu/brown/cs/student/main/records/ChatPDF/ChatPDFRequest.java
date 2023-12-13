package edu.brown.cs.student.main.records.ChatPDF;

import java.util.List;

public record ChatPDFRequest(Boolean ReferenceSources, String sourceId, List<Message> messages) {
}
