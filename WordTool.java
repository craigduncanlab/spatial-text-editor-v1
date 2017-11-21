//By Craig Duncan.  Created 19 November 2017
/* Tool to help with statistical Frequency Counts on text e.g. legal document

It turns out that the kind of document can usually be predicted by these factors:
The names of the parties, and the document title are in the top 3 most frequent words?
If we have a Lease, we can see if the occurrence of Shopping & Centre is present and frequent
(that will allow classification just using decision trees, without much sophistication)
Having classified the document, the system can then prompt from the sample clauses/alternates.
*/

//make sure package has DefsContainer and Definition classes

import java.util.*;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
//for pattern matching:
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordTool {
	 //setup (declare) instance variables.  Need the qualifying word 'static' to share one for whole class.
	String filename = ""; //optional filename to use for the WordTool
  int mostCommonLimit=5;

//constructor method with argument
public WordTool (String fname) {
 	this.filename=fname;
}
//constructor no arguments
public WordTool () {
  
}

private TreeMap<Integer, String> makeMapFromStringCounts(String inputMe) {
    //map structure for counting
    Map<String, Integer> map = new HashMap<String, Integer>();
    //use function String as input for inner Scanner
    Scanner linescan = new Scanner(inputMe);
    //
    linescan.useDelimiter("<tr>[^<]|<tc>|[^\\p{L}0-9']+");
          while (linescan.hasNext()) {
             String thisEntry=linescan.next();
             //No output needed.  System.out.println(thisEntry);
             if (map.containsKey(thisEntry)) {
              int tv = map.get(thisEntry);
              tv++;
              map.put(thisEntry,tv);
             }
             else {
              map.put(thisEntry,1);
             }
          }
          linescan.close();    
      //create a new hashmap with k, v reversed, then Treemap it
        HashMap<Integer, String> myNewHashMap = new HashMap<>();
    for(Map.Entry<String, Integer> entry : map.entrySet()){
      myNewHashMap.put(entry.getValue(), entry.getKey());
      }
      //Use TreeMap for count output
      TreeMap<Integer, String> newTreeMap = new TreeMap<>(myNewHashMap);
      return newTreeMap;
}

/* method to trim String
@parameter excludedWords is excluded words that are common filler words will be trimmed
These words tell you something about the language of lawyers too.
@return trimmedTreeMap as trimmed TreeMap.  Notice it is in reverse order so largest counts at top.
TO DO: put in external Dictionary; possibly deal with phrases rather than words alone
Also - not using double brace initialisation of ArrayList at this stage.
*/

private TreeMap<Integer, String> trimTreeMap(TreeMap<Integer, String> myTMap) {
      ArrayList<String> excludedWords = new ArrayList<String>(Arrays.asList("amount","arising","before","between","clause","conditions","connection","cost","costs","date","from","including","means","must","notice","proposed","provisions","reasonable","reasonably","relevant","respect","only","other","payment","specified","terms","that","this","than","time","under","which","will","with","written"));
      TreeMap<Integer, String> trimmedTreeMap = new TreeMap<Integer, String>(Collections.reverseOrder());
      myTMap.forEach((k,v)->
        {
          //introduce threshold for results. e.g. 5+ results, length of three or more.  Not excluded.
          if ((k>5 && v.length()>3) && (excludedWords.contains(v)!=true)) {
            trimmedTreeMap.put(k,v);
        }
    }
    );
    return trimmedTreeMap;
}

/* Print TreeMap.  No trimming here.  Do prior to method call */

private void printTreeMap(TreeMap<Integer, String> myTMap) {

      myTMap.forEach((k,v)->
        {
          System.out.println(k + " : " + v);
    }
    );
}

/* return the most common entries of TreeMap.  Assumes descending order (i.e. reverse sorted) 
Use Generics here <> and not Raw Types
*/

private void displayMostCommon(TreeMap<Integer, String> myTMap) {
  System.out.println("Top "+Integer.toString(mostCommonLimit)+" Frequency Count:\n ------------------------ \n");
  Set<Map.Entry<Integer,String>> set = myTMap.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Display elements
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      System.out.println(me.getKey() + " : "+ me.getValue());
    }
}

/* Makes a string with most common words in file, excluding fill words
It uses the Collections Iterator class method 'next', on the set of Map entries.
It uses the Map.Entry methods getKey() and getValue() to extract items.
All objects use the specific TreeMap data types, not raw types.

Uses 'while' rather than 'for' to ensure there are sufficient words to report on 
*/

