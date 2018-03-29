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
    Scene ImportScene; // scene for adding on textStage.
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
    ClauseContainer WorkspaceClauseContainer = null;
    BoxContainer WorkspaceBoxes; //A serializable top-level container (optional)
    Collection wsCollection = new Collection(); //for holding workspace contents (inside boxes)
    //Opus = project collection.  Display Projects as Icons, as in a library.
    Stage ProjectLibStage;
    Scene ProjectLibScene;
    Group ProjectLibGroup;
    ProjectContainer myProjectLib = new ProjectContainer();
    //ProjectOpen Stage (to display contents of each Project i.e. an open Project with Collection(s), MergeData etc)
    Stage ProjectStage;
    Scene ProjectScene;
    Group ProjectGroup;
    //String ProjectName = "project.ser";
    Project myOpenProject = new Project(); //currently opened project
    //To do: MergeDataWindow
    //Collection Stage (to hold groups of libraries and documents).. i.e an open Collection.
    Stage CollectionStage;
    Scene CollectionScene;
    Group CollectionGroup;
    String CollectionName = "collection.ser";
    Collection myCollection = new Collection(); //the curently open Collection.
    //To hold groups of Clauses in SpriteBoxes (as needed) i.e. an Open Document.
    Stage DocumentStage;
    Scene DocumentScene;
    Group DocumentGroup;
    String BoxFilename="document.ser";
    ClauseContainer DocumentClauseContainer = null; 
    ClauseContainer LibraryClauseContainer = null; //library import/save
    //Library Window (for display of the Open Library)
    Stage LibraryStage=null;
    Scene LibraryScene;
    Group LibraryGroup;
    String LibFilename="library.ser";
    //importStage
    Stage importStage;

    //textmaker window (no edits)
    Stage textOutputStage;
    Scene textOutputScene; 
    ScrollPane textmakerGroup_root;
    TextArea textmakerTextArea = new TextArea();
    //Display SpriteBoxes window(s)
    Scene defScene;  //<----used multiple times in different methods.  TO DO:  localise Scene variables.
    //Group defGroup_root; //<---used for display Sprites in new stage
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
    //Container editor
    TextArea docnameEdit;
    TextArea authorEdit;
    TextArea notesEdit;
    TextArea CCdateEdit;
    ClauseContainer myEditCC;
    //Group editGroup_root;
    Stage editorStage;
    Pane editGroup_root;
    //document loaded sequence
    int loaddocnum=0;
    int libdocnum=0;

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

//---EVENT HANDLER FUNCTIONS

private void toggleView(Stage myStage) {
                
    if (myStage==null) {
        System.out.println("Problem with Stage setup +"+myStage.toString());
    }
    if (myStage.isShowing()==false) {
        myStage.show();
        return;
    }
    if (myStage.isShowing()==true) {
        myStage.hide();
        return;
    }
}
            

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
        this.ImportScene = new Scene(scroll_rootNode, windowWidth, windowHeight, Color.GREY); //width x height in pixels?  
        //add Scene to Stage and position it
        textStage.setScene(ImportScene);
        textStage.sizeToScene(); 
        myStageManager.setPosition(textStage, "importwindow");
        textStage.show();
        
    }


/*
Method to setup single stage to display all Projects
*/

public Group setupProjectLibStage() {

    //Create scrollpane as root and attach group node
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //give root node its Scene with event handlers on it
    ProjectLibScene = new Scene (outerScroll,650,400); //default width x height (px)
    ProjectLibScene .addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected - Project Stage! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("project");
             }
        });
    //Configure the Stage and its position/visibility
    ProjectLibStage.setScene(ProjectLibScene); //set current scene for the Stage
    ProjectLibStage.setTitle("Project");//later, use loaded project title
    myStageManager.setPosition(ProjectLibStage,"projectlib");
    ProjectLibStage.hide(); 
    return tempGroup;
}

/*
Method to setup single Project stage for currently open Project
*/

public Group setupProjectOpenStage() {

    //Create scrollpane as root and attach group node
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //give root node its Scene with event handlers on it
    ProjectScene = new Scene (outerScroll,650,400); //default width x height (px)
    ProjectScene .addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected - Project Stage! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("project");
             }
        });
    //Configure the Stage and its position/visibility
    ProjectStage.setScene(ProjectScene); //set current scene for the Stage
    ProjectStage.setTitle("Project");//later, use loaded project title
    myStageManager.setPosition(ProjectStage,"project");
    ProjectStage.hide(); 
    return tempGroup;
}

/*
Method to setup single Collection stage 
*/

public Group setupCollectionStage() {

    //Create scrollpane as root and attach group node
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //give root node its Scene with event handlers on it
    CollectionScene = new Scene (outerScroll,650,400); //default width x height (px)
    CollectionScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected - Collection Stage! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("collection");
             }
        });
    //Configure the Stage and its position/visibility
    CollectionStage.setScene(CollectionScene); //set current scene for the Stage
    CollectionStage.setTitle("Collection");
    myStageManager.setPosition(CollectionStage,"collection");
    CollectionStage.hide(); 
    return tempGroup;
}

/*
Method to setup single Library stage for currently open library
*/

public Group setupLibraryStage() {
    //Create scrollpane as root and attach group node
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //give root node its Scene with event handlers on it
    LibraryScene = new Scene (outerScroll,650,400); //default width x height (px)
    LibraryScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("library");
             }
        });
    //Configure the Stage and its position/visibility
    LibraryStage.setScene(LibraryScene); //set current scene for the Stage
    LibraryStage.setTitle("Library");
    myStageManager.setPosition(LibraryStage,"library");
    LibraryStage.hide(); //<---do this after set position otherwise it affects scene attachment
    return tempGroup;
}

/*
Method to setup single Document stage for currently open Document
*/

public Group setupDocumentStage() {

    //Create scrollpane as root and attach group node
    Group tempGroup = new Group();
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(tempGroup);
    //give root node its Scene with event handlers on it
    DocumentScene = new Scene (outerScroll,650,400); //default width x height (px)
    DocumentScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected - Doc Stage! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("document");
             }
        });
    //Configure the Stage and its position/visibility
    DocumentStage.setScene(DocumentScene); //set current scene for the Stage
    DocumentStage.setTitle("Document");
    myStageManager.setPosition(DocumentStage,"document");
    DocumentStage.hide(); //<---do this after set position otherwise it affects scene attachment
    return tempGroup;
}

/* 

The root node is a Group (resizes according to children, unlike Pane).

*/

