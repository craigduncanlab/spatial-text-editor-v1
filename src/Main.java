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
    Group WorkspaceGroup;
    ClauseContainer WorkspaceNode = null;
    BoxContainer WorkspaceBoxes; //A serializable top-level container (optional)
    ClauseContainer wsCollection = new ClauseContainer(); //for holding workspace contents (inside boxes)
    //Opus = project collection.  Display Projects as Icons, as in a library.
    StageManager Stage_PROJLIB;
    Stage ProjectLibStage;
    Scene ProjectLibScene;
    Group ProjectLibGroup;
    ClauseContainer projectLibNode = new ClauseContainer();
    //ProjectOpen Stage (to display contents of each Project i.e. an open Project with Collection(s), MergeData etc)
    StageManager Stage_PROJ;
    Stage ProjectStage;
    Scene ProjectScene;
    Group ProjectGroup;
    ClauseContainer projectNode = new ClauseContainer(); //currently opened project
    //To do: MergeDataWindow
    //Collection Stage (to hold groups of libraries and documents).. i.e an open Collection.
    StageManager Stage_COLL;
    /*
    Stage CollectionStage;
    Scene CollectionScene;
    Group CollectionGroup;
    String CollectionName = "collection.ser";
    */
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
    ClauseContainer libraryNode = null; //library import/save
    //ImportStage
    StageManager Stage_Import;
    Stage ImportStage;
    Scene ImportScene; // scene for adding on textStage.
    ScrollPane import_rootnode_scroll; //root Node for import stage

    //Text Output windows (no edits)
    StageManager Stage_Output = new StageManager();
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
    StageManager Stage_EDITNODEPROP = new StageManager();
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
    ClauseContainer clauseCarton = myTool.StatuteSectionImport(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 

//---COMMON DOCUMENT / SPRITEBOX REQUESTS

private Clause getDefaultNodeData() {
    String label = "New document"; //unused
    String text = "Some text";
    String heading = "A heading";
    String category = "clause"; //for now - check it later
    Clause myClause = new Clause(label,heading,text,category); 
    return myClause;
}

private ClauseContainer getNewNodeWithData(int nodelevel) {
    ClauseContainer clauseNode = new ClauseContainer();
    clauseNode.setDocName("Node with Data"+Integer.toString(libdocnum));
    clauseNode.setNodeCategory("clause");
    clauseNode.setNodeLevel(nodelevel);
    clauseNode.setType("document");
    clauseNode.setAuthorName("Craig");
    clauseNode.addNodeClause(getDefaultNodeData());
    return clauseNode;
}

private ClauseContainer getNewNodeNoData() {
    ClauseContainer clauseNode = new ClauseContainer();
    clauseNode.setDocName("New Node"+Integer.toString(libdocnum));
    clauseNode.setNodeCategory("collection");
    clauseNode.setType("collection");
    clauseNode.setAuthorName("Craig");
    //clauseNode.addNodeClause(makeProFormaClause());
    return clauseNode;
}

private SpriteBox makeBoxWithNode(ClauseContainer node) {
    
    SpriteBox b = new SpriteBox();
    b.setOnMousePressed(PressBoxEventHandler); 
    b.setOnMouseDragged(DragBoxEventHandler);
    b.setBoxNode(node);
    return b;
}

private SpriteBox boxNodeForStage(ClauseContainer node, StageManager mySM) {
    
    SpriteBox b = new SpriteBox(node,mySM);
    b.setOnMousePressed(PressBoxEventHandler); //internalise?
    b.setOnMouseDragged(DragBoxEventHandler);
    return b;
}

private ClauseContainer makeProjectTree() {

    ClauseContainer firstClause = getNewNodeWithData(1);
    ClauseContainer tempDoc = new ClauseContainer();
    tempDoc.addChildNode(firstClause); //some metadata?
    ClauseContainer tempCollection = new ClauseContainer();
    tempCollection.addChildNode(tempDoc);
    ClauseContainer tempProject = new ClauseContainer();
    tempProject.addChildNode(tempCollection);
    return tempProject;
}

private SpriteBox makeProjectBox() {

    ClauseContainer proj = makeProjectTree();
    SpriteBox box = makeBoxWithNode(proj);
    return box;
}

private void addNewBoxWithClause() {

    SpriteBox b = new SpriteBox(getNewNodeWithData(3),Stage_DOC);
    placeSpriteOnTargetStage(b, Stage_DOC);
    }

//LOAD, SAVE AND NEW FOR NODES - EVENT HANDLERS

private void LoadNode(String filename, StageManager mySM) {
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
                //create spritebox
                //SpriteBox b = new SpriteBox(targetNode, mySM);
                SpriteBox b = boxNodeForStage(targetNode,mySM);
                placeSpriteOnTargetStage(b, mySM);
            }

private void SaveNode (StageManager mySM) {
        ClauseContainer node = mySM.getDisplayNode();
        System.out.println("Saving:"+node.toString());
        String filename = mySM.getFilename();

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
private void setParentChild(StageManager targetSM, SpriteBox mySprite) {
    ClauseContainer parentNode = targetSM.getRefParentNode();
    ClauseContainer thisNode = mySprite.getBoxNode();
    thisNode.setParentNode(parentNode);
    parentNode.addChildNode(thisNode);
}

//method to set parent node based on stage for child node on stage
private void unsetParentChild(SpriteBox mySprite) {
    ClauseContainer thisNode = mySprite.getBoxNode();
    ClauseContainer parentNode=thisNode.getParentNode();
    thisNode.unsetParentNode();
    parentNode.removeChildNode(thisNode);
}


//Adds a container that is not holding a specific clause (could be anything)
private void NewNodeForStage(StageManager targetStage, int nodelevel) {

    SpriteBox b = new SpriteBox(getNewNodeWithData(nodelevel),targetStage);
    b.setOnMousePressed(PressBoxEventHandler); 
    b.setOnMouseDragged(DragBoxEventHandler);
    if (targetStage.getStage().isShowing()==true) {
        placeSpriteOnTargetStage(b, targetStage);
        System.out.println("New node for stage - target is:"+targetStage.toString());
    }
    else {
        System.out.println(b.toString()+Stage_WS.toString());
            placeSpriteOnStage(b, Stage_WS);
            System.out.println("New sprite on Stage_WS called from main new node:"+b.toString());
    }
}

//place Sprite on Target stage if open otherwise workspace

private void placeSpriteOnTargetStage(SpriteBox mySprite, StageManager targetStage) {
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
}

//end alert status for current sprite and reassign
private void moveAlertFromBoxtoBox(SpriteBox hadFocus, SpriteBox mySprite) {
    hadFocus = getCurrentSprite();
    if (hadFocus!=null) {
        hadFocus.endAlert();
    }
    setCurrentSprite(mySprite);
    mySprite.doAlert();
    }
       
//General function for box clicks
private void processBoxClick(MouseEvent t, StageManager mySM) {

SpriteBox hadFocus=null;
SpriteBox currentSprite = (SpriteBox)t.getSource();

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

        break;
    case 2:
        System.out.println("Two clicks");
        
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        
        //Dbl Click action options depending on box type
        ClauseContainer myNode = currentSprite.getBoxNode();
        if (mySM == Stage_EDITNODEPROP && (myNode.NodeIsLeaf()==false)) {
            editGroup_root = Main.this.setupNodePropertyEditor(mySM, editorStage);
            }
        if (mySM == Stage_EDITNODEPROP && (myNode.NodeIsLeaf()==true)) {
            editGroup_root = Main.this.setupClauseEditorPanel(mySM, editorStage);
        }
        else {
            openBoxesOnStage(mySM,myNode);
        }

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

public void pressMe() {
    System.out.println ("Button pressed - registered with main app");
}


/* Setup text area with blank text to start.  To put default text in at time of constructing,
insert text strings into TextArea arguments
make this public so that the inner class can find it  */

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

//fine tuning of region objects.   Not needed if working directly with Pane etc
public Region setWorkSpaceRegion(Node inputNode) {
    Region myRegion = new Region();
    return myRegion;
    //Paint myPaint = new Paint(Color.BEIGE);
    //BackgroundFill myBF = new BackgroundFill(myPaint); 
    /*
    BackgroundFill myBF = new BackgroundFill(Color.BEIGE,null,null); //cnr radii, inset
    Background myBG = new Background(myBF);
    Region myRegion = new Region ();
    myRegion.setBackground(myBG);
    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
    double myBigX = ScreenBounds.getWidth();
    double myBigY = ScreenBounds.getHeight()-100.00;
    myRegion.setPrefSize(myBigX,myBigY); //Node property
    
    //
    Region myRegion = setWorkSpaceRegion();
    //set layout of node in region...
        double zero = 0.0;
        double rw = 600;
        double rh = 600;
        double baseOff = 40;
        boolean fillWidth=true;
        boolean fillHeight=true;
        boolean snap=false;
        myRegion.layoutInArea(inputNode, zero,zero,rw,rh,baseOff,new Insets (40,40,40,40),fillWidth,fillHeight,HPos.LEFT,VPos.TOP,snap);
      
    return myRegion;
    */
}

public Scene updateWorkSpaceScene(Group myGroup) {
        
        workspaceScene = new Scene (myGroup,Stage_WS.getBigX(),Stage_WS.getBigY(), Color.BEIGE);
        
        //optional event handler
        workspaceScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent mouseEvent) {
             System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
            }
        });
        return workspaceScene;
    }
    
