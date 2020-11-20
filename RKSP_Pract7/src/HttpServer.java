import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static int PORT = 8080;

    public static void main(String[] args){

        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port: " +
                        serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + PORT + " is blocked.");
            System.exit(-1);
        }

        while (true){
            try{
                Socket clientSocket = serverSocket.accept();
                ClientSession session = new ClientSession(clientSocket);
                new Thread(session).run();
            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