public Group setupWorkspaceStage(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        
        
        Group myGroup_root = new Group(); //for root node
        DocumentClauseContainer = new ClauseContainer();
        WorkspaceClauseContainer = new ClauseContainer();
        LibraryClauseContainer = new ClauseContainer(); //for library window/stage
        WorkspaceBoxes = new BoxContainer(); //for load/save
        Group menubarGroup = new Group(); //to hold menubar


        MenuBar menuBar = new MenuBar();
        //Items for horizontal menu, vertical MenuItems for each
        Menu menuObject = new Menu("New");
        Menu menuWorkspace = new Menu("Workspace");
        Menu menuDocument = new Menu("Document");
        Menu menuCollection = new Menu("Collection");
        Menu menuProject = new Menu("Project");
        Menu menuProjectLib = new Menu ("ProjectLib");
        Menu menuLibrary = new Menu("Library");
        Menu menuOutput = new Menu("Output");
        Menu menuImport = new Menu("Importer");
        Menu menuViews = new Menu("Views");
        MenuItem NewDef = new MenuItem("Def");
        MenuItem NewClause = new MenuItem("Clause");
        MenuItem NewEvent = new MenuItem("Event");
        MenuItem NewDocCont = new MenuItem("Document");
        MenuItem NewLibrary = new MenuItem("NewLibrary");
        MenuItem NewCollection = new MenuItem("Collection");
        MenuItem NewProject = new MenuItem("Project");
        MenuItem viewImporter = new MenuItem("Importer");
        MenuItem viewDocument = new MenuItem("Document");
        MenuItem viewEditor = new MenuItem("Editor");
        MenuItem viewtextmaker = new MenuItem("Textmaker");
        MenuItem viewToolbar = new MenuItem("Clause Toolbar");
        MenuItem viewLibrary = new MenuItem("Library");
        MenuItem viewCollection = new MenuItem("Collection");
        MenuItem viewProject = new MenuItem("Project");
        MenuItem viewProjectLib = new MenuItem("ProjectLib");
        MenuItem SaveProject = new MenuItem("Save");
        MenuItem LoadProject = new MenuItem("Load");
        MenuItem SaveColl = new MenuItem("Save");
        MenuItem LoadColl = new MenuItem("Load");
        MenuItem SaveWork = new MenuItem("Save");
        MenuItem LoadWork = new MenuItem("Load");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem SaveDocAs = new MenuItem("Save");
        MenuItem LoadDocWS = new MenuItem("Load");
        MenuItem SaveCont = new MenuItem("SaveCont");
        MenuItem LoadDocCont = new MenuItem("LoadCont");
        MenuItem NewDoc = new MenuItem("New");
        MenuItem OutputDoc = new MenuItem("Output as Text");
        MenuItem SaveLibrary = new MenuItem("Save");
        MenuItem LoadLibrary = new MenuItem("Load");
        MenuItem PrintBoxes = new MenuItem("PrintBoxes");
        MenuItem SaveOutput = new MenuItem("Save");
        MenuItem FileOpen = new MenuItem("FileOpen");
        MenuItem WordCount = new MenuItem("WordCount");
        MenuItem InputFile = new MenuItem("InputFile");
        MenuItem GetDefText = new MenuItem("GetDefText");
        MenuItem GetDefs = new MenuItem("GetDefs");
        MenuItem GetClauses = new MenuItem("GetClauses");
        MenuItem GetSections = new MenuItem("GetSections");
         menuObject.getItems().addAll(NewDef,NewClause,NewEvent,NewDocCont,NewLibrary,NewCollection,NewProject);
         menuViews.getItems().addAll(
            viewProjectLib,
            viewProject,
            viewCollection,
            viewLibrary,
            viewDocument,
            viewEditor,
            viewImporter,
            viewtextmaker,
            viewToolbar);
         menuProject.getItems().addAll(
            SaveProject,
            LoadProject);
         menuCollection.getItems().addAll(
            SaveColl,
            LoadColl);
         menuWorkspace.getItems().addAll(
            SaveWork,
            LoadWork,
            OutputWork,
            PrintBoxes);
        menuDocument.getItems().addAll(
            SaveCont,
            LoadDocCont,
            NewDoc); //OutputDoc
        menuLibrary.getItems().addAll(
            SaveLibrary,
            LoadLibrary);
        menuOutput.getItems().addAll(
            SaveOutput);
        menuImport.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections);
        
        //COLLECTION FUNCTIONS
        /*Trial method to save a Collection 
        The Collection object doesn't serialize, but the ArrayList it contains will
        */
        SaveColl.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //Check
                System.out.println("Save Coll Pre-check");
                ArrayList<ClauseContainer> myACCsave = myCollection.getCollectionItems();
                Iterator myACCsaveIT = myACCsave.iterator();
                while(myACCsaveIT.hasNext()) {
                    System.out.println(myACCsaveIT.next().toString());
                }
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                BoxFilename = "collection.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                if (BoxFilename.equals("")) {
                    BoxFilename="collection2.ser";
                }
                try {
                    fos = new FileOutputStream(BoxFilename);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(myCollection);
                    /*
                    out.writeObject(myACCsave); //the top-level object to be saved
                    */
                    out.close();
                    myACCsave = myCollection.getCollectionItems();
                    System.out.println("Saved Collection:"+myACCsave.toString());
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                
            }
        });

        /* Load Collection
        Currently only loads ArrayList, so loses Collection object meta-data
        */
        LoadColl.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //Check
                System.out.println("Load Coll Pre-check");
                ArrayList<ClauseContainer> myACC = new ArrayList<ClauseContainer>();
                Iterator myACCit = myACC.iterator();
                while(myACCit.hasNext()) {
                    System.out.println(myACCit.next().toString());
                }
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="collection.ser";
                FileInputStream fis = null;
                ObjectInputStream in = null;
                try {
                    fis = new FileInputStream(BoxFilename);
                    in = new ObjectInputStream(fis);
                    myCollection=(Collection)in.readObject();
                    in.close();
                    System.out.println(myCollection.toString());
                    myACC = myCollection.getCollectionItems();
                    System.out.println("Loaded Collection:"+myACC.toString());
                    displaySpritesInCollectionStage(myCollection, "Collection");
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                System.out.println("Load Coll Post-check");
                ArrayList<ClauseContainer> myACC2 = myCollection.getCollectionItems();
                Iterator myACC2it = myACC2.iterator();
                while(myACC2it.hasNext()) {
                    System.out.println(myACC2it.next().toString());
                }
            }
        });

        //Method to save workspace (serial)

        SaveWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                BoxFilename = "workspace.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
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

        /* Method to load up saved workspace */

        LoadWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="workspace.ser";
                WorkspaceClauseContainer.setDocName(BoxFilename);
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
                WorkspaceClauseContainer.setClauseArray(inputContainer.getClauseArray());
                displaySpritesOnWorkspace(WorkspaceClauseContainer);
            }
        });

        //EXPORT WORKSPACE TO OUTPUT
        OutputWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            WorkspaceClauseContainer.doPrintIteration();
            String output=WorkspaceClauseContainer.getClauseAndText();
            textmakerTextArea.setText(output);
            if (textOutputStage.isShowing()==false) {
                    textOutputStage.show();
            }

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

        //----DOCUMENT EVENT HANDLER FUNCTIONS 
        /*  Function to save current document clauses/contents  */
        SaveDocAs.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                BoxFilename = "document.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                ArrayList<Clause> myBoxList =  DocumentClauseContainer.getClauseArray();
                //BoxFilename = DocumentClauseContainer.getDocName();
                if (BoxFilename.equals("")) {
                    BoxFilename="document-1-temp.ser";
                }
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

        //Trial method to save a Clause Container (just the DocumentClauseContainer)
        SaveCont.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                BoxFilename = "document-cont.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                //ArrayList<Clause> myBoxList =  DocumentClauseContainer.getClauseArray();
                //BoxFilename = DocumentClauseContainer.getDocName();
                //ClauseContainer myBoxList = DocumentClauseContainer;
                //TO DO: select an array of Clause Containers to write out.
                if (BoxFilename.equals("")) {
                    BoxFilename="document-cont.ser";
                }
                try {
                    fos = new FileOutputStream(BoxFilename);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(DocumentClauseContainer); //the top-level object to be saved
                    out.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        /* Method to load up container of document clauses from arraylist.  
        Now loads as SpriteBox with ClauseContainer (effectively, link for opening later)
         */

        //TO DO : Just insert function name here and function detail elsewhere
        LoadDocWS.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="document.ser";
                FileInputStream fis = null;
                ObjectInputStream in = null;
                ClauseContainer newDocument = new ClauseContainer();
                //ArrayList <Clause> myBoxListIn = null;
                try {
                    fis = new FileInputStream(BoxFilename);
                    in = new ObjectInputStream(fis);
                    newDocument=(ClauseContainer)in.readObject();
                    /*
                    myBoxListIn = (ArrayList<Clause>)in.readObject();
                    */
                    in.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                System.out.println("Loaded document:"+newDocument.toString());
                loaddocnum++;
                newDocument.setDocName("CraigsLoadedDocument"+Integer.toString(loaddocnum));
                //create spritebox
                SpriteBox b = new SpriteBox();
                b.setBoxContent(newDocument);
                b.setOnMousePressed(PressDocBoxEventHandler); //dbl click = open contents window
                b.setOnMouseDragged(DragBoxEventHandler);
                placeSpriteOnMainStage(b);
            }
        });

        //load documents from single clause container
        LoadDocCont.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="document-cont.ser";
                FileInputStream fis = null;
                ObjectInputStream in = null;
                ClauseContainer inputContainer = new ClauseContainer();
                try {
                    fis = new FileInputStream(BoxFilename);
                    in = new ObjectInputStream(fis);
                    inputContainer = (ClauseContainer)in.readObject();
                    in.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                System.out.println("Success loaded document!");
                inputContainer.doPrintIteration();
                System.out.println(inputContainer.toString());
                //Add this Container to Spritebox and add to Workspace
                loaddocnum++;
                if (inputContainer.getDocName().equals("")) {
                    inputContainer.setDocName("CraigsLoadedDocument"+Integer.toString(loaddocnum));
                }
                //create spritebox
                SpriteBox b = new SpriteBox();
                b.setBoxContent(inputContainer);
                b.setOnMousePressed(PressDocBoxEventHandler); 
                b.setOnMouseDragged(DragBoxEventHandler);
                //
                if (CollectionStage.isShowing()==true) {
                    placeInCollectionStage(b);
                    myCollection.addCC(inputContainer); //store contents not sprite
                }
                else {
                    placeSpriteOnMainStage(b);
                    wsCollection.addCC(inputContainer);
                }
            }
        });

        //New document
        NewDoc.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                System.out.println ("Called New Document function");
                DocumentGroup = Main.this.setupDocumentStage();
                mySpriteManager.resetDocXY();
                //
                ClauseContainer inputContainer = new ClauseContainer();
                Clause myClause = new Clause("Label","Heading","Text","clause");
                inputContainer.addClause(myClause);
                DocumentClauseContainer.setClauseArray(inputContainer.getClauseArray());
                displaySpritesInDocumentStage(DocumentClauseContainer, "New Document");
            }
        });

        //LIBRARY load and save functions
        SaveLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
                String BoxFilename="library.ser";
                FileOutputStream fos = null;
                ObjectOutputStream out = null;
                try {
                    fos = new FileOutputStream(BoxFilename);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(LibraryClauseContainer); //the top-level object to be saved
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
                System.out.println ("Called New Library function");
                //Create SpriteBox as icon
                SpriteBox b;
                String label = "New Library Document"; //unused
                String text = "replace with some text";
                String heading = "replace with a heading";
                String category = "clause"; //for now - check it later
                Clause myClause = new Clause(label,heading,text,category); 
                //create clause container
                ClauseContainer tempContainer = new ClauseContainer();
                libdocnum++;
                tempContainer.setDocName("CraigsLibrary"+Integer.toString(libdocnum));
                tempContainer.setType("library");
                tempContainer.addClause(myClause); //default clause for new container to work with
                b = new SpriteBox(myClause); //leave default settings to the 'setClause' method in SpriteBox
                b.setBoxContent(tempContainer);
                b.setOnMousePressed(PressLibBoxEventHandler); 
                b.setOnMouseDragged(DragBoxEventHandler);
                //place icon in Collection Stage (contents visible on double-click)
                
                // b.setInCollection(true); - not needed as done by Spritebox
                if (CollectionStage.isShowing()==true) {
                    placeInCollectionStage(b);
                    myCollection.addCC(tempContainer);
                }
                else {
                    placeSpriteOnMainStage(b);
                    wsCollection.addCC(tempContainer);
                }
                //check
                ArrayList<ClauseContainer> testData = new ArrayList<ClauseContainer>();
                testData = myCollection.getCollectionItems();
                System.out.println("Finished NewLibCont. \nTesting current Collection : "+testData.toString());
            }
        }); 

        //Toggle visibility of Document window
        viewDocument.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(DocumentStage);
            }
        });

        //Toggle visibility of Library window
        viewLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(LibraryStage);
            }
        });

        //Toggle visibility of Project window
        viewProject.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(ProjectStage);
            }
        });

        //Toggle visibility of Collection window
        viewCollection.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(CollectionStage);
            }
        });

        //Toggle visibility of output window
        viewtextmaker.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(textOutputStage);
            }
        });

        //toggle visibility of editor
        viewEditor.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(editorStage);
            }
        });

         //toggle visibility of importer
        viewImporter.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(importStage);
            }
        });

         //toggle visibility of toolbar
        viewToolbar.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(toolbarStage);
            }
        });

        SaveOutput.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            System.out.println("Save Output selected!");
            EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
            String savecontents = textmakerTextArea.getText();
            myfileApp.replaceText(savecontents);
            }
        });

        /* --- OBJECT MENU --- */
        NewClause.setOnAction(addNewClauseBox);
        NewDef.setOnAction(addNewDefBox);
        NewEvent.setOnAction(addNewEventBox);
        NewDocCont.setOnAction(addNewDocCont);
        
        /* --- IMPORT MENU ---   TO DO: File Open*/
        WordCount.setOnAction(updateWordCounts); //argument is an EventHandler with ActionEvent object
        GetDefText.setOnAction(extractDefinitions);
        GetDefs.setOnAction(makeDefIcons);
        GetClauses.setOnAction(makeClauseIcons);
        GetSections.setOnAction(importStatuteClauses);

        

        //add group layout object as root node for Scene at time of creation
        //defScene = new Scene (myGroup_root,650,300); //default width x height (px)
        defScene = new Scene (myGroup_root,myStageManager.getBigX(),myStageManager.getBigY(), Color.BEIGE);
        //scene.getRoot()).getChildren().addAll(menuBar);
        

        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("workspace");
             }
        });

               //
        myStage.setScene(defScene); //set current scene for the Stage
        //Position
        myStageManager.setPosition(myStage,"workspace");
        myStage.show();
        
        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuViews, menuObject,menuWorkspace, menuDocument, menuLibrary, menuOutput, menuImport,menuCollection,menuProject);     
        menubarGroup.getChildren().addAll(menuBar);
        VBox vbox1 = new VBox(0,menubarGroup);
        myGroup_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=650;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return myGroup_root;
        
    }

