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
method to obtain list of graph nodes in sequence, starting with the selected node/box
*/

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

//put child nodes on queue and give them a node index
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