public Stage newWorkstageFromGroup(Group myGroup) {
        Scene myScene = updateWorkSpaceScene(myGroup);
        Stage myStage = Stage_WS.makeNewSpriteStage(myScene); 
        //Stage_WS.setSpriteGroup(myGroup);
        return myStage; 
}


/* Make menuBar for workspace */

private void makeMenuBarGroup(Group myGroup) {
        
        //MENUBAR SETUP
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
        MenuItem NewDoc = new MenuItem("Document");
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
         menuObject.getItems().addAll(NewDef,NewClause,NewEvent,NewDoc,NewLibrary,NewCollection,NewProject);
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
            SaveDoc,
            LoadDoc); //OutputDoc
        menuLibrary.getItems().addAll(
            SaveLibrary,
            LoadLibrary);
        menuOutput.getItems().addAll(
            SaveOutput);
        menuImport.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections);
        
        //PROJECT

        NewProject.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                NewNodeForStage(Stage_PROJLIB,2);
            }
        }); 


        SaveProject.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //Check
                SaveNode(Stage_PROJ);
            }
        });

        /* Load Collection into an open window TO DO: as icon.
        */
        LoadProject.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = Stage_PROJ.getFilename();
                LoadNode(filename,Stage_PROJLIB);
            }
        });
        
        //COLLECTION FUNCTIONS

        // New Collection object

        NewCollection.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                NewNodeForStage(Stage_COLL,1);
            }
        }); 

        //Save the current Collection object in use (open) by the GUI

        SaveColl.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                SaveNode(Stage_COLL);
            }
        });

        /* Load Collection into an open window TO DO: as icon.
        */
        LoadColl.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = Stage_COLL.getFilename();
                LoadNode(filename, Stage_PROJ);
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
                LoadNode(filename, Stage_WS);
            }
        });

        //EXPORT WORKSPACE TO OUTPUT
        OutputWork.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            WorkspaceNode.doPrintIteration();
            String output=WorkspaceNode.getClauseAndText();
            Stage_Output.setOutputText(output);
            Stage_Output.showStage();
            }
        });

        
        PrintBoxes.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                //call the 'print function' on the BoxContainer object (for now)
                WorkspaceBoxes.ContentsDump();
                //TO DO: ADD SERIALISATION OR FUNCTION CALL
            }
        });    

        /* --- New clause/leaf nodes --- */
        //New document
        NewDef.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                System.out.println("New Def button - called from Main");
                NewNodeForStage(Stage_DOC,0);
            }
        });

        NewClause.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                System.out.println("New Clause button - called from Main");
                NewNodeForStage(Stage_DOC,0);
            }
        });

        NewEvent.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                System.out.println("New Event button - called from Main");
                NewNodeForStage(Stage_DOC,0);
            }
        });

        //----DOCUMENT EVENT HANDLER FUNCTIONS 
        
        //New document
        NewDoc.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                System.out.println("Event handler in main detected - newdoc button");
                NewNodeForStage(Stage_COLL,1);
            }
        });

        //Trial method to save a Clause Container (just the documentNode)
        SaveDoc.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                SaveNode(Stage_DOC);
            }
        });

        //load documents from single clause container
        LoadDoc.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = Stage_DOC.getFilename();
                LoadNode(filename,Stage_COLL);
            }
        });

        //LIBRARY load and save functions
        SaveLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                SaveNode(Stage_LIB);
            }
        });    

        LoadLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                String filename = Stage_LIB.getFilename();
                LoadNode(filename,Stage_PROJ);
            }
        });


        //Load up an empty library window
        //TO DO: Save under different name

        NewLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                NewNodeForStage(Stage_PROJLIB,1);
            }
        }); 

        //Toggle visibility of Document window
        viewDocument.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_DOC);
            }
        });

        //Toggle visibility of Library window
        viewLibrary.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_LIB);
            }
        });

        //Toggle visibility of Project Lib window
        viewProjectLib.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_PROJLIB);
            }
        });

        //Toggle visibility of Project window
        viewProject.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_PROJ);
            }
        });

        //Toggle visibility of Collection window
        viewCollection.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_COLL);
            }
        });

        //Toggle visibility of output window
        viewtextmaker.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_Output);
            }
        });

        //toggle visibility of editor
        viewEditor.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_EDITNODEPROP);
            }
        });

         //toggle visibility of importer
        viewImporter.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_Import);
            }
        });

         //toggle visibility of toolbar
        viewToolbar.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                toggleView(Stage_Toolbar);
            }
        });

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

        

        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuViews, menuObject,menuWorkspace, menuDocument, menuLibrary, menuOutput, menuImport,menuCollection,menuProject);     
        myGroup.getChildren().addAll(menuBar);
}

