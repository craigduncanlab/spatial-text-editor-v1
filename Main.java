/** This application creates a GUI as a legal doc environment
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

 //package classes
 //import WordTool;


/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

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
    //Main Stage (window) that owns all other Stages
    Stage ParentStage;
    Group ParentWIPGroup;
    ClauseContainer ParentWIPClauseContainer;
    //Inspector window (no edits)
    Stage inspectorWindow;
    Scene inspectorScene;
    ScrollPane inspectorGroup_root;
    TextArea inspectorTextArea = new TextArea();
    //Display SpriteBoxes window(s)
    Scene defScene;  //<----used multiple times in different methods.  TO DO:  localise Scene variables.
    Group defGroup_root; //<---used for display Sprites in new stage
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Clause editor
    TextArea labelEdit;
    TextArea headingEdit;
    TextArea textEdit;
    TextArea categoryEdit;
    Clause editClause;
    //Group editGroup_root;
    Pane editGroup_root;

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
        double windowWidth = 800;
        double windowHeight = leftColHeight+100;
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
        HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);

        //Button for Word Counts with Action Event handler
        Button btn = new Button();
        //myControlsManager.newStdButton();
        btn.setText("Update Word Counts");
        btn.setOnAction(updateWordCounts);
        
        //Button for definitions with Action Event handler
        Button btnDefs = new Button();
        btnDefs.setTooltip(new Tooltip ("Press to extract definitions from top text area"));
        btnDefs.setText("Extract Definitions");
        btnDefs.setOnAction(extractDefinitions);

        //Button for definitions icons with Action Event handler
        Button btnDefIcons = new Button();
        btnDefIcons.setTooltip(new Tooltip ("Press to create definitions icons from top text area"));
        btnDefIcons.setText("Extract Def Icons");
        btnDefIcons.setOnAction(makeDefIcons);

        //Button for Clause blocks with Action Event handler
        Button btnClauses = new Button();
        btnClauses.setTooltip(new Tooltip ("Press to extract Clauses from top Text Area"));
        btnClauses.setText("Extract Clause Icons");
        btnClauses.setOnAction(makeClauseIcons);

        //Button for Importing Statutory Sectoins 
        Button btnImportStatute = new Button();
        btnImportStatute.setTooltip(new Tooltip ("Press to extract Statute Cl. from top Text Area"));
        btnImportStatute.setText("Extract Statute Sectns");
        btnImportStatute.setOnAction(importStatuteClauses);

        //Set horizontal box to hold buttons and place horizontal boxes inside vertical box
        hbox3 = new HBox(0,btn,btnDefs,btnDefIcons, btnClauses, btnImportStatute);
        VBox vbox2 = new VBox(0,hbox1);
        vbox2.setPrefWidth(totalwidth);
        vbox2.getChildren().add(hbox3);
        
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

/* Setup method to create a space to add or remove clauses, and then process them into some kind of output

At this stage, the root node is a Group (sizes according to children, unlike Pane).

*/

