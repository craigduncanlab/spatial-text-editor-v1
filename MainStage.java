/** This application creates a GUI with a HelloWorld button, which when pressed, sends "hello world" to the terminal 
All JavaFX applications need a Stage and a Scene
JavaFX implementation of GUI started 17.11.2017 by Craig Duncan
*/
 

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
//these last 2 are for textarea
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
    String myTextFile="";


public static void main(String[] args) {
        launch(args);
  }

//--Uses of the Word Tools ---

private String getTextfromFile(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getString(fname);
}

private String getMostCommon(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getStringMostCommon(fname);
}

private void getStatsfromFile(String fname) {
    WordTool myTool = new WordTool();
    myTool.getCount(fname);
}



/* Setup text area with blank text to start.  To put default text in at time of constructing,
insert text strings into TextArea arguments */

private void setupStage(Stage textStage) {

StackPane root = new StackPane();
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
        TextArea textArea3 = new TextArea();
        TextArea textArea4 = new TextArea();
        textArea3.setPrefColumnCount(widthcol1); //set max width 
        textArea4.setPrefColumnCount(widthcol2); //set max width 
        //
        TextArea textArea5 = new TextArea();
        TextArea textArea6 = new TextArea();
        textArea5.setPrefColumnCount(widthcol1); //set max width 
        textArea6.setPrefColumnCount(widthcol2); //set max width 
        //Set horizontal boxes with spacing and child nodes *i.e. a row 
        HBox hbox1 = new HBox(0,textArea1,textArea2);
        HBox hbox2 = new HBox(0,textArea3,textArea4);
        //put each of our rows into a vertical scroll box
        VBox vbox2 = new VBox(0,hbox1,hbox2);
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
        textStage.setScene(scene);
        //optional: 
        textStage.sizeToScene(); 
        //mandatory
        textStage.show();
}

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
        this.getStatsfromFile(fname);
}

/** OVERRIDE
*/
  
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Utilities");
        //I'm going to try out a few of the cool JavaFX controls
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
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
        
    }
}