package edu.brown.cs.student.main;

import edu.brown.cs.student.main.server.exceptions.DatasourceException;

public class MockPDFSource implements PDFSource {
  private String sourceId;
  public MockPDFSource(){
    this.sourceId = "default";
  }
  @Override
  public String addURL(String url) throws DatasourceException {
    this.sourceId = url;
    return this.sourceId;
  }

  @Override
  public String addFile(String filepath) throws DatasourceException {
    this.sourceId = filepath;
    return this.sourceId;
  }

  @Override
  public String getContent(String sourceId, String question) throws DatasourceException {
    if (sourceId.equals("test")){
      switch (question) {
        case "foop" -> {
          return "yes";
        }
        case "yay" -> {
          return "no";
        }
      }
    }
    return null;
  }

}