/* Setup Stage as a Container inspection and edit Window 
resuses the editorStage with new Scene and content for Container objects 
*/

public Pane setupContainerEditor(Stage myStage, String myTitle) {

        System.out.println("Setup Container editor Panel");
        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        //Group editorPanel_root = new Group(); 
        Pane editorPane = new Pane();

        Scene CCeditScene = new Scene (editorPane,400,400, Color.GREY); //default width x height (px)
        //optional event handler (cf editorScene.setOnMousePressed(EventHandler))
        CCeditScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected - Container edit! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("CCEditor");
        }
        });
        //TextArea and contents
        Text docnameTag = new Text("Doc/Lib Name:");
        docnameEdit = new TextArea();
        docnameEdit.setPrefRowCount(2);
        Text authorTag = new Text("Author:");
        authorEdit = new TextArea();
        authorEdit.setPrefHeight(100);
        authorEdit.setPrefWidth(400);
        authorEdit.setWrapText(true);
        Text notesTag = new Text("Notes:");
        notesEdit = new TextArea();
        notesEdit.setPrefRowCount(1);
        Text dateTag = new Text("Date:");
        CCdateEdit = new TextArea();
        CCdateEdit.setPrefRowCount(1);
        //
        SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
        System.out.println("Current sprite for edit:"+focusSprite.toString());
        Object myEditObject = focusSprite.getBoxContent();
        myEditCC = (ClauseContainer)myEditObject;
        System.out.println("Current Container for edit:"+myEditCC.toString());
        VBox vboxEdit;

        //TO DO: cater for library separately?
        vboxEdit = new VBox(0,docnameTag,docnameEdit,authorTag,authorEdit,notesTag,notesEdit,dateTag,CCdateEdit);
        
        //children vertical grow priority
        vboxEdit.setVgrow(docnameEdit,null);
        vboxEdit.setVgrow(authorEdit,null);
        vboxEdit.setVgrow(notesEdit,null);
        vboxEdit.setFillWidth(true); //for width of vbox children 
        System.out.println("FillWidth status: "+vboxEdit.isFillWidth());

        //put values in for curent Container (name, author etc)
        docnameEdit.setText(myEditCC.getDocName());
        authorEdit.setText(myEditCC.getAuthorName());
        notesEdit.setText(myEditCC.getNotes());
        CCdateEdit.setText(myEditCC.getDate());

        myStage.setScene(CCeditScene); //set current scene for the Stage
        //Layout
        myStageManager.setPosition(myStage,"editor");
        myStage.show();
        
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateContainerEditor);

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
        editorPane.getChildren().add(vboxAll); //add the vbox to the root node to hold everything
        /*int totalwidth=190;
        vboxAll.setPrefWidth(totalwidth); //this is in different units to textarea
        */
       
        //return the child node, not the root in this case? e.g.vBoxEdit?
        return editorPane;
}