public Group setupClauseWIPstage(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        
        
        Group myGroup_root = new Group(); //for root
        ParentWIPClauseContainer = new ClauseContainer();
        ParentWIPGroup = new Group(); //for child node
        //add group layout object as root node for Scene at time of creation
        //defScene = new Scene (myGroup_root,650,300); //default width x height (px)
        defScene = new Scene (myGroup_root,myStageManager.getBigX(),myStageManager.getBigY(), Color.BEIGE);
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Clause WIP Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("ClauseWIP");
             }
        });

               //
        myStage.setScene(defScene); //this selects the stage as current scene
        //Position
        myStageManager.setPosition(ParentStage,myStage,"WIP");
        myStage.show();
        
        /*
        //Button for new clauses
        Button btnNewClause = new Button();
        btnNewClause.setText("Add New Clause");
        btnNewClause.setTooltip(new Tooltip ("Press to add a new clause"));
        btnNewClause.setOnAction(addNewClauseBox);

        //Button for new definitions addNewDefBox
        Button btnNewDef = new Button();
        btnNewDef.setText("Add New Definition");
        btnNewDef.setTooltip(new Tooltip ("Press to add a new definition"));
        btnNewDef.setOnAction(addNewDefBox);
        
        //Button for removing clauses
        Button btnDeleteClause = new Button();
        btnDeleteClause.setTooltip(new Tooltip ("Press to remove selected clause"));
        btnDeleteClause.setText("Remove Clause");
        //btnDeleteClause.setOnAction(extractDefinitions);

        //Button for summary print list of clauses
        Button btnClausePrint = new Button();
        btnClausePrint.setTooltip(new Tooltip ("Press to list all clauses in inspector/console"));
        btnClausePrint.setText("Print List");
        btnClausePrint.setOnAction(printClauseList);

        //Button for export/document clauses TO DO: some config or separate panel.
        Button btnExportClause = new Button();
        btnExportClause.setTooltip(new Tooltip ("Press to output clauses as RTF"));
        btnExportClause.setText("RTF Export");
        //btnDeleteClause.setOnAction(extractDefinitions);

        //Set horizontal box to hold buttons
        HBox hboxButtons = new HBox(0,btnNewDef,btnNewClause,btnDeleteClause,btnClausePrint);
        VBox vbox1 = new VBox(0,ParentWIPGroup,hboxButtons);
        */
        //
        VBox vbox1 = new VBox(0,ParentWIPGroup);
        myGroup_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=650;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return ParentWIPGroup;
        
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

        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        //Group editorPanel_root = new Group(); 
        Pane editorPanel_root = new Pane();

        Scene editorScene = new Scene (editorPanel_root,300,600, Color.GREY); //default width x height (px)
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
        Text headingTag = new Text("heading:");
        headingEdit = new TextArea();
        Text contentsTag = new Text("Contents:");
        textEdit = new TextArea();
        textEdit.setMinHeight(300);
        textEdit.setWrapText(true);
        Text categoryTag = new Text("Category:");
        categoryEdit = new TextArea();
        //
        VBox vboxEdit = new VBox(0,labelTag,labelEdit,headingTag,headingEdit,contentsTag,textEdit,categoryTag,categoryEdit);
        vboxEdit.setMinWidth(300);  //150
        vboxEdit.setPrefHeight(500); //250
        //vboxEdit.setVgrow(textEdit, Priority.ALWAYS);
        //
        SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
        editClause = focusSprite.getClause();
        labelEdit.setText(editClause.getLabel());
        headingEdit.setText(editClause.getHeading());
        textEdit.setText(editClause.getClause());
        categoryEdit.setText(editClause.getCategory());
        //
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
        int totalwidth=190;
        vboxAll.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case? e.g.vBoxEdit?
        return editorPanel_root;
}


/* Setup Stage as a Toolbar Panel for Sprite Move, Copy functions etc */

