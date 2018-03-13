//import utilities needed for Arrays lists etc
import java.util.*;

/* By Craig Duncan 13.3.2018 
Aim is to create a Container to store 'SpriteBox' objects on Stage
To then use for load/save functions for serialization.
Depends on SpriteBox class.

*/

public class BoxContainer {
//setup declare instance variables. shared in class if preceded by static.	
ArrayList<SpriteBox> myBoxes = new ArrayList<SpriteBox>();  
int numClauses=0; //this will hold number of clauses

//empty constructor no arguments
public BoxContainer() {

}

//uses Java collections property for the .add method
public void addBox(SpriteBox newBox) {
	this.myBoxes.add(newBox);
}

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/


public ArrayList<SpriteBox> getBoxArray() {
	return this.myBoxes;
}

/* method to return number of SpriteBoxes in the Container */

public int getNumBoxes() {
	return this.myBoxes.size();
}

//TO DO: Methods to delete or remove boxes

}