/* Setup Stage as a Clause inspection and edit Window */

public Pane setupEditorPanel(Stage myStage, String myTitle) {

        System.out.println("Making editor Panel");
        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        //Group editorPanel_root = new Group(); 
        Pane editorPanel_root = new Pane();

        Scene editorScene = new Scene (editorPanel_root,400,400, Color.GREY); //default width x height (px)
        //optional event handler (cf editorScene.setOnMousePressed(EventHandler))
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
        labelEdit.setPrefColumnCount(80);
        Text headingTag = new Text("Clause heading:");
        headingEdit = new TextArea();
        headingEdit.setPrefRowCount(2);
        Text contentsTag = new Text("Clause text:");
        textEdit = new TextArea();
        //textEdit.setPrefRowCount(5);
        //textEdit.setPrefColumnCount(80);
        textEdit.setPrefHeight(100);
        textEdit.setPrefWidth(400);
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
        //first paramater is the spacing between childen
        vboxEdit = new VBox(0,headingTag,headingEdit,dateTag,dateEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        }
        else {
        //VBox vboxEdit = new VBox(0,labelTag,labelEdit,headingTag,headingEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        /* do not edit label separately for now*/
        vboxEdit = new VBox(0,headingTag,headingEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        //vboxEdit.setVgrow(textEdit, Priority.ALWAYS);
        }
        //children vertical grow priority
        vboxEdit.setVgrow(textEdit,null);
        vboxEdit.setVgrow(categoryEdit,null);
        vboxEdit.setVgrow(dateEdit,null);
        vboxEdit.setFillWidth(true); //for width of vbox children 
        System.out.println("FillWidth status: "+vboxEdit.isFillWidth());
        //Appearance for specific types of Clauses
        if (editClause.getCategory().equals("definition")) {
            headingTag.setText("Defined term:");
            contentsTag.setText("means:");
        }
        /*if (editClause.getCategory().equals("event")) {
            headingTag.setText("Event:");
            contentsTag.setText("Description:");
            dateTag.setText("Date:");
            dateEdit.setText(editClause.getEventDate());
            //TO DO: add participants/witness edit
        }
        */

        if (editClause instanceof Event) {
            headingTag.setText("Event:");
            contentsTag.setText("Description:");
            dateTag.setText("Date:");
            dateEdit.setText(((Event)editClause).getDate());
        }

        labelEdit.setText(editClause.getHeading());
        headingEdit.setText(editClause.getHeading());
        textEdit.setText(editClause.getClause());
        categoryEdit.setText(editClause.getCategory());

        myStage.setScene(editorScene); //set current scene for the Stage
        //Layout
        myStageManager.setPosition(myStage,"editor");
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
        toolbarScene = new Scene (toolbar_root,150,350, Color.GREY); //default width x height (px)
        //optional event handler
        toolbarScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Clause Toolbar: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("Toolbar");
             }
        });

        //
        myStage.setScene(toolbarScene); //set current scene for the Stage
        //Layout
        myStageManager.setPosition(myStage,"toolbar");
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

        //Button for moving clauses to Document
        Button btnMoveClauseDoc = myControlsManager.newStdButton();
        btnMoveClauseDoc.setText("Move to Document");
        btnMoveClauseDoc.setTooltip(new Tooltip ("Press to move clause to Document Window"));
        btnMoveClauseDoc.setOnAction(MoveClausetoDoc);

        //Button for copying clause to document (leaves copy behind)
        Button btnCopyClauseDoc = myControlsManager.newStdButton();
        btnCopyClauseDoc.setText("Copy to Document");
        btnCopyClauseDoc.setTooltip(new Tooltip ("Press to copy clause to Document Window"));
        btnCopyClauseDoc.setOnAction(CopyClausetoDoc);

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

        //Button for copying ClauseContainer to Collection (leaves copy in workspace)
        Button btnCopyCC = myControlsManager.newStdButton();
        btnCopyCC.setText("Copy to Collection");
        btnCopyCC.setTooltip(new Tooltip ("Press to copy Container to Collection"));
        btnCopyCC.setOnAction(CopyCCtoCollection);


        Button btnDoEdit = myControlsManager.newStdButton();
        btnDoEdit.setText("Edit");
        btnDoEdit.setTooltip(new Tooltip ("Press to Edit Selection (Red Block)"));
        btnDoEdit.setOnAction(DoEditStage);

        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClauseWS,btnCopyClause);
        //VBox vbox1 = new VBox(0,btnNewDef,btnNewClause,btnCopyCC,btnMoveClauseWS,btnCopyClauseWS,btnMoveClauseDoc, btnCopyClauseDoc, btnCopyClauseLib,btnMoveClauseLib,btnDeleteClause,btnDoEdit);
        VBox vbox1 = new VBox(0,btnCopyCC,btnMoveClauseWS,btnCopyClauseWS,btnMoveClauseDoc, btnCopyClauseDoc, btnCopyClauseLib,btnMoveClauseLib,btnDeleteClause,btnDoEdit);
        
        //
        toolbar_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        return toolbar_root;
}


/** Setup independent definitions window 
@Returns a Scrollpane representing the root node

@notes Scene size will determine initial width of Stage window 

**/

public ScrollPane setupDefinitionsWindow() {
        
        defsTextStage = new Stage();
            
        //create a scrollpane as root with a text area inside
        ScrollPane rootnode_scroll = new ScrollPane();
        rootnode_scroll.setFitToHeight(true);
        rootnode_scroll.setFitToWidth(true);
        double width = 800; 
        double height = 500; 
        textArea3.setPrefHeight(height);  
        textArea3.setPrefWidth(width);
        textArea3.setWrapText(true);
        rootnode_scroll.setContent(textArea3); 

        //give rootnode its Scene with event handler
        int setWidth=500;
        int setHeight=500;
        Scene defsTextScene = new Scene (rootnode_scroll,setWidth,setHeight); //width x height (px)
        defsTextScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected for text scroll window! " + mouseEvent.getSource());
             }
        });
        //Size and positioning
        defsTextStage.setScene(defsTextScene);
        defsTextStage.setTitle("Definitions List");
        myStageManager.setPosition(defsTextStage, "display");
        defsTextStage.setY(350);
        defsTextStage.show();
        //TO DO: return Stage object
        return rootnode_scroll; 
        }

