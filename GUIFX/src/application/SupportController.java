package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SupportController {
	
	private DataOperator dop = null;
	private Main main;
	protected String ID = "";
	private String[] userData = null;
	String homePath = "Finaq/";
	protected Stage stage = null;
	
	//Object Initializations:
	
	//Panes
	@FXML
	protected Pane signinP;
	@FXML
	protected Pane profileP;
	@FXML
	protected Pane loginP;
	@FXML
	protected Pane addIncomeP;
	@FXML
	protected Pane addExpenseP;
	@FXML
	protected Pane addSavingP;
	@FXML
	protected Pane addGoalP;
	@FXML
	protected Pane simulateP;
	
	//Profile
	@FXML
	protected PasswordField oldPasswordPF;
	@FXML
	protected PasswordField newPasswordPF;
	@FXML
	protected Label resultPasswordLPr;
	@FXML
	protected TextField nameTPr;
	@FXML
	protected TextField lastNameTPr;
	@FXML
	protected TextField emailTPr;
	@FXML
	protected Label resultDataLPr;
	@FXML
	protected Label nameLPr;
	
	//Sign In
	@FXML
	protected TextField nameTSg;
	@FXML
	protected TextField lastNameTSg;
	@FXML
	protected TextField emailTSg;
	@FXML
	protected PasswordField passwordTSg;
	@FXML
	protected Label resultLSg;
	@FXML
	protected RadioButton usdR;
	@FXML
	protected RadioButton eurR;
	@FXML
	protected RadioButton gbpR;
	
	//Log In
	@FXML
	protected TextField emailTLg;
	@FXML
	protected PasswordField passwordTLg;
	@FXML
	protected Label resultLLg;
	
	//Add Incomes
	@FXML
	protected TextField amountTIn;
	@FXML
	protected TextField descriptionTIn;
	@FXML 
	protected Label resultLIn;
	@FXML
	protected CheckBox monthlyIncomeCIn;
	
	//Add Expenses
	@FXML
	protected TextField amountTEx;
	@FXML
	protected TextField descriptionTEx;
	@FXML
	protected ToolBar expenseTypesTBEx;
	@FXML
	protected ToolBar reasonStatusTBEx;
	@FXML
	protected RadioButton debitREx;
	@FXML
	protected RadioButton creditREx;
	@FXML
	protected RadioButton paidREx;
	@FXML
	protected RadioButton progressREx;
	@FXML
	protected RadioButton needREx;
	@FXML
	protected RadioButton wantREx;
	@FXML
	protected DatePicker dateEx;
	@FXML
	protected CheckBox debtPayCEx;
	@FXML
	protected RadioButton savingsREx;
	@FXML 
	protected Label resultLEx;
	
	//Add Savings
	@FXML 
	protected Label resultMLSv;
	@FXML 
	protected Label resultTLSv;
	@FXML
	protected TextField amountMTSv;
	@FXML
	protected TextField amountTTSv;
	@FXML
	protected TextField goalTSv;
	@FXML
	protected ToolBar categoriesTTSv;
	@FXML
	protected ToolBar pickTSv;
	
	//Add Goal
	@FXML
	protected TextField budgetTGo;
	@FXML
	protected ToolBar categoriesTGo;
	@FXML 
	protected Label resultLGo;
	
	//Simulate
	@FXML 
	protected TextField incomeTSi;
	@FXML 
	protected TextField expensesTSi;
	@FXML 
	protected TextField budgetTSi;
	@FXML 
	protected TextField debtTSi;
	@FXML 
	protected TextField savingsTSi;
	@FXML 
	protected Label resultLSi;
	
	public SupportController() {
		this.dop = main.dop;
		this.stage = main.stage;
	}
	
	//Select a pane that is being called, for others: setVisible = false;
	//Types: "profile", "signin", "login", "addIncome", "addExpense", "addSaving", "addGoal", "simulate"
	protected void setPane(String type) {
		Pane[] panes = new Pane[] {profileP, signinP, loginP, addIncomeP, addExpenseP, addSavingP, addGoalP, simulateP};
		
		for(Pane pane: panes) {
			pane.setVisible(false);
		}
		
		userData = dop.getOutputFromIdentifier("ID", ID, "users");
		if(userData != null) {
			setInitialValues();
		}
		
		switch(type) {
			case "profile":
				profileP.setVisible(true);
				break;
			case "signin":
				signinP.setVisible(true);
				break;
			case "login":
				loginP.setVisible(true);
				break;
			case "addIncome":
				addIncomeP.setVisible(true);
				break;
			case "addExpense":
				addExpenseP.setVisible(true);
				break;
			case "addSaving":
				addSavingP.setVisible(true);
				break;
			case "addGoal":
				addGoalP.setVisible(true);
				break;
			case "simulate":
				simulateP.setVisible(true);
				break;
		}
		
	}
	
	//Fills the Savings Tool Bar with Saving Goal Radio Buttons that are not already fulfilled (target > saved)
	private void fillSavingsToolBar(ArrayList<String[]> savingsData) {
		ToggleGroup tg = new ToggleGroup();
		for(String[] row: savingsData) {
			
			if(Double.parseDouble(row[3]) < Double.parseDouble(row[4])) {
				
				RadioButton rb = new RadioButton(row[1]);
				
				if(pickTSv.getItems().size() == 0) {
					rb.setSelected(true);
				}
				
				rb.setToggleGroup(tg);
				pickTSv.getItems().add(rb);
			}
			
		}
	}
	
	//returns a boolean that declares if: "Total Savings" - "sum of all the money in the unfinished goals" is >= "amountD"
	//unfinished = savings < target
	private boolean moneyAvailableInSavings(double amountD) {
		
		String totalSavings = dop.getOutputFromIdentifier("ID", ID, "users info")[3];
		
		ArrayList<String[]> data = dop.getAllOutputs(ID+"-savings");
		
		double sum = 0;
		for(String[] row: data) {
			if(Double.parseDouble(row[3]) < Double.parseDouble(row[4])) {
				sum += Double.parseDouble(row[3]);
			}
		}
		
		return ((Double.parseDouble(totalSavings) - sum) >= amountD);
		
	}
	
	//calculates the "Used" column for every Goal in the Budgeting Table
	protected void fillUsedBudget(ArrayList<ArrayList<String[]>> finData, Controller cont, String category){
		ArrayList<String[]> budgets = finData.get(0);

		if(budgets.size() == 0) {
			return;
		}
		
		double sum = cont.getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 6, category, null);
		dop.alterValueInRow(ID+"-budgets", "Category", category, "Used", Double.toString(sum));
		
	}
	
	//Assigning initial value
	private void setInitialValues() {
		nameTPr.setPromptText(userData[1]);
		lastNameTPr.setPromptText(userData[2]);
		emailTPr.setPromptText(userData[3]);
		nameLPr.setText(userData[1]+" "+userData[2]);

		ArrayList<String[]> savingsData = dop.getAllOutputs(ID+"-savings");
		fillSavingsToolBar(savingsData);
	}
	
	//Returns a String based which one of the currency radio buttons was pressed
	private String getCurrency() {
		if(usdR.isSelected()) {
			return "USD";
		}
		else if(eurR.isSelected()) {
			return "EUR";
		}
		else if(gbpR.isSelected()) {
			return "GBP";
		}
		return null;
	}
	
	//Checks if the String input is not blank / empty / invalid
	// if the boolean isOnlyNumbers is true it also checks if the button is only numbers
	protected boolean inputIsValid(String input, boolean isOnlyNumbers) {
		boolean result = input != null && !input.isBlank() && !input.isEmpty() && !input.contains("\"") && !input.contains("\'")
				&& !input.contains("\n");
		
		if(isOnlyNumbers) {
			 try {
				 double d = Double.parseDouble(input);
			     if(d <= 0) {
			    	 return false;
			     }
			     return true;
			     
			 } catch (NumberFormatException e) {
			     return false;
			 }
		}
		
		return result;
	}
	
	//Checks if the email address is a valid email
    public static boolean isEmailValid(String email) {
    	String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        
        if (email == null) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }
	
	
	//Button Actions:
	
	
	
	//Sign In Buttons:
	@FXML
	public void logInBSg(ActionEvent e) {
		setPane("login");
	}
	
	@FXML
	public void signInBSg(ActionEvent e) {
		if(!dop.exists("users", "email", emailTSg.getText()) 
				&& inputIsValid(nameTSg.getText(), false) 
				&& inputIsValid(lastNameTSg.getText(), false) 
				&& inputIsValid(emailTSg.getText(), false)
				&& inputIsValid(passwordTSg.getText(), false)
				&& isEmailValid(emailTSg.getText())) {
			
			String currency = getCurrency();
			String ID = dop.generatePasscode("ID");
			String token = dop.generatePasscode("token");
			
			//default premium = 1
			String[] input = {ID, nameTSg.getText(), lastNameTSg.getText(), emailTSg.getText(), passwordTSg.getText(), "1", currency, LocalDate.now().toString(), 
					token, LocalDate.now().plusMonths(1).toString()};
			dop.setInput("users", input);
			dop.addTableType(ID, "budgets");
			dop.addTableType(ID, "savings");
			dop.addTableType(ID, "transactions");
			dop.setInput("users info", new String[] {ID, "0", "0", "0", "0", LocalDate.now().toString()});
			dop.setInput(ID+"-budgets", new String[] {LocalDate.now().toString(), "Housing", "0.0", "0.0"});
			dop.setInput(ID+"-savings", new String[] {LocalDate.now().toString(), "Something", "Housing", "0.0", "0.0"});
			dop.setInput(ID+"-transactions", new String[] {LocalDate.now().toString(), LocalDate.now().toString(), "0.0", "Something", "Debit", "expense", "Housing", "need", "paid"});
			resultLSg.setText("Signing Successful!");
			resultLSg.setStyle("-fx-text-fill: lightgreen");
		}
		else {
			resultLSg.setText("Invalid input!");
			resultLSg.setStyle("-fx-text-fill: red");
		}
	}
	
	//Profile Buttons:
	
	@FXML
	public void submitDataBPr(ActionEvent e) {
		String name = nameTPr.getText().isBlank() ? nameTPr.getPromptText(): nameTPr.getText();
		String lastName = lastNameTPr.getText().isBlank() ? lastNameTPr.getPromptText(): lastNameTPr.getText();
		String email = emailTPr.getText().isBlank() ? emailTPr.getPromptText(): emailTPr.getText();
		if(inputIsValid(name, false) && inputIsValid(lastName, false) && inputIsValid(email, false) && isEmailValid(email) && !dop.exists("users", "Email", email)){
			dop.alterValueInRow("users", "ID", ID, "First Name", name);
			dop.alterValueInRow("users", "ID", ID, "Last Name", lastName);
			dop.alterValueInRow("users", "ID", ID, "Email", email);
			resultDataLPr.setText("Data Changed Successfully!");
			resultDataLPr.setStyle("-fx-text-fill: lightgreen");
		}
		else {
			resultDataLPr.setText("Data Not Changed!");
			resultDataLPr.setStyle("-fx-text-fill: red");
		}
		
	}
	
	@FXML
	public void submitPasswordBPr(ActionEvent e) {
		String oldPassword = oldPasswordPF.getText();
		String newPassword = newPasswordPF.getText();
		
		if(userData[4].equals(oldPassword) && inputIsValid(newPassword, false)) {
			dop.alterValueInRow("users", "ID", ID, "Password", newPassword);
			resultPasswordLPr.setText("Password Changed Successfully!");
			resultPasswordLPr.setStyle("-fx-text-fill: lightgreen");
		}
		else {
			resultPasswordLPr.setText("Password Not Changed!");
			resultPasswordLPr.setStyle("-fx-text-fill: red");
		}
	}
	
	@FXML
	public void logOutBPr(ActionEvent e) {
		File tk =new File(homePath+"tk.txt");
		if(tk.exists()) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(tk));
				bw.write("");
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}
	
	//Log In Buttons
	@FXML
	public void logInBLg(ActionEvent e) {
		
		if(inputIsValid(emailTLg.getText(), false) && inputIsValid(passwordTLg.getText(), false) && isEmailValid(emailTLg.getText())) {

			String[] data = dop.importProfileWithUserData(emailTLg.getText(), passwordTLg.getText());
			if(data != null) {
	            File tk =new File(homePath+"tk.txt");
	    		if(tk.exists()) {
	    			try {
	    				BufferedWriter bw = new BufferedWriter(new FileWriter(tk));
	    				bw.write(data[8]);
	    				bw.close();
	    			} catch (IOException e1) {
	    				e1.printStackTrace();
	    			}
	    		}
	            try {
	            	Main app = new Main();
	                app.loadMainScreen(Main.stage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			else {
				resultLLg.setText("User Not Found!");
				resultLLg.setStyle("-fx-text-fill: red");
			}
			
		}
		
		else {
			resultLLg.setText("Invalid User Data!");
			resultLLg.setStyle("-fx-text-fill: red");
		}
	}
	
	@FXML
	public void signInBLg(ActionEvent e) {
		setPane("signin");
	}
	
	//Add Incomes Buttons:
	@FXML
	public void submitBIn(ActionEvent e) {
		String amount = amountTIn.getText();
		String description = descriptionTIn.getText();
		
		if(inputIsValid(amount, true) && inputIsValid(description, false)) {
			String[] input = {LocalDate.now().toString(), LocalDate.now().toString(), amount, description, "", "income", "", "", ""};
			dop.setInput(ID+"-transactions", input);
			String[] info = dop.getOutputFromIdentifier("ID", ID, "users info");
			double newAmount = Double.parseDouble(info[1]) + Double.parseDouble(amount);
			dop.alterValueInRow("users info", "ID", ID, "Total Balance", Double.toString(newAmount));
			dop.alterValueInRow("users info", "ID", ID, "Date", LocalDate.now().toString());
			if(monthlyIncomeCIn.isSelected()) {
				dop.alterValueInRow("users info", "ID", ID, "Annual Income", Double.toString(newAmount * 12));
			}
			resultLIn.setText("Done!");
			resultLIn.setStyle("-fx-text-fill: lightgreen");
            try {
            	Main app = new Main();
                app.loadMainScreen(Main.stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else {
			resultLIn.setText("Invalid input!");
			resultLIn.setStyle("-fx-text-fill: red");
		}
	}

	//Add Expenses Buttons:
	@FXML
	public void submitBEx(ActionEvent e) {
		String amount = amountTEx.getText();
		String description = descriptionTEx.getText();
		String paymentType = null;
		boolean credit = creditREx.isSelected();
		String expenseType = "Miscellaneous";
		String reason = needREx.isSelected() ? "need": "want";
		String status = paidREx.isSelected() ? "paid": "progress";
		String[] info = dop.getOutputFromIdentifier("ID", ID, "users info");
		String dateS = LocalDate.now().minusDays(1).toString();
		
		pass:
		if(inputIsValid(amount, true) && inputIsValid(description, false)) {
			System.out.println("SUBMIT CALLED");
			
			if(debitREx.isSelected()) {
				paymentType = "Debit";
			}
			
			else if(creditREx.isSelected()) {
				paymentType = "Credit";
			}
			
			else {
				paymentType = "Saving";
			}
			
			double amountD = Double.parseDouble(amount);
			
			for(Node node: expenseTypesTBEx.getItems()) {
				if(node instanceof RadioButton rb) {
					if(rb.isSelected()) {
						expenseType = rb.getText().toLowerCase();
					}
				}
			}
			
			double result = Double.parseDouble(info[1]) - amountD;
			if(result < 0 && paymentType.equals("Debit")) {
				resultLEx.setText("Not Enough money in your account!");
				resultLEx.setStyle("-fx-text-fill: red");
			}
			if(credit) {
				System.out.println(info[2]+" "+amountD+" "+(Double.parseDouble(info[2]) + amountD));
				dop.alterValueInRow("users info", "ID", ID, "Total Debt", Double.toString(Double.parseDouble(info[2]) + amountD));
			}
			
			else if(debtPayCEx.isSelected()) {
				if(paymentType.equals("Saving")) {
					if(!moneyAvailableInSavings(amountD)) {
						resultLEx.setText("Can't get into debt with the savings!");
						resultLEx.setStyle("-fx-text-fill: red");
						break pass;
					}
				}
				if((Double.parseDouble(info[2]) - amountD) >= 0){
					dop.alterValueInRow("users info", "ID", ID, "Total Debt", Double.toString(Double.parseDouble(info[2]) - amountD));
				}
				else {
					resultLEx.setText("The debt is lower than your amount!");
					resultLEx.setStyle("-fx-text-fill: red");
					break pass;
				}
			}
			
			if(savingsREx.isSelected() && moneyAvailableInSavings(amountD) && !credit) {
				System.out.println(info[3]+" "+amountD+" "+(Double.parseDouble(info[3]) - amountD));
				if((Double.parseDouble(info[3]) - amountD) >= 0) {
				dop.alterValueInRow("users info", "ID", ID, "Total Savings", Double.toString(Double.parseDouble(info[3]) - amountD));
				}
				else {
					resultLEx.setText("Can't get into debt with the savings!");
					resultLEx.setStyle("-fx-text-fill: red");
					break pass;
				}
			}
			else if(savingsREx.isSelected() && !moneyAvailableInSavings(amountD)) {
				resultLEx.setText("Can't get into debt with the savings!");
				resultLEx.setStyle("-fx-text-fill: red");
				break pass;
			}
			
			if(debtPayCEx.isSelected() && credit) {
				resultLEx.setText("Can't pay out debt from debt!");
				resultLEx.setStyle("-fx-text-fill: red");
				break pass;
			}
			
			if(dateEx.getValue() != null) {
				LocalDate date = dateEx.getValue();
				dateS = date.toString();
			}
			
			if(!credit && !savingsREx.isSelected()) {
				dop.alterValueInRow("users info", "ID", ID, "Total Balance", Double.toString(result));
			}
			
			dop.alterValueInRow("users info", "ID", ID, "Date", LocalDate.now().toString());
			
			//If they pay their debt it is not increasing their expenses thus 0
			//Expenses have been accounted for while taking the loan
			if(debtPayCEx.isSelected()) {
				String[] input = {LocalDate.now().toString(), dateS, "0", description, paymentType, "expense", expenseType.toLowerCase(), reason, status};
				dop.setInput(ID+"-transactions", input);
			}
			
			else {
				String[] input = {LocalDate.now().toString(), dateS, amount, description, paymentType, "expense", expenseType.toLowerCase(), reason, status};
				dop.setInput(ID+"-transactions", input);
				
				//Add Money to the budget coategory
				String[] budgetInfo = dop.getOutputFromIdentifier("Category", expenseType.toLowerCase(), ID+"-budgets");
				if(budgetInfo != null) {
					dop.alterValueInRow(ID+"-budgets", "Category", expenseType.toLowerCase(), "Used", Double.toString(Double.parseDouble(budgetInfo[3]) + amountD));
				}
			}
			
			
            try {
            	Main app = new Main();
                app.loadMainScreen(Main.stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
			resultLEx.setText("Done!");
			resultLEx.setStyle("-fx-text-fill: lightgreen");
			
		}
		
		else {
			resultLEx.setText("Invalid input!");
			resultLEx.setStyle("-fx-text-fill: red");
		}
	}
	
	//Add Savings Buttons:
	@FXML
	public void submitMoneySv(ActionEvent e) {
		String amount = amountMTSv.getText();
		double amountD = Double.parseDouble(amount);
		String[] info = dop.getOutputFromIdentifier("ID", ID, "users info");
		double currentAmount = Double.parseDouble(info[1]);
		double savingsAmount = Double.parseDouble(info[3]);
		double balanceResult = currentAmount - amountD;
		double savingsResult = savingsAmount + amountD;
		String goal = "";
		
		pass:
		if(inputIsValid(amount, true)) {

			for(Node node: pickTSv.getItems()) {
				if(node instanceof RadioButton rb) {
					if(rb.isSelected()) {
						goal = rb.getText();
					}
				}
			}

			String[] savedData = dop.getOutputFromIdentifier("Goal", goal, ID+"-savings");
			double previousSaved = Double.parseDouble(savedData[3]);
			double previousTarget = Double.parseDouble(savedData[4]);
			
			if(previousSaved + amountD <= previousTarget) {
				dop.alterValueInRow("users info", "ID", ID, "Total Balance", Double.toString(balanceResult));
				dop.alterValueInRow("users info", "ID", ID, "Total Savings", Double.toString(savingsResult));
				dop.alterValueInRow(ID+"-savings", "Goal", goal, "Saved", Double.toString(previousSaved + amountD));
				
				try {
	            	Main app = new Main();
	                app.loadMainScreen(Main.stage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	            
				resultMLSv.setText("Done!");
				resultMLSv.setStyle("-fx-text-fill: lightgreen");
			}
			
			else {
				resultMLSv.setText("Amount surpasing the Saving Goal!");
				resultMLSv.setStyle("-fx-text-fill: red");
				break pass;
			}
			
		}
		else {
			resultMLSv.setText("Invalid input!");
			resultMLSv.setStyle("-fx-text-fill: red");
		}
		
	}
	@FXML
	public void submitTargetSv(ActionEvent e) {
		
		String amount = amountTTSv.getText();
		String goal = goalTSv.getText();
		String category = "Miscellaneous";
		
		pass:
		if(inputIsValid(amount, true) && inputIsValid(goal, false)) {
			if(!dop.exists(ID+"-savings", "Goal", goal)) {
				
				for(Node node: categoriesTTSv.getItems()) {
					if(node instanceof RadioButton rb) {
						if(rb.isSelected()) {
							category = rb.getText().toLowerCase();
						}
					}
				}
				
				String[] input = {LocalDate.now().toString(), goal, category, "0.0", amount};
				dop.setInput(ID+"-savings", input);
				
				try {
	            	Main app = new Main();
	                app.loadMainScreen(Main.stage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	            
				resultTLSv.setText("Done!");
				resultTLSv.setStyle("-fx-text-fill: lightgreen");
				
			}
			
			else {
				resultTLSv.setText("A goal like this already exists!");
				resultTLSv.setStyle("-fx-text-fill: red");
				break pass;
			}
			
		}
		
		else {
			resultMLSv.setText("Invalid input!");
			resultMLSv.setStyle("-fx-text-fill: red");
		}
		
	}
	
	//Add Goal Buttons:
	@FXML
	public void submitBGo(ActionEvent e) {
		String amount = budgetTGo.getText();
		String category = "Miscellaneous";
		Controller cont = new Controller();
		cont.ID = this.ID;
		cont.dop = this.dop;
		ArrayList<ArrayList<String[]>> finData = cont.getFinData();
		
		pass:
		if(inputIsValid(amount, true)) {
			
			for(Node node: categoriesTGo.getItems()) {
				if(node instanceof RadioButton rb) {
					if(rb.isSelected()) {
						category = rb.getText().toLowerCase();
					}
				}
			}
			
			//Find out if the budget for this category has already not been set this month
			boolean alreadySet = false;
			for(String[] row: finData.get(0)) {
				LocalDate rowDate = LocalDate.parse(row[0]);
				
				if(rowDate.getMonth().equals(LocalDate.now().getMonth()) && rowDate.getYear() == LocalDate.now().getYear()) {
					if(row[1].equals(category)) {
						alreadySet = true;
					}
				}
				
			}
			
			if(!alreadySet) {
				String[] input = {LocalDate.now().toString(), category, amount, "0.0"};
				dop.setInput(ID+"-budgets", input);
				
				fillUsedBudget(finData, cont, category);
				
				try {
	            	Main app = new Main();
	                app.loadMainScreen(Main.stage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				resultLGo.setText("Done!");
				resultLGo.setStyle("-fx-text-fill: lightgreen");
			}
			
			else {
				dop.alterValueInRow(ID+"-budgets", "Category", category.toLowerCase(), "Budget", Double.toString(Double.parseDouble(amount)));
			}
			
		}
		
		else {
			resultLGo.setText("Invalid input!");
			resultLGo.setStyle("-fx-text-fill: red");
		}
	}
	
	//Simulate Buttons:
	@FXML
	private void submitBSi(ActionEvent e) {
		
		if(inputIsValid(incomeTSi.getText(), true) && inputIsValid(expensesTSi.getText(), true) && inputIsValid(budgetTSi.getText(), true) && inputIsValid(debtTSi.getText(), true) && inputIsValid(savingsTSi.getText(), true)) {
			double income = Double.parseDouble(incomeTSi.getText());
			double expenses = Double.parseDouble(expensesTSi.getText());
			double budget = Double.parseDouble(budgetTSi.getText());
			double debt = Double.parseDouble(debtTSi.getText());
			double savings = Double.parseDouble(savingsTSi.getText());
			AIServerConnector asc = new AIServerConnector(income, expenses, (savings / income), budget, (debt / income));
			resultLSi.setText("Estimated Score: "+(Math.max(Math.min(asc.result, 850), 300)));
			resultLSi.setStyle("-fx-text-fill: white");
		}
		
		else {
			resultLSi.setText("Invalid input!");
			resultLSi.setStyle("-fx-text-fill: red");
		}
	}
	
}
