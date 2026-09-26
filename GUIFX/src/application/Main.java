package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	protected Parent root;
	protected static DataOperator dop;
	protected String homePath = "Finaq/";
	protected static Stage stage = null;
	protected Controller controller = null;

	public static void main(String[] args) {
		dop = new DataOperator("root", "Password12345");
		/*dop.deleteRow("users", "ID", "p7tg446x7", Types.VARCHAR);
		dop.deleteRow("users info", "ID", "p7tg446x7", Types.VARCHAR);*/
		launch(args);
	}
	
	public Main() {

	}
	
	//Loads Token from a file. If the file doesn't exist, it creates a new file. 
	//Makes sure the Token is not expired. Removes "Password" and "Token" from the userData.
	//If the token doesn't exist or it is expired it returns NULL
	protected String[] readToken() {
		File tk =new File(homePath+"tk.txt");
		String token = "";
		if(!tk.exists()) {
			try {
				tk.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Scanner br =new Scanner(tk);
			if(br.hasNextLine()) {
				token = br.nextLine();
			}
			else {
				br.close();
				return null;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(token == null || token.isBlank() || token.isEmpty()) {
			return null;
		}
		String[] userData = dop.getOutputFromIdentifier("Token", token, "users");
		if(userData == null) {
			return null;
		}
		LocalDate exLD = LocalDate.parse(userData[9]);
		if(LocalDate.now().isAfter(exLD)) {
			return null;
		}
		return userData;
	}
	
	protected DataOperator getDOP() {
		return dop;
	}
	
	//Adds essential icons to the labels upon starting
	private void addIcons(Scene scene){
		ImageView dashboardM = (ImageView) scene.lookup("#dashboardMI");
		ImageView incomeM = (ImageView) scene.lookup("#incomeMI");
		ImageView expensesM = (ImageView) scene.lookup("#expensesMI");
		ImageView savingsM = (ImageView) scene.lookup("#savingsMI");
		ImageView budgetingM = (ImageView) scene.lookup("#budgetingMI");
		ImageView advisorM = (ImageView) scene.lookup("#advisorMI");
		ImageView incomeI = (ImageView) scene.lookup("#addIncomeI");
		ImageView expensesI = (ImageView) scene.lookup("#addExpensesI");
		ImageView savingsI = (ImageView) scene.lookup("#addSavingsI");
		ImageView budgetingI = (ImageView) scene.lookup("#addGoalI");
		
		dashboardM.setImage(new Image(new File(homePath+"home_i.png").toURI().toString()));
		incomeM.setImage(new Image(new File(homePath+"income_i.png").toURI().toString()));
		expensesM.setImage(new Image(new File(homePath+"expenses_i.png").toURI().toString()));
		savingsM.setImage(new Image(new File(homePath+"savings_i.png").toURI().toString()));
		budgetingM.setImage(new Image(new File(homePath+"budget_i.png").toURI().toString()));
		advisorM.setImage(new Image(new File(homePath+"advisor_i.png").toURI().toString()));
		incomeI.setImage(new Image(new File(homePath+"add_i.png").toURI().toString()));
		expensesI.setImage(new Image(new File(homePath+"add_i.png").toURI().toString()));
		savingsI.setImage(new Image(new File(homePath+"add_i.png").toURI().toString()));
		budgetingI.setImage(new Image(new File(homePath+"add_i.png").toURI().toString()));
		
	}
	
	//Load the Main Screen if the token is valid
	protected void loadMainScreen(Stage mStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		root = loader.load();
		Scene scene = new Scene(root, Color.web("#F5F7FA"));
		Image logo = new Image("LOGO.png");
		Controller controller = loader.getController();
		this.controller = controller;
		controller.dop = dop;
		controller.setRoot(root);
		Pane[] panes = controller.panes;
		Pane dashboardP = controller.dashboardP;
		
		dashboardP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(dashboardP)) {pane.setVisible(false);}}
		
		mStage.setY(0);
		mStage.setX(0);
		mStage.getIcons().add(logo);
		mStage.setWidth(1000);
		mStage.setHeight(600);
		mStage.setResizable(false);
		mStage.setTitle("Finaq");
		mStage.setIconified(false);
		mStage.setScene(scene);
		mStage.show();
		mStage.setOnCloseRequest(event -> System.exit(0));
		addIcons(scene);
	}
	//Load the Main Screen if the token is valid
	protected void loadSupportScreen(Stage mStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Support.fxml"));
		root = loader.load();
		Scene scene = new Scene(root, Color.web("#F5F7FA"));
		Image logo = new Image("LOGO.png");
		SupportController supportController = loader.getController();
		supportController.setPane("login");
		mStage.setY(0);
		mStage.setX(0);
		mStage.getIcons().add(logo);
		mStage.setWidth(416);
		mStage.setHeight(540);
		mStage.setResizable(false);
		mStage.setTitle("Finaq");
		mStage.setIconified(false);
		mStage.setScene(scene);
		mStage.show();
	}

	//Set up a GUI Controller and make initial manual changes of the GUI
	@Override
	public void start(Stage mStage) throws Exception {
		Main.stage = mStage;
		if(readToken() != null) {
			loadMainScreen(mStage);
		}
		else {
			loadSupportScreen(mStage);
		}
	}

}