/** This application creates a GUI with a HelloWorld button, which when pressed, sends "hello world" to the terminal 
All JavaFX applications need a Stage and a Scene
JavaFX implementation of GUI started 17.11.2017 by Craig Duncan
*/
 

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//Scene graph (nodes)
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
//Scene - general appearance & layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
//for scroll panes
 import javafx.scene.control.ScrollPane;
 //package classes
 //import WordTool;


/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

The main method will uses the launch method of the Application class.
see
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html

*/
public class MainStage extends Application {
    //setup instance variables here.  Static if shared across class
    //Stage textStage = null;
    Stage textStage = new Stage(); //basic constructor
    TextArea textArea1 = new TextArea();
    TextArea textArea2 = new TextArea();
    TextArea textArea3 = new TextArea();
    TextArea textArea4 = new TextArea();
    Text graphicBoxText = new Text(); //for label
    String myTextFile="";


public static void main(String[] args) {
        launch(args);
  }

//-- Using instances of WordTool objects ---

private String getTextfromFile(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getFileAsString(fname);
}

private String getMostCommon(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromFile(fname);
}

private void printStatsfromFile(String fname) {
    WordTool myTool = new WordTool();
    myTool.printCountFromFile(fname);
}

//used by event handler
private String getMatched(String data) {
    WordTool myTool = new WordTool();
    DefContainer defbox = myTool.doDefTextSearch(data);
    return defbox.getDefAndText();
                
}
//used by event handler
private String getCommonWordsNow(String data) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromString(data);
}

public void pressMe() {
    System.out.println ("Button pressed - registered with main app");
}


/* Setup text area with blank text to start.  To put default text in at time of constructing,
insert text strings into TextArea arguments
make this public so that the inner class can find it  */

public void setupStage(Stage textStage) {

        StackPane root = new StackPane(); //currently unused.
        //root.getChildren().add(btn);  //we put the button on the StackPane.
        /**create a text area.  if no size in constructor these will fit the scene we create later */
        
        //TextArea textArea = new TextArea();
        //textStage.setScene(new Scene(root, 300, 250));  //this creates scene with button
        //textStage.show(); //only needs to be done once per stage?

        /* Each Stage is given its own 'window'
        If we create more than 1 Stage, the last stage created is initially on top */

        textStage.setTitle("Text Statistics App");
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int widthcol1=66; //columns? Think of in % terms?
        int widthcol2=33;
        int totalwidth=800; //this is pixels?
        
        this.textArea1.setWrapText(true);

        /* Set text area 2 to display some basic stats about the text*/
        this.textArea2.setWrapText(true);
        this.textArea1.setPrefColumnCount(widthcol1); //set max width 
        this.textArea2.setPrefColumnCount(widthcol2); //set max width 
        //
        this.textArea3 = new TextArea();
        this.textArea4 = new TextArea();
        textArea3.setPrefColumnCount(widthcol1); //set max width 
        textArea4.setPrefColumnCount(widthcol2); //set max width 
        //
        TextArea textArea5 = new TextArea();
        TextArea textArea6 = new TextArea();
        textArea5.setPrefColumnCount(widthcol1); //set max width 
        textArea6.setPrefColumnCount(widthcol2); //set max width 
        //button for Word Counts
        Button btn = new Button();
        btn.setText("Update Word Counts");
        //event handling listener, handle override and outer class method calls
        btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
                System.out.println("Word Count Button was pressed!");
                //Outer class method class
                String gotcha = MainStage.this.textArea1.getText();
                String newTA = MainStage.this.getCommonWordsNow(gotcha);
                MainStage.this.textArea2.setText(newTA);
                //new one
                //MainStage.this.getMatched(gotcha);
            }
        });
        //Button for definitions
        Button btnDefs = new Button();
        btnDefs.setText("Extract Definitions");
        //event handling listener, handle override and outer class method calls
        btnDefs.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
                System.out.println("Get Defs Button was pressed!");
                //Outer class method class
                String gotcha = MainStage.this.textArea1.getText();
                String newDefs = MainStage.this.getMatched(gotcha);
                MainStage.this.textArea3.setText(newDefs);
                //MainStage.this.textArea4.setText(newDefs);
            }
        });
        //Set horizontal boxes with spacing and child nodes *i.e. a row 
        HBox hbox1 = new HBox(0,this.textArea1,this.textArea2);
        HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);
        HBox hbox3 = new HBox(0,btn,btnDefs);
        //put each of our rows into a vertical scroll box
        VBox vbox2 = new VBox(0,hbox1,hbox2,hbox3);
        /* An alternative method is like this:
        vbox2.getChildren().addAll(hbox1,hbox2,hbox3);
        */
        vbox2.setPrefWidth(totalwidth); //this is in different units to textarea
        /* Lastly, put the Vbox inside a scroll pane 
        Set the scroll pane width otherwise it will default to some width based on contents of scene, stage */
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(vbox2); 

        //add your parent node to scene.  e.g. you put your vbox2 inside a scroll pane, add the scroll pane.
        Scene scene = new Scene(scroll, 800, 500); //width x height in pixels?
        textStage.setX(200);
        textStage.setScene(scene);
        //optional: 
        textStage.sizeToScene(); 
        //mandatory
        textStage.show();
    }
        /* ---- SETUP A SECOND STAGE FOR SOME VISUAL OUTPUT--- */
        /*  groups have no layout or sizing for children...they have Nodes, positioned at (0,0)
        Group myGroup = new Group(); 
        myGroup.getChildren().addAll(some Nodes here....);
        Might be useful to have a group and add the group to the flow...
        [or just use stackpane?]
        */

