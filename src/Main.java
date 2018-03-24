/** 

This application creates a GUI as a legal doc staging, editing & visualisation environment

JavaFX implementation of GUI started 17.11.2017 by Craig Duncan

*/
 

import javafx.application.Application;
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
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
//Scene - general appearance & layout of Stages, nodes
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
//Scene colour
import javafx.scene.paint.Color;

//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//ArrayList etc
import java.util.*;
//For serialization IO (saving BoxContainer as flat data file)
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
//File chooser
import javafx.stage.FileChooser;
//key events
import javafx.scene.input.KeyEvent;

/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

usage:
From powerdock folder:
javac -d classes ./src/*.java
java -cp :classes Main

From classes folder:
javac -d ../classes ../src/*.java
java Main


*/
public class Main extends Application {
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
    //General managers
    SpriteManager mySpriteManager;
    StageManager myStageManager = new StageManager();
    ControlsManager myControlsManager = new ControlsManager();
    //Main Stage (Workspace window) that owns all other Stages
    Stage ParentStage;
    Group WorkspaceGroup;
    BoxContainer WorkspaceBoxes; //A serializable top-level container (optional)
    //Main Collections for Clauses
    ClauseContainer WorkspaceClauseContainer = null; //workspace
    ClauseContainer LibraryClauseContainer = null; //library import/save
    //importStage
    Stage importStage;

    //textmaker window (no edits)
    Stage textmakerWindow;
    Scene textmakerScene;
    ScrollPane textmakerGroup_root;
    TextArea textmakerTextArea = new TextArea();
    //Display SpriteBoxes window(s)
    Scene defScene;  //<----used multiple times in different methods.  TO DO:  localise Scene variables.
    Group defGroup_root; //<---used for display Sprites in new stage
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Toolbar
    Stage toolbarStage = null;
    Group toolbarGroup = null;
    Scene toolbarScene = null;   
    //Clause editor
    TextArea labelEdit;
    TextArea headingEdit;
    TextArea textEdit;
    TextArea categoryEdit;
    TextArea dateEdit;
    Clause editClause;
    Event editEvent;
    //Group editGroup_root;
    Stage editorStage;
    Pane editGroup_root;
    //Library Window (as needed)
    Stage LibraryStage=null;
    Group LibraryGroup;
    String LibFilename="library.ser";

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

private ClauseContainer grabDefinitionsFile(String fname) {
    WordTool myTool = new WordTool();
    String data = myTool.getFileAsString(fname);
    ClauseContainer defbox = myTool.doDefTextSearch(data);
    return defbox;
}

private ClauseContainer extractDefinitionsFromSampleString(String mydata) {
    WordTool myTool = new WordTool();
    ClauseContainer defbox = myTool.doDefTextSearch(mydata);
    return defbox;
} 

//return a ClauseContainer object with clauses after using text document as input

private ClauseContainer extractClausesFromSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    ClauseContainer clauseCarton = myTool.ClauseImport(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 

//extractStatuteSectionsFromSampleText(textArea1.getText());
//return a ClauseContainer object with statute sections after using text document as input

private ClauseContainer extractStatuteSectionsFromSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    ClauseContainer clauseCarton = myTool.StatuteSectionImport(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 

//used by event handler

private String getMatched(String data) {
    WordTool myTool = new WordTool();
    ClauseContainer defbox = myTool.doDefTextSearch(data);
    return defbox.getClauseAndText();
                
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

public void setupImportStage(Stage textStage, String myTitle) {

        //This is the stage to be used but is not the JavaFX application default
        textStage.setTitle(myTitle);
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int totalwidth=900; //this is pixels?
        
        //config for window
        double leftColWidth = 650;
        double leftColHeight = 400;
        double rightColWidth = 150;
        double windowWidth = leftColWidth+rightColWidth;
        double windowHeight = leftColHeight;
        /* Setup a horizontal box with two text areas, but put first in scrollpane to allow scrolling */
        this.textArea1.setWrapText(true);
        this.textArea2.setWrapText(true);
        this.textArea1.setPrefWidth(leftColWidth);
        this.textArea1.setPrefHeight(leftColHeight);
        this.textArea2.setPrefWidth(rightColWidth);
        
        ScrollPane textpane = new ScrollPane();
        textpane.setContent(textArea1);
        textpane.setPrefHeight(leftColHeight);  
        textpane.setPrefWidth(leftColWidth);
        hbox1 = new HBox(0,textpane,this.textArea2);
        //
        this.textArea3 = new TextArea();
        this.textArea4 = new TextArea();
        this.textArea3.setPrefWidth(leftColWidth);
        this.textArea4.setPrefWidth(leftColWidth);
        
        //Set horizontal boxes with spacing and child nodes *i.e. a row 
        //HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);
        VBox vbox2 = new VBox(0,hbox1);
        vbox2.setPrefWidth(totalwidth);
        //vbox2.getChildren().add(hbox3);
        
        
        // Lastly, attach vbox to root scrollpane and add to Scene
        scroll_rootNode = new ScrollPane();
        scroll_rootNode.setContent(vbox2); 
        this.MainScene = new Scene(scroll_rootNode, windowWidth, windowHeight, Color.GREY); //width x height in pixels?  
        //add Scene to Stage and position it
        textStage.setScene(MainScene);
        textStage.sizeToScene(); 
        myStageManager.setPosition(ParentStage,textStage, "importwindow");
        textStage.show();
        
    }

/*Method to setup single Library stage for Workspace
modelled on setupBlocksStage method (with scroller) but initially hidden
*/

public Group setupLibraryStage(Stage myStage, String myTitle) {
    //LibraryStage.setTitle ("Library");
    //LibraryGroup = setupBlocksWindow(LibraryStage,"Library");
    //Group tempGroup = Main.this.setupBlocksWindow(myStage, myTitle); //Object class Group
    //myStageManager.setPosition(ParentStage,myStage,"library");
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //add group layout object as root node for Scene at time of creation
    Scene tempScene = new Scene (outerScroll,650,400); //default width x height (px)
        //optional event handler
    tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("library");
             }
        });
    myStage.setScene(tempScene); //this selects the stage as current scene
    myStage.setTitle(myTitle);
    //set position before visibility
    myStageManager.setPosition(ParentStage,myStage,"library");
    myStage.hide(); //<---do this later otherwise it affects scene attachment
    return tempGroup;
}

/* Setup method to create a space to add or remove clauses, and then process them into some kind of output

At this stage, the root node is a Group (sizes according to children, unlike Pane).

*/

public Group setupWorkspaceStage(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        
        
        Group myGroup_root = new Group(); //for root
        WorkspaceClauseContainer = new ClauseContainer();
        LibraryClauseContainer = new ClauseContainer(); //for library window/stage
        WorkspaceBoxes = new BoxContainer(); //for load/save
        WorkspaceGroup = new Group(); //for child node


        MenuBar menuBar = new MenuBar();
        //Items for horizontal menu, vertical MenuItems for each
        Menu menuObject = new Menu("Object");
        Menu menuWorkspace = new Menu("Workspace");
        Menu menuLibrary = new Menu("Library");
        Menu menuOutput = new Menu("Output");
        Menu menuImport = new Menu("Importer");
        Menu menuViews = new Menu("Views");
        MenuItem NewDef = new MenuItem("Def");
        MenuItem NewClause = new MenuItem("Clause");
        MenuItem NewEvent = new MenuItem("Event");
        MenuItem viewImporter = new MenuItem("Importer");
        MenuItem viewEditor = new MenuItem("Editor");
        MenuItem viewtextmaker = new MenuItem("Textmaker");
        MenuItem viewToolbar = new MenuItem("Clause Toolbar");
        MenuItem viewLibrary = new MenuItem("Library");
        MenuItem SaveWork = new MenuItem("Save");
        MenuItem LoadWork = new MenuItem("Load");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem SaveLibrary = new MenuItem("Save");
        MenuItem LoadLibrary = new MenuItem("Load");
        MenuItem NewLibrary = new MenuItem("New");
        MenuItem PrintBoxes = new MenuItem("PrintBoxes");
        MenuItem SaveOutput = new MenuItem("Save");
        MenuItem FileOpen = new MenuItem("FileOpen");
        MenuItem WordCount = new MenuItem("WordCount");
        MenuItem InputFile = new MenuItem("InputFile");
        MenuItem GetDefText = new MenuItem("GetDefText");
        MenuItem GetDefs = new MenuItem("GetDefs");
        MenuItem GetClauses = new MenuItem("GetClauses");
        MenuItem GetSections = new MenuItem("GetSections");
         menuObject.getItems().addAll(NewDef,NewClause,NewEvent);
         menuViews.getItems().addAll(
            viewLibrary,
            viewImporter,
            viewEditor,
            viewtextmaker,
            viewToolbar);
         menuWorkspace.getItems().addAll(
            SaveWork,
            LoadWork,
            OutputWork,
            PrintBoxes);
        menuLibrary.getItems().addAll(
            SaveLibrary,
            LoadLibrary,
            NewLibrary);
        menuOutput.getItems().addAll(
            SaveOutput);
        menuImport.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections);
        
