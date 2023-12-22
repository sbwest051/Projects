package edu.brown.cs.student.main.records.ChatPDF;

/**
 * ChatPDFRequest data structure containing the role of a message and the content.
 * @param role Role will either be "user" or "assistant".
 * @param content Content of the message.
 */
public record Message(String role, String content) {

}