//Function to setup independent output window

public ScrollPane setupTextOutputWindow() {

        textOutputStage = new Stage();

        //setup root node as a scrollpane
        ScrollPane rootnode_scroll = new ScrollPane();
        rootnode_scroll.setFitToHeight(true);
        rootnode_scroll.setFitToWidth(true);
        //give rootnode a Scene and event handlers for scene
        int setWidth=500;
        int setHeight=250;
        textOutputScene = new Scene (rootnode_scroll,setWidth,setHeight); //width x height (px)
        textOutputScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
             }
        });
        //Size and positioning
        textOutputStage.setScene(textOutputScene);
        myStageManager.setPosition(textOutputStage, "textmaker");
        textOutputStage.setTitle("Text Output");
        //setup text scroll node
        double width = 600; 
        double height = 500; 
        rootnode_scroll.setPrefHeight(height);  
        rootnode_scroll.setPrefWidth(width);
        rootnode_scroll.setContent(textmakerTextArea); 
        //text area settings
        textmakerTextArea.setWrapText(true);
        textmakerTextArea.setText("Some future contents");
        //default is hidden
        textOutputStage.hide();
        return rootnode_scroll;
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
a RELATIVE inequality or division of social, economic or legal power that defines a transaction or structure, and the role of the participants.

TO DO: put into groups for managing different areas of law, but iterate through all.
*/

public Boolean isLegalRoleWord (String myWord) {
    ArrayList<String> RoleWords = new ArrayList<String>(Arrays.asList("employer","employee","landlord","tenant","lessor","lessee","director","shareholder","trustee","beneficiary", "debtor","creditor", "payor", "payee","mortgagor","mortgagee","regulator","manager","partner","owner","guarantor","guarantee","seller","buyer","vendor","purchaser","grantor","grantee","distributor","bailor","bailee","master","servant","licensor","licensee","developer","carrier","lender","borrower"));
    Iterator<String> myIterator = RoleWords.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
        String checkWord = myIterator.next();
        if (myWord.equalsIgnoreCase(checkWord)) { //use equals fo case checking
            return true;
        }
    }
    return false;
}

/*Method to iterate through all sprites on Workspace */
public void removeBoxesFromStage(Group targetGroup) {
    //loop:
    //.getChildren().remove(Spritebox??); 
    //remove associated clauses
    //should clauses keep record of parent SpriteBox ref?
    /*ArrayList<Clause> myWSList = WorkspaceClauseContainer.getClauseArray();
        Iterator<Clause> myDeleteList = myWSList.iterator();
        while (myiterator.hasNext()) {
            //removeFromMainStage()
            //Delete WS Spritebox from Stage
        }
        */
    }


/* Method to remove spritebox from workspace */
public void removeFromMainStage(SpriteBox thisSprite) {
    /*
        WorkspaceGroup.getChildren().remove(thisSprite); 
        mySpriteManager.removeFromMainStage(thisSprite);
        */
    }

/* Method to open the contents of currentSprite DocContainer in Document Stage */
public void openDocumentContainerStage(ClauseContainer myContainer) {
    
    //point to same container
    DocumentClauseContainer=myContainer;
    mySpriteManager.resetDocXY(); //may not be needed in function below
    displaySpritesInDocumentStage(DocumentClauseContainer, "Current Document");
    }

/* Method to open the contents of currentSprite DocContainer in Document Stage */
public void openLibraryContainerStage(ClauseContainer myContainer) {
    
    /* This doesn't pick up metadata, only clauses:
    LibraryClauseContainer.setClauseArray(myContainer.getClauseArray());
    */
    LibraryClauseContainer=myContainer;
    mySpriteManager.resetLibXY(); //may not be needed
    displaySpritesInLibraryStage(LibraryClauseContainer, "Current Library");
    }


/* Method to handle all consequences of adding spritebox to workspace
1. Add to workspace group (implicitly removes from existing group)
2. update state in sprite manager
3. add to collections (if needed)
*/

    public void placeSpriteOnMainStage(SpriteBox thisSprite) {
        WorkspaceGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeOnMainStage(thisSprite);
        WorkspaceBoxes.addBox(thisSprite);
    }

/* Method to handle all consequences of adding spritebox to Collection
1. Add to library group
2. update state in sprite manager (i.e. position)
3. add to collections (if needed)
*/
    public void placeInCollectionStage(SpriteBox thisSprite) {
        System.out.println("Sprite received for placing in Collection:"+thisSprite.toString());
        CollectionGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeInCollection(thisSprite);
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
        WorkspaceBoxes.addBox(thisSprite);
    }

/* Method to handle all consequences of adding spritebox to Document stage
1. Add to document group
2. update state in sprite manager (i.e. position)
3. add to collections (if needed)
*/
    public void placeInDocumentStage(SpriteBox thisSprite) {
        System.out.println("Sprite received for placing in Document:"+thisSprite.toString());
        DocumentGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeInDocument(thisSprite);
        WorkspaceBoxes.addBox(thisSprite);
    }

    public void placeInOtherStage(Stage myStage, Group thisGroup, SpriteBox thisSprite) {
        System.out.println("Sprite received for placing in OtherStage:"+thisSprite.toString());
        thisGroup.getChildren().add(thisSprite);
        thisSprite.setInOtherStage(true); 
        mySpriteManager.placeInOtherStage(thisSprite);
        WorkspaceBoxes.addBox(thisSprite);
            //set current box as the current
            //myStageManager.setCurrentSprite(b);
    }

/* Method to copy SpriteBox including event handlers needed
It takes just the clause from the existing Sprite and builds rest from scratch */
    public SpriteBox makeCopySprite (SpriteBox mySprite) {
        Object copyClause = mySprite.getClause(); //this copies the pointer, not contents
        System.out.println(copyClause.toString());
        //copy values for clause, not references
        SpriteBox copySprite;
        if (copyClause instanceof Event) {
            Event freshEvent = makeCopyEvent((Event)copyClause);
            copySprite = new SpriteBox(freshEvent);
        }
        else {
            Clause freshClause = makeCopyClause((Clause)copyClause);
            copySprite = new SpriteBox(freshClause);
        }
        copySprite.setOnMousePressed(PressBoxEventHandler); 
        copySprite.setOnMouseDragged(DragBoxEventHandler);
        return copySprite;
    }

/*Method to copy contents of SpriteBox with Document(ClauseCollection) */
 public SpriteBox makeCopyCollectionSprite (SpriteBox mySprite) {
        ClauseContainer copyCC = (ClauseContainer)mySprite.getBoxContent(); //this copies the pointer, not contents
        ClauseContainer tempContainer = copyCC.cloneContainer();
        myCollection.addCC(tempContainer); 
        System.out.println(copyCC.toString());
        SpriteBox copySprite = new SpriteBox();
        copySprite.setBoxContent(tempContainer);
        //event handlers
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
    anotherClause.setFreq(myClause.getFreq());
    return anotherClause;
}

//make copy of Event
public Event makeCopyEvent(Event myClause) {
    Event anotherClause = new Event();
    anotherClause.setEventDesc(myClause.getEventDesc());
    anotherClause.setDate(myClause.getDate());
    anotherClause.setClauselabel(myClause.getLabel());
    anotherClause.setClausetext(myClause.getClause());
    anotherClause.setHeading(myClause.getHeading());
    anotherClause.setCategory(myClause.getCategory());
    anotherClause.setFreq(myClause.getFreq());
    return anotherClause;
    }

