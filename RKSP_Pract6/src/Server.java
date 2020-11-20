import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket server;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args){
        try{
            try{
                server = new ServerSocket(12345);
                System.out.println("The server is running!");


                clientSocket = server.accept();
                try{
                    System.out.println("The client accepted!");
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));




                    String word = in.readLine();
                    System.out.println("The client sent me message: " + word);
                    word=word.toUpperCase();
                    int i = 0; // Position in the Roman string
                    int current = 0; // the current Roman numeral character to Arabic
                    int previous = 0;
                    int arabic = 0; // the equivalent of the Roman string

                    while (i < word.length()){
                        char letter = word.charAt(i);
                        switch(letter){
                            case ('I'):
                                current = 1;
                                break;
                            case ('V'):
                                current = 5;
                                break;
                            case ('X'):
                                current = 10;
                                break;
                            case ('L'):
                                current = 50;
                                break;
                            case ('C'):
                                current = 100;
                                break;
                            case ('D'):
                                current = 500;
                                break;
                            case ('M'):
                                current = 1000;
                                break;
                        }
                        if(current > previous)
                            arabic += current - (previous * 2);
                        else
                            arabic += current;
                        previous = current;
                        i++;
                    }
                    System.out.println("The result: " + arabic);



                    System.out.println("Sending message back to the client!");
                    out.write("The Arabic equivalent of Roman number " + word + " is " + arabic + "\n");
                    out.flush();
                } finally {
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("The server is down!");
                server.close();
            }
        } catch (IOException e){
            System.err.println(e);
        }
    }
}
