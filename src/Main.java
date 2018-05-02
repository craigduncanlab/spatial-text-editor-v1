/** 

This application creates a GUI as a legal doc staging, editing & visualisation environment

JavaFX implementation of GUI started 17.11.2017 by Craig Duncan

*/
 

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
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
//Scene - general appearance & layout of Background Fills, Stages, nodes
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
//Scene colour and Background Fills
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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
    ControlsManager myControlsManager = new ControlsManager();
    //
    StageManager ParentStageSM;//= new StageManager();
    Stage ParentStage;
    //Main Stage (Workspace window) that owns all other Stages
    StageManager Stage_WS;
    Stage WorkspaceStage;
    //Group WorkspaceGroup; //deprecated
    BoxContainer WorkspaceBoxes; //A serializable top-level container (optional)
    ClauseContainer wsCollection = new ClauseContainer(); //for holding workspace contents (inside boxes)
    //Opus = project collection.  Display Projects as Icons, as in a library.
    StageManager Stage_PROJLIB;
    StageManager Stage_TEST;
    ClauseContainer projectLibNode = new ClauseContainer();
    //ProjectOpen Stage (to display contents of each Project i.e. an open Project with Collection(s), MergeData etc)
    StageManager Stage_PROJ;
    ClauseContainer projectNode = new ClauseContainer(); //currently opened project
    //To do: MergeDataWindow
    //Collection Stage (to hold groups of libraries and documents).. i.e an open Collection.
    StageManager Stage_COLL;
    
    ClauseContainer collectionNode = new ClauseContainer(); 
    
    //the curently open Collection.
    //To hold groups of Clauses in SpriteBoxes (as needed) i.e. an Open Document.
    StageManager Stage_DOC;
    Stage DocumentStage;
    Scene DocumentScene;
    Group DocumentGroup;
    String BoxFilename="document.ser";
    ClauseContainer documentNode = new ClauseContainer(); 
    
    //Library Window (for display of the Open Library)
    StageManager Stage_LIB;
    Stage LibraryStage=null;
    Scene LibraryScene;
    Group LibraryGroup;
    String LibFilename="library.ser";
    ClauseContainer libraryNode = new ClauseContainer(); //library import/save
    //ImportStage
    StageManager Stage_Import;
    Stage ImportStage;
    Scene ImportScene; // scene for adding on textStage.
    ScrollPane import_rootnode_scroll; //root Node for import stage

    //Text Output windows (no edits)
    StageManager Stage_Output;
    StageManager Stage_Definitions = new StageManager();
    
    //Display SpriteBoxes window(s)
    Scene workspaceScene;  //<----used multiple times in different methods.  TO DO:  localise Scene variables.
    //Group defGroup_root; //<---used for display Sprites in new stage
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Toolbar
    StageManager Stage_Toolbar;
    Stage toolbarStage = null;
    Group toolbarGroup = null;
    Scene toolbarScene = null;   
    //Clause editor
    StageManager Stage_EDITNODEPROP;
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
    //move active sprite tracking to here from spritemanager class (redundant)
    SpriteBox activeSprite;
    SpriteTracker myTracker = new SpriteTracker();
    //STAGE IDS
    int location = 0;
    //Menu to hold view toggle functions, but configure as needed.
    Menu theViewMenu;
    Menu theNewNodeMenu;

    //TO DO:
    //Enclose these inside Node class (clause container)
    
    NodeCategory NC_notes = new NodeCategory("notes",0,"khaki");
    NodeCategory NC_footnotes = new NodeCategory ("footnotes",0,"khaki");
    NodeCategory NC_clause = new NodeCategory ("clause",0,"blue");
    NodeCategory NC_def = new NodeCategory ("definition",0,"green");
    NodeCategory NC_testimony = new NodeCategory ("testimony",0,"lightblue");
    NodeCategory NC_witness = new NodeCategory ("witness",0,"lightblue");
    NodeCategory NC_fact = new NodeCategory ("fact",0,"lightblue");
    NodeCategory NC_event = new NodeCategory ("event",0,"lightblue");
    NodeCategory NC_library = new NodeCategory ("library",1,"lemon");
    NodeCategory NC_document = new NodeCategory ("document",1,"darkblue");
    NodeCategory NC_law = new NodeCategory ("law",0,"darkgold");
    NodeCategory NC_collection = new NodeCategory ("collection",2,"orange");
    NodeCategory NC_project = new NodeCategory ("project",3,"white");
    NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
    
    ArrayList<NodeCategory> nodeCatList = new ArrayList<NodeCategory>(Arrays.asList(NC_notes,NC_footnotes,NC_clause,NC_def,NC_law,NC_fact,NC_event,NC_witness,NC_testimony));

    //To hold Stage with open node that is current
    StageManager OpenNodeStage;
    ClauseContainer NodeTarget;

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