/* Method to remove current SpriteBox and contained clause from system 
TO DO: Cater for other windows e.g. new loaded workspace window...
*/
public void deleteSprite(SpriteBox mySprite) {
    System.out.println("in Lib: "+mySprite.isInLibrary());
    System.out.println("on WS: "+mySprite.isOnStage());
    System.out.println("in Doc: "+mySprite.isInDocumentStage());
    System.out.println("in Collection: "+mySprite.isInCollection());
    if (mySprite.isInLibrary()==true) {
        LibraryClauseContainer.removeClause(mySprite.getClause());
        LibraryGroup.getChildren().remove(mySprite); 
        mySprite.setInLibrary(false); //not needed?
        //do this to refresh - or just Library window?
        //ParentStage.show();
        LibraryStage.show();
        return;
    }
    if (mySprite.isOnStage()==true) {
        WorkspaceClauseContainer.removeClause(mySprite.getClause());
        wsCollection.removeCC(mySprite.getCC());
        WorkspaceGroup.getChildren().remove(mySprite); 
        mySprite.setOnStage(false); //not needed?
        //do this to refresh
        ParentStage.show();
        return;
    }

    if (mySprite.isInDocumentStage()==true) {
        DocumentClauseContainer.removeClause(mySprite.getClause());
        DocumentGroup.getChildren().remove(mySprite); 
        mySprite.setInDocumentStage(false); //not needed?
        //do this to refresh
        DocumentStage.show();
        return;
    }
    if (mySprite.isInCollection()==true) {
        myCollection.removeCC(mySprite.getCC());
        CollectionGroup.getChildren().remove(mySprite); 
        mySprite.setInCollection(false); //not needed?
        //do this to refresh
        CollectionStage.show();
        return;
    }
    if (mySprite.isInOtherStage()==true) {
        //DocumentClauseContainer.removeClause(mySprite.getClause());
        //DocumentGroup.getChildren().remove(mySprite);
        System.out.println("Removing ["+mySprite.toString()+"] from Parent: "+mySprite.getParent().toString()); 
        ((Group) mySprite.getParent()).getChildren().remove(mySprite);
        mySprite.setInOtherStage(false); //not needed?
        //do this to refresh
        //DocumentStage.show();
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
        WorkspaceGroup = Main.this.setupWorkspaceStage(ParentStage, "Main Workspace");

        //setup Project Libary window
        ProjectLibStage = new Stage();
        myStageManager.setStageParent(ParentStage,ProjectLibStage);
        ProjectLibGroup = Main.this.setupProjectLibStage();

        //setup Project window
        ProjectStage = new Stage();
        myStageManager.setStageParent(ParentStage,ProjectStage);
        ProjectGroup = Main.this.setupProjectOpenStage();

        //setup Collection window
        CollectionStage = new Stage();
        myStageManager.setStageParent(ParentStage,CollectionStage);
        CollectionGroup = Main.this.setupCollectionStage();

        //setup libary window
        LibraryStage = new Stage();
        myStageManager.setStageParent(ParentStage,LibraryStage);
        LibraryGroup = Main.this.setupLibraryStage();

        //setup Document window
        DocumentStage = new Stage();
        myStageManager.setStageParent(ParentStage,DocumentStage);
        DocumentGroup = Main.this.setupDocumentStage();

        //setup Editor window
        editorStage = new Stage();
        myStageManager.setStageParent(ParentStage,editorStage);

        //*setup Import window (text input display and editing)
        importStage = new Stage();
        myStageManager.setStageParent(ParentStage,importStage);
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
        myStageManager.setStageParent(ParentStage,toolbarStage);
        toolbarGroup = Main.this.setupToolbarPanel(toolbarStage, "Toolbar");

        /* Setup default Output Stage  */
        
        textmakerGroup_root = Main.this.setupTextOutputWindow();

        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

    /* This is a method to create eventhandler for Document ClauseContainer objects */

    EventHandler<MouseEvent> PressDocBoxEventHandler = 
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
                    System.out.println(currentSprite.toString());
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

                    //SpriteInternal instanceof ClauseContainer) 
                    ClauseContainer tempContainer = (ClauseContainer)currentSprite.getBoxContent();
                    System.out.println("Detected Doc ClauseContainer double click");
                    openDocumentContainerStage(tempContainer);
                    

                    break;
                case 3:
                    System.out.println("Three clicks");
                    break;
            }
            t.consume(); //trying this to see if it frees up for second press but better to deal with cause
        }
    };
    
    /* This is a method to create eventhandler for Library ClauseContainer objects */

    EventHandler<MouseEvent> PressLibBoxEventHandler = 
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
                    System.out.println(currentSprite.toString());
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

                    //SpriteInternal instanceof ClauseContainer) 
                    ClauseContainer tempContainer = (ClauseContainer)currentSprite.getBoxContent();
                    System.out.println("Detected Lib ClauseContainer double click");
                    openLibraryContainerStage(tempContainer);
                    

                    break;
                case 3:
                    System.out.println("Three clicks");
                    break;
            }
            t.consume(); //trying this to see if it frees up for second press but better to deal with cause
        }
    };
    


    /* This is a method to create a new eventhandler for the SpriteBox objects.
     These are Stackpane that incorporate a Rectangle and a Text Node as components
     They contain Clauses rather than Clause Containers

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
                    System.out.println(currentSprite.toString());
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
                    if (textOutputStage.isShowing()==false) {
                        textOutputStage.show();
                    }
                    textmakerTextArea.setText(myOutput);
                    */
                    Object SpriteInternal = currentSprite.getBoxContent();
                    if (SpriteInternal instanceof Clause) {
                        System.out.println("Detected Clause double click");
                        // show editor window instead, for current Sprite
                        //editorStage = new Stage();
                        editGroup_root = Main.this.setupEditorPanel(editorStage, "Editor");
                    }

                    if (SpriteInternal instanceof ClauseContainer) {
                        ClauseContainer tempContainer = (ClauseContainer)currentSprite.getBoxContent();
                        System.out.println("Detected ClauseContainer double click");
                        openDocumentContainerStage(tempContainer);
                    }

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
            //SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            SpriteBox hadFocus = mySpriteManager.getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                        System.out.println("Deleting..."+hadFocus.toString());
                        deleteSprite(hadFocus);
                    }
                    else {
                        System.out.println("No current sprite to delete");
                    }
            //currentSprite.endAlert();
            /* For now, use single delete function, alternatively use different
            functions depending on where SpriteBox is located 
            */
            //deleteSprite(currentSprite);
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
            //assume it could only be in library for now - not stage specific?
             if (currentSprite.isInDocumentStage()==true) {
                DocumentClauseContainer.removeClause(currentSprite.getClause());
                currentSprite.setInDocumentStage(false);
            }
            if (currentSprite.isInLibrary()==true) {
                DocumentClauseContainer.removeClause(currentSprite.getClause());
                currentSprite.setInLibrary(false);
            }

            if (currentSprite.isOnStage()==false) {
            /* add clause to the list of clauses in the clause array */
                WorkspaceClauseContainer.addClause(currentSprite.getClause()); 
                //add sprite to WS Stage. This will clean up object on Stage elsewhere...
                placeSpriteOnMainStage(currentSprite);
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
             //cleanup stage refs.  TO DO: use sprite manager
            
            //remove from other clauses list (so it is not saved)
            if (currentSprite.isOnStage()==true) {
                WorkspaceClauseContainer.removeClause(currentSprite.getClause());
            }
            if (currentSprite.isInDocumentStage()==true) {
                DocumentClauseContainer.removeClause(currentSprite.getClause());
            }
            //add sprite to Library Stage. This will clean up same object on Workspace...
            if (currentSprite.isInLibrary()==false) {
            /* add clause to the list of clauses in the Library clause array */
                LibraryClauseContainer.addClause(currentSprite.getClause()); 
                placeInLibraryStage(currentSprite);
                //WorkspaceBoxes.removeBox(currentSprite);
            }
        }
    };

    EventHandler<ActionEvent> MoveClausetoDoc = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            WorkspaceBoxes.removeBox(currentSprite); //cleanup stage refs.  TO DO: use sprite manager
            if (currentSprite.isInDocumentStage()==false) {
            /* add clause to the list of clauses in the Library clause array */
            DocumentClauseContainer.addClause(currentSprite.getClause()); 
            }
            //remove from Workspace clause list (so it is not saved)
            if (currentSprite.isOnStage()==true) {
                WorkspaceClauseContainer.removeClause(currentSprite.getClause());
            }
            if (currentSprite.isInLibrary()==true) {
                LibraryClauseContainer.removeClause(currentSprite.getClause());
            }
            //add sprite to Document Stage. This will clean up same object on Workspace...
            placeInDocumentStage(currentSprite);
            }
    };

    EventHandler<ActionEvent> CopyCCtoCollection = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            if (currentSprite==null) {
                System.out.println("Current sprite not detected");
            }
            if (currentSprite.isInCollection()==false) {
            //copy Spritebox and clause contents
            SpriteBox copySprite = makeCopyCollectionSprite(currentSprite);
            System.out.println(copySprite.toString());
            //add sprite to stage and to Collection
            placeInCollectionStage(copySprite);
            ClauseContainer tempContainer = (ClauseContainer)copySprite.getBoxContent();
            myCollection.addCC(tempContainer);
            //check
            ArrayList<ClauseContainer> testData = new ArrayList<ClauseContainer>();
            testData = myCollection.getCollectionItems();
            System.out.println("Finished copy to CC. \nTesting current Collection : "+testData.toString());
            if (CollectionStage.isShowing()==false) {
                CollectionStage.show();
            }
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

    EventHandler<ActionEvent> CopyClausetoDoc = 
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
            if (currentSprite.isInDocumentStage()==false) {
            //copy Spritebox and clause contents
            SpriteBox copySprite = makeCopySprite(currentSprite);
            /* add clause to the list of clauses in the Library clause array */
            DocumentClauseContainer.addClause(copySprite.getClause());  
            //add sprite to Library Stage. 
            System.out.println(copySprite.toString());
            placeInDocumentStage(copySprite);
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
                placeSpriteOnMainStage(copySprite); 
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
        //editorStage = new Stage();
        SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
        Object mycontents = focusSprite.getBoxContent();
        if (mycontents instanceof ClauseContainer) {
            editGroup_root = Main.this.setupContainerEditor(editorStage, "Editor");
        }
        else {
                editGroup_root = Main.this.setupEditorPanel(editorStage, "Editor");
            }    
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
            //add clause from sprite to clauses container
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
             if (DocumentStage.isShowing()==true) {
                    placeInDocumentStage(b);
                    DocumentClauseContainer.addClause(b.getClause());
                }
                else {
                    placeSpriteOnMainStage(b);
                    WorkspaceClauseContainer.addClause(b.getClause()); 
                } 
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
            b = new SpriteBox(); 
            b.setClause(myClause);
            //event handler
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            if (DocumentStage.isShowing()==true) {
                    placeInDocumentStage(b);
                    DocumentClauseContainer.addClause(myClause);
                }
                else {
                    placeSpriteOnMainStage(b);
                    WorkspaceClauseContainer.addClause(myClause); 
                } 
            }
        };

    //addNewDocCont
    /* Event handler for adding a new Document Container to Collection area */

    EventHandler<ActionEvent> addNewDocCont = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            System.out.println ("Called New Document function");
            //Create SpriteBox as icon
            SpriteBox b;
            String label = "New Clause for Document"; //for the label on box
            String text = "replace with some text";
            String heading = "replace with a heading";
            String category = "clause"; //for now - check it later
            Clause myClause = new Clause(label,heading,text,category); 
            b = new SpriteBox(myClause); //leave default settings to the 'setClause' method in SpriteBox
            //create clause container
            ClauseContainer tempContainer = new ClauseContainer();
            loaddocnum++;
            tempContainer.setDocName("CraigsDocument"+Integer.toString(loaddocnum));
            tempContainer.setType("document");
            tempContainer.addClause(myClause); //default clause for new container to work with
            //
            b.setBoxContent(tempContainer);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            //place icon in Collection Stage (contents visible on double-click)
            if (CollectionStage.isShowing()==true) {
                //CollectionStage.show();
                placeInCollectionStage(b);
                myCollection.addCC(tempContainer);  
            }
            else {
                placeSpriteOnMainStage(b);
                wsCollection.addCC(tempContainer);
            }
            //check
            ArrayList<ClauseContainer> testData = new ArrayList<ClauseContainer>();
            testData = myCollection.getCollectionItems();
            System.out.println("Finished NewDocCont. \nTesting current Collection : "+testData.toString());
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
             //add clause from sprite to clauses container
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            if (DocumentStage.isShowing()==true) {
                placeInDocumentStage(b);
                DocumentClauseContainer.addClause(b.getClause());
            }
            else {
                placeSpriteOnMainStage(b); 
                WorkspaceClauseContainer.addClause(b.getClause());
            }
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
        ArrayList<Clause> myDList = WorkspaceClauseContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        int offX=50;
        int offY=50;
        while (myiterator.hasNext()) {
            Clause thisClause = myiterator.next();
            if (isLegalRoleWord(thisClause.getLabel())==true) {
                thisClause.setCategory("legalrole");
            }
            if (thisClause instanceof Event) {
                System.out.println("Displaying clause on workspace that is Event:"+thisClause.toString());
            }
            SpriteBox b = new SpriteBox(thisClause);
            //location
            b.setTranslateX(offX);         
            b.setTranslateY(offY);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            WorkspaceGroup.getChildren().add(b);
            b.setOnStage(true);
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

    /* 
    Display Sprites in existing Library Stage. nb mySpriteManager.resetLibXY();
    */

    public void displaySpritesInLibraryStage(ClauseContainer inputContainer, String myTitle) {
        if (LibraryStage==null) {
            System.out.println("Problem with Library Stage setup");
        }

        ScrollPane outerScroll = new ScrollPane();
        LibraryGroup = new Group(); //clean the existing stage (no archive)
        outerScroll.setContent(LibraryGroup); //add myGroup as child of scrollpane
        //add scrollpane as root node for Scene of set size
        LibraryScene = new Scene (outerScroll,650,400); //default width x height (px)
        //add event handler for mouse event
        LibraryScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on scene detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("library");
             }
        });

        LibraryStage.setScene(LibraryScene); //this selects this stage as current scene
        //DocumentStage.setTitle(myTitle);
        LibraryStage.setTitle(inputContainer.getDocName());
        
        //process
        ArrayList<Clause> myDList = inputContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        mySpriteManager.resetLibXY();
        while (myiterator.hasNext()) {
            Clause thisClause = myiterator.next();
            if (isLegalRoleWord(thisClause.getLabel())==true) {
                thisClause.setCategory("legalrole");
            }
            SpriteBox b = new SpriteBox(thisClause);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            //add to Library Group
            placeInLibraryStage(b);
        }
        LibraryStage.show();
        //return Library;
        } 

