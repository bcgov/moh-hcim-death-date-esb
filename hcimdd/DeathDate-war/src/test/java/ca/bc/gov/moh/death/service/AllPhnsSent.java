package ca.bc.gov.moh.death.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;

/**
 * Tests that the expected PHNs are received.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
class AllPhnsSent implements Runnable {

    private static final Pattern PHN_PATTERN = Pattern.compile("<id root=\"2.16.840.1.113883.3.51.1.1.6\" extension=\"(.{10})\"\\/>");
    private final MockEndpoint mockEndpoint;
    private final int expectedCount;
    private List<String> expectedPhns;

    /**
     * Create an expectation that we will receive expectedCount PHNs like this
     * PHNPHNPHN0, PHNPHNPHN1, PHNPHNPHN2, etc.
     * 
     * @param mockEndpoint
     * @param expectedCount 
     */
    public AllPhnsSent(MockEndpoint mockEndpoint, int expectedCount) {
        this.mockEndpoint = mockEndpoint;
        this.expectedCount = expectedCount;
        this.expectedPhns = null;
    }

    /**
     * Create an expectation that we will receive the expectedPhns.
     * 
     * @param mockEndpoint
     * @param expectedPhns 
     */
    public AllPhnsSent(MockEndpoint mockEndpoint, String... expectedPhns) {
        this.mockEndpoint = mockEndpoint;
        this.expectedCount = expectedPhns.length;
        this.expectedPhns = Arrays.asList(expectedPhns);
    }

    @Override
    public void run() {
        List<Exchange> exchanges = mockEndpoint.getExchanges();
        Assert.assertEquals(expectedCount, exchanges.size());
        if (expectedPhns != null) {
            List<String> actualPhns = new ArrayList<>();
            for (Exchange exchange : exchanges) {
                actualPhns.add(findPhn(exchange));
            }
            actualPhns.containsAll(expectedPhns);
        } else {
            for (int i = 0; i < expectedCount; i++) {
                String phn = findPhn(exchanges.get(i));
                Assert.assertEquals("PHNPHNPHN" + i, phn);
            }
        }
    }

    /**
     * Returns the PHN from the given Exchange. Throws an exception is no PHN is found.
     * @param exchange
     * @return the PHN.
     */
    private static String findPhn(Exchange exchange) {
        String in = exchange.getIn().getBody(String.class);
        Matcher matcher = PHN_PATTERN.matcher(in);
        Assert.assertTrue("PHN not found", matcher.find());
        return matcher.group(1);
    }

}
