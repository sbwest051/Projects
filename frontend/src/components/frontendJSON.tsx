interface JSONStructure {
  files: Source[];
  columns: Column[];  
}

interface JSONFilepathStructure {
  filepath: string;
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

// has unparsed keywords
export interface Query {
  queryTitle: string;
  question: string;
  keywords: string;
}
// has parsed keywords
export type Column = {
  title: string;
  question: string;
} &({ keywordList: any } | { keywordMap: { [key: string]: any } });

// Define the SourceData type as a tuple that represents the structure of data inputs.
export type SourceData = [string, string, string]; // Tuple of [pdfType, title, link/filepath]

export function keyMapHelper(pairs: string[]){
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
        // Push the value into the corresponding array, splitting by spaces 
        keywordMap[key].push(...value.split(' ').map(v => v.trim()));
      } else {
        // If there is no colon, assume it is a single value and add it to a 'default' key
        if (!keywordMap['default']) {
          keywordMap['default'] = [];
        }
        keywordMap['default'].push(pair);
      }
    });
    return keywordMap;  // Return the constructed keywordMap
    }
export function constructJSON(
  dataValues: SourceData[],
  queries: Query[]
): JSONStructure {
  const files = dataValues.map(([pdfType, title, linkOrPath]): Source => {
    return pdfType === "filepath"
      ? { title, filepath: linkOrPath }
      : { title, url: linkOrPath };
  });

  const columns = queries.map((query) => {
    const keywordsArray = query.keywords.split(",");
    const columnKeywordsData = !keywordsArray[0].includes(":")
      ? { keywordList: keywordsArray.map((keyword) => keyword.trim()) }
      : { keywordMap: keyMapHelper(keywordsArray) };

    return {
      title: query.queryTitle,
      question: query.question,
      ...columnKeywordsData,
    };
  });

  return { files, columns };
}

export function constructFilepathJSON(
  filepath: string,
  queries: Query[]
): JSONFilepathStructure {
  const columns = queries.map((query) => {
    const keywordsArray = query.keywords.split(",");
    const columnKeywordsData = !keywordsArray[0].includes(":")
      ? { keywordList: keywordsArray.map((keyword) => keyword.trim()) }
      : { keywordMap: keyMapHelper(keywordsArray) };

    return {
      title: query.queryTitle,
      question: query.question,
      ...columnKeywordsData,
    };
  });

  return { filepath, columns };
}






