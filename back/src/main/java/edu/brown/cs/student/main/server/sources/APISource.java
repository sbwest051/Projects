package edu.brown.cs.student.main.server.sources;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;

public interface APISource {
  void setStateCodes() throws DatasourceException;
  String[] getCountyBroadband(String state, String county)
      throws DatasourceException, BadRequestException;
}
