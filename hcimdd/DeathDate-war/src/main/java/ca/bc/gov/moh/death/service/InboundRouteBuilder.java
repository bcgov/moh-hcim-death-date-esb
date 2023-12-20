package ca.bc.gov.moh.death.service;

import ca.bc.gov.moh.death.entity.AckMessage;
import ca.bc.gov.moh.death.processor.DeathDateFileArchiveProcessor;
import ca.bc.gov.moh.death.processor.audit.DeathDateAuditProcessor;
import ca.bc.gov.moh.death.processor.audit.DeathDateFileDropProcessor;
import ca.bc.gov.moh.death.transaction.RevisePerson;
import com.jcraft.jsch.JSch;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.dataformat.bindy.fixed.BindyFixedLengthDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.hl7.v3.HCIMINPersonRevised;
import org.hl7.v3.MCCIIN000002;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

import static ca.bc.gov.moh.death.service.BatchFile.RECORD_LENGTH;

@Component
public class InboundRouteBuilder extends SpringRouteBuilder {

    // ftpProperties and appProperties are kept for test cases. For main functionality the values are now inject using spring.
    protected Properties ftpProperties;
    protected Properties appProperties;

    @Value("${hcimRevisePersonEndPointURI}")
    private String hcimRevisePersonEndPointURI;

    @Value("${autocreateFolders}")
    private String autocreateFolders;

    @Value("${filePath}")
    private String filePath;

    @Value("${moveToPath}")
    private String moveToPath;

    @Value("${scheduler.cron}")
    private String schedulerCron;
    //    private String schedulerCleanUp;
    @Value("${ftpUser}")
    private String ftpUser;

    @Value("${ftpPrivateKey}")
    private String ftpPrivateKey;

    @Value("${ftpPrivateKeyPassphrase}")
    private String ftpPrivateKeyPassphrase;

    @Value("${ftpPort}")
    private String ftpPort;
    @Value("${ftpUri}")
    private String ftpUri;

    @Value("${hcimTimeout}")
    private int hcimTimeout;

    @Value("${maximumRedeliveries}")
    int maximumRedeliveries;

    @Value("${redeliveryDelay}")
    int redeliveryDelay;

    @Value("${jmsQueue}")
    private String jmsQueName;

    public static final String JMS_CORRELATION_ID = "JMSCorrelationID";
    public static final String MAX_MESSAGE_NUMBER = "MaxMessageNumber";

    private static final Logger logger = LoggerFactory.getLogger(InboundRouteBuilder.class);

    private static final int TIMEOUT_SECONDS = 1000;
    private static final String RETRY_COUNT = "retryCount";

    private static final String RECEIVE = "RECEIVE";
    private static final String START = "START";
    private static final String ENTITY_CREATED = "ENTITY_CREATED";
    private static final String HCIM_IN = "HCIM_IN";
    private static final String HCIM_OUT = "HCIM_OUT";
    private static final String COMPLETE = "COMPLETE";

    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";
//    private static final String CLEAN_UP = "CLEAN_UP";

    // Static class Processor objects
    protected static Processor RETRY_EXCEPTION_PROCESSOR = new RetryExceptionProcessor();
    protected static Processor BATCH_FILE_PROCESSOR = new BatchFileProcessor();
    protected static Processor HCIM_ACK_PROCESSOR = new HcimAckProcessor();
    protected static Processor MESSAGE_ID_PROCESSOR = new MessageIdProcessor();

    // File Archive Processor object
    protected static Processor FILE_ARCHIVE = new DeathDateFileArchiveProcessor();

    // File drop Processor objects
    protected static Processor FILE_DROP_RECEIVE = new DeathDateFileDropProcessor(RECEIVE);
    protected static Processor FILE_DROP_HCIM_IN = new DeathDateFileDropProcessor(HCIM_IN);
    protected static Processor FILE_DROP_HCIM_OUT = new DeathDateFileDropProcessor(HCIM_OUT);
    protected static Processor FILE_DROP_ERROR = new DeathDateFileDropProcessor(ERROR);