private ClauseContainer NodeFromDefinitionsSampleText(String mydata) {
    WordTool myTool = new WordTool();
    ClauseContainer defbox = myTool.doDefTextSearch(mydata);
    return defbox;
} 

//return a ClauseContainer object with clauses after using text document as input

private ClauseContainer NodeFromClausesSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    ClauseContainer clauseCarton = myTool.ClauseImport(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 

/* Convert String into a Node with contained clauses */

private ClauseContainer NodeFromStatuteSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    if (mydata==null) {
        System.out.println("Problem with sampling text for extraction");
    }
    ClauseContainer clauseCarton = myTool.StatuteSectionImport(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 

//---COMMON DOCUMENT / SPRITEBOX REQUESTS

//To do: make this redundant

private Clause getDefaultNodeData() {
    String label = "New document"; //unused
    String text = "Some text";
    String heading = "A heading";
    String category = "clause"; //for now - check it later
    Clause myClause = new Clause(label,heading,text,category); 
    return myClause;
}

private SpriteBox boxNodeForStage(ClauseContainer node, StageManager mySM) {
    
    SpriteBox b = new SpriteBox(node,mySM);
    b.setOnMousePressed(PressBoxEventHandler); //internalise?
    b.setOnMouseDragged(DragBoxEventHandler);
    return b;
}

//LOAD, SAVE AND NEW FOR NODES - EVENT HANDLERS

//for Stage_WS
private void LoadNodeWS(String filename, StageManager mySM) {
                ClauseContainer targetNode = new ClauseContainer(); //not global anymore 
                //String filename = mySM.getFilename();
                System.out.println("Load filename:"+filename);
                //TO DO: check function
                FileInputStream fis = null;
                ObjectInputStream in = null;
                try {
                    fis = new FileInputStream(filename);
                    in = new ObjectInputStream(fis);
                    targetNode=(ClauseContainer)in.readObject();
                    in.close();
                    System.out.println(targetNode.toString());
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                loaddocnum++;
                if (targetNode.getDocName().equals("")) {
                    targetNode.setDocName("LoadedNode"+Integer.toString(loaddocnum));
                }
                
                //--> IF adding to workspace... mySM.newNodeForWorkspace(targetNode);
                Stage_WS.setWSNode(targetNode);
            }

/*
Method Loads Node into Open Node - for all nodes other than Stage_WS
ALthough this currently uses the same filename the context in app can change.
*/
private void LoadNode(String filename) {
                //update the Target to the currentStage
                OpenNodeStage = Stage_WS.getCurrentFocus();
                ClauseContainer targetNode = new ClauseContainer(); //not global anymore 
                //String filename = mySM.getFilename();
                System.out.println("Load filename:"+filename);
                //TO DO: check function
                FileInputStream fis = null;
                ObjectInputStream in = null;
                try {
                    fis = new FileInputStream(filename);
                    in = new ObjectInputStream(fis);
                    targetNode=(ClauseContainer)in.readObject();
                    in.close();
                    System.out.println(targetNode.toString());
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                catch(ClassNotFoundException ex)
                {
                     ex.printStackTrace();
                }
                loaddocnum++;
                if (targetNode.getDocName().equals("")) {
                    targetNode.setDocName("LoadedNode"+Integer.toString(loaddocnum));
                }
                OpenNodeStage.OpenNewNodeNow(targetNode,Stage_WS);
            }

private void SaveNode(StageManager mySM) {
        ClauseContainer node = mySM.getDisplayNode();
        System.out.println("Saving:"+node.toString());
        //String filename = mySM.getFilename();
        String filename = "loadnode.ser"; //static filename for now

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(node); //the top-level object to be saved
            out.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

//method to set parent node based on stage for child node on stage
//TO DO: Rewrite so that nodes handle this internally
private void setParentChild(StageManager targetSM, SpriteBox mySprite) {
    ClauseContainer parentNode = targetSM.getRefParentNode();
    ClauseContainer thisNode = mySprite.getBoxNode();
    thisNode.setParentNode(parentNode);
    parentNode.addChildNode(thisNode);
}

/*
Adds a box with generic data for a category of node.
The node is added as a child node and passed as box to open node viewer.
TO DO:
Define discrete stages and delegagte to appropriate objects:
1. Create Node
2. Make Node a Child Node of the Open Node
3. Let Open Node viewer update both open node and presentation of child nodes as box objects
(last step is separateion of data/view concerns)

//The Workspace is always 'open' but not used unless space clicked on first.

*/

private void NewChildNodeForOpenNode(NodeCategory nodecat) {

    //use the persistent Stage_WS instance to get the current stage (class variable)
    if (Stage_WS==null) {
        System.out.println("Problem with Stage_WS");
    }
    OpenNodeStage = Stage_WS.getCurrentFocus();
    if (OpenNodeStage==null) {
        System.out.println("Problem with setting Open Node");
    }
    System.out.println("Creating new node for this viewer:"+OpenNodeStage.toString());

    //ClauseContainer newNode =  new ClauseContainer(nodecat);
    OpenNodeStage.OpenNewNodeNow(new ClauseContainer(nodecat),Stage_WS);
}

//place Sprite on Target stage if open otherwise workspace

private void placeSpriteOnTargetStage(SpriteBox mySprite, StageManager targetStage) {
        //targetStage.placeSpriteOnStage(...)//show stage always
        if (targetStage.getStage().isShowing()==true) {
            placeSpriteOnStage(mySprite, targetStage);
            System.out.println("New sprite :"+mySprite.toString()+"on target stage:"+targetStage.toString());
        }
        else {
            System.out.println(mySprite.toString()+Stage_WS.toString());
            placeSpriteOnStage(mySprite, Stage_WS);
            System.out.println("New sprite on Stage_WS:"+mySprite.toString());
        }
}

//---EVENT HANDLER FUNCTIONS

private void toggleView(StageManager mySM) {
             
    mySM.toggleStage();
    OpenNodeStage=mySM;
}

/*
Method to end alert status for current sprite and reassign
Currently this looks at all Sprite Boxes globally (regardless of viewer/location)
*/
/*
private void moveAlertFromBoxtoBox(SpriteBox hadFocus, SpriteBox mySprite) {
    hadFocus = getCurrentSprite();
    if (hadFocus!=null) {
        hadFocus.endAlert();
    }
    setCurrentSprite(mySprite);
    mySprite.doAlert();
    }
    */
       
//General function for box clicks
private void processBoxClick(MouseEvent t) {

SpriteBox hadFocus=null;
SpriteBox currentSprite = (SpriteBox)t.getSource();  //selects a class for click source

int clickcount = t.getClickCount();

orgSceneX = t.getSceneX();
orgSceneY = t.getSceneY();

orgTranslateX = currentSprite.getTranslateX();
orgTranslateY = currentSprite.getTranslateY();
//orgTranslateX = ((SpriteBox)(t.getSource())).getTranslateX();
//orgTranslateY = ((SpriteBox)(t.getSource())).getTranslateY();
System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);

switch(clickcount) {
    //single click
    case 1:
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        System.out.println("One click");
        //change stage focus with just one click on spritebox (but node still closed)
        OpenNodeStage=currentSprite.getStageLocation();

        break;
    case 2:
        System.out.println("Two clicks");
        
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        
        //Dbl Click action options depending on box type
       
        OpenNodeStage=currentSprite.getStageLocation();
        //only open if not already open (TO DO: reset when all children closed)
        //prevent closing until all children closed
        //close all children when node closed.
        OpenRedNodeNow(currentSprite);
        
        break;
    case 3:
        System.out.println("Three clicks");
        break;
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

/* Setup text area with blank text to start.  To put default text in at time of constructing,
TO DO: delete this and put scroller into node viewer.  */

public void setupImportStage(StageManager myStageManager, Stage textStage, String myTitle) {

        //This is the stage to be used but is not the JavaFX application default
        textStage.setTitle(myTitle);
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int totalwidth=900; //this is pixels?
        
        //config for window
        double leftColWidth = 650;
        double leftColHeight = 400;
        double rightColWidth = 150;
        /* Setup a horizontal box with two text areas, but put first in scrollpane to allow scrolling */
        
        TextArea textarea2 = new TextArea();
        this.textArea2.setWrapText(true);
        this.textArea2.setPrefWidth(rightColWidth);
        
        //put text in a scrollpane
        this.textArea1.setWrapText(true);
        this.textArea1.setPrefWidth(leftColWidth);
        this.textArea1.setPrefHeight(leftColHeight);

        //config the scrollpane and put textarea1(input) into it
        ScrollPane textpane = new ScrollPane();
        textpane.setContent(textArea1);
        textpane.setPrefHeight(leftColHeight);  
        textpane.setPrefWidth(leftColWidth);

        //put scrollpane and text output area into an hbox
        HBox hbox1 = new HBox(0,textpane,this.textArea2);
        //put the horizontal boxes in a vertical box which will also be in a scrollpane
        VBox vbox2 = new VBox(0,hbox1);
        vbox2.setPrefWidth(totalwidth);
        
        // Lastly, attach vbox to root scrollpane and add to Scene
        double windowWidth=400;
        double windowHeight = 150;
        import_rootnode_scroll = new ScrollPane();
        import_rootnode_scroll.setContent(vbox2); 
        this.ImportScene = new Scene(import_rootnode_scroll, windowWidth, windowHeight, Color.GREY); //width x height in pixels?  
        //add Scene to Stage and position it
        textStage.setScene(ImportScene);
        textStage.sizeToScene(); 
        //myStageManager.setInitStage(Stage_WS);
        myStageManager.setStage(textStage); //maybe rename in Stage Manager
        myStageManager.setSceneRoot(import_rootnode_scroll);
        textStage.show();
    }

/*Method to add category views needed.
As this will toggle views to stages, and each stage has a parent Stage_WS,
Stage_WS should be defined before this call (i.e. not null)
The new StageManager (viewer/app) will create a new display node (data) for this category
at time of creating viewer.
*/

private void addMenuViewsItems(Menu myMenu) {

         Iterator<NodeCategory> myIterator = nodeCatList.iterator(); //alternatively use Java method to see if in Array?
            while (myIterator.hasNext()) {
            NodeCategory myCat = myIterator.next();
            //System.out.println(myCat.getCategory());
            MenuItem myNewViewItem = new MenuItem(myCat.getCategory());
            //
            StageManager myNewStage = new StageManager(Stage_WS, myCat, PressBoxEventHandler,DragBoxEventHandler); //to do: title.  Global?
            myMenu.getItems().add(myNewViewItem);
            //handlers
            myNewViewItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                toggleView(myNewStage); //To DO: Link this to the 'category' stages
            }
        });

        }
}

/* Method to add the 'new' function to the menu.  
This will utilises the stages already set up to put a new item in the Open stage
(although what we really want to do is put new item in the Category Stage: so use this for place)
The "NEW" aspect uses Stage_WS therefore be called by or after the addMenuViewsItems method.
*/
private void addNewObjectItems (Menu myMenu) {
        Iterator<NodeCategory> myIterator = nodeCatList.iterator(); //alternatively use Java method to see if in Array?
            while (myIterator.hasNext()) {
            NodeCategory myCat = myIterator.next();
            System.out.println(myCat.getCategory());
            MenuItem myNewViewItem = new MenuItem(myCat.getCategory());
            myMenu.getItems().add(myNewViewItem);
            //handlers
            myNewViewItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                //If opening in current Stage or Stage_WS (Place)
                OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
            }
        });

        }
}
/*
menuNewNode.getItems().addAll(NewDef,NewClause,NewNote,NewFootnote,NewWitness,NewTestimony,NewEvent,NewFact,NewLaw,NewDoc,NewLibrary,NewCollection,NewProject);
        
*/