public Group setupToolbarPanel(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        Group toolbar_root = new Group(); //for root
        Scene toolbarScene = new Scene (toolbar_root,150,150, Color.GREY); //default width x height (px)
        //optional event handler
        toolbarScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Extracted Clause Window: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("Toolbar");
             }
        });

        //
        myStage.setScene(toolbarScene); //this selects the stage as current scene
        //Layout
        myStageManager.setPosition(ParentStage,myStage,"WIP Toolbar");
        myStage.show();
        
        //Button for new clauses
        //Button btnNewClause = new Button();
        Button btnNewClause = myControlsManager.newStdButton();
        btnNewClause.setText("Add New Clause");
        btnNewClause.setTooltip(new Tooltip ("Press to add a new clause"));
        btnNewClause.setOnAction(addNewClauseBox);

        //Button for new definitions addNewDefBox
        //Button btnNewDef = new Button();
        Button btnNewDef = myControlsManager.newStdButton();
        btnNewDef.setText("Add New Def");
        btnNewDef.setTooltip(new Tooltip ("Press to add a new definition"));
        btnNewDef.setOnAction(addNewDefBox);
        
        /*
        //Button for removing clauses
        Button btnDeleteClause = new Button();
        btnDeleteClause.setTooltip(new Tooltip ("Press to remove selected clause"));
        btnDeleteClause.setText("Remove Clause");
        //btnDeleteClause.setOnAction(extractDefinitions);
        */

        //Button for summary print list of clauses
        Button btnClausePrint = myControlsManager.newStdButton();
        btnClausePrint.setTooltip(new Tooltip ("Press to list all clauses in inspector/console"));
        btnClausePrint.setText("List Stage");
        btnClausePrint.setOnAction(printClauseList);

        /*//Button for export/document clauses TO DO: some config or separate panel.
        Button btnExportClause = new Button();
        btnExportClause.setTooltip(new Tooltip ("Press to output clauses as RTF"));
        btnExportClause.setText("RTF Export");
        //btnDeleteClause.setOnAction(extractDefinitions);
        */

        //Button for moving clauses
        Button btnMoveClause = myControlsManager.newStdButton();
        btnMoveClause.setText("Place on Stage");
        btnMoveClause.setTooltip(new Tooltip ("Press to move clause to Clause WIP Window"));
        btnMoveClause.setOnAction(MoveClausetoWIP);

        /* //Button for copying clauses
        Button btnCopyClause = new Button();
        btnCopyClause.setText("Copy to WIP");
        btnCopyClause.setTooltip(new Tooltip ("[TBA] Press to copy clause to Clause WIP Window"));
        //btnCopyClause.setOnAction(CopyClausetoWIP);
        */

        Button btnDoEdit = myControlsManager.newStdButton();
        btnDoEdit.setText("Edit");
        btnDoEdit.setTooltip(new Tooltip ("Press to Edit Selection (Red Block)"));
        btnDoEdit.setOnAction(DoEditStage);


        //TO DO:  Buttons for 'Copy to Library' {Definition Library}{Clause Library}
        //Button for "Load a clause library from disk"  etc
        
        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClause,btnCopyClause);
        VBox vbox1 = new VBox(0,btnNewDef,btnNewClause,btnClausePrint,btnMoveClause,btnDoEdit);
        
        //VBox vbox1 = new VBox(0,btnMoveClause,btnCopyClause,btnDoEdit);
        //
        toolbar_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return toolbar_root;
}


