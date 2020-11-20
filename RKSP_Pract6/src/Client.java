import java.io.*;
import java.net.Socket;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedWriter out;
    private static BufferedReader in;

    private  static final int    serverPort = 12345;
    private  static final String localhost  = "127.0.0.1";

    public static void main(String[] args){
        try {
            try {
                System.out.println("Welcome to Client side\n" +
                        "Connecting to the server\n\t" +
                        "(IP address " + localhost +
                        ", port " + serverPort + ")");
                clientSocket = new Socket("Localhost", serverPort);
                System.out.println("The connection is established!");
                System.out.println(
                        "\tLocalPort = " +
                                clientSocket.getLocalPort() +
                                "\n\tInetAddress.HostAddress = " +
                                clientSocket.getInetAddress()
                                        .getHostAddress() +
                                "\n\tReceiveBufferSize (SO_RCVBUF) = "
                                + clientSocket.getReceiveBufferSize());


                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.print("Enter Roman number here: ");
                String Roman = reader.readLine();
                while(Roman.matches(".*[0-9].*") || !Roman.matches("[M|D|C|L|X|V|I]*")){
                    System.out.print("Wrong Roman number! Try again: ");
                    Roman = reader.readLine();
                }
                System.out.println("Sending entered Roman number to server!");
                out.write(Roman + "\n");
                out.flush();

                String serverWord = in.readLine();
                System.out.println("The server sent me this message:\n\t" + serverWord);
            } finally {
                System.out.println("Client was closed...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e){
            System.err.println(e);
        }
    }
}