private String getMostCommonFromMap(TreeMap<Integer, String> myTMap) {
    String output="";
    Set<Map.Entry<Integer,String>> set = myTMap.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Construct string
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      output=output + me.getKey() + " : " + me.getValue() + "\n"; //line return
    }
    return output;
}

private String readFile(String fname) {
      String output="";
      Scanner sc = null;
      File myFile = new File(fname);
       //you only need try and catch if the Scanner argument is a disk file.
       try {
        sc = new Scanner(myFile); 
       }
       catch (Exception e) {
                   e.printStackTrace();
                  } 
       while (sc.hasNextLine()) {
       String S = sc.nextLine();
       output = output + S;
       }
       return output;

}

/*  Method to find specific parts of the string, in turn
    
    Explanation: 
    Finds one or more word characters between the quotes then the word 'means'
    misses the first definition because there is a quote missing 
    The quantifiers * = zero or more and + = one or more times
    So this checks for possible preceding words and spaces, but optional
    This would check for up to 3 terms in definition:
    Pattern p = Pattern.compile("\\\"(\\w* *\\w* *\\w+)\\\" means{1}");
    */

public String printMatchedDefs (String mydata) {
    String output="";
    //This one checks for any number of words followed by spaces then a word end quote:
    //Brackets that aren't escaped are used by the regexp pattern. \w is word character
    //quantifiers include * for 0 or more, + for one or more
    Pattern p = Pattern.compile("\\\"((\\w* *)*\\w+)\\\" means{1}");
    //if I make the means match once with {1} it helps to short-circuit missing quotes
    Matcher matcher = p.matcher(mydata);
    int matchCount=0;
    while (matcher.find())
        {
         System.out.println(matcher.group(1)); 
         //if you use group(1) you limit output to something identified with () inside the pattern
         //0 = first group, 1 = 2nd etc
         matchCount++;
         output=output+"\n"+matcher.group(1); //TO DO: use Definitions object
        }
        System.out.println(matchCount+" matches \n");  
        //TO DO: store this.
        //definitions object?  i.e. definitions stored in an array as data type?
        //read off from file, then store current set of definitions as an object?
        //TO DO 2: strip off the text of the definition at the same time, store in definitions.
        return output;
  }

  /*
  method to extract Definitions from string, make a Definition object for each Definition and store in a DefContainer 
  */

  public DefContainer makeDefsCollection(String mydata) {
    DefContainer myContainer = new DefContainer();
    String output="";
    //This one checks for any number of words followed by spaces then a word end quote:
    //Brackets that aren't escaped are used by the regexp pattern. \w is word character
    //quantifiers include * for 0 or more, + for one or more
    Pattern p = Pattern.compile("\\\"((\\w* *)*\\w+)\\\" means{1}");
    //if I make the means match once with {1} it helps to short-circuit missing quotes
    Matcher matcher = p.matcher(mydata);
    int matchCount=0;
    while (matcher.find())
        {
         System.out.println(matcher.group(1)); 
         //if you use group(1) you limit output to something identified with () inside the pattern
         //0 = first group, 1 = 2nd etc
         matchCount++;
         output=output+"\n"+matcher.group(1); //TO DO: use Definitions object
         Definition myDef  = new Definition();
         myDef.setDeflabel(matcher.group(1));
         myContainer.addDef(myDef);
        }
        System.out.println(matchCount+" matches \n");  
        System.out.println("Test iteration");
        myContainer.doPrintIteration();
        //TO DO: store this.
        //definitions object?  i.e. definitions stored in an array as data type?
        //read off from file, then store current set of definitions as an object?
        //TO DO 2: strip off the text of the definition at the same time, store in definitions.
        return myContainer;
  }

//for other methods to call these are public methods
public void printCountFromFile(String fname) {
  String data = this.readFile(fname);
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  printTreeMap(trimTreeMap(dataTM));
  displayMostCommon(trimTreeMap(dataTM));

}

public String getCommonWordsFromFile(String fname) {
  String data = this.readFile(fname);
  return getCommonWordsFromString(data);
}

public String getFileAsString(String fname) {
  String data = this.readFile(fname);
  return data;
}

public static void main(String args[]){
		/* This is needed if running as a single instance on object creation
    WordTool bob = new WordTool();
		String data = bob.readFile("popstarlease.txt");
    bob.makeMapFromStringCounts(data);
    */
}

public String getCommonWordsFromString(String data) {
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  return getMostCommonFromMap(trimTreeMap(dataTM));
}

}
