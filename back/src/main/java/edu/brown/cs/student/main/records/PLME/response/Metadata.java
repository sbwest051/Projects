package edu.brown.cs.student.main.records.PLME.response;

import java.util.Map;

/**
 * Part of the response object from MetadataHandler (MetadataTable -> File).
 * @param result either "success" or "error" to denote whether there was a problem retrieving any
 *              of the data.
 * @param rawResponse the raw response from the PDF source for the specific file for the query.
 * @param data A map of keywords to an array of doubles. Values: [Reliability score, Relevance
 *             Score].
 * @param message Error message if there was a problem retrieving metadata.
 */
public record Metadata(String result, String rawResponse, Map<String, Double[]> data,
                       String message) {

}
