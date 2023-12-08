interface User {
    name: string;
    age: number;
    isAdmin: boolean;
}

// interface Files {
//     files: 
// }

// interface Source {
//     title: string
//     url: string
//     filepath: string
// }
export type Source = {
  title: string;
} & ({ url: string } | { filepath: string });


// Define the SourceData type as a tuple that represents the structure of data inputs.
export type SourceData = [string, string, string]; // Tuple of [pdfType, title, link/filepath]

// This function takes an array of SourceData and maps it to an array of Source.
export function constructJSON(dataValues: SourceData[]): { files: Source[] } {
  const files = dataValues.map(([pdfType, title, linkOrPath]): Source => {
    return pdfType === 'filepath'
      ? { title, filepath: linkOrPath }
      : { title, url: linkOrPath };
  });
    // Make JSON structure with the files array.
  return { files };
}