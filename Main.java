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
//For serialization IO 
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
    //Text Output windows (no edits)
    StageManager Stage_Output;
    
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
    Menu theWorldsMenu;
    Menu theNotesMenu;
    Menu theProtocolMenu;
    Menu theEventsMenu;


    ArrayList<NodeCategory> nodeCatList;

    //To hold Stage with open node that is current
    StageManager OpenNodeStage;  
    ClauseContainer NodeTarget;
    //to hold Master Node for project i.e. data
    ClauseContainer masterNode = new ClauseContainer();

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
                /* deal with in constructor 
                if (targetNode.getDocName().equals("")) {
                    targetNode.setDocName("LoadedNode"+Integer.toString(loaddocnum));
                }
                */
                
                //--> IF adding to workspace... mySM.newNodeForWorkspace(targetNode);
                masterNode.addChildNode(targetNode);
                Stage_WS.setWSNode(masterNode);
            }

/*
Method Loads Node into Open Node on Open Stage - for all nodes other than Stage_WS
Although this currently uses the same filename the context in app can change.
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
                /* deal with in constructor
                if (targetNode.getDocName().equals("")) {
                    targetNode.setDocName("LoadedNode"+Integer.toString(loaddocnum));
                }
                */
                OpenNodeStage.OpenNewNodeNow(targetNode,Stage_WS);
            }

