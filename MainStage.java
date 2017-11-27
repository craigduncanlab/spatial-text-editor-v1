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
 //for Mouse Click and Drag
 import javafx.scene.input.MouseEvent;
 import javafx.scene.Cursor;

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
    //setup instance variables here.  Static if shared across class (i.e. static=same memory location used)
    //Stage textStage = null;
    Stage textStage = new Stage(); //basic constructor
    TextArea textArea1 = new TextArea();
    TextArea textArea2 = new TextArea();
    TextArea textArea3 = new TextArea();
    TextArea textArea4 = new TextArea();
    Text graphicBoxText = new Text(); //for label
    String myTextFile="";
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //Declare any objects for 2nd window here
    Stage visualWindow;
    DefBox littleBox;
    StackBox littleStack;
    Scene graphicscene; //the scene in the second stage (window)


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

        //This is the stage to be used but is not JavaFX default
        textStage.setTitle("Text Statistics App");
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int widthcol1=66; //columns? Think of in % terms?
        int widthcol2=33;
        int totalwidth=900; //this is pixels?
        
        this.textArea1.setWrapText(true);
        this.textArea1.setPrefColumnCount(widthcol1); //set max width 
        /* Set text area 2 to display some basic stats about the text*/
        this.textArea2.setWrapText(true);
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
        
        //Set horizontal boxes with spacing and child nodes *i.e. a row 
        HBox hbox1 = new HBox(0,this.textArea1,this.textArea2);
        HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);
        
        //button for Word Counts
        Button btn = new Button();
        btn.setText("Update Word Counts");
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

        //Set horizontal box for buttons
        HBox hbox3 = new HBox(0,btn,btnDefs);
        //put each of our rows into a vertical scroll box
        //VBox vbox2 = new VBox(0,hbox1,hbox2,hbox3);
        //vbox2.getChildren().addAll(hbox1,hbox2,hbox3);
        VBox vbox2 = new VBox(0,hbox1);
        vbox2.getChildren().add(hbox2);
        vbox2.getChildren().add(hbox3);
        
        vbox2.setPrefWidth(totalwidth); //this is in different units to textarea
        /* Lastly, put the Vbox inside a scroll pane 
        Set the scroll pane width otherwise it will default to some width based on contents of scene, stage */
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(vbox2); 
        //add your parent node to scene.  e.g. you put your vbox2 inside a scroll pane, add the scroll pane.
        Scene scene = new Scene(scroll, 900, 500); //width x height in pixels?  contents have diff sizes
        /*Adding this to avoid consumption of event by child controls i.e. this works first */
        textStage.setX(200);
        textStage.setScene(scene);
        //optional: 
        textStage.sizeToScene(); 
        
    }
        /* ---- SETUP A SECOND STAGE FOR SOME VISUAL OUTPUT--- 
        Layout options include Group, GridPane, FlowPane, AnchorPane etc.
        Groups have no layout or sizing but are possibly better for absolute positioning.
        */

public void setupGraphWindow(Stage myStage) {
        //Stage<--Scene<---Group or Layout(Stackpane,Vbox etc)<--Shapes and text {i.e. Leaf-Node objects}
        //define littleBox in start method rather than here...
        this.graphicBoxText.setText("Just some dummy text");
        
        /* USING FLOWPANE
        FlowPane myFlow = new FlowPane();  
        */
        
        /* USING GRIDPANE 
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
        */

        /* USING GROUP LAYOUT */
        Group myGroup = new Group();
        
        //First box in window.  Each box object is a ready Stackpane with 2 child nodes
        littleStack = new StackBox("Some new text");
        littleStack.setTranslateX(300); //was 300
        littleStack.setTranslateY(100);//was 100
        littleStack.setOnMousePressed(PressBoxEventHandler); 
        littleStack.setOnMouseDragged(DragBoxEventHandler);

        StackBox myBox2 = new StackBox("the 2nd box","green");
        myBox2.setTranslateX(100);
        myBox2.setTranslateY(200);
        myBox2.setOnMousePressed(PressBoxEventHandler); 
        myBox2.setOnMouseDragged(DragBoxEventHandler);

        //add boxes to group
        myGroup.getChildren().addAll(myBox2,littleStack);
        
        //add layout object to scene
        Scene boxScene = new Scene (myGroup,400,400); //width x height (px)
        
        //detects mouse click anywhere in graphics window scene
        boxScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("mouse click detected! " + mouseEvent.getSource());
             }
        });
        myBox2.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("mouse click detected for box2! " + mouseEvent.getSource());
             }
        });

        littleStack.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("mouse click detected for littleStack! " + mouseEvent.getSource());
             }
        });

        myStage.setScene(boxScene);
        myStage.setTitle("The Graphics Window");
        myStage.setX(1000);
        //stage.setWidth(800);
        //stage.setHeight(400);
        myStage.show();
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
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("File Utilities");
        //primaryStage.show();
        primaryStage.close();
       
        //*Stage that I will use for main text display and editing
        Stage myStage = new Stage();
        this.setupStage(myStage);
        //set some default text in main text window
        this.myTextFile="popstarlease.txt";
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        myStage.show();
        
        //Stage I will use as a graphics window
        visualWindow = new Stage();
        this.setupGraphWindow(visualWindow);
        visualWindow.show();
        
        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.

    }



    /* This is a method to create a new eventhandler for the StackBox objects which are themselves a Stackpane that incorporate a Rectangle and a Text Node as components*/

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            // If you are only moving child objects not panes
            orgTranslateX = ((StackBox)(t.getSource())).getTranslateX();
            orgTranslateY = ((StackBox)(t.getSource())).getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);
            t.consume(); //trying this to see if it frees up for second press but better to deal with cause
        }
    };
    
     /* This is a method to create a new eventhandler for the StackBox objects */
     /* These currently have no limits on how far you can drag */

    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            ((StackBox)(t.getSource())).setTranslateX(newTranslateX);
            ((StackBox)(t.getSource())).setTranslateY(newTranslateY);
            System.out.println("Handling drag");
            t.consume();//check

        }
    };
}