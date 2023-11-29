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
  ariaLabel: string;
}


export function ControlledInput({
  value,
  setValue,
  ariaLabel,
}: ControlledInputProps) {
  
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
      <select>
        <option value="option1">Filepath</option>
        <option value="option2"> Link </option>
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
