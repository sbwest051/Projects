package edu.brown.cs.student.server;

import edu.brown.cs.student.main.server.sources.ACSAPISource;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestACSAPISource {

  @Test
  public void testCountyBroadband() throws DatasourceException, BadRequestException {
    ACSAPISource state = new ACSAPISource();
    state.setStateCodes();
    Assert.assertEquals("88.7", state.getCountyBroadband("California", "Yolo County")[1]);
    Assert.assertEquals("90.1", state.getCountyBroadband("Rhode Island", "Newport County")[1]);
  }

  @Test
  public void testBadInput() throws DatasourceException {
    ACSAPISource state = new ACSAPISource();
    state.setStateCodes();
    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("New", "Yolo County"));
    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("", "Yolo County"));
    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("california", "Yolo County"));

    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("California", "County"));
    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("California", "yolo"));
    Assert.assertThrows(
        BadRequestException.class, () -> state.getCountyBroadband("California", ""));
  }
}
