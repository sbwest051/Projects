import { test, expect } from "@playwright/test";

/**
 * This is carried out before every test that is run. It is responsible for
 * loading the page. By doing this in the beforeEach, we reduce redundency.
 */
test.beforeEach(async ({ page }, testInfo) => {
  await page.goto("http://localhost:8001/");
});

/**
 * This tests that the popup is visible on the page.
 */

test("popup appears", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
});

/**
 * This tests that we can exit from the popup and go to the loading page.
 */
test("Adding files page", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();
  await expect(page.getByText("Instructions")).not.toBeVisible();
});

/**
 * This tests that we can actually submit features.
 */
test("actually submitting", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("PDF Title");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("/data/allergy.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page.getByPlaceholder("Enter title of query here!").fill("Query Title");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("Example Question?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page.getByPlaceholder("Enter keyword list or map").fill("keyword");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const titleValue = "PDF Title";
  await expect(page.getByText(titleValue)).toBeVisible;
  // const queryTitle = "Query Title";
  // await expect(page.getByText(queryTitle)).toBeVisible;
  const resultValue = "success";
  await expect(page.getByText(resultValue)).toBeVisible;
});

/**
 * This tests that an empty submission will get an error alert
 */
test("empty submission", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();
  const [dialog] = await Promise.all([
    page.waitForEvent("dialog"),
    page.getByText("Submit").click(),
  ]);
  expect(dialog.type()).toBe("alert");
  expect(dialog.message()).toContain("Input is missing one or more parameters");
});

/**
 * This tests that submitting with a false filepath with return a table with an error
 */
test("submitting with invalid filepath", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("PDF Title");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/invalid.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page.getByPlaceholder("Enter title of query here!").fill("Query Title");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("Example Question?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page.getByPlaceholder("Enter keyword list or map").fill("keyword");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const titleValue = "PDF Title";
  await expect(page.getByText(titleValue)).toBeVisible;
  // const queryTitle = "Query Title";
  // await expect(page.getByText(queryTitle)).toBeVisible;
  const resultValue = "success";
  await expect(page.getByText(resultValue)).toBeVisible;
  const errorMSG = "File must be in the data folder.";
  await expect(page.getByText(errorMSG)).toBeVisible;
});

/**
 * This tests that a submission attempt without a query will return an alert
 */
test("submitting without query", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("PDF Title");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");
  const [dialog] = await Promise.all([
    page.waitForEvent("dialog"),
    page.getByText("Submit").click(),
  ]);
  expect(dialog.type()).toBe("alert");
  expect(dialog.message()).toContain("Input is missing one or more parameters");
});

/**
 * This tests that a submission attempt without keywords will return an alert
 */
test("submitting without keywords", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("PDF Title");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page.getByPlaceholder("Enter title of query here!").fill("Query Title");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("Example Question?");
  const [dialog] = await Promise.all([
    page.waitForEvent("dialog"),
    page.getByText("Submit").click(),
  ]);
  expect(dialog.type()).toBe("alert");
  expect(dialog.message()).toContain("Input is missing one or more parameters");
});

/**
 * This tests an actual submission with 1 query and 1 pdf
 */
test("actual submission", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("Allergy");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page
    .getByPlaceholder("Enter title of query here!")
    .fill("What allergy");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("What allergy was tested?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page
    .getByPlaceholder("Enter keyword list or map")
    .fill("Mal d 1, Pru du 6, Peanut, BLEWFUIW");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const resultValue = "success";
  await expect(page.getByText(resultValue)).toBeVisible;
  const titleValue = "Allergy";
  await expect(page.getByText(titleValue)).toBeVisible;
  // const queryTitle = "What allergy";
  // await expect(page.getByText(queryTitle)).toBeVisible;
  const errorMSG = "File must be in the data folder.";
  await expect(page.getByText(errorMSG)).toBeVisible;
});

/**
 * This tests a submission with two files
 */
