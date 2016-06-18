// Class to read and format DMDX event data into XML.
//
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.jdom.*;
import org.jdom.output.*;


/**
 * Class <tt>ZilVerter</tt> reads a DMDX output data file in .zil format
 * and formats the data in XML, producing one XML file per subject.
 *
 * @author  Tom Hicks. 06/12/03.
 * @version $Revision: 1.11 $<BR>
 * Last Modified: $Date: 2003/06/19 05:38:23 $
 *                Finish up by parsing data lines.
 */
public class ZilVerter {
  /** Number of lines read from input */
  private int lineNum = 0;

  /** If not null, this is the most recent line read from the input data */
  private String nextLine = null;

  /** The XML output document being created from the ZIL input file */
  private Document xmlDoc = null;

  /** The input data reader containing the ZIL input file to be parsed */
  private BufferedReader zin = null;


  //
  // Pre-compiled Patterns for use in parsing input data lines
  //

  private Pattern data_pat = Pattern.compile(
    "\\s*(\\d+(?:\\.\\d+)?),([+-]\\w+)");

  private Pattern item_pat = Pattern.compile(
    "Item (\\d+), COT (\\d+(?:\\.\\d+)?)");

  private Pattern subject_pat = Pattern.compile(
    "Subject (\\d+), (\\d+/\\d+/\\d+ \\d+:\\d+:\\d+) on "+
    "(\\w+), refresh (\\d+(?:\\.\\d+)?ms)(?:, ID (\\w+))?");



  /**
   * Constructor with all required arguments.
   *
   * @param inputRdr the input data reader from which to read lines.
   */
  public ZilVerter (BufferedReader inputRdr) {
    this.zin = inputRdr;
  }


