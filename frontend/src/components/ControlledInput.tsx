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
  title : string;
  setTitle : (newValue: string) => void;
  ariaLabel: string;
  pdfType: string;
}


export function ControlledInput({
  value,
  setValue,
  title,
  setTitle,
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
        value={title}
        placeholder="Enter Title of PDF"
        onChange={(ev) => setTitle(ev.target.value)}
        aria-label={ariaLabel}
      />
          {/* <input
        type="text"
        className="repl-command-box"
        value={value}
        placeholder="Enter Title of PDF"
        onChange={(ev) => setValue(ev.target.value)}
        aria-label={ariaLabel}
      ></input> */}
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

