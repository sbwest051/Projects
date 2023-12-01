package edu.brown.cs.student.main.records.maps;


import java.util.List;

public record FeatureCollection(String type, List<Feature> features) {
  public FeatureCollection(List<Feature> features){
    this("FeatureCollection", features);
  }
}

