package nl.thewally.stepdefs;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import cucumber.api.PendingException;
import cucumber.api.java.After;
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
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class SimpleWithWiremock {

    @Rule
    public WireMockRule generic = new WireMockRule(8888);

    private List<User> users;
    private List<Book> books;

    @Before
    public void prepare() {
        generic.start();
        WireMock.configureFor("localhost", 8888);
        WireMock.reset();
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
                user.setBooks(addingBooks);
            }
        }
    }

    @Given("^service getBooksForUsers returns response for all users and for each user$")
    public void serviceGetBooksForUsersReturnsResponseForAllUsersAndForEachUser() throws Throwable {
        TemplateHandler template = new TemplateHandler();
        for(User user:users) {
            template.setTemplate("responses/getBooksForUsers.response.xml.ftl");
            template.setValue("id", user.getId());
            template.setValue("lastName", user.getLastName());
            template.setValue("firstName", user.getFirstName());
            template.setValue("streetName", user.getStreetName());
            template.setValue("houseNumber", user.getHouseNumber());
            template.setValue("postalCode", user.getPostalCode());
            template.setValue("city", user.getCity());
            template.setValue("books", user.getBooks());

            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(post(urlEqualTo("/getBooksForUsers"))
                    .withId(uuidVal)
                    .withRequestBody(containing(String.valueOf(user.getId())))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml")
                            .withBody(template.getOutput())));

        }
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

    @Then("^stop test$")
    public void stopTest() throws Throwable {
        Thread.sleep(300000);
    }

    @After
    public void close() {
        generic.stop();
    }
}
