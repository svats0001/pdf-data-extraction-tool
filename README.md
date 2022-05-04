# pdf-data-extraction-tool

What the program does?
- Search a PDF file for user provided keywords and return a new PDF file containing sentences inside of the input PDF file that match the keywords provided
- Generate a list of all purported metrics (metric/value/date pairs may sometimes be inaccurate) or numeric values from an input PDF file and allow the user to search by metric
- Generate a list of all tables present in an input PDF file and allow the user to view the content in each individual table
- Allow the user to export one or more tables from the table viewer to a CSV file for further data analysis purposes

Why is this tool useful?
- Automated data extraction from a PDF file that will enable you to automatically extract key data present inside of a PDF
- You don't have to scan through large PDF files in order to find key data, which are often numeric values or tabular data
- Extract data from a PDF into file formats that are more suitable for data analysis, like the CSV file format

How to get started?
1. Clone this repository into your local file system
2. Open the top level folder (PDF_Data_Extraction_Tool) in an IDE for Java developers such as Eclipse as a project
3. Don't move any files around the project file hierarchy otherwise features might not work
4. To be able to use the metrics generation and table generation features, you must have Python installed on your system otherwise you'll run into an error. These features use Python file resources that contain machine learning algorithms that perform most of the grunt work
5. Run the FileInputScreen class to start the program

How to get help?
Create a new issue in this repository and write a message inside of the issue for whatever you need assistance with

Who maintains and contributes to this project?
I created this small tool as practice for my Java, Python and general software development skills. I probably won't be adding anything else to it unless I happen to get comments from other users about any bugs or improvements. If you like, you can fork this repository and make your own changes and improvements
