import { useState } from "react";
import "../styles/App.css";
import REPL from "./REPL";

/**
 * This is the highest level component!
 */

function App() {
  return (
    <div className="App">
      <p className="App-header">
        <h1>PLME</h1>
        <h2>Primary Literature Metadata Extractor</h2>
      </p>
      <REPL />
    </div>
  );
}

export default App;
