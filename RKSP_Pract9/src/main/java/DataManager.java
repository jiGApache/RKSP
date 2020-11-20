import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class DataManager {
    private static DataManager instance;

    public static DataManager getInstance(){
        if(instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    private DataManager(){
        //cars = new ArrayList<>();
    }

    private List<Car> findAll(){
        List<Car> cars = (List<Car>) HibernateSessionFactoryUtil
                                            .getSessionFactory()
                                            .openSession()
                                            .createQuery("From Car")
                                            .list();
        return cars;
    }

    private Car findById(int id){
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Car.class, id);
    }

    public int getAvailableId(){
        int id = 0;
        List<Car> cars= findAll();
        for(Car car : cars){
            if(car.getId() >= id)
                id = car.getId() + 1;
        }
        return id;
    }

    public void addNewCar(int id, String price, String color, String year){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(new Car(id, price, color, year));
        tx.commit();
        session.close();
    }

    public boolean deleteById(int id){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Car car = findById(id);
        session.delete(car);
        tx.commit();
        session.close();
        return true;
    }

    public boolean updateById(int id, String price, String color, String year){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Car car = findById(id);
        session.evict(car);
        car.setPrice(price);
        car.setColor(color);
        car.setYear(year);
        session.update(car);
        tx.commit();
        session.close();
        return true;
    }

    public String getHtmlTable(){
        String htmlTable = "";
        List<Car> cars = findAll();
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
}
