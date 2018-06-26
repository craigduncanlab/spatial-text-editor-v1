import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc
//some other libraries needed for HTML output
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
//
//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
//scene
import javafx.scene.layout.Pane;
//for saving writable images as files
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class makeHTML {

String[] numberLevel = new String[100];

public makeHTML() {
	
}

public void HTMLoutput(ClauseContainer myNode, String filename) {
	//
	cleantemplate(filename);
	//
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getDFS(myNode);
	//System.out.println("Inside HTMLoutput.  The current nodes[1] is :"+myGraphSeq[1].toString());
	System.out.println("container length: "+myGraphSeq.length);
	//structure
	int u=0;
	ClauseContainer thisNode=new ClauseContainer();
	while (myGraphSeq[u]!=null)
	{
		thisNode = myGraphSeq[u];
		System.out.println(u+":"+thisNode.toString());
		thisNode.setNodeRef(0); //reset "Visited"
		writeHTMLFilesOut(u,thisNode,filename);
		//concepts image
		if (thisNode.NodeIsLeaf()==false) {  //has some children so will have a 'concept image'
			saveConceptImage(thisNode,filename);
		}
		System.out.println("Inside HTMLoutput.  The current index is:"+u);
		u++;
	}
	writeHTMLFilesEnd(filename);
	//thisNode.setNodeRef(0);
}

//builds concept image from the node data alone, then saves as image and creates HTML link.
private void saveConceptImage(ClauseContainer myNode,String mainfile) {
	Group myConceptGroup=makeConceptsImage();
	//iterate through node children and call addNodeToConceptsImage() for each
	ArrayList<ClauseContainer> childNodes = myNode.getChildNodes();
    Iterator<ClauseContainer> myiterator = childNodes.iterator();
	//only operates if there are Child Nodes to add
    while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            //System.out.println("Current child node to be added: "+thisNode.toString());
            myConceptGroup=addNodeToConceptsImage(thisNode,myConceptGroup);
        }
    String filename = myNode.getDocName();
	writeSnapshot(filename,myConceptGroup); //present state of Group.
	writeHTMLimage(myNode,mainfile);	
}

//method to write all html output in this branch to same file
//needs to remove the HTMLeditor tags from each record and insert new ones around composite file

private void writeHTMLFilesOut(int index, ClauseContainer myNode, String filename) {
	String reportfile="../html/"+filename+".html";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String replaceString;
	String logString = myNode.getHTML();
	String tempString = logString.replaceAll("<.*html.*>",""); //regEx
	String temp4String = logString.replaceAll("<.*head>",""); //regex
	replaceString = temp4String.replace("<.*body>",""); //String
	
	/*if (myNode.getDepth()<3) {
		logString = "<p><b>"+myNode.getDepth()+"."+myNode.getLevelCount()+" "+myNode.getHeading()+"</b></p>"+replaceString;
	}
	else {
		logString = "<p><b>        "+myNode.getHeading()+"</b></p>"+replaceString;
	}
	*/
	if(index==0) {
	 	logString = "<html><head><title>"+filename+"</title><head>"+"<body>";//+replaceString;
	 	logString=logString+"<p><b>"+myNode.getOutline()+" "+myNode.getHeading()+"</b></p>"+replaceString;
	 }
	 else {
	 	logString = "<p><b>";
	 	logString=logString+myNode.getOutline();
	 	logString=logString+" "+myNode.getHeading()+"</b></p>"+replaceString;
	 }
	myNode.setVisited(false); //reset state before next HTML write/graph traversal
	System.out.print(logString); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	System.out.println("Inside writeHTMLFilesOut.  The current index is:"+index);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

private void writeHTMLFilesEnd (String filename) {
	String reportfile="../html/"+filename+".html";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = "</body></html>";
	System.out.print(logString); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

private void cleantemplate(String filename) {
	String reportfile="../html/"+filename+".html";
	cleanfile(reportfile);
}

private void cleanfile(String reportfile) {
	try {

	PrintWriter pw = new PrintWriter(reportfile);
	pw.close();
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}

}

//make ConceptsImage in its own stage for image purposes (snapshot) only
//currently uses a pane with no group.  No scroller.
private Group makeConceptsImage() {
    //Stage imageStage = new Stage();
    int winWidth=650;
    int winHeight=400;
    Group myConceptGroup = new Group();
    Pane largePane = new Pane();
    largePane.setPrefSize(winWidth, winHeight);
    largePane.getChildren().add(myConceptGroup); //toggle option? 
    //nodes must be in scene to use with WriteableImage snapshot
    Scene myScene = new Scene (largePane,winWidth, winHeight); //default width x height (px)
    //Stage.setScene(myScene); 
    return myConceptGroup;
}

//simply make up a box for each 'node', then add to group and capture that as image
//WritableImage image = root.snapshot(new SnapshotParameters(), null);

private Group addNodeToConceptsImage (ClauseContainer myNode, Group myGroup) {
    SpriteBox b = new SpriteBox(myNode); //evenhandlers not needed
    if (b==null) {
        System.out.println("SpriteBox null in addNodeToConceptsImage");
        System.exit(0);
    }
    myGroup.getChildren().add(b); //GUI tree 
    //System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    //simple update to position or put at origin.  Position is held in data node.
    if (b.getX()!=0 || b.getY()!=0) {
        b.setTranslateX(myNode.getChildNodeX());
        b.setTranslateY(myNode.getChildNodeY());
    }
    else {
        b.setTranslateX(0);
        b.setTranslateY(0); 
    } 
    //setFocusBox(b); //local information
    //b.setStageLocation(StageManager.this); //give Sprite the object for use later.
    return myGroup; //return updated
    }

//method to write the concepts node as an image which will then be used in HTML statements
//get the image
private void writeSnapshot(String filename, Group myGroup) {
	int winWidth=650;
    int winHeight=400;
	try {
	WritableImage image = new WritableImage (winWidth,winHeight); //set this to same as myGroup etc
	SnapshotParameters spa = new SnapshotParameters();
	ImageView view = new ImageView(myGroup.snapshot(spa, image));

	//[The snapshot is the equivalent of something like SwingFXUtils.fromFXImage???]

	//name the output file
	String pathname = "../html/images/"+filename+".png"; //still need extension?
	File file = new File(pathname);
	//save it - may need Buffered Image???
	//RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
	BufferedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
	ImageIO.write(renderedImage,"png",file);
	} catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

private void writeHTMLimage(ClauseContainer myNode, String filename) {
	String localimage = myNode.getDocName();
	String reportfile="../html/"+filename+".html";
	String pathname = "../html/images/"+localimage+".png";

	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = "<p><img src=\""+pathname+"\"><p>";
	System.out.print(logString); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

}


