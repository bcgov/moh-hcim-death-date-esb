
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
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
    private String apiUrl;
    private String apiKey;
    private final String transactionType;
    private final String messageType;
    
    public DeathDateFileArchiveProcessor() {
        Properties appProperties;
        try {
            appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
            this.archivePath = appProperties.getProperty("archivePath");
            this.apiUrl = appProperties.getProperty("apiUrlPresignedS3");
            this.apiKey = appProperties.getProperty("apiKeyPresignedS3");
        } catch (NamingException e) {
            Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
        this.transactionType = "Archived";
        this.messageType = "ARCHIVE";
    }
    
    @Override
    public void process(Exchange exchange) {
        
        // No Correlation ID for Archiving process
        // String correlationId = exchange.getIn().getHeader(JMS_CORRELATION_ID, String.class);
        String transactionId = exchange.getIn().getHeader(TRANSACTION_ID_HEADER_KEY, String.class);
        
        String messageBody = exchange.getIn().getBody(String.class);
 
        if (messageBody != null && !messageBody.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
            // Filename format: TIMESTAMP-ARCHIVE-TRANSACTION_ID
            String fileName = timeStamp + "-" + messageType + "-" + transactionId;

            // Get the pre-signed URL to upload the file to S3
            if (apiUrl != null) {
                try {
                    // Put together the API endpoint to request a pre-signed URL
                    URL url = URI.create(apiUrl + "?key=" + URLEncoder.encode(fileName, "UTF-8")).toURL();

                    // Initialize the connection
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("GET");

                    // Add the API key to the request
                    if (apiKey != null && !apiKey.isBlank()) {
                        httpConnection.setRequestProperty("x-api-key", apiKey);
                    }

                    // Get the response which contains the presigned URL
                    InputStream content = (InputStream) httpConnection.getContent();
                    StringBuilder response = new StringBuilder();
                    int nextChar = content.read();

                    // Read the pre-signed URL from the response stream
                    while (nextChar >= 0) {
                        // It's wrapped in quotes by default, remove them
                        if ((char)nextChar != '"') {
                            response.append((char)nextChar);
                        }

                        nextChar = content.read();
                    }

                    // Convert to a URL object
                    URL uploadUrl = URI.create(response.toString()).toURL();

                    // Set up the request to the S3 bucket over the pre-signed URL
                    HttpURLConnection uploadConnection = (HttpURLConnection) uploadUrl.openConnection();
                    uploadConnection.setRequestMethod("PUT");
                    uploadConnection.setDoOutput(true);

                    // Add the file data
                    DataOutputStream requestStream = new DataOutputStream(uploadConnection.getOutputStream());
                    requestStream.writeBytes(messageBody);
                    requestStream.flush();
                    requestStream.close();

                    // Upload the file and get the response
                    int responseCode = uploadConnection.getResponseCode();

                    // Check the response code for an error response
                    if (responseCode != 200) {
                        Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(
                            Level.SEVERE,
                            "Response code is " + responseCode + " from S3 PUT request: " + uploadConnection.getResponseMessage()
                        );
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(DeathDateFileArchiveProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
    }
}
