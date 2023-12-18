import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { QueryInput } from "./QueryInput";
import { constructFilepathJSON } from "./frontendJSON";
import { REPLView } from "./REPLView";

interface FilepathProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  tableData: any[];
  setTableData: Dispatch<SetStateAction<any[]>>;
}

export function Filepath(props: FilepathProps) {
  const [queryTitle, setQueryTitle] = useState("");
  const [question, setQuestion] = useState("");
  const [keywords, setKeywords] = useState("");
  const [showTable, setShowTable] = useState(false); // state to control the visibility of the table

  function handleFileSubmit() {
    const jsonStructure = constructFilepathJSON(props.value, queryTitle, question, keywords);
    console.log(jsonStructure);

    fetch('http://localhost:4000/plme', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(jsonStructure),
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      console.log(data);
      if (data.result === "success") {
        props.setTableData(data.fileList);
        setQuestion(question); 
        setShowTable(true);
      }else {
        alert(data.message)
      // If result is not success, set the error message and show the popup
      // setErrorMessage(data.message || 'An error occurred.');
      // setShowErrorPopup(true);
    }

    })
    .catch(error => {
      console.error('Error:', error);
    });
  }

  return (
    <>
    {showTable && <REPLView question={question} tableData={props.tableData} />}
      <input
        type="text"
        className="repl-command-box"
        value={props.value}
        onChange={(ev) => props.setValue(ev.target.value)}
        aria-label={props.ariaLabel}
        placeholder="Enter filepath here!"
      />
      <h3>Enter Query information in these boxes</h3>
      <QueryInput
        queryTitle={queryTitle}
        setQueryTitle={setQueryTitle}
        question={question}
        setQuestion={setQuestion}
        keywords={keywords}
        setKeywords={setKeywords}
        ariaLabel="Query Input"
      />
      <button aria-label="manual submit button" onClick={handleFileSubmit}>
        Submit
      </button>
    </>
  );
}
