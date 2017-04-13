package nl.thewally.helpers.HttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class HttpXmlValidator {

    private static final Logger LOG = LoggerFactory.getLogger(HttpXmlValidator.class);

    private Document document;

    public HttpXmlValidator(String xml) {
        this.document = createDocument(xml);
    }

    private Document createDocument(String xml) {

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(false);
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            return builder.parse(src);
        } catch (ParserConfigurationException e) {
            LOG.debug("Cannot create DocumentBuilder: \n" + e);
        } catch (SAXException e) {
            LOG.debug("Cannot parse response to Document by SAXException: \n" + e);
        } catch (IOException e) {
            LOG.debug("Cannot parse response to Document: \n" + e);
        }
        return null;
    }

    public NodeList getNodeListByXpath(String xPathExpression) throws XPathExpressionException {
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile(xPathExpression);
            return (NodeList) expr.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
    }

    public Document getDocument() {
        return document;
    }
}
