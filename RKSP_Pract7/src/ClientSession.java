import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientSession implements Runnable{

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String request;

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        request = null;
    }

    @Override
    public void run() {
        try {
            //Getting header from client
            String header = readHeader();
            System.out.println(header);
            getRequest(header);
            if(request.equals("GET")){
                //Getting argument form the link
                String roman = getArgumentFromHeader(header);
                if(!roman.isEmpty()) {
                    System.out.println("Got roman number: " + roman);

                    //Calculating Arabic number
                    int arabic = Calculate(roman);

                    //Sending resource to the client
                    int code = send(roman, arabic);
                    System.out.println("Sent back result. Code: " + code + "\n");
                } else {
                    int code = send();
                    System.out.println("Sent back page. Code: " + code + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty())
                break;
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private void getRequest(String header){
        int from = 0;
        int to = header.indexOf(" ");
        request = header.substring(from, to);
        System.out.println("Request: " + request);
    }

    private String getArgumentFromHeader(String header){
        int from = header.indexOf("?") + 1;
        int to = header.indexOf(" ", from);
        System.out.println("from: " + from);
        System.out.println("to: " + to);
        if(to == -1 || from == 0){
            return "";
        } else {
            return header.substring(from, to);
        }
    }

    private int Calculate(String roman){
        roman=roman.toUpperCase();
        int i = 0; // Position in the Roman string
        int current = 0; // the current Roman numeral character to Arabic
        int previous = 0;
        int arabic = 0;

        while (i < roman.length()){
            char letter = roman.charAt(i);
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
        return arabic;
    }

    private int send(String roman, int arabic) throws IOException {
        int code = 200;
        String answer = getAnswer(code, roman, arabic);
        PrintWriter prStream = new PrintWriter(out, false);
        prStream.print(answer);
        prStream.flush();
        return code;
    }

    private int send(){
        int code = 200;
        String page = getPage(code);
        PrintWriter prStream = new PrintWriter(out, false);
        prStream.print(page);
        prStream.flush();
        return code;
    }

    private String getPage(int code){
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1" + code + " OK" + "\n");
        buffer.append("Date: " + new Date().toGMTString() + "\n");
        buffer.append("Connection: keep-alive" + "\n");
        buffer.append("Content-Type: text.html; charset=utf-8" + "\n");
        buffer.append("\n");
        buffer.append("<html>\n\t<head>\n\t\t");
        buffer.append("<title>Roman to Arabic</title>" + "\n\t");
        buffer.append("<meta charset=\"UTF-8\"/>" + "\n\t");
        buffer.append("</head>\n\t<body>\n\t\t");
        buffer.append("<p>Работу выполнил: Чехуров Денис Александрович</p>");
        buffer.append("<p>Номер группы: ИКБО-12-18</p>");
        buffer.append("<p>Номер индивидуального задания: 12</p>");
        buffer.append("<p>Текст индивидуального задания: \"Перевод римских чисел в арабские\"</p>");
        buffer.append("<hr>");
        buffer.append("</body>\n</html>");
        return buffer.toString();
    }

    private String getAnswer(int code, String roman, int arabic) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1" + code + " OK" + "\n");
        buffer.append("Date: " + new Date().toGMTString() + "\n");
        buffer.append("Connection: keep-alive" + "\n");
        buffer.append("Content-Type: text.html; charset=utf-8" + "\n");
        buffer.append("\n");
        buffer.append("<html>\n\t<head>\n\t\t");
        buffer.append("<title>Roman to Arabic</title>" + "\n\t");
        buffer.append("<meta charset=\"UTF-8\"/>" + "\n\t");
        buffer.append("</head>\n\t<body>\n\t\t");
        buffer.append("<p>Работу выполнил: Чехуров Денис Александрович</p>");
        buffer.append("<p>Номер группы: ИКБО-12-18</p>");
        buffer.append("<p>Номер индивидуального задания: 12</p>");
        buffer.append("<p>Текст индивидуального задания: \"Перевод римских чисел в арабские\"</p>");
        buffer.append("<hr>");
        buffer.append("<p>The Arabic equivalent of Roman number " + roman + " is " + arabic + "</p>" + "\n\t");
        buffer.append("</body>\n</html>");
        return buffer.toString();
    }
}
