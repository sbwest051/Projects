import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";

/**
 * A connection between components in the mock.
 * @param data The currently stored CSV
 * @param count The current number of commands being displayed
 */
interface REPLViewProps {
  data: string | string[][];
  count: number;
}

/**
 *
 * @param props An interface between a higher level component and a lower one
 * @returns A HTML Table containing the data contained in the props
 */

export function REPLView(props: REPLViewProps) {
  return (
    // aria label to allow us to test how different tables would be returned by the order their corresponding request is
    <table align="center" aria-label={`ViewTable ${props.count}`}>
      <thead>
        <tr>
        </tr>
      </thead>
      <tbody aria-label={`body${props.count}`}>
        {Array.isArray(props.data) ? (
          props.data.map((row, rowIndex) => (
            <tr key={rowIndex}>
              {row.map((col, colIndex) => (
                <td
                  aria-label={`table ${props.count}, row ${rowIndex}, cell ${colIndex}`}
                  key={colIndex}
                >
                  {col}
                </td>
              ))}
            </tr>
          ))
        ) : (
          // switching over to handle when data is a string
          <tr>
            <td aria-label={`singleCell ${props.count}`}>{props.data}</td>
          </tr>
        )}
      </tbody>
    </table>
  );
}