/*

Create new scene from Clause Containers that updates the Collection Stage.

*/
    public void displaySpritesInCollectionStage(Collection inputCollection, String myTitle) {
        //like a general blockswindow setup
        System.out.println("Displaying Collection:"+inputCollection.toString());
        ScrollPane outerScroll = new ScrollPane();
        CollectionGroup = new Group(); //refresh
        outerScroll.setContent(CollectionGroup); //add myGroup as child of scrollpane
        //add a new scrollpane as root node for Scene of set size
        CollectionScene = new Scene (outerScroll,650,400); //default width x height (px)
        //add event handler for mouse event
        CollectionScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on Collection scene detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("collection");
             }
        });

        CollectionStage.setScene(CollectionScene); //this selects this stage as current scene
        CollectionStage.setTitle(myTitle);
        
        myStageManager.setPosition(CollectionStage,"collection");

        //process
        ArrayList<ClauseContainer> myCCList = inputCollection.getCollectionItems();
        System.out.println("Items:"+inputCollection.getCount());
        Iterator<ClauseContainer> myiterator = myCCList.iterator();

        mySpriteManager.resetCollXY();
        while (myiterator.hasNext()) {
            ClauseContainer myCC = myiterator.next();
            String tempType = myCC.getType();
            SpriteBox b = new SpriteBox();
            b.setBoxContent(myCC); //insert the relevant container
            
            //event handlers
            b.setOnMouseDragged(DragBoxEventHandler);
            if (tempType.equals("library")) {
                b.setOnMousePressed(PressLibBoxEventHandler);
            }
            else {
                b.setOnMousePressed(PressDocBoxEventHandler); 
            }
            placeInCollectionStage(b);
        }
        CollectionStage.show();
        }    

