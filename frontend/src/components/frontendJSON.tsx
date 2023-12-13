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
} & ({ keywordList: any } | { keywordMap: any }); // TODO: need to figure out how to make this more specific

// Define the SourceData type as a tuple that represents the structure of data inputs.
export type SourceData = [string, string, string]; // Tuple of [pdfType, title, link/filepath]

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
    ? { keywordList: keywords.split(",") }
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
