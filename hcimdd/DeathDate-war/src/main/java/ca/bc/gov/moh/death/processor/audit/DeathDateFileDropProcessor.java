/*
 * *********************************************************************************************************************
 *  Copyright (c) 2018, Ministry of Health, BC.                                                 *
 *                                                                                                                     *
 *  All rights reserved.                                                                                               *
 *    This information contained herein may not be used in whole                                                       *
 *    or in part without the express written consent of the                                                            *
 *    Government of British Columbia, Canada.                                                                          *
 *                                                                                                                     *
 *  Revision Control Information                                                                                       *
 *  File:                $Id::                                                                                       $ *
 *  Date of Last Commit: $Date::                                                                                     $ *
 *  Revision Number:     $Rev::                                                                                      $ *
 *  Last Commit by:      $Author::                                                                                   $ *
 *                                                                                                                     *
 * *********************************************************************************************************************
 */
package ca.bc.gov.moh.death.processor.audit;

import static ca.bc.gov.moh.death.processor.audit.DeathDateAuditProcessor.TRANSACTION_ID_HEADER_KEY;
import static ca.bc.gov.moh.death.service.InboundRouteBuilder.JMS_CORRELATION_ID;
import ca.bc.gov.moh.esb.util.filedrop.FileDropProcessor;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *
 * @author joshua.burton
 */
public class DeathDateFileDropProcessor implements Processor {

    private String apiUrl;
    private String apiKey;
    private final String transactionType;
    private String messageType;

    public DeathDateFileDropProcessor() {
        Properties appProperties;
        try {
            appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
            this.apiUrl = appProperties.getProperty("apiUrlPresignedS3");
            this.apiKey = appProperties.getProperty("apiKeyPresignedS3");
        } catch (NamingException e) {
            Logger.getLogger(DeathDateFileDropProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
        this.transactionType = "BatchDeath";
    }

    public DeathDateFileDropProcessor(String messageType) {
        this();
        this.messageType = messageType;
    }

    @Override
    public void process(Exchange exchange) {
        String messageTypeFileName;
        String correlationId = exchange.getIn().getHeader(JMS_CORRELATION_ID, String.class);
        if (correlationId != null) {
            messageTypeFileName = messageType + "-" + correlationId;
        } else {
            messageTypeFileName = messageType;
        }

        String transactionId = exchange.getIn().getHeader(TRANSACTION_ID_HEADER_KEY, String.class);
        FileDropProcessor fdp = new FileDropProcessor("", transactionType, transactionId);

        String messageBody = exchange.getIn().getBody(String.class);
        fdp.dropS3File(messageBody, messageTypeFileName, apiUrl, apiKey);
    }
}
