
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Md. Mukitul Islam Ratul
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    static int count = 0;
    static ArrayList<ClientHandler> cList = new ArrayList<>();
    static ArrayList<String> loggedIn = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Server is running");
        //create a server
        ServerSocket server = new ServerSocket(9898);

        while (true) {
            //waiting for client
            Socket cs = server.accept();

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(cs.getOutputStream());

            while (true) {
                String response = inFromUser.readLine();
                System.out.println("response: " + response);
                String msg = "Please enter your detail here!!";
                outToClient.writeBytes(msg + "\n");

                String userInfo = inFromUser.readLine();
                System.out.println("user info: " + userInfo);
                StringTokenizer token = new StringTokenizer(userInfo, ":");
                String clientName = token.nextToken();
                String clientPass = token.nextToken();

                FileWriter fw = new FileWriter("userAccount.txt", true);
                if (response.equals("Registration")) {
                    try (BufferedReader bf = new BufferedReader(new FileReader("userAccount.txt"))) {
                        String line = bf.readLine();
                        while (line != null) {
                            StringTokenizer token1 = new StringTokenizer(line, ":");
                            String name = token1.nextToken();
                            System.out.println(name);
                            if (name.equals(clientName)) {
                                count++;
                                break;
                            }
                            line = bf.readLine();
                        }
                    }
                    if (count > 0) {

                        count = 0;
                        String msg1 = "Username already exits. Try a new one!!";
                        outToClient.writeBytes(msg1 + "\n");
                    } else {
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(userInfo);
                        bw.newLine();
                        bw.close();

                        String msg2 = "New Account Created!! You are now logged in.";
                        outToClient.writeBytes(msg2 + "\n");

                        loggedIn.add(clientName);
                        //send it to a thread
                        ClientHandler c1 = new ClientHandler(cs, clientName, inFromUser, outToClient);
                        cList.add(c1);
                        System.out.println(cList);
                        c1.start();
                        break;
                    }
                } else if (response.equals("Login")) {
                    try (BufferedReader bf = new BufferedReader(new FileReader("userAccount.txt"))) {
                        String line = bf.readLine();
                        while (line != null) {

                            if (line.equals(userInfo)) {
                                count++;
                            }
                            line = bf.readLine();
                        }
                    }
                    if (count > 0) {
                        count = 0;
                        String msg1 = "Login Successful !";
                        outToClient.writeBytes(msg1 + "\n");

                        loggedIn.add(clientName);
                        //send it to a thread
                        ClientHandler c1 = new ClientHandler(cs, clientName, inFromUser, outToClient);
                        cList.add(c1);
                        System.out.println(cList);
                        c1.start();
                        break;
                    } else {
                        String msg1 = "Invalid Username or password!";
                        outToClient.writeBytes(msg1 + "\n");
                    }
                }
            }

        }

    }

}