private Menu getMenuViews() {
    return this.theViewMenu;
}

private void setMenuViews() {
    this.theViewMenu = new Menu("Views");
}

private Menu getMenuNewNode() {
    return this.theNewNodeMenu;
}

private void setMenuNewNode() {
    this.theNewNodeMenu = new Menu("New");
}


/* Make menuBar for workspace */

private MenuBar makeMenuBar() {
        
        //MENUBAR SETUP
        MenuBar menuBar = new MenuBar();
        //Items for horizontal menu, vertical MenuItems for each
        
        //Menu menuNewNode = new Menu("New");
        Menu menuWorkspace = new Menu("Workspace");
        Menu menuDocument = new Menu("Document");
        Menu menuCollection = new Menu("Collection");
        Menu menuFile = new Menu("File/Node");
        Menu menuProjectLib = new Menu ("ProjectLib");
        Menu menuLibrary = new Menu("Library");
        Menu menuOutput = new Menu("Output");
        Menu menuImport = new Menu("TextTools");
        
        //instance variables (content of these 2 is empty until ready to insert list and event handlers)
        setMenuViews();
        Menu menuViews = getMenuViews();
        setMenuNewNode();
        Menu menuNewNode = getMenuNewNode();
        //
        //TO DO: Place Menu with any Level 1 Category Nodes
        //
        MenuItem SaveNode = new MenuItem("Save");
        MenuItem LoadSavedNode = new MenuItem("Load");
        MenuItem SaveColl = new MenuItem("Save");
        MenuItem LoadColl = new MenuItem("Load");
        MenuItem SaveWork = new MenuItem("Save");
        MenuItem LoadWork = new MenuItem("Load");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem SaveDoc = new MenuItem("SaveDoc");
        MenuItem LoadDoc = new MenuItem("LoadDoc");
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
        MenuItem NodeFromSelection = new MenuItem("Selection->ChildNode");
         menuFile.getItems().addAll(LoadSavedNode,SaveNode);
         menuWorkspace.getItems().addAll(
            SaveWork,
            LoadWork,
            OutputWork,
            PrintBoxes);
        menuOutput.getItems().addAll(
            SaveOutput);
        menuImport.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections,NodeFromSelection);
        
        //Method will save the current open node with focus.

        SaveNode.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            SaveNode(OpenNodeStage); //save everything on the stagefload
            }
        });

        /* Load Collection into an open window TO DO: as icon.
        */
        LoadSavedNode.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = "loadnode.ser";
                LoadNode(filename);
            }
        });
        
        //---WORKSPACE FUNCTIONS ---

        //Method to save workspace (serial)

        SaveWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                SaveNode(Stage_WS);
            }
        });

        /* Method to load up saved workspace */

        LoadWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = Stage_WS.getFilename();
                LoadNodeWS(filename, Stage_WS);
            }
        });

        //EXPORT WORKSPACE TO OUTPUT
        OutputWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            Stage_WS.getDisplayNode().doPrintIteration();
            String output=Stage_WS.getDisplayNode().getClauseAndText();
            Stage_Output.setOutputText(output);
            Stage_Output.showStage();
            }
        });

        
        PrintBoxes.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //call the 'print function' on the BoxContainer object (for now)
                //WorkspaceBoxes.ContentsDump();
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
            }
        });    

        //DOCUMENT EVENT HANDLERS

        SaveOutput.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            System.out.println("Save Output selected!");
            EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
            myfileApp.replaceText(Stage_Output.getOutputText());
            }
        });
        
        /* --- IMPORT MENU ---   TO DO: File Open*/
        WordCount.setOnAction(updateWordCounts); //argument is an EventHandler with ActionEvent object
        GetDefText.setOnAction(extractDefinitions);
        GetDefs.setOnAction(makeDefBoxesFromText);
        GetClauses.setOnAction(makeClauseBoxesFromText);
        GetSections.setOnAction(makeBoxesFromStatuteText);
        NodeFromSelection.setOnAction(makeSelectedChildNode);

        

        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuViews, menuFile, menuNewNode,menuWorkspace, 
            /*menuDocument, menuLibrary,*/ menuOutput, menuImport/*,menuCollection,menuProject*/);     
        
        //create an event filter so we can process mouse clicks on menubar (and ignore them!)
        menuBar.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
            System.out.println("MenuBar click detected! " + mouseEvent.getSource());
            //mouseEvent.consume(); //consume this event - so menu works or not?
            //TO DO: proceed but ignore it for change of focus purposes?
             }
        });

        return menuBar;
}