        //TO : Just insert function name here and function detail elsewhere
        /*TO DO: ADD OPTION TO TAKE FILENAME AS ARG WHEN INVOKING MAIN
                i.e. you can specify your workspace name when starting it up */
        SaveWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="workboxes.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                //ArrayList <SpriteBox> myBoxList = new ArrayList<SpriteBox>();
                //ArrayList <SpriteBox> myBoxList = WorkspaceBoxes.getBoxArray();
                //ArrayList<String> myBoxList =  new ArrayList<String>(Arrays.asList("Hot","Potato","Test"));
                ArrayList<Clause> myBoxList =  WorkspaceClauseContainer.getClauseArray();
                try {
                    fos = new FileOutputStream(BoxFilename);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(myBoxList); //the top-level object to be saved
                    out.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        /* Method to load up saved workspace clauses.  At the moment, this loads up in a new window.
        So it allows the workspace to be created fresh, but the loaded window is not saved. */

        //TO DO : Just insert function name here and function detail elsewhere
        LoadWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="workboxes.ser";
                FileInputStream fis = null;
                ObjectInputStream in = null;
                ArrayList <Clause> myBoxListIn = null;
                try {
                    fis = new FileInputStream(BoxFilename);
                    in = new ObjectInputStream(fis);
                    myBoxListIn = (ArrayList<Clause>)in.readObject();
                    in.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                System.out.println("Success!");
                System.out.println(myBoxListIn.toString());
                ClauseContainer inputContainer = new ClauseContainer();
                inputContainer.setClauseArray(myBoxListIn);
                //hold existing workspace
                displaySpritesInNewStage(WorkspaceClauseContainer, "Previous Workspace");
                //TO DO: clear workspace
                //Load saved Workspace into Workspace.
                displaySpritesOnWorkspace(inputContainer);
            }
        });

        //EXPORT WORKSPACE TO OUTPUT
        OutputWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            WorkspaceClauseContainer.doPrintIteration();
            String output=WorkspaceClauseContainer.getClauseAndText();
            textmakerTextArea.setText(output);

            }
        });

        
        /*//Button for export/document clauses TO DO: some config or separate panel.
        Button btnExportClause = new Button();
        btnExportClause.setTooltip(new Tooltip ("Press to output clauses as RTF"));
        btnExportClause.setText("RTF Export");
        //btnDeleteClause.setOnAction(extractDefinitions);
        */

        //TO : Just insert function name here and function detail elsewhere
        PrintBoxes.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //call the 'print function' on the BoxContainer object (for now)
                WorkspaceBoxes.ContentsDump();
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
            }
        });    

        //Library load and save functions
        SaveLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="library.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                ArrayList<Clause> myLibraryList =  LibraryClauseContainer.getClauseArray();
                try {
                    fos = new FileOutputStream(BoxFilename);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(myLibraryList); //the top-level object to be saved
                    out.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }

            }
        });    

        
        LoadLibrary.setOnAction(LoadLibraryFile);
        
        //Load up an empty library window
        //TO DO: Save under different name

        NewLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                System.out.println ("Called New Library function");
                ClauseContainer inputContainer = new ClauseContainer();
                Clause myClause = new Clause("Label","Heading","Text","clause");
                inputContainer.addClause(myClause);
                LibraryClauseContainer.setClauseArray(inputContainer.getClauseArray());
                mySpriteManager.resetLibXY();
                LibraryGroup = displaySpritesInNewStage(LibraryClauseContainer, "New Library");
            }
        }); 

        //Toggle visibility of Library window
        viewLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                if (LibraryStage==null) {
                    System.out.println("Problem with Library Stage setup");
                }
                if (LibraryStage.isShowing()==false) {
                    LibraryStage.show();
                    return;
                }
                if (LibraryStage.isShowing()==true) {
                    LibraryStage.hide();
                    return;
                }
            }
        });

        //Toggle visibility of output window
        viewtextmaker.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                if (textmakerWindow.isShowing()==false) {
                    textmakerWindow.show();
                    return;
                }
                if (textmakerWindow.isShowing()==true) {
                    textmakerWindow.hide();
                    return;
                }
            }
        });

        //toggle visibility of editor
        viewEditor.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                if (editorStage.isShowing()==false) {
                    editorStage.show();
                    return;
                }
                if (editorStage.isShowing()==true) {
                    editorStage.hide();
                    return;
                }
            }
        });

         //toggle visibility of importer
        viewImporter.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                if (importStage.isShowing()==false) {
                    importStage.show();
                    return;
                }
                if (importStage.isShowing()==true) {
                    importStage.hide();
                    return;
                }

            }
        });

         //toggle visibility of toolbar
        viewToolbar.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                if (toolbarStage.isShowing()==false) {
                    toolbarStage.show();
                    return;
                }
                if (toolbarStage.isShowing()==true) {
                    toolbarStage.hide();
                    return;
                }
            }
        });

        //To add Menus you simply use 'getMenus' on the MenuBar and do not add to Scene.
        //menuBar.getMenus().addAll(menuViews);
        //menuBar.getMenus().add(menuViews);  

        SaveOutput.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            System.out.println("Save Output selected!");
            EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
            String savecontents = textmakerTextArea.getText();
            myfileApp.replaceText(savecontents);
            }
        });

        /*procedues for Object menu*/
        NewClause.setOnAction(addNewClauseBox);
        NewDef.setOnAction(addNewDefBox);
        NewEvent.setOnAction(addNewEventBox);
        /*procedures for Import menu  TO DO: File Open*/
        WordCount.setOnAction(updateWordCounts); //argument is an EventHandler with ActionEvent object
        GetDefText.setOnAction(extractDefinitions);
        GetDefs.setOnAction(makeDefIcons);
        GetClauses.setOnAction(makeClauseIcons);
        GetSections.setOnAction(importStatuteClauses);

        menuBar.getMenus().addAll(menuViews, menuObject,menuWorkspace, menuLibrary, menuOutput, menuImport);     


        //add group layout object as root node for Scene at time of creation
        //defScene = new Scene (myGroup_root,650,300); //default width x height (px)
        defScene = new Scene (myGroup_root,myStageManager.getBigX(),myStageManager.getBigY(), Color.BEIGE);
        //scene.getRoot()).getChildren().addAll(menuBar);
        WorkspaceGroup.getChildren().addAll(menuBar);

        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("workspace");
             }
        });

               //
        myStage.setScene(defScene); //this selects the stage as current scene
        //Position
        myStageManager.setPosition(ParentStage,myStage,"workspace");
        myStage.show();
        
        VBox vbox1 = new VBox(0,WorkspaceGroup);
        myGroup_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=650;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return WorkspaceGroup;
        
    }


 /* 
 ---- SETUP A NEW STAGE TO DISPLAY MOVEABLE BOX OBJECTS--- 
This is a simple generic scene creator for a Stage.  It sets size and Title and default position.  
The Group object (root node) is placed in the scene without any text box etc.  
Method will @return same Group layout object (root node) to enable addition of further leaf nodes

Child objects can be added to root node later.

The Scene placed on the stage is a standard size window (wd x ht) in a fixed position (no need to pass arguments about size yet)

Adds a generic event handler for future use.

 */

 public Group setupBlocksWindow(Stage myStage, String myTitle) {
        
        /*OLD
        Group myGroup_root = new Group();
        //add group layout object as root node for Scene at time of creation
        defScene = new Scene (myGroup_root,650,600); //default width x height (px)
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("blocks");
             }
        });

        myStage.setScene(defScene); //this selects the stage as current scene
        myStage.setTitle(myTitle);
        myStageManager.setPosition(ParentStage,myStage, "icons");
        myStage.show();
        return myGroup_root;
        */
        Group myGroup_root = new Group();
        ScrollPane outerScroll = new ScrollPane();
        outerScroll.setContent(myGroup_root);
        //add group layout object as root node for Scene at time of creation
        defScene = new Scene (outerScroll,650,600); //default width x height (px)
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("blocks");
             }
        });

        myStage.setScene(defScene); //this selects the stage as current scene
        myStage.setTitle(myTitle);
        myStageManager.setPosition(ParentStage,myStage, "icons");
        myStage.show();
        return myGroup_root;

}

