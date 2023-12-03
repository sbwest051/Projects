import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { QueryInput } from "./QueryInput";
import { LoadViewCSV, SearchCSV } from "../mockedJson";


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
  // keeps track of whether a pdf is a link or a filepath
  const [pdfTypes, setPdfTypes] = useState<string[]>(["filepath"]); 


  // This function is triggered when the submit button is clicked.
  function handleSubmit(commandString: string) {
    setCount(count + 1);
    setPdfTypes([...pdfTypes,props.pdfType ]) //check to see if this line is necessary 
    console.log("Selected PDF Types:", pdfTypes);
    //TODO: Add functionality to pass to backend handler


    // inputValues.forEach((value, index) => {
    const dataValues = inputValues.map((value, index) => [pdfTypes[index], value])
  // });
  }
  // function handleAddInputProp() {
  //   //setFile([...inputProps, ""]);
  // }
  function handleAddInputProp() {
    setInputValues([...inputValues, ""]);
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

      {inputValues.map((value, index) => (
        <ControlledInput
          value={value}
          setValue={(newValue: string) => {
            const newInputValues = [...inputValues];
            newInputValues[index] = newValue;
            setInputValues(newInputValues);
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

      <button aria-label="button" onClick={() => handleSubmit(commandString)}>
        {/*This is where we will asign which function and/or code to use*/}
        "Submit"
      </button>
      <button onClick={handleAddInputProp}> add new pdf </button>
    </div>
  );
}