private void SaveNode(StageManager mySM) {
        ClauseContainer node = mySM.getDisplayNode();
        System.out.println("Saving:"+node.toString());
        //String filename = mySM.getFilename();
        String filename = Stage_WS.getFilename();

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

/*
Adds a box with generic data for a category of node.
The node is added as a child node and passed as box to open node viewer.
TO DO:
Define discrete stages and delegagte to appropriate objects:
1. Create Node
2. Make Node a Child Node of the Open Node
3. Let Open Node viewer update both open node and presentation of child nodes as box objects
(last step is separation of data/view concerns)

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
    System.out.println("About to open new node with Stage_WS: "+Stage_WS.toString());
    OpenNodeStage.OpenNewNodeNow(new ClauseContainer(nodecat,masterNode),Stage_WS);
}

//place Sprite on Target stage if open otherwise workspace
//NOW REDUNDANT AS STAGE MANAGER WILL ADD BOX USING NODE (SEP OF CONCERNS)

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
        //refreshNodeViewScene();
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

/*Method to add category views needed.
As this will toggle views to stages, and each stage has a parent Stage_WS,
Stage_WS should be defined before this call (i.e. not null)
The new StageManager (viewer/app) will create a new display node (data) for this category
at time of creating viewer.

//.getItems() method of Menu returns an ObservableList, which has useful add, remove, clear, isEmpty methods
*/

private void addMenuViewsItems(ArrayList<NodeCategory> myCatList) {

        if (myCatList==null) {
            System.out.println("Error: 'View' menu not populated");
            return;
        }
        else {
            System.out.println("Categories to add to view:"+myCatList.toString());
        }

        Menu myMenu = getMenuViews();
        System.out.println("View items menu");
        if (myMenu.getItems().isEmpty()) {
            System.out.println("Menu is currently empty");
        }
        else {
            System.out.println("Menu is not empty but cleaning...");
            myMenu.getItems().clear();
        }
        if (myMenu.getItems().isEmpty()) {
             System.out.println("Menu cleaning successful");
        }
        else {
            System.out.println("Views Menu cleaning unsuccessful");
        }
        
         Iterator<NodeCategory> myIterator = myCatList.iterator(); //alternatively use Java method to see if in Array?
            while (myIterator.hasNext()) {
            NodeCategory myCat = myIterator.next();
            //System.out.println(myCat.getCategory());
            MenuItem myNewViewItem = new MenuItem(myCat.getCategory());
            ClauseContainer myCatNode = new ClauseContainer(myCat, masterNode, "The holding area for all nodes of this category",myCat.getCategory());
            StageManager myNewStage = new StageManager(Stage_WS, myCatNode, PressBoxEventHandler,DragBoxEventHandler); //to do: title.  Global?
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
/*
private void addMenuCreateNew (ArrayList<NodeCategory> myCatList) {
        if (myCatList==null) {
            System.out.println("Error: 'New' menu not populated");
            return;
        }
        else {
            System.out.println("Categories to add to new:"+myCatList.toString());
        }

        Menu myMenu = getmenuNewElement();
        System.out.println("New items menu");
        if (myMenu.getItems().isEmpty()) {
            System.out.println("Menu is currently empty");
        }
        else {
            System.out.println("Menu is not empty but cleaning...");
            myMenu.getItems().clear();
        }
        if (myMenu.getItems().isEmpty()) {
             System.out.println("Menu cleaning successful");
        }

        Iterator<NodeCategory> myIterator = myCatList.iterator(); //alternatively use Java method to see if in Array?
            while (myIterator.hasNext()) {
            NodeCategory myCat = myIterator.next();
            System.out.println(myCat.getCategory());
            MenuItem myNewViewItem = new MenuItem(myCat.getCategory());
            myMenu.getItems().add(myNewViewItem);
            //handlers
            myNewViewItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                //New node..
                ClauseContainer newNode = new ClauseContainer(myCat);
                //Add new object to the category node
                myCat.getCategoryNode().addChildNode(newNode);
                //place a COPY (REF) of node in the relevant open node.  Testing...
                OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus id.
                OpenNodeStage.OpenNewNodeNow(newNode,Stage_WS); // check they both update
                // place a NEW object in the relevant open node... 
                //OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
                    System.out.println("Nodes ");
                    System.out.println("Category Node: "+ myCat.getCategoryNode().getChildNodes().toString());
                    System.out.println("Context Node: "+OpenNodeStage.getDisplayNode().getChildNodes().toString());
            }
        });
        }
}
*/

/* Method to add the 'new' function to the menu.  
This will utilises the stages already set up to put a new item in the Open stage
(although what we really want to do is put new item in the Category Stage: so use this for place)
The "NEW" aspect uses Stage_WS therefore should be called by or after the addMenuViewsItems method.
*/

//populate a menu to create a new node, from a node category list
private void addMenuForNew (Menu myMenu, ArrayList<NodeCategory> myCatList) {
        if (myCatList==null) {
            System.out.println("Error: 'New' menu not populated");
            return;
        }
        else {
            System.out.println("Categories to add to new:"+myCatList.toString());
        }
        if (myMenu==null) {
            System.out.println("Error: Menu not defined");
            return;
        }
        System.out.println("New items menu");
        if (myMenu.getItems().isEmpty()) {
            System.out.println("Menu is currently empty");
        }
        else {
            System.out.println("Menu is not empty but cleaning...");
            myMenu.getItems().clear();
        }
        if (myMenu.getItems().isEmpty()) {
             System.out.println("Menu cleaning successful");
        }

        Iterator<NodeCategory> myIterator = myCatList.iterator(); //alternatively use Java method to see if in Array?
            while (myIterator.hasNext()) {
            NodeCategory myCat = myIterator.next();
            System.out.println(myCat.getCategory());
            MenuItem myNewViewItem = new MenuItem(myCat.getCategory());
            myMenu.getItems().add(myNewViewItem);
            //handlers
            myNewViewItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                //Add new object to the category node
                if (myCat.getCategoryNode()==null) {
                    System.out.println("This category item has no category node");
                    myCat.setCategoryNode(new ClauseContainer(myCat,masterNode));
                    //To set up a stage to display myCat invisibly when menu is created...
                    //StageManager myNewStage = new StageManager(Stage_WS, myCat, PressBoxEventHandler,DragBoxEventHandler); //to do: title.  Global?
                }
                ClauseContainer newNode = new ClauseContainer(myCat,myCat.getCategoryNode());
                myCat.getCategoryNode().addChildNode(newNode);
                System.out.println("Category Node: "+ myCat.getCategoryNode().getChildNodes().toString());
                //place a COPY (REF) of node in the relevant open node.  Testing...
                OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus id.
                OpenNodeStage.OpenNewNodeNow(newNode,Stage_WS); // check they both update
                /* place a NEW object in the relevant open node... */
                //OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
                    System.out.println("Nodes ");
                    System.out.println("Context Node: "+OpenNodeStage.getDisplayNode().getChildNodes().toString());
            }
        });
        }
}

//standard method to add view/new node items under these menus based on node categories in this worldview

private void populateMenus(ArrayList<NodeCategory> nodelist) {
    addMenuViewsItems(nodelist); //need to do this after Stage_WS defined as it is parent for toggle views.
    //addMenuCreateNew(nodelist);
    addMenuForNew(getmenuNewElement(),nodelist);
}