/* Setup Stage as a Clause inspection and edit Window */

public Pane setupEditorPanel(Stage myStage, String myTitle) {

        System.out.println("Making editor Panel");
        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        //Group editorPanel_root = new Group(); 
        Pane editorPanel_root = new Pane();

        Scene editorScene = new Scene (editorPanel_root,400,400, Color.GREY); //default width x height (px)
        //optional event handler
        editorScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("editor Panel Window: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("Editor");
             }
        });
        //TextArea and contents
        Text labelTag = new Text("Label:");
        labelEdit = new TextArea();
        labelEdit.setPrefRowCount(2);
        labelEdit.setPrefColumnCount(40);
        Text headingTag = new Text("Clause heading:");
        headingEdit = new TextArea();
        headingEdit.setPrefRowCount(2);
        Text contentsTag = new Text("Clause text:");
        textEdit = new TextArea();
        textEdit.setPrefRowCount(5);
        textEdit.setPrefColumnCount(40);
        textEdit.setWrapText(true);
        Text categoryTag = new Text("Category:");
        categoryEdit = new TextArea();
        categoryEdit.setPrefRowCount(1);
        Text dateTag = new Text("Date:");
        dateEdit = new TextArea();
        dateEdit.setPrefRowCount(1);
        //
        SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
        editClause = focusSprite.getClause();
        VBox vboxEdit;
        if (editClause.getCategory().equals("event")) {
        vboxEdit = new VBox(0,headingTag,headingEdit,dateTag,dateEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        }
        else {
        //VBox vboxEdit = new VBox(0,labelTag,labelEdit,headingTag,headingEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        /* do not edit label separately for now*/
        vboxEdit = new VBox(0,headingTag,headingEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        //vboxEdit.setVgrow(textEdit, Priority.ALWAYS);
        }
        //Appearance for specific types of Clauses
        if (editClause.getCategory().equals("definition")) {
            headingTag.setText("Defined term:");
            contentsTag.setText("means:");
        }
        if (editClause.getCategory().equals("event")) {
            headingTag.setText("Event:");
            contentsTag.setText("Description:");
            dateTag.setText("Date:");
            dateEdit.setText(editClause.getEventDate());
            //TO DO: add participants/witness edit
        }
        labelEdit.setText(editClause.getHeading());
        headingEdit.setText(editClause.getHeading());
        textEdit.setText(editClause.getClause());
        categoryEdit.setText(editClause.getCategory());

        myStage.setScene(editorScene); //this selects the stage as current scene
        //Layout
        myStageManager.setPosition(ParentStage,myStage,"editor");
        myStage.show();
        
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateEditor);

        //Button for cancel
        Button btnEditCancel = new Button();
        btnEditCancel.setText("Cancel Edits");
        btnEditCancel.setTooltip(new Tooltip ("Press to Cancel current edits"));
        //btnEditCancel.setOnAction(EditCancel);
        
        
        //Set horizontal box to hold buttons
        HBox hboxButtons = new HBox(0,btnUpdate,btnEditCancel);
        //Finish
        VBox vboxAll = new VBox(0,vboxEdit,hboxButtons);
        vboxAll.setPrefWidth(200);
        //
        editorPanel_root.getChildren().add(vboxAll); //add the vbox to the root node to hold everything
        /*int totalwidth=190;
        vboxAll.setPrefWidth(totalwidth); //this is in different units to textarea
        */
       
        //return the child node, not the root in this case? e.g.vBoxEdit?
        return editorPanel_root;
}


