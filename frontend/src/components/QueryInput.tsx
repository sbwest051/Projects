import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

// uses value state variable to update the command string in the REPL input class
interface QueryInputProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  onKeyPress?: (event: React.KeyboardEvent<HTMLInputElement>) => void;
}

// function which updates the command string from the text box input
export function QueryInput({
  value,
  setValue,
  ariaLabel,
  onKeyPress,
}: QueryInputProps) {
  return (
    <input
      type="text"
      className="repl-command-box"
      value={value}
      placeholder="Enter query here!"
      onChange={(ev) => setValue(ev.target.value)}
      aria-label={ariaLabel}
      aria-description="where to put your query "
      //onKeyPress={onKeyPress}
      autoFocus
    ></input>
  );
}
