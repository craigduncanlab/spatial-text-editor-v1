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

private TreeMap<Integer, String> makeMapFromDefinitions (DefContainer myContainer, String inputMe) {
    //map structure for counting
    Map<String, Integer> map = new HashMap<String, Integer>();
    //use function String as input for inner Scanner
    Scanner linescan = new Scanner(inputMe);
    //line delimeters: not POSIX {Letter} or numerals 0 to 9 or apostrophe
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


private TreeMap<Integer, String> makeMapFromStringCounts(String inputMe) {
    //map structure for counting
    Map<String, Integer> map = new HashMap<String, Integer>();
    //use function String as input for inner Scanner
    Scanner linescan = new Scanner(inputMe);
    //line delimeters: not POSIX {Letter} or numerals 0 to 9 or apostrophe
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
          //introduce threshold for results. e.g. 5+ results, length of 4 or more.  Not excluded.
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
      output=output + me.getKey() + " : " + me.getValue() + "\n"; //line return (UNIX Style x0A)
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
       output = output + S + "\n"; //\ r inserts a \x0D: to preserve LF (as x0A) use \n
       }
       return output;

}

/*  Method to find specific parts of the definition - label and text and store all in a container
Checks for a post-text definition i.e. "A" means B.
catch unicode hyphen and line returns and quotes after 'means' 
TO DO: Capture this in-text definition pattern as an alternative:
[this|the|a] ... ("definition")
    */


  public DefContainer doDefTextSearch(String mydata) {
    DefContainer myContainer = new DefContainer();
    String output="";
    
    //This works for quoted definitions only:
    //OLD: Pattern p = Pattern.compile("\\\"(([\\w\\’' ]*)*[\\w\\’']+)\\\" means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.");
    String Uni_dbl_qt = "[\\u201c\\u201d\\u201e\\u201f\\\"]"; //not using \\x22 for now
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dash = "\\u2013";
    String myRE=Uni_dbl_qt+"(([\\w\\’' ]*)[\\w\\’']+)"+Uni_dbl_qt+" means[\\-:, ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    Pattern p = Pattern.compile(myRE);
    Matcher matcher = p.matcher(mydata);
    int matchCount=0;
    while (matcher.find())
        {
         System.out.println(matcher.group(1)+" group 2:"+matcher.group(2)+" group 3:"+matcher.group(3));
         matchCount++; //no longer needed unless output
         Definition myDef  = new Definition();
         myDef.setDeflabel(matcher.group(1));
         myDef.setDeftext(matcher.group(3));
         myContainer.addDef(myDef);
        }
    //iterate again and update the frequency of use of Defs
    //TO DO : Make a hash map instead of arraylist with the definition label and the def object?
    ArrayList<Definition> myDList = myContainer.getDefArray();
    Iterator<Definition> myiterator = myDList.iterator();
      while (myiterator.hasNext()) {
        Definition mydefinition = myiterator.next();
        String myLabel = mydefinition.getLabel();
        //String mytext = mydefinition.getDef();
        Pattern pd = Pattern.compile(myLabel);
        Matcher checkDefs = pd.matcher(mydata);
         while (checkDefs.find())
         {
           mydefinition.incFreq();
         }
         String FreqCnt = Integer.toString(mydefinition.getFreq());
         //OK: System.out.println(myLabel+" : "+FreqCnt);
        }    
    return myContainer;
  }

/*  Method to find clauses and store them in clause objects
    Assumes headings are capital letters on a single row
    
*/

  public ClauseContainer ClauseCapHeadingExtract(String mydata) {
    
    //Pattern p = Pattern.compile("[[a-z][0-9]\\<\\>]*([A-Z' ]{2,}[A-Z'](?=\\x0A|\\x0d))");
    ClauseContainer myContainer=null;
    String[] patternString = new String[3];
    Integer[] groupIn = new Integer[3];
    //setup
    int numPatterns=3;
    patternString[0]="[[a-z][0-9]\\<\\>]*([A-Z']{1,}( )*[A-Z']*(?=\\x0A|\\<))";
    groupIn[0]=1;
    patternString[1]="(\\x0A)([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[1]=2;
    patternString[2]="(\\x0A)*([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[2]=2;

    for (int sIndex=0;sIndex<numPatterns;sIndex++) {
    Pattern p = Pattern.compile(patternString[sIndex]);
    myContainer = new ClauseContainer();
    String output="";
    System.out.println("pattern matcher set");
    Matcher matcher = p.matcher(mydata);
    int groupCount = matcher.groupCount();
    System.out.println("Groupcount : "+groupCount);
    int matchCount=0;
    ArrayList<String> myClauseList = new ArrayList<String>();
    while (matcher.find())
          {
            for (int i = 1; i <= groupCount; i++) {
                // Group i substring
                System.out.println("Group " + i + ": " + matcher.group(i));
          }
         int x = groupIn[sIndex];
         matchCount++; //no longer needed unless output
         if (!matcher.group(x).equals("") && !matcher.group(x).equals(" ")) {
             Clause myC  = new Clause();
             myC.setClauselabel(matcher.group(x));
             myC.setHeading(matcher.group(x));
             myClauseList.add(matcher.group(x));
             myContainer.addClause(myC);
          }
        }
    System.out.println("Finished Cap Heading search");
    System.out.println("# of Headings Found: "+myClauseList.size()); 
    if (myClauseList.size()>4) {
      return this.ClauseTextExtract(myContainer, mydata);
    }
    }
    //populate clause text before returning
    return this.ClauseTextExtract(myContainer, mydata);
    }