/* 

Setup workspace stage with 2 subgroups for vertical separation:
(a) menubar
(b) sprite display area.  (this group is returned to become WorkspaceGroup - global.)

*/

public Group makeWorkspaceGroups() {

        Group myGroup_root = new Group(); //for root node
        BorderPane myBP = new BorderPane();
        Group menubarGroup = new Group(); //subgroup
        Group displayAreaGroup = new Group(); //subgroup
        makeMenuBarGroup(menubarGroup); 
        
        //the Pane holding the group allows movement of SpriteBoxes independently, without relative movement
        Pane workspacePane = new Pane();
        //workspacePane.setPadding(new Insets(150,150,150,150));
        workspacePane.getChildren().addAll(displayAreaGroup);
        
        //The BorderPane holds the menu separate from Pane, adds constraints. 
        myBP.setTop(menubarGroup);
        myBP.setCenter(workspacePane);
        myBP.setMargin(workspacePane, new Insets(50,50,50,50));
        //add the Box Pane
        myGroup_root.getChildren().addAll(myBP);
        Stage_WS.setSceneRoot(myGroup_root); //store 
        //for box placement within the Scene - attach them to the correct Node.
        Stage_WS.setSpriteGroup(displayAreaGroup);
        return myGroup_root;  
    }

/* TO DO: One for edit metadata/properties; another for editing clauses
*/

