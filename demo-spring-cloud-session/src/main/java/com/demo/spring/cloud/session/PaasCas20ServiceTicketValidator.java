package com.demo.spring.cloud.session;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyRetriever;
import org.jasig.cas.client.ssl.HttpURLConnectionFactory;
import org.jasig.cas.client.ssl.HttpsURLConnectionFactory;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 针对广联云CAS协议的解析器.
 */
public class PaasCas20ServiceTicketValidator extends AbstractUrlBasedTicketValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

  private ProxyRetriever proxyRetriever;
  private ProxyGrantingTicketStorage proxyGrantingTicketStorage;
  private HttpURLConnectionFactory urlConnectionFactory = new HttpsURLConnectionFactory();
  private String encoding;
  private static String base64;

  public PaasCas20ServiceTicketValidator(String casServerUrlPrefix, String appKey, String appSecret)
      throws UnsupportedEncodingException {
    super(casServerUrlPrefix);
    this.proxyRetriever = new Cas20ProxyRetriever(casServerUrlPrefix, this.getEncoding(),
        this.getURLConnectionFactory());
    base64 = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", appKey, appSecret).getBytes("utf-8"));
    base64 = String.format("%s %s", "Basic", base64);
  }

  @Override
  protected String getUrlSuffix() {
    return "serviceValidate";
  }

  @Override
  protected Assertion parseResponseFromServer(String response) throws TicketValidationException {
    String error = XmlUtils.getTextForElement(response, "authenticationFailure");
    if (CommonUtils.isNotBlank(error)) {
      throw new TicketValidationException(error);
    } else {
      String principal = XmlUtils.getTextForElement(response, "user");
      String proxyGrantingTicketIou = XmlUtils.getTextForElement(response, "proxyGrantingTicket");
      String proxyGrantingTicket;
      if (!CommonUtils.isBlank(proxyGrantingTicketIou) && this.proxyGrantingTicketStorage != null) {
        proxyGrantingTicket = this.proxyGrantingTicketStorage.retrieve(proxyGrantingTicketIou);
      } else {
        proxyGrantingTicket = null;
      }

      if (CommonUtils.isEmpty(principal)) {
        throw new TicketValidationException(
            "No principal was found in the response from the CAS server.");
      } else {
        Map<String, Object> attributes = this.extractCustomAttributes(response);
        AssertionImpl assertion;
        AttributePrincipal attributePrincipal = new AttributePrincipalImpl(principal, attributes,
            proxyGrantingTicket, this.proxyRetriever);
        assertion = new AssertionImpl(attributePrincipal);
        this.customParseResponse(response, assertion);
        return assertion;
      }
    }
  }

  @Override
  protected String retrieveResponseFromServer(URL validationUrl, String ticket) {
    return getResponseFromServer(validationUrl, encoding);
  }

  public static String getResponseFromServer(URL constructedUrl, String encoding) {
    HttpURLConnection conn = null;
    InputStreamReader in = null;

    try {
      conn = (HttpURLConnection) constructedUrl.openConnection();

      conn.setRequestProperty("Authorization", base64);

      if (CommonUtils.isEmpty(encoding)) {
        in = new InputStreamReader(conn.getInputStream());
      } else {
        in = new InputStreamReader(conn.getInputStream(), encoding);
      }

      StringBuilder builder = new StringBuilder(255);

      int byteRead;
      while ((byteRead = in.read()) != -1) {
        builder.append((char) byteRead);
      }

      String var7 = builder.toString();
      return var7;
    } catch (Exception var11) {
      LOGGER.error(var11.getMessage(), var11);
      throw new RuntimeException(var11);
    } finally {
      CommonUtils.closeQuietly(in);
      if (conn != null) {
        conn.disconnect();
      }

    }
  }

  protected Map<String, Object> extractCustomAttributes(String xml) {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    spf.setValidating(false);

    try {
      SAXParser saxParser = spf.newSAXParser();
      XMLReader xmlReader = saxParser.getXMLReader();
      PaasCas20ServiceTicketValidator.CustomAttributeHandler handler = new PaasCas20ServiceTicketValidator.CustomAttributeHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.parse(new InputSource(new StringReader(xml)));
      return handler.getAttributes();
    } catch (Exception var6) {
      this.logger.error(var6.getMessage(), var6);
      return Collections.emptyMap();
    }
  }

  protected void customParseResponse(String response, Assertion assertion)
      throws TicketValidationException {
  }

  private class CustomAttributeHandler extends DefaultHandler {

    private Map<String, Object> attributes;
    private boolean foundAttributes;
    private String currentAttribute;
    private StringBuilder value;

    private CustomAttributeHandler() {
    }

    @Override
    public void startDocument() throws SAXException {
      this.attributes = new HashMap();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName,
        Attributes attributes) throws SAXException {
      if ("attributes".equals(localName)) {
        this.foundAttributes = true;
      } else if (this.foundAttributes) {
        this.value = new StringBuilder();
        this.currentAttribute = localName;
      }

    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
      if (this.currentAttribute != null) {
        this.value.append(chars, start, length);
      }

    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
      if ("attributes".equals(localName)) {
        this.foundAttributes = false;
        this.currentAttribute = null;
      } else if (this.foundAttributes) {
        Object o = this.attributes.get(this.currentAttribute);
        if (o == null) {
          this.attributes.put(this.currentAttribute, this.value.toString());
        } else {
          Object items;
          if (o instanceof List) {
            items = (List) o;
          } else {
            items = new LinkedList();
            ((List) items).add(o);
            this.attributes.put(this.currentAttribute, items);
          }

          ((List) items).add(this.value.toString());
        }
      }

    }

    public Map<String, Object> getAttributes() {
      return this.attributes;
    }
  }
}