/*

Create new scene from input Clause Container that updates the Document Stage.

*/
    public void displaySpritesInDocumentStage(ClauseContainer inputContainer, String myTitle) {
        
        //Use Pane as root Node for Scene
        ScrollPane outerScroll = new ScrollPane();
        DocumentGroup = new Group(); //clean the existing stage (no archive)
        outerScroll.setContent(DocumentGroup); //add myGroup as child of scrollpane
        //add scrollpane as root node for Scene of set size
        DocumentScene = new Scene (outerScroll,650,400); //default width x height (px)
        //add event handler for mouse event
        DocumentScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on scene detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("document");
             }
        });

        DocumentStage.setScene(DocumentScene); //this selects this stage as current scene
        //DocumentStage.setTitle(myTitle);
        DocumentStage.setTitle(inputContainer.getDocName());
        
        myStageManager.setPosition(DocumentStage,"document");
        //process Container clauses
        ArrayList<Clause> myDList = inputContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        mySpriteManager.resetDocXY();
        while (myiterator.hasNext()) {
            Clause thisClause = myiterator.next();
            if (isLegalRoleWord(thisClause.getLabel())==true) {
                thisClause.setCategory("legalrole");
            }
            //create a sprite for this Definition
            SpriteBox b = new SpriteBox(thisClause);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            placeInDocumentStage(b);
        }
        DocumentStage.show();
        }    
  


/*Create adHoc Stage designed to hold Spriteboxes (rectangles etc)
Return Stage because it is the top level Java FX container.
THe Scene is a container of nodes that is added to the Stage.
Scene nodes include scrollpanes, rectangles etc.
The root node should be a node that can have children e.g. Group, Scrollpane.

TO DO/OPTION:
Have a single stage for WIP document.  Then change the scene displayed
(where each scene is a collection of clauses displayed as rectangle boxes)
Event handlers will apply to new scene added to that stage.

*/
    public Stage displaySpritesInNewStage(ClauseContainer inputContainer, String myTitle) {
        Stage myStage = new Stage();
        //Scene contents: a Group within Scrollpane (Scrollpane is the Scene's root node)
        ScrollPane outerScroll = new ScrollPane();
        Group myGroup = new Group();
        outerScroll.setContent(myGroup); 
        //now give the root node its Scene, then add event listeners
        Scene myScene = new Scene (outerScroll,650,400); //default width x height (px)
        myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on scene detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("blocks");
             }
        });
        
        //Stage settings, including title
        myStage.setScene(myScene); //this selects this stage as current scene
        myStage.setTitle(myTitle);
        myStage.setY(600);
        myStageManager.setPosition(myStage, "icons");
        myStage.show();

        //------process blocks for display
        ArrayList<Clause> myDList = inputContainer.getClauseArray();
        Iterator<Clause> myiterator = myDList.iterator();

        mySpriteManager.resetOtherXY();
        while (myiterator.hasNext()) {
            Clause thisClause = myiterator.next();
            if (isLegalRoleWord(thisClause.getLabel())==true) {
                thisClause.setCategory("legalrole");
            }
            SpriteBox b = new SpriteBox(thisClause);
            //event handlers
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            //add Spriteboxes (rectangles) to the Group in the Scrollpane in Scene
            placeInOtherStage(myStage,myGroup,b);
        }
        myStage.show();
        //return defGroup;
        return myStage;
        }    
     
    /* Method to load up a library from serialized disk data
    Uses the Main instance variable for library name.
    Currrently loads icon (sprite) to Workspace.
    */

        public void libraryLoader() {
            FileInputStream fis = null;
            ObjectInputStream in = null;
            String libraryFName = "library.ser";
            ClauseContainer myLibraryIn = new ClauseContainer();
            try {
                fis = new FileInputStream(libraryFName);
                in = new ObjectInputStream(fis);
                myLibraryIn = (ClauseContainer)in.readObject();
                in.close();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            catch(ClassNotFoundException ex)
            {
                 ex.printStackTrace();
            }
            System.out.println("Library Load Success!");
            System.out.println(myLibraryIn.toString());
            //
            //tempContainer.addClause(myClause); //default clause for new container to work with
            SpriteBox b = new SpriteBox(); //leave default settings to the 'setClause' method in SpriteBox
            b.setBoxContent(myLibraryIn);
            b.setOnMousePressed(PressLibBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            //place icon in Collection Stage (contents visible on double-click)
            if (CollectionStage.isShowing()==true) {
                placeInCollectionStage(b);
                myCollection.addCC(myLibraryIn);
            }
            else {
                placeSpriteOnMainStage(b);
                wsCollection.addCC(myLibraryIn);
            }
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
                Stage myStage = new Stage();
               
                //Root node is Scrollpane containing a group
                ScrollPane outerScroll = new ScrollPane();
                Group CountGroup_root = new Group();
                outerScroll.setContent(CountGroup_root); 
                //now give the root node its Scene, then add event listeners
                Scene myScene = new Scene (outerScroll,650,600); //default width x height (px)
                myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                     @Override
                     public void handle(MouseEvent mouseEvent) {
                     System.out.println("Mouse click on scene detected! " + mouseEvent.getSource());
                     mySpriteManager.setStageFocus("blocks");
                         }
                    });
                
                //Stage settings, including title
                myStage.setScene(myScene); //this selects this stage as current scene
                myStage.setTitle("Common Words Window");
                myStage.setY(600);
                myStageManager.setPosition(myStage, "icons");
                myStage.show();

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
            //make a new stage
            defsTextStage_root = Main.this.setupDefinitionsWindow();
            //Outer class method class to obtain text from analysis area
            String gotcha = Main.this.textArea1.getText();
            String newDefs = Main.this.getMatched(gotcha);
            //now set the content of text area inside scrollpane to our extracted text
            textArea3.setText(newDefs);
            System.out.println("Get Defs Button was pressed!");
            }
        };
        
        /* Update Container in Container Editor */
        
        EventHandler<ActionEvent> UpdateContainerEditor = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            myEditCC.setDocName(docnameEdit.getText());
            myEditCC.setAuthorName(authorEdit.getText());
            myEditCC.setNotes(notesEdit.getText());
            myEditCC.setDate(CCdateEdit.getText());
            System.out.println("Container updated!");
            //update the SpriteBox content with updated CC, which will update GUI
            SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
            focusSprite.setBoxContent(myEditCC);
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
            //if (editClause.getCategory().equals("event")) {
            if (editClause instanceof Event) {
                ((Event)editClause).setDate(dateEdit.getText());
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