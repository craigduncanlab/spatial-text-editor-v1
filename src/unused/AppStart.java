import javafx.application.Application;
/* 
Created 20.11.2017

For launching from another application:
https://stackoverflow.com/questions/25873769/launch-javafx-application-from-another-class 
*/
//No need to extend application here or any overrides

public class AppStart {


//empty constructor
public AppStart() {
	
}

/*

main method .  If I can pass data to Application, then Application should be able to read those parameters
*/

public static void main(String args[]) {
	Application.launch(MainStage.class, args);
	}
}