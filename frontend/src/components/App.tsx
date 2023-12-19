import { useState, useEffect } from "react";
import "../styles/App.css";
import REPL from "./REPL";
import {Filepath }from "./Filepath";
import Popup from "reactjs-popup";
import { TableData } from "./responseJSON";

function App() {
    const [showPopup, setShowPopup] = useState(true);
    const [isFileClicked, setIsFileClicked] = useState(false);
    const [isManualClicked, setIsManulClicked] = useState(false);
    const [filePathValue, setFilePathValue] = useState("");
    const [tableData, setTableData] = useState<any>([]);

    // useEffect(() => {
    //     // Logic to control when the popup should be shown
    //     // For example, setShowPopup(true);
    // }, []);
        const handleFileButton = () => {
        setIsFileClicked(true);
        setShowPopup(false); 
    };
        const handleManualButton = () => {
        setIsManulClicked(true);
        setShowPopup(false);
    };

    return (
        <div className="App">
            <Popup open={showPopup} closeOnDocumentClick={false}>
                <div className="popup">
                    {/* <p>Instructions</p> */}
                    <p>Please choose how you would like to enter your query</p>
                    <button onClick={handleFileButton} >Enter query with file </button>
                    <button onClick={handleManualButton}>Enter query manually </button>
                </div>
            </Popup>
            <p className="App-header">
                <h1>PLME</h1>
                <h2>Primary Literature Metadata Extractor</h2>
            </p>
            {isManualClicked && <REPL />}
                        {isFileClicked && (
                <Filepath 
                    value={filePathValue} 
                    setValue={setFilePathValue} 
                    ariaLabel="File input"                   
                    tableData={tableData}
                    setTableData={setTableData}
                />
            )}
            {/* <REPL /> */}
        </div>
    );
}

export default App;
