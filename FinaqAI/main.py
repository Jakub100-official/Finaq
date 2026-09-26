import os

import kagglehub
import tensorflow as tf
import pandas as pd
from sklearn.preprocessing import StandardScaler
import joblib

from Socket import Socket


class Training:

    model = 0

    #Create a model
    def create_model(self, X_train):
        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=(X_train.shape[1],)),
            tf.keras.layers.Dense(64, activation="relu"),
            tf.keras.layers.Dense(32, activation="relu"),
            tf.keras.layers.Dense(8, activation="relu"),
            tf.keras.layers.Dense(1)
        ])
        return model

    #Update a model
    def update_model(self):
        return tf.keras.models.load_model("score.keras")

    #Save Keras and Scaler
    def save_model(self, model, scaler, score):
        model.save("score.keras")
        joblib.dump(scaler, "scaler.pkl")
        with open("score.txt", "w") as  fl:
            fl.write(str(score))
        print("MODEL SAVED!!!")

    #Check if the model made a progress and should / shouldn't be saved
    def check_for_saving(self, model, scaler, accuracy, new):
        old_accuracy = 0
        with open("score.txt", "r") as fl:
            old_accuracy = float(fl.readline())
        if new:
            self.save_model(model, scaler, accuracy)
        elif accuracy < old_accuracy:
            self.save_model(model, scaler, accuracy)
        else:
            print("MODEL NOT SAVED")


    #Fit the dataset to make it feedable to the model
    def fit_dataset(self, train_f, test_f, scaler, new):
        NUMERICAL_COLUMNS = ["monthly_income", "monthly_expense_total", "savings_rate", "budget_goal", "credit_score", "debt_to_income_ratio"]


        for col in NUMERICAL_COLUMNS:
            train_f[col] = pd.to_numeric(train_f[col])
            test_f[col] = pd.to_numeric(test_f[col])

        #Remove blank rows
        train_f = train_f.dropna()
        test_f = test_f.dropna()

        #Separate Results
        Y_train = train_f.pop("credit_score")
        X_train = train_f[["monthly_income", "monthly_expense_total", "savings_rate", "budget_goal", "debt_to_income_ratio"]]
        Y_test = test_f.pop("credit_score")
        X_test = test_f[["monthly_income", "monthly_expense_total", "savings_rate", "budget_goal", "debt_to_income_ratio"]]


        # Convert to float32
        X_train = X_train.astype("float32")
        X_test = X_test.astype("float32")

        # Normalize data
        if new:
            X_train = scaler.fit_transform(X_train)
        else:
            X_train = scaler.transform(X_train)
        X_test = scaler.transform(X_test)
        return X_train, Y_train, X_test, Y_test

    #Trains bakpropagates and tests a model and saves if the code imporves
    def train_test(self, new, train_f, test_f):
        scaler = self.get_scaler(new)
        X_train, Y_train, X_test, Y_test = self.fit_dataset(train_f, test_f, scaler, new)
        model = self.create_model(X_train) if new else self.update_model()
        model.compile(optimizer=tf.keras.optimizers.Adam(learning_rate=0.001), loss="mse", metrics=["mae"])
        self.model = model
        early_stop = tf.keras.callbacks.EarlyStopping(monitor="val_loss", patience=10, restore_best_weights=True)
        model.fit(X_train, Y_train, epochs=100, batch_size=32, validation_split=0.2, callbacks=[early_stop])  # batch_size = x samples before changing weights
        loss, mae= model.evaluate(X_test, Y_test)
        print("Mean absolute error:", mae)
        self.check_for_saving(model, scaler, mae, new)
        prediction = model.predict(X_test[0:5])
        print("Predictions:")
        print(prediction)

    #get Data Sets
    def get_data_sets(self):
        return pd.read_csv("personal_finance_tracker_dataset.csv"), pd.read_csv("test.csv")

    #Feeds the NN an input and returns the output
    def get_results(self, data):
        model = self.update_model()
        model.compile(optimizer=tf.keras.optimizers.Adam(learning_rate=0.001), loss="mse", metrics=["mae"])
        scaler = joblib.load("scaler.pkl")

        scaled_data = scaler.transform(data)

        result = model.predict(scaled_data)
        return result

if __name__ == "__main__":
    tr = Training()
    sc = Socket(tr)
    '''train_f, test_f = tr.get_data_sets()
    for i in range(20):
        tr.train_test(False, train_f, test_f)'''