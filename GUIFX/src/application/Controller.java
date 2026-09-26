package application;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.Scanner;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class Controller {
	Parent root;
	Pane dashboardP;
	Pane incomeP;
	Pane expensesP;
	Pane savingsP;
	Pane budgetingP;
	Pane advisorP;
	Pane settingsP;
	Pane[] panes;
	String homePath = "Finaq/";
	protected DataOperator dop;
	protected SupportGUI supportGUI;
	protected String ID;
	private ArrayList<String[]> userData = null;
	private ArrayList<ArrayList<String[]>> finData = null;
	
	//FXML Objects Initialization
	@FXML 
	private VBox recentTransVB;
	@FXML 
	private VBox upcommingBillsVB;
	@FXML
	private LineChart<String, Number> incomeC;
	@FXML
	private LineChart<String, Number> expensesC;
	@FXML
	private BarChart<String, Number> savingsC;
	@FXML
	private PieChart budgetingC;
	@FXML
	private Label synchL;
	@FXML
	private Label nameL;
	
	//Income Sources Table Initialization
	@FXML
	private TableView<String[]> incomeSourcesT;
	@FXML
	private TableColumn<String[], String> sourceCol;
	@FXML
	private TableColumn<String[], String> amountCol;
	
	//Expenses Table Initialization
	@FXML
	private TableView<String[]> expensesT;
	@FXML
	private TableColumn<String[], String> dateET;
	@FXML
	private TableColumn<String[], String> categoryET;
	@FXML
	private TableColumn<String[], String> descriptionET;
	@FXML
	private TableColumn<String[], String> paymentET;
	@FXML
	private TableColumn<String[], String> amountET;
	@FXML
	private TableColumn<String[], String> statusET;
	
	//Savings Table Initialization
	@FXML
	private TableView<String[]> savingsT;
	@FXML
	private TableColumn<String[], String> goalST;
	@FXML
	private TableColumn<String[], String> categoryST;
	@FXML
	private TableColumn<String[], String> savedST;
	@FXML
	private TableColumn<String[], String> targetST;
	@FXML
	private TableColumn<String[], String> progressST;
	
	//Budgeting Table Initialization
	@FXML
	private TableView<String[]> budgetingT;
	@FXML
	private TableColumn<String[], String> categoryBT;
	@FXML
	private TableColumn<String[], String> budgetBT;
	@FXML
	private TableColumn<String[], String> usedBT;
	@FXML
	private TableColumn<String[], String> remainingBT;
	@FXML
	private TableColumn<String[], String> progressBT;
	
	//DashboardLabels
	@FXML
	private ImageView cashFlow1I;
	@FXML
	private Label cashFlow1L;
	@FXML
	private ImageView totalBalance1I;
	@FXML
	private Label totalSavings1L;
	@FXML
	private Label totalBalance1L;
	@FXML
	private Label totalDebt1L;
	@FXML
	private Label totalYearlyExpenses1L;
	@FXML
	private Label totalSavingsL;
	@FXML
	private Label totalBalanceL;
	@FXML
	private Label totalDebtL;
	@FXML
	private Label totalYearlyExpensesL;
	@FXML
	private Label financialHealthL;
	@FXML
	private ImageView cashFlowI;
	@FXML
	private ImageView totalBalanceI;
	
	
	//Income Labels
	@FXML
	private Label annualIncomeL;
	@FXML
	private Label monthlyIncomeL;
	@FXML
	private Label cashFlowL;
	@FXML
	private Label incomeChangeApproximationL;
	
	//Expenses Labels
	@FXML
	private Label avDayL;
	@FXML
	private Label thisMonthL;
	@FXML
	private Label thisYearL;
	
	//Savings Labels
	@FXML
	private Label totalSavedL;
	@FXML
	private Label savingsRateL;
	@FXML
	private Label projectedSavingsTotalL;
	@FXML
	private Label goalCompletionL;
	
	//Budget Labels
	@FXML
	private Label incomeL;
	@FXML
	private Label usedL;
	@FXML
	private Label budgetHealthL;
	@FXML
	private Label remainingL;
	
	//AI Advisor
	@FXML
	private Label creditScoreL;
	@FXML
	private Label insightL;
	@FXML
	private Label financialChangesL;
	@FXML
	private Label estimatedIncomeL;
	@FXML
	private Label estimatedExpensesL;
	@FXML
	private Label estimatedSavingsL;
	
	
	public Controller() {
	}
	
	private Label setPercentLabelValue(Label label) {
		double value = Double.parseDouble(label.getText());
	    String formattedText = String.format("%,.2f%%", value);
		label.setText(formattedText);
		return label;
	}
	
	//Changes a universal currency short into a sign
	private String getCurrencySign(String currency) {
		String text = "";
		switch (currency){
		 	case "USD":
		 		text = "$";
		 		break;
		 	case "EUR":
		 		text = "€";
		 		break;
		 	case "GBP":
		 		text = "£";
		 		break;
		}
		return text;
	}
	
	//Returns a money value formatted with a currency sign and proper spacing
	private String setMoneyStringValue(String amount, String currency) {
		String text = getCurrencySign(currency);
		double numericAmount = Double.parseDouble(amount);
		text += String.format("%,.2f", numericAmount);
		return text;
	}
	//Returns a percentage value formatted with a percentage sign at the end
	private String setPercentStringValue(String amount) {
		String text = "";
		double numericAmount = Double.parseDouble(amount);
		text += String.format("%,.2f%%", numericAmount);
		
		return text;
	}
	
	//returns a formated label based on the text
	private Label setMoneyLabelValue(Label label, ImageView image, String currency, boolean defaultColor) {
		 double amount = Double.parseDouble(label.getText());
		 if (image != null) {

			    String imagePath = amount < 0 ? homePath+"fall_i.png" : homePath+"rise_i.png";

			    File file = new File(imagePath).getAbsoluteFile();

			    Image img = new Image(file.toURI().toString());

			    image.setImage(img);
			}
		 if(defaultColor) {
			 if (amount<0) {
				 label.setTextFill(Color.RED);
			 }
			 else {
				 label.setTextFill(Color.web("#00ff22"));
			 }
		 }
		 String text = "";
		 
		 text += getCurrencySign(currency);
		 
		 text += String.format("%,.2f", amount);
		 
		 label.setText(text);
		
		 return label;
		
	}
	
	//set values and a default look for all Table Views
	private void setTableViewParameters(TableView<String[]> table, ArrayList<TableColumn<String[], String>> columns, String[][] arrays) {
		table.getItems().clear();
		for (int i=0; i<columns.size(); i++){
			final int fin = i;
			columns.get(i).setStyle("-fx-alignment: CENTER;");
			columns.get(i).setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[fin]));
		}
		for (int i=0; i<arrays.length; i++) {
			table.getItems().add(arrays[i]);
		}
		table.getSelectionModel().setCellSelectionEnabled(true);
		
	}
	//set values and a default look for all Line Charts
	private void setLineChartParameters(LineChart<String, Number> lineC, String[] strs, Number[] nums) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		lineC.getData().clear();
		for (int i=0; i<nums.length; i++){
			series.getData().add(new XYChart.Data<>(strs[i], nums[i]));
		}
		
	    lineC.getData().add(series);
	}
	//set values and a default look for all Bar Charts
	private void setBarChartParameters(BarChart<String, Number> barC, String[] strs, Number[] nums) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		barC.getData().clear();
		for (int i=0; i<nums.length; i++){
			series.getData().add(new XYChart.Data<>(strs[i], nums[i]));
		}
	    barC.getData().add(series);
	}
	//set values and a default look for all Pie Charts
	private void setPieChartParameters(PieChart pieC, String[] strs, double[] nums) {
		pieC.getData().clear();
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
		for (int i=0; i<strs.length; i++) {
			data.add(new PieChart.Data(strs[i], nums[i]));
		}
		pieC.setData(data);
	}
	
	//Set a standard look for a label in a VBox
	private Label createVBLabel(String text) {
		Label label = new Label(text);
		RadialGradient color = new RadialGradient(
				0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
				new Stop(0.0, new Color(0.0942, 0.0024, 0.3263, 1.0)),
				new Stop(1.0, new Color(0.0235, 0.0039, 0.1804, 1.0)));
		BackgroundFill fill = new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY);
		label.setBackground(new Background(fill));
		label.setFont(new Font("Arial", 15));
		label.setTextFill(Color.WHITE);
		label.setPrefSize(323.2, 40);
		label.setAlignment(Pos.CENTER);
		return label;
	}
	
	//Creating tables
	
	private void createIncomeTable(String[][] input) {
		ArrayList<TableColumn<String[], String>> al = new ArrayList <TableColumn<String[], String>>();
		al.add(sourceCol);
		al.add(amountCol);
		setTableViewParameters(incomeSourcesT, al, input);
	}
	
	private void createExpensesTable(String[][] input) {
		ArrayList<TableColumn<String[], String>> exAL = new ArrayList<>();
		exAL.add(dateET);
		exAL.add(categoryET);
		exAL.add(descriptionET);
		exAL.add(paymentET);
		exAL.add(amountET);
		exAL.add(statusET);
		setTableViewParameters(expensesT, exAL, input);
	}
	
	private void createSavingsTable(String[][] input) {
		ArrayList<TableColumn<String[], String>> al = new ArrayList <TableColumn<String[], String>>();
		al.add(goalST);
		al.add(categoryST);
		al.add(savedST);
		al.add(targetST);
		al.add(progressST);
		setTableViewParameters(savingsT, al, input);
	}
	
	private void createBudgetingTable(String[][] input) {
		ArrayList<TableColumn<String[], String>> al = new ArrayList <TableColumn<String[], String>>();
		al.add(categoryBT);
		al.add(budgetBT);
		al.add(usedBT);
		al.add(remainingBT);
		al.add(progressBT);
		setTableViewParameters(budgetingT, al, input);
	}
	
	private static LocalDate dateToLocalDate(Date date) {
		
		return date.toLocalDate();
	}
	
	private Date localDateToDate(LocalDate localDate) {
		
		return Date.valueOf(localDate);
	}
	
	//Outputs array of int marking rows that contain a date difference from today by month or a year
	//if a startD is null -> from current date, else -> from date specified
	private int[] getTimePerRows(String[] coulmn, String period, Date startD) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		LocalDate today = null;
		if(startD == null) {
			today = LocalDate.now();
		}
		else {
			today = dateToLocalDate(startD);
		}
		
		for(int i=0; i<coulmn.length; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			LocalDate target = LocalDate.parse(coulmn[i]);
			
	        boolean isSameMonthAndYear = (target.getMonth() == today.getMonth()) && (target.getYear() == today.getYear());
	        boolean isSameYear = target.getYear() == today.getYear();
	        
	        if(period.equals("month") && isSameMonthAndYear) {
	        	result.add(i);
	        }
	        else if(period.equals("year") && isSameYear) {
	        	result.add(i);
	        }
		}
		return result.stream().mapToInt(Integer::intValue).toArray();
	}
	
	//Manage day difference
	private long daysApart(Date oldDate) {
		LocalDate today = LocalDate.now();
		LocalDate oldDateLocal = dateToLocalDate(oldDate);
		long daysPassed = ChronoUnit.DAYS.between(oldDateLocal, today);
		return daysPassed;
	}
	
	private String[] removeStringFromArray(String[] array, int... nums) {
		List<String> list = new ArrayList<>(Arrays.asList(array));

		for(int i=0; i<nums.length; i++) {
			list.remove(array[nums[i]]);
		}
		array = list.toArray(String[]::new);
		return array;
	}
	
	//Loads Token from a file. If the file doesn't exist, it creates a new file. 
	//Makes sure the Token is not expired. Removes "Password" and "Token" from the userData.
	//If the token doesn't exist or it is expired it returns NULL
	private String[] readToken() {
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
		userData = removeStringFromArray(userData, 4, 8);
		return userData;
	}

	//Returns a boolean representing if a database has at least monthNum months of consecutive data since the last piece of data
	//Database must have a date column
	private boolean hasMonthsOfData(String database, int monthNum, int dateColumnPos) {
		ArrayList<String[]> db = dop.getAllOutputs(database);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
		
		for(int i=0; i<db.size(); i++) {
			try {
				Date date = Date.valueOf(db.get(i)[dateColumnPos]);
				dates.add(dateToLocalDate(date));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dates.sort(null);
		
		//removing duplicate months
		for(int i = dates.size() - 1; i > 0; i--) {
			YearMonth current = YearMonth.from(dates.get(i));
			YearMonth previous = YearMonth.from(dates.get(i - 1));
			if(current.equals(previous)) {
				dates.remove(i);
			}
		}
		
		if(dates.size() - monthNum <= 0) {
			return false;
		}
		
		for(int i = dates.size() - 1; i > dates.size() - monthNum; i--) {
			YearMonth current = YearMonth.from(dates.get(i));
			YearMonth previous = YearMonth.from(dates.get(i - 1));
			if(!current.equals(previous.plusMonths(1))) {
				return false;
			}
		}
		return true;
		
	}
	
	//Changes ArrayList<String[]> from listing arrays of rows to listing arrays of columns
	private ArrayList<String[]> rowsToColumns(ArrayList<String[]> array){
		ArrayList<String[]> result = new ArrayList<String[]>();

		for(int x=0; x<array.get(0).length; x++) { //columns
			String[] column = new String[array.size()];
			
			for(int ix=0; ix<array.size(); ix++) { //rows
				column[ix] = array.get(ix)[x];
			}
				
			result.add(column);
		}
		
		return result;
	}

	//Reads values for labels from the server and stores them in this order:
	// Date / Category / Budget / Used
	// Date / Goal / Category / Saved / Target
	// Date / Due Date / Amount / Description / Payment / Type / Category / Reason / Status
	protected ArrayList<ArrayList<String[]>> getFinData(){
		ArrayList<ArrayList<String[]>> result = new ArrayList<ArrayList<String[]>>();
		dop.addTableType(ID, "budgets");
		ArrayList<String[]> budgets = dop.getAllOutputs(ID+"-budgets");
		result.add(budgets);
		dop.addTableType(ID, "savings");
		ArrayList<String[]> savings = dop.getAllOutputs(ID+"-savings");
		result.add(savings);
		dop.addTableType(ID, "transactions");
		ArrayList<String[]> transactions = dop.getAllOutputs(ID+"-transactions");
		result.add(transactions);
		return result;
	}
	
	//Reads values for labels from the server and stores them in this order:
	// ID / First Name / Last Name / Email / Premium / Currency / Date / Expiration Date
	// Total Balance / Total Debt / Total Savings / Annual Income
	protected ArrayList<String[]> getPersonalData(){
		ArrayList<String[]> result =new ArrayList<>();
		if(readToken()!=null) {
			String[] userData = readToken();
			result.add(userData);
		}
		else {
			return null;
		}
		ID = result.get(0)[0];
		String[] usersInfo = removeStringFromArray(dop.getOutputFromIdentifier("ID", ID, "users info"), 0, 5);
		result.add(usersInfo);
		return result;
	}
	
	//Returns only those rows in an array that have a specific value (conVal) for a specific column (conCol)
	private String[] getColumnValuesByCondition(ArrayList<String[]> array, int pos, int conCol, String conVal) {
		ArrayList<String[]> colArray = rowsToColumns(array);
		ArrayList<Integer> places = new ArrayList<Integer>();
		for(int i=0; i<colArray.get(0).length; i++) {
			if(conCol == -1 || conVal == null) {
				places.add(i);
			}
			else if(colArray.get(conCol)[i].equals(conVal)) {
				places.add(i);
			}
		}
		String[] result = new String[places.size()];
		for(int i=0; i<places.size(); i++) {
			result[i] = colArray.get(pos)[places.get(i)];
		}
		return result;
	}
	
	//Returns only those rows in an array that have a specific month (dateMonth) in a dateColumn (dateCol)
		private String[] getColumnValuesByDate(ArrayList<String[]> array, int pos, int dateCol, LocalDate dateMonth) {
			ArrayList<String[]> colArray = rowsToColumns(array);
			ArrayList<Integer> places = new ArrayList<Integer>();
			for(int i=0; i<colArray.get(0).length; i++) {
				LocalDate date = LocalDate.parse(colArray.get(dateCol)[i]);
				if(dateMonth.getMonth().equals(date.getMonth()) && dateMonth.getYear() == date.getYear()) {
					places.add(i);
				}
			}
			String[] result = new String[places.size()];
			for(int i=0; i<places.size(); i++) {
				result[i] = colArray.get(pos)[places.get(i)];
			}
			return result;
		}
	
	//Returns a sum of numbers from an array's column
	private double getSumByColumn(ArrayList<String[]> array, int column) {
		ArrayList<String[]> colArray = rowsToColumns(array);
		double result = 0;
			
		for(int i=0; i<colArray.get(column).length; i++) {
				result += Double.parseDouble(colArray.get(column)[i]);
		}
		return result;
	}
	
	//Returns a Number Of Columns that fit the same criteria as getSumByDatePeriod() function
	private int getNumberByDatePeriod(ArrayList<String[]> array, int dateColumn, int numColumn, String period, int conColumn, String con, Date date) {
		ArrayList<String[]> colArray = rowsToColumns(array);
		int[] places = getTimePerRows(colArray.get(dateColumn), period, date);
		int result = 0;
		
		for(int i=0; i<places.length; i++) {
			if(conColumn != -1 && con != null && colArray.get(conColumn)[i].equals(con)) {
				result++;
			}
			
			else if(conColumn == -1 || con == null) {
				result++;
			}
		}
		return result;
	}
	
	//The same as getNumberByDatePeriod() function, but with 2 conditionColumns
	//None of the 2 conditions can be a Null though and non of the conColumns can be a -1
	private int getNumberByDatePeriod(ArrayList<String[]> array, int dateColumn, int numColumn, String period, int conColumn, String con, int conColumn2, String con2, Date date) {
		ArrayList<String[]> colArray = rowsToColumns(array);
		int[] places = getTimePerRows(colArray.get(dateColumn), period, date);
		int result = 0;
		
		for(int i=0; i<places.length; i++) {
			if(colArray.get(conColumn)[i].equals(con) && colArray.get(conColumn2)[i].equals(con2)) {
				result++;
			}
		}
		return result;
	}
	
	//Returns a sum of numbers from an array's numColumn for a specific year or a month based on dateColumn if a conditionColumn == condition
	//if a date is null -> from current date, else -> from date specified
	protected double getSumByDatePeriod(ArrayList<String[]> array, int dateColumn, int numColumn, String period, int conColumn, String con, Date date) {
		int[] places = {};
		ArrayList<String[]> colArray = rowsToColumns(array);
		if(dateColumn != -1 && period != null) {
			places = getTimePerRows(colArray.get(dateColumn), period, date);
		}
		else {
			places = new int[colArray.size()];
			for(int i=0; i<colArray.size(); i++) {
				places[i] = i;
			}
		}
		
		double result = 0;
		
		for(int i=0; i<places.length; i++) {
			if(conColumn != -1 && con != null && colArray.get(conColumn)[i].equals(con)) {
				result += Double.parseDouble(colArray.get(numColumn)[places[i]]);
			}
			
			else if(conColumn == -1 || con == null) {
				result += Double.parseDouble(colArray.get(numColumn)[places[i]]);
			}
		}
		return result;
	}
	
	//The same as getSumByDatePeriod() function, but with 2 conditionColumns
	//None of the 2 conditions can be a Null though and none of the conColumns can be -1
	protected double getSumByDatePeriod(ArrayList<String[]> array, int dateColumn, int numColumn, String period, int conColumn, String con, int conColumn2, String con2, Date date) {
		int[] places = {};
		ArrayList<String[]> colArray = rowsToColumns(array);
		if(dateColumn != -1 && period != null) {
			places = getTimePerRows(colArray.get(dateColumn), period, date);
		}
		else {
			places = new int[colArray.size()];
			for(int i=0; i<colArray.size(); i++) {
				places[i] = i;
			}
		}
		
		double result = 0;
		
		for(int i=0; i<places.length; i++) {
			if(colArray.get(conColumn)[i].equals(con) && colArray.get(conColumn2)[i].equals(con2)) {
				result += Double.parseDouble(colArray.get(numColumn)[places[i]]);
			}
		}
		return result;
	}
	
	//Returns a date that is num of datePeriods away from a given date
	private Date returnPreviousDate(Date date, int num, String datePeriods) {
		LocalDate localDate = date.toLocalDate();
		switch(datePeriods) {
			case "day":
				date = Date.valueOf(localDate.minusDays(num));
				break;
			case "month":
				date = Date.valueOf(localDate.minusMonths(num));
				break;
			case "year":
				date = Date.valueOf(localDate.minusYears(num));
				break;
		}
		return date;
	}
	
	//Returns an array of numbers representing how much does each of the values represent from the total in order they come in
	private double[] calcPercentageFromTotal(double... nums) {
		double[] result = new double[nums.length];
		double sum = Arrays.stream(nums).sum();
		
		for(int i=0; i<nums.length; i++) {
			result[i] = nums[i] / sum;
		}
		 return result;
	}
	
	//Return points from a max directly proportionate, if more return max points
	private double getPointsDir(double maxNum, double maxPoints, double num) {
		if(num >= maxNum) {
			return maxPoints;
		}
		if(num <= 0) {
			return 0;
		}
		else {
			return (num / maxNum) * maxPoints;
		}
	}
	
	//get 0 points at Max number, Max points at 0, and calculate points in between
	private double getPointsIndir(double maxNum, double maxPoints, double num) {
		if(num >= maxNum) {
			return 0;
		}
		if(num == 0) {
			return maxPoints;
		}
		return ((maxNum - num) / maxNum) * maxPoints;
	}
	
	//returns Max points if lower than maxNum, returns 0 at >100, calculates in between
	private double getMaxPointsFromMaxDown(double maxNum, double maxPoints, double num) {
		if(num <= maxNum) {
			return maxPoints;
		}
		if(num >= 100) {
			return 0;
		}
		return (100 - num) / (100 - maxNum) * maxPoints;
	}
	
	//Returns a Financial Health Score based on Annual Savings, Annual Income, Total Debt, and Annual Expense
	private double getFinHealthScore(double yearlyIncome, double yearlySavings, double totalDebt, double yearlyExpenses) {
		double p1 = getPointsDir(20, 30, (yearlySavings / yearlyIncome)*100);
		double p2 = getPointsIndir(50, 30, (totalDebt / yearlyIncome)*100);
		double p3 = getMaxPointsFromMaxDown(50, 25, (yearlyExpenses / yearlyIncome)*100);
		double p4 = getPointsDir(20, 15, ((yearlyIncome - (yearlySavings + yearlyExpenses)) / yearlyIncome)*100);
		return (p1 + p2 + p3 + p4);
	}
	
	//Get a number Deviation of an expense over months
	private double getDeviation(double currentExpenses, double yearlyExpenses, double yearlyExNum) {
		if(yearlyExNum == 0) return 0;
		double av = yearlyExNum == 0 ? 0 : yearlyExpenses / yearlyExNum;
		double absoluteDeviation = currentExpenses - av;
		double percentDeviation = absoluteDeviation / av;
		
		return percentDeviation * 100;
	}
	
	//Get a score for the largest expense that has increased the most on average since last month
	//Returns String[] -> [0] = category, [1] = largest deviation, [2] months average, [3] monthly expenses
	private String[] getInsightScore() {
		String[] categories = {"Housing", "Utilities", "Food & Dining", "Transportation", "Shopping", "Entertainment", "Healthcare", "Education", "Travel", "Family", "Pets", "Personal Care", "Debt Payments", "Miscellaneous"};
		double[] deviations = new double[categories.length];
		double[] avs = new double [categories.length];
		double[] mExpenses = new double [categories.length];
		
		for(int i=0; i<categories.length; i++) {
			double yearlyExpenses = getSumByDatePeriod(finData.get(2), 0, 2, "year", 5, "expense", 6, categories[i].toLowerCase(), null);
			int yearlyExpensesNum = getNumberByDatePeriod(finData.get(2), 0, 2, "year", 5, "expense", 6, categories[i].toLowerCase(), null);
			double monthlyExpenses = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 6, categories[i].toLowerCase(), null);
			mExpenses[i] = monthlyExpenses;
			double av = yearlyExpenses / yearlyExpensesNum;
			avs[i] = av;
			deviations[i] = getDeviation(monthlyExpenses, yearlyExpenses, yearlyExpensesNum);
		}
		
		double[] devSorted = Arrays.copyOf(deviations, categories.length);
		Arrays.sort(devSorted);
		int pos = -1;
		double largest = devSorted[categories.length - 1];
		for(int i=0; i<categories.length; i++) {
			if(deviations[i] == largest) {
				pos = i;
			}
		}
		
		return new String[] {categories[pos], ""+largest, ""+avs[pos], ""+mExpenses[pos]};
	}
	
	//Calculate and form a result for a financialChangesL
	protected String finChangesText() {
		String[] categories = {"Housing", "Utilities", "Food & Dining", "Transportation", "Shopping", "Entertainment", "Healthcare", "Education", "Travel", "Family", "Pets", "Personal Care", "Debt Payments", "Miscellaneous"};
		String[] icons = {"🏠", "💡", "🍔", "⛽", "🛒", "🎬", "🏥", "📚", "💳", "📈", "🏦", "✈", "👶", "🐶", "💄", "📦"};
		String result = "";
		
		for(int i=0; i<categories.length; i++) {
			double previousExpensesSum = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 6, categories[i].toLowerCase(), returnPreviousDate(Date.valueOf(LocalDate.now()), 1, "month"));
			double expensesSum = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 6, categories[i].toLowerCase(), null);
			double percent = previousExpensesSum != 0 ? ((expensesSum - previousExpensesSum) / previousExpensesSum) * 100: 0;
			
			result += icons[i]+" "+categories[i]+":   "+String.format("%.2f",percent)+"%\n";
		}
		return result;
	}
	
	//Returns estimated income and expenses based on data from previous months
	private double getEstimatedAmount(String type) {
		int max = hasMonthsOfData(ID+"-transactions", 5, 0) ? 5: 3;
		double[] monthlyData = new double[max];
		
		for(int i=0; i<max; i++) {
			monthlyData[i] = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, type, returnPreviousDate(Date.valueOf(LocalDate.now()), i, "month"));
		}
		
		double numerator = 0;
		double denominator = 0;
		for(int i=0; i<max; i++) {
			if(i + 1 < max) {
				numerator += monthlyData[i] - monthlyData[i + 1];
				denominator++;
			}
		}
		
		double change = numerator / denominator;
		
		return monthlyData[max - 1] + change;
	}
	
	//Get all unique values from every specific column in an array
	private  ArrayList<Date> getAllUniqueDates(ArrayList<String[]> array, int pos) {
		ArrayList<Date> result = new ArrayList<Date>();
		
		mainLoop:
		for(String[] row: array) {
			String dtS = row[pos];
			LocalDate localD = LocalDate.parse(dtS);
			for(Date d: result) {
				if(dateToLocalDate(d).getYear() == localD.getYear()) {
					continue mainLoop;
				}
			}
			result.add(localDateToDate(localD));
			
		}
		
		return result;
		
	}
	
	//Assigns values to a Line Chart based on data from a specific period: "year", "all"
	//dataType = "income", "expense", "saving"
	//chartType = "line", "bar", if it's "line" set the barChart = NULL, and vice versa
	private void assignValuesToChartPeriodBased(LineChart<String, Number> lineChart, BarChart<String, Number> barChart, String period, String dataType, String chartType) {
		
		if(period.equals("year")) {
			
			String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			Number[] data = new Number[12];
			
			for(int i=1; i<=12; i++) {
				LocalDate date = LocalDate.of(Year.now().getValue(), i, 1);
				Date dateD = localDateToDate(date);
				double monthlyData = 0;
				if(dataType.equals("saving")) {
					monthlyData = getSumByDatePeriod(finData.get(1), 0, 3, "month", -1, null, dateD);
				}
				else {
					monthlyData = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, dataType, dateD);
				}
				data[i - 1] = monthlyData;
			}
			
			if(chartType.equals("line")) {
				setLineChartParameters(lineChart, months, data);
			}
			else if(chartType.equals("bar")) {
				setBarChartParameters(barChart, months, data);
			}
			
		}
		
		else if(period.equals("all")) {
			ArrayList<Date> dates = getAllUniqueDates(finData.get(2), 0);
			
			Number[] data = new Number[dates.size()];

			String[] datesS = new String[dates.size()];
			int i=0;
			for(Date date: dates) {
				double yearlyData = getSumByDatePeriod(finData.get(2), 0, 2, "year", 5, dataType, date);
				data[i] = yearlyData;

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				datesS[i] = formatter.format(date);
				i++;
				
			}
			
			if(chartType.equals("line")) {
				setLineChartParameters(lineChart, datesS, data);
			}
			else if(chartType.equals("bar")) {
				setBarChartParameters(barChart, datesS, data);
			}
		}
		
	}
	
	//Returns a String[][] created from a number of String[]
	//Where (in the input) each String[] represents a column of values
	//It reverses the order of the rows
	//Formats all money values in moneyColumns
	private String[][] getFlattenedColumns(int[] moneyColumns, boolean useMoneyFormat, String[]... strings){
		String[][] result = new String[strings[0].length][strings.length];
		for(int i=0; i<strings.length; i++) {
			midLoop:
			for(int x=0; x<strings[i].length; x++) {
				int reverseRow = strings[i].length - x - 1;
				if(useMoneyFormat) {
					for(int m:moneyColumns) {
						if(m == i) {
							result[reverseRow][i] = this.setMoneyStringValue(strings[i][x], getCurrencySign(userData.get(0)[5]));
							continue midLoop;
						}
					}
				}
				result[reverseRow][i] = strings[i][x];
				
			}
		}
		
		return result;
	}
	
	//Returns a 2D String array that contains a new value in each row on a specific index (adding a column)
	//Into the new column it adds values based on an operation between 2 values in the row specified by index1 and index2
	// If moneyFormat, it makes sure that the double will be formatted properly as a money
	//If percentFormat, it adds a percentage sign at the end of the string
	private String[][] addColumnIn2DArrayWithOperation(String[][] array, int index1, int index2, String operation, boolean moneyFormat, boolean percentFormat){
		String[][] result = Arrays.copyOf(array, array.length);
		for(int i=0; i<result.length; i++) {
			String[] copy  = Arrays.copyOf(result[i], result[i].length+1);
			
			switch(operation){
			
				case "+":
					copy[result[i].length] = String.format("%.2f", Double.parseDouble(copy[index1]) + Double.parseDouble(copy[index2]));
					break;
					
				case "-":
					copy[result[i].length] = String.format("%.2f", Double.parseDouble(copy[index1]) - Double.parseDouble(copy[index2]));
					break;
					
				case "*":
					copy[result[i].length] = String.format("%.2f", Double.parseDouble(copy[index1]) * Double.parseDouble(copy[index2]));
					break;
					
				case "/":
					copy[result[i].length] = String.format("%.2f", Double.parseDouble(copy[index1]) / Double.parseDouble(copy[index2]));
					break;
					
				case "%":
					copy[result[i].length] = String.format("%.2f", (Double.parseDouble(copy[index1]) / Double.parseDouble(copy[index2]))*100);
					break;
					
			}
			
			// Prevents NaN
			if((operation.equals("/") || operation.equals("%")) && Double.parseDouble(copy[index2]) == 0){
				copy[result[i].length] = "0";
			}
			
			if(moneyFormat) {
				copy[result[i].length] = setMoneyStringValue(copy[result[i].length], getCurrencySign(userData.get(0)[5]));
			}
			else if(percentFormat) {
				copy[result[i].length] = setPercentStringValue(copy[result[i].length]);
			}
			
			result[i] = copy;
		}
		
		return result;
	}
	
	//Assigning values to the tables in the Application
	private void assignValuesToTables() {
		String[] incomeSources = getColumnValuesByCondition(finData.get(2), 3, 5, "income");
		String[] incomeValues = getColumnValuesByCondition(finData.get(2), 2, 5, "income");
		
		String[] exDate = getColumnValuesByCondition(finData.get(2), 0, 5, "expense");
		String[] exCat = getColumnValuesByCondition(finData.get(2), 6, 5, "expense");
		String[] exDes = getColumnValuesByCondition(finData.get(2), 3, 5, "expense");
		String[] exPay = getColumnValuesByCondition(finData.get(2), 4, 5, "expense");
		String[] exAmount = getColumnValuesByCondition(finData.get(2), 2, 5, "expense");
		String[] exSta = getColumnValuesByCondition(finData.get(2), 8, 5, "expense");
		
		String[] saDes = getColumnValuesByCondition(finData.get(1), 1, -1, null);
		String[] saCat = getColumnValuesByCondition(finData.get(1), 2, -1, null);
		String[] saSaved = getColumnValuesByCondition(finData.get(1), 3, -1, null);
		String[] saTarget = getColumnValuesByCondition(finData.get(1), 4, -1, null);
		
		String[] bgCat = getColumnValuesByDate(finData.get(0), 1, 0, LocalDate.now());
		String[] bgBudget = getColumnValuesByDate(finData.get(0), 2, 0, LocalDate.now());
		String[] bgUsed = getColumnValuesByDate(finData.get(0), 3, 0, LocalDate.now());
		createIncomeTable(getFlattenedColumns(new int[] {1}, true, incomeSources, incomeValues));
		createExpensesTable(getFlattenedColumns(new int[] {4}, true, exDate, exCat, exDes, exPay, exAmount, exSta));
		
		//Those that get the money string formatting later in addColumnIn2DArrayWithOperation() should not get it before with the getFlattenedColumns()
		
		String[][] savings2D = getFlattenedColumns(new int[] {2, 3}, false, saDes, saCat,saSaved, saTarget);
		String[][] addedSavings2D = addColumnIn2DArrayWithOperation(savings2D, 2, 3, "%", false, true);
		createSavingsTable(addedSavings2D);
		
		String[][] budgeting2D = getFlattenedColumns(new int[] {1, 2}, false, bgCat, bgBudget, bgUsed);
		String[][] addedBudgeting2D = addColumnIn2DArrayWithOperation(budgeting2D, 1, 2, "-", true, false);
		addedBudgeting2D = addColumnIn2DArrayWithOperation(addedBudgeting2D, 2, 1, "%", true, false);
		createBudgetingTable(addedBudgeting2D);
		
		//Filling the Recent transactions V Box
		ArrayList<String[]> transactions = finData.get(2);
		recentTransVB.getChildren().clear();
		for(int i=transactions.size()-1; i>=0; i--) {
			String[] row = transactions.get(i);
			String negative = row[5].equals("expense") ? "-": "";
			Label label = createVBLabel(" Amount: "+negative+setMoneyStringValue(row[2], userData.get(0)[5])+" | Date: "+row[0]);
			label.setMinHeight(50);
			label.setMaxHeight(50);
			recentTransVB.getChildren().add(label);
		}
		
		//Filling the Upcomming Payments V Box
		ArrayList<String[]> colData = finData.get(2);
		upcommingBillsVB.getChildren().clear();
		for(int i=colData.size()-1; i>=0; i--) {
			String[] row = colData.get(i);
			LocalDate deadline = LocalDate.parse(row[1]);
			if(deadline.isAfter(LocalDate.now()) && deadline.getMonth().equals(LocalDate.now().getMonth()) && row[5].equals("expense")) {
				Label label = createVBLabel(row[3]);
				label.setMinHeight(50);
				label.setMaxHeight(50);
				upcommingBillsVB.getChildren().add(label);
			}
		}
	}
	
	//Assigning values to charts in the Application
	private void assignValuesToCharts() {
		double monthlyNeeds = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 7, "need", null);
		double monthlyWants = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", 7, "want", null);
		double savings = getSumByDatePeriod(finData.get(1), 0, 3, "month", -1, null, null);
		double sum = monthlyNeeds + monthlyWants + savings;
		
		assignValuesToChartPeriodBased(incomeC, null, "year", "income", "line");
		assignValuesToChartPeriodBased(expensesC, null, "year", "expense", "line");
		assignValuesToChartPeriodBased(null, savingsC, "year", "saving", "bar");
		setPieChartParameters(budgetingC, new String[] {"Needs", "Wants", "Savings"}, new double[] {monthlyNeeds / sum, monthlyWants / sum, savings / sum});
	}
	
	//Assigning values to labels in the Application
	private void assignValuesToLabels() {
		double yearlyIncomes = getSumByDatePeriod(finData.get(2), 0, 2, "year", 5, "income", null);
		double monthlyIncomes = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "income", null);
		double yearlyExpenses = getSumByDatePeriod(finData.get(2), 0, 2, "year", 5, "expense", null);
		double monthlyExpenses = getSumByDatePeriod(finData.get(2), 0, 2, "month", 5, "expense", null);
		double monthlySavings = getSumByDatePeriod(finData.get(1), 0, 3, "month", -1, null, null);
		double yearlySavings = getSumByDatePeriod(finData.get(1), 0, 3, "year", -1, null, null);
		double monthlyUsedBudget = getSumByDatePeriod(finData.get(0), 0, 3, "month", -1, null, null);
		double monthlyExpectedBudget = getSumByDatePeriod(finData.get(0), 0, 2, "month", -1, null, null);
		double needTransactions = getSumByDatePeriod(finData.get(2), 0, 2, "month", 7, "need", null);
		double wantTransactions = getSumByDatePeriod(finData.get(2), 0, 2, "month", 7, "want", null);
		String currency = getCurrencySign(userData.get(0)[5]);
		
		//wants to needs to savings ratio
		double[] wtntsr = calcPercentageFromTotal(needTransactions, wantTransactions, monthlySavings);
		double budgetHealth =  Math.max(0, 100 - (Math.abs(wtntsr[0] - 50) + Math.abs(wtntsr[1] - 30) + Math.abs(wtntsr[2] - 20)) / 3);
		double previousIncomes = getSumByDatePeriod(finData.get(2), 0, 2, "year", 5, "income", returnPreviousDate(Date.valueOf(LocalDate.now()), 3, "month"));
		String changed = monthlyIncomes > previousIncomes ?  "increased": "decreased";
		double percentDifference = Math.abs(previousIncomes - monthlyIncomes) / previousIncomes * 100;;
		double numDifference = Math.abs(monthlyIncomes - previousIncomes) * 12;
		String extra = changed.equals("increased") ? "extra": "";
		String earn = changed.equals("increased") ? "earn": "lose";
		
		//AI insight text
		String insight = "";
		if(hasMonthsOfData(ID+"-transactions", 2, 0)) {
			double score = Double.parseDouble(getInsightScore()[1]);
			String category = getInsightScore()[0];
			double average = Double.parseDouble(getInsightScore()[2]);
			double monthlyExs = Double.parseDouble(getInsightScore()[3]);
			boolean positive = score >= 0;
			String more = positive ? "more": "less";
			insight = "You are spending "+String.format("%.2f", score)+"% "+more+" on "+category+" than usual.\n"+
			"This month: "+currency+String.format("%.2f", monthlyExs)+"\n"+
			"Average: "+currency+String.format("%.2f", average)+"\n";
		}
		
		//Income Expenses and Savings Prediction
		double incomePrediction = getEstimatedAmount("income");
		double expensesPrediction = getEstimatedAmount("expense");
		double savingsPrediction = incomePrediction - expensesPrediction;
		
		//Dashboard Labels
		totalSavings1L.setText(userData.get(1)[2]);
		setMoneyLabelValue(totalSavings1L, null, userData.get(0)[5], false);
		totalBalance1L.setText(userData.get(1)[0]);
		setMoneyLabelValue(totalBalance1L, totalBalance1I, userData.get(0)[5], true);
		totalDebt1L.setText(userData.get(1)[1]);
		setMoneyLabelValue(totalDebt1L, null, userData.get(0)[5], false);
		totalYearlyExpenses1L.setText(""+yearlyExpenses);
		setMoneyLabelValue(totalYearlyExpenses1L, null, userData.get(0)[5], false);
		cashFlow1L.setText(""+(yearlyIncomes - yearlyExpenses));
		setMoneyLabelValue(cashFlow1L, cashFlow1I, userData.get(0)[5], true);

		totalBalanceL.setText(userData.get(1)[0]);
		setMoneyLabelValue(totalBalanceL, totalBalanceI, userData.get(0)[5], true);
		totalDebtL.setText(userData.get(1)[1]);
		setMoneyLabelValue(totalDebtL, null, userData.get(0)[5], false);
		if(yearlyIncomes > 0) {
			financialHealthL.setText(""+getFinHealthScore(yearlyIncomes, yearlySavings, Double.parseDouble(userData.get(1)[1]), yearlyExpenses));
			setPercentLabelValue(financialHealthL);
		}
		cashFlowL.setText(""+(yearlyIncomes - yearlyExpenses));
		setMoneyLabelValue(cashFlowL, cashFlowI, userData.get(0)[5], true);
		
		//Income Labels
		annualIncomeL.setText(userData.get(1)[3]);
		setMoneyLabelValue(annualIncomeL, null, userData.get(0)[5], false);
		monthlyIncomeL.setText(""+(Double.parseDouble(userData.get(1)[3])/12));
		setMoneyLabelValue(monthlyIncomeL, null, userData.get(0)[5], false);
		if(hasMonthsOfData(ID+"-savings", 3, 0) && hasMonthsOfData(ID+"-transactions", 3, 0)) {
			incomeChangeApproximationL.setText("Your current income has "+changed+" by "+String.format("%.2f", percentDifference)+"% over the last 3 months. "
					+ "If this trend continues, you'll "+earn+" approximately "+currency+String.format("%.2f", numDifference)+" "+extra+" this year.");
		}
		
		//Expenses Labels
		avDayL.setText(""+(yearlyExpenses / 365));
		setMoneyLabelValue(avDayL, null, userData.get(0)[5], false);
		thisMonthL.setText(""+monthlyExpenses);
		setMoneyLabelValue(thisMonthL, null, userData.get(0)[5], false);
		thisYearL.setText(""+yearlyExpenses);
		setMoneyLabelValue(thisYearL, null, userData.get(0)[5], false);
		
		//Savings Labels
		totalSavedL.setText(userData.get(1)[2]);
		setMoneyLabelValue(totalSavedL, null, userData.get(0)[5], false);
		savingsRateL.setText(""+(monthlySavings / (Double.parseDouble(userData.get(1)[3]) / 12)) * 100);
		setPercentLabelValue(savingsRateL);
		projectedSavingsTotalL.setText(""+getSumByColumn(finData.get(1), 4));
		setMoneyLabelValue(projectedSavingsTotalL, null, userData.get(0)[5], false);
		goalCompletionL.setText(""+(getSumByColumn(finData.get(1), 3) / getSumByColumn(finData.get(1), 4)) * 100);
		setPercentLabelValue(goalCompletionL);
		
		//Budget Labels
		incomeL.setText(""+(Double.parseDouble(userData.get(1)[3]) / 12));
		setMoneyLabelValue(incomeL, null, userData.get(0)[5], false);
		usedL.setText(""+monthlyUsedBudget);
		setMoneyLabelValue(usedL, null, userData.get(0)[5], false);
		budgetHealthL.setText(""+budgetHealth);
		setPercentLabelValue(budgetHealthL);
		remainingL.setText(""+(monthlyUsedBudget - (Double.parseDouble(userData.get(1)[3]) / 12)));
		setMoneyLabelValue(remainingL, null, userData.get(0)[5], false);
		
		//AI Advisor Labels
		AIServerConnector asc = new AIServerConnector(monthlyIncomes, monthlyExpenses, (monthlySavings / monthlyIncomes), monthlyExpectedBudget, Double.parseDouble(userData.get(1)[1]) / yearlyIncomes);
		creditScoreL.setText(""+(Math.max(Math.min(asc.result, 850), 300)));
		if(hasMonthsOfData(ID+"-transactions", 2, 0)) {
			insightL.setText(insight);
		}
		if(hasMonthsOfData(ID+"-transactions", 2, 0)) {
			financialChangesL.setText(finChangesText());
		}
		if(hasMonthsOfData(ID+"-transactions", 3, 0)) {
			estimatedIncomeL.setText("Income: $"+incomePrediction);
			estimatedExpensesL.setText("Expenses: $"+expensesPrediction);
			estimatedSavingsL.setText("Savings: $"+savingsPrediction);
		}
		
	}
	
	@FXML
	public void initialize() {
		updateValues();
		
	}
	
	//Set initial values for all labels, tables, and charts
	protected void updateValues() {
		Main main = new Main();
		dop = main.getDOP();
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Number[] nums = new Number[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		
		financialChangesL.setText("🏠 Housing:   0%\n"
				+ "💡 Utilities:   0%\n"
				+ "🍔 Food & Dining:   0%\n"
				+ "⛽ Transportation:   0%\n"
				+ "🛒 Shopping:   0%\n"
				+ "🎬 Entertainment:   0%\n"
				+ "🏥 Healthcare:   0%\n"
				+ "📚 Education:   0%\n"
				+ "💳 Debt Payments:   0%\n"
				+ "📈 Investments:   0%\n"
				+ "🏦 Savings:   0%\n"
				+ "✈ Travel:   0%\n"
				+ "👶 Family:   0%\n"
				+ "🐶 Pets:   0%\n"
				+ "💄 Personal Care:   0%\n"
				+ "📦 Miscellaneous:   0%");
		
		/*recentTransVB.getChildren().add(createVBLabel("You will see your recent transactions here."));
		upcommingBillsVB.getChildren().add(createVBLabel("You will see your upcomming bills here."));*/
		
		//setting default values for Charts
		/*setLineChartParameters(incomeC, Arrays.copyOf(months, 6), Arrays.copyOf(nums, 6));
		setLineChartParameters(expensesC, Arrays.copyOf(months, 6), Arrays.copyOf(nums, 6));
		setBarChartParameters(savingsC, Arrays.copyOf(months, 6), Arrays.copyOf(nums, 6));
		setPieChartParameters(budgetingC, new String[] {"Needs", "Wants", "Savings"}, new double[] {50,30,20});*/
		
		//assign default table values
		/*createIncomeTable(new String[][] {{"Income Sources", "Amounts Earned"}});
		createExpensesTable(new String[][] {{"0/0/0000", "🏠 Housing", "Rent", "Debit", "$1500", "progress"}});
		createSavingsTable(new String[][] {{"New Roof", "🏠 Housing", "$000,000,000.00", "$000,000,000.00", "40%"}});
		createBudgetingTable(new String[][] {{"🏠 Housing", "$000,000,000.00", "$000,000,000.00", "$000,000,000.00", "40%"}});*/
		
		ArrayList<String[]> userData = getPersonalData();
		ArrayList<ArrayList<String[]>> finData = getFinData();
		this.userData = userData;
		this.finData = finData;
		assignValuesToLabels();
		assignValuesToCharts();
		assignValuesToTables();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
		LocalDateTime ld = LocalDateTime.now();
	    String formattedDate = dateFormat.format(ld);
		synchL.setText("Last sync: "+formattedDate);
		nameL.setText(userData.get(0)[1]+" "+userData.get(0)[2]);
		
		//Delete all rows of budgeting that are older than a month
		if(!finData.get(0).isEmpty()) {
			for(String[] row: finData.get(0)) {
				LocalDate today = LocalDate.now();
				LocalDate thatDate = LocalDate.parse(row[0]);
				if(!today.getMonth().equals(thatDate.getMonth()) || today.getYear() != thatDate.getYear()) {
					dop.deleteRow(ID+"-budgets", "Date", thatDate.toString(), Types.DATE);
				}
			}
		}
		
	}
	
	//Finds all the panels of the sidebar in the FXML file and puts them into the "panes" (Pane[])
	public void setRoot(Parent root) {
		dashboardP = (Pane) root.lookup("#dashboard_pane");
		incomeP = (Pane) root.lookup("#income_pane");
		expensesP = (Pane) root.lookup("#expenses_pane");
		savingsP = (Pane) root.lookup("#savings_pane");
		budgetingP = (Pane) root.lookup("#budgeting_pane");
		advisorP = (Pane) root.lookup("#advisor_pane");
		settingsP = (Pane) root.lookup("#settings_pane");
		panes = new Pane[] {dashboardP, incomeP, expensesP, savingsP, budgetingP, advisorP, settingsP};
	}
	
	
	
	//Income Buttons:
	
	@FXML
	public void addIncomesB(ActionEvent e) {
		supportGUI = new SupportGUI("addIncome", ID);
	}
	
	@FXML
	public void allIncomeB(ActionEvent e) {
		assignValuesToChartPeriodBased(incomeC, null, "all", "income", "line");
		
	}
	
	@FXML
	public void yearIncomeB(ActionEvent e) {
		assignValuesToChartPeriodBased(incomeC, null, "year", "income", "line");
		
	}
	
	//Expenses buttons:

	@FXML
	public void addExpensesB(ActionEvent e) {
		supportGUI = new SupportGUI("addExpense", ID);
	}
	
	@FXML
	public void allExpensesB(ActionEvent e) {
		assignValuesToChartPeriodBased(expensesC, null, "year", "expense", "line");
		
	}
	
	@FXML
	public void yearExpensesB(ActionEvent e) {
		assignValuesToChartPeriodBased(expensesC, null, "year", "expense", "line");
	}
	
	//Savings buttons:

	@FXML
	public void addSavingsB(ActionEvent e) {
		supportGUI = new SupportGUI("addSaving", ID);
	}
	
	@FXML
	public void allSavingsB(ActionEvent e) {
		assignValuesToChartPeriodBased(null, savingsC, "all", "saving", "bar");
			
	}
		
	@FXML
	public void yearSavingsB(ActionEvent e) {
		assignValuesToChartPeriodBased(null, savingsC, "year", "saving", "bar");
			
	}
	
	//Budgeting buttons:

	@FXML
	public void addGoalB(ActionEvent e) {
		supportGUI = new SupportGUI("addGoal", ID);
	}
	
	//AI buttons

	@FXML
	public void simulateB(ActionEvent e) {
		//Test:
		/*Scanner sc = new Scanner(System.in);
		System.out.println("Provide data with space inbetween each as follows: monthlyIncome monthlyExpenses savingsRate budgetGoal debtToIncomeRatio");
		String[] strings = sc.nextLine().split(" ");
		double[] data = new double[strings.length];
		for(int i=0; i<strings.length; i++) {data[i] = Double.parseDouble(strings[i]);}
		sc.close();
	AIServerConnector asc = new AIServerConnector(data[0], data[1], data[2], data[3], data[4]);
	System.out.println("NN prediction: "+asc.result);*/
		
		supportGUI = new SupportGUI("simulate", "");
	}
	
	
	//Pane Buttons
	
	@FXML
	public void dashboardB(ActionEvent e) {
		dashboardP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(dashboardP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void incomeB(ActionEvent e) {

		incomeP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(incomeP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void expensesB(ActionEvent e) {

		expensesP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(expensesP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void savingsB(ActionEvent e) {
		
		savingsP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(savingsP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void budgetingB(ActionEvent e) {

		budgetingP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(budgetingP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void advisorB(ActionEvent e) {

		advisorP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(advisorP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void settingsB(ActionEvent e) {

		settingsP.setVisible(true);
		for (Pane pane:panes) {if(!pane.equals(settingsP)) {pane.setVisible(false);}}
		
	}
	
	@FXML
	public void profileB(MouseEvent e) {
		supportGUI = new SupportGUI("profile", ID);
	}
	
}

