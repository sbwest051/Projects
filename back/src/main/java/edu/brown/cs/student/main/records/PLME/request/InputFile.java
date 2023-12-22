package edu.brown.cs.student.main.records.PLME.request;

/**
 * Data structure as part of the PLMEInput containing all input information for a file. Title
 * must not be null, but only either filepath or url is needed.
 * @param title
 * @param filepath
 * @param url
 */
public record InputFile(String title, String filepath, String url) {}
