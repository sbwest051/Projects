## Project Details:
Name: Server;
Team Members: Ethan Park (epa127), Blake Horne (behorne);
Estimated Time (average): 21 hours;
repo link: https://github.com/cs0320-f23/server-epa127-behorne.git

## Design Choices:
The program starts with the main method in Server. Main calls the Server method which sets up each endpoint of the
server. Server also creates an instance of CSVData which contains the data that LoadHandler, ViewHandler, and
SearchHandler all use by associating with CSVData, and Server takes in an instance of ACSAPISource which
BroadbandHandler associates and uses. The loadcsv endpoint calls handle of the LoadHandler. The LoadHandler takes in
a CSV filepath parameter to create a list of the CSV in CSVData. The searchcsv endpoint calls handle of SearchHandler.
The SearchHandler takes in parameters indicating a keyword, if the CSV has headers, and optionally a column name.
The SearchHandler then returns a set of the rows in which the keyword appears. The viewcsv endpoint calls handle of
ViewHandler which returns a printed visual of the CSV data. The broadband endpoint calls handle of BroadbandHandler.
BroadbandHandler uses parameters of a state and county name and passes them to the method getCountyBroadband of
ACSAPISource. ACSAPISource then connects to the American Community Survey API to get the codes for the state
and county, in order to then search up the percentage of homes in the county which have broadband.
This data is then passed back to the BroadbandHandler which then returns a response showing the percentage at the
particular date and time it was asked for. We did have to get outside resources to help with deserializing as we got
code on how to use a Scanner from https://www.baeldung.com/convert-input-stream-to-string.

## Errors/Bugs:

## Tests:
We've tested each of our handlers for there ability to perform their function correctly as well as each error
they can possibly throw based on the request made to the server. We've also tested ACSAPISource to check if it is
returning the correct percentage, and if it is also reporting the correct error. However, we did not test each method
of ACSAPISource as the one method, getCountyData, encompasses all methods of the class. We don't have tests for
serializing and deserializing as this was done manually as our code couldn't run without it. Something to note is that
testEmptySearch is not working properly when the test suite is run altogether, but passes when ran on its own.

## How to:
The program is run by running the main method of Server. From there, requests can be made to the server
at http://localhost:4000.

