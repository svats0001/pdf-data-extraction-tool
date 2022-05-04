import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * This class is concerned with all of the functionalities relating to generating a list of metrics inside of a PDF
 * file
 * @author     Shantanu Vats
 * @version    1.0
 * @since      28/04/22
 */
public class MetricsGenerator {
	/**
	 * Default constructor
	 */
	public MetricsGenerator() {
		
	}
	/**
	 * Generates a list of all metrics that were found in the PDF file, using an external Python file that makes
	 * use of the Named Entity Extractor (NER) machine learning algorithm by spaCy
	 * @param newFile                    The PDF file for which to generate metrics for
	 * @return                           A list of strings containing metric/value/date results 
	 * @throws IOException               Occurs if the FileWriter was unable to write extracted text from the PDF file
	 *                                   to a new file that is used by the Python file
	 * @throws InterruptedException      Occurs if the external Python file execution process is interrupted
	 */
	public static ArrayList<String> generateMetrics(File newFile) throws IOException, InterruptedException {
		/**
		 * The extracted text from the PDF file
		 */
		String fileText = ReportGenerator.extractTextFromPDF(newFile);
		/**
		 * File name for the output file that will contain the extracted text from the input PDF file, which the
		 * Python file will read in order to generate metrics
		 */
		String outputFileName = "filetext-" + java.time.LocalDate.now() + ".txt";
		/**
		 * Creates a new file in same directory as input PDF file and overwrites any existing text inside the file
		 */
		FileWriter extractedText = new FileWriter(FileInputScreen.getSaveLocationPath() + "\\" + outputFileName, false);
		extractedText.write(fileText);
		extractedText.close();
		/**
		 * A new ProcessBuilder object that executes the Python file containing metric generation functionality and
		 * passes a command line argument containing the location in the file system of the file containing 
		 * extracted text created previously
		 */
		ProcessBuilder pb = new ProcessBuilder("python", "res/december_report_nlp_test.py", FileInputScreen.getSaveLocationPath() + "\\" + outputFileName, "res/ner_model");
		pb.redirectErrorStream(true);
		/**
		 * A list of strings that will hold the metric/value/date results generated in the standard output of the
		 * Python file
		 */
		ArrayList<String> result = new ArrayList<String>();
		/**
		 * Start the execution of the Python file
		 */
		Process process = pb.start();
		/**
		 * New BufferedReader object for reading lines from the standard output of the execution of the Python file
		 */
		BufferedReader bfr = new BufferedReader(new InputStreamReader(process.getInputStream()));
		/**
		 * The next line in the input stream of the process
		 */
		String line = bfr.readLine();
		while (line != null) {
			result.add(line);
			line = bfr.readLine();
		}
		process.waitFor();
		
		return result;
	}
}