    // Audit Processor objects
    protected static Processor AUDIT_RECEIVE = new DeathDateAuditProcessor(RECEIVE, INFO);
    protected static Processor AUDIT_START = new DeathDateAuditProcessor(START, INFO);
    protected static Processor AUDIT_ENTITY_CREATED = new DeathDateAuditProcessor(ENTITY_CREATED, INFO);
    protected static Processor AUDIT_HCIM_IN = new DeathDateAuditProcessor(HCIM_IN, INFO);
    protected static Processor AUDIT_HCIM_OUT = new DeathDateAuditProcessor(HCIM_OUT, INFO);
//    protected static Processor AUDIT_CLEANUP = new DeathDateAuditProcessor(CLEAN_UP, INFO);
    protected static Processor AUDIT_ERROR = new DeathDateAuditProcessor(ERROR, ERROR);
    protected static Processor AUDIT_HCIM_COMPLETE = new DeathDateAuditProcessor(COMPLETE, INFO);

    // Used by test classes to set the JMS queue URI to "direct:batchRecordQueue"
    protected static String camelJMSComponentName = "jms:";  // used for jms uri

    public InboundRouteBuilder() {
    }

    // Constructor used by InboundRouteBuilderTest to manually set ftpProperties and appProperties.
    public InboundRouteBuilder(Properties ftpProperties, Properties appProperties) {
        super();
        this.ftpProperties = ftpProperties;
        this.appProperties = appProperties;
        /*
        the below call is required for test cases only as this class is now using Spring injected properties.
        Test class is not using spring injection, hence the attributes need to be set from the passed properties
        files.
         */
        mapProperties();
    }

    /**
     * Method used only for test cases. Once the test cases start using spring injection, this method can be removed.
     */
    private void mapProperties() {
        hcimRevisePersonEndPointURI = appProperties.getProperty("hcimRevisePersonEndPointURI");
        maximumRedeliveries = Integer.parseInt(appProperties.getProperty("maximumRedeliveries"));
        redeliveryDelay = Integer.parseInt(appProperties.getProperty("redeliveryDelay"));
        hcimTimeout = Integer.parseInt(appProperties.getProperty("hcimTimeout"));

        autocreateFolders = ftpProperties.getProperty("autocreateFolders");
        filePath = ftpProperties.getProperty("filePath");
        moveToPath = ftpProperties.getProperty("moveToPath");
        schedulerCron = ftpProperties.getProperty("scheduler.cron");
//        schedulerCleanUp = ftpProperties.getProperty("scheduler.cleanup");
        ftpUser = ftpProperties.getProperty("ftpUser");
        ftpPrivateKey = ftpProperties.getProperty("ftpPrivateKey");
        ftpPrivateKeyPassphrase = ftpProperties.getProperty("ftpPrivateKeyPassphrase");
        ftpPort = ftpProperties.getProperty("ftpPort");
        ftpUri = ftpProperties.getProperty("ftpUri");
        jmsQueName = appProperties.getProperty("jmsQueName");
    }

