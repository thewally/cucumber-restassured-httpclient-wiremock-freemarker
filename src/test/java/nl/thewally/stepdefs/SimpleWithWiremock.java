package nl.thewally.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.thewally.freemarker.Book;
import nl.thewally.freemarker.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arjen on 10-4-17.
 */
public class SimpleWithWiremock {

    private List<User> users;
    private List<Book> books;

    @Given("^users are available$")
    public void usersAreAvailable(List<User> users) throws Throwable {
        this.users = users;
    }

    @Given("^books are available$")
    public void booksAreAvailable(List<Book> books) throws Throwable {
        this.books = books;
    }

    @Given("^user (\\d+) has books with id$")
    public void userHasBooksWithId(int userId, List<Integer> bookIds) throws Throwable {
        List<Book> addingBooks = new ArrayList<>();
        for(int bookId:bookIds) {
            for(Book book:books) {
                if(book.getId()==bookId) {
                    addingBooks.add(book);
                }
            }
        }

        for(User user:users) {
            if(user.getId() == userId) {
                user.setBook(addingBooks);
            }
        }
    }

    @Given("^service getBooksForUsers returns response for all users and for each user$")
    public void serviceGetBooksForUsersReturnsResponseForAllUsersAndForEachUser() throws Throwable {

    }

    @When("^send request message to service getBooksForUsers for (user (\\d+)|all users)$")
    public void sendRequestMessageToServiceGetBooksForUsersForUser(int user) throws Throwable {
        if(user != 0) {

        } else {

        }
    }

    @Then("^getBookForUser returns for (user (\\d+)|all users) with their own books$")
    public void getbookforuserReturnsForUserWithTheirOwnBooks(int user) throws Throwable {
        if(user != 0) {

        } else {

        }
    }
}
