import javax.persistence.*;

@Entity
@Table (name = "cars")
public class Car {

    @Id
    private int id;

    @Column(name = "price")
    private String price;

    @Column(name = "color")
    private String color;

    @Column(name = "year")
    private String year;

    public Car(){}

    public Car(int id, String price, String color, String year) {
        this.id = id;
        this.price = price;
        this.color = color;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                "price='" + price + '\'' +
                ", color='" + color + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
