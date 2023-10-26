package ca.bc.gov.moh.death.service;

import static ca.bc.gov.moh.death.service.InboundRouteBuilder.camelJMSComponentName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * Note that the majority of thrown exceptions and error logs related to the 
 * tests in this class happen because there is no initial GlassFish context,
 * which prevents certain properties from being set. For the sake of unit
 * testing, these properties are either not required or can only be properly
 * tested through integration testing.
 * 
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
public class InboundRouteBuilderTest extends CamelTestSupport {

    private final String tenRecords;
    private final String twoRecords;
    private final String oneRecord;
    private final String invalidResponse;
    private final String validResponse;
    
    
    public InboundRouteBuilderTest() throws IOException {
        this.tenRecords = IOUtils.toString(Objects.requireNonNull(BatchFileTest.class.getClassLoader()
                .getResourceAsStream("10Records.txt")), StandardCharsets.UTF_8);
        this.twoRecords = IOUtils.toString(Objects.requireNonNull(BatchFileTest.class.getClassLoader()
                .getResourceAsStream("twoRecords.txt")), StandardCharsets.UTF_8);
        this.oneRecord = IOUtils.toString(Objects.requireNonNull(BatchFileTest.class.getClassLoader()
                .getResourceAsStream("oneRecord.txt")), StandardCharsets.UTF_8);
        this.invalidResponse = IOUtils.toString(Objects.requireNonNull(BatchFileTest.class.getClassLoader()
                .getResourceAsStream("empiUnavailableHcimOut.xml")), StandardCharsets.UTF_8);
        this.validResponse = IOUtils.toString(Objects.requireNonNull(BatchFileTest.class.getClassLoader()
                .getResourceAsStream("validHcimOut.xml")), StandardCharsets.UTF_8);
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    /**
     * Creates and returns a new InboundRouteBuilder instance. Note that the
     * ftpProperties and appProperties attributes are set using the
     * InboundRouteBuilder's alternative constructor. Additionally, the
     * jmsUri static variable used to configure the JMS queue endpoint URIs in
     * the InboundRouteBuilder class is set to a direct endpoint, configuring 
     * the JMS queue endpoints to be "direct:*" as opposed to "jms*".
     * 
     * @return RouteBuilder
     */
    @Override
    public RouteBuilder createRouteBuilder() {
        Properties appProperties = new Properties();
        Properties ftpProperties = new Properties();
        
        appProperties.setProperty("maximumRedeliveries", "4");
        appProperties.setProperty("redeliveryDelay", "0");
        appProperties.setProperty("hcimTimeout", "0");
        appProperties.setProperty("jmsQueName", "batchRecordQueue");
        ftpProperties.setProperty("ftpPassword", "test\r");
        camelJMSComponentName ="direct:";
        return new InboundRouteBuilder(ftpProperties, appProperties);

    }

    /**
     * Configure the routes by adding Camel "advice".
     *
     *
     * @throws Exception
     */
    @Before
    public void configureRoutes() throws Exception {
        RouteDefinition fromFtp = context.getRouteDefinition("ftp://deathuser");
        fromFtp.adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:start");
            }
        });

        RouteDefinition fromSubsub = context.getRouteDefinition("direct:sendMessage");
        fromSubsub.adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToUri("spring-ws:*").replace().to("mock:springws");
            }
        });

        getMockEndpoint("mock:springws").whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(validResponse);
            }
        });
        
        context.start();
    }

    /**
     * Adds an empty PlatformTransactionManager implementation to the 
     * JndiRegistry. This is necessary, as the createRouteBuilder method above 
     * changes the JMS queue endpoint URIs from "jms*" to "direct:*", which
     * cannot be transactional. By overriding this implementation's methods with
     * empty equivalents, transaction functionality is disabled, allowing the 
     * route to include ".transacted()" without compromising the tests.
     * 
     * @return JndiRegistry
     * @throws Exception 
     */
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry reg = super.createRegistry();
        
        PlatformTransactionManager txMgr = new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
                return null;
            }

            @Override
            public void commit(TransactionStatus status) throws TransactionException {
            }

            @Override
            public void rollback(TransactionStatus status) throws TransactionException {
            }
        };
        reg.bind("jmsTransactionManager", txMgr);
        
        return reg;
    }
    
    @After
    @Override
    public void tearDown() throws Exception {
        context.stop();
    }

    @Test
    public void smokeTest() throws Exception {
        getMockEndpoint("mock:springws").expectedMessageCount(2);

        template.sendBody("direct:start", twoRecords);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAllRecordsSent_1() throws Exception {
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"), 1));

        template.sendBody("direct:start", oneRecord);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAllRecordsSent_2() throws Exception {
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"), 2));

        template.sendBody("direct:start", twoRecords);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAllRecordsSent_10() throws Exception {
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"), 10));

        template.sendBody("direct:start", tenRecords);
        assertMockEndpointsSatisfied();
    }

    /**
     * <ol>
     * <li>Given that we send two records
     * <li>When the second record gets an HCIM response that should trigger a
     * retry
     * <li>Then the system should send the message again
     * </ol>
     */
    @Test
    public void testRetryOnInvalidHcimAck() throws Exception {
        getMockEndpoint("mock:springws").whenExchangeReceived(1, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(invalidResponse);
            }
        });
        getMockEndpoint("mock:springws").expectedMessageCount(3);
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"), "PHNPHNPHN0", "PHNPHNPHN1", "PHNPHNPHN1"));

        template.sendBody("direct:start", twoRecords);
        assertMockEndpointsSatisfied();
    }

    /**
     * <ol>
     * <li>Given that we send two records
     * <li>When the second record causes a spring-ws error once (HTTP 404, 500,
     * etc.)
     * <li>Then the system should retry sending the message
     * </ol>
     */
    @Test
    public void testRetryOnSpringWsException() throws Exception {
        getMockEndpoint("mock:springws").whenExchangeReceived(1, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new IllegalStateException("any spring exception");
            }
        });
        getMockEndpoint("mock:springws").expectedMessageCount(3);
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"), "PHNPHNPHN0", "PHNPHNPHN1", "PHNPHNPHN1"));

        template.sendBody("direct:start", twoRecords);
        assertMockEndpointsSatisfied();
    }

    /**
     * <ol>
     * <li>Given that we send two records
     * <li>When the second record continually causes a spring-ws error (HTTP
     * 404, 500, etc.)
     * <li>Then the system should retry sending the message until retries
     * attempts are exhausted
     * </ol>
     */
    @Test
    public void testLastMessageFailed_retriesExhausted() throws Exception {
        getMockEndpoint("mock:springws").whenAnyExchangeReceived(new Processor() {
            int i;

            @Override
            public void process(Exchange exchange) throws Exception {
                i++;
                if (i > 1) {
                    throw new IllegalStateException("any spring exception");
                }
                exchange.getIn().setBody(validResponse);
            }
        });
        getMockEndpoint("mock:springws").expectedMessageCount(6);
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"),
                "PHNPHNPHN0",
                "PHNPHNPHN1",
                "PHNPHNPHN1",
                "PHNPHNPHN1",
                "PHNPHNPHN1",
                "PHNPHNPHN1"));

        template.sendBody("direct:start", twoRecords);
        assertMockEndpointsSatisfied();
    }

    /**
     * <ol>
     * <li>Given that we send ten records
     * <li>When the seventh record continually causes a spring-ws error (HTTP
     * 404, 500, etc.)
     * <li>Then the system should retry sending the message until retry attempts
     * are exhausted
     * <li>And it should process the rest of messages
     * </ol>
     */
    @Test
    public void testMiddleMessageFailed_retriesExhausted() throws Exception {
        getMockEndpoint("mock:springws").whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String body = exchange.getIn().getBody(String.class);
                if (body.contains("PHNPHNPHN6")) {
                    throw new IllegalStateException("any spring exception");
                }
                exchange.getIn().setBody(validResponse);
            }
        });
        getMockEndpoint("mock:springws").expectedMessageCount(14);
        getMockEndpoint("mock:springws").expects(new AllPhnsSent(getMockEndpoint("mock:springws"),
                "PHNPHNPHN0", "PHNPHNPHN1", "PHNPHNPHN2", "PHNPHNPHN3", "PHNPHNPHN4", "PHNPHNPHN5",
                "PHNPHNPHN6", "PHNPHNPHN6", "PHNPHNPHN6", "PHNPHNPHN6", "PHNPHNPHN6",
                "PHNPHNPHN7", "PHNPHNPHN8", "PHNPHNPHN9"));

        template.sendBody("direct:start", tenRecords);
        assertMockEndpointsSatisfied();
    }

    // TODO: test empty file
    // TODO: test malformed XML
    // TODO: test not XML response
    // TODO: test what happens if an error is thrown that is NOT handled by a retry
}
