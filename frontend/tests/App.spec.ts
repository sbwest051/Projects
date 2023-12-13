import { test, expect } from "@playwright/test";

/**
The general shapes of tests in Playwright Test are:
1. Navigate to a URL
2. Interact with the page
3. Assert something about the page against your expectations
*/

/**
 * These tests are to make sure that upon opening the server we have the expected functionality
 * Also tests the way that mode starts out as well as the way that
 */

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
});

test("on page load, I see an input bar", async ({ page }) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await expect(page.getByLabel("Command input")).toBeVisible();
});

test("after I type into the input box, its text changes", async ({ page }) => {
  // Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});

test("on page load, i see a button", async ({ page }) => {
  await expect(page.getByRole("button")).toBeVisible();
});

test("On page load, history is empty", async ({ page }) => {
  //await expect(page.getByLabel("Command 0")).not.toBeVisible();
  // const history = await page.$eval(".repl-history", (element) => element);
  // expect(history).toBeEmpty;
  // await expect(page.getByLabel("Commanded 0")).toHaveText("");
  // await expect(page.getByLabel("Output 0")).toHaveText("");
  const history = await page.$(".repl-history"); // Select the .repl-history element
  expect(history).not.toBeNull(); // Check if the element exists
  expect(await history?.textContent()).toBe(""); // Check if the element's content is empty
});

test("after I click the button, my command gets pushed", async ({ page }) => {
  const firstHistory = await page.$eval(
    ".repl-history",
    (element) => element.textContent
  );
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");
  await page.getByLabel("button").click();

  // Check if the command is pushed to the history
  const updatedHistory = await page.$eval(
    ".repl-history",
    (element) => element.textContent
  );
  expect(updatedHistory).toContain(
    "Output:error-invalid-command:Please enter your command then press submit"
  );
});

test('submiting "mode" changes history output', async ({ page }) => {
  // Navigate to the page

  const mode_input = `Command: mode`;

  // Get the initial history output
  const initialHistory = await page.$eval(
    ".repl-history",
    (element) => element.textContent
  );

  // Type "mode" into the input box and click button
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByLabel("button").click();

  //await page.waitForTimeout(1000);
  //May not be necessary because of await

  // Get the updated history output based off of the css tag
  const updatedHistory = await page.$eval(
    ".repl-history",
    (element) => element.textContent
  );
  // Assert that the history has changed as expected and that we are in verbose mode
  await expect(page.getByLabel("Commanded 0")).toHaveText(mode_input);
  await expect(page.getByLabel("singleCell 0")).toHaveText("Mode: verbose");
  expect(updatedHistory).not.toEqual(initialHistory);

  // make sure we can switch back into simple

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByLabel("button").click();
  // check that our first history output returned unmutated and that the command disapears
  await expect(page.getByLabel("Commanded 0")).not.toBeVisible();
  await expect(page.getByLabel("singleCell 0")).toHaveText("Mode: verbose");


  //check that new command is returned in simple format as well

  await expect(page.getByLabel("Commanded 1")).not.toBeVisible();
  await expect(page.getByLabel("singleCell 1")).toHaveText("Mode: simple");
});
