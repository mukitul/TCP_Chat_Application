
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class ClientHandler extends Thread {

    private String name;
    final BufferedReader dis;
    final DataOutputStream dos;
    int index;
    Socket s;
    boolean isloggedin;
    ArrayList<String> Friends = new ArrayList<>();

    public ClientHandler(Socket s, String name, BufferedReader dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;

    }

    public void run() {
        String received;
        while (true) {
            try {
                // receive the string
                received = dis.readLine();

                System.out.println(received);
                if (received.equals("logout")) {
                    Server.loggedIn.remove(name);
                    this.isloggedin = false;
                    this.dos.writeBytes("Logged out \n");
                    this.s.close();

                    break;
                } else if (received.equals("my online friends")) {

                    for (ClientHandler mc : Server.cList) {
                        // if the recipient is found, write on its
                        // output stream

                        if (mc.name.equals(name)) {
                            System.out.println("Serving for :" + mc.name);
                            dos.writeBytes(Friends + "\n");
                            break;
                        }
                    }
                } else if (received.equals("show all users")) {

                    dos.writeBytes(Server.loggedIn + "\n");

                    //dos.writeBytes(Server.loggedIn + "\n");
                } else {
                    // break the string into message and recipient part
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String MsgToSend = st.nextToken();
                    String recipient = st.nextToken();
                    if (MsgToSend.equals("sfr")) {
                        //june ---> may
                        for (ClientHandler mc : Server.cList) {
                            // if the recipient is found, write on its
                            // output stream

                            if (mc.name.equals(recipient) && mc.isloggedin == true) {
                                mc.dos.writeBytes("Friend Request : " + this.name + " wants to be your friend. Be FRIEND?\n");
                                mc.Friends.add(this.name);
                                break;
                            }
                        }

                    }
                    if (MsgToSend.equals("frnd")) {
                        //ratul ---> mukit
                        for (ClientHandler mc : Server.cList) {
                            // if the recipient is found, write on its
                            // output stream

                            if (mc.name.equals(recipient) && mc.isloggedin == true) {
                                mc.dos.writeBytes("Friend Request accepted by: " + this.name + "\n");
                                mc.Friends.add(this.name);

                                break;
                            }
                        }
                    } else if (MsgToSend.equals("rjct")) {
                        for (ClientHandler mc : Server.cList) {
                            // if the recipient is found, write on its
                            // output stream

                            if (mc.name.equals(recipient) && mc.isloggedin == true) {
                                mc.dos.writeBytes("Friend Request rejected by: " + this.name + "\n");
                                break;
                            }
                        }
                    }
                    if (recipient.equals("all")) {
                        for (ClientHandler mc : Server.cList) {
                            // if the recipient is found, write on its
                            // output stream
                            for (String fname : Friends) {
                                if (mc.name.equals(fname)) {
                                    mc.dos.writeBytes(this.name + " : " + MsgToSend + "\n");
                                }
                            }

                        }
                    } else if (!MsgToSend.equals("sfr") || !MsgToSend.equals("frnd") || !MsgToSend.equals("rjct")) {
                        // search for the recipient in the connected devices list.
                        // ar is the vector storing client of active users
                        String[] reciever = recipient.split(":");

                        for (ClientHandler mc : Server.cList) {
                            // if the recipient is found, write on its
                            // output stream
                            for (int i = 0; i < reciever.length; i++) {

                                for (String fname : Friends) {
                                    if (mc.name.equals(reciever[i]) && mc.name.equals(fname)) {
                                        mc.dos.writeBytes(this.name + " : " + MsgToSend + "\n");
                                        break;
                                    }
                                }

                            }

                        }
                    }

                }

            } catch (IOException e) {

            }

        }
        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {

        }
    }
}
