package ca.bc.gov.moh.death.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.hl7.v3.MCCIIN000002;
import org.slf4j.LoggerFactory;

/**
 * Throw an exception if the Exchange does not contain a valid response.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
class HcimAcknowledgementCheck implements Processor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HcimAcknowledgementCheck.class);
    private static final Set<String> acknowledgementCodeBlacklist = loadFromCsv();

    /**
     * Throw an exception if the Exchange does not contain a valid response.
     *
     * TODO: This is outdated if you split up HTTP error code processing, which
     * I think you should.
     *
     * A valid response:
     *
     * <ul>
     * <li>is an HL7v3 MCCI_IN000002 response.</li>
     * <li>contains a valid acknowledgment message.</li>
     * <li>has a 200 series HTTP response code</li>
     * </ul>
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        if (exchange.getIn() == null || exchange.getIn().getBody() == null) {
            throw new RetryException("Camel Exchange message or body is null.");
        }
        MCCIIN000002 response = null;
        try {
            response = exchange.getIn().getBody(MCCIIN000002.class);
        } catch (TypeConversionException e) {
            throw new RetryException(e);
        }
        try {
            String code = response.getAcknowledgement().getValue().getAcknowledgementDetail().get(0).getCode().getCode();
            if (acknowledgementCodeBlacklist.contains(code)) {
                throw new RetryException(String.format("HCIM acknowledgement code '%s' is on the blacklist.", code));
            }
        } catch (NullPointerException ex) {
            throw new RetryException(ex);
        }
    }

    // TODO: Maybe you should unit test this. Maybe add some validation.
    private static Set<String> loadFromCsv() throws IllegalStateException {
        try {
            InputStreamReader in = new InputStreamReader(HcimAcknowledgementCheck.class.getClassLoader().getResourceAsStream("errorMap.csv"), "UTF-8");
            List<CSVRecord> list = CSVFormat.EXCEL.withHeader("code", "text", "eaSummary", "behaviour").withSkipHeaderRecord().parse(in).getRecords();
            Set<String> blacklist = new HashSet<>();
            for (CSVRecord cSVRecord : list) {
                // TODO trim behaviour and code
                if (cSVRecord.get("behaviour").matches("Retry")) {
                    blacklist.add(cSVRecord.get("code"));
                }
            }
            logger.info("HCIM acknowledgement code blacklist configured: {}", blacklist);
            return blacklist;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
