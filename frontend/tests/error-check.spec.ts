import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
});

// Search tests

test("Directly calling search before loading results in an error", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("search data/SimpleCSV.csv");

  await page.getByLabel("button").click();
  const mock_output = `error-csv-not-loaded`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});

// View tests

test("Directly calling view before loading results in an error", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("view");

  await page.getByLabel("button").click();
  const mock_output = `error-csv-not-loaded`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});

// Load Tests

test("Loading a file outside of the data folder results in a security error", async ({
  page,
}) => {
  await page
    .getByLabel("Command input")
    .fill("load_file ..../C:/Windows/System32.csv");

  await page.getByLabel("button").click();
  const mock_output = `error-security`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});

test("Loading a file within the data folder that doesn't exist results in a specific error", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("load_file data/EpmtyCVS.csv");

  await page.getByLabel("button").click();
  const mock_output = `error-no-csv`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});

test("System is clear when requested values are not being mocked", async ({
  page,
}) => {
  await page.getByLabel("Command input").fill("load_file data/ten-star.csv");

  await page.getByLabel("button").click();
  const mock_output = `error-request-not-in-mock`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});

// Other

test("Random commands result in an error", async ({ page }) => {
  await page.getByLabel("Command input").fill("a");

  await page.getByLabel("button").click();
  const mock_output = `error-invalid-command:Please enter your command then press submit`;
  await expect(page.getByLabel("singleCell 0")).toHaveText(mock_output);
});
