package edu.brown.cs.student.main.records.PLME.response;

import java.util.List;

public record File(String type, List<Metadata> metadataColumns, Double minRelScore) {
  public File(List<Metadata> metadataColumns){
    this("file", metadataColumns, 0.0);
  }

  public File(List<Metadata> metadataColumns, Double minRelScore){
    this("file", metadataColumns, minRelScore);
  }
}