/* Setup Stage as a Toolbar Panel for Sprite Move, Copy functions etc */

public Group setupToolbarPanel(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        //Instance variable
        Group toolbar_root = new Group(); //for root
        toolbarScene = new Scene (toolbar_root,150,250, Color.GREY); //default width x height (px)
        //optional event handler
        toolbarScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Clause Toolbar: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("Toolbar");
             }
        });

        //
        myStage.setScene(toolbarScene); //this selects the stage as current scene
        //Layout
        myStageManager.setPosition(ParentStage,myStage,"toolbar");
        myStage.show();
        
        //Button for new clauses
        //Button btnNewClause = new Button();
        Button btnNewClause = myControlsManager.newStdButton();
        btnNewClause.setText("New Clause");
        btnNewClause.setTooltip(new Tooltip ("Press to add a new clause"));
        btnNewClause.setOnAction(addNewClauseBox);

        //Button for new definitions addNewDefBox
        //Button btnNewDef = new Button();
        Button btnNewDef = myControlsManager.newStdButton();
        btnNewDef.setText("New Def");
        btnNewDef.setTooltip(new Tooltip ("Press to add a new definition"));
        btnNewDef.setOnAction(addNewDefBox);
        
        
        //Button for removing clauses
        Button btnDeleteClause = myControlsManager.newStdButton();
        btnDeleteClause.setTooltip(new Tooltip ("Press to remove selected clause"));
        btnDeleteClause.setText("Delete");
        btnDeleteClause.setOnAction(deleteCurrentSprite);

        //Button for moving clauses to Workspace
        Button btnMoveClauseWS = myControlsManager.newStdButton();
        btnMoveClauseWS.setText("Move to Workspace");
        btnMoveClauseWS.setTooltip(new Tooltip ("Press to move clause to Workspace Window"));
        btnMoveClauseWS.setOnAction(MoveClausetoWS);

        //Button for moving clauses to Library
        //To DO: only visible if Library has been loaded
        Button btnMoveClauseLib = myControlsManager.newStdButton();
        btnMoveClauseLib.setText("Move to Library");
        btnMoveClauseLib.setTooltip(new Tooltip ("Press to move clause to Library Window"));
        btnMoveClauseLib.setOnAction(MoveClausetoLib);

        //Button for copying clause to library (leaves copy in workspace)
        Button btnCopyClauseLib = myControlsManager.newStdButton();
        btnCopyClauseLib.setText("Copy to Library");
        btnCopyClauseLib.setTooltip(new Tooltip ("Press to copy clause to Library Window"));
        btnCopyClauseLib.setOnAction(CopyClausetoLib);

        //Button for copying clauses to workspace
        Button btnCopyClauseWS = myControlsManager.newStdButton();
        btnCopyClauseWS.setText("Copy to Workspace");
        btnCopyClauseWS.setTooltip(new Tooltip ("Press to copy clause to Workspace"));
        btnCopyClauseWS.setOnAction(CopyClausetoWorkspace);

        Button btnDoEdit = myControlsManager.newStdButton();
        btnDoEdit.setText("Edit");
        btnDoEdit.setTooltip(new Tooltip ("Press to Edit Selection (Red Block)"));
        btnDoEdit.setOnAction(DoEditStage);

        //TO DO:  Buttons for 'Copy to Library' {Definition Library}{Clause Library}
        //Button for "Load a clause library from disk"  etc
        
        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClauseWS,btnCopyClause);
        VBox vbox1 = new VBox(0,btnNewDef,btnNewClause,btnMoveClauseWS,btnCopyClauseWS,btnCopyClauseLib,btnMoveClauseLib,btnDeleteClause,btnDoEdit);
        
        //VBox vbox1 = new VBox(0,btnMoveClauseWS,btnCopyClause,btnDoEdit);
        //
        toolbar_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return toolbar_root;
}


/** Setup independent text textmaker window (output preview)
@parameter Requires a Stage object and a title as arguments
@Returns a Scrollpane representing the root node

@notes Scene size will determine initial width of Stage window 

**/

