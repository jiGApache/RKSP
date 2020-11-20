public class Car {
    private String id;
    private String price;
    private String color;
    private String year;

    public Car(String id, String price, String color, String year) {
        this.id = id;
        this.price = price;
        this.color = color;
        this.year = year;
    }

    public String getId() {
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
