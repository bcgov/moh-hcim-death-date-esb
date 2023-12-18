package ca.bc.gov.moh.death.service;

import java.io.IOException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;

/**
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
public class HcimAcknowledgementCheckTest {
    
   private final String retryResponse1;
   private final String retryResponse2;
   private final String validResponse;

    public HcimAcknowledgementCheckTest() throws IOException {
        this.retryResponse1 = IOUtils.toString(HcimAcknowledgementCheckTest.class.getClassLoader().getResourceAsStream("empiUnavailableHcimOut.xml"), "UTF-8");
        this.retryResponse2 = IOUtils.toString(HcimAcknowledgementCheckTest.class.getClassLoader().getResourceAsStream("technicalErrorHcimOut.xml"), "UTF-8");
        this.validResponse = IOUtils.toString(HcimAcknowledgementCheckTest.class.getClassLoader().getResourceAsStream("validHcimOut.xml"), "UTF-8");
    }

    @Test(expected = RetryException.class)
    public void testInvalidResponseType() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody("this is not an HL7v3 message");
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    @Test(expected = RetryException.class)
    public void testInvalidResponseCode1() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(retryResponse1);
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    @Test(expected = RetryException.class)
    public void testInvalidResponseCode2() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(retryResponse2);
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    @Test(expected = RetryException.class)
    public void testNullBody() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(null);
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    @Test(expected = RetryException.class)
    public void testNullAck() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(validResponse.replaceFirst("(?s)<acknowledgement typeCode=\"CA\">.*</acknowledgement>", ""));
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    @Test
    public void testValidResponse() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(validResponse);
        HcimAcknowledgementCheck instance = new HcimAcknowledgementCheck();
        instance.process(exchange);
    }
    
    // TODO test malformed XML. 
    // TODO test XML missing ack.
    
}
