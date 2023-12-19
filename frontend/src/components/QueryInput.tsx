import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

// uses value state variable to update the command string in the REPL input class
interface QueryInputProps {
  queryTitle: string;
  setQueryTitle: (value: string) => void;
  question: string;
  setQuestion: (value: string) => void;
  keywords: string;
  setKeywords: (value: string) => void;
  // score: number;
  // setScore: Dispatch<SetStateAction<number>>;
  ariaLabel: string;
  onKeyPress?: (event: React.KeyboardEvent<HTMLInputElement>) => void;
}

// function which updates the command string from the text box input
export function QueryInput({
  queryTitle,
  setQueryTitle,
  question,
  setQuestion,
  keywords,
  setKeywords,
  // score,
  // setScore,
  ariaLabel,

  onKeyPress,
}: QueryInputProps) {
  return (
    <><input
      type="text"
      className="repl-command-box"
      value={queryTitle}
      placeholder="Enter title of query here!"
      onChange={(ev) => setQueryTitle(ev.target.value)}
      aria-label={ariaLabel}
      aria-description="where to put your query "
      //onKeyPress={onKeyPress}
      autoFocus
    ></input><textarea
      // type="text"
      className="repl-command-box"
      value={question}
      style={{ height: "40px" }}
      placeholder="Enter question of query here!"
      onChange={(ev) => setQuestion(ev.target.value)}
      aria-label={ariaLabel}
      aria-description="where to put your query "
      //onKeyPress={onKeyPress}
      autoFocus
    ></textarea><input
      type="text"
      className="repl-command-box"
      value={keywords}
      placeholder="Enter keyword list or map "
      onChange={(ev) => setKeywords(ev.target.value)}
      aria-label={ariaLabel}
      aria-description="enter keyword list or map "
      //onKeyPress={onKeyPress}
      autoFocus
    ></input>
    {/* <input
      type="text"
      className="repl-command-box"
      // value={score}
      placeholder="Enter minimum reliability score"
      onChange={(ev) => setScore(Number(ev.target.value))} 
      // ^^ maybe introduce error checking function to make sure
      aria-label={ariaLabel}
      aria-description="Where to enter minimum reliablity score "
      //onKeyPress={onKeyPress}
      autoFocus
    ></input> */}
    </>
  );
}
