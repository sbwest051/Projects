package edu.brown.cs.student.main.records.PLME.response;

import java.util.List;

/**
 * Part of the response object from MetadataHandler (MetadataTable). Contains all the information
 * pertaining to a specific file.
 * @param result either "success" or "error" to denote whether all the information was
 *               successfully retrieved.
 * @param filepath if filepath was entered to retrieve the pdf.
 * @param url if url was entered to retrieve the pdf.
 * @param title Title given to the paper.
 * @param metadata List of Metadata containing all the collected metadata and corresponding scores.
 * @param message Error message if there was a problem retrieving data with this file.
 */
public record File(String result, String filepath, String url, String title,
                   List<Metadata> metadata, String message) {

}
