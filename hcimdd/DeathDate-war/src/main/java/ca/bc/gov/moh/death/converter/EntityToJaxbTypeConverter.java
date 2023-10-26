/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.moh.death.converter;

import ca.bc.gov.moh.death.entity.DateAttribute;
import ca.bc.gov.moh.death.entity.PersonNameAttribute;
import ca.bc.gov.moh.death.entity.RequestMessage;
import ca.bc.gov.moh.death.transaction.EventMessage;
import ca.bc.gov.moh.death.entity.BooleanAttribute;
import ca.bc.gov.moh.death.entity.GenderAttribute;
import ca.bc.gov.moh.esb.common.entity.CommunicationFunction;
import ca.bc.gov.moh.esb.common.entity.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hl7.v3.BL;
import org.hl7.v3.CE;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.MCCIMT000100BCDevice;
import org.hl7.v3.NullFlavor;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.PN;
import org.hl7.v3.TS;
import org.hl7.v3.api.JaxbRequestControlActProcessWithEffectiveTime;
import org.hl7.v3.api.RequestJaxbMessage;

/**
 *
 * @author conrad.gustafson
 */
public class EntityToJaxbTypeConverter {

    protected static final ObjectFactory objectFactory = new ObjectFactory();
    private static final String GENDER_FEMALE = "F";
    private static final String GENDER_MALE = "M";
    protected static final String PERSON_NAME_TYPE_LEGAL_DECLARED = "L";
    protected static final String STATUS_CODE_ACTIVE = "active";
    protected static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    protected static void convertCommonFields(RequestJaxbMessage jaxb, RequestMessage request) {

        // set ID
        if (request.getStubExtension() != null) {
            jaxb.getId().setExtension(request.getStubExtension());
        } else {
            jaxb.getId().setExtension(request.getMessageId());
        }

        // set creation time
        final TS creationTime = new TS();
        creationTime.setValue(convertToTS(request.getCreationTime()));
        jaxb.setCreationTime(creationTime);

        // set receiver information
        List<CommunicationFunction> receiver = request.getReceiver();
        if (receiver != null && !receiver.isEmpty()) {
            CommunicationFunction receiverCommunicationFunction = receiver.get(0);

            String receiverSystemNameValue = receiverCommunicationFunction.getSystemName();
            final MCCIMT000100BCDevice receiverDevice = jaxb.getReceiver().getDevice();
            receiverDevice.getId().setExtension(receiverSystemNameValue);

            String receiverOrganizationValue = receiverCommunicationFunction.getOrganization();
            receiverDevice.getAsAgent().getValue().getRepresentedOrganization().getValue().getId().setExtension(receiverOrganizationValue);
        }

        // set sender information
        CommunicationFunction senderCommunicationFunction = request.getSender();
        String senderSystemNameValue = senderCommunicationFunction.getSystemName();
        final MCCIMT000100BCDevice senderDevice = jaxb.getSender().getDevice();
        senderDevice.getId().setExtension(senderSystemNameValue);
        String senderOrganizationValue = senderCommunicationFunction.getOrganization();
        senderDevice.getAsAgent().getValue().getRepresentedOrganization().getValue().getId().setExtension(senderOrganizationValue);

        // set data enterer
        String dataEntererID = null;
        if (request.getAuthor() != null) {
            User dataEnterer = request.getAuthor().getUser();
            if (dataEnterer != null && dataEnterer.getUserId() != null) {              
                dataEntererID = dataEnterer.getUserId();
            }
        }
        StringBuilder dataEntererIdentifierBuilder = new StringBuilder("");
        if (!StringUtils.isEmpty(dataEntererID)) {
            dataEntererIdentifierBuilder.append(dataEntererID);
        }

        if (StringUtils.isEmpty(dataEntererID)) {
            dataEntererIdentifierBuilder.append("");
        }

        jaxb.getControlActProcess().getDataEnterer().getAssignedPerson().getId().setExtension(dataEntererIdentifierBuilder.toString());


        // set event time
        if (jaxb.getControlActProcess() instanceof JaxbRequestControlActProcessWithEffectiveTime
                && request instanceof EventMessage) {
            Date timeToLive = DateUtils.addDays(new Date(System.currentTimeMillis()), 1);
            TS effectiveTimeTS = new TS();
            effectiveTimeTS.setValue(convertToTS(timeToLive));
            JaxbRequestControlActProcessWithEffectiveTime capWithEffectiveTime = (JaxbRequestControlActProcessWithEffectiveTime) jaxb.getControlActProcess();
            capWithEffectiveTime.setEffectiveTime(effectiveTimeTS);
        }
    }

