package edu.brown.cs.student.main;

import java.util.List;

public record geometry(String type, List<List<List<List<Double>>>> coordinates) {

  public geometry(List<List<List<List<Double>>>> coordinates) {
    this("MultiPolygon", coordinates);
  }
}
