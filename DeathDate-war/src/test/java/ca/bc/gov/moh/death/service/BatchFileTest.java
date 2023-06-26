package ca.bc.gov.moh.death.service;

import java.io.IOException;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

/**
 * Tests fixed-width file unmarshalling.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
public class BatchFileTest extends CamelTestSupport {
    
    private final String oneRecord;
    private final String twoRecords;

    public BatchFileTest() throws IOException {
        this.oneRecord = IOUtils.toString(BatchFileTest.class.getClassLoader().getResourceAsStream("oneRecord.txt"), "UTF-8");
        this.twoRecords = IOUtils.toString(BatchFileTest.class.getClassLoader().getResourceAsStream("twoRecords.txt"), "UTF-8");
    }

    @Test
    public void testUnmarshalBatchFile_oneRecord() throws Exception {

        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(1);

        template.sendBody("direct:toObject", oneRecord);

        mock.assertIsSatisfied();

        BatchFile batchFile = mock.getReceivedExchanges().get(0).getIn().getBody(BatchFile.class);
        assertNotNull(batchFile);
        assertEquals("PHNPHNPHN0", batchFile.getPhn());
        assertEquals("SMITHS", batchFile.getSurnm());
        assertEquals("JOHNJ", batchFile.getFirst());
        assertEquals("SECNDSE", batchFile.getSecnd());
        assertEquals("THIRDTHI", batchFile.getThird());
        assertEquals("M", batchFile.getSex());
        assertEquals("1990-05-06", batchFile.getBirth());
        assertEquals("1997-12-06", batchFile.getDeath());
    }

    @Test
    public void testUnmarshalBatchFile_twoRecords() throws Exception {

        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(1);

        template.sendBody("direct:toObject", twoRecords);

        mock.assertIsSatisfied();

        @SuppressWarnings("unchecked")
        List<BatchFile> batchFiles = mock.getReceivedExchanges().get(0).getIn().getBody(List.class);

        BatchFile batchFile = batchFiles.get(0);
        assertNotNull(batchFile);
        assertEquals("PHNPHNPHN0", batchFile.getPhn());
        assertEquals("SMITHS", batchFile.getSurnm());
        assertEquals("JOHNJ", batchFile.getFirst());
        assertEquals("SECNDSE", batchFile.getSecnd());
        assertEquals("THIRDTHI", batchFile.getThird());
        assertEquals("M", batchFile.getSex());
        assertEquals("1990-05-06", batchFile.getBirth());
        assertEquals("1997-12-06", batchFile.getDeath());

        batchFile = batchFiles.get(1);
        assertNotNull(batchFile);
        assertEquals("PHNPHNPHN1", batchFile.getPhn());
        assertEquals("SMITHS", batchFile.getSurnm());
        assertEquals("JOHNJ", batchFile.getFirst());
        assertEquals("SECN", batchFile.getSecnd());
        assertEquals(null, batchFile.getThird());
        assertEquals("F", batchFile.getSex());
        assertEquals("1951-02-26", batchFile.getBirth());
        assertEquals("2004-02-02", batchFile.getDeath());
    }

//    @Test
//    public void testUnmarshalBatchFile_recordIsTooShort() throws Exception {
//
//        Exchange exchange = context.getEndpoint("direct:toObject").createExchange();
//
//        exchange.getIn().setBody(oneRecord.substring(0, oneRecord.length() / 2));
//        Exchange out = context.createProducerTemplate().send("direct:toObject", exchange);
//
//        assertTrue(out.isFailed());
//        assertThat(out.getException(), instanceOf(java.lang.IllegalArgumentException.class));
//    }

    @Test
    public void testUnmarshalBatchFile_empty() throws Exception {

        Exchange exchange = context.getEndpoint("direct:toObject").createExchange();

        exchange.getIn().setBody("");
        Exchange out = context.createProducerTemplate().send("direct:toObject", exchange);

        assertTrue(out.isFailed());
        assertThat(out.getException(), instanceOf(java.lang.IllegalArgumentException.class));
        assertEquals(out.getException().getMessage(), "No records have been defined in the the file");
    }

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:toObject")
                        .process(InboundRouteBuilder.BATCH_FILE_PROCESSOR)
                        .unmarshal().bindy(BindyType.Fixed, BatchFile.class)
                        .to("mock:result");
            }
        };
    }

}
