import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap, Array,ArrayList etc


public class GraphUtil {

//for queue
ArrayList<ClauseContainer> myQueue = new ArrayList<ClauseContainer>();
//for node indexing
int nodeSeq=0;
ClauseContainer[] myGraphSeq = new ClauseContainer[300];
//
String fileOutput=""; //use stringbuffer


public GraphUtil() {
	
}

/*
private ClauseContainer[] makeGraphArray(HashMap myMap) {
	Integer[] keys = new Integer[myMap.size()];
	ClauseContainer[] values = new ClauseContainer[myMap.size()];
	int index = 0;
	for (Map.Entry<Integer,ClauseContainer> mapEntry : myMap.entrySet()) {
	    keys[index] = mapEntry.getKey();
	    values[index] = mapEntry.getValue();
	    //write out to file
	    index++;
	}
	return values;
}

//write output
private void writeGraphArray(HashMap myMap) {
	
	for (Map.Entry<Integer,ClauseContainer> mapEntry : myMap.entrySet()) {
	    Integer nodeIndex = mapEntry.getKey();
	    ClauseContainer myNode = mapEntry.getValue();
	    //write out to file
	}
}
*/
//main method to obtain list of graph nodes in sequence

public ClauseContainer[] getGraphNodeOrder(ClauseContainer myNode) {
	updateNodeRefs(myNode);
	return this.myGraphSeq;
}

private void updateNodeRefs(ClauseContainer myRootNode) {
	nodeSeq=0;
	myRootNode.setNodeRef(0);
	this.myQueue.add(myRootNode);
	this.myGraphSeq[0]=myRootNode;
	nodeSeq++;
	//
	while(this.myQueue.isEmpty()==false) {
		ClauseContainer firstNode = this.myQueue.get(0);
		addChildrenQueue(firstNode); //add children to Queue
		this.myQueue.remove(firstNode); //remove from Queue
	}
}

//put children on queue and give them a node index
private void addChildrenQueue(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return;
	}
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		this.myQueue.add(nextNode);
		nextNode.setNodeRef(this.nodeSeq);
		this.myGraphSeq[this.nodeSeq]=nextNode; //could use .add but this ensures index is aligned?
		this.nodeSeq++;
		}
	}
}