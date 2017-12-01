/** This application creates a GUI as a legal doc environment
JavaFX implementation of GUI started 17.11.2017 by Craig Duncan
*/
 

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
//Scene graph (nodes)
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
//traversal of scene graph
import javafx.scene.Node; 
import javafx.scene.Parent;
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
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
 //ArrayList etc
 import java.util.*;

 //package classes
 //import WordTool;


/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

*/
public class MainStage extends Application {
    //setup instance variables here.  Static if shared across class (i.e. static=same memory location used)
    //instance variables for Screens to hold them if changed.
    Stage textStage = new Stage(); //basic constructor for main text stage
    Scene MainScene; // scene for adding on textStage.
    Scene boxScene;   //scene for graphic window
    Group boxGroup_root; //root node for graphic window
    ScrollPane scroll_rootNode; //root Node for Text Area
    HBox hbox1; //an hbox to add text and things to!
    HBox hbox3;
    TextArea textArea1 = new TextArea();
    TextArea textArea2 = new TextArea();
    TextArea textArea3 = new TextArea();
    TextArea textArea4 = new TextArea();
    String myTextFile="";
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //Declare any objects for 2nd window here
    Stage visualWindow;
    DefBox littleBox;
    StackBox littleStack;
    Scene graphicscene; //the scene in the second stage (window)
    //inspector window
    Stage inspectorWindow;
    Scene inspectorScene;
    Group inspectorGroup_root;
    TextArea inspectorTextArea = new TextArea();
    //Definitions window
    Stage defWindow;
    Scene defScene;
    Group defGroup_root;
    TextArea defTextArea = new TextArea();

/*The main method uses the launch method of the Application class.
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html
*/


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

private DefContainer grabDefinitionsFile(String fname) {
    WordTool myTool = new WordTool();
    String data = myTool.getFileAsString(fname);
    DefContainer defbox = myTool.doDefTextSearch(data);
    return defbox;
} 

private DefContainer grabDefinitionsString(String mydata) {
    WordTool myTool = new WordTool();
    DefContainer defbox = myTool.doDefTextSearch(mydata);
    return defbox;
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
        textStage.setTitle("Text Working Space");
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int widthcol1=66; //columns? Think of in % terms?
        int widthcol2=33;
        int totalwidth=900; //this is pixels?

        //TO DO:  CONCEPTUALISE WINDOWS/GROUPS TO WORK WITH EACH AS OBJECTS
        
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
        hbox1 = new HBox(0,this.textArea1,this.textArea2);
        HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);
        
        //button for Common Word Counts
        Button btn = new Button();
        btn.setText("Update Word Counts");
        btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
                System.out.println("Word Count Button was pressed!");
                //Outer class method class
                String gotcha = MainStage.this.textArea1.getText();
                String newTA = MainStage.this.getCommonWordsNow(gotcha);
                MainStage.this.textArea2.setText(newTA);
                //new stage
                Stage MainWords = new Stage();
                Group CountGroup_root = MainStage.this.setupChildWindow(MainWords, "The Common Words Window");
                //new one
                //MainStage.this.getMatched(gotcha);

                    //---ADD YELLOW BOXES WITH COMMON WORDS TO GRAPHICS WINDOW---

