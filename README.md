# Finaq
Financial Assistant and Advisor

Finaq is a personal finance and budgeting planner and organizer. The GUI is made using JavaFX and Scene Builder with FXML files.
It helps users track income and expenses, manage savings goals, create budgets,
and receive financial insights based on their transaction history.

The top right corner is dedicated to a profile that allows the user to edit his personal information
The right side panel contains 6 buttons leading to 6 panels each covering the center and the left part

Panels:
Dashboard - shows essential numbers from other panels, recent transactions and upcoming bills
Income - displays sources of income, graph of income changes throughout the month
Expenses - show sources and amounts of expenses and debts also graphically
Savings - allow users to create saving goals and store their money in each; when fulfilled user can start spending
Budgeting - allows users track their spending based on categories, projects budget health score and graph of Wants Needs and Savings
AI Advisor - gives suggestions based on given data, makes educated predictions of future fin. situation, estimates credit score

Logins and Signings:
User logs in with their email and password, verified by the server. A temporary token (valid for one month) is created and stored locally.
Every month the user has to relog in.

Database:
Stores personal and general financial user data in 2 separate tables.
For each user a set of 3 tables is created for budgeting, transactions and savings.
Each holding data stored for longer periods of time and tracked by user's ID.
All of the data is being stored on a server created using MySQL and Xampp.
Published using Ngrok.

Neural Network
The neural network is built using Python's Tensorflow.
Trained and tested on data from Credit Score Database: https://www.kaggle.com/datasets/khushikyad001/personal-finance-tracker-dataset/data
The model calulates its result from a saved Keras mode and a saved Scaler created and saved while training.
Model was trained by repeating 100 epochs and then saving the keras, scaler and the score based on a tested accuracy.
Data is shared with the Finaq app using Python Socket.
Published using a separate Ngrok.

Technologies used:
Java
JavaFX
FXML
Scene Builder
MySQL
XAMPP
Python
TensorFlow
Ngrok
