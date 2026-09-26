package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AIServerConnector {
	
	protected double result = 0;
	private String host = "ngrok connector";
	
	private boolean numberIsVaild(double num) {
		return Double.isFinite(num) && !Double.isNaN(num);
	}
	
	protected AIServerConnector(double monthlyIncome, double monthlyExpenses, double savingsRate, double budgetGoal, double debtToIncomeRatio) {
		if(numberIsVaild(monthlyIncome) && numberIsVaild(monthlyExpenses) && numberIsVaild(savingsRate) && numberIsVaild(budgetGoal) && numberIsVaild(debtToIncomeRatio)) {
		
			 try {
				Socket socket = new Socket(host, 00000);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            out.println("username:password");
	            boolean in = Boolean.parseBoolean(br.readLine());
	            if(in) {
	            	System.out.println("GET_CS:"+monthlyIncome+" "+monthlyExpenses+" "+savingsRate+" "+budgetGoal+" "+debtToIncomeRatio+" ");
	            	out.println("GET_CS:"+monthlyIncome+" "+monthlyExpenses+" "+savingsRate+" "+budgetGoal+" "+debtToIncomeRatio+" ");
	            	result = Double.parseDouble(br.readLine());
	            	result = Math.round(result * 100.0) / 100.0;
	            }
			 } catch (UnknownHostException e) {
					result = 0;
			 } catch (IOException e) {
					result = 0;
			 }
		}
		
		else {
			result = 0;
		}
	}
	
}