/** Setup independent text inspector window 
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
         
        //inspector panel settings
        if (StageType.equals("inspector")) {
            setWidth=250;
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

/* Method to pass over request to place sprite to SpriteManager 
    Adds the instance variable Group for benefit of the SpriteManager
    */

    public void placeOnMainStage(SpriteBox thisSprite) {
        ParentWIPGroup.getChildren().add(thisSprite); 
        mySpriteManager.placeOnMainStage(thisSprite);
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
        Group clausePlayBox = Main.this.setupClauseWIPstage(ParentStage, "Main Stage");

        //*Stage that I will use for main text input display and editing
        Stage myStage = new Stage();
        this.setupImportStage(myStage,"Text Importer");
        //set some default text in main text window
        //this.myTextFile="popstarlease.txt";
        this.myTextFile="electricity.txt";
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        myStage.show();

        //setup main toolbar
        Stage toolbarStage = new Stage();
        Group toolbarGroup = Main.this.setupToolbarPanel(toolbarStage, "Toolbar");

        /* Setup default Stage with Scrollpane to display Text as Inspector
        */
        inspectorWindow = new Stage();
        double width = 600; 
        double height = 500; 
        inspectorGroup_root = Main.this.setupScrollTextWindow(inspectorWindow, "inspector", "Inspector Window");
        inspectorGroup_root.setPrefHeight(height);  
        inspectorGroup_root.setPrefWidth(width);
        inspectorGroup_root.setContent(inspectorTextArea); 
        /* OLD //Outer class method class to obtain text from analysis area
        String gotcha = Main.this.textArea1.getText();
        String newDefs = Main.this.getMatched(gotcha);
        */
        //set the default scrollpane content to a designated text area and size scrollpane
        inspectorTextArea.setWrapText(true);
        inspectorTextArea.setText("Some future contents");

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
            //change colour if double click

            switch(t.getClickCount()){
                case 1:
                    System.out.println("One click");
                    //change colour or something
                    break;
                case 2:
                    System.out.println("Two clicks");
                    
                    //unfocus current Sprite - only works for the Sandbox? or record in any window?  
                    SpriteBox hadFocus = mySpriteManager.getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                    }
                    SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
                    currentSprite.doAlert();
                    //change target in WIP stage
                    
                    if (mySpriteManager.getStageFocus().equals("ClauseWIP")) {
                        mySpriteManager.setTargetSprite(currentSprite);
                    }
                    
                    mySpriteManager.setCurrentSprite(currentSprite);  //what if not on MainStage?
                    Clause internalClause = currentSprite.getClause();
                    String myOutput = internalClause.getClause();

                    //
                    if (inspectorWindow.isShowing()==false) {
                        inspectorWindow.show();
                    }
                    inspectorTextArea.setText(myOutput);

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
            t.consume();//check

        }
    };

    //BUTTON EVENT HANDLERS

    // Method to move selected sprite to Clause WIP (will not duplicate)
    /*
            The following 'add' actually copies to the second stage.
            By moving the object or referring to it on the new Stage, it forces JavaFX to refresh.

            Java FX does its own cleanup.

            To achieve a 'copy' rather than a move, additional code needed.

     */

    EventHandler<ActionEvent> MoveClausetoWIP = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            //add sprite to Stage for clause WIP.  This will clean up object elsewhere...
            ParentWIPClauseContainer.addClause(currentSprite.getClause()); 
            placeOnMainStage(currentSprite);
        }
    };

    /* TO DO: Turn this into a copy not a move */

    EventHandler<ActionEvent> CopyClausetoWIP = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            
            ParentWIPClauseContainer.addClause(currentSprite.getClause()); 
            placeOnMainStage(currentSprite); 
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
        Stage editorStage = new Stage();

        editGroup_root = Main.this.setupEditorPanel(editorStage, "Editor");
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
            String label = "New Definition"; //TO DO: Sprite Manager to increment #
            String heading = "Default Definition Heading";
            String text = "means...[default text inside definition]";
            String category = "definition";
            Clause myClause = new Clause(label,heading,text,category); 
            //common
            b = new SpriteBox(); //leave default settings to the 'setClause' method in SpriteBox
            b.setClause(myClause);
            ParentWIPClauseContainer.addClause(b.getClause()); //add clause from sprite to clauses container
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
            String label = "New Clause";
            String text = "Default text inside Clause";
            String heading = "Default Clause Heading";
            String category = "clause";
            Clause myClause = new Clause(label,heading,text,category); 
            //b = new SpriteBox(label, "blue"); //default clause colour is blue
            //everthing after here is common to new clauses and definitions
            b = new SpriteBox(myClause); //leave default settings to the 'setClause' method in SpriteBox
            //b.setClause(myClause);
            ParentWIPClauseContainer.addClause(myClause); //add clause in Sprite to Clauses container
            //event handler
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
             //inspectorTextArea.setText("This is where list of clauses will appear");
             ParentWIPClauseContainer.doPrintIteration();
             String output=ParentWIPClauseContainer.getClauseAndText();
             inspectorTextArea.setText(output);
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

    public void displaySpritesInNewStage(ClauseContainer inputContainer, String myTitle) {
        ClauseContainer myContainer = inputContainer;
        Stage adHoc = new Stage();
        defGroup_root = Main.this.setupBlocksWindow(adHoc, myTitle);
        
        adHoc.setY(600);

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
        }    
     
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
        //
        //update word counts
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
        //update word counts
        EventHandler<ActionEvent> UpdateEditor = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //make a new stage with scrollpane
            
            //editClause = focusSprite.getClause();
            editClause.setClauselabel(labelEdit.getText());
            editClause.setHeading(headingEdit.getText());
            editClause.setClausetext(textEdit.getText());
            editClause.setCategory(categoryEdit.getText());
            //update the SpriteBox on the GUI
            SpriteBox focusSprite = mySpriteManager.getCurrentSprite();
            focusSprite.setClause(editClause);
            System.out.println("Clause updated!");
            }
        };
        
}