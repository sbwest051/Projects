# term-project-swest9-epark73-bwangenh--

## Project details
**Name:** Primary Literature Metadata Extractor (PLME)

**Description:** A huge part of research is review literature, which can be very useful to provide a
good update on the status of research in a certain field. However, research is humungous. To make an
effective review, one must go through and collect metadata from thousands of papers. Normally, this 
process would be done manually.

The problem we are attempting to solve with this project is to significantly decrease the amount of 
time to collect accurate metadata from a huge dataset. This primarily affects those in academia and 
higher education, including students like ourselves and our classmates, which allowed us to initially 
learn about the problem. It’s possible to do this process manually, relying on skimming individual papers 
or tools with semi-functional filtering capabilities, but our project will make the process far more 
efficient and labor-effective. Our project is important in making the literature review process far 
easier, which could help us and the many other students who must read research papers in large quantities.

**Team members and contributions:**
Brooke Wangenheim (bwangenh) - Integration and Testing; 
Ethan Park (epark73) - Backend;
Serena West (swest9) - Frontend;

**Estimated Time:** ≈30 hours each

https://github.com/cs0320-f23/term-project-swest9-epark73-bwangenh.git

## Design choices
### Backend
- PDFSource interface is accepted by the MetadataHandler class to allow for different sources of 
  content.
- See records folder documentation for additional data structure and json structure information.

## Errors/Bugs
### Frontend
- The current outputs result in some cells not matching their column label. 
- Relevance scores are relatively lost for most files input
- Post request may cause CORS error depending on system settings.

## Tests


## How to…
### Run Frontend
In the frontend directory, run npm run start in the terminal to start the frontend.

**Query**

**Manual File input:**
Provide the file’s title and public link or pathway to file (must be in data folder of the backend)

All queries require a title and question

**Query inputs:**
If you would like to enter a keyword list separate you keywords with commas

To enter a **keyword map** please input in this format:
Keyword1: word1 word2 word3, Keyword2: word4 word 5 word6

**File input with csv:**
Enter csv file in first filepath box (must be in data folder of the backend) and input query same as above query
instructions. The first column of the csv should be the title of the pdf. The second column is 
the filepath (if uploading local pdf). The third column is the link to the pdf. Only one of the 2nd or 3rd columns should be filled.

### Run backend
Run the Server file to turn on the backend API.

## Credits
Multipart Post Request: 
https://stackoverflow.com/questions/1378920/how-can-i-make-a-multipart-form-data-post-request-using-java#comment1218277_1379002

Levenshtein Edit Distance Algorithm:
https://www.educative.io/answers/the-levenshtein-distance-algorithm

## Future Implementation
### Frontend
- Table sorting and searching
- Cleaner user interface
### Backend
- Caching for space optimization
- Using APIs to map from DOI links to all relevant information on a paper
- External API extensibility
- Creation of an NLP model to minimize costs and optimize the metadata extracting algorithm.