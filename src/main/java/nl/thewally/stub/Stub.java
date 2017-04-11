package nl.thewally.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import nl.thewally.freemarker.TemplateHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Stub {

    private WireMockRule stub;
    private final Map<String, UUID> uuid = new HashMap<>();

    public void start(WireMockRule stub, int portnumber) {
        this.stub = stub;
        if (!stub.isRunning()) {
            stub.start();
            WireMock.configureFor("localhost", portnumber);
            WireMock.reset();
        }
    }

    public void setResponse(int status, String endpoint, String response) {
        stub.stubFor(post(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void setResponse(int status, String endpoint, String servicename, String response) {
        UUID uuidVal = UUID.randomUUID();
        uuid.put(servicename, uuidVal);
        stub.stubFor(post(urlEqualTo(endpoint))
                .withId(uuidVal)
                .withRequestBody(containing(servicename))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void setResponse(int status, String endpoint, String servicename, TemplateHandler template) throws Throwable {
        if (servicename == null) {
            setResponse(status, endpoint, template.getOutput());
        } else {
            setResponse(status, endpoint, servicename, template.getOutput());
        }
    }

    public void setGetResponse(int status, String endpoint, String response) {
        stub.stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void setGetResponse(int status, String endpoint, String servicename, String response) {
        UUID uuidVal = UUID.randomUUID();
        uuid.put(servicename, uuidVal);
        stub.stubFor(get(urlEqualTo(endpoint))
                .withId(uuidVal)
                .withRequestBody(containing(servicename))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void setGetResponse(int status, String endpoint, String servicename, TemplateHandler template) throws Throwable {
        if (servicename == null) {
            setGetResponse(status, endpoint, template.getOutput());
        } else {
            setGetResponse(status, endpoint, servicename, template.getOutput());
        }
    }

    public void changeResponse(int status, String endpoint, String response) {
        stub.editStub(post(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void changeResponse(int status, String endpoint, String servicename, String response) {
        stub.editStub(post(urlEqualTo(endpoint))
                .withId(getUuidByServicename(servicename))
                .withRequestBody(containing(servicename))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));
    }

    public void changeResponse(int status, String endpoint, String servicename, TemplateHandler template) throws Throwable {
        if (servicename == null) {
            changeResponse(status, endpoint, template.getOutput());
        } else {
            changeResponse(status, endpoint, servicename, template.getOutput());
        }
    }

    public void stop() {
        if (stub != null && stub.isRunning()) {
            stub.stop();
        }
    }

    private UUID getUuidByServicename(String servicename) {
        UUID returnval = null;
        for (Map.Entry pair : uuid.entrySet()) {
            if (pair.getKey().equals(servicename)) {
                returnval = UUID.fromString(pair.getValue().toString());
            }
        }

        return returnval;
    }

}