                    //TO DO: The StackBoxes will be meta-objects include both defs, clause and data.
                    //They should incorporate the text or contents objects so that the GUI can feed this back and forward from text edit windows etc as required.
                    WordTool myHelper = new WordTool();
                    ArrayList<String> boxList = new ArrayList<String>();
                    try {
                    boxList = myHelper.commonBoxSet(gotcha);
                    }
                    catch (Exception e) {
                               e.printStackTrace();
                              } 
                    Iterator<String> i = boxList.iterator();
                    int offX = 0;
                    while (i.hasNext()) {
                        offX=offX+50;
                        StackBox b;
                        if (offX<=100) {
                            b = new StackBox(i.next()); //default blue
                            b.setContent("This is a blue box");
                        } else {
                            b = new StackBox(i.next(), "yellow");
                            b.setContent("This is a yellow box");
                        }
                        b.setTranslateX(offX); //increments offset each time for display. 
                        //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
                        b.setTranslateY(offX);
                        b.setOnMousePressed(PressBoxEventHandler); 
                        b.setOnMouseDragged(DragBoxEventHandler);
                        
                        CountGroup_root.getChildren().add(b);
                    }
            }
        });
        //Button for definitions
        Button btnDefs = new Button();
        btnDefs.setTooltip(new Tooltip ("Press to extract definitions from top text area"));
        btnDefs.setText("Extract Definitions");
        //event handling listener, handle override and outer class method calls
        btnDefs.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
        //Outer class method class
                String gotcha = MainStage.this.textArea1.getText();
                String newDefs = MainStage.this.getMatched(gotcha);
                MainStage.this.textArea3.setText(newDefs);
                //MainStage.this.textArea4.setText(newDefs);
                System.out.println("Get Defs Button was pressed!");
                //MainStage.this.textArea4.setText(newDefs);
    }

        });

        //Button for definitions icons
        Button btnDefIcons = new Button();
        btnDefIcons.setTooltip(new Tooltip ("Press to create definitions icons from top text area"));
        btnDefIcons.setText("Extract Def Icons");
        //event handling listener, handle override and outer class method calls
        btnDefIcons.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
        System.out.println("Get DefIcons Button was pressed!");
        //make a new stage
        Stage adHoc = new Stage();
        defGroup_root = MainStage.this.setupChildWindow(adHoc, "The Definitions Display/Inspector Window");
        DefContainer myContainer = grabDefinitionsString(textArea1.getText());
        ArrayList<Definition> myDList = myContainer.getDefArray();
        Iterator<Definition> myiterator = myDList.iterator();
        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Definition mydefinition = myiterator.next();
            String myLabel = mydefinition.getLabel();
            String mydeftext = mydefinition.getDef();
            String FreqCnt = Integer.toString(mydefinition.getFreq());
            String myCont = myLabel+"("+FreqCnt+")";
            StackBox b;
            if (offY<=100) {
                b = new StackBox(myCont); //default blue
                b.setContent(mydeftext); //to do - transfer defs to sep objects in StackBox
            } else {
                b = new StackBox(myCont, "green");
                b.setContent(mydeftext);
            }
            b.setTranslateX(offX); //increments offset each time for display. 
            //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
            b.setTranslateY(offY);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            defGroup_root.getChildren().add(b);
            if (offX>640) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        adHoc.show();
            }

        });

        //Button for Clause icons
        Button btnClauses = new Button();
        btnClauses.setTooltip(new Tooltip ("Press to work with Clauses"));
        btnClauses.setText("Show Clause Boxes");
        //event handling listener, handle override and outer class method calls
        /* TO DO: Add library of clauses and object types

        btnClauses.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
        System.out.println("Clause Boxes Button was pressed!");
        //make a new stage
        Stage ClauseStage = new Stage();
        Stage adHoc = new Stage();
        ClauseGroup_root = MainStage.this.setupChildWindow(adHoc, "The Clause Window");
        //TO DO: get source of data
        DefContainer myContainer = grabDefinitionsString(textArea1.getText());
        ArrayList<Definition> myDList = myContainer.getDefArray();
        Iterator<Definition> myiterator = myDList.iterator();
        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Definition mydefinition = myiterator.next();
            String myLabel = mydefinition.getLabel();
            String mydeftext = mydefinition.getDef();
            String FreqCnt = Integer.toString(mydefinition.getFreq());
            String myCont = myLabel+"("+FreqCnt+")";
            StackBox b;
            if (offY<=100) {
                b = new StackBox(myCont); //default blue
                b.setContent(mydeftext); //to do - transfer defs to sep objects in StackBox
            } else {
                b = new StackBox(myCont, "green");
                b.setContent(mydeftext);
            }
            b.setTranslateX(offX); //increments offset each time for display. 
            //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
            b.setTranslateY(offY);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            defGroup_root.getChildren().add(b);
            if (offX>640) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        adHoc.show();
            }

        });

        */

        //Set horizontal box for buttons
        hbox3 = new HBox(0,btn,btnDefs,btnDefIcons, btnClauses);
        //put each of our rows into a vertical scroll box
        //VBox vbox2 = new VBox(0,hbox1,hbox2,hbox3);
        //vbox2.getChildren().addAll(hbox1,hbox2,hbox3);
        VBox vbox2 = new VBox(0,hbox1);
        vbox2.getChildren().add(hbox2);
        vbox2.getChildren().add(hbox3);
        
        vbox2.setPrefWidth(totalwidth); //this is in different units to textarea
        /* Lastly, put the Vbox inside a scroll pane 

        /* ---- THE SCROLLPANE IS THE ROOT NODE OF THE SCENE (MAINSCENE) ---
        Make sure it is an instance variable?
        Set the scroll pane width otherwise it will default to some width based on contents of scene, stage 
        
        */
        scroll_rootNode = new ScrollPane();
        scroll_rootNode.setContent(vbox2); 
        //add your parent node to scene.  e.g. you put your vbox2 inside a scroll pane, add the scroll pane.
        this.MainScene = new Scene(scroll_rootNode, 900, 500); //width x height in pixels?  contents have diff sizes
        /*Adding this to avoid consumption of event by child controls i.e. this works first */
        textStage.setX(200);
        textStage.setScene(MainScene);
        //Size and positioning
        textStage.sizeToScene(); 
        textStage.setX(50); 
        textStage.setY(50); 
        
    }

 /* 
 ---- SETUP A NEW STAGE TO DISPLAY BOX OBJECTS--- 
This is a standard size window in a fixed position (no need to pass arguments about size yet)
Maybe have separate function to set size/position after creation.
Return root node (in this case a Group object) to enable addition of further leaf nodes
These are not instance variables.  Consider if necessary on other occasions.
 */

 public Group setupChildWindow(Stage myStage, String myTitle) {
        
        /* MINIMUM SETUP USING GROUP LAYOUT  - WHICH LAYOUT IS MOST APPROPRIATE ?*/
        Group myGroup_root = new Group();
        //add group layout object to scene
        defScene = new Scene (myGroup_root,600,400); //width x height (px)
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
             }
        });
        //add TextArea to group
        myGroup_root.getChildren().add(defTextArea); //std Text Area as default (optional)
        //set some initial content
        defTextArea.setText("Some future contents");

        myStage.setScene(defScene); //this selects the stage as current scene
        myStage.setTitle(myTitle);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        myStage.setY(0);
        myStage.setY(450);
        myStage.show();
        return myGroup_root;

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

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
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
        
        //I will use another Stage as a graphics window
        visualWindow = new Stage();
        Group CommonWords_root = MainStage.this.setupChildWindow(visualWindow, "The Graphics Window");
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        visualWindow.setX(primScreenBounds.getWidth() / 1.5); 
        visualWindow.setY(25);

        //I will use another Stage as an inspector window
        inspectorWindow = new Stage();
        inspectorGroup_root = MainStage.this.setupChildWindow(inspectorWindow, "Inspector Window");
        inspectorWindow.setX(primScreenBounds.getWidth() / 1.5); 
        inspectorGroup_root.getChildren().add(inspectorTextArea);
        inspectorTextArea.setText("Some future contents");
        inspectorTextArea.setWrapText(true);
        
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
            //change colour if double click

            switch(t.getClickCount()){
                case 1:
                    System.out.println("One click");
                    //change colour or something
                    break;
                case 2:
                    System.out.println("Two clicks");
                    //toggle
                    Boolean isAlert = ((StackBox)(t.getSource())).isAlert();
                    if (isAlert==true) {
                        ((StackBox)(t.getSource())).endAlert();
                        //toDO: clear the inspector window contents
                    }
                    else {
                        ((StackBox)(t.getSource())).doAlert();
                        String myOutput = ((StackBox)(t.getSource())).getContent();
                        inspectorTextArea.setText(myOutput);

                    }
                    //where t is the current Stackbox
                    break;
                case 3:
                    System.out.println("Three clicks");
                    break;
            }
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
            System.out.println("The handler for drag box is acting");
            t.consume();//check

        }
    };
}