private VBox makeToolBarButtons() {

        //Button for removing clauses
        Button btnDeleteClause = myControlsManager.newStdButton();
        btnDeleteClause.setTooltip(new Tooltip ("Press to delete selected node"));
        btnDeleteClause.setText("Delete");
        btnDeleteClause.setOnAction(deleteCurrentSprite);

        /*
        //Button for moving clauses to Workspace
        Button btnMoveClauseWS = myControlsManager.newStdButton();
        btnMoveClauseWS.setText("Move to Workspace");
        btnMoveClauseWS.setTooltip(new Tooltip ("Press to move box to Workspace Window"));
        btnMoveClauseWS.setOnAction(MoveBoxtoWorkspace);
        */

        /*
        Button for moving clauses to Library
        //To DO: only visible if Library has been loaded
        Button btnMoveClauseLib = myControlsManager.newStdButton();
        btnMoveClauseLib.setText("Move to Library");
        btnMoveClauseLib.setTooltip(new Tooltip ("Press to move box to Library Window"));
        btnMoveClauseLib.setOnAction(MoveBoxtoLibrary);
        */

        //Button for moving clauses to Document
        Button btnMoveTarget = myControlsManager.newStdButton();
        btnMoveTarget.setText("Move to Target");
        btnMoveTarget.setTooltip(new Tooltip ("Press to move selected to Target node"));
        btnMoveTarget.setOnAction(MoveBoxtoTarget);

        //Button for copying clause to document (leaves copy behind)
        Button btnCopyTarget = myControlsManager.newStdButton();
        btnCopyTarget.setText("Copy to Target");
        btnCopyTarget.setTooltip(new Tooltip ("Press to copy selected to Target node"));
        btnCopyTarget.setOnAction(CopytoTarget);

        //doEdit - NOW REDUNDANT AS OPENING NODE IS EQUIVALENT
        Button btnDoEdit = myControlsManager.newStdButton();
        btnDoEdit.setText("Open");
        btnDoEdit.setTooltip(new Tooltip ("Press to Open Selected Node"));
        btnDoEdit.setOnAction(OpenNodeViewNow);
    
        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClauseWS,btnCopyClause);
        //VBox vbox1 = new VBox(0,btnCopyColl,btnCopyCC,btnMoveClauseWS,btnCopyClauseWS,btnMoveTarget, btnCopyTarget,btnDeleteClause,btnDoEdit);
        VBox vbox1 = new VBox(0,btnMoveTarget,btnCopyTarget,btnDeleteClause,btnDoEdit);
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth);
        return vbox1;

}

