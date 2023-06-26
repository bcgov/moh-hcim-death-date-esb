/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.moh.death.entity;

import ca.bc.gov.moh.esb.util.audit.AuditableResponse;
import ca.bc.gov.moh.esb.util.audit.AuditableResponseMessage;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author killian.faussart
 */
public class AckMessage implements AuditableResponse, AuditableResponseMessage {

    private String responseCode;
    private String responseText;
    
    @Override
    public List<AuditableResponseMessage> getAuditableResponseMessageList() {
        return Arrays.asList(new AuditableResponseMessage[]{this});
    }

    @Override
    public String getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(String responseCode){
        this.responseCode = responseCode;
    }

    @Override
    public String getResponseText() {
        return responseText;
    }
    
    public void setResponseText(String responseText){
        this.responseText = responseText;
    }
    
}
