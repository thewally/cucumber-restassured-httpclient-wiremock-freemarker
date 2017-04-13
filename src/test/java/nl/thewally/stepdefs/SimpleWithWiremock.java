package nl.thewally.stepdefs;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.thewally.freemarker.Book;
import nl.thewally.freemarker.TemplateHandler;
import nl.thewally.freemarker.User;
import nl.thewally.helpers.HttpService.HttpServiceClient;
import nl.thewally.helpers.HttpService.HttpXmlValidator;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class SimpleWithWiremock {

    @Rule
    public WireMockRule generic = new WireMockRule(8888);

    private List<User> users;
    private List<Book> books;

    private HttpServiceClient GetBooksForUsersService;

    @Before
    public void prepare() {
        generic.start();
        WireMock.configureFor("localhost", 8888);
        WireMock.reset();

        GetBooksForUsersService  = new HttpServiceClient("http://localhost:8888/GetBooksForUsers");
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

    @Given("^service getBooksForUsers returns response for all users or by user id$")
    public void serviceGetBooksForUsersReturnsResponseForAllUsersOrByUserId() throws Throwable {
        TemplateHandler template = new TemplateHandler();
        for(User user:users) {
            List<User> tempUser = new ArrayList<>();
            tempUser.add(user);

            template.setTemplate("responses/getBooksForUsers.response.xml.ftl");
            template.setValue("users", tempUser);

            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(post(urlEqualTo("/GetBooksForUsers"))
                    .withId(uuidVal)
                    .withRequestBody(containing(String.valueOf(user.getId())))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                            .withBody(template.getOutput())));

        }

        template.setTemplate("responses/getBooksForUsers.response.xml.ftl");
        template.setValue("users", users);

        UUID uuidVal = UUID.randomUUID();
        generic.stubFor(post(urlEqualTo("/getBooksForUsers"))
                .withId(uuidVal)
                .withRequestBody(containing("ALL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                        .withBody(template.getOutput())));
    }

    @When("^send request message to service getBooksForUsers for user (\\d+)$")
    public void sendRequestMessageToServiceGetBooksForUsersForUser(int user) throws Throwable {
        TemplateHandler template = new TemplateHandler();
        template.setTemplate("requests/getBooksForUsers.request.xml.ftl");
        template.setValue("user", user);
        GetBooksForUsersService.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        GetBooksForUsersService.sendPostRequest(template.getOutput());
        System.out.println("==REQUEST==");
        for (Header header:GetBooksForUsersService.getRequestHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println(GetBooksForUsersService.getRequest());
    }

    @Then("^getBookForUser returns for user (\\d+) with their own books$")
    public void getbookforuserReturnsForUserWithTheirOwnBooks(int user) throws Throwable {
        System.out.println("==RESPONSE==");
        for (Header header:GetBooksForUsersService.getResponseHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println(GetBooksForUsersService.getResponse());

        HttpXmlValidator response = new HttpXmlValidator(GetBooksForUsersService.getResponse());
        NodeList nodeList = response.getDocument().getElementsByTagName("user").item(0).getChildNodes();

        for(int x = 0; x < nodeList.getLength(); x++) {
            System.out.println(nodeList.item(x).getNodeName());
        }

        String lastname = response.getDocument().getElementsByTagName("lastname").item(0).getTextContent();
        System.out.println(lastname);
//        Element y = (Element)response.getDocument().getElementsByTagName("user").item(0);
//        Element q = (Element)y.getChildNodes().item(2);
//        String x = q.getNodeValue();

////        Assert.assertTrue(users.getLength()==1);
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
