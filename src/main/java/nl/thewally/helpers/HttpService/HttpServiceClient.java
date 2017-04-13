package nl.thewally.helpers.HttpService;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServiceClient.class);
    private HttpClient client;
    private static String CHARSET = "UTF-8";

    private final String endpoint;

    private HttpPost postRequest;
    private HttpResponse response;

    private String requestString, responseString;


    private Map<String, String> headers = new HashMap<>();

    public HttpServiceClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setHeaders(Map<String, String> addHeaders) {
        for (Map.Entry<String, String> header : addHeaders.entrySet()) {
            headers.put(header.getKey(), header.getValue());
        }
    }

    public void sendPostRequest(String requestMessage) {
        try {
            client = HttpClientBuilder.create().build();
            postRequest = new HttpPost(endpoint);

            HttpEntity entity = new ByteArrayEntity(requestMessage.getBytes(CHARSET));
            for (Map.Entry<String, String> header : headers.entrySet()) {
                postRequest.setHeader(header.getKey(), header.getValue());
            }
            postRequest.setEntity(entity);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            LOG.debug("Cannot parse request message.");
        } catch (IOException e) {
            LOG.debug("Cannot send request.");
        }

        try {
            requestString = EntityUtils.toString(postRequest.getEntity());
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOG.debug("Cannot parse message to String.");
        }
    }
    public Header[] getRequestHeaders() {
        return postRequest.getAllHeaders();
    }

    public String getRequest() {
        return parseMessageToString(postRequest);
    }

    public Header[] getResponseHeaders() {
        return response.getAllHeaders();
    }

    public String getResponse() {
        return parseMessageToString(response);
    }

    public String getValueByTagName(String tagName) {
        List<String> values = new ArrayList<>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(requestString.getBytes("UTF-8"))));
            NodeList nodeList = doc.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                values.add(nodeList.item(i).getTextContent());
            }
        } catch (ParserConfigurationException e) {
            LOG.debug("Cannot parse configuration: " + e);
        } catch (SAXException e) {
            LOG.debug("Something goes wrong: " + e);
        } catch (IOException e) {
            LOG.debug("Cannot parse response: " + e);
        }
        return values.get(0);
    }

    public List<String> getValueListByXpath(String xpathExpression) throws Exception {
        List<String> values = new ArrayList<>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
//            docBuilderFactory.setNamespaceAware(false);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(responseString.getBytes("UTF-8"))));
            //Evaluate XPath against Document itself
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            NodeList nodeList = (NodeList) expr.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {

                values.add(nodeList.item(i).getTextContent());
            }
        } catch (ParserConfigurationException e) {
            LOG.debug("Cannot parse configuration: " + e);
        } catch (SAXException e) {
            LOG.debug("Something goes wrong: " + e);
        } catch (IOException e) {
            LOG.debug("Cannot parse response: " + e);
        }
        return values;
    }

    private String parseMessageToString(HttpPost request) {
        try {
            requestString = EntityUtils.toString(request.getEntity());
        } catch (IOException e) {
            LOG.debug("Cannot parse message to String.");
        }
        return requestString;
    }

    private String parseMessageToString(HttpResponse response) {
        try {
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOG.debug("Cannot parse message to String.");
        }
        return responseString;
    }
}
