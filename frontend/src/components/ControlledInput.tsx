import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

// Remember that parameter names don't necessarily need to overlap;
// I could use different variable names in the actual function.
interface ControlledInputProps {
  value: string;
  // This type comes from React+TypeScript. VSCode can suggest these.
  //   Concretely, this means "a function that sets a state containing a string"
  //setValue: Dispatch<SetStateAction<string>>;
  setValue: (newValue: string) => void;
  setPdfType: (newType: string) => void;
  ariaLabel: string;
  pdfType: string;
}


export function ControlledInput({
  value,
  setValue,
  setPdfType,
  ariaLabel,
  
}: ControlledInputProps) {
    const handleSelectChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = event.target.value;
    setPdfType(selectedValue);
    console.log(selectedValue);
    // You can do something with the selected value here
  };
  return (
    <>
      <input
        type="text"
        className="repl-command-box"
        value={value}
        placeholder="Enter pdf link or filepath here!"
        onChange={(ev) => setValue(ev.target.value)}
        aria-label={ariaLabel}
      ></input>
      <select onChange={handleSelectChange}>
        <option value="filepath">Filepath</option>
        <option value="link">Link</option>
      </select>
    </>
    
  );
}

// import "../styles/main.css";
// import { Dispatch, SetStateAction, useState } from "react";

// // Remember that parameter names don't necessarily need to overlap;
// // I could use different variable names in the actual function.
// interface ControlledInputProps {
//   value: string;
//   // files: string[];
//   setValue: Dispatch<SetStateAction<string>>;
//   ariaLabel: string;
//   //setFiles: Dispatch<SetStateAction<string[]>>;
// }

// const [inputProps, setInputProps] = useState<string[]>([]);

// // function handleAddInputProp() {
// //   setInputProps([...inputProps, ""]);
// // }

// // Input boxes contain state. We want to make sure React is managing that state,
// //   so we have a special component that wraps the input box.
// // export function ControlledInput({
// //   value,
// //   setValue,
// //   ariaLabel,
// // }: ControlledInputProps) {
// //   return (
// //     //{inputProps.map((input, index) => (
// //     <input
// //       type="text"
// //       className="repl-command-box"
// //       value={value}
// //       placeholder="Enter PDF here"
// //       onChange={(ev) => setValue(ev.target.value)}
// //       aria-label={ariaLabel}
// //       aria-description="where to put your PDF"
// //       //ref={inputRef}
// //       autoFocus
// //     ></input>
// //   );
// // }
// export function ControlledInput({
//   value,
//   setValue,
//   ariaLabel,
//   // files,
//   // setFiles,
// }: ControlledInputProps) {
//   return (
//     <div>
//       <input
//         type="text"
//         className="repl-command-box"
//         value={value}
//         placeholder="Enter PDF here"
//         onChange={(ev) => setValue(ev.target.value)}
//         aria-label={ariaLabel}
//         aria-description="where to put your PDF"
//         autoFocus
//       />
//     </div>
//   );
// }
