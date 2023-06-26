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

import ca.bc.gov.moh.esb.util.SimpleSerializer;
import ca.bc.gov.moh.esb.util.audit.AffectedParties;
import ca.bc.gov.moh.esb.util.audit.AuditProcessor;
import ca.bc.gov.moh.esb.util.audit.AuditableMessage;
import ca.bc.gov.moh.esb.util.audit.AuditableParty;
import ca.bc.gov.moh.esb.util.audit.AuditableResponse;
import ca.bc.gov.moh.esb.util.audit.AuditableResponseMessage;
import ca.bc.gov.moh.esb.util.audit.entity.AffectedParty;
import ca.bc.gov.moh.esb.util.audit.entity.EventMessage;
import ca.bc.gov.moh.esb.util.audit.entity.Transaction;
import ca.bc.gov.moh.esb.util.audit.entity.TransactionEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processor class for auditing events in the Camel flows. Details can be added
 * by implementing the following interfaces on the classes for a Message object:
 *
 * AuditableMessage AuditableResponse AuditableParty
 *
 * @author joshua.burton
 */
public class DeathDateAuditProcessor implements Processor {

    public static final String TRANSACTION_HEADER_KEY = "HSA_TRANSACTION";
    public static final String TRANSACTION_ID_HEADER_KEY = "HSA_TRANSACTION_ID";

    public static final String MESSAGE_ID_HEADER_KEY = "messageId";

    private static final String LOGGED_EXCEPTIONS_HEADER_KEY = "LOGGED_EXCEPTIONS";
    
    public static final String TRANSACTION_MESSAGE_TYPE_HEADER_KEY = "messageType";
    public static final String TRANSACTION_MESSAGE_CONTEXT_IN = "IN";
    public static final String TRANSACTION_MESSAGE_CONTEXT_OUT = "OUT";

    /**
     * Configuration variables passed in from camel config
     */
    private String eventType = "DEFAULT";
    private String level = "DEFAULT";
    private final String transactionType;

    /**
     * Default constructor
     */
    public DeathDateAuditProcessor() {
        transactionType = "BatchDeath";
    }

    /**
     * Constructor
     *
     * @param eventType String
     * @param level String
     */
    public DeathDateAuditProcessor(String eventType, String level) {
        this();
        this.eventType = eventType;
        this.level = level;
    }

    /**
     * Audits the current state of the exchange to the transaction auditing
     * database
     *
     * @param exchange Exchange
     * @throws Exception
     */
    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        boolean newTransaction = false;
 
        String serializedTransaction = exchange.getIn().getHeader(TRANSACTION_HEADER_KEY, String.class);
        Transaction transaction;
        if (StringUtils.isEmpty(serializedTransaction)) {
            newTransaction = true;
            String transactionID = UUID.randomUUID().toString();
            exchange.getIn().setHeader(TRANSACTION_ID_HEADER_KEY, transactionID);
            transaction = new Transaction(transactionID);
        } else {
            transaction = (Transaction) SimpleSerializer.fromString(serializedTransaction);
        }
        
