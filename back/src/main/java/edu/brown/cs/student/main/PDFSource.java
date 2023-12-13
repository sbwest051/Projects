package edu.brown.cs.student.main;

import edu.brown.cs.student.main.records.ChatPDF.ChatPDFResponse;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;

public interface PDFSource {
  String getContent(String sourceId, String question) throws DatasourceException;
  String addURL(String url) throws DatasourceException;
  String addFile(String filepath) throws DatasourceException;
}
