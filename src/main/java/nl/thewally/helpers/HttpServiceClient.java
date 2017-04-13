package nl.thewally.helpers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializer;
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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServiceClient.class);
    private HttpClient client;
    private HttpPost postRequest;
    private HttpResponse response;
    private final String endpoint;
    private String requestString, responseString;

    public HttpServiceClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public void sendRequest(String requestMessage) {
        try {
            client = HttpClientBuilder.create().build();
            postRequest = new HttpPost(endpoint);

            HttpEntity entity = new ByteArrayEntity(requestMessage.getBytes("UTF-8"));
            postRequest.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
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

    public String getPostRequest() {
        return prettyPrintXml(requestString);
    }

    public String getResponse() {
//        return responseString;
        return prettyPrintXml(responseString);
    }

    public String getValueByTagName(String tagName) {
        List<String> values = new ArrayList<>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(responseString.getBytes("UTF-8"))));
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

    private String prettyPrintXml(String xml) {
        try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = xml.startsWith("<?xml");

            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

            return writer.writeToString(document);
        } catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | DOMException | LSException e) {
            throw new RuntimeException(e);
        }
    }
}
