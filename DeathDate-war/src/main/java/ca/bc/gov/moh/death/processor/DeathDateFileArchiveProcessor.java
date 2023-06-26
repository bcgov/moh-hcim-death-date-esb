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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
    
    private String archivePath;
    private final String transactionType;
    private final String messageType;
    
    public DeathDateFileArchiveProcessor() {
        Properties appProperties;
        try {
            appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
            this.archivePath = appProperties.getProperty("archivePath");
        } catch (NamingException e) {
            Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
        this.transactionType = "Archived";
        this.messageType = "ARCHIVE";
    }
    
    @Override
    public void process(Exchange exchange) {
        
        // No Correlation ID for Archiving process
//        String correlationId = exchange.getIn().getHeader(JMS_CORRELATION_ID, String.class);
        String transactionId = exchange.getIn().getHeader(TRANSACTION_ID_HEADER_KEY, String.class);
        
        String messageBody = exchange.getIn().getBody(String.class);
 
        if (messageBody != null && !messageBody.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
            // Filename format: TIMESTAMP-ARCHIVE-TRANSACTION_ID
            String fileName = archivePath + transactionType + "/"
                    + timeStamp + "-" + messageType +  "-" + transactionId   ;
            
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            try (
                PrintWriter writer = new PrintWriter(fileName)) {
                writer.println(messageBody);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
}