public Pane setupNodePropertyEditor(StageManager mySM, Stage myStage) {

        System.out.println("Setup node property editor Panel");
        myStage.setTitle("Node Property Editor");
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
        SpriteBox focusSprite = getCurrentSprite();
        System.out.println("Current sprite for edit:"+focusSprite.toString());
        myEditCC = focusSprite.getBoxNode();
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
        
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateContainerEditor);

        //Button for cancel
        Button btnEditCancel = new Button();
        btnEditCancel.setText("Cancel Edits");
        btnEditCancel.setTooltip(new Tooltip ("Press to Cancel current edits"));
        
        //Set horizontal box to hold buttons
        HBox hboxButtons = new HBox(0,btnUpdate,btnEditCancel);
        //Finish
        VBox vboxAll = new VBox(0,vboxEdit,hboxButtons);
        vboxAll.setPrefWidth(200);
        //
        editorPane.getChildren().add(vboxAll); //add the vbox to the root node to hold everything
        //Stage Management and Layout
        mySM.setStageName("Editor");
        mySM.setStageParent(Stage_WS);
        mySM.setPosition();
        mySM.getStage().show();
        mySM.setSceneRoot(editorPane);
        return editorPane;
}

/* Setup Stage as a Clause inspection and edit Window */

public Pane setupClauseEditorPanel(StageManager myStageManager, Stage myStage) {

        System.out.println("Making editor Panel");
        myStage.setTitle("Clause Content Editor Panel");
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
        SpriteBox focusSprite = getCurrentSprite();
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

        if (editClause.getCategory().equals("event")) {
            headingTag.setText("Event:");
            contentsTag.setText("Description:");
            dateTag.setText("Date:");
            dateEdit.setText(((Event)editClause).getDate());
        }

        labelEdit.setText(editClause.getHeading());
        headingEdit.setText(editClause.getHeading());
        textEdit.setText(editClause.getClauseText());
        categoryEdit.setText(editClause.getCategory());

        myStage.setScene(editorScene); //set current scene for the Stage
        //Layout
        myStageManager.setStageParent(Stage_WS);
        myStageManager.setPosition();
        myStage.show();
        
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateClauseInEditor);

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
        
        //Stage Management and Layout
        myStageManager.setStageName("Editor");
        myStageManager.setStageParent(Stage_WS);
        myStageManager.setPosition();
        myStageManager.getStage().show();

        return editorPanel_root;
}

