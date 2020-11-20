import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Configuration;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientSession implements Runnable{
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final String INDEX_HTML = "http/index.html";
    private final String CALCULATE_HTML = "http/calculate.html";
    private final String CALC_JS = "js/calc.js";
    private final String CALC_CSS = "css/calc.css";
    private final String CARS_TABLE_HTML = "http/carsTable.html";
    private final String CARS_JS = "js/cars.js";
    private final String ERROR_HTML = "http/error.html";


    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            String header = readHeader();
            System.out.println(header);
            String requestType = getRequestType(header);
            if(!header.isEmpty())
                send(requestType, header);
        } catch (IOException | TemplateException e) {
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
        while(reader.ready()){
            builder.append((char) reader.read());
        }
        return builder.toString();
    }

    private String getRequestType(String header){
        int from = 0;
        int to = header.indexOf(" ");
        String request = header.substring(from, to);
        System.out.println("Request: " + request);
        return request;
    }

    private int send(String reqType, String header) throws IOException, TemplateException {
        if(reqType.equals("GET"))           return GET(header);
        else if(reqType.equals("POST"))     return POST(header);
        else {
            System.out.println("Unknown request type");
            return 400;
        }
    }

    private int GET(String header) throws IOException, TemplateException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        InputStream resStream;
        BufferedReader br;

        String resource = getResource(header);
        String resourcePath = getResPath(resource);

        int code = getCode(resourcePath);
        String headerAnswer = getHeader(code);
        bw.write(headerAnswer);

        if(resourcePath.equals(INDEX_HTML)){
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(this.getClass(), "/");
            Map<String, Object> root = new HashMap<>();
            root.put("info", "<p>Работу выполнил: Чехуров Денис Александрович</p>\n" +
                                    "<p>Номер группы: ИКБО-12-18</p>\n" +
                                    "<p>Номер индивидуального задания: 12</p>\n" +
                                    "<p>Текст индивидуального задания: \"Перевод римских чисел в арабские\"</p>");
            Template temp = cfg.getTemplate(resourcePath);
            temp.process(root, bw);
            bw.flush();
        } else if((resStream = HttpServer.class.getResourceAsStream(resourcePath)) != null
                && (code == 200 || code == 404)){
            br = new BufferedReader(new InputStreamReader(resStream));
            String line = br.readLine();
            while(line != null){
                bw.write(line);
                line = br.readLine();
            }
            bw.flush();
            br.close();
            resStream.close();
        }
        bw.close();
        System.out.println("Sent " + resource + " | Code: " + code + "\n");
        return code;
    }

    private int POST(String header) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        String resource = getResource(header);
        String resourcePath = getResPath(resource);

        int code = getCode(resourcePath);

        Map<String, String> params = getParamsFromHeader(header);
        if(!params.isEmpty()){
            String headerAnswer = getHeader(code);
            bw.write(headerAnswer);
            if(params.containsKey("romanNumber")){
                int arabic = calculate(params.get("romanNumber"));
                System.out.println("Roman " + params.get("romanNumber") + " -> Arabic " + arabic);
                bw.write("Результат: " + arabic);
                bw.flush();
            } else if(params.containsKey("type")){
                if(params.get("type").equals("add")){
                    String price = params.get("price");
                    String color = params.get("color");
                    String year = params.get("year");
                    //String id = DataManager.getInstance().getAvailableId();
                    //DataManager.getInstance().getCarsList().add(new Car(id, price, color, year));

                    int id = DataManager.getInstance().getAvailableId();//Ищет максиамльный id среди всех записей
                    DataManager.getInstance().addNewCar(id, price, color, year); //Добавляет запись по id

                    bw.write(Integer.toString(id));
                    bw.flush();
                } else if (params.get("type").equals("downloadTable")){
                    String htmlTable = DataManager.getInstance().getHtmlTable();

                    //Нужет метод "получить все записи"

                    bw.write(htmlTable);
                    bw.flush();
                } else if (params.get("type").equals("delete")){
                    int id = Integer.parseInt(params.get("id"));

                    boolean res = DataManager.getInstance().deleteById(id);

                    if(res) bw.write("Deleted");
                    else bw.write("Something gone wrong");
                } else if (params.get("type").equals("update")){
                    int id = Integer.parseInt(params.get("id"));
                    String price = params.get("price");
                    String color = params.get("color");
                    String year = params.get("year");

                    boolean res = DataManager.getInstance().updateById(id, price, color, year);

                    if (res) bw.write("Updated");
                    else bw.write("Something gone wrong");
                }
            }
        }
        System.out.println("Sent data for " + resource + " | Code: " + code + "\n");
        bw.close();
        return code;
    }

    private String getResource(String header){
        int from = header.indexOf(" ") + 1;
        int to = header.indexOf(" ", from);
        String resource = header.substring(from, to);
        from = resource.lastIndexOf("/");
        resource = resource.substring(from);
        int paramIndex = resource.indexOf("?");
        if(paramIndex != -1)
            resource = resource.substring(0, paramIndex);
        System.out.println("Resource: " + resource);
        return resource;
    }

    private String getResPath(String resource){
        if (resource.equals("/") || resource.equals("/index.html")) return INDEX_HTML;
        else if (resource.equals("/calculate.html"))                return CALCULATE_HTML;
        else if (resource.equals("/calc.js"))                       return CALC_JS;
        else if (resource.equals("/calc.css"))                      return CALC_CSS;
        else if (resource.equals("/carsTable.html"))                return CARS_TABLE_HTML;
        else if (resource.equals("/cars.js"))                       return CARS_JS;
        else                                                        return ERROR_HTML;
    }

    private int getCode(String resource){
        if(resource.equals(INDEX_HTML) || resource.equals(CALCULATE_HTML) ||
                resource.equals(CALC_CSS) || resource.equals(CALC_JS) ||
                resource.equals(CARS_TABLE_HTML) || resource.equals(CARS_JS))
            return 200;
        else if (resource.equals(ERROR_HTML))
            return 404;
        else
            return 500;
    }

    private String getHeader(int code){
        return "HTTP/1.1 " + code + " " + getAnswer(code) + "\n" +
                "Date: " + new Date() + "\n" +
                "Connection: keep-alive" + "\n" +
                "Content-Type: text.html; charset=utf-8" + "\n" +
                "\n";
    }

    private String getAnswer(int code){
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }

    private Map<String, String> getParamsFromHeader(String header){
        Map<String, String> params = new HashMap<String, String>();

        int from = header.lastIndexOf(System.getProperty("line.separator"));
        String strParams = header.substring(from);
        strParams = strParams.substring(1);

        int to;
        while((to = strParams.indexOf(" ")) != -1){
            from = 0;

            String subStr = strParams.substring(from, to);

            params.put(subStr.substring(0, subStr.indexOf("=")), subStr.substring((subStr.indexOf("=") + 1)));

            from = to + 1;
            strParams = strParams.substring(from);
        }
        params.put(strParams.substring(0, strParams.indexOf("=")), strParams.substring(strParams.indexOf("=") + 1));
        System.out.println(params);
        return params;
    }

    private int calculate(String roman){
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
}