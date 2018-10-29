
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Md. Mukitul Islam Ratul
 */
public class Client {

    public static void main(String[] args) throws IOException {
        boolean notDone = true;
        System.out.println("Client is running");
        //create connection with server
        Socket socket = new Socket("localhost", 9898);

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (notDone) {

            //input from console
            System.out.println("Registration || Login");
            System.out.println("Enter Your Response:");
            String response = inFromUser.readLine();
            //send the info to server
            outToServer.writeBytes(response + '\n');
            //receive message from server
            String inputFromServer = inFromServer.readLine();
            System.out.println("From Server:" + inputFromServer);

            //input from console
            System.out.println("Enter Name:");
            String userName = inFromUser.readLine();

            //input from console
            System.out.println("Enter Password:");
            String userPass = inFromUser.readLine();

            //send the info to server
            outToServer.writeBytes(userName + ':' + userPass + '\n');

            //receive message from server
            String inputFromServer2 = inFromServer.readLine();
            System.out.println("From Server:" + inputFromServer2);
            if (inputFromServer2.equals("Username already exits. Try a new one!!")) {
                notDone = true;
            } else if (inputFromServer2.equals("Invalid Username or password!")) {
                notDone = true;
            } else {
                notDone = false;
            }
        }

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    //input from console
                    System.out.println("Enter Message:");
                    try {
                        String userMSG = inFromUser.readLine();
                        //send the message to server
                        outToServer.writeBytes(userMSG + '\n');
                    } catch (IOException ex) {
                        System.out.println("You are currently logged out. Please run this program again to Sign In. !");
                    }

                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String receivedMsg = inFromServer.readLine();

                        if (receivedMsg == null) {
                            break;
                        } else {
                            System.out.println(receivedMsg);
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