private void configWorldMenuItem(MenuItem myMenuItem, ArrayList<NodeCategory> nodelist) {
     myMenuItem.setOnAction(new EventHandler<ActionEvent>() {
     public void handle(ActionEvent t) {
                populateMenus(nodelist);
                Stage_WS.setCurrentFocus(Stage_WS);
                OpenNodeStage = Stage_WS.getCurrentFocus();
            }
        });
}

/*
Menu and Menu items have methods available to Observable List:
https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html
*/

private void addMenuWorldsItem(MenuItem myMenuItem, ArrayList<NodeCategory> nodelist) {
    //menuWorlds.getItems().addAll(menuitem1,menuitem2);
    getMenuWorlds().getItems().add(myMenuItem); //add submenu
    configWorldMenuItem(myMenuItem,nodelist);
}

private Menu getMenuViews() {
    return this.theViewMenu;
}


private void setMenuViews() {
    this.theViewMenu = new Menu("Views");
}

private void setmenuNewElement() {
    this.theNewNodeMenu = new Menu("New"); //a new node (not a copy?)
}

private void setMenuWorlds() {
    this.theWorldsMenu = new Menu("Worlds");
}

private Menu getMenuWorlds() {
    return this.theWorldsMenu;
}

private void setMenuNotes() {
    this.theNotesMenu = new Menu("Notes");
}

private Menu getMenuNotes() {
    return this.theNotesMenu;
}

private void setMenuEvents() {
    this.theEventsMenu = new Menu("Events");
}

private Menu getMenuEvents() {
    return this.theEventsMenu;
}

//Menu MenuLaw = new Menu("Protocol(Law)");

private void setMenuLaw() {
    this.theProtocolMenu = new Menu("Law");
}

private Menu getMenuLaw() {
    return this.theProtocolMenu;
}

private Menu getmenuNewElement() {
    return this.theNewNodeMenu;
}


/* Make menuBar for workspace */

