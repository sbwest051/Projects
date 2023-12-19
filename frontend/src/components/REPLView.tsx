import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { TableData } from "./responseJSON";

/**
 * A connection between components in the mock.
 * @param data The currently stored CSV
 * @param count The current number of commands being displayed
 */
interface REPLViewProps {
  tableData: TableData;
  question: string;
  //count: number;
}
// interface Row {
//   File: string;
//   [key: string]: string | undefined;
// }

/**
 *
 * @param props An interface between a higher level component and a lower one
 * @returns A HTML Table containing the data contained in the props
 */

export function REPLView(props: REPLViewProps) {
  // Extracting query titles for column headers
  const columnHeaders = props.tableData.headers.map((header) => header.title);

  // Creating rows for each file
  const tableRows = props.tableData.fileList.map((file) => {
    // Start with the file title
    let row: string[] = [file.title];

    // Go through each metadata object to find the keyword with the highest reliability score
    file.metadata.forEach((metadata) => {
      if (metadata.result === "success") {
        let highestScore = 0;
        let highestKeyword = "";
        let relv = "";

        // Find the keyword with the highest reliability score
        for (const [keyword, [reliability, relevance]] of Object.entries(
          metadata.data
        )) {
          // the score that goes with the highest reliability goes with 
          if (reliability > highestScore) {
            highestScore = reliability;
            highestKeyword = keyword;
            relv = relevance.toString();
          }
        }

        // Append the highest keyword to the row
        row.push(
          highestKeyword +
            " \n Reliability Score: " +
            highestScore.toString() +
            " \n Relevance Score: " +
            relv
        );
        // row.push(highestScore.toString());
      } else {
        // If metadata result is not 'success', append 'N/A'
        row.push("N/A");
      }
      row.push(metadata.rawResponse);
    });

    // Append the raw response and reliability score to the end of the row
    // const metadataWithResponse = file.metadata.find(
    //   (m) => m.result === "success"
    // );
    // if (metadataWithResponse) {
    //   row.push(metadataWithResponse.rawResponse);
    //   const highestScoreEntry = Object.entries(
    //     metadataWithResponse.data
    //   ).reduce(
    //     (acc, [keyword, [score]]) => (score > acc[1] ? [keyword, score] : acc),
    //     ["", 0]
    //   );
    //   row.push(highestScoreEntry[1].toString());
    // } else {
    //   // If no successful metadata, append 'N/A'
    //   row.push("N/A", "N/A");
    // }

    return row;
  });

  // Render the table with rows
  return (
    <table>
      <thead>
        <tr>
          <th>File</th>
          {columnHeaders.map((header, idx) => (
            <th key={idx}>{header}</th>
          ))}

          <th>Raw Response</th>
        </tr>
      </thead>
      <tbody>
        {tableRows.map((rowData, index) => (
          <tr key={index}>
            {rowData.map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// return (
//   <><p aria-label="Query">Query: {props.question}</p>
//   <table aria-label="Table containing query results">
//     <thead>
//       <tr>
//         <th aria-label="Title">Title</th>
//         <th aria-label="Question Answer">Question Answer</th>
//       </tr>
//     </thead>
//     <tbody>
//       {props.tableData.map((file, index) => (
//         <tr key={index}>
//           <td>{file.title}</td>
//           <td>{file.metadata[0].rawResponse}</td>
//         </tr>
//       ))}
//     </tbody>
//   </table></>
// );
