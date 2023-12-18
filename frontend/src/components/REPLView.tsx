import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";

/**
 * A connection between components in the mock.
 * @param data The currently stored CSV
 * @param count The current number of commands being displayed
 */
interface REPLViewProps {
  tableData: any[];
  //count: number;
}

/**
 *
 * @param props An interface between a higher level component and a lower one
 * @returns A HTML Table containing the data contained in the props
 */

export function REPLView(props: REPLViewProps) {
  return (
    <table>
      <thead>
        <tr>
          <th>Title</th>
          <th>Question Answer</th>
        </tr>
      </thead>
      <tbody>
        {props.tableData.map((file, index) => (
          <tr key={index}>
            <td>{file.title}</td>
            <td>{file.metadata[0].rawResponse}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