private VBox makeToolBarButtons() {

        //Button for removing clauses
        Button btnDeleteClause = myControlsManager.newStdButton();
        btnDeleteClause.setTooltip(new Tooltip ("Press to delete selected box"));
        btnDeleteClause.setText("Delete");
        btnDeleteClause.setOnAction(this.deleteCurrentSprite);

        //Button for moving clauses to Workspace
        Button btnMoveClauseWS = myControlsManager.newStdButton();
        btnMoveClauseWS.setText("Move to Workspace");
        btnMoveClauseWS.setTooltip(new Tooltip ("Press to move box to Workspace Window"));
        btnMoveClauseWS.setOnAction(MoveBoxtoWorkspace);

        //Button for moving clauses to Library
        //To DO: only visible if Library has been loaded
        Button btnMoveClauseLib = myControlsManager.newStdButton();
        btnMoveClauseLib.setText("Move to Library");
        btnMoveClauseLib.setTooltip(new Tooltip ("Press to move box to Library Window"));
        btnMoveClauseLib.setOnAction(MoveBoxtoLibrary);

        //Button for moving clauses to Document
        Button btnMoveClauseDoc = myControlsManager.newStdButton();
        btnMoveClauseDoc.setText("Move to Document");
        btnMoveClauseDoc.setTooltip(new Tooltip ("Press to move box to Document Window"));
        btnMoveClauseDoc.setOnAction(MoveBoxtoDocument);

        //Button for copying clause to document (leaves copy behind)
        Button btnCopyClauseDoc = myControlsManager.newStdButton();
        btnCopyClauseDoc.setText("Copy to Document");
        btnCopyClauseDoc.setTooltip(new Tooltip ("Press to copy box to Document Window"));
        btnCopyClauseDoc.setOnAction(CopyBoxtoDocument);

        //Button for copying clause to library (leaves copy in workspace)
        Button btnCopyClauseLib = myControlsManager.newStdButton();
        btnCopyClauseLib.setText("Copy to Library");
        btnCopyClauseLib.setTooltip(new Tooltip ("Press to copy box to Library Window"));
        btnCopyClauseLib.setOnAction(CopyBoxtoLibrary);

        //Button for copying clauses to workspace
        Button btnCopyClauseWS = myControlsManager.newStdButton();
        btnCopyClauseWS.setText("Copy to Workspace");
        btnCopyClauseWS.setTooltip(new Tooltip ("Press to copy box to Workspace"));
        btnCopyClauseWS.setOnAction(CopyBoxtoWorkspace);

        //Button for copying ClauseContainer to Collection (leaves copy in workspace)
        Button btnCopyCC = myControlsManager.newStdButton();
        btnCopyCC.setText("Copy to Collection");
        btnCopyCC.setTooltip(new Tooltip ("Press to copy box to Collection"));
        btnCopyCC.setOnAction(CopyBoxtoCollection);

        //CopyBoxtoProject
        Button btnCopyColl = myControlsManager.newStdButton();
        btnCopyColl.setText("Copy to Project");
        btnCopyColl.setTooltip(new Tooltip ("Press to copy box to Project"));
        btnCopyColl.setOnAction(CopyBoxtoProject);

        //doEdit
        Button btnDoEdit = myControlsManager.newStdButton();
        btnDoEdit.setText("Edit");
        btnDoEdit.setTooltip(new Tooltip ("Press to Edit Selection (Red Block)"));
        btnDoEdit.setOnAction(DoPropertyEditStage);

        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClauseWS,btnCopyClause);
        VBox vbox1 = new VBox(0,btnCopyColl,btnCopyCC,btnMoveClauseWS,btnCopyClauseWS,btnMoveClauseDoc, btnCopyClauseDoc, btnCopyClauseLib,btnMoveClauseLib,btnDeleteClause,btnDoEdit);
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth);
        return vbox1;

}

/* Setup Stage as a Toolbar Panel for Sprite Move, Copy functions etc */

public Group setupToolbarPanel(StageManager mySM, Stage myStage, String myTitle) {

        //do this before .show
        mySM.setStage(myStage);
        mySM.setStageParent(Stage_WS);
        
        //Instance variable
        Group toolbar_root = new Group(); //for root
        toolbarScene = new Scene (toolbar_root,150,350, Color.GREY); //default width x height (px)
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
        toolbar_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
       
        //setup Stage config
        mySM.setStageParent(Stage_WS);
        mySM.setPosition();
        mySM.setSceneRoot(toolbar_root);
        myStage.setTitle(myTitle);
        myStage.setScene(toolbarScene); //set current scene for the Stage
        myStage.show();

        return toolbar_root;
}


/** Setup independent definitions window 
@Returns a Scrollpane representing the root node

@notes Scene size will determine initial width of Stage window 

**/

public ScrollPane setupDefinitionsWindow(StageManager myStageManager) {
        
        defsTextStage = new Stage();
            
        //create a scrollpane as root with a text area inside
        ScrollPane rootnode_scroll = new ScrollPane();
        rootnode_scroll.setFitToHeight(true);
        rootnode_scroll.setFitToWidth(true);

        //make Text Area
        double width = 800; 
        double height = 500; 
        textArea3.setPrefHeight(height);  
        textArea3.setPrefWidth(width);
        textArea3.setWrapText(true);

        //add Text Area
        rootnode_scroll.setContent(textArea3); 

        //Add rootnode to Scene graph
        int setWidth=500;
        int setHeight=500;
        Scene defsTextScene = new Scene (rootnode_scroll,setWidth,setHeight); //width x height (px)
        defsTextScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected for text scroll window! " + mouseEvent.getSource());
             }
        });
        //Add Scene to Stage, size and position 
        defsTextStage.setScene(defsTextScene);
        defsTextStage.setY(350);
        defsTextStage.show();

        myStageManager.setStageParent(Stage_WS);
        myStageManager.setPosition();
        myStageManager.setSceneRoot(rootnode_scroll);
        myStageManager.setTitle("Definitions List");
        
        return rootnode_scroll; 
        }

//Function to setup independent output window

