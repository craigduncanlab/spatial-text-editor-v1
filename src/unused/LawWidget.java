
//import utilities needed for Arrays lists etc
//import

/* This is a class to provide a general data structure for modules/topics in legal docs
The idea is that this can act as a general container for sorts of objects that might be collected together, like topics, definitions, clauses, data
They can be unpacked and inspected as needed, or dropped into a workspace as a group.
They are very close to StackBox, but StackBox is the primary GUI level, and has a label and then data.
TO DO: Decide how much data, and what kind of data this can encapsulate to make it easier to build and work with StackBoxes.

*/

public class LawWidget {
	//setup declare instance variables. shared in class if preceded by static.	
	String Label=""; 
	String Contents=""; 
	String TopicText = "";
	DefContainer TopicDefinitions;

	//empty constructor no arguments
	public LawWidget() {

	}

	//Add the text to this definition object

	public void setLabel(String mytext) {
		this.Label=mytext;
	}

	//Add the label to this definition object

	public void setTopicText(String mytext) {
		this.TopicText=mytext;
	}

	public String getTopicText() {
		return this.TopicText;
	}

	public String getLabel() {
		return this.Label;

	}

}