  /**
   * Main method to setup the input data stream, instantiate the class,
   * and call the top-level method.
   */
  public static void main (String[] args) {
    try {
      InputStream istream = null;

      if (args.length > 0) {
        File inFyl = new File(args[0]);
        if (!inFyl.exists()) {
          System.err.println("Unable to find input file '" + args[0] + "'");
          System.exit(1);
        }
        istream = new FileInputStream(inFyl);
      }
      else
        istream = System.in;

      ZilVerter zv =
        new ZilVerter(new BufferedReader(new InputStreamReader(istream)));
      zv.convert();
    }
    catch (Exception ex) {
      System.err.println("(ZilVerter) Got Exception:\n"); 
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }


  //
  // Private Methods
  //
  
  /**
   * The top-level method to read and parse the input data stream and
   * generate the corresponding XML to the standard output stream.
   */
  private void convert () throws IOException {
    try {
      // start top-level XML
      Element root = new Element("zil");    // create XML root element
      xmlDoc = new Document(root);          // create the top-level XML document

      // find all subjects in the input file and process them
      String subjLine = null;
      while ((subjLine = findLine("Subject ")) != null) {
        doSubject(root, subjLine);
      }

      // Generate and output the XML from the internal data structure
      XMLOutputter out = new XMLOutputter("  ", true);
      out.setExpandEmptyElements(true);
      out.setTextNormalize(true);
      out.output(xmlDoc, System.out);
    }
    catch (Exception ex) {
      System.err.println("(ZilVerter) Error on input line " + lineNum +
                         ". Got Exception:\n"); 
      ex.printStackTrace(System.err);
      System.exit(2);
    }
  }


  /**
   * Process each Subject from the input data stream.
   *
   * @param root the XML root element to which to attach generated XML.
   * @param subjLine the input data line from which to parse subject information
   *                 for this particular Subject.
   */
  private void doSubject (Element root, String subjLine)
    throws IOException
  {
    Element subj = addNode(root, "subject"); // create & attach XML subject node

    parseSubjectLine(subj, subjLine);       // parse the fields from subject line

    // read and process all subordinate Item lines
    String line = null;
    while ((line = getLine()) != null) {
      if (lineIsItem(line)) {
        doItem(subj, line);                 // process an Item line
      }
      else if (lineIsComment(line))         // skip over comment lines
        continue;
      else                                  // we ran out of Items
        break;
    }
  }


  /**
   * Parse the important fields from the given subject line, create XML
   * for them, and attach the XML to the given parent node.
   *
   * @param parent the parent XML node to which to attach parsed information.
   * @param subjLine the subject line from which to parse information.
   */
  private void parseSubjectLine (Element parent, String subjLine) {
    Matcher m = subject_pat.matcher(subjLine);
    if (m.find()) {
      String snum = m.group(1);             // get the subject number
      parent.setAttribute("num", snum);
      String datetime = m.group(2);         // get the date/time field
      parent.setAttribute("dateTime", datetime);
      String hostname = m.group(3);         // get the hostname field
      parent.setAttribute("host", hostname);
      String refresh = m.group(4);          // get the refresh time field
      parent.setAttribute("refresh", refresh);
      String subjId = m.group(5);           // get the (optional) subject id
      if (subjId != null)                   // if a subject id was found then
        parent.setAttribute("id", subjId);  // attach the subject id
    }
  }


  /**
   * Process each Item from the input data stream.
   *
   * @param parent the parent Subject element to which to attach generated XML.
   * @param itemLine the input data line from which to parse item information
   *                 for this particular Item.
   */
  private void doItem (Element parent, String itemLine)
    throws IOException
  {
    Element item = addNode(parent, "item"); // create and attach new XML item node

    parseItemLine(item, itemLine);          // parse the fields from the item line

    // handle nested data lines for this Item
    String line = null;
    while ((line = getLine()) != null) {
      if (lineIsData(line))
        parseDataLine(item, line);
      else if (lineIsItem(line)) {
        nextLine = line;
        break;
      }
      else if (lineIsComment(line))         // skip over comment lines
        continue;
      else                                  // exit loop on anything else
        break;
    }
  }


  /**
   * Parse the important fields from the given Item line, create XML
   * for them, and attach the XML to the given parent node.
   *
   * @param parent the parent XML node to which to attach parsed information.
   * @param itemLine the Item line from which to parse information.
   */
  private void parseItemLine (Element parent, String itemLine) {
    Matcher m = item_pat.matcher(itemLine);
    if (m.find()) {
      String inum = m.group(1);             // get the item number
      parent.setAttribute("num", inum);
      String cot = m.group(2);              // get the onset time field
      parent.setAttribute("COT", cot);
    }
  }


  /**
   * Parse the whitespace-separated data fields from the given data line
   * and generate XML for eaach data field.
   *
   * @param parent the parent XML node to which to attach parsed information.
   * @param dataLine the Data line from which to parse data fields.
   */
  private void parseDataLine (Element parent, String dataLine)
    throws IOException
  {
    Element rt = null;
    Matcher m = data_pat.matcher(dataLine);
    int pos = 0;                            // starting index for find

    // find each whitespace-separated data field
    while (m.find(pos)) {
      rt = addNode(parent, "rt");           // create and attach new XML rt node
      String rtype = m.group(2);            // get the response type
      rt.setAttribute("type", rtype);
      String rtime = m.group(1);            // get the response time
      rt.addContent(rtime);
      pos = m.end();                        // reset starting index for next find
    }
  }


  /**
   * Create a new child node with the given name. Attach the new node to
   * the given parent node. The textual content of the new node will be
   * the given value.
   *
   * @param parent the element to become the parent of the new node.
   * @param name the (nonnull!) name string for the new node.
   * @param value the (nonnull!) textual value for the new node.
   */
  private void addChild (Element parent, String name, String value) {
    Element el = new Element(name);
    el.addContent(value);
    parent.addContent(el);
  }


  /**
   * Create a new node: an internal containing element, with the given name.
   * Attach the newly created node to the given parent element and
   * return the newly created node.
   *
   * @param parent the element to become the parent of the new node.
   * @param name the name string for the new node.
   *
   * @return the newly created node.
   */
  private Element addNode (Element parent, String name) {
    Element el = new Element(name);
    parent.addContent(el);
    return el;
  }


  /**
   * Find and return a line from the input data beginning with the given
   * search string.
   *
   * @param srchStr the prefix string to search each line for.
   *
   * @return a line from the input whose prefix matches the given string or
   *         <tt>null</tt> if end-of-file is reached before the search string
   *         is found.
   */
  private String findLine (String srchStr)
    throws IOException
  {
    String line = null;
    while ((line = getLine()) != null) {
      if (line.startsWith(srchStr))
        break;
    }
    return line;
  }


  /**
   * Read and return the next available input line, either from the
   * single line buffer or from the given input data stream.
   *
   * @return a line from the input or <tt>null</tt> if end-of-file is reached.
   */
  private String getLine () throws IOException {
    String line = null;

    // check first for a previously read line
    if (nextLine != null) {                 // if a line was previously pushed back
      line = nextLine;                      // copy pushed back line to local line
      nextLine = null;                      // empty pushed back line buffer
      return line;                          // return the line
    }

    // buffer was empty so read a new line
    while ((line = zin.readLine()) != null) {
      lineNum += 1;
      return line;
    }

    return line;
  }


  /**
   * Tell whether the given line is a comment line or not.
   *
   * @param line the line to be tested
   *
   * @return <tt>true</tt> if the given line is a comment, else <tt>false</tt>.
   */
  private boolean lineIsComment (String line) {
    if ((line == null) || line.equals(""))
      return false;

    return line.startsWith("!");
  }


  /**
   * Tell whether the given line is a 'data line' or not.
   *
   * @param line the line to be tested
   *
   * @return <tt>true</tt> if the given line is a 'data line' or <tt>false</tt>
   *         if the given line is not a 'data line', is <tt>null</tt>, is empty,
   *         or has size less than 2.
   */
  private boolean lineIsData (String line) {
    if ((line == null) || line.equals("") || (line.length() < 2))
      return false;

    char first = line.charAt(0);
    char second = line.charAt(1);
    return ( Character.isDigit(first) || 
             (Character.isWhitespace(first) && Character.isDigit(second)) );
  }


  /**
   * Tell whether the given line is an 'Item line' or not.
   *
   * @param line the line to be tested
   *
   * @return <tt>true</tt> if the given line is an 'item line' or <tt>false</tt>
   *         if the given line is not an 'Item line', is <tt>null</tt>,
   *         or is empty,
   */
  private boolean lineIsItem (String line) {
    if ((line == null) || line.equals(""))
      return false;

    return line.startsWith("Item ");
  }

}
