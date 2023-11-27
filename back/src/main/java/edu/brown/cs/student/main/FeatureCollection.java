package edu.brown.cs.student.main;


import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import java.util.List;
import java.util.Map;

public record FeatureCollection(String type, List<Feature> features) {
  public FeatureCollection(List<Feature> features){
    this("FeatureCollection", features);
  }
}