public void setupGraphWindow(Stage myStage) {
        //Stage<--Scene<---Group or Layout(Stackpane,Vbox etc)<--Shapes and text {i.e. Leaf-Node objects}
        DefBox littleBox = new DefBox(); //rectangle?
        this.graphicBoxText.setText("Just some dummy text");
        
        //FlowPane myFlow = new FlowPane();  //Use this for master layout
        GridPane myGrid = new GridPane();
        myGrid.setPadding(new Insets(5, 5, 5, 5));
        myGrid.setMinSize(500, 500);
        myGrid.setVgap(5);
        myGrid.setHgap(5);
        StackPane myBoxStack = new StackPane();
        myBoxStack.getChildren().addAll(graphicBoxText,littleBox);
        myGrid.getChildren().add(myBoxStack); //add to the layout object
        //add another child stack (box) to layout object TO DO write function
        StackPane myCloneStack = new StackPane();
        Text myText2 = new Text ("happy Days");
        DefBox myBox2  = new DefBox();
        myCloneStack.getChildren().addAll(myText2, myBox2);
        //add the Second Stack (box) to the same Grid
        myGrid.add(myCloneStack,2,2); //col, row and no children method
        //add layout object to scene
        Scene boxScene = new Scene (myGrid,400,400); //width x height (px)
        //myStage.setScene(boxScene);
        
        myStage.setScene(boxScene);
        myStage.setTitle("The Graphics Window");
        myStage.setX(1000);
        //stage.setWidth(800);
        //stage.setHeight(400);
        myStage.show();
}
/*
public void addLabelBoxToWindow (Stage myStage, String myText) {
        //Stage<--Scene<---Group<--Layout(Stackpane,Vbox etc)<--Shapes and text
        //nb you are working with the 'Scene Graph' to navigate....
        //if you pass Stage in that is an instance object, you get access to the additions?
        //you use the method 'getChildren' on objects to find what's already on them
        DefBox littleBox = new DefBox();
        TextField myTextBox = new TextField(myText);
        Scene myScene = myStage.getChildren();
        myStage.setScene(myScene);
        StackPane stack = new StackPane();
        //stack.getChildren().addAll(littleBox, myTextBox);
        myScene.add(stack);
        }
*/
private void setArea1Text(String fname) {
        //get text from file and put in textarea 1
        String myText=this.getTextfromFile(fname);
        this.textArea1.setText(myText);

}

private void setArea2Text(String fname) {
        //get stats from file and put in textarea 2
        String myStats=this.getMostCommon(fname);
        this.textArea2.setText(myStats);
        //send some stats to console
        this.printStatsfromFile(fname);
}

/*
private String getArea1Text() {
        //get stats from file and put in textarea 2
        return this.textArea1.getText();
        //return myStats;
}
*/

/** OVERRIDE
*/
  
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Utilities");
        //I'm going to try out a few of the cool JavaFX controls
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        //this registers an event handler for button press ("ActionEvent")
        btn.setOnAction(new EventHandler<ActionEvent>() {
            //not sure why this @Override was here
            //@Override 
            //This is the event handler method
            public void handle(ActionEvent event) {
                System.out.println("The Text Area Button was pressed!");
            }
        });
        primaryStage.show();

        //use this object to setup the Stage for main use
        //TO DO: Setup a secondary 'Stage' for file input, creation of toolbars etc.
        this.myTextFile="popstarlease.txt";
        Stage myStage = new Stage();
        this.setupStage(myStage);
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        /* This only affects the primary stage set by the application */
        primaryStage.close();
        Stage visualWindow = new Stage();
        this.setupGraphWindow(visualWindow);
        //this.addLabelBoxToWindow(visualWindow, "help!");
        
    }
}