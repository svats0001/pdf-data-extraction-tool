import java.awt.geom.Rectangle2D;
import java.io.File;
import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
/**
 * Class that encompasses all of the functionality required to generate an automated report based on keyword inputs
 * provided by user
 * @author Shantanu Vats
 * @version 1.0
 * @since 28/04/22
 */
public class ReportGenerator {
	/**
	 * Array that holds all of the keywords provided by the user in the FileInputScreen class
	 */
	private static String[] keywords;
	/**
	 * Default constructor for the class
	 */
	public ReportGenerator() {
		
	}
	/**
	 * Extracts all the text from a PDF file and returns a string containing the extracted text
	 * @param newFile          A file object representing the PDF file for which to extract text
	 * @return                 A string representing the extracted text
	 * @throws IOException     Occurs when the loadPDF method of the Loader class is not able to load the specified
	 *                         param newFile as a PDDocument 
	 */
	public static String extractTextFromPDF(File newFile) throws IOException {
		/**
		 * A PDDocument object that represents the PDF file for which text needs to be extracted
		 */
		PDDocument pdDoc = Loader.loadPDF(newFile);
		/**
		 * A string representing the extracted text
		 */
		String pageText = "";
		/**
		 * An area of the page that ignores possible headers and footers in the page
		 */
		Rectangle2D region = new Rectangle2D.Double(0, 25, 595, 792);
		/**
		 * A name for the Rectangle2D object
		 */
		String regionName = "region";
		/**
		 * The number of pages in the PDF file
		 */
		int numPages = pdDoc.getNumberOfPages();
		
		for (int j = 0; j < numPages; j++) {
			/**
			 * An object that only extracts text in a particular region of the page
			 */
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			/**
			 * One page of the PDF file, based on the index provided in the loop
			 */
			PDPage page = pdDoc.getPage(j);
			stripper.addRegion(regionName, region);
			stripper.extractRegions(page);
			pageText += stripper.getTextForRegion(regionName);
		}
		pdDoc.close();
		
		return pageText;
	}
	/**
	 * Generates a PDF file report of sentences containing a keyword based on a PDF file selected by the user 
	 * and a set of keywords
	 * @param newFile          File object representing a PDF file selected by the user
	 * @throws IOException     Occurs if the extractTextFromPDF method is unable to extract the text from the
	 *                         specified PDF file
	 */
	@SuppressWarnings("resource")
	public static void generateReport(File newFile) throws IOException {
		keywords = FileInputScreen.words;
		/**
		 * ArrayList for holding all non-empty keywords provided by user in FileInputScreen
		 */
		ArrayList<String> sentenceKeywords = new ArrayList<String>();
		
		for (int i = 0; i < keywords.length; i++) {
			if (keywords[i] != "") {
				sentenceKeywords.add(keywords[i]);
			}
		}
		/**
		 * The extracted text from the PDF file selected by the user
		 */
		String pageText = extractTextFromPDF(newFile);
		/**
		 * ArrayList of arraylists that holds the list of sentences found for each keyword provided by the user
		 */
		ArrayList<ArrayList<String>> sentenceResults = findSentencesForKeywords(pageText, sentenceKeywords);
		/**
		 * Create a new document of style PDF to store the sentences that have been found for each keyword
		 */
		PDDocument createFile = new PDDocument();
		/**
		 * Set font of new document to Helvetica
		 */
		PDFont font =  new PDType1Font(Standard14Fonts.FontName.HELVETICA);
		/**
		 * A sample text to be used for generating the line width of each sentence in the new document
		 */
		String baseText = "Production of 83,476 dry metric tonnes (dmt) of spodumene concentrate (September Quarter";
		/**
		 * The size of the font in the new document
		 */
		int fontSize = 12;
		/**
		 * The amount of space to be left blank at the top of a page in the new document
		 */
		int topMargin = 30;
		/**
		 * The width of each line in a page in the new document
		 */
		float lineWidth = font.getStringWidth(baseText)/1000 * fontSize;
		/**
		 * The height of each line in a page in the new document
		 */
		float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight()/1000 * fontSize;
		
		for (int l = 0; l < sentenceResults.size(); l++) {
			/**
			 * New page in the document
			 */
			PDPageContentStream contentStream = createNewPDPage(createFile, font, fontSize, lineHeight, lineWidth, topMargin);
			/**
			 * Counter for the number of lines in the page
			 */
			int lineCounter = 0;
			/**
			 * A limit for the number of lines that can be in one page
			 */
			int lineLimit = 50;
			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), fontSize);
			contentStream.showText(sentenceKeywords.get(l));
			contentStream.setFont(font, fontSize);
			contentStream.newLineAtOffset(0, -2*lineHeight);
			lineCounter = lineCounter + 3;
			for (int m = 0; m < sentenceResults.get(l).size(); m++) {
				/**
				 * A sentence for a particular keyword
				 */
				String startSentence = sentenceResults.get(l).get(m);
				/**
				 * ArrayList containing a finalised set of sentences where each sentence is split if it exceeds the
				 * maximum width for a line
				 */
				ArrayList<String> finalSentences = new ArrayList<String>();
				/**
				 * The number of characters after which a sentence is split into smaller sentences 
				 */
				int splitLength = 80;
				/**
				 * The length of the initial sentence
				 */
				int startSentenceLength = startSentence.length();
				/**
				 * A line/character counter
				 */
				int n = 0;
				while ((n*splitLength) < startSentenceLength) {
					if (startSentenceLength < (n*splitLength + splitLength)) {
						finalSentences.add(startSentence.substring(n*splitLength, startSentenceLength));
					}
					else {
						finalSentences.add(startSentence.substring(n*splitLength, n*splitLength + splitLength));
					}
					n++;
				}
				/**
				 * Iterator object for iterating through the finalised list of sentences after splitting sentences
				 * into smaller sentences
				 */
				Iterator<String> finalSentencesIter = finalSentences.iterator();
				while (finalSentencesIter.hasNext()) {
					contentStream.showText(finalSentencesIter.next());
					contentStream.newLineAtOffset(0, -lineHeight);
					lineCounter++;
					if (lineCounter > lineLimit) {
						contentStream.endText();
						contentStream.close();
						contentStream = createNewPDPage(createFile, font, fontSize, lineHeight, lineWidth, topMargin);
						lineCounter = 0;
					}
				}
				contentStream.newLineAtOffset(0, -lineHeight);
				lineCounter++;
			}
			contentStream.endText();
			contentStream.close();
		}
		/**
		 * The name of the new PDF file to be created in the user's file system. File to be created in the same
		 * directory as input PDF file.
		 */
		String newFilePath = FileInputScreen.getSaveLocationPath() + "/" + FileInputScreen.fileName.split("\\.")[0];
		for (int p = 0; p < sentenceKeywords.size(); p++) {
			newFilePath = newFilePath + "-" + sentenceKeywords.get(p);
		}
		newFilePath = newFilePath + java.time.LocalDate.now() + ".pdf";
		createFile.save(new File(newFilePath));
		createFile.close();
	}
	/**
	 * Initialises a new page in a PDDocument with specified font, line dimensions and margin
	 * @param fileToCreatePageIn     The PDDocument in which to add the new page
	 * @param font                   The type of font to be used
	 * @param fontSize               Size of the font
	 * @param lineHeight             Height of each line
	 * @param lineWidth              Width of each line
	 * @param topMargin              Amount of blank space at the top of the page
	 * @return                       PDPageContentStream for a new page in a document
	 * @throws IOException           Occurs when a new page is unable to be created in param fileToCreatePageIn
	 */
	public static PDPageContentStream createNewPDPage(PDDocument fileToCreatePageIn, PDFont font, int fontSize, float lineHeight, float lineWidth, int topMargin) throws IOException {
		/**
		 * A new page
		 */
		PDPage nextPage = new PDPage();
		fileToCreatePageIn.addPage(nextPage);
		/**
		 * Content stream for the newly created page
		 */
		PDPageContentStream contentStream = new PDPageContentStream(fileToCreatePageIn, nextPage);
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.newLineAtOffset((nextPage.getMediaBox().getWidth() - lineWidth)/2, nextPage.getMediaBox().getHeight() - topMargin - lineHeight);
		return contentStream;
	}
	/**
	 * Given a list of provided keywords, returns the sentences in a PDF file that contain the keywords
	 * @param textInPage          The extracted text in a PDF page
	 * @param userKeywords        The list of keywords provided by the user in FileInputScreen
	 * @return                    A list of lists containing a list of sentences for each keyword
	 */
	public static ArrayList<ArrayList<String>> findSentencesForKeywords(String textInPage, ArrayList<String> userKeywords) {
		/**
		 * Split extracted text based on characters that often represent a separation of content in a PDF file,
		 * such as a full stop, bullet points, semicolon or colon
		 */
		String[] sentences = textInPage.split("\\.|;|:|\u2022");
		/**
		 * Returns true if all the words in a keyword have been matched in a sentence
		 */
		boolean allMatched = true;
		/**
		 * A list of matched sentences for each keyword
		 */
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < userKeywords.size(); i++) {
			/**
			 * List of matched sentences for a keyword
			 */
			ArrayList<String> tmpRes = new ArrayList<String>();
			/**
			 * Split keyword into array of words as one keyword may contain multiple words
			 */
			String[] individualWords = userKeywords.get(i).split(" ");
			/**
			 * A single sentence in the list of all sentences
			 */
			for (String j : sentences) {
				/**
				 * A single word in the list of all words in one keyword
				 */
				for (String k : individualWords) {
				    if (j.indexOf(k) == -1) {
					    allMatched = false;
					    break;
				    }
				}
				if (allMatched) {
					j = j.replaceAll("\\n", "");
					j = j.replaceAll("\\r", "");
					tmpRes.add(j);
				}
				else {
					allMatched = true;
				}
			}
			results.add(tmpRes);
		}
		return results;
	}
	/**
	 * An algorithm for generating a list of numbers/metrics from the PDF file for each keyword provided
	 * @param textInPage     Extracted text from the PDF file
	 * @param keywords       List of keywords provided by the user in FileInputScreen
	 * @return               List of lists of digits/numbers/metrics in a matched sentence for each keyword
	 */
	public static ArrayList<ArrayList<String>> findNumbersForKeywords(String textInPage, String[][] keywords) {
		/**
		 * Array of sentences whereby the extracted text is split on characters that often signal a separation of
		 * content in a file
		 */
		String[] sentences = textInPage.split("\\.|;|:|\u2022");
		/**
		 * Returns true if all the words in a keyword are present in a sentence, given that there may be more than
		 * one word in one keyword
		 */
		boolean allMatched = true;
		/**
		 * The possible unit of measurement for an identified metric/number
		 */
		String units;
		/**
		 * List of lists of metrics/numbers for each keyword
		 */
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < keywords.length; i++) {
			/**
			 * List of metrics/numbers for a keyword
			 */
			ArrayList<String> tmpRes = new ArrayList<String>();
			/**
			 * Array of individual words in one keyword, given that there may be more than one word in a single
			 * keyword
			 */
			String[] individualWords = keywords[i][0].split(" ");
			/**
			 * One sentence from the list of all sentences in the PDF file
			 */
			for (String j : sentences) {
				units = "";
				/**
				 * One word in the array of all words in a single keyword
				 */
				for (String k : individualWords) {
				    if (j.indexOf(k) == -1) {
					    allMatched = false;
					    break;
				    }
				}
				if (allMatched) {
					if (keywords[i].length > 1) {
						allMatched = false;
					    for (int m = 1; m < keywords[i].length; m++) {
						    if (j.contains(keywords[i][m])) {
							    units = keywords[i][m];
							    allMatched = true;
							    break;
						    }
					    }
					}
					if (!allMatched) {
						allMatched = true;
						continue;
					}
					/**
					 * A string containing all of the numbers/metrics found in a matched sentence
					 */
					String pageDigits = "";
					/**
					 * Returns true if the previous character was not a digit
					 */
					boolean previousLetter = true;
					for (int l = 0; l < j.length(); l++) {
						if (Character.isDigit(j.charAt(l))) {
							if (previousLetter) {
								if (pageDigits.length() != 0) {
									pageDigits = pageDigits + " ";
								}
							}
							pageDigits = pageDigits + String.valueOf(j.charAt(l));
							previousLetter = false;
						}
						else {
							if (l != (j.length()-1) && previousLetter == false && Character.isDigit(j.charAt(l+1)) && (j.charAt(l) == ',')) {
								continue;
							}
							previousLetter = true;
						}
					}
				    if (pageDigits.length() > 0) {
				    	pageDigits = pageDigits.replace(" ", units+" ");
				    	pageDigits = pageDigits + units;
				    	tmpRes.add(pageDigits);
				    }
				}
				else {
					allMatched = true;
				}
			}
			results.add(tmpRes);
		}
		return results;
	}
}