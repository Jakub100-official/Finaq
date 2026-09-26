package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SupportGUI{
	Parent root;
	static DataOperator dop;
	private String type = "";
	String homePath = "Finaq/";
	private String ID = "";
	
	//Types: "profile", "signin", "login", "addIncome", "addExpense", "addSaving", "addGoal", "simulate"
	protected SupportGUI(String type, String ID) {
		this.ID = ID;
		this.type = type;
		try {
			Start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Assign and Start the GUI
	//Assign initial values to components
	public void Start() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Support.fxml"));
		root = loader.load();
		Scene scene = new Scene(root, Color.web("#F5F7FA"));
		Image logo = new Image("LOGO.png");
		SupportController controller = loader.getController();
		if(type.equals("")) {
			type.equals("login");
		}
		controller.ID = ID;
		controller.setPane(type);
		Stage supportStage =  new Stage();
		controller.stage = supportStage;
		supportStage.getIcons().add(logo);
		supportStage.setResizable(false);
		supportStage.setTitle("Finaq");
		supportStage.setIconified(false);
		supportStage.setScene(scene);
		supportStage.show();

	}
	
}
