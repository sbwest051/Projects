package edu.brown.cs.student.main.records.PLME.response;

import java.util.List;

public record File(String result, String filepath, String url, String title,
                   List<Metadata> metadata, String message) {

}