public void setupTextOutputWindow(StageManager myStageManager, String myTitle) {

        myStageManager.putTextScrollerOnStage();
        myStageManager.setOutputText("Some future contents");
        myStageManager.hideStage();
        myStageManager.setTitle(myTitle);
        myStageManager.setStageParent(Stage_WS);
        myStageManager.setPosition();
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

/* Method to open the node in a new Stage window */

private void openNodeInNewStage(ClauseContainer myContainer) {
        StageManager myStageManager = new StageManager();
        myStageManager.defaultConfigStage();
        openBoxesOnStage(myStageManager, myContainer);
    }

/* Method to open the node in existing Stage  */

public void openBoxesOnStage(StageManager mySM, ClauseContainer myNode) {

    mySM.openBoxesOnStage(myNode);
    setCurrentSprite(mySM.getFocusBox());
    /*
    mySM.resetSpriteOrigin();
    mySM.defaultConfigStage();
    mySM.setTitle(myNode.getDocName());
    mySM.setDisplayNode(myNode);
    displayBoxesOnStage(mySM,myNode);
    */
}

/* Box up a container of Sprites and place on Stage */

 public void displayBoxesOnStage(StageManager myStageManager, ClauseContainer myNode) {
    
        ArrayList<ClauseContainer> myNodes = myNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = myNodes.iterator();

        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            SpriteBox b = makeBoxWithNode(thisNode);
            placeSpriteOnStage(b, myStageManager);
        }
        setCurrentSprite(myStageManager.getFocusBox());
        myStageManager.getStage().show();
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

//General method to place sprite on Stage.  Uses Stage Manager class 
//Since data nodes are to mirror GUI, update parent child relations here too

private void placeSpriteOnStage(SpriteBox mySprite, StageManager targetStage) {
    SpriteBox prevSprite = getCurrentSprite(); //not based on the button
    if (prevSprite !=null) {
        prevSprite.endAlert(); 
        System.out.println("Ended alert:"+prevSprite.toString());
    }
    setCurrentSprite(mySprite); 
    targetStage.addSpriteToStage(mySprite);
    
    }


private void placeCurrentSpriteOnStage(StageManager targetStage) {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    ClauseContainer currentParent = currentSprite.getBoxNode().getParentNode();
    unsetParentChild(currentSprite);
    targetStage.addSpriteToStage(currentSprite);
    setParentChild(targetStage,currentSprite);
}

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

public void deleteSprite(SpriteBox mySprite) {
    
    StageManager mySM = mySprite.getStageLocation();
    mySM.removeSpriteFromStage(mySprite);
    mySM.getStage().show(); 
    unsetParentChild(mySprite);
}


//new Stage constructor.  If useful, move to StageManager constructor
public StageManager newStageConst(StageManager mySM, String myFileLabel, ClauseContainer defaultNode) {
    mySM = new StageManager(myFileLabel); //category
    mySM.setStageParent(Stage_WS);
    mySM.defaultConfigStage(); //empty
    mySM.setTitle(myFileLabel);
    mySM.setFilename(myFileLabel+".ser"); //default
    mySM.setDisplayNode(defaultNode); //default Node
    mySM.setPressBox(PressBoxEventHandler);
    mySM.setDragBox(DragBoxEventHandler);
    mySM.hideStage(); //set default as hidden for now
    return mySM;
}

/*
public void setStageInit (StageManager mySM, String myTitle)  {
    mySM.setInitStage(Stage_WS);
    Group tempGroup = Main.this.setupNewSpriteStage(mySM);
    mySM.setSpriteGroup(tempGroup);
}

public void identifyStages (StageManager mySM, Stage myStage, Group myGroup) {
    myStage = mySM.getStage();
    myGroup = mySM.getSpriteGroup();
}

*/


/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
       
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("Powerdock App");
        primaryStage.show();
        primaryStage.close();
        
        ParentStageSM = new StageManager();
        ParentStage = new Stage();
        ParentStageSM.setStage(ParentStage);

        //
    
        Stage_WS = new StageManager("workspace"); //category
        Stage_WS.setTitle("Workspace");
        Stage_WS.setFilename("workspace.ser");
        Stage_WS.setPressBox(PressBoxEventHandler);
        Stage_WS.setDragBox(DragBoxEventHandler);
        Stage_WS.setDisplayNode(WorkspaceNode); //default Node
        WorkspaceGroup=makeWorkspaceGroups();
        WorkspaceStage = newWorkstageFromGroup(WorkspaceGroup);        
       
        //Group tempGroup = Main.this.setupNewSpriteStage(mySM);
        //Stage_WS.setSpriteGroup(tempGroup);
        //identifyStages(Stage_WS,WorkspaceStage,WorkspaceGroup);
        
        //setup Project Libary window
        
        //setStageInit(Stage_PROJLIB,ProjectLibStage,ProjectLibGroup,"Project Library");
        //identifyStages(Stage_PROJLIB,ProjectLibStage,ProjectLibGroup);

        //configStageMan(Stage_PROJLIB,ProjectLibStage,ProjLibScene,ProjectLibGroup,"Project Library");
        
        //configStageMan(Stage_PROJ,ProjectStage,ProjectScene,ProjectGroup,"Project");

        //configStageMan(Stage_COLL,CollectionStage,CollectionScene,CollectionGroup,"Collection");
        
        Stage_COLL=newStageConst(Stage_COLL,"collection", collectionNode);
        Stage_LIB=newStageConst(Stage_LIB,"library", libraryNode);
        Stage_DOC=newStageConst(Stage_DOC,"document", documentNode);
        Stage_PROJ=newStageConst(Stage_PROJ,"project", projectNode);
        Stage_PROJLIB=newStageConst(Stage_PROJLIB,"projlib", projectLibNode);

        /*
        Stage_COLL = new StageManager("collection"); //category
        Stage_COLL.setStageParent(Stage_WS);
        Stage_COLL.defaultConfigStage(); //empty
        Stage_COLL.setTitle("collection");
        Stage_COLL.setFilename("collection.ser"); //default
        Stage_COLL.setDisplayNode(collectionNode); //default Node
        Stage_COLL.setPressBox(PressBoxEventHandler);
        Stage_COLL.setDragBox(DragBoxEventHandler);
        Stage_COLL.showStage();
        */
        

        //configStageMan(Stage_LIB,LibraryStage,LibraryScene,LibraryGroup,"Library");
        /*
        Stage_LIB = new StageManager("library"); //category
        Stage_LIB.setStageParent(Stage_WS);
        Stage_LIB.defaultConfigStage(); //empty
        Stage_LIB.setTitle("library");
        Stage_LIB.setFilename("library.ser");
        Stage_LIB.setDisplayNode(libraryNode); //default Node
        Stage_LIB.setPressBox(PressBoxEventHandler);
        Stage_LIB.setDragBox(DragBoxEventHandler);
        Stage_LIB.showStage();
        */
        
        /*
        Stage_DOC = new StageManager("document"); //category
        Stage_DOC.setStageParent(Stage_WS);
        Stage_DOC.defaultConfigStage(); //empty
        Stage_DOC.setTitle("document");
        Stage_DOC.setFilename("document.ser");
        Stage_DOC.setDisplayNode(documentNode); //default Node
        Stage_DOC.setPressBox(PressBoxEventHandler);
        Stage_DOC.setDragBox(DragBoxEventHandler);
        Stage_DOC.showStage();
        */

        //configStageMan(Stage_DOC,DocumentStage,DocumentScene,DocumentGroup,"Document");

        /*setup Editor window
        editorStage = new Stage();
        StageManager Stage_EDITCLAUSE = new StageManager();
        Stage_EDITCLAUSE.setStageParent(ParentStage,editorStage);
        */

        /*
        //setup Node Propety Editor window
        editorStage = new Stage();
        StageManager Stage_EDITNODEPROP = new StageManager();
        Stage_EDITNODEPROP.setTitle("Editor");
        Stage_EDITNODEPROP.setStageParent(ParentStage,editorStage);
        */

        /*
        //*setup Import window (text input display and editing)
        ImportStage = new Stage();
        Stage_Import = new StageManager();
        this.setupImportStage(Stage_Import,ImportStage,"Text Importer");
        //set some default text in main text window
        //this.myTextFile="popstarlease.txt";
        this.myTextFile="electricity.txt";
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        // use this line if you want it by default: ImportStage.show();
        ImportStage.hide();
        */
        //setup main toolbar for buttons
        toolbarStage = new Stage();
        Stage_Toolbar = new StageManager();
        toolbarGroup = Main.this.setupToolbarPanel(Stage_Toolbar,toolbarStage, "Toolbar");

        /* Setup default text Output Stage  */
        Main.this.setupTextOutputWindow(Stage_Output,"Output");
        
        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

/* This is a defined Eventhandler object (holding a function) for mouse clicks on Project boxes */

    EventHandler<MouseEvent> PressProjBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t,Stage_PROJ);
            t.consume();
        }
    };


