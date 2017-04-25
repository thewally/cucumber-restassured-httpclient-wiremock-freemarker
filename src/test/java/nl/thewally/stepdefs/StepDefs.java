package nl.thewally.stepdefs;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nl.thewally.freemarker.Book;
import nl.thewally.freemarker.TemplateHandler;
import nl.thewally.freemarker.User;
import org.junit.Assert;
import org.junit.Rule;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

public class StepDefs {

    @Rule
    public WireMockRule generic = new WireMockRule(8888);

    private List<User> users;
    private List<Book> books;

    private RequestSpecification request;
    private Response response;

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
        generic.stubFor(post(urlEqualTo("/GetBooksForUsers"))
                .withId(uuidVal)
                .withRequestBody(containing("ALL"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                        .withBody(template.getOutput())));
    }

    @Given("^possible states are available$")
    public void possibleStatesAreAvailable(List<String> states) throws Throwable {
        TemplateHandler template = new TemplateHandler();
        for(String state:states) {
            template.setTemplate("responses/setState.response.xml.ftl");
            template.setValue("state", state);

            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(post(urlEqualTo("/SetState"))
                    .inScenario("SCENARIO")
                    .withId(uuidVal)
                    .withRequestBody(containing(state))
                    .willSetStateTo(state)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                            .withBody(template.getOutput())));

        }

        template = new TemplateHandler();

        for (String state : states) {
            template.setTemplate("responses/getState.response.xml.ftl");
            template.setValue("state", state);
            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(get(urlEqualTo("/GetState"))
                    .inScenario("SCENARIO")
                    .whenScenarioStateIs(state)
                    .withId(uuidVal)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                            .withBody(template.getOutput())));
        }
    }



    @When("^send request message to service getBooksForUsers for (.*)$")
    public void sendRequestMessageForUser(String user) throws Throwable {
        if(!user.equals("ALL")) {
            try {
                Integer.parseInt(user);
            } catch (NumberFormatException e) {
                Assert.fail("Value "+user+" is not supported. Use userId or value 'ALL'");
            }
        }

        TemplateHandler template = new TemplateHandler();
        template.setTemplate("requests/getBooksForUsers.request.xml.ftl");
        template.setValue("user", user);
        request = given().header("Content-Type","text/xml; charset=\"utf-8\"").body(template.getOutput());
        response = request.when().post("http://localhost:8888/GetBooksForUsers");
    }

    @Then("^getBookForUser returns for (.*) with their own books$")
    public void checkBooksForUsers(String userId) throws Throwable {
        response.prettyPrint();
        response.then().assertThat().statusCode(200);

        if(!userId.equals("ALL")) {
            try {
                Integer.parseInt(userId);
            } catch (NumberFormatException e) {
                Assert.fail("Value "+userId+" is not supported. Use userId or value 'ALL'");
            }
        }

        String xml = response.andReturn().asString();
        for(User user: users) {
            if(String.valueOf(user.getId()).equals(userId) || userId.equals("ALL")){
                XmlPath userInfo = new XmlPath(xml).setRoot("getBooksForUsersResponse.users.user"
                        + ".findAll { it.id == '"+String.valueOf(user.getId())+"'}");
                List<Book> books = user.getBooks();
                Assert.assertEquals(user.getLastName(),userInfo.get("lastname"));
                Assert.assertEquals(user.getFirstName(),userInfo.get("firstname"));
                Assert.assertEquals(String.valueOf(user.getHouseNumber()),userInfo.get("housenumber"));
                Assert.assertEquals(user.getPostalCode(),userInfo.get("postalcode"));
                Assert.assertEquals(user.getCity(),userInfo.get("city"));
                if(books == null) {
                    Assert.assertEquals(0, userInfo.get("books.book.size()"));
                } else {
                    Assert.assertEquals(books.size(), userInfo.get("books.book.size()"));
                    for(Book book:books) {
                        XmlPath bookInfo = new XmlPath(xml).setRoot("getBooksForUsersResponse.users.user"
                                + ".findAll { it.id == '"+String.valueOf(user.getId())+"'}"
                                + ".books.book"
                                + ".findAll { it.id == '"+String.valueOf(book.getId())+"'}");
                        Assert.assertEquals(String.valueOf(book.getId()),bookInfo.get("id"));
                        Assert.assertEquals(book.getTitle(),bookInfo.get("title"));
                        Assert.assertEquals(book.getAuthor(),bookInfo.get("author"));
                        Assert.assertEquals(book.getYear(),bookInfo.get("year"));
                    }
                }
            }
        }
    }

    @Then("^stop test$")
    public void stopTest() throws Throwable {
        Thread.sleep(300000);
    }

    @When("^change state to (.*)$")
    public void changeStateTo(String state) throws Throwable {
        TemplateHandler template = new TemplateHandler();
        template.setTemplate("requests/setState.request.xml.ftl");
        template.setValue("state", state);
        request = given().header("Content-Type","text/xml; charset=\"utf-8\"").body(template.getOutput());
        response = request.when().post("http://localhost:8888/SetState");

        response.prettyPrint();

        response.then().assertThat().statusCode(200);
        String xml = response.andReturn().asString();
        XmlPath result = new XmlPath(xml).setRoot("setStateResponse");
        Assert.assertEquals("OK",result.get("result"));
    }

    @Then("^state is changed to (.*)$")
    public void stateIsChangedTo(String state) throws Throwable {
        request = given();
        response = request.when().get("http://localhost:8888/GetState");

        response.prettyPrint();

        response.then().assertThat().statusCode(200);
        String xml = response.andReturn().asString();
        XmlPath result = new XmlPath(xml).setRoot("getStateResponse");
        Assert.assertEquals(state,result.get("state"));
    }


    @After
    public void close() {
        generic.stop();
    }
}