/* 

Method to check results of heading extraction exercise
For now, it just checks whether there are few results. 
TO DO: Check whether Clause headings include the most common words etc and report on that

*/
public Boolean checkHeadingExtraction(ClauseContainer myContainer) {
    ArrayList<Clause> myCList = myContainer.getClauseArray();
    if (myCList.size()<2) {
      return false;
    }
    else {
      return true;
    }

}
    /* Method to populate clause text from Clause Headings */
    
    public ClauseContainer ClauseTextExtract(ClauseContainer myContainer, String mydata) {
    System.out.println("Clause text extract");
    ArrayList<Clause> myCList = myContainer.getClauseArray();
    Iterator<Clause> myiterator = myCList.iterator();
    System.out.println("Array Size: "+myCList.size()); //conveniently ArrayList is in Collections with a size method
    if (myiterator!=null) {
    String[] indexedList = new String[150];
    indexedList[0] = "";
    //String UpperWord="";
    String myRegEx = "";
    String LooseRegEx="([\\w\\d\\s\\(\\)\\:\\-\\;\\,\\.\\/\\’'\\<\\>\\[\\]\\u201c\\u201d\\u2013\\u2019\\x0d\\x0a\\\" ]*)";
    String LowerWord="lorem ipsum";
    int indexWindow=0;
    Clause FirstClause=null;
    Clause UpperClause=null;
    Clause LowerClause=null;
    if (myiterator.hasNext()) {
      System.out.println("First has next");
      FirstClause=myiterator.next();
      UpperClause = FirstClause;
      LowerClause = FirstClause;
    }
    while (myiterator.hasNext()) {
        //System.out.println("Inner has next");
        if (indexWindow>0) {
         UpperClause = LowerClause;
        }
        LowerClause = myiterator.next();
        String UpperWord = UpperClause.getHeading();
        LowerWord = LowerClause.getHeading();
        //u2010-u201F is a good range for UTF8
        myRegEx="(?="+UpperWord+")"+LooseRegEx+"(?="+LowerWord+")";
        System.out.println("Now matching: "+myRegEx+" on "+UpperWord);
        Pattern pcl = Pattern.compile(myRegEx);
          //System.out.println(pcl.pattern());
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(1));
            UpperClause.setClausetext(clauseCaptcha.group(1));
          }
          indexWindow++;  
        } 
        //pickup the clause text for last match to end of data String
        myRegEx="(?="+LowerWord+")"+LooseRegEx;
        System.out.println("Now matching: "+myRegEx+" on "+LowerWord);
        Pattern pcl = Pattern.compile(myRegEx);
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(1));
            LowerClause.setClausetext(clauseCaptcha.group(1));
          } 
        } 
      return myContainer;
    }

//for other methods to call these are public methods
public void printCountFromFile(String fname) {
  String data = this.readFile(fname);
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  printTreeMap(trimTreeMap(dataTM));
  displayMostCommon(trimTreeMap(dataTM));

}

public ArrayList commonBoxSet(String data) {
  ArrayList<String> output = new ArrayList();
  //String data = this.readFile(fname);
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  TreeMap<Integer,String> shortList = trimTreeMap(dataTM);
  //iterator on entry set generated by Map
    Set<Map.Entry<Integer,String>> set = shortList.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Construct string
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      output.add(me.getValue());
      //output=output + me.getKey() + " : " + me.getValue() + "\n"; //line return (UNIX Style x0A)
    }
    return output;
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
