import { SetStateAction, useState } from "react";
import "../styles/main.css";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";

/* 
  Top level component for the REPL. Handles all lower components by passing in props.
*/
export default function REPL() {
  const [history, setHistory] = useState<[string, string | string[][]][]>([]);
  //const [isVerbose, setVerbose] = useState<boolean>(false);
  const [data, setData] = useState<string[][]>([]);
  const [count, setCount] = useState<number>(0);
  const [tableData, setTableData] = useState<any>([]);
 // const [files, setFiles] = useState<string[]>([]);

  return (
    <div className="repl">
      <REPLHistory
        history={history}
        count={count}
        tableData={tableData}
      />
      {/* <hr></hr> */}
      <REPLInput
        // files={files}
        history={history}
        setHistory={setHistory}
        tableData={tableData}
        setTableData={setTableData}
        //setFiles={setFiles}
        //data={data}
        setData={setData}
        count={count} pdfType={""}      />
    </div>
  );
}
