import { test, expect } from "@playwright/test";
import { LoadViewCSV } from "../src/mockedJson";

//***/

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
});

test("Check that the data is updated properly when a valid file is input in verbose mode", async ({
  page,
}) => {
  // switching to verbose
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByLabel("button").click();

  //loading the data to be viewed
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");
  await page.getByLabel("button").click();

  //asking to view the data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  const filepath = "data/SimpleCSV.csv";

  //const data = LoadViewCSV.requests.get(filepath)?.response;

  await page.getByLabel("button").click();

  await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Get the updated history output

  // Assert that the history has changed as expected and file loads
  await expect(page.getByLabel("Commanded 1")).toBeVisible;
  await expect(page.getByLabel("Commanded 1")).toHaveText(
    `Command: load_file ${filepath}`
  );
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("singleCell 1")).toHaveText("success");

  // checking the output of view
  await expect(page.getByLabel("Commanded 2")).toBeVisible;
  await expect(page.getByLabel("Commanded 2")).toHaveText(`Command: view`);
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("row 0, cell 0")).toHaveText("a");
  await expect(page.getByLabel("row 0, cell 1")).toHaveText("b");
  await expect(page.getByLabel("row 0, cell 2")).toHaveText("c");
  await expect(page.getByLabel("row 1, cell 0")).toHaveText("d");
  await expect(page.getByLabel("row 1, cell 1")).toHaveText("e");
  await expect(page.getByLabel("row 1, cell 2")).toHaveText("f");
});

test("Check that the data is updated properly when a valid file is input in simple mode", async ({
  page,
}) => {
  //loading the data to be viewed
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");
  await page.getByLabel("button").click();

  //asking to view the data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  const filepath = "data/SimpleCSV.csv";

  await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Assert that the history has changed as expected and file loads
  await expect(page.getByLabel("Commanded 0")).not.toBeVisible;
  await expect(page.getByLabel("singleCell 0")).toHaveText("success");

  // checking the output of view
  await expect(page.getByLabel("Commanded 1")).not.toBeVisible;
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText("a");
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("b");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("c");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("d");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("e");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("f");
});

test("Check that the data is updated properly when two different csvs are loaded (simple mode)", async ({
  page,
}) => {
  //loading the data to be viewed
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");
  await page.getByLabel("button").click();

  //asking to view the data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  const filepath = "data/SimpleCSV.csv";

  await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Assert that the history has changed as expected and file loads
  await expect(page.getByLabel("Commanded 0")).not.toBeVisible;
  await expect(page.getByLabel("singleCell 0")).toHaveText("success");

  // checking the output of view
  await expect(page.getByLabel("Commanded 1")).not.toBeVisible;

  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText("a");
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("b");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("c");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("d");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("e");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("f");

  // asking to load a new set of data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV2.csv");
  await page.getByLabel("button").click();

  //making sure that it is a success
  await expect(page.getByLabel("Commanded 2")).not.toBeVisible;
  await expect(page.getByLabel("singleCell 2")).toHaveText("success");

  //asking to view the new data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  await expect(page.getByLabel("Commanded 3")).not.toBeVisible;
  await expect(page.getByLabel("table 3, row 0, cell 0")).toHaveText("adios");
  await expect(page.getByLabel("table 3, row 0, cell 1")).toHaveText("hola");
  await expect(page.getByLabel("table 3, row 1, cell 0")).toHaveText("bye");
  await expect(page.getByLabel("table 3, row 1, cell 1")).toHaveText("hi");

  // checking that when we view again we get a new table corresponding to the new data\
});

test("Check that the data is able to be loaded and viewed from empty csv", async ({
  page,
}) => {
  // switching to verbose
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByLabel("button").click();

  //loading the data to be viewed
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/EmptyCSV.csv");
  await page.getByLabel("button").click();

  //asking to view the data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  const filepath = "data/EmptyCSV.csv";

  //const data = LoadViewCSV.requests.get(filepath)?.response;

  await page.getByLabel("button").click();

  await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Get the updated history output

  // Assert that the history has changed as expected and file loads
  await expect(page.getByLabel("Commanded 1")).toBeVisible;
  await expect(page.getByLabel("Commanded 1")).toHaveText(
    `Command: load_file ${filepath}`
  );
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("singleCell 1")).toHaveText("success");

  // checking the output of view
  await expect(page.getByLabel("Commanded 2")).toBeVisible;
  await expect(page.getByLabel("Commanded 2")).toHaveText(`Command: view`);
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("body2")).toHaveText("");
});

test("Check that the data isn't mutated when the mode command is ran after its viewed", async ({
  page,
}) => {
  //loading the data to be viewed
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");
  await page.getByLabel("button").click();

  //asking to view the data
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("button").click();

  const filepath = "data/SimpleCSV.csv";

  await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Assert that the history has changed as expected and file loads
  await expect(page.getByLabel("Commanded 0")).not.toBeVisible;
  await expect(page.getByLabel("singleCell 0")).toHaveText("success");

  // checking the output of view
  await expect(page.getByLabel("Commanded 1")).not.toBeVisible;
  //TODO: Figure out how to attatch the table to the output
  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText("a");
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("b");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("c");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("d");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("e");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("f");

  //change to verbose mode
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByLabel("button").click();

  //assure that it actually changed bu the output stays the same

  await expect(page.getByLabel("Commanded 0")).toBeVisible;
  await expect(page.getByLabel("Commanded 0")).toHaveText(
    `Command: load_file ${filepath}`
  );
  await expect(page.getByLabel("singleCell 0")).toHaveText("success");
  await expect(page.getByLabel("Commanded 1")).toBeVisible;
  await expect(page.getByLabel("Commanded 1")).toHaveText(`Command: view`);
  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText("a");
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("b");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("c");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("d");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("e");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("f");
});
