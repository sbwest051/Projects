import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

// uses value state variable to update the command string in the REPL input class
interface FilepathProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  onKeyPress?: (event: React.KeyboardEvent<HTMLInputElement>) => void;


}


// function which updates the command string from the text box input
export function Filepath({
  value,
  setValue,
  ariaLabel,
  onKeyPress,
}: FilepathProps) {

    function handleFileSubmit() {
      const jsonStructure = { filepath: value };
      fetch('http://localhost:4002/plme', {
      method: 'POST',
      headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify(jsonStructure),
  
})
    console.log(value)

  }
  return (
    <><input
          type="text"
          className="repl-command-box"
          value={value}
          placeholder="Enter filepath here!"
          onChange={(ev) => setValue(ev.target.value)}
          aria-label={ariaLabel}
          aria-description="where to put your file path"
          onKeyPress={onKeyPress}
          autoFocus
      ></input><button aria-label="manual submit button" onClick={() => handleFileSubmit()}>
              Submit </button></>
    
  );
}