/* Setup Stage as a Toolbar Panel for Sprite Move, Copy functions etc */

public void setupToolbarPanel(StageManager mySM) {

        //do this before .show
        Stage myStage = mySM.getStage();
        //Instance variable
        Group myGroup = new Group(); //for root
        toolbarScene = new Scene (myGroup,150,350, Color.GREY); //default width x height (px)
        myStage.setScene(toolbarScene); //set current scene for the Stage
        //optional event handler
        
        toolbarScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Clause Toolbar: Mouse click detected! " + mouseEvent.getSource());
         //mySpriteManager.setStageFocus("Toolbar");
             }
        });

        VBox vbox1 = makeToolBarButtons();
        
        //add Button box to root node
        myGroup.getChildren().add(vbox1); //add the vbox to the root node to hold everything
       
        //setup Stage config
        mySM.setPosition();
        mySM.setSceneRoot(myGroup);
        myStage.show();

        //return toolbar_root;
}

private void setArea1Text(String fname) {
        //get text from file and put in textarea 1
        String myText=this.getTextfromFile(fname);
        this.textArea1.setText(myText);

}

private void setArea2Text(String fname) {
        //get stats from file and put in textarea 2
        String myStats=this.getMostCommon(fname);
        Stage_Import.setOutputText(myStats);
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

/*
Method to end alert status for current sprite and reassign
Currently this looks at all Sprite Boxes globally (regardless of viewer/location)
*/
private void moveAlertFromBoxtoBox(SpriteBox hadFocus, SpriteBox mySprite) {
    hadFocus = getCurrentSprite();
    if (hadFocus!=null) {
        hadFocus.endAlert();
    }
    setCurrentSprite(mySprite);
    Stage_WS.setCurrentFocus(mySprite.getStageLocation());
    mySprite.doAlert();
    }
 

//general method to store currentSprite

private void setCurrentSprite(SpriteBox mySprite) {
    SpriteBox activeSprite = myTracker.getCurrentSprite();
    if (activeSprite!=null) {  //might be no current sprite if not dbl clicked
            activeSprite.endAlert();
        }
        myTracker.setCurrentSprite(mySprite);
        mySprite.doAlert();  
}

private SpriteBox getCurrentSprite() {
    //return this.activeSprite;
    return myTracker.getCurrentSprite();  
}

/*
General method to place sprite on Stage.  Uses Stage Manager class 
Since data nodes are to mirror GUI, update parent child relations here too
27.4.18 - change approach so that it adds this node (rather than box) as sub-node to another node.
The node viewer will then be responsible for display of child nodes (e.g. boxes)
*/

private void placeSpriteOnStage(SpriteBox mySprite, StageManager targetStage) {
    
    setCurrentSprite(mySprite); 
    targetStage.addNewSpriteToStage(mySprite); 
    }

//This is a move not a copy.  

private void placeCurrentSpriteOnStage(StageManager targetStage) {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    deleteSpriteGUI(currentSprite);
    currentSprite.unsetParentNode(); //To DO: let node/viewer handle this.
    targetStage.addNewSpriteToStage(currentSprite);
}

//Does this merely require copying the Data Node and calling node-based SM function?

public void copySpriteToDestination(SpriteBox mySprite, StageManager myStageMan) {
            
            System.out.println("Sprite to copy:"+mySprite.toString());
            SpriteBox b = mySprite.clone(); //clone event handlers or add?
            System.out.println("Sprite clone:"+b.toString());
            myStageMan.showStage();
            placeSpriteOnStage(b, myStageMan);  
}

public void copyCurrentSpriteToDestination(StageManager myStageMan) {
            
            SpriteBox mySprite = getCurrentSprite();
            copySpriteToDestination(mySprite,myStageMan); 
}

/* Method to remove current SpriteBox and contents 
*/

public void deleteSpriteGUI(SpriteBox mySprite) {
    
    if (mySprite!=null) {
        mySprite.getStageLocation().removeSpriteFromStage(mySprite);
    }
    else
    {
        System.out.println("Error : no sprite selected to delete");
    }
}

//STAGE METHODS

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
       
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("Powerdock App");
        primaryStage.show();
        //primaryStage.close(); //why?
        
        ParentStageSM = new StageManager();
        ParentStage = new Stage();
        ParentStageSM.setStage(ParentStage);
        ParentStageSM.setTitle("Powerdock");

        //
        MenuBar myMenu = makeMenuBar();
        Stage_WS = new StageManager("Workspace", NC_WS, myMenu, PressBoxEventHandler, DragBoxEventHandler);  //sets up GUI for view
        addMenuViewsItems(getMenuViews()); //need to do this after Stage_WS defined as it is parent for toggle views.
        addNewObjectItems(getMenuNewNode()); //do this after View menu
       
        //Temporary: demonstration nodes at start
        Stage_WS.setCurrentFocus(Stage_WS);
        NewChildNodeForOpenNode(NC_library);
        NewChildNodeForOpenNode(NC_project);
        
        //setup main toolbar for buttons
        Stage_Toolbar = new StageManager(Stage_WS,"Tools");
        setupToolbarPanel(Stage_Toolbar);

        /* Setup a general text Output Stage (for workspace?) */
        Stage_Output = new StageManager(Stage_WS,"Output");
        Stage_Output.setupTextOutputWindow();
        
        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

    /* Event handler added to box with clause content */

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t);
            t.consume();
        }
    };
    
     /* This is an eventhandler interface to create a new eventhandler class for the SpriteBox objects 
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
            //end alert status for sprite
            SpriteBox hasFocus = getCurrentSprite();
            hasFocus.endAlert();
            //change the active sprite to the current touched sprite.
            setCurrentSprite(currentSprite); //clicked sprite
            System.out.println("The handler for drag box is acting");
            //updates to sprite that triggered event
            currentSprite.setTranslateX(newTranslateX);
            currentSprite.setTranslateY(newTranslateY);
            currentSprite.doAlert(); //in case single click event doesn't detect
            t.consume();//check
        }
    };

    //BUTTON EVENT HANDLERS

    EventHandler<ActionEvent> deleteCurrentSprite = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
        
            deleteSpriteGUI(getCurrentSprite());
            }
        };

    // Method to move selected sprite to Clause WIP (will not duplicate)
    /*
            The following 'add' actually copies to the second stage.
            By moving the object or referring to it on the new Stage, it forces JavaFX to refresh.

            Java FX does its own cleanup.

            To achieve a 'copy' rather than a move, additional code needed.

     */

    /*EventHandler<ActionEvent> MoveBoxtoWorkspace = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            placeCurrentSpriteOnStage(Stage_WS);
            }
        };
        */

    /* 
    Method enables you to copy or move in these easy steps:
    (1) Click on a box to make it active (red).
    (2) Click to target stage (not on a box).
    (3) Select move to target {TO DO: Shortcut key}

    This works because the sprite with the red alert (current sprite) doesn't lost focus
    even when a click to a new stage (but not a box) changes the focus. 
    */

    EventHandler<ActionEvent> MoveBoxtoTarget = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            placeCurrentSpriteOnStage(OpenNodeStage); 
            }
    };

    EventHandler<ActionEvent> CopytoTarget = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            copyCurrentSpriteToDestination(OpenNodeStage);
        }
    };

    EventHandler<ActionEvent> OpenNodeViewNow = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
        
        OpenRedNodeNow(getCurrentSprite());
        }
    };

    public void OpenRedNodeNow (SpriteBox currentSprite) { 
        if (currentSprite.getChildStage()==null) {
            OpenNodeStage = new StageManager(Stage_WS, currentSprite, PressBoxEventHandler, DragBoxEventHandler); 
        }
        //make node viewer visible if still open but not showing
        else {
            OpenNodeStage = currentSprite.getChildStage();
            OpenNodeStage.showStage();
        }
     }

    /* This is a copy not a move 
    MAY NOW BE DEPRECATED BECAUSE TARGET FUNCTION WORKS WELL */

    /*
    EventHandler<ActionEvent> CopyBoxtoWorkspace = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            copyCurrentSpriteToDestination(Stage_WS);
        }
    };
    */
     
    //printClauseList
        EventHandler<ActionEvent> printClauseList = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
             //textmakerTextArea.setText("This is where list of clauses will appear");
             Stage_WS.getDisplayNode().doPrintIteration();
             String output=Stage_WS.getDisplayNode().getClauseAndText();
             Stage_Output.setOutputText(output);

             /* TO DO: Have a separate "Output/Preview" Window to show clause output.  
             //Maybe HTMLview?
             i.e. this will be an 'output console', but within the application.
             */
            }
        };

    //Make boxes for imported definitions

    EventHandler<ActionEvent> makeDefBoxesFromText = 
    new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            String sample = OpenNodeStage.getInputText();
            //TO DO: add node to openstage
            //To do: call this from inside StageManager instance
            ClauseContainer nodeSample = NodeFromDefinitionsSampleText(sample);
            OpenNodeStage.addOpenNodeChildren(nodeSample);
        }
    };

     //Make boxes for imported clauses

    EventHandler<ActionEvent> makeClauseBoxesFromText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        
             //use the persistent Stage_WS instance to get the current stage (class variable)
             OpenNodeStage = Stage_WS.getCurrentFocus();
             String sample = OpenNodeStage.getInputText();
             ClauseContainer nodeSample = NodeFromClausesSampleText(sample);
             OpenNodeStage.addOpenNodeChildren(nodeSample);
             /*
             ClauseContainer focusNode=OpenNodeStage.getDisplayNode();
             focusNode.addNodeChildren(nodeSample); //data
             System.out.println("Updated child nodes for this node:"+focusNode.toString());
             OpenNodeStage.updateOpenNodeView(); //view
             */
        }
    };
    

     //Make boxes for imported statute clauses

    EventHandler<ActionEvent> makeBoxesFromStatuteText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        //TO DO: get source of data
        OpenNodeStage = Stage_WS.getCurrentFocus();
        String sample = OpenNodeStage.getInputText();
        if (sample.equals("")) {
            System.out.println("No text to sample");
        }
        ClauseContainer nodeSample = NodeFromStatuteSampleText(sample);
        OpenNodeStage.addOpenNodeChildren(nodeSample);
        /*
        ClauseContainer focusNode=OpenNodeStage.getDisplayNode();
        focusNode.addNodeChildren(nodeSample); //data
        System.out.println("Updated child nodes for this node:"+focusNode.toString());
        OpenNodeStage.updateOpenNodeView(); //view
        */
        }
    };
    

        //---- MORE EVENT HANDLERS ----

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

            //Update the import stage common words count text area
            String gotcha = Main.this.textArea1.getText();
            String newTA = Main.this.getCommonWordsNow(gotcha);
            Stage_Import.setOutputText(newTA);
            Main.this.textArea2.setText(newTA);
            
           
            //new stage with scroll window to hold boxes created for common wods
            StageManager myStageManager = new StageManager(Stage_WS, "Workspace");
            Stage myStage = new Stage();
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
            myStageManager.setPosition();
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
    
        /* Process the text in the input area of the current Node viewer 
        (whether saved or not)
        */

        EventHandler<ActionEvent> extractDefinitions = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Get Defs Text Button was pressed!");
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            String gotcha = OpenNodeStage.getInputText();
            //System.out.println("Current input text:"+gotcha);
            String newDefs = Main.this.getMatched(gotcha);
            OpenNodeStage.setOutputText(newDefs); //output to current node viewer
            }
        };
        
        /*
        Method to take selected text and create a child node in open Node viewer with it
        Encapsulation:
        Since text selection works on the Open Stage, it is possible to call a public function on that object
        and make all functions private 
        */

        EventHandler<ActionEvent> makeSelectedChildNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            System.out.println("Make Node (From Selected Text) Button was pressed!");
            if (Stage_WS.getCurrentFocus()==OpenNodeStage) {
             System.out.println("Change of Viewer Focus OK in Main!");
             System.out.println("Viewer :"+OpenNodeStage.toString());
             }
             else {
                System.out.println("Problem with change Viewer Focus");
                System.out.println("Current OpenNode :"+ OpenNodeStage);
                System.out.println("Stage WS Focus :"+ Stage_WS.getCurrentFocus());
             }
            OpenNodeStage.selectedAsChildNode();
            }
        };
}