        Properties appProperties;
        try {
            appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
            transaction.setSourceSystem(appProperties.getProperty("senderSystem"));
            transaction.setOrganization(appProperties.getProperty("senderOrg"));
            transaction.setUserId(appProperties.getProperty("userId"));
        } catch (NamingException e) {
            Logger.getLogger(DeathDateAuditProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
        transaction.setType(transactionType);
        transaction.setServer(InetAddress.getLocalHost().getHostName());

        String messageTypeHeader = (String) exchange.getIn().getHeader(TRANSACTION_MESSAGE_TYPE_HEADER_KEY);
        if (StringUtils.isEmpty(messageTypeHeader)) {
            exchange.getIn().setHeader(TRANSACTION_MESSAGE_TYPE_HEADER_KEY, transactionType);
        }

        Object body = exchange.getIn().getBody();

        TransactionEvent event = new TransactionEvent();
        event.setEventTime(new Date());
        event.setType(eventType);
       
        String messageId = null;
        event.setTransactionId(transaction.getTransactionId());
        if (body instanceof AuditableMessage) {
            messageId = ((AuditableMessage) body).getMessageId();
        } else {
            messageId = exchange.getIn().getHeader(MESSAGE_ID_HEADER_KEY, String.class);
        }
        event.setMessageId(messageId);

        Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

        if (body instanceof AffectedParties) {
            List<AuditableParty> parties = ((AffectedParties) body).getAuditableParties();
            for (AuditableParty party : parties) {
                AffectedParty ap = new AffectedParty();
                ap.setTransactionId(transaction.getTransactionId());
                ap.setIdentifierSource(party.getIdentifierSource());
                ap.setIdentifier(party.getIdentifier());
                ap.setIdentifierType(party.getIdentifierType());
                ap.setStatus(party.getStatus());
            }
        }

        if (body instanceof AuditableResponse) {
            AuditableResponse auditable = (AuditableResponse) body;
            List<AuditableResponseMessage> auditableResponseMessageList = auditable.getAuditableResponseMessageList();
            for (AuditableResponseMessage auditableResponseMessage : auditableResponseMessageList) {
                EventMessage eventMessage = new EventMessage();
                eventMessage.setErrorCode(StringUtils.abbreviate(auditableResponseMessage.getResponseCode(), 50));
                eventMessage.setLevel(level);
                eventMessage.setMessageText(StringUtils.abbreviate(auditableResponseMessage.getResponseText(), 500));
                eventMessage.setTransactionEventId(event);
                if (auditableResponseMessage.getResponseCode()!=null && !auditableResponseMessage.getResponseCode().isEmpty() && 
                    auditableResponseMessage.getResponseText()!=null && !auditableResponseMessage.getResponseText().isEmpty()){
                    event.getEventMessageList().add(eventMessage);
                }
            }

        } 
        
        if (exception != null && !alreadyLogged(exception, exchange)) {

            if (exception.getCause() != null) {
                exception = exception.getCause();
            }

            if (exception instanceof BeanValidationException) {
                Set<ConstraintViolation<Object>> constraintViolations = ((BeanValidationException) exception).getConstraintViolations();
                for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setErrorCode(StringUtils.abbreviate(constraintViolation.getMessageTemplate().replace("{", "").replace("}", ""), 50));
                    eventMessage.setLevel("ERROR");
                    String msg = StringUtils.abbreviate(constraintViolation.getMessage(), 500);
                    eventMessage.setMessageText(msg);
                    eventMessage.setTransactionEventId(event);
                    event.getEventMessageList().add(eventMessage);
                }
            } else {
                EventMessage eventMessage = new EventMessage();
                eventMessage.setErrorCode(StringUtils.abbreviate(exception.getClass().getSimpleName(), 50));
                eventMessage.setLevel("ERROR");
                String msg = StringUtils.abbreviate(exception.getMessage(), 500);
                eventMessage.setMessageText(msg);
                eventMessage.setTransactionEventId(event);
                event.getEventMessageList().add(eventMessage);
            }
                // Need to record the exception once it is logged so it doesn't 
            // hang around on the exchange and get logged with every
            // subsequent event (QC-151)
            // Note: clearing it caused problems with the audit processor
            addToLoggedList(exception, exchange);

        }

        try {
            AuditProcessor ap = new AuditProcessor();
            if (newTransaction) {
                transaction = ap.insert(transaction);
                
            }
            ap.insert(event);

        } catch (Exception e) {
            Logger.getLogger(DeathDateAuditProcessor.class.getName()).log(Level.SEVERE, null, e);
        }

        String newSerializedTransaction = SimpleSerializer.toString(transaction);
        exchange.getIn().setHeader(TRANSACTION_HEADER_KEY, newSerializedTransaction);
    }

    private boolean alreadyLogged(Throwable exception, Exchange exchange) {

        Throwable exceptionToTest = exception;
        if (exception.getCause() != null) {
            exceptionToTest = exception.getCause();
        }

        List<Throwable> loggedExceptionList = getLoggedExceptionList(exchange);
        for (Throwable loggedException : loggedExceptionList) {
            if (loggedException == exceptionToTest) {
                return true;
            }
        }
        return false;
    }

    private void addToLoggedList(Throwable exception, Exchange exchange) {
        List<Throwable> loggedExceptionList = getLoggedExceptionList(exchange);
        loggedExceptionList.add(exception);
    }

    private List<Throwable> getLoggedExceptionList(Exchange exchange) {
        List<Throwable> loggedExceptions = (List<Throwable>) exchange.getIn().getHeader(LOGGED_EXCEPTIONS_HEADER_KEY);
        if (loggedExceptions == null) {
            loggedExceptions = new ArrayList<Throwable>();
            exchange.getIn().setHeader(LOGGED_EXCEPTIONS_HEADER_KEY, loggedExceptions);
        }
        return loggedExceptions;
    }
}

