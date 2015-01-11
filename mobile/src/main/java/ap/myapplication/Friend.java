package ap.myapplication;

/**
 * Created by Krish on 1/11/2015.
 */
public class Friend {
    public String id;
    public String name;
    private double price;

    public Friend(){
        super();
    }

    public Friend(String id, String name) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.id;
    }
}
