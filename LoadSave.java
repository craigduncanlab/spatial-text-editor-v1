import java.net.*;
import java.io.*;

//import utilities needed for Arrays lists etc
import java.util.*; //scanner etc
//JavaFX
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
//
import javafx.scene.shape.Rectangle;
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
import javafx.scene.control.Labeled;
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
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//Paint
import javafx.scene.paint.Color;
//Menus
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

//Class to create a load/save dialogue box (Stage) for use in application

public class LoadSave {

//JavaFX instance variables
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;
TextArea inputTextArea = new TextArea();
//target Stage information
StageManager targetSM = new StageManager();
//current dialogue
Stage myStage;
ClauseContainer targetNode = new ClauseContainer();

//contructor
public LoadSave () {

}

private HBox SaveButtonSetup() {
	Button btnSave = new Button();
	Button btnCancel = new Button();
	Button btnOpen = new Button();
	//
    btnSave.setText("Save");
    btnSave.setTooltip(new Tooltip ("Save highlighted as template file"));
    btnSave.setOnAction(clickSave);
    //
    btnCancel.setText("Cancel");
    btnCancel.setTooltip(new Tooltip ("Cancel file"));
    btnCancel.setOnAction(clickCancel);
    //
	HBox hboxButton = new HBox(0,btnSave,btnCancel);
	return hboxButton;
}

private HBox LoadButtonSetup() {
	Button btnSave = new Button();
	Button btnCancel = new Button();
	Button btnOpen = new Button();
	//
	btnOpen.setText("Open");
    btnOpen.setTooltip(new Tooltip ("Open file"));
    btnOpen.setOnAction(clickOpen);
    //
    btnCancel.setText("Cancel");
    btnCancel.setTooltip(new Tooltip ("Cancel file"));
    btnCancel.setOnAction(clickCancel);
    //
	HBox hboxButton = new HBox(0,btnOpen,btnCancel);
	return hboxButton;
}

private VBox vertSetup(HBox myhbox) {
	inputTextArea.setPrefRowCount(1);
	VBox myvbox = new VBox(0,inputTextArea,myhbox);
	return myvbox;
}

public void makeSave(StageManager targetSM, ClauseContainer myNode) {
	this.targetSM = targetSM; //store for later
	this.targetNode = myNode; //store for later
	//make this dialogue
	makeDialogue("Save Template",0);
}

public void saveName(ClauseContainer myNode) {
  TemplateUtil myUtil = new TemplateUtil();
  String filename = myNode.getDocName();
  myUtil.saveTemplate(myNode,filename);
  System.out.println("Save template completed");
}

public void makeLoad(StageManager targetSM) {
	this.targetSM = targetSM; //store for later
	//this.targetNode = null; //store for later
	//make this dialogue
	makeDialogue("Load Template",1);
}

//create dialogue box and display

private void makeDialogue(String title, int option) {
	int winWidth=200;
	int winHeight=100;
	double x = 600;
	double y = 50;
	this.myStage = new Stage();
	HBox myHBox = new HBox();
	if (option==0) {
		myHBox=SaveButtonSetup();
	}
	if (option==1) {
		myHBox=LoadButtonSetup();
	}
	VBox vertFrame=vertSetup(myHBox);
	Pane largePane = new Pane();
    largePane.setPrefSize(winWidth, winHeight);
    largePane.getChildren().add(vertFrame); 
    Scene tempScene = new Scene (largePane,winWidth,winHeight+100); //default width x height (px)
    this.myStage.setScene(tempScene);
    this.myStage.setX(x);
   	this.myStage.setY(y);
   	this.myStage.setTitle(title);
   	this.myStage.initOwner(this.targetSM.getStage());//set parent to workstage Stage
   	this.myStage.show();
   	//return myStage;
}

public void Close() {
	this.myStage.close();
}

EventHandler<ActionEvent> clickOpen = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            TemplateUtil myUtil = new TemplateUtil();
            String filename=inputTextArea.getText();
            //ClauseContainer newNode = myUtil.getTemplate(filename); 
            ClauseContainer newNode = myUtil.getStructuredData(filename); 
            if (newNode!=null) {
                LoadSave.this.targetSM.OpenNewNodeNow(newNode,LoadSave.this.targetSM);
                LoadSave.this.Close();
            }
          }
      };

EventHandler<ActionEvent> clickSave = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
	        //
	        StageManager myStage=LoadSave.this.targetSM.getCurrentFocus();
	        ClauseContainer thisNode = LoadSave.this.targetNode;
	        String filename=inputTextArea.getText();
	        TemplateUtil myUtil = new TemplateUtil();
	        myUtil.saveTemplate(thisNode,filename);
	        System.out.println("Save template completed");
	        LoadSave.this.Close();
          }
      };
EventHandler<ActionEvent> clickCancel = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            LoadSave.this.myStage.close(); //closes this object 
          }
      };

}