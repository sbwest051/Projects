package edu.brown.cs.student.main.plme.sources;

import edu.brown.cs.student.main.exceptions.DatasourceException;

public interface PDFSource {
  String getContent(String sourceId, String question) throws DatasourceException;
  String addURL(String url) throws DatasourceException;
  String addFile(String filepath) throws DatasourceException;
}
