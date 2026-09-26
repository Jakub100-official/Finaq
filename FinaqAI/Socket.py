import socket as sc
import numpy as np
import joblib
import tensorflow as tf

class Socket:
    training = 0
    host = "0.0.0.0"
    #Connects with clients, checks for username and a password, if correct returns a result of the NN
    def create_socket(self):
        with sc.socket(sc.AF_INET, sc.SOCK_STREAM) as s:
            s.bind((self.host, 5000))
            s.listen()
            print("Server Started")
            while True:
                conn, address = s.accept()
                with conn:
                    print(f"Connected with {address}")
                    username_password = conn.recv(1024).decode().strip()
                    passed = bool(username_password == "root:Password12345")
                    if passed:
                        conn.send((str(passed) +  "\n").encode())
                        question = conn.recv(1024).decode().strip()
                        if "GET_CS:" in question:
                            question = question.removeprefix("GET_CS:")
                            data = question.split()
                            data = np.array([float(x) for x in data])
                            print("Data:", data)
                            data = data.reshape(1, -1)
                            result = self.training.get_results(data)
                            print("Result:", result)
                            conn.send(((str(float(result[0][0])) +  "\n")).encode())



    def __init__(self, training):
        self.training = training
        self.create_socket()