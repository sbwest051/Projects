package edu.brown.cs.student.main.records.maps;

import java.util.Map;

public record properties(String state, String city, String name, String holc_id, String holc_grade,
                         Integer neighborhood_id, Map<String, String> area_description_data) {

  public properties(String state, String city, String name, String holc_id, String holc_grade,
      Integer neighborhood_id, Map<String, String> area_description_data, String i) {
    this(state, city, name, holc_id, holc_grade, neighborhood_id, area_description_data);
  }
}
