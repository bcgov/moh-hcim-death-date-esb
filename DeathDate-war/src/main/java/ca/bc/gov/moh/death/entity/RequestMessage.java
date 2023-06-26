package ca.bc.gov.moh.death.entity;

import java.io.Serializable;

/**
 * A request or input message with input/parameter data needed for processing of
 * the requested action.
 *
 * @author patrick.weckermann
 * @version 1.0
 * @created 26-Aug-2014 2:49:34 PM
 */
public abstract class RequestMessage extends Message implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private Message message;
    
    // Used only when sending to HCIM-Stub for testing
    private String stubExtension;

    public RequestMessage() {

    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }
    
    /**
     * @return the stub extension
     */
    public String getStubExtension() {
        return stubExtension;
    }
    
    /**
     * @param stubExtension the stub extension to set
     */
    public void setStubExtension(String stubExtension) {
        this.stubExtension = stubExtension;
    }

}//end RequestMessage
