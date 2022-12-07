import java.io.BufferedInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketApp {

    public static void main(String[] args) {

        String usage = """
                Usage: Server
                <program> <server> <port> <cookie-file.txt>
                Usage: Client
                <program> <client> <host> <port>
                """;

        if ((args.length) != 3) {
            System.out.println("Incorrect Inputs. Please check the following usage.");
            System.out.println(usage);
            return;
        }

        String type = args[0];
        if (type.equalsIgnoreCase("server")) {
            int port = Integer.parseInt(args[1]);
            String fileName = args[2];
            StartServer(port, fileName);
        } else if (type.equalsIgnoreCase("client")) {
            String hostName = args[1];
            int port = Integer.parseInt(args[2]);
            StartClient(hostName, port);
        } else {
            System.out.println("Incorrect Arguments");
        }
    }

    public static void StartServer(int port, String fileName) {
        ServerSocket server;
        
        try {
            server = new ServerSocket(port);
            Socket socket = server.accept();

            // IN
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream()); 
            DataInputStream dis = new DataInputStream(bis);

            // OUT
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                String fromClient = dis.readUTF();

                if (fromClient.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.println("LOG: msg from client: " + fromClient);

                if (fromClient.equalsIgnoreCase("get-cookie")) {
                    // send a random cookie from file
                    dos.writeUTF("Dummy cookie"); // ******* edit ****
                    dos.flush();
                } else {
                    // Invalid command from server
                    dos.writeUTF("From server: Invalid Command");
                    dos.flush();
                }
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void StartClient(String host, int port) {
        try {
            Socket socket = new Socket(host, port); //***** fix */

            // IN 
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 

            // OUT
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            Scanner sc = new Scanner(System.in);
            boolean stop = false;

            while (!stop) {
                String line = sc.nextLine();
                if (line.equalsIgnoreCase("exit")) {
                    dos.writeUTF("exit");
                    dos.flush();
                    stop = true;
                    break;
                }

                dos.writeUTF(line);
                dos.flush();

                String fromServer = dis.readUTF();
                System.out.println("Resp from server " + fromServer);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}