public ScrollPane setupScrollTextWindow(Stage myStage, String StageType, String myTitle) {
        
        
        ScrollPane scroll_root1 = new ScrollPane();
        scroll_root1.setFitToHeight(true);
        scroll_root1.setFitToWidth(true);
        //default layout settings (display panels etc) 
        int setWidth=500;
        int setHeight=500;
         
        //textmaker panel settings
        if (StageType.equals("textmaker")) {
            setWidth=500;
            setHeight=250; 
        }

        Scene defScene = new Scene (scroll_root1,setWidth,setHeight); //width x height (px)
       
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected for text scroll window! " + mouseEvent.getSource());
             }
        });
        //Size and positioning
        myStage.setScene(defScene);
        myStageManager.setPosition(ParentStage, myStage, StageType);
        myStage.setTitle(myTitle);
        myStage.show();
        return scroll_root1; 
        
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

/* Method to see if any label or text contains legal 'role' words, for display purposes 

Many of these are pair words: relationship dichotomies; 
a RELATIVE inequality or division of social, economic or legal power that defines a transaction or struture, and the role of the participants.

TO DO: put into groups for managing different areas of law, but iterate through all.
*/

public Boolean isLegalRoleWord (String myWord) {
    ArrayList<String> RoleWords = new ArrayList<String>(Arrays.asList("employer","employee","landlord","tenant","lessor","lessee","director","shareholder","trustee","beneficiary", "debtor","creditor", "payor", "payee","mortgagor","mortgagee","regulator","manager","partner","owner","guarantor","guarantee","seller","buyer","vendor","purchaser","grantor","grantee","distributor","bailor","bailee","master","servant","licensor","licensee","developer","carrier","lender","borrower"));
    Iterator<String> myIterator = RoleWords.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
        String checkWord = myIterator.next();
        if (myWord.equalsIgnoreCase(checkWord)) {
            return true;
        }
        /* pedantic version with case checking
        if (myWord.equals(checkWord)) {
            return true;
        }
        */
    }
    return false;
}

