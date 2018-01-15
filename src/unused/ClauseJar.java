
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 28.11.2017
This will store Clause(s) objects.  Based on DefContainer but will amend.
Should the StackBox be the generic object that can hold Clauses etc?  e.g. it can pull out label and text from a range of objects?
Should there be a super class and then clauses etc extend them?  Should this extend StackBox?
THe intention is to make this available so that it can be dropped into a Topic Object and then opened to inspect clauses of the same topic.
*/

public class ClauseJar {
//setup declare instance variables. shared in class if preceded by static.	
ArrayList<Definition> myDefinitions = new ArrayList<Definition>();  
int numDefs=0; //this will hold number of definitions

//empty constructor no arguments
public ClauseJar() {

}

public void addDef(Definition newDef) {
	this.myDefinitions.add(newDef);
}

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/

public void doPrintIteration() {
	//make the iterator <Definition> type otherwise it defaults to Object
	Iterator<Definition> myiterator = this.myDefinitions.iterator();
	while (myiterator.hasNext()) {
		Definition mydefinition = myiterator.next();
		String mylabel = mydefinition.getLabel();
		String mytext = mydefinition.getDef();
		System.out.println(mylabel+" : "+mytext);
	}
}

/* 
This method returns both labels and text.  It uses the instance container. 
*/

public String getDefAndText() {
	String output="";
	//make the iterator <Definition> type otherwise it defaults to Object
	Iterator<Definition> myiterator = this.myDefinitions.iterator();
	while (myiterator.hasNext()) {
		Definition mydefinition = myiterator.next();
		String mylabel = mydefinition.getLabel();
		String mytext = mydefinition.getDef();
		System.out.println(mylabel+" : "+mytext);
		output=output+mylabel+":\n----------\n"+mytext+"\n\n";
	}
	return output;
}


}