    public static PN convertNameAttributeToPN(PersonNameAttribute nameAttribute) {
        PN pn = new PN();
        List<Serializable> nameContent = pn.getContent();

        // There is only one name on a person in FindCandidates
        String firstName = StringUtils.capitalize(nameAttribute.getFirstName());
        String middleName = StringUtils.capitalize(nameAttribute.getMiddleName());
        String lastName = StringUtils.capitalize(nameAttribute.getLastName());
        String thirdName = StringUtils.capitalize(nameAttribute.getTitle());
        String preferredGivenName = StringUtils.capitalize(nameAttribute.getPreferredGivenName());

        pn.getUse().add(PERSON_NAME_TYPE_LEGAL_DECLARED);

        if (!StringUtils.isEmpty(lastName)) {
            EnFamily familyNameElement = new EnFamily();
            familyNameElement.setText(lastName);
            nameContent.add(objectFactory.createENFamily(familyNameElement));
        }
        if (!StringUtils.isEmpty(firstName)) {
            EnGiven firstNameElement = new EnGiven();
            firstNameElement.setText(firstName);
            nameContent.add(objectFactory.createENGiven(firstNameElement));
        }
        if (!StringUtils.isEmpty(middleName)) {
            EnGiven middleNameElement = new EnGiven();
            middleNameElement.setText(middleName);
            nameContent.add(objectFactory.createENGiven(middleNameElement));
        }
        if (!StringUtils.isEmpty(preferredGivenName)) {
            EnGiven preferredGivenNameElement = new EnGiven();
            preferredGivenNameElement.setText(preferredGivenName);
            preferredGivenNameElement.getQualifier().add(PREFERRED_NAME_QUALIFIER);
            final JAXBElement<EnGiven> ENGiven = objectFactory.createENGiven(preferredGivenNameElement);
            nameContent.add(ENGiven);
        }
        if (!StringUtils.isEmpty(thirdName)) {
            EnGiven thirdNameElement = new EnGiven();
            thirdNameElement.setText(thirdName);
            nameContent.add(objectFactory.createENGiven(thirdNameElement));
        }

        return pn;
    }
    private static final String PREFERRED_NAME_QUALIFIER = "CL";

    public static TS convertDateAttributeToTS(DateAttribute dateAttribute) {

        if (dateAttribute == null || dateAttribute.getRawValue() == null){ 
            return null;
        }
        
        TS ts = new TS();
        ts.setValue(dateAttribute.getRawValue());

        return ts;
    }
    
    public static BL convertBooleanAttributeToBL(BooleanAttribute ba) {
        BL bl = new BL();
        bl.setValue(ba.getValue());
        return bl;
    }
    
    protected static CE convertGenderAttributeToCE(GenderAttribute genderAttribute) {
        if (genderAttribute == null || genderAttribute.getValue() == null) {
            return null;
        }
        CE gender = objectFactory.createCE();
        switch (genderAttribute.getValue()) {
            case Male:
                gender.setCode(GENDER_MALE);
                return gender;
            case Female:
                gender.setCode(GENDER_FEMALE);
                return gender;
            case Unknown:
                gender.setNullFlavor(NullFlavor.UNK);
                return gender;
            default:
                return null;
        }
    }
    
    protected static String convertToTS(Date creationTime) {
        synchronized (DATE_TIME_FORMAT) {
            return DATE_TIME_FORMAT.format(creationTime);
        }
    }
}
