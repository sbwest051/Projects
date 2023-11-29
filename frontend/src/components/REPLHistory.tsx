import { Dispatch, SetStateAction, useState } from "react";
import "../styles/main.css";
import { REPLInput } from "./REPLInput";
import { REPLView } from "./REPLView";

/**
 * A connection between components in the mock.
 * @param history The history of each submitted command, stored in tuples or string 2D arrays
 * @param isVerbose Whether the current view method is verbose or not
 * @param count The current number of commands being displayed
 */
interface REPLHistoryProps {
  history: [string, string | string[][]][];
  // isVerbose: boolean;
  // setVerbose: Dispatch<SetStateAction<boolean>>;
  count: number;
}

/**
 * Handles all commands in the mock as well as displaying verbose/simple
 * @param props An interface between a higher level component and a lower one.
 * @returns A list of command responses, or a list of commands and their responses if isVerbose is toggled
 */
export function REPLHistory(props: REPLHistoryProps) {
  //const [data, setData] = useState<string[][]>([]);
  // Callback function to set data
  // const updateData = (newData) => {
  //   setData(newData);
 // if (!props.isVerbose) {
    return (
      //Insert here the actual return
      <div className="repl-history">
        {props.history.map((command, index) => (
          <>
            <p aria-label={`Output ${index}`}>Output:</p>
            <REPLView data={command[1]} count={index} />
          </>
        ))}
      </div>
    );
 // }
  // return (
  //   <div className="repl-history">
  //     {props.history.map((command, index) => (
  //       <>
  //         <p aria-label={`Commanded ${index}`}>Command: {command[0]}</p>
  //         <p aria-label={`Output ${index}`}>
  //           Output: <REPLView data={command[1]} count={index} />
  //         </p>
  //       </>
  //     ))}
  //   </div>
  // );
}
