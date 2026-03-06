package gui;

public class PRODUCT {
    protected String name;
    protected String type;

    public PRODUCT(String name, String type) {
        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Product{name='" + name + "', type='" + type + "'}";
    }
}
