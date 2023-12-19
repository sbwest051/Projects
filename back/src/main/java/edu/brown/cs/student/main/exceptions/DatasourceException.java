package edu.brown.cs.student.main.exceptions;

/**
 * A class extending Throwable which is meant to be thrown when there is an issue with accessing the datasource.
 */
public class DatasourceException extends Exception {
  private final Throwable cause;
  public DatasourceException(String message) {
    super(message);
    this.cause = null;
  }

  public DatasourceException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}
