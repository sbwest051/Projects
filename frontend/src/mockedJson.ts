// Takes in a command argument and returns the "loaded" CSV

interface BackendResponse {
  state: string;
  response: string[][];
}

// Takes in a command argument and returns the search. Handles searching across the entire
// CSV as well as rows and columns, as well as incorrect searches

export const LoadViewCSV = {
  requests: new Map<String, BackendResponse>(),
};

// First mocked request
LoadViewCSV.requests.set(
  "data/SimpleCSV.csv", // what is inputed in the command line
  {
    state: "success",
    response: [
      ["a", "b", "c"],
      ["d", "e", "f"],
    ],
  }
);

LoadViewCSV.requests.set(
  "data/SimpleCSV2.csv", // what is inputed in the command line
  {
    state: "success",
    response: [
      ["adios", "hola"],
      ["bye", "hi"],
    ],
  }
);

// Second mocked request
LoadViewCSV.requests.set("data/EmptyCSV.csv", {
  state: "success",
  response: [],
});

// Third mocked request
LoadViewCSV.requests.set("data/EpmtyCVS.csv", {
  state: "error-no-csv",
  response: [],
});

// Accessing outside of data file
LoadViewCSV.requests.set("..../C:/Windows/System32.csv", {
  state: "error-security",
  response: [],
});

export const SearchCSV = {
  requests: new Map<String, BackendResponse>([
    // Search 1: Success - ColumnName
    [
      "columnName=velocity 27",
      {
        state: "success",
        response: [
          ["Unlaiden Swallow", "129", "27", "Birb"],
          ["", "67", "27", "Untitled"],
        ],
      },
    ],
    // Search 2: Success - ColumnIndex
    [
      "columnIndex=2 aaa",
      {
        state: "success",
        response: [
          ["CS Student 398", "aaa", "20", "Stressed"],
          ["CS Student 459", "aaa", "21", "Relaxing"],
          ["CS Student 999", "aaa", "19", "Cramming"],
        ],
      },
    ],
    // Search 2: Success - Whole CSV
    [
      "aaa",
      {
        state: "success",
        response: [
          ["CS Student 398", "aaa", "20", "Stressed"],
          ["CS Student 459", "20", "aaa", "Relaxing"],
          ["CS Student 999", "ohno", "21", "aaa"],
        ],
      },
    ],
    // Search 4: Failure
    [
      "column=velocity 28",
      {
        state: "failure",
        response: [],
      },
    ],
    // Search 5: Failure
    [
      "velocity 27",
      {
        state: "failure",
        response: [],
      },
    ],
  ]),
};