    @Override
    public void configure() throws Exception {
        logger.info("Death Date Apache Camel route initializing.");
        String jmsUri = camelJMSComponentName + jmsQueName;

        DataFormat bindy = new BindyFixedLengthDataFormat(BatchFile.class);
        DataFormat jaxb = new JaxbDataFormat("org.hl7.v3");

        // MoH SFTP server still proposes using ssh-rsa
        JSch.setConfig("server_host_key", JSch.getConfig("server_host_key") + ",ssh-rsa");
        JSch.setConfig("PubkeyAcceptedAlgorithms", JSch.getConfig("PubkeyAcceptedAlgorithms") + ",ssh-rsa");

        String fromFtp = "sftp:" + ftpUri + ":" + ftpPort + filePath
                + "?autoCreate=" + autocreateFolders
                + "&move=" + moveToPath + "/${date:now:yyyyMMddHHmmssSSS}/${file:name}"//
                + "&privateKeyFile=" + ftpPrivateKey
//                + "&privateKeyPassphrase=RAW(" + ftpPrivateKeyPassphrase + ")"
                + "&username=" + ftpUser
                + "&runLoggingLevel=INFO"
                + "&idempotent=true"
                + "&idempotentKey=${file:path}-${file:size}"
                + "&idempotentRepository=#jpaStore"
                + "&scheduler=spring&scheduler.cron=" + schedulerCron
                + "&bridgeErrorHandler=true";

        // TODO review FTP command
//        String fromFtpCleanup = "sftp:" + ftpUri + ":" + ftpPort + filePath + moveToPath
//                + "?delete=" + deleteAfterReading
//                + "&privateKeyUri=" + ftpPrivateKey
//                + "&privateKeyPassphrase=RAW(" + ftpPrivateKeyPassphrase + ")"
//                + "&username=" + ftpUser
//                + "&idempotent=true&idempotentRepository=#jpaStore&inProgressRepository=#jpaStore"
//                + "&scheduler=spring&scheduler.cron=" + schedulerCleanUp;

        onException(RetryException.class)
                .onRedelivery(RETRY_EXCEPTION_PROCESSOR)
                .maximumRedeliveries(maximumRedeliveries)
                .redeliveryDelay(redeliveryDelay)
                .handled(true)
                .retryAttemptedLogLevel(LoggingLevel.INFO)
                .retriesExhaustedLogLevel(LoggingLevel.INFO)
                .logRetryAttempted(true)
                .logStackTrace(true);

        onException(Exception.class)
                .logHandled(true)
                .logStackTrace(true)
                .handled(true)
                .doTry()
                .process(AUDIT_ERROR)
                .doCatch(Exception.class)
                .endDoTry();

        from(fromFtp)
                .routeId("ftp://deathuser")
                .log("The ${routeId} route is processing a file named '${header.CamelFileName}'")
                .process(AUDIT_RECEIVE) // first step always as it set the transaction ID
                .process(FILE_ARCHIVE) // Add a new process to Archive file
                .process(FILE_DROP_RECEIVE)
                .process(BATCH_FILE_PROCESSOR)
                .unmarshal(bindy)
                .process(AUDIT_START)
                .split(body())
                .setHeader(JMS_CORRELATION_ID, simple("${property.CamelSplitIndex}++"))
                .setHeader(MAX_MESSAGE_NUMBER, simple("${property.CamelSplitSize}"))
                .to(ExchangePattern.InOnly, jmsUri)
                .end();

        from(jmsUri)
                .routeId("jms:deathAuditQueue")
                .transacted()
                .log("Sending message ${header.JMSCorrelationID} of ${header.MaxMessageNumber}")
                .convertBodyTo(RevisePerson.class)
                .process(MESSAGE_ID_PROCESSOR)
                .process(AUDIT_ENTITY_CREATED)
                .convertBodyTo(HCIMINPersonRevised.class)
                .marshal(jaxb)
                .process(FILE_DROP_HCIM_IN)
                .process(AUDIT_HCIM_IN)
                .to("direct:sendMessage")
                .process(FILE_DROP_HCIM_OUT)
                .process(HCIM_ACK_PROCESSOR)
                .process(AUDIT_HCIM_OUT)
                .choice()
                .when(header(JMS_CORRELATION_ID).isEqualTo(header(MAX_MESSAGE_NUMBER)))
                .process(AUDIT_HCIM_COMPLETE);

//            from(fromFtpCleanup)
//                .routeId("ftp://cleanup")
//                .log("The ${routeId} route is cleanup files older than 7 days on the SFTP server'")
//                .process(AUDIT_CLEANUP) // Add Audit step, so we know when Cleanup happened
////                .process(FILE_DROP_RECEIVE)
////                .process(BATCH_FILE_PROCESSOR)
//                .end();
                
        /*
         This sub-route exists for error handling.

         When an error handler is configured, Camel retries from the point 
         of failure. If an error handler is not configured, the error will 
         escalate until it is handled, in which case it is handled at the
         point of failure.
        
         If the spring-ws endpoint throws an exception, or if the ErrorCheck
         class determines a retry is necessary, the error will escalate to the
         main route, "ftp://deathuser", and that route's error handler will
         retry at the point of failure, which in this case will be the entire
         "direct:sendMessage" route.
          
         https://camel.apache.org/how-do-i-retry-processing-a-message-from-a-certain-point-back-or-an-entire-route.html 
         */
        from("direct:sendMessage")
                .routeId("direct:sendMessage")
                .errorHandler(noErrorHandler())
                .doTry()
                .to("spring-ws:" + hcimRevisePersonEndPointURI
                        + "&timeout=" + (hcimTimeout * TIMEOUT_SECONDS))
                .doCatch(Exception.class)
                .process(exchange -> {
                    throw new RetryException(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class));
                })
                .end()
                .unmarshal(jaxb).id("unmarshalStep")
                .process(new HcimAcknowledgementCheck()).id("errorNode");
    }

    private static class BatchFileProcessor implements Processor {

        /*
         Adds padding to the end of each record as needed.
        
         Note that this processor exists only because the padding
         attribute for the DataField annotation is currently broken. If Apache
         fixes the bug, this processor might be replaced later on with the 
         padding attributes.
        
         https://issues.apache.org/jira/browse/CAMEL-9476
         */
        @Override
        public void process(Exchange exchange) throws Exception {
            // Initialize
            String[] body = exchange.getIn().getBody(String.class).split("\\r\\n|[\\r\\n]");
            StringBuilder newBody = new StringBuilder();

            // Loop for each record in the file
            for (String record : body) {

                // Add record
                newBody.append(record);

                // Add trailing spaces if needed
                int recordLength = RECORD_LENGTH - record.length();
                if (recordLength > 0) {
                    char[] spaces = new char[recordLength];
                    Arrays.fill(spaces, ' ');
                    newBody.append(spaces);
                }

                // Add newline character, unless we are at the last record
                if (!record.equals(body[body.length - 1])) {
                    newBody.append("\n");
                }
            }
            // Set body
            exchange.getIn().setBody(newBody.toString());
        }
    }

    private static class RetryExceptionProcessor implements Processor {

        // RetryException auditing and file drop
        @Override
        public void process(Exchange exchange) throws Exception {
            // Temporarily stores the original message body
            String temp = exchange.getIn().getBody(String.class);

            Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, RetryException.class);
            logger.error("A retry exception occured:", ex);
            while (ex.getCause() != null) {
                ex = ex.getCause();
            }
            int retryCount = exchange.getProperty(RETRY_COUNT, 1, int.class);

            exchange.getIn()
                    .setBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n"
                            + "<Error>\r\n"
                            + "    <ErrorCode>" + ex.getClass().getSimpleName() + "</ErrorCode>\r\n"
                            + "    <ErrorMessage>" + ex.getMessage() + "</ErrorMessage>\r\n"
                            + "    <RetryCount>" + retryCount + "</RetryCount>\r\n"
                            + "</Error>\r\n");

            // Set the retry count to the next value
            exchange.setProperty(RETRY_COUNT, ++retryCount);
            // Set EXCEPTION_CAUGHT, allows audit endpoint to use correct exception code
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, ex);

            AUDIT_ERROR.process(exchange);
            FILE_DROP_ERROR.process(exchange);

            // Set message body back to the original message
            exchange.getIn().setBody(temp);
        }
    }

    private static class HcimAckProcessor implements Processor {

        /*
         Set the body of the message to be an AckMessage which implements
         the AuditableResponse interface. This allows the AuditProcessor to
         create an EventMessage.
         */
        @Override
        public void process(Exchange exchange) throws Exception {
            AckMessage ackMessage = new AckMessage();
            MCCIIN000002 response = exchange.getIn().getBody(MCCIIN000002.class);
            String code = response.getAcknowledgement().getValue().getAcknowledgementDetail().get(0).getCode().getCode();
            String text = response.getAcknowledgement().getValue().getAcknowledgementDetail().get(0).getText().getText();
            ackMessage.setResponseCode(code);
            ackMessage.setResponseText(text);
            exchange.getIn().setBody(ackMessage);
        }
    }

    private static class MessageIdProcessor implements Processor {

        /*
         Inserts the messageId in the Header of the exchange message 
         so that we can always have it in the audit database (TRANSACTION_EVENT table) 
         event if the message body is not an AuditableMessage
         */
        @Override
        public void process(Exchange exchange) throws Exception {
            RevisePerson rp = exchange.getIn().getBody(RevisePerson.class);
            exchange.getIn().setHeader(DeathDateAuditProcessor.MESSAGE_ID_HEADER_KEY, rp.getMessageId());
        }
    }

}
