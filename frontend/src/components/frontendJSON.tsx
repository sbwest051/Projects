interface JSONStructure {
  files: Source[];
  columns: Column[];  // Define the type of 'columns' or use a specific type
}


// interface Source {
//     title: string
//     url: string
//     filepath: string
// }
export type Source = {
  title: string;
} & ({ url: string } | { filepath: string });

export type Column = {
  title: string;
  question: string;
} &({ keywordList: any } | { keywordMap: { [key: string]: any } });

// Define the SourceData type as a tuple that represents the structure of data inputs.
export type SourceData = [string, string, string]; // Tuple of [pdfType, title, link/filepath]

function keyMapHelper(pairs: string[]){
  const keyValuePairs = pairs.map(piece => piece.trim());

// Initialize an empty object
    const keywordMap: { [key: string]: any } = {};

    // Iterate over each key-value pair string
    keyValuePairs.forEach(pair => {
      // Check if the pair contains a colon, indicating a key-value structure
      if (pair.includes(':')) {
        // Split by colon to separate the key from the value
        const [key, value] = pair.split(':').map(part => part.trim());
        // If the key doesn't exist in the object, initialize it with an empty array
        if (!keywordMap[key]) {
          keywordMap[key] = [];
        }
        // Push the value into the corresponding array, splitting by commas if necessary
        keywordMap[key].push(...value.split(',').map(v => v.trim()));
      } else {
        // If there is no colon, assume it is a single value and add it to a 'default' key
        if (!keywordMap['default']) {
          keywordMap['default'] = [];
        }
        keywordMap['default'].push(pair);
      }
    });
    }


// This function takes an array of SourceData and maps it to an array of Source.
export function constructJSON(dataValues: SourceData[], queryTitle: string, question: string, keywords: string): JSONStructure {
  const files = dataValues.map(([pdfType, title, linkOrPath]): Source => {
    console.log(pdfType);
    return pdfType === 'filepath'
      ? { title, filepath: linkOrPath }
      : { title, url: linkOrPath };
  });

  const keywordsArray = keywords.split(",");
  const columnKeywordsData = !keywordsArray[0].includes(":")
    ? {keywordList: keywords.split(",").map(keyword => keyword.trim())}
    : { keywordMap: { /* figure out how to construct */ } };

  const newColumn = {
    title: queryTitle,
    question: question,
    ...columnKeywordsData,
  };

  return {
    files,
    columns: [newColumn],
  };
}
