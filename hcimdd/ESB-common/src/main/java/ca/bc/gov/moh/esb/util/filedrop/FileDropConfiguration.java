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
package ca.bc.gov.moh.esb.util.filedrop;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author joshua.burton
 */
public class FileDropConfiguration {

    private Properties fileDropConfigurationProperties;
    private final String TRANSACTION_TYPE_ALL = "ALL";
    private final String MESSAGE_CONTEXT_ALL = "ALL";
    
    boolean isMessageToBeSaved(String transactionType, String messageContext) {
        
        Boolean isMessageToBeSaved = Boolean.FALSE;
        
        try {
            Context ctx = new InitialContext();
            fileDropConfigurationProperties = (Properties) ctx.lookup("java:app/fileDropConfig");

            if (isEmpty(transactionType)) {
                transactionType = TRANSACTION_TYPE_ALL;
            }

            if (isEmpty(messageContext)) {
                messageContext = MESSAGE_CONTEXT_ALL;
            }

            String propertyKey = transactionType + "." + messageContext;
            String propertyValue = (String) fileDropConfigurationProperties.get(propertyKey);

            if (!isEmpty(propertyValue)) {
                isMessageToBeSaved = Boolean.valueOf(propertyValue);
                return isMessageToBeSaved;
            }

            // Check the general case for the transaction type
            propertyKey = transactionType + "." + MESSAGE_CONTEXT_ALL;
            propertyValue = (String) fileDropConfigurationProperties.get(propertyKey);
            if (!isEmpty(propertyValue)) {
                isMessageToBeSaved = Boolean.valueOf(propertyValue);
                return isMessageToBeSaved;
            }

            // Check the general case for the message context
            propertyKey = TRANSACTION_TYPE_ALL + "." + messageContext;
            propertyValue = (String) fileDropConfigurationProperties.get(propertyKey);
            if (!isEmpty(propertyValue)) {
                isMessageToBeSaved = Boolean.valueOf(propertyValue);
                return isMessageToBeSaved;
            }

            // Check the catch-all case
            propertyKey = TRANSACTION_TYPE_ALL + "." + MESSAGE_CONTEXT_ALL;
            propertyValue = (String) fileDropConfigurationProperties.get(propertyKey);
            if (!isEmpty(propertyValue)) {
                isMessageToBeSaved = Boolean.valueOf(propertyValue);
                return isMessageToBeSaved;
            }
            
        } catch (NamingException e) {
            Logger.getLogger(FileDropConfiguration.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return isMessageToBeSaved;
    }
    
    private static boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }
}