private MenuBar makeMenuBar() {
        
        //MENUBAR SETUP
        MenuBar menuBar = new MenuBar();
        // --- FILE MENU ---
        Menu menuFile = new Menu("File");
        MenuItem OpenTempl = new MenuItem("Open Template");
        MenuItem SaveName = new MenuItem("Save (selected)");
        MenuItem SaveTempl = new MenuItem("Save As (selected)");
        MenuItem SaveAllTempl = new MenuItem("Save All");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem PrintTree = new MenuItem("Print as HTML");
        PrintTree.setOnAction(writeHTML);
        
        menuFile.getItems().addAll(OpenTempl,SaveName,SaveTempl,SaveAllTempl,
            OutputWork,
            PrintTree);
        //Items for horizontal menu, vertical MenuItems for each
        /*
        //Menu menuNewElement = new Menu("New");
        //Menu menuWorkspace = new Menu("Workspace");
        //
        //setMenuWorlds();
        //Menu menuWorlds = getMenuWorlds();

        setMenuNotes();
        Menu menuNotes = getMenuNotes();
        //
        //Menu menuDocument = new Menu("Document");
        //Menu menuCollection = new Menu("Collection");
        
        //Menu MenuLaw = new Menu("Protocol(Law)");
        //
        setMenuEvents();
        Menu menuEvents = getMenuEvents();
        //
        setMenuLaw();
        Menu MenuLaw = getMenuLaw();
        //Menu menuProjectLib = new Menu ("ProjectLib");
        //Menu menuLibrary = new Menu("Library");
        
        
        
        //instance variables (content of these 2 is empty until ready to insert list and event handlers)
        setMenuViews();
        Menu menuViews = getMenuViews();
        setmenuNewElement();
        Menu menuNewElement = getmenuNewElement();
        
       
        //Menu menuWorlds = getMenuWorlds();
        //
        //TO DO: Place Menu with any Level 1 Category Nodes
        //

        /*
        MenuItem SaveNode = new MenuItem("SaveBox");
        MenuItem LoadSavedNode = new MenuItem("LoadBox");
        MenuItem SaveColl = new MenuItem("Save");
        MenuItem LoadColl = new MenuItem("Load");
        MenuItem SaveWork = new MenuItem("Save");
        MenuItem LoadWork = new MenuItem("Load");
       
        MenuItem SaveDoc = new MenuItem("SaveDoc");
        MenuItem LoadDoc = new MenuItem("LoadDoc");
        */
        
        // --- TEXT MENU ---
        Menu menuText = new Menu("Text");
        MenuItem FileOpen = new MenuItem("FileOpen");
        MenuItem WordCount = new MenuItem("WordCount");
        MenuItem InputFile = new MenuItem("InputFile");
        MenuItem GetDefText = new MenuItem("GetDefText");
        MenuItem GetDefs = new MenuItem("GetDefs");
        MenuItem GetClauses = new MenuItem("GetClauses");
        MenuItem GetSections = new MenuItem("GetSections");
        MenuItem NodeFromSelection = new MenuItem("Selection->ChildNode");

        MenuItem DictTempl = new MenuItem("DictionaryTemplate");
        MenuItem DictTemplCounts =  new MenuItem("DictionaryTemplateCounts");
        MenuItem AustliiCounts =  new MenuItem("AustliiCounts");
        MenuItem AustliiFirmCounts = new MenuItem("AustliiFirmCounts");
        
        menuText.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections,DictTempl,DictTemplCounts,AustliiCounts,AustliiFirmCounts,NodeFromSelection);
        
        //--- MENU NEW
        Menu menuNew = new Menu("New");
        MenuItem newNode = new MenuItem("Box");
        newNode.setOnAction(newNodeMaker);
        menuNew.getItems().addAll(newNode);
        //--- OUTPUT MENU ---
        Menu menuOutput = new Menu("Output");
        MenuItem saveOutput = new MenuItem("Save");
        menuOutput.getItems().addAll(saveOutput);
        saveOutput.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            System.out.println("Save Output selected!");
            EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
            myfileApp.replaceText(Stage_Output.getOutputText());
            }
        });


        
        //DATA
        //MenuItem setFollower = new MenuItem("setFollower");
        //MenuItem unsetFollower = new MenuItem("unsetFollower");
        //MenuLaw.getItems().addAll(setFollower,unsetFollower);
        //Method will save the current open node with focus.

        /*
        SaveNode.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            SaveNode(OpenNodeStage); //save everything on the stagefload
            }
        });

        /* Load Collection into an open window TO DO: as icon.
        */
        /*
        LoadSavedNode.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = "loadnode.ser";
                LoadNode(filename);
            }
        });
        */
        
        //---WORKSPACE FUNCTIONS ---
        /*
        //Method to save workspace (serial)

        SaveWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                SaveNode(Stage_WS);
            }
        });

        /* Method to load up saved workspace */

        /*
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
        */
        
        /* --- TEXT MENU ---  */
        WordCount.setOnAction(updateWordCounts); //argument is an EventHandler with ActionEvent object
        GetDefText.setOnAction(extractDefinitions);
        GetDefs.setOnAction(makeDefBoxesFromText);
        GetClauses.setOnAction(makeClauseBoxesFromText);
        GetSections.setOnAction(makeBoxesFromStatuteText);
        NodeFromSelection.setOnAction(makeSelectedChildNode);
        SaveName.setOnAction(saveDocName);
        SaveTempl.setOnAction(saveTemplate);
        OpenTempl.setOnAction(openTemplate);
        SaveAllTempl.setOnAction(saveAll);
        DictTempl.setOnAction(makeDictNode);
        DictTemplCounts.setOnAction(makeDictCountsNode);
        AustliiCounts.setOnAction(countAustliiDictionary);
        AustliiFirmCounts.setOnAction(countAustliiFirms);


        //DATA MENU
        //setFollower.setOnAction(handleSetFollower);
        //unsetFollower.setOnAction(handleUnsetFollower);

        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuFile, menuNew, menuText, menuOutput);     
        
        //create an event filter so we can process mouse clicks on menubar (and ignore them!)
        menuBar.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
            System.out.println("MenuBar click detected! " + mouseEvent.getSource());
            //OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus after click
            System.out.println("MB Open Node, Viewer :"+OpenNodeStage.toString());
            System.out.println("MB Viewer :"+Stage_WS.getCurrentFocus());
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

/* Method to see if any label or text contains legal 'role' words, for display purposes 

Many of these are pair words: relationship dichotomies; 
a RELATIVE inequality or division of social, economic or legal power that defines a transaction or structure, and the role of the participants.

TO DO: put into groups for managing different areas of law, but iterate through all.
*/
/*

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
*/

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
7.6.18 - used by 'copy sprite to destination'.  TO DO:  copy node, send to stage managers to handle.'
e.g. targetStage.OpenNewNodeNow? or targetStage.PlaceNodeNow...needs work
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

