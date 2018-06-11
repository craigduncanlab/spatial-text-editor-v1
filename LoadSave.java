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

//File IO locations
String searchfolder = "../templates/";
//target Stage information
StageManager targetSM = new StageManager();

//contructor
public LoadSave () {

}

private HBox buttonSetup() {
	Button btnSave = new Button();
	Button btnCancel = new Button();
	Button btnOpen = new Button();
	//
	btnOpen.setText("Open");
    btnOpen.setTooltip(new Tooltip ("Open file"));
    btnOpen.setOnAction(clickOpen);
    //
    btnSave.setText("Save");
    btnSave.setTooltip(new Tooltip ("Save file"));
    btnSave.setOnAction(clickSave);
    //
    btnCancel.setText("Cancel");
    btnCancel.setTooltip(new Tooltip ("Cancel file"));
    btnCancel.setOnAction(clickCancel);
    //
	HBox hboxButton = new HBox(0,btnOpen,btnSave,btnCancel);
	return hboxButton;
}

private VBox vertSetup(HBox myhbox) {
	inputTextArea.setPrefRowCount(1);
	VBox myvbox = new VBox(0,inputTextArea,myhbox);
	return myvbox;
}


//return a graph node holding the current dictionary as a graph of subnodes
private ClauseContainer getTemplate(String filename) {
	String filepath=this.searchfolder+filename;
	return readTemplate(filepath);
}

private ClauseContainer readTemplate(String fileref) {
	String boxlabel = "Template";
	NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
	ClauseContainer templateNode = new ClauseContainer(NC_templ);
	templateNode.setDocName(boxlabel);
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No text/html content");
			return null;
		}
		int nl=0;
		while (scanner1.hasNextLine()) {
			nl++;
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first word in row
			String hdword = scanner2.next();
			ClauseContainer wordNode = new ClauseContainer(NC_templ,templateNode,hdword,hdword);
			templateNode.addChildNode(wordNode);
			//create child nodes for rest of words in row
			while (scanner2.hasNext()) {
				String rowword = scanner2.next();
				wordNode.addChildNode(new ClauseContainer(NC_templ,wordNode,rowword,rowword));
			}
		scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return null;
	}
	return templateNode;
}

public void makeLoadSave(StageManager targetSM) {
	this.targetSM= targetSM; //store for later
	//make this dialogue
	makeDialogue();
}

//create dialogue box and display

private void makeDialogue() {
	int winWidth=300;
	int winHeight=200;
	double x = 500;
	double y = 500;
	Stage myStage = new Stage();
	VBox vertFrame=vertSetup(buttonSetup());
	Pane largePane = new Pane();
    largePane.setPrefSize(winWidth, winHeight);
    largePane.getChildren().add(vertFrame); 
    Scene tempScene = new Scene (largePane,winWidth+100,winHeight+100); //default width x height (px)
    myStage.setScene(tempScene);
    myStage.setX(x);
   	myStage.setY(y);
   	myStage.show();
   	//return myStage;
}

EventHandler<ActionEvent> clickOpen = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            String filename=inputTextArea.getText()+".pdt";
            ClauseContainer newNode = getTemplate(filename); 
            if (newNode!=null) {
                LoadSave.this.targetSM.OpenNewNodeNow(newNode,LoadSave.this.targetSM);
            }
          }
      };

EventHandler<ActionEvent> clickSave = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            String filename=inputTextArea.getText()+".pdt";
            //TO DO
            return;
          }
      };
EventHandler<ActionEvent> clickCancel = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //TO DO: close window
            return;
          }
      };

}