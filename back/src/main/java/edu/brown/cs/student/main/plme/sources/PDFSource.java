package edu.brown.cs.student.main.plme.sources;

import edu.brown.cs.student.main.exceptions.DatasourceException;

/**
 * Interface for the MetadataHandler to take in. Will require the taking in of URLs and Files and
 * questions.
 */
public interface PDFSource {
  String getContent(String sourceId, String question) throws DatasourceException;
  String addURL(String url) throws DatasourceException;
  String addFile(String filepath) throws DatasourceException;
}
