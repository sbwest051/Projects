import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { QueryInput } from "./QueryInput";
import { constructFilepathJSON, Query } from "./frontendJSON";
import { REPLView } from "./REPLView";
import { TableData } from "./responseJSON";

interface FilepathProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  tableData: TableData;
  setTableData: Dispatch<SetStateAction<TableData>>;
}

export function Filepath(props: FilepathProps) {
  const [queryTitle, setQueryTitle] = useState("");
  const [question, setQuestion] = useState("");
  const [keywords, setKeywords] = useState("");
  const [showTable, setShowTable] = useState(false); // state to control the visibility of the table
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

  function handleFileSubmit() {
    const jsonStructure = constructFilepathJSON(props.value, queries);
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
        props.setTableData(data);
        // setQuestion(question); 
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
      {showTable && (
        <REPLView question={question} tableData={props.tableData} />
      )}
      <input
        type="text"
        className="repl-command-box"
        value={props.value}
        onChange={(ev) => props.setValue(ev.target.value)}
        aria-label={props.ariaLabel}
        placeholder="Enter filepath here!"
      />
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
      <button aria-label="manual submit button" onClick={handleFileSubmit}>
        Submit
      </button>
    </>
  );
}
