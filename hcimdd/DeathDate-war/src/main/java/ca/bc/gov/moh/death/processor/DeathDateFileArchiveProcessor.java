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
package ca.bc.gov.moh.death.processor;

import static ca.bc.gov.moh.death.processor.audit.DeathDateAuditProcessor.TRANSACTION_ID_HEADER_KEY;
import ca.bc.gov.moh.esb.util.S3FileUploader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class DeathDateFileArchiveProcessor implements Processor {

    private String apiUrl;
    private String apiKey;
    private final String messageType;

    public DeathDateFileArchiveProcessor() {
        Properties appProperties;
        try {
            appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
            this.apiUrl = appProperties.getProperty("apiUrlPresignedS3");
            this.apiKey = appProperties.getProperty("apiKeyPresignedS3");
        } catch (NamingException e) {
            Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
        this.messageType = "ARCHIVE";
    }

    @Override
    public void process(Exchange exchange) {

        // No Correlation ID for Archiving process
        String transactionId = exchange.getIn().getHeader(TRANSACTION_ID_HEADER_KEY, String.class);

        String messageBody = exchange.getIn().getBody(String.class);

        if (messageBody != null && !messageBody.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
            // Filename format: TIMESTAMP-ARCHIVE-TRANSACTION_ID
            String fileName = timeStamp + "-" + messageType + "-" + transactionId;

            S3FileUploader s3FileUploader = new S3FileUploader(apiUrl, apiKey);
            s3FileUploader.uploadFile(messageBody, fileName);
        }
    }
}
