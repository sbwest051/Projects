package edu.brown.cs.student.main.records.PLME;

import java.util.List;
import java.util.Map;

/**
 * Part of both the request (PLMEInput) and response (MetadataTable) records. Contains all the
 * information for a metadata column. Should only contain either keywordList or keywordMap.
 * @param title Header for the column.
 * @param question question asked to the PDF source.
 * @param keywordList list of strings containing possible keywords.
 * @param keywordMap Map to list of strings containing possible keywords.
 */
public record MDCInput(String title, String question, List<String> keywordList, Map<String,
    List<String>> keywordMap) {}
