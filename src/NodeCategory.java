import java.util.*;

public class NodeCategory implements java.io.Serializable {
//6.4.18 - setup class to record node classifier	

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -64787598237212345L;

int nodelevel = 0;
String nodecategory = "";
String colour = "";
StageManager nodeCatViewer;
int doccount=0;
//put document counter inside each node category object


public NodeCategory() {
	
}

public NodeCategory(String Category, int level, String Col) {
	setCategory(Category);
	setLevel(level);
	setColour(Col);
}

public void setLevel(int myLevel) {
	this.nodelevel = myLevel;
}

public int getLevel() {
	return this.nodelevel;
}

public void setCategory(String myCat) {
	this.nodecategory = myCat;
}

public String getCategory() {
	return this.nodecategory;
}

public void setCatViewer(StageManager myView) {
	this.nodeCatViewer = myView;
}

public StageManager getCatViewer() {
	return this.nodeCatViewer;
}

public void setColour(String col) {
	this.colour = col;
}

public String getColour() {
	return this.colour;
}

public int getDocCount() {
    return this.doccount;
}

public void resetDocCount() {
    this.doccount=0;
}

public int advanceDocCount() {
    return this.doccount++;
}

}