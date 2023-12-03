package edu.brown.cs.student.main.records.PLME.request;

import java.util.List;

public record PLMEInput(String filepath, List<InputFile> files, List<MDCInput> columns) {
}
