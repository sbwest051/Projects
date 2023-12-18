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
  question: string
  //count: number;
}

/**
 *
 * @param props An interface between a higher level component and a lower one
 * @returns A HTML Table containing the data contained in the props
 */

export function REPLView(props: REPLViewProps) {
  return (
    <><p aria-label="Query">Query: {props.question}</p>
    <table aria-label="Table containing query results">
      <thead>
        <tr>
          <th aria-label="Title">Title</th>
          <th aria-label="Question Answer">Question Answer</th>
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
    </table></>
  );
}
