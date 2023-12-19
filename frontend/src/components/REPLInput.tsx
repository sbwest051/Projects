import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { QueryInput } from "./QueryInput";
import { constructJSON, Source, SourceData, Query } from "./frontendJSON";
import { REPLView } from "./REPLView";
import { mockData } from "../mock";
import { TableData } from "./responseJSON";

/**

 * @param history The history of each submitted command, stored in tuples or string 2D arrays
 * @param setHistory The function by which we alter history
 * @param data The currently stored CSV
 * @param setData How to set the currently stored data
 * @param count The current number of commands being displayed
 */

// interface MockDataType {
//   result: string;
//   headers: {
//     title: string;
//     question: string;
//     keywordList: string[];
//   }[];
//   fileList: {
//     result: string;
//     filepath: string;
//     title: string;
//     metadata: {
//       result: string;
//       rawResponse: string;
//       data: { [key: string]: [number, number] };
//     }[];
//   }[];
// }

interface REPLInputProps {
  history: [string, string | string[][]][];
  //files: string[]
  setHistory: Dispatch<SetStateAction<[string, string | string[][]][]>>;
  //setFiles: Dispatch<SetStateAction<string[]>>;
  count: number;
  setData: Dispatch<SetStateAction<string[][]>>;
  pdfType: string;
  tableData: TableData;
  setTableData: Dispatch<SetStateAction<TableData>>;
  //setMode:
}
/**
 * Handles the input and slight parsing of commands in the mock.
 * @param props An interface between a higher level component and a lower one.
 * @returns The input text box and submit button
 */
export function REPLInput(props: REPLInputProps) {
  const [count, setCount] = useState<number>(0);
  //const [files, setFiles] = useState<string[]>([]);
  const [inputValues, setInputValues] = useState<string[]>([""]);
  // title inpute
  const [titleValues, setTitleValues] = useState<string[]>([""]);
  // keeps track of whether a pdf is a link or a filepath
  const [pdfTypes, setPdfTypes] = useState<string[]>(["filepath"]);
  const [queryTitle, setQueryTitle] = useState("");
  const [question, setQuestion] = useState("");
  const [keywords, setKeywords] = useState("");
  const [score, setScore] = useState("");
  const [showTable, setShowTable] = useState(false); // state to control the visibility of the table
  //const [tableData, setTableData] = useState([]);
  const [queries, setQueries] = useState<Query[]>([
    { queryTitle: "", question: "", keywords: "" },
  ]);

  function handleAddQuery() {
    const newQuery = { queryTitle: "", question: "", keywords: "" };
    setQueries([...queries, newQuery]);
  }

  function updateQuery(index: number, field: keyof Query, value: string) {
    const updatedQueries = [...queries];
    updatedQueries[index] = { ...updatedQueries[index], [field]: value };
    setQueries(updatedQueries);
  }

  // This function is triggered when the submit button is clicked.
  function handleSubmit() {
    //setPdfTypes([...pdfTypes,props.pdfType ]) //check to see if this line is necessary
    console.log("Selected PDF Types:", pdfTypes);

    // inputValues.forEach((value, index) => {
    //list of lists with each inner list being a list that tells  [pdf type, title, link/filepath,]
    const dataValues = inputValues.map((value, index) => [
      pdfTypes[index],
      titleValues[index],
      value,
    ]);
    //setFiles(dataValues)

    const validData: SourceData[] = dataValues.filter(
      (item) => item.length === 3
    ) as SourceData[];
    const jsonStructure = constructJSON(validData, queries);
    console.log(jsonStructure);
    console.log(JSON.stringify(jsonStructure));

    fetch('http://localhost:4000/plme', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(jsonStructure),

    })
    .then(response => {
      // Check if the response is successful
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // Log the data received from the server
      console.log(data);
          if (data.result === "success") {
          // Update the tableData and question state if data.result is "success"
          props.setTableData(data);
          setShowTable(true); // Set the state to show the table
        }{
            alert(data.message)
          // If result is not success, set the error message and show the popup
          // setErrorMessage(data.message || 'An error occurred.');
          // setShowErrorPopup(true);
        }
      // REPLView(data.fileList,question)
    })
    .catch(error => {
      // Log any errors encountered during the fetch
      console.error('Error:', error);
    });
    //UNCOMMENT THESE LINE FOR MOCKING
    // props.setTableData(mockData);
    // setShowTable(true); // Display the table with mock data
  }

  function handleAddInputProp() {
    setInputValues([...inputValues, ""]);
    setTitleValues([...titleValues, ""]);
    console.log(...inputValues);
    setPdfTypes([...pdfTypes, props.pdfType]);
    console.log(...pdfTypes);
  }
  return (
    <div className="repl-input">
      {showTable && (
        <REPLView question={question} tableData={props.tableData} />
      )}

      {inputValues.map((value, index) => (
        <ControlledInput
          value={value}
          setValue={(newValue: string) => {
            const newInputValues = [...inputValues];
            newInputValues[index] = newValue;
            setInputValues(newInputValues);
          }}
          title={titleValues[index]}
          setTitle={(newTitle: string) => {
            const newTitleValues = [...titleValues];
            newTitleValues[index] = newTitle;
            setTitleValues(newTitleValues);
          }}
          ariaLabel={`Command input ${index}`}
          pdfType={pdfTypes[index]}
          setPdfType={(newType: string) => {
            const newPdfTypes = [...pdfTypes];
            newPdfTypes[index] = newType;
            setPdfTypes(newPdfTypes);
          }}

          //ariaLabel={"Command input"}
          // files={props.files}
          // setFiles={props.setFiles}
        />
      ))}
      <button onClick={handleAddInputProp}> add new pdf </button>

      <h3>Enter Query information in these boxes</h3>
      {queries.map((query, index) => (
        <QueryInput
          key={index}
          queryTitle={query.queryTitle}
          setQueryTitle={(value) => updateQuery(index, "queryTitle", value)}
          question={query.question}
          setQuestion={(value) => updateQuery(index, "question", value)}
          keywords={query.keywords}
          setKeywords={(value) => updateQuery(index, "keywords", value)}
          ariaLabel={`Query Input ${index + 1}`}
        />
      ))}
      <button onClick={handleAddQuery}>Add New Query</button>
      <button aria-label="manual submit button" onClick={() => handleSubmit()}>
        Submit{" "}
      </button>
    </div>
  );
}