test("two files!", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("Allergy");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");

  await page.getByText("add new pdf").click();
  await page.getByPlaceholder("Enter title of PDF").nth(1).click();
  await page.getByPlaceholder("Enter title of PDF").nth(1).fill("Allergy 2");
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(1)
    .click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(1)
    .fill("data/immunotherapy.pdf");

  await page.getByPlaceholder("Enter title of query here!").click();
  await page
    .getByPlaceholder("Enter title of query here!")
    .fill("What allergy");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("What allergy was tested?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page
    .getByPlaceholder("Enter keyword list or map")
    .fill("Mal d 1, Pru du 6, Peanut, BLEWFUIW");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const resultValue = "success";
  await expect(page.getByText(resultValue)).toBeVisible;
  const titleValue = "Allergy";
  await expect(page.getByText(titleValue)).toBeVisible;
  const titleValue2 = "Allergy 2";
  await expect(page.getByText(titleValue2)).toBeVisible;
  // const queryTitle = "What allergy";
  // await expect(page.getByText(queryTitle)).toBeVisible;
});

/**
 * This tests a submission with three files
 */
test("three files!", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("Allergy");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");

  await page.getByText("add new pdf").click();
  await page.getByPlaceholder("Enter title of PDF").nth(1).click();
  await page
    .getByPlaceholder("Enter title of PDF")
    .nth(1)
    .fill("Immunotherapy");
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(1)
    .click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(1)
    .fill("data/immunotherapy.pdf");

  await page.getByText("add new pdf").click();
  await page.getByPlaceholder("Enter title of PDF").nth(2).click();
  await page.getByPlaceholder("Enter title of PDF").nth(2).fill("Allergy 2");
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(2)
    .click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .nth(2)
    .fill("data/allergy2.pdf");

  await page.getByPlaceholder("Enter title of query here!").click();
  await page
    .getByPlaceholder("Enter title of query here!")
    .fill("What allergy");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("What allergy was tested?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page
    .getByPlaceholder("Enter keyword list or map")
    .fill("Mal d 1, Pru du 6, Peanut, BLEWFUIW");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const resultValue = "success";
  await expect(page.getByText(resultValue)).toBeVisible;
  const titleValue = "Allergy";
  await expect(page.getByText(titleValue)).toBeVisible;
  const titleValue2 = "Immunotherapy";
  await expect(page.getByText(titleValue2)).toBeVisible;
  const titleValue3 = "Allergy 2";
  await expect(page.getByText(titleValue3)).toBeVisible;
});

/**
 * This tests how we run a second file after the first
 */

test("next query", async ({ page }) => {
  await expect(page.getByText("Instructions")).toBeVisible();
  await expect(page.getByText("Enter query manually")).toBeVisible();
  await page.getByText("Enter query manually").click();
  await expect(
    page.getByText("Enter Query information in these boxes")
  ).toBeVisible();

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("Allergy");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page
    .getByPlaceholder("Enter title of query here!")
    .fill("What allergy");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("What allergy was tested?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page
    .getByPlaceholder("Enter keyword list or map")
    .fill("Mal d 1, Pru du 6, Peanut, BLEWFUIW");
  await page.getByText("Submit").click();
  await expect(page.getByRole("table")).toBeVisible();
  const titleValue2 = "Allergy";
  await expect(page.getByText(titleValue2)).toBeVisible;
  const query = "What allergy was tested?";
  await expect(page.getByText(query)).toBeVisible;

  await page.getByPlaceholder("Enter title of PDF").click();
  await page.getByPlaceholder("Enter title of PDF").fill("Allergy");
  await page.getByPlaceholder("Enter pdf link or filepath here!").click();
  await page
    .getByPlaceholder("Enter pdf link or filepath here!")
    .fill("data/allergy.pdf");
  await page.getByPlaceholder("Enter title of query here!").click();
  await page.getByPlaceholder("Enter title of query here!").fill("Author");
  await page.getByPlaceholder("Enter question of query here!").click();
  await page
    .getByPlaceholder("Enter question of query here!")
    .fill("Who was the author?");
  await page.getByPlaceholder("Enter keyword list or map").click();
  await page.getByPlaceholder("Enter keyword list or map").fill("Author IDK");
  await page.getByText("Submit").click();
  await expect(page.getByText(query)).not.toBeVisible;
  const query2 = "Who was the author?";
  await expect(page.getByText(query2)).toBeVisible;
});
