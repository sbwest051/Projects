package edu.brown.cs.student.main.server.exceptions;

/**
 * A class extending Throwable which is meant to be thrown when there is an issue with the
 * request sent by the user.
 */
public class BadRequestException extends Exception {
  private final Throwable cause;

  public BadRequestException(String message) {
    super(message);
    this.cause = null;
  }

  public BadRequestException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}
