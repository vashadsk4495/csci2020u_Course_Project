package game;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Main extends Application {

    //Variables
    private TextArea textArea = new TextArea();
    private int clientNumber = 0;

    @Override
    public void start(Stage primaryStage) {

        //Set scene
        Scene scene = new Scene(new ScrollPane(textArea), 450, 200);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Creating server
        new Thread( () -> {
            try {

                ServerSocket serverSocket = new ServerSocket(8000); //create serversocket

                textArea.appendText("Server started at " + new Date() + '\n');

                while (true) {

                    //Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    clientNumber++; //increase client no

                    Platform.runLater( () -> {

                        textArea.appendText("A new client is connected to this server!" + '\n');
                        textArea.appendText(" - Starting thread for client " + clientNumber + " at " + new Date() + '\n');

                        //instance of InetAddress for the client on the socket
                        InetAddress inetAddress = socket.getInetAddress();
                        //get client's host name and Ip address
                        textArea.appendText(" - Client " + clientNumber + "'s Host name is " + inetAddress.getHostName() + '\n');
                        textArea.appendText(" - Client " + clientNumber + "'s IP address is " + inetAddress.getHostAddress() + '\n' + '\n');
                    });

                    new Thread(new clientHandler(socket)).start();
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    class clientHandler implements Runnable {
        private Socket socket;

        public clientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true) {

                    // Receive finalscore from the client
                    int finalScore = inputFromClient.readInt();

                    //Send  back to the client
                    String message = "Thanks for playing!";
                    outputToClient.writeUTF(message);

                    Platform.runLater(() -> {
                        textArea.appendText("Final Score was " + finalScore + '\n' + '\n');
                    });
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}