/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.bc.gov.moh.death.transaction;

import ca.bc.gov.moh.death.entity.IdentifierAttribute;
import ca.bc.gov.moh.death.entity.Person;
import ca.bc.gov.moh.death.entity.RequestMessage;
import ca.bc.gov.moh.esb.util.audit.AuditableResponse;
import ca.bc.gov.moh.esb.util.audit.AuditableResponseMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

/**
 *
 * @author Patrick.Weckermann
 */
public class RevisePerson extends RequestMessage implements EventMessage, AuditableResponse {
    
    private static final long serialVersionUID = 7526472295622776147L;
    
    @Valid
    private Person person;
    
    private Date eventTime;
    
    // This is only here to capture error messages
    private final List<AuditableResponseMessage> auditableResponseMessages = new ArrayList<>();

    public RevisePerson() {
        person = new Person();
            List<IdentifierAttribute> identifierList = new ArrayList<>();
            person.setIdentifier(identifierList);
    }

    /**
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    @Override
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getMessageGroupId() {
        List<IdentifierAttribute> identifierList = getPerson().getIdentifier();
        for (IdentifierAttribute identifier : identifierList) {
            if (identifier.isMRN()) {
                return identifier.getValue();
            }
        }
        return null;
    }
    
    @Override
    public List<IdentifierAttribute> getAuditableIdentifiers() {
        List<IdentifierAttribute> identifierList = new ArrayList<>();
        if (this.getPerson() != null) {
            identifierList.addAll(this.getPerson().getAllIdentifiers());
        }
        return identifierList;
    }

    @Override
    public List<AuditableResponseMessage> getAuditableResponseMessageList() {
        return auditableResponseMessages;
    }
}
