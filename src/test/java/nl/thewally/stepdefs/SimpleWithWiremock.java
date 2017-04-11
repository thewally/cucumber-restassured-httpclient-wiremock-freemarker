package nl.thewally.stepdefs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.thewally.freemarker.Book;
import nl.thewally.freemarker.TemplateHandler;
import nl.thewally.freemarker.User;
import nl.thewally.stub.Stub;
import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

public class SimpleWithWiremock {

    @Rule
    public WireMockRule generic = new WireMockRule(8888);
    private final Stub stub = new Stub();


    private List<User> users;
    private List<Book> books;

    @Before
    public void prepare() {
        stub.start(generic, 8888);
    }

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


        TemplateHandler template = new TemplateHandler();
        template.setTemplate("responses/getBooksForUsers.response.xml.ftl");
        stub.setResponse(200, "/getBooksForUsers", "getBooksForUsers", template);


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
