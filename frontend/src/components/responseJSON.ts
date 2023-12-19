export interface MetadataData {
  [keyword: string]: number[]; 
}

export interface Metadata {
  result: string;
  rawResponse: string;
  data: MetadataData;
}

export interface FileData {
  result: string;
  filepath: string;
  title: string;
  metadata: Metadata[];
}

export interface Header {
  title: string;
  question: string;
  keywordList: string[];
}

export interface TableData {
  result: string;
  headers: Header[];
  fileList: FileData[];
}
