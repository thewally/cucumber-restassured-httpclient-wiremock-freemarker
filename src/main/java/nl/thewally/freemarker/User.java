package nl.thewally.freemarker;

import org.eclipse.jetty.util.StringUtil;

import java.util.List;

public class User {
    private int id;
    private String lastName;
    private String firstName;
    private String streetName;
    private int houseNumber;
    private String postalCode;
    private String city;
    private List<Book> books;

    public User(int id, String lastName, String firstName, String streetName, int houseNumber, String postalCode, String city) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
    }

    public Object getItem(String item){
        Object returnVal = null;
        if(item.toLowerCase().equals("id")) {
            returnVal = getId();
        } else if(item.toLowerCase().equals("lastname")) {
            returnVal = getLastName();
        } else if(item.toLowerCase().equals("firstname")) {
            returnVal = getFirstName();
        } else if(item.toLowerCase().equals("streetname")) {
            returnVal = getStreetName();
        } else if(item.toLowerCase().equals("housenumber")) {
            returnVal = getHouseNumber();
        } else if(item.toLowerCase().equals("postalcode")) {
            returnVal = getPostalCode();
        } else if(item.toLowerCase().equals("city")) {
            returnVal = getCity();
        } else if(item.toLowerCase().equals("books")) {
            returnVal = getBooks();
        } else {
            returnVal = "unknown";
        }
        return returnVal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
