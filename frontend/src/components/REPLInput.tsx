import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { QueryInput } from "./QueryInput";
import {constructJSON, Source, SourceData} from "./frontendJSON"


/**
 * A connection between components in the mock.
 * @param history The history of each submitted command, stored in tuples or string 2D arrays
 * @param setHistory The function by which we alter history
 * @param isVerbose Whether the current view method is verbose or not
 * @param setVerbose How to set the verbocity
 * @param data The currently stored CSV
 * @param setData How to set the currently stored data
 * @param count The current number of commands being displayed
 */
interface REPLInputProps {
  history: [string, string | string[][]][];
  //files: string[]
  setHistory: Dispatch<SetStateAction<[string, string | string[][]][]>>;
  //setFiles: Dispatch<SetStateAction<string[]>>;
  count: number;
  setData: Dispatch<SetStateAction<string[][]>>;
  pdfType: string;
  //setMode:
}
/**
 * Handles the input and slight parsing of commands in the mock.
 * @param props An interface between a higher level component and a lower one.
 * @returns The input text box and submit button
 */
export function REPLInput(props: REPLInputProps) {
  // Remember: let React manage state in your webapp.
  // Manages the contents of the input box
  const [commandString, setCommandString] = useState<string>("");
  const [count, setCount] = useState<number>(0);
  const [files, setFiles] = useState<string[]>([]);
  const [inputValues, setInputValues] = useState<string[]>([""]);
  const [titleValues, setTitleValues] = useState<string[]>([""]);
  // keeps track of whether a pdf is a link or a filepath
  const [pdfTypes, setPdfTypes] = useState<string[]>(["filepath"]); 


  // This function is triggered when the submit button is clicked.
  function handleSubmit(commandString: string) {
    setCount(count + 1);
    setPdfTypes([...pdfTypes,props.pdfType ]) //check to see if this line is necessary 
    console.log("Selected PDF Types:", pdfTypes);
    //TODO: Add functionality to pass to backend handler


    // inputValues.forEach((value, index) => {
     //list of lists with each inner list being a list that tells  [pdf type, title, link/filepath,] 
    const dataValues = inputValues.map((value, index) => [pdfTypes[index], titleValues[index], value])
    //setFiles(dataValues)
    const validData: SourceData[] = dataValues
  .filter(item => item.length === 3) as SourceData[];
    const jsonStructure = constructJSON(validData);
    console.log(jsonStructure)
  fetch('http://localhost:4002/plme', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify(jsonStructure),
})
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));

    //console.log(JSON.stringify(jsonStructure, null, 2));
  }
  // function handleAddInputProp() {
  //   //setFile([...inputProps, ""]);
  // }
  function handleAddInputProp() {
    setInputValues([...inputValues, ""]);
    setTitleValues([...titleValues, ""]);
    console.log(...inputValues);
    setPdfTypes([...pdfTypes,props.pdfType ])
    console.log(...pdfTypes);
  }
  return (
    <div className="repl-input">
      {/* <fieldset> */}
      {/* <legend>Enter a command:</legend> */}
      <QueryInput
        value={commandString}
        setValue={setCommandString}
        ariaLabel={"Query input"}
        onKeyPress={(e) => {
        if (e.key === "Enter") {
              handleSubmit(commandString);
            }
          }}
      />
      <> </>

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


            <QueryInput
        value={commandString}
        setValue={setCommandString}
        ariaLabel={"Query input"}
        onKeyPress={(e) => {
        if (e.key === "Enter") {
              handleSubmit(commandString);
            }
          }}
      />
            <button aria-label="button" onClick={() => handleSubmit(commandString)}>
        {/*This is where we will asign which function and/or code to use*/}
        "Submit"
      </button>
      
    </div>
  );
}