/* This is a method to create eventhandler for mouse clicks on Collection boxes */

    EventHandler<MouseEvent> PressCollBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t,Stage_COLL);
            t.consume();
            }
    };

    /* This is a method to create eventhandler for Document ClauseContainer objects */

    EventHandler<MouseEvent> PressDocBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t,Stage_DOC);
            t.consume();
        }
    };

    
    /* This is a method to create eventhandler for Library ClauseContainer objects */

    EventHandler<MouseEvent> PressLibBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t,Stage_LIB);
            t.consume();
        }
    };
    
    /* Event handler added to box with clause content */

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t,Stage_EDITNODEPROP);
            t.consume();
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
            //end alert status for sprite
            SpriteBox hasFocus = getCurrentSprite();
            hasFocus.endAlert();
            //change the active sprite to the current touched sprite.
            setCurrentSprite(currentSprite);
            System.out.println("The handler for drag box is acting");
            //updates to sprite that triggered event
            currentSprite.setTranslateX(newTranslateX);
            currentSprite.setTranslateY(newTranslateY);
            currentSprite.doAlert(); //in case single click event doesn't detect
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
            //SpriteBox currentSprite = getCurrentSprite(); //not based on the button
            //lose focus
            SpriteBox hadFocus = getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                        System.out.println("Deleting..."+hadFocus.toString());
                        deleteSprite(hadFocus);
                    }
                    else {
                        System.out.println("No current sprite to delete");
                    }
            }
        };

    // Method to move selected sprite to Clause WIP (will not duplicate)
    /*
            The following 'add' actually copies to the second stage.
            By moving the object or referring to it on the new Stage, it forces JavaFX to refresh.

            Java FX does its own cleanup.

            To achieve a 'copy' rather than a move, additional code needed.

     */

    EventHandler<ActionEvent> MoveBoxtoWorkspace = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            placeCurrentSpriteOnStage(Stage_WS);
            }
        };

    EventHandler<ActionEvent> MoveBoxtoLibrary = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            placeCurrentSpriteOnStage(Stage_LIB);
            }
    };

    EventHandler<ActionEvent> MoveBoxtoDocument = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            placeCurrentSpriteOnStage(Stage_DOC);
            }
    };

    EventHandler<ActionEvent> CopyBoxtoProject = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            copyCurrentSpriteToDestination(Stage_PROJ);
        }
    };

    EventHandler<ActionEvent> CopyBoxtoCollection = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            copyCurrentSpriteToDestination(Stage_COLL);  
        }
    };

    EventHandler<ActionEvent> CopyBoxtoLibrary = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) { 
            copyCurrentSpriteToDestination(Stage_LIB);
        }
    };

    EventHandler<ActionEvent> CopyBoxtoDocument = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            copyCurrentSpriteToDestination(Stage_DOC);
        }
    };

    /* This is a copy not a move */

    EventHandler<ActionEvent> CopyBoxtoWorkspace = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            copyCurrentSpriteToDestination(Stage_WS);
        }
    };

    /* Invoke the SpriteBox/Clause Property Editor */

    EventHandler<ActionEvent> DoPropertyEditStage = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Edit Button was pressed!");
            editGroup_root = Main.this.setupNodePropertyEditor(Stage_EDITNODEPROP,editorStage); //mySM, Stage
        }
    };
     
    //printClauseList
        EventHandler<ActionEvent> printClauseList = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
             //textmakerTextArea.setText("This is where list of clauses will appear");
             WorkspaceNode.doPrintIteration();
             String output=WorkspaceNode.getClauseAndText();
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
            openNodeInNewStage(NodeFromDefinitionsSampleText(textArea1.getText()));
        }
    };

     //Make boxes for imported clauses

    EventHandler<ActionEvent> makeClauseBoxesFromText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        
        openNodeInNewStage(NodeFromClausesSampleText(textArea1.getText()));
        }
    };
    

     //Make boxes for imported statute clauses

    EventHandler<ActionEvent> makeBoxesFromStatuteText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        //TO DO: get source of data
        openNodeInNewStage(NodeFromStatuteSampleText(textArea1.getText()));
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
            StageManager myStageManager = new StageManager();
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
            myStageManager.setStageParent(Stage_WS);
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
    
        EventHandler<ActionEvent> extractDefinitions = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Get Defs Button was pressed!");
            //temp StageManager
            Main.this.setupTextOutputWindow(Stage_Definitions,"Definitions Imported");
            //Outer class method class to obtain text from analysis area
            String gotcha = Main.this.textArea1.getText();
            String newDefs = Main.this.getMatched(gotcha);
            
            //now set the content of text area inside scrollpane to our extracted text
            Stage_Definitions.setOutputText(newDefs);
            
            }
        };
        
        /* Update Container or Collectoin in Container Editor */
        
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
            SpriteBox focusSprite = getCurrentSprite();
            focusSprite.setBoxNode(myEditCC);
            }
        };

        /* Update Clause in Editor */
        
        EventHandler<ActionEvent> UpdateClauseInEditor = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
           
            if (editClause instanceof Event) {
                ((Event)editClause).setDate(dateEdit.getText());
                System.out.println("Event updated!");
            }
            editClause.setClauselabel(headingEdit.getText());
            editClause.setHeading(headingEdit.getText());
            editClause.setClauseText(textEdit.getText());
            editClause.setCategory(categoryEdit.getText());
            System.out.println("Clause updated!");
            //update the SpriteBox on the GUI
            SpriteBox focusSprite = getCurrentSprite();
            ClauseContainer tempCC = focusSprite.getBoxNode();
            tempCC.addNodeClause(editClause);
            }
        };
}