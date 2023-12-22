package edu.brown.cs.student.main.records.PLME.request;

import edu.brown.cs.student.main.records.PLME.MDCInput;
import java.util.List;

/**
 * Data structure that will collect all the deserialized information via the backend server.
 * @param filepath for if a csv containing all the files is used.
 * @param files for if a list of manually inputted files is used.
 * @param columns containing headers, questions, and keyword lists/maps.
 */
public record PLMEInput(String filepath, List<InputFile> files, List<MDCInput> columns) {
}