//Set current selected Sprite's node as data link parent. 
//REDUNDANT.  WAS USED BY EVENT HANDLER 

private void setCurrentSpriteDataParent() {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    OpenNodeStage=Stage_WS.getCurrentFocus();
    System.out.println("Stage that is to have follower set:"+OpenNodeStage.toString());
    System.out.println("Source sprite for parent data:"+currentSprite.toString());
    OpenNodeStage.setFollow(currentSprite);
    if (OpenNodeStage.getDisplayNode().isFollower()==false) {
        System.out.println ("Error in setting follower status for:"+OpenNodeStage.toString());
    }
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
        primaryStage.hide();
        //primaryStage.close(); //why?
        
        ParentStageSM = new StageManager();
        ParentStage = new Stage();
        ParentStageSM.setStage(ParentStage);
        ParentStageSM.setTitle("Powerdock");

        //master Node for save all workspace
         masterNode.setDocName("Workspace");
         masterNode.setHeading("Workspace(saved)");
         masterNode.setShortname("default");

        //general application nodes
        NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
        //nodeCatList = makeLawWorldCategories(); <---optional, to restore NodeCats
        //
        MenuBar myMenu = makeMenuBar();
        Stage_WS = new StageManager("Workspace", NC_WS, masterNode, myMenu, PressBoxEventHandler, DragBoxEventHandler);  //sets up GUI for view
        Stage_WS.setCurrentFocus(Stage_WS);
        OpenNodeStage = Stage_WS.getCurrentFocus();
        //nodes and menus
        /*
        NodeConfig myNodeConfig = new NodeConfig();
        MenuItem defaultWM = new MenuItem("Default");
        populateMenus(myNodeConfig.getDefaultNodes());
        addMenuWorldsItem(defaultWM,myNodeConfig.getDefaultNodes());
        MenuItem menuitem1 = new MenuItem("Litigation");
        addMenuWorldsItem(menuitem1,myNodeConfig.getLitNodes());
        MenuItem menuitem2 = new MenuItem("Commercial");
        addMenuWorldsItem(menuitem2,myNodeConfig.getCommercialNodes());
        MenuItem menuitem3 = new MenuItem("MerchantWorld");
        addMenuWorldsItem(menuitem3,myNodeConfig.getMerchantNodes());
        MenuItem menuitem4 = new MenuItem("DictionaryTemplate");
        addMenuWorldsItem(menuitem4,myNodeConfig.getDictionaryNodes());
        //Menu menuNotes Items
        //setMenuNotes();
        addMenuForNew(getMenuEvents(),myNodeConfig.getEventsNodeConfigNodeConfigNodeConfig());
        addMenuForNew(getMenuNotes(),myNodeConfig.getNotesNodes());
        addMenuForNew(getMenuLaw(),myNodeConfig.getLawNodes());
        */
        //setup main toolbar for buttons
        Stage_Toolbar = new StageManager(Stage_WS,"Tools");
        setupToolbarPanel(Stage_Toolbar);

        /* Setup a general text Output Stage (for workspace?) */
        Stage_Output = new StageManager(Stage_WS,"Output");
        Stage_Output.setupTextOutputWindow();

        //Temporary: demonstration nodes at start
        Stage_WS.setCurrentFocus(Stage_WS);
        OpenNodeStage = Stage_WS.getCurrentFocus();

        
        
        //otherwise load them in with project to obtain current docnum etc.

        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

    /* private method to initialise Node categories if needed 
    nb  the view/new object menus need to be conditional on there being node categories loaded in.
    4.5.18
    If the 'Workspace loads in the categories'  e.g. as part of a worldview, then
    specific worlds and projects can own the categories, instead of the application.
    Alternatively, have main project instatiate Worlds that can be chosen as a node from main menu
    Then when this is 'selected', it comes with its own categories of 'objects' to populate that world.
    (this will then dynamically change the Views/New menus)
    This will require a 'worldlist' to populate the menus.

    */

    

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
     These currently have no limits on how far you can drag 
     Handle release events in Stage Managers ?*/

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

    //Open a new stage in all cases (a kind of refresh)

    public void OpenRedNodeNow (SpriteBox currentSprite) { 
        
        OpenNodeStage = new StageManager(Stage_WS, currentSprite, PressBoxEventHandler, DragBoxEventHandler); 

        /*if (currentSprite.getChildStage()==null) {
            OpenNodeStage = new StageManager(Stage_WS, currentSprite, PressBoxEventHandler, DragBoxEventHandler); 
        }
        //make node viewer visible if still open but not showing
        else {
            OpenNodeStage = currentSprite.getChildStage();
            OpenNodeStage.showStage();
        }
        */
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

    // --- EVENT HANDLERS

    // new spritebox on stage

    EventHandler<ActionEvent> newNodeMaker = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //create a new node
                NodeCategory NC_default = new NodeCategory("default",33,"darkblue");
                //find current node
                OpenNodeStage=Stage_WS.getCurrentFocus();
                ClauseContainer parentNode = OpenNodeStage.getDisplayNode();
                //new node with category and currentNode as parent
                ClauseContainer newDataNode = new ClauseContainer(NC_default,parentNode);
                
                //place a COPY (REF) of node in the relevant open node.  Testing...
                OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus id.
                OpenNodeStage.OpenNewNodeNow(newDataNode,Stage_WS); // check they both update
                /* place a NEW object in the relevant open node... */
                //OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
                    System.out.println("Nodes ");
                    System.out.println("Context Node: "+OpenNodeStage.getDisplayNode().getChildNodes().toString());
            }
    };
        
     
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
            //handle data node updates through the Stage object
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
            /* TO DO: rewrite so that it is output of current OpenNode
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
            */
        }
    };
    
    /*
        //menu button handler to call method to set follower 
        EventHandler<ActionEvent> handleSetFollower = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("About to set follower...");
            setCurrentSpriteDataParent();
            }
        };

        EventHandler<ActionEvent> handleUnsetFollower = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            OpenNodeStage=Stage_WS.getCurrentFocus();
            OpenNodeStage.unsetFollow(); //call node or GUI?
            }
        };
    */
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
             System.out.println("SCN Viewer :"+OpenNodeStage.toString());
             }
             else {
                System.out.println("Problem with change Viewer Focus");
                System.out.println("Current OpenNode :"+ OpenNodeStage);
                System.out.println("Stage WS Focus :"+ Stage_WS.getCurrentFocus());
             }
            OpenNodeStage.selectedAsChildNode();
            }
        };

       
        //to call function to make dictionary template as needed
        EventHandler<ActionEvent> makeDictNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer dictNode = myFS.getDictionaryTemplate();
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to call function to make dictionary template with counts as needed
        EventHandler<ActionEvent> makeDictCountsNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            String myfile = "1.html";
            ClauseContainer dictNode = myFS.getDictionaryWithCounts(myfile,"dictionary.txt");
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to call function to make dictionary template with counts as needed
        EventHandler<ActionEvent> getFirmCounts = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            String myfile = "1.html";
            ClauseContainer dictNode = myFS.getDictionaryWithCounts(myfile,"firms.txt");
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to load a new template to workspace
        EventHandler<ActionEvent> openTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            myLS.makeLoad(Stage_WS);
            }
        };

        //save all (i.e. workspace etc)
        EventHandler<ActionEvent> saveAll = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            ClauseContainer thisNode;
                    if (Main.this.masterNode!=null) {
                        myLS.makeSave(Stage_WS,Main.this.masterNode);
                    }
                    else {
                       myLS.Close();
                    }
                }
            };

        //save  template
        EventHandler<ActionEvent> saveDocName = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        myLS.saveName(thisNode);
                    }
                    else {
                       myLS.Close();
                    }
                }
            }; 

         //save As template
        EventHandler<ActionEvent> saveTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        myLS.makeSave(Stage_WS,thisNode);
                    }
                    else {
                       myLS.Close();
                    }
                }
            }; 

        //write out html content from this node tree
        //save template
        EventHandler<ActionEvent> writeHTML = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            makeHTML mh = new makeHTML();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        mh.HTMLoutput(thisNode,thisNode.getDocName());
                    }
                    else {
                       //mh.Close();
                    }
                }
            };

        //to call function to make an austlii folder (.html) node with word counts inside
        EventHandler<ActionEvent> countAustliiDictionary = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer austliiNode = myFS.getAustliiWithCounts("dictionary.txt");
            OpenNodeStage.OpenNewNodeNow(austliiNode,Stage_WS);
            }
        };

        EventHandler<ActionEvent> countAustliiFirms = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer austliiNode = myFS.getAustliiWithCounts("firms.txt");
            OpenNodeStage.OpenNewNodeNow(austliiNode,Stage_WS);
            }
        };
}