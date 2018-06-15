import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


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
		System.out.println("Inside HTMLoutput.  The current index is:"+u);
		u++;
	}
	writeHTMLFilesEnd(filename);
	//thisNode.setNodeRef(0);
}

//method to write all html output in this branch to same file
//needs to remove the HTMLeditor tags from each record and insert new ones around composite file

private void writeHTMLFilesOut(int index, ClauseContainer myNode, String filename) {
	String reportfile="../templates/"+filename+".html";
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
	String reportfile="../templates/"+filename+".html";
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
	String reportfile="../templates/"+filename+".html";
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
}

