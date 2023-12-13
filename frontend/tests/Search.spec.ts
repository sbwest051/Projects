import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
});

// Correctness tests

test("Search returns a value when searching by columnIndex", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");
  await page.getByLabel("button").click();
  const mock_output = `success`;
  // Check that loading is successful
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);

  // Search CSV
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnIndex=2 aaa");

  await page.getByLabel("button").click();

  // Check that Searching is successful

  await expect(page.getByLabel("row 0, cell 0")).toHaveText("CS Student 398");
  await expect(page.getByLabel("row 0, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("row 0, cell 2")).toHaveText("20");
  await expect(page.getByLabel("row 0, cell 3")).toHaveText("Stressed");
  await expect(page.getByLabel("row 1, cell 0")).toHaveText("CS Student 459");
  await expect(page.getByLabel("row 1, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("row 1, cell 2")).toHaveText("21");
  await expect(page.getByLabel("row 1, cell 3")).toHaveText("Relaxing");
  await expect(page.getByLabel("row 2, cell 0")).toHaveText("CS Student 999");
  await expect(page.getByLabel("row 2, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("row 2, cell 2")).toHaveText("19");
  await expect(page.getByLabel("row 2, cell 3")).toHaveText("Cramming");
});

// Searching by columnIndex instead

test("Search returns a value when when searching by columnName", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");

  await page.getByLabel("button").click();
  const mock_output = `success`;
  // Check that loading is successful
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);

  // Search CSV
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnName=velocity 27");

  await page.getByLabel("button").click();

  // Check that Searching is successful

  await expect(page.getByLabel("row 0, cell 0")).toHaveText("Unlaiden Swallow");
  await expect(page.getByLabel("row 0, cell 1")).toHaveText("129");
  await expect(page.getByLabel("row 0, cell 2")).toHaveText("27");
  await expect(page.getByLabel("row 0, cell 3")).toHaveText("Birb");
  await expect(page.getByLabel("row 1, cell 0")).toHaveText("");
  await expect(page.getByLabel("row 1, cell 1")).toHaveText("67");
  await expect(page.getByLabel("row 1, cell 2")).toHaveText("27");
  await expect(page.getByLabel("row 1, cell 3")).toHaveText("Untitled");
});

// Chained command tests

test("Searching works when searching by index and then by name", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");

  await page.getByLabel("button").click();
  const mock_output = `success`;
  // Check that loading is successful
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);

  // Search CSV
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnName=velocity 27");

  await page.getByLabel("button").click();

  // Check that Searching is successful

  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText(
    "Unlaiden Swallow"
  );
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("129");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("27");
  await expect(page.getByLabel("table 1, row 0, cell 3")).toHaveText("Birb");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("67");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("27");
  await expect(page.getByLabel("table 1, row 1, cell 3")).toHaveText(
    "Untitled"
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnIndex=2 aaa");

  await page.getByLabel("button").click();

  await expect(page.getByLabel("table 2, row 0, cell 0")).toHaveText(
    "CS Student 398"
  );
  await expect(page.getByLabel("table 2, row 0, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 2, row 0, cell 2")).toHaveText("20");
  await expect(page.getByLabel("table 2, row 0, cell 3")).toHaveText(
    "Stressed"
  );
  await expect(page.getByLabel("table 2, row 1, cell 0")).toHaveText(
    "CS Student 459"
  );
  await expect(page.getByLabel("table 2, row 1, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 2, row 1, cell 2")).toHaveText("21");
  await expect(page.getByLabel("table 2, row 1, cell 3")).toHaveText(
    "Relaxing"
  );
  await expect(page.getByLabel("table 2, row 2, cell 0")).toHaveText(
    "CS Student 999"
  );
  await expect(page.getByLabel("table 2, row 2, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 2, row 2, cell 2")).toHaveText("19");
  await expect(page.getByLabel("table 2, row 2, cell 3")).toHaveText(
    "Cramming"
  );
});

test("Searching --> bad loading --> Searching", async ({ page }) => {
  await page.getByLabel("Command input").fill("load_file data/SimpleCSV.csv");

  await page.getByLabel("button").click();
  const mock_output = `success`;
  // Check that loading is successful
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);

  // Search CSV
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnName=velocity 27");

  await page.getByLabel("button").click();

  // Check that Searching is successful

  await expect(page.getByLabel("table 1, row 0, cell 0")).toHaveText(
    "Unlaiden Swallow"
  );
  await expect(page.getByLabel("table 1, row 0, cell 1")).toHaveText("129");
  await expect(page.getByLabel("table 1, row 0, cell 2")).toHaveText("27");
  await expect(page.getByLabel("table 1, row 0, cell 3")).toHaveText("Birb");
  await expect(page.getByLabel("table 1, row 1, cell 0")).toHaveText("");
  await expect(page.getByLabel("table 1, row 1, cell 1")).toHaveText("67");
  await expect(page.getByLabel("table 1, row 1, cell 2")).toHaveText("27");
  await expect(page.getByLabel("table 1, row 1, cell 3")).toHaveText(
    "Untitled"
  );

  // Then incorrectly load another CSV
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/EpmtyCVS.csv");
  await page.getByLabel("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search columnIndex=2 aaa");
  await page.getByLabel("button").click();

  // And it should still work on the old CSV

  await expect(page.getByLabel("table 3, row 0, cell 0")).toHaveText(
    "CS Student 398"
  );
  await expect(page.getByLabel("table 3, row 0, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 3, row 0, cell 2")).toHaveText("20");
  await expect(page.getByLabel("table 3, row 0, cell 3")).toHaveText(
    "Stressed"
  );
  await expect(page.getByLabel("table 3, row 1, cell 0")).toHaveText(
    "CS Student 459"
  );
  await expect(page.getByLabel("table 3, row 1, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 3, row 1, cell 2")).toHaveText("21");
  await expect(page.getByLabel("table 3, row 1, cell 3")).toHaveText(
    "Relaxing"
  );
  await expect(page.getByLabel("table 3, row 2, cell 0")).toHaveText(
    "CS Student 999"
  );
  await expect(page.getByLabel("table 3, row 2, cell 1")).toHaveText("aaa");
  await expect(page.getByLabel("table 3, row 2, cell 2")).toHaveText("19");
  await expect(page.getByLabel("table 3, row 2, cell 3")).toHaveText(
    "Cramming"
  );
});
