/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

//Screen
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

public class StageManager {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();

//constructor
public StageManager() {

}

    /* A default screen position 

    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
            double mySetX = ScreenBounds.getWidth() / 1.8;
            myStage.setX(mySetX);
            myStage.setY(450);

    */

    /*Editor panel
    //Layout
    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
    double mySetX = ScreenBounds.getWidth() / 1.1;
    //myStage.setX(500);
    myStage.setX(mySetX);
    myStage.setY(150);
    */

    /*Toolbar panel
    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        double mySetX = ScreenBounds.getWidth() / 1.1;
        //myStage.setX(500);
        myStage.setX(mySetX);
        myStage.setY(50);

    */

    /*inspector panel 
    //Layout
        Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        //inspector
        double mySetX = ScreenBounds.getWidth() / 1.8;
        mySetX = ScreenBounds.getWidth() / 1.5; 
        myStage.setX(mySetX); 
        myStage.setScene(defScene);

        */

//getters and setters
public void setCurrentXY(double x, double y) {

	this.latestX=x;
    this.latestY=y;
}

/* The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
*/

public void setPosition(Stage Parent, Stage myStage, String myCategory) {

    switch(myCategory){

            case "WIP":
                myStage.setX(0);
                myStage.setY(0);
                myStage.toBack();
                break;

            case "editor":
                myStage.initOwner(Parent);  //this must be called before '.show()' on child
                myStage.setX(650); //TO DO: Relative
                myStage.setY(0);
                myStage.toFront();
                break;

            case "toolbar":
                myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(0);
                myStage.toFront();
                break;

            case "filewindow":
                myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(500);
                myStage.toFront();
                break;
            
            case "scrollpanel":
                myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(550);
                myStage.toFront();
                break;
            
            case "icons":
                myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(550);
                myStage.toFront();
                break;
                   
            default:
                myStage.initOwner(Parent);
                myStage.setX(200);
                myStage.setY(200);
                myStage.toFront();
                break;
    }
}

public void incrementXY() {

    this.latestX=this.latestX+50;
    this.latestY=this.latestY+50;
}

public void resetXY() {

    this.latestX=50;
    this.latestY=50;
}

public double getX() {
    return this.latestX;
}

public double getY() {
    return this.latestY;
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}


public double getBigY() {
    return this.myBigY;
}

}