/*Method to iterate through all sprites on Workspace */
public void removeBoxesFromStage(Group targetGroup) {
    //loop:
    //.getChildren().remove(Spritebox??); 
    //remove associated clauses
    //should clauses keep record of parent SpriteBox ref?
    ArrayList<Clause> myWSList = WorkSpaceClauseContainer.getClauseArray();
        Iterator<Clause> myDeleteList = myWSList.iterator();
        while (myiterator.hasNext()) {
            //removeFromMainStage()
            //Delete WS Spritebox from Stage
        }


/* Method to remove spritebox from workspace */
public void removeFromMainStage(SpriteBox thisSprite) {
        WorkspaceGroup.getChildren().remove(thisSprite); 
        mySpriteManager.removeFromMainStage(thisSprite);
    }


/* Method to handle all consequences of adding spritebox to workspace
1. Add to workspace group (implicitly removes from existing group)
2. update state in sprite manager
3. add to collections (if needed)
*/

    public void placeOnMainStage(SpriteBox thisSprite) {
        WorkspaceGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeOnMainStage(thisSprite);
        WorkspaceBoxes.addBox(thisSprite);
    }

/* Method to handle all consequences of adding spritebox to Library
1. Add to library group
2. update state in sprite manager (i.e. position)
3. add to collections (if needed)
*/
    public void placeInLibraryStage(SpriteBox thisSprite) {
        System.out.println("Sprite received for placing in Library:"+thisSprite.toString());
        LibraryGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeInLibrary(thisSprite);
    }

/* Method to copy SpriteBox including event handlers needed
It takes just the clause from the existing Sprite and builds rest from scratch */
    public SpriteBox makeCopySprite (SpriteBox mySprite) {
        Clause copyClause = mySprite.getClause(); //this copies the pointer, not contents
        System.out.println(copyClause.toString());
        //copy values for clause, not references
        Clause freshClause = makeCopyClause(copyClause);
        SpriteBox copySprite = new SpriteBox(freshClause);
        copySprite.setOnMousePressed(PressBoxEventHandler); 
        copySprite.setOnMouseDragged(DragBoxEventHandler);
        return copySprite;
    }

/*Method  copy of Clause object but with a new reference 
Option: put these getters and setters inside a Clause constructor */
public Clause makeCopyClause(Clause myClause) {
    Clause anotherClause = new Clause();
    anotherClause.setClauselabel(myClause.getLabel());
    anotherClause.setClausetext(myClause.getClause());
    anotherClause.setHeading(myClause.getHeading());
    anotherClause.setCategory(myClause.getCategory());
    anotherClause.setEventDesc(myClause.getEventDesc());
    anotherClause.setEventDate(myClause.getEventDate());
    anotherClause.setFreq(myClause.getFreq());
    return anotherClause;
}

/* Method to remove current SpriteBox and contained clause from system 
TO DO: Cater for other windows e.g. new loaded workspace window...
*/
public void deleteSprite (SpriteBox mySprite) {
    if (mySprite.isInLibrary()==true) {
        LibraryClauseContainer.removeClause(mySprite.getClause());
        LibraryGroup.getChildren().remove(mySprite); 
        //do this to refresh - or just Library window?
        //ParentStage.show();
        LibraryStage.show();
        return;
    }
    if (mySprite.isOnStage()==true) {
        WorkspaceClauseContainer.removeClause(mySprite.getClause());
        WorkspaceGroup.getChildren().remove(mySprite); 
        //do this to refresh
        ParentStage.show();
        return;
    }
}

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("File Utilities");
        //primaryStage.show();
        primaryStage.close();

        //the object that manages sprite with focus etc
        mySpriteManager = new SpriteManager();
       
        //setup clauses sandbox as first Stage (preserves order for display)
        ParentStage = new Stage();
        Group clausePlayBox = Main.this.setupWorkspaceStage(ParentStage, "Main Workspace");

        //setup libary window
        LibraryStage = new Stage();
        LibraryGroup = Main.this.setupLibraryStage(LibraryStage, "Library");

        //*Stage that I will use for main text input display and editing
        importStage = new Stage();
        this.setupImportStage(importStage,"Text Importer");
        //set some default text in main text window
        //this.myTextFile="popstarlease.txt";
        this.myTextFile="electricity.txt";
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        // use this line if you want it by default: importStage.show();
        importStage.hide();

        //setup main toolbar
        toolbarStage = new Stage();
        toolbarGroup = Main.this.setupToolbarPanel(toolbarStage, "Toolbar");

        /* Setup default Stage with Scrollpane to display Text as textmaker
        */
        textmakerWindow = new Stage();
        double width = 600; 
        double height = 500; 
        textmakerGroup_root = Main.this.setupScrollTextWindow(textmakerWindow, "textmaker", "Output Window");
        textmakerGroup_root.setPrefHeight(height);  
        textmakerGroup_root.setPrefWidth(width);
        textmakerGroup_root.setContent(textmakerTextArea); 
        /* OLD //Outer class method class to obtain text from analysis area
        String gotcha = Main.this.textArea1.getText();
        String newDefs = Main.this.getMatched(gotcha);
        */
        //set the default scrollpane content to a designated text area and size scrollpane
        textmakerTextArea.setWrapText(true);
        textmakerTextArea.setText("Some future contents");

        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

    /* This is a method to create a new eventhandler for the SpriteBox objects which are themselves a Stackpane that incorporate a Rectangle and a Text Node as components

    //unfocus current Sprite - only works for the Sandbox.  
    Need to refine scope so that it works with current window

    */

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            // If you are only moving child objects not panes
            orgTranslateX = ((SpriteBox)(t.getSource())).getTranslateX();
            orgTranslateY = ((SpriteBox)(t.getSource())).getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);
            
            SpriteBox hadFocus=null;
            SpriteBox currentSprite = null;
            switch(t.getClickCount()){
                //single click
                case 1:
                    System.out.println("One click");
                    //end alert status for current sprite
                    hadFocus = mySpriteManager.getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                    }
                    currentSprite = ((SpriteBox)(t.getSource()));
                    //change current sprite and set alert colour
                    mySpriteManager.setCurrentSprite(currentSprite);
                    currentSprite.doAlert();
                    break;
                case 2:
                    System.out.println("Two clicks");
                    
                    //unfocus current Sprite - only works for the Sandbox? or record in any window?  
                    hadFocus = mySpriteManager.getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                    }
                    //change colour if double click
                    currentSprite = ((SpriteBox)(t.getSource()));
                    currentSprite.doAlert();
                    //change target only if in Workspace stage 
                    if (mySpriteManager.getStageFocus().equals("workspace")) {
                        mySpriteManager.setTargetSprite(currentSprite);
                    }
                    
                    mySpriteManager.setCurrentSprite(currentSprite);  //what if not on MainStage?
                    Clause internalClause = currentSprite.getClause();
                    String myOutput = internalClause.getClause();

                    /* previously - double click was output window 
                    if (textmakerWindow.isShowing()==false) {
                        textmakerWindow.show();
                    }
                    textmakerTextArea.setText(myOutput);
                    */

                    // show editor window instead, for current Sprite
                    editorStage = new Stage();
                    editGroup_root = Main.this.setupEditorPanel(editorStage, "Editor");

                    break;
                case 3:
                    System.out.println("Three clicks");
                    break;
            }
            t.consume(); //trying this to see if it frees up for second press but better to deal with cause
        }
    };
    
     /* This is eventhandler interface to create a new eventhandler class for the SpriteBox objects 
     This uses a lambda expression to create an override of the handle method
     */
     /* These currently have no limits on how far you can drag */

    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            currentSprite.setTranslateX(newTranslateX);
            currentSprite.setTranslateY(newTranslateY);
            System.out.println("The handler for drag box is acting");
            //end alert status for current sprite
            mySpriteManager.getCurrentSprite().endAlert();
            //change the active sprite to the current touched sprite.
            mySpriteManager.setCurrentSprite(currentSprite);
            currentSprite.doAlert();
            //single click event by itself will not change alert status - need to do it here.
            t.consume();//check

        }
    };

    //BUTTON EVENT HANDLERS

    //

    EventHandler<ActionEvent> deleteCurrentSprite = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference - should be updated based on previous selections 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            /* For now, use single delete function, alternatively use different
            functions depending on where SpriteBox is located 
            */
            deleteSprite(currentSprite);
            }
        };

    // Method to move selected sprite to Clause WIP (will not duplicate)
    /*
            The following 'add' actually copies to the second stage.
            By moving the object or referring to it on the new Stage, it forces JavaFX to refresh.

            Java FX does its own cleanup.

            To achieve a 'copy' rather than a move, additional code needed.

     */

    EventHandler<ActionEvent> MoveClausetoWS = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            if (currentSprite.isOnStage()==false) {
            /* add clause to the list of clauses in the clause array */
            WorkspaceClauseContainer.addClause(currentSprite.getClause()); 
            //assume it could only be in library for now - not stage specific?
            LibraryClauseContainer.removeClause(currentSprite.getClause());
            //add sprite to Stage for clause WIP. This will clean up object on Stage elsewhere...
            placeOnMainStage(currentSprite);
            }
        }
    };

    EventHandler<ActionEvent> MoveClausetoLib = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            if (currentSprite.isInLibrary()==false) {
            /* add clause to the list of clauses in the Library clause array */
            LibraryClauseContainer.addClause(currentSprite.getClause()); 
            //remove from Workspace clause list (so it is not saved)
            WorkspaceClauseContainer.removeClause(currentSprite.getClause()); 
            //add sprite to Library Stage. This will clean up same object on Workspace...
            placeInLibraryStage(currentSprite);
            WorkspaceBoxes.removeBox(currentSprite); //cleanup stage refs.  TO DO: use sprite manager
            }
        }
    };

    EventHandler<ActionEvent> CopyClausetoLib = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            if (currentSprite==null) {
                System.out.println("Current sprite not detected");
            }
            //don't lose focus - just do a copy 
            //currentSprite.endAlert();
            if (currentSprite.isInLibrary()==false) {
            //copy Spritebox and clause contents
            SpriteBox copySprite = makeCopySprite(currentSprite);
            /* add clause to the list of clauses in the Library clause array */
            LibraryClauseContainer.addClause(copySprite.getClause());  
            //add sprite to Library Stage. 
            System.out.println(copySprite.toString());
            placeInLibraryStage(copySprite);
            }
        }
    };

    /* This is a copy not a move */

    EventHandler<ActionEvent> CopyClausetoWorkspace = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            if (currentSprite.isOnStage()==false) {
                //lose focus
                currentSprite.endAlert();
                //copy sprite
                SpriteBox copySprite = makeCopySprite(currentSprite);
                /* add clause to the list of clauses in the clause array */
                WorkspaceClauseContainer.addClause(copySprite.getClause()); 
                placeOnMainStage(copySprite); 
            }
        }
    };

    /* Invoke the SpriteBox/Clause Editor */

    /* 

    Event handlers for each SpriteBox added, so that they can handle mouse events inside the Window they've been added to 
    
    */

    EventHandler<ActionEvent> DoEditStage = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
        System.out.println("Edit Button was pressed!");
        editorStage = new Stage();

        editGroup_root = Main.this.setupEditorPanel(editorStage, "Editor");
        }    
    };
     
    /* Event handler to save contents of textmaker & overwrite file
    currently uses default filename 'textmakercontents.txt'
    */

    EventHandler<ActionEvent> SaveOutputVw = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
        System.out.println("Save Button was pressed!");
        EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
        String savecontents = textmakerTextArea.getText();
        myfileApp.replaceText(savecontents);
        }    
    };
     
    /* Event handler for adding a new definition box to WIP staging area
    TO DO: Prevent user from attempting to add same object to same stage twice.
    i.e. if focus is on clause WIP stage, then either copy, or disallow.
    */

    EventHandler<ActionEvent> addNewDefBox = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            SpriteBox b;
            String label = "New Definition"; //TO DO: Sprite Manager to increment # unused.
            String heading = "replace with Defined Term";
            String text = "replace with definition text";
            String category = "definition";
            Clause myClause = new Clause(label,heading,text,category); 
            //common
            b = new SpriteBox(); //leave default settings to the 'setClause' method in SpriteBox
            b.setClause(myClause);
            WorkspaceClauseContainer.addClause(b.getClause()); //add clause from sprite to clauses container
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            placeOnMainStage(b); 
            }
        };

/* Event handler for adding a new clause box to WIP staging area
    TO DO: Prevent user from attempting to add same object to same stage twice.
    i.e. if focus is on clause WIP stage, then either copy, or disallow.
    TO DO: Make this a generic add "SpriteBox", then vary the internal clause (incl category, colour) based on calling button
    */

    EventHandler<ActionEvent> addNewClauseBox = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            SpriteBox b;
            String label = "New Clause"; //unused
            String text = "replace with clause text";
            String heading = "replace with clause heading";
            String category = "clause";
            Clause myClause = new Clause(label,heading,text,category); 
            //b = new SpriteBox(label, "blue"); //default clause colour is blue
            //everthing after here is common to new clauses and definitions
            b = new SpriteBox(myClause); //leave default settings to the 'setClause' method in SpriteBox
            //b.setClause(myClause);
            WorkspaceClauseContainer.addClause(myClause); //add clause in Sprite to Clauses container
            //event handler
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            placeOnMainStage(b); 
            }
        };

    EventHandler<ActionEvent> addNewEventBox = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            SpriteBox b;
            String label = "New Event"; //TO DO: Sprite Manager to increment # unused.
            String text = "replace with event text";
            String category = "definition";
            //Event myEvent = new Event(label,heading,text,category); 
            Event myEvent = new Event(label,text);
            //common
            b = new SpriteBox(); //leave default settings to the 'setClause' method in SpriteBox
            b.setClause(myEvent);
            WorkspaceClauseContainer.addClause(b.getClause()); //add clause from sprite to clauses container
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            placeOnMainStage(b); 
            }
        };

    //printClauseList
        EventHandler<ActionEvent> printClauseList = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
             //textmakerTextArea.setText("This is where list of clauses will appear");
             WorkspaceClauseContainer.doPrintIteration();
             String output=WorkspaceClauseContainer.getClauseAndText();
             textmakerTextArea.setText(output);

             /* TO DO: Have a separate "Output/Preview" Window to show clause output.  
             //Maybe HTMLview?
             i.e. this will be an 'output console', but within the application.
             */
            }
        };


    /* 

    Event handlers for each SpriteBox added, so that they can handle mouse events inside the Window they've been added to 
    
    */

    EventHandler<ActionEvent> makeDefIcons = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
        //obtain data to display
        ClauseContainer myContainer = extractDefinitionsFromSampleString(textArea1.getText());
        System.out.println("Get DefIcons Button was pressed!");
        displaySpritesInNewStage(myContainer, "Imported Definitions");
        }
    };

    /* 
    Event handlers for each clause block added, so that they can handle mouse events inside the Window they've been added to 
    */

    EventHandler<ActionEvent> makeClauseIcons = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        //TO DO: get source of data
        ClauseContainer myContainer = extractClausesFromSampleText(textArea1.getText());
        System.out.println("Clause Icons Button was pressed!");
        displaySpritesInNewStage(myContainer, "Imported Clauses");
        }
    };
    

    /* 
    Event handlers for each for importing statute clauses 
    */


    EventHandler<ActionEvent> importStatuteClauses = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        //TO DO: get source of data
        ClauseContainer myContainer = extractStatuteSectionsFromSampleText(textArea1.getText());
        System.out.println("Clause Icons Button was pressed!");
        displaySpritesInNewStage(myContainer, "Imported Clauses");
        }
    };
    
    /* Add this Clause Container to main workspace */

    //Create adHoc Stage but return root (Group) so that it can be stored if significant e.g. Library

    public void displaySpritesOnWorkspace(ClauseContainer wsContainer) {
        ClauseContainer myContainer = wsContainer;
        //Use ParentStage and WorkspaceGroup (node) 
        //remove the old workspace
        
            //refresh
        //update the Workspace 
        WorkspaceClauseContainer=wsContainer;

        ParentStage.show();
        //use updated workspace for display
        ArrayList<Clause> myDList = WorkSpaceClauseContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        int offX=50;
        int offY=50;
        while (myiterator.hasNext()) {
            Clause mydefinition = myiterator.next();
            if (isLegalRoleWord(mydefinition.getLabel())==true) {
                mydefinition.setCategory("legalrole");
            }
            SpriteBox b = new SpriteBox(mydefinition);
            //location
            b.setTranslateX(offX);         
            b.setTranslateY(offY);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            WorkspaceGroup.getChildren().add(b);
            if (offX>440) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        
        //return defGroup_root;
        }

    //Create Library Stage
    //Create adHoc Stage but return root (Group) so that it can be stored if significant e.g. Library

    public void displaySpritesInLibraryStage(ClauseContainer inputContainer, String myTitle) {
        ClauseContainer myContainer = inputContainer;
        if (LibraryStage==null) {
            System.out.println("Problem with Library Stage setup");
        }
        //process
        ArrayList<Clause> myDList = myContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Clause mydefinition = myiterator.next();
            if (isLegalRoleWord(mydefinition.getLabel())==true) {
                mydefinition.setCategory("legalrole");
            }
            SpriteBox b = new SpriteBox(mydefinition);
            //location
            b.setTranslateX(offX);         
            b.setTranslateY(offY);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            //add to Library Group
            LibraryGroup.getChildren().add(b);
            if (offX>440) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        LibraryStage.show();
        //return Library;
        } 

    

        //Create adHoc Stage but return root (Group) so that it can be stored if significant e.g. Library

    public Group displaySpritesInNewStage(ClauseContainer inputContainer, String myTitle) {
        ClauseContainer myContainer = inputContainer;
        Stage adHoc = new Stage();
        defGroup_root = Main.this.setupBlocksWindow(adHoc, myTitle); //Object class Group
        
        adHoc.setY(600);

        //event handlers
        //adHoc. set an event Handler to tell Stage Manager it has focus, so it can work with 
        //'copy all these to Stage'

        //process
        ArrayList<Clause> myDList = myContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Clause mydefinition = myiterator.next();
            if (isLegalRoleWord(mydefinition.getLabel())==true) {
                mydefinition.setCategory("legalrole");
            }
            SpriteBox b = new SpriteBox(mydefinition);
            //location
            b.setTranslateX(offX);         
            b.setTranslateY(offY);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            defGroup_root.getChildren().add(b);
            if (offX>440) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        adHoc.show();
        return defGroup_root;
        }    
     
        //Method to load up a library from serialized disk data
        //Uses the Main instance variable for library name.

        public void libraryLoader() {
            FileInputStream fis = null;
            ObjectInputStream in = null;
            ArrayList <Clause> myLibraryIn = null;
            try {
                fis = new FileInputStream(this.LibFilename);
                in = new ObjectInputStream(fis);
                myLibraryIn = (ArrayList<Clause>)in.readObject();
                in.close();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            catch(ClassNotFoundException ex)
            {
                 ex.printStackTrace();
            }
            System.out.println("Success!");
            System.out.println(myLibraryIn.toString());
            //ClauseContainer inputContainer = new ClauseContainer();
            //inputContainer.setClauseArray(myLibaryIn);
            LibraryClauseContainer.setClauseArray(myLibraryIn);
            mySpriteManager.resetLibXY();
            displaySpritesInLibraryStage(LibraryClauseContainer, "Loaded Library Clauses");
        }

        //---- MORE EVENT HANDLERS ----
          
        //handle library open request.   Keeps library name for project as state variable 
        //To do: see if filename can be passed to this Object
        EventHandler<ActionEvent> LoadLibraryFile = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent t) {
                libraryLoader();
            }
        }; 

        EventHandler<ActionEvent> OpenInputFile = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Import button was pressed!");
            }
        };

        //update word counts
        EventHandler<ActionEvent> updateWordCounts = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
                System.out.println("Word Count Button was pressed!");
                //Outer class method class
                String gotcha = Main.this.textArea1.getText();
                String newTA = Main.this.getCommonWordsNow(gotcha);
                Main.this.textArea2.setText(newTA);
                //new stage
                Stage MainWords = new Stage();
                Group CountGroup_root = Main.this.setupBlocksWindow(MainWords, "Common Words Window");

                //Spriteboxes holding common words
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
                    //create 'clause' from the word, not just an empty spritebox
                    String newlabel = i.next();
                    Clause cword = new Clause (newlabel,newlabel,"","default");
                    if (isLegalRoleWord(newlabel)==true) {
                        cword.setCategory("legalrole");
                    }
                    //new Spritebox to hold new clause
                    SpriteBox b = new SpriteBox(cword);
                    offX=offX+50;  //increments offset each time
                    b.setTranslateX(offX); 
                    b.setTranslateY(offX);
                    b.setOnMousePressed(PressBoxEventHandler); 
                    b.setOnMouseDragged(DragBoxEventHandler);      
                    CountGroup_root.getChildren().add(b);
                }
            }
        };
    
        EventHandler<ActionEvent> extractDefinitions = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //make a new stage with scrollpane
            defsTextStage = new Stage();
            defsTextStage_root = Main.this.setupScrollTextWindow(defsTextStage, "display", "Definitions List");
            defsTextStage.setY(350);
            //Outer class method class to obtain text from analysis area
            String gotcha = Main.this.textArea1.getText();
            String newDefs = Main.this.getMatched(gotcha);
            //set the default scrollpane content to a designated text area and size it
            defsTextStage_root.setContent(textArea3); 
            double width = 800; 
            double height = 500; 
            textArea3.setPrefHeight(height);  
            textArea3.setPrefWidth(width);
            textArea3.setWrapText(true);
            //now set the content of text area inside scrollpane to our extracted text
            textArea3.setText(newDefs);
            System.out.println("Get Defs Button was pressed!");
            }
        };
        
        /* Update Clause in Editor */
        
        EventHandler<ActionEvent> UpdateEditor = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //make a new stage with scrollpane
            
            //editClause = focusSprite.getClause();
            //editClause.setClauselabel(labelEdit.getText());
            //For now: set label as for heading
            if (editClause.getCategory().equals("event")) {
                editClause.setEventDate(dateEdit.getText());
                System.out.println("Event updated!");
            }
            editClause.setClauselabel(headingEdit.getText());
            editClause.setHeading(headingEdit.getText());
            editClause.setClausetext(textEdit.getText());
            editClause.setCategory(categoryEdit.getText());
            System.out.println("Clause updated!");
            //update the SpriteBox on the GUI
            SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
            focusSprite.setClause(editClause);
            }
        };
}