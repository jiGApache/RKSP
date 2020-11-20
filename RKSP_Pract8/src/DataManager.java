import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;

    public static DataManager getInstance(){
        if(instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    private List<Car> cars;
    private List<Integer> ids;

    private DataManager(){
        cars = new ArrayList<>();
        ids = new ArrayList<>();
    }

    public String getAvailableId(){
        int id = 0;
        while(ids.contains(id)){
            id++;
            System.out.println("Id: " + id);
        }
        ids.add(id);
        return Integer.toString(id);
    }

    public boolean removeCarById(String id){
        boolean res = false;
        for (Car car : cars){
            if(car.getId().equals(id)){
                cars.remove(car);
                ids.remove(Integer.parseInt(id));
                res = true;
                break;
            }
        }
        return res;
    }

    public boolean updateCarById(String id, String price, String color, String year){
        boolean res = false;
        for (Car car : cars){
            if(car.getId().equals(id)){
                car.setColor(color);
                car.setPrice(price);
                car.setYear(year);
                res = true;
            }
        }
        return res;
    }

    public String getHtmlTable(){
        String htmlTable = "";
        for (int i = 0; i < cars.size(); i++){
            htmlTable += "<tr id=\"" + cars.get(i).getId() + "\">" +
                    "<td>" + cars.get(i).getPrice() + "</td>" +
                    "<td>" + cars.get(i).getColor() + "</td>" +
                    "<td>" + cars.get(i).getYear() + "</td>" +
                    "<td><input type=\"button\" value=\"изменить\" onclick=\"selectRow(this.parentElement.parentElement)\"/></td>" +
                    "<td><input type=\"button\" value=\"удалить\" onclick=\"deleteRow(this.parentElement.parentElement)\"/></td>" +
                    "</tr>";

        }
        return htmlTable;
    }

    public List<Integer> getIds(){
        return ids;
    }

    public List<Car> getCarsList(){
        return cars;
    }
}
