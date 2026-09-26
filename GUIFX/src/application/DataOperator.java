package application;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class DataOperator {
	
	private String ID = "";
	private Connection connection;
	private Statement st = null;
	private String host = "ngrok support";

	//Connects to the MySQL database
	protected DataOperator(String UserName, String Password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://"+host+":0000/finaq", UserName, Password);
			st=connection.createStatement();
			System.out.println("Connected: "+connection.isValid(0));
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//Disconnect from the MySQL Server
	protected void terminateConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected int getRowNum(String table) {
		String order = "SELECT * FROM `"+table+"`";
		int res = 0;
		try {
			ResultSet rs = st.executeQuery(order);
			while(rs.next()) {
				res++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	//generates a unique 9 digit ID or a 12 digit Token consisting of letters and integers
	//makes sure no such ID has been used before
	//Letters only lowercase because they will be used to name tables in mysql which only accept lowercase letters
	protected String generatePasscode(String type) {
		int digits = type.equals("ID") ? 9 : 12;
		String chars = "a b c d e f g h i j k l m n o p q r s t u v w x y z "
				+ "0 1 2 3 4 5 6 7 8 9";
		String[] charsA = chars.split(" ");
		Random random = new Random();
		String code = "";
		ArrayList<String> data = new ArrayList<String>();
		String order = "SELECT "+type+" FROM users";
		try {
			ResultSet rs = st.executeQuery(order);
			while(rs.next()) {
				data.add(rs.getString(type));
			}
			while (code.equals("") || data.contains(code)){
				for (int i=0; i<digits; i++) {
					code+=charsA[random.nextInt(0, charsA.length)];
				}
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
		
	}
	
	//Creates a new table for a new user
	protected void addTableType(String ID, String type) {
		try {
			String exe = "CREATE TABLE IF NOT EXISTS `"+ID+"-"+type+"` (";
			switch (type) {
			
				case "budgets":
					exe += "Date DATE,";
					exe += "Category VARCHAR(20),";
					exe += "Budget DOUBLE NOT NULL,";
					exe += "Used DOUBLE NOT NULL";
					break;
				
				case "savings":
					exe += "Date DATE,";
					exe += "Goal VARCHAR(20),";
					exe += "Category VARCHAR(20),";
					exe += "Saved DOUBLE NOT NULL,";
					exe += "Target DOUBLE NOT NULL";
					break;
					
				case "transactions":
					exe += "Date DATE,";
					exe += "`Due Date` DATE,";
					exe += "Amount DOUBLE NOT NULL,";
					exe += "Description VARCHAR(30),";
					exe += "Payment VARCHAR(15),";
					exe += "Type VARCHAR(7),";
					exe += "Category VARCHAR(20),";
					exe += "Reason VARCHAR(4),";
					exe += "Status VARCHAR(20)";
					break;
				default:
					System.out.println("PASS ON: "+type);
			}
			
			exe += ")";
			int res = st.executeUpdate(exe);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	//make sure that depending on the SQL type of a column the statement can be prepend properly
	private void prepStatementInFormat(PreparedStatement pst, String input, int type, int i){
		try {
		switch (type){
			case Types.TINYINT:
				pst.setInt(i, Integer.parseInt(input));
            	break;
			case Types.VARCHAR:
				pst.setString(i, input);
				break;
			case Types.INTEGER:
				pst.setInt(i, Integer.parseInt(input));
				break;
			case Types.DOUBLE:
				pst.setDouble(i, Double.parseDouble(input));
				break;
			case Types.DATE:
				Date sqlDate = Date.valueOf(input);
				pst.setDate(i, sqlDate);
				break;
			case Types.BOOLEAN:
				pst.setBoolean(i, Boolean.parseBoolean(input));
				break;
			default:
                pst.setString(i, input);
                break;
		}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//Add a row in a mySQL
	protected int setInput(String table, String[] inputs) {
		String order = "SELECT * FROM `"+table+"`";
		String statement = "INSERT INTO `"+table+"` (";
		int result = 0;
		
		try {
			
			ResultSet rs = st.executeQuery(order);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for(int i=1; i<=rsmd.getColumnCount(); i++) {
				statement += "`"+rsmd.getColumnName(i)+"`";
				
				if(i+1<=rsmd.getColumnCount()) {
					statement += ", ";
				}
			}
			
			statement+=") VALUES (";
			
			for(int i=0; i<inputs.length; i++) {
				statement += "?";
				
				if(i+1<inputs.length) {
					statement += ", ";
				}
			}
			
			statement += ")";
			PreparedStatement pst = connection.prepareStatement(statement);
			
			for (int i=0; i<inputs.length; i++) {
				int columnType = rsmd.getColumnType(i + 1);
				prepStatementInFormat(pst, inputs[i], columnType, i + 1);
			}

			result = pst.executeUpdate();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//Changes a particular value in "column" for a value of an "input" in a row where "idColumn" has a value of "identifier"
	protected int alterValueInRow(String table, String idColumn, String identifier, String column, String input) {
		String statement = "UPDATE `"+table+"` SET `"+column+"` = ? WHERE `"+idColumn+"` = ?";
		int result = 0;
		try(PreparedStatement pst = connection.prepareStatement(statement)) {
			String order = "SELECT * FROM `"+table+"`";
			ResultSet rs = st.executeQuery(order);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int cType = rsmd.getColumnType(rs.findColumn(column));
			prepStatementInFormat(pst, input, cType, 1);
			int idType = rsmd.getColumnType(rs.findColumn(idColumn));
			prepStatementInFormat(pst, identifier, idType, 2);
			
			result = pst.executeUpdate();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//Returns a boolean that declares whether there is any value of "identifier" in a column "idColumn
	protected boolean exists(String table, String idColumn, String identifier) {
		ArrayList<String> output = new ArrayList<String>();
		String order = "SELECT * FROM `"+table+"`";
		try {
			ResultSet rs = st.executeQuery(order);
	        
			while(rs.next()) {
				output.add(rs.getObject(rs.findColumn(idColumn)).toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(String s: output) {
			if(s.equals(identifier)) {
				return true;
			}
		}
		return false;
	}
	
	//Get output from a mySQL table based on an identifier and its idColumn
	protected String[] getOutputFromIdentifier(String idColumn, String identifier,  String table) {
		ArrayList<String> output= new ArrayList<String>();
		String order = "SELECT * FROM `"+table+"`";
		try {
			ResultSet rs = st.executeQuery(order);
			ResultSetMetaData rsmd = rs.getMetaData();
	        int columnCount = rsmd.getColumnCount();
	        int check = 0;
			while(rs.next()) {
				if(rs.getObject(rs.findColumn(idColumn)).toString().equals(identifier)) {
					for (int i=1; i<=columnCount; i++) {
						output.add(rs.getObject(i).toString());
					}
					check++;
				}
			}
			if(check < 1) {
				return null;
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return output.toArray(String[]::new);
	}
	
	//Deletes a row in a table where a "columnID" == "id"
	protected void deleteRow(String table, String columnID, String id, int idType) {
		String order = "DELETE FROM `" + table + "` WHERE `" + columnID + "` = ?";
		try {
			PreparedStatement pst = connection.prepareStatement(order);
			prepStatementInFormat(pst, id, idType, 1);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected ArrayList<String[]> getAllOutputs(String table) {
		ArrayList<String[]> output= new ArrayList<String[]>();
		String order = "SELECT * FROM `"+table+"`";
		try {
			ResultSet rs = st.executeQuery(order);
			ResultSetMetaData rsmd = rs.getMetaData();
	        int columnCount = rsmd.getColumnCount();
	        
			while(rs.next()) {
				String[] row = new String[columnCount];
				for (int i=1; i<=columnCount; i++) {
					row[i - 1] = rs.getObject(i).toString();
				}
				output.add(row);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	
	//based on a hashed user Token it outputs the user info
	protected String[] importProfileWithToken(String token) {
		return getOutputFromIdentifier("Token", token, "users");
	}
	
	//based on a hashed Username, Password, and ID  it outputs the user info
	protected String[] importProfileWithUserData(String email, String password) {
		String[] data = getOutputFromIdentifier("Email", email, "users");
		
		if(data != null && password.equals(data[4])) {
			return data;
		}
		return null;
	}
	
}
