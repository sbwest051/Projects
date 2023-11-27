package edu.brown.cs.student.main;

public record Feature(String type, geometry geometry, properties properties) {

  public Feature(geometry geometry, properties properties) {
    this("Feature", geometry, properties);
  }
}
