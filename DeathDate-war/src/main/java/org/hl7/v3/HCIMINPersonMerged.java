//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.05 at 09:56:19 AM PDT 
//


package org.hl7.v3;

import ca.bc.gov.moh.esb.util.audit.AuditableMessage;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hl7.v3.api.RequestJaxbMessage;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}PRPA_IN101004CA.MCCI_MT000100BC.Message">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "HCIM_IN_PersonMerged")
public class HCIMINPersonMerged
    extends PRPAIN101004CAMCCIMT000100BCMessage implements RequestJaxbMessage, AuditableMessage {

    @XmlTransient
    private final static String DEFAULT_IDENTIFIER_ROOT = "2.16.840.1.113883.3.51.1.1.1";
    @XmlTransient
    private final static String DEFAULT_VERSION_CODE = "V3PR1";
    @XmlTransient
    private final static String DEFAULT_PERSON_MERGED_REQUEST_EXTENSION = "HCIM_IN_PersonMerged";
    @XmlTransient
    private final static String DEFAULT_INTERACTION_ID_ROOT = "2.16.840.1.113883.3.51.1.1.2";
    @XmlTransient
    private final static String DEFAULT_PROCESSING_CODE = "P";
    @XmlTransient
    private final static String DEFAULT_ACCEPT_ACK_CODE = "AL";
    @XmlTransient
    private final static CommunicationFunctionType DEFAULT_RECEIVER_TYPE_CODE = CommunicationFunctionType.RCV;
    @XmlTransient
    private final static String DEFAULT_RECEIVER_DEVICE_DETERMINERCODE = "INSTANCE";
    @XmlTransient
    private final static List<String> DEFAULT_RECEIVER_DEVICE_CLASSCODE = Arrays.asList(new String[]{"DEV"});
    @XmlTransient
    private final static String DEFAULT_RECEIVER_DEVICE_ID_ROOT = "2.16.840.1.113883.3.51.1.1.4";
    @XmlTransient
    private final static List<String> DEFAULT_RECEIVER_AGENT_CLASSCODE = Arrays.asList(new String[]{"AGNT"});
    @XmlTransient
    private final static String DEFAULT_RECEIVER_REPRESENTED_ORG_DETERMINERCODE = "INSTANCE";
    @XmlTransient
    private final static List<String> DEFAULT_RECEIVER_REPRESENTED_ORG_CLASSCODE = Arrays.asList(new String[]{"ORG"});
    @XmlTransient
    private final static String DEFAULT_RECEIVER_REPRESENTED_ORG_ID_ROOT = "2.16.840.1.113883.3.51.1.1.3";
    @XmlTransient
    private final static CommunicationFunctionType DEFAULT_SENDER_TYPE_CODE = CommunicationFunctionType.SND;
    @XmlTransient
    private final static String DEFAULT_SENDER_DEVICE_DETERMINERCODE = "INSTANCE";
    @XmlTransient
    private final static List<String> DEFAULT_SENDER_DEVICE_CLASSCODE = Arrays.asList(new String[]{"DEV"});
    @XmlTransient
    private final static String DEFAULT_SENDER_DEVICE_ID_ROOT = "2.16.840.1.113883.3.51.1.1.5";
    @XmlTransient
    private final static List<String> DEFAULT_SENDER_AGENT_CLASSCODE = Arrays.asList(new String[]{"AGNT"});
    @XmlTransient
    private final static String DEFAULT_SENDER_REPRESENTED_ORG_DETERMINERCODE = "INSTANCE";
    @XmlTransient
    private final static List<String> DEFAULT_SENDER_REPRESENTED_ORG_CLASSCODE = Arrays.asList(new String[]{"ORG"});
    @XmlTransient
    private final static String DEFAULT_SENDER_REPRESENTED_ORG_ID_ROOT = "2.16.840.1.113883.3.51.1.1.3";
    @XmlTransient
    private final static List<String> DEFAULT_CONTROL_ACT_PROCESS_CLASSCODE = Arrays.asList(new String[]{"CACT"});
    @XmlTransient
    private final static List<String> DEFAULT_CONTROL_ACT_PROCESS_MOODCODE = Arrays.asList(new String[]{"EVN"});
    @XmlTransient
    public final static String DEFAULT_INNER_PERSON_ID_ROOT = "2.16.840.1.113883.3.51.1.1.6.1";
    @XmlTransient
    private final static List<String> DEFAULT_CONTROL_ACT_PROCESS_DATA_ENTERER_TYPECODE = Arrays.asList(new String[]{"ENT"});
    @XmlTransient
    private final static List<String> DEFAULT_CONTROL_ACT_PROCESS_ASSIGNED_PERSON_CLASSCODE = Arrays.asList(new String[]{"ASSIGNED"});
    @XmlTransient
    private final static String DEFAULT_CONTROL_ACT_PROCESS_ASSIGNED_PERSON_ID_ROOT = "2.16.840.1.113883.3.51.1.1.7";
    @XmlTransient
    private final static String DEFAULT_PROCESSING_MODE_CODE = "I"; // can't find it!
    @XmlTransient
    private final static String DEFAULT_BUSINESS_USE = "BUS";
    @XmlTransient
    public final static String DEFAULT_REGISTRATION_IDENTIFIER_ROOT = "2.16.840.1.113883.3.51.1.1.6";
    @XmlTransient
    public final static List<String> DEFAULT_SUBJECT_TYPE_CODE = Arrays.asList(new String[]{"SUBJ"});
    @XmlTransient
    private final List<String> DEFAULT_REGISTRATION_EVENT_CLASS_CODE = Arrays.asList(new String[]{"REG"});
    @XmlTransient
    private final List<String> DEFAULT_REGISTRATION_EVENT_MOOD_CODE = Arrays.asList(new String[]{"EVN"});
    @XmlTransient
    private final List<String> DEFAULT_SUBJECT1_TYPECODE = Arrays.asList(new String[]{"SBJ"});
    @XmlTransient
    private final List<String> DEFAULT_IDENTIFIED_PERSON_CLASS_CODE = Arrays.asList(new String[]{"IDENT"});
    @XmlTransient
    public static List<String> DEFAULT_IDENTIFIED_PERSON_INNER_CLASS_CODE = Arrays.asList(new String[]{"PSN"});
    @XmlTransient
    public static String DEFAULT_IDENTIFIED_PERSON_INNER_DETERMINER_CODE = "INSTANCE";
    @XmlTransient
    public static String DEFAULT_RELATIONSHIP_CODE_NEW_MOTHER = "NMTH";
    @XmlTransient
    public static String DEFAULT_RELATIONSHIP_CODE_ASSIGN_AUTH_NAME = "NBPHN";
    @XmlTransient
    public static List<String> DEFAULT_PRIOR_REGISTRATION_CLASS_CODE = Arrays.asList(new String[]{"REG"});
    @XmlTransient
    public static List<String> DEFAULT_PRIOR_REGISTRATION_MOOD_CODE = Arrays.asList(new String[]{"EVN"});
    @XmlTransient
    public static List<String> DEFAULT_REPLACE_OF_TYPE_CODE = Arrays.asList(new String[]{"RPLC"});
    @XmlTransient
    public final static String DEFAULT_OTHER_IDS_IDENTIFIER_ROOT = "2.16.840.1.113883.3.51.1.1.6.4";
    @XmlTransient
    public final static String STATUS_CODE_OBSOLETE = "obsolete";
    @XmlTransient
    public static String DEFAULT_PLAYED_OTHER_IDS_CLASS_CODE = "ROL";
    @XmlTransient
    public final static int DEFAULT_INNER_PERSON_ID_MIN_DIGITS = 7;

    public HCIMINPersonMerged() {
        populateDefaults();
    }

    private void populateDefaults() {
        final ObjectFactory objectFactory = new ObjectFactory();

        // <id>
        id = new II();
        id.setRoot(DEFAULT_IDENTIFIER_ROOT);
        id.setUse(DEFAULT_BUSINESS_USE);
        this.setId(id);

        // <versionCode>
        versionCode = new CS();
        versionCode.setCode(DEFAULT_VERSION_CODE);

        // <interactionId>
        interactionId = new II();
        interactionId.root = DEFAULT_INTERACTION_ID_ROOT;
        interactionId.extension = DEFAULT_PERSON_MERGED_REQUEST_EXTENSION;
//        interactionId.use = DEFAULT_BUSINESS_USE;
//        interactionId.displayable = DEFAULT_INTERACTION_DISPLAYABLE;

        // <processingCode>
        processingCode = new CS();
        processingCode.setCode(DEFAULT_PROCESSING_CODE);

        // <processingModeCode>
        processingModeCode = new CS();
        processingModeCode.setCode(DEFAULT_PROCESSING_MODE_CODE);

        // <acceptAckCode>
        acceptAckCode = new CS();
        acceptAckCode.setCode(DEFAULT_ACCEPT_ACK_CODE);

        // <receiver>
        receiver = new MCCIMT000100BCReceiver();
        receiver.typeCode = DEFAULT_RECEIVER_TYPE_CODE;
        receiver.device = new MCCIMT000100BCDevice();
        receiver.device.determinerCode = DEFAULT_RECEIVER_DEVICE_DETERMINERCODE;
        receiver.device.classCode = DEFAULT_RECEIVER_DEVICE_CLASSCODE;
        receiver.device.id = new II();
        receiver.device.id.root = DEFAULT_RECEIVER_DEVICE_ID_ROOT;
//        receiver.device.id.use = DEFAULT_BUSINESS_USE;
        MCCIMT000100BCAgent receiverAgent = new MCCIMT000100BCAgent();
        receiver.device.asAgent = objectFactory.createMCCIMT000100BCDeviceAsAgent(receiverAgent);
        receiverAgent.classCode = DEFAULT_RECEIVER_AGENT_CLASSCODE;
        MCCIMT000100BCOrganization receiverRepresentedOrganization = new MCCIMT000100BCOrganization();
        receiverAgent.representedOrganization = objectFactory.createMCCIMT000100BCAgentRepresentedOrganization(receiverRepresentedOrganization);
        receiverRepresentedOrganization.determinerCode = DEFAULT_RECEIVER_REPRESENTED_ORG_DETERMINERCODE;
        receiverRepresentedOrganization.classCode = DEFAULT_RECEIVER_REPRESENTED_ORG_CLASSCODE;
        receiverRepresentedOrganization.id = new II();
        receiverRepresentedOrganization.id.root = DEFAULT_RECEIVER_REPRESENTED_ORG_ID_ROOT;
//        receiverRepresentedOrganization.id.use = DEFAULT_BUSINESS_USE;

        // <sender>
        sender = new MCCIMT000100BCSender();
        sender.typeCode = DEFAULT_SENDER_TYPE_CODE;
        sender.device = new MCCIMT000100BCDevice();
        sender.device.determinerCode = DEFAULT_SENDER_DEVICE_DETERMINERCODE;
        sender.device.classCode = DEFAULT_SENDER_DEVICE_CLASSCODE;
        sender.device.id = new II();
        sender.device.id.root = DEFAULT_SENDER_DEVICE_ID_ROOT;
//        sender.device.id.use = DEFAULT_BUSINESS_USE;

        MCCIMT000100BCAgent senderAgent = new MCCIMT000100BCAgent();
        sender.device.asAgent = objectFactory.createMCCIMT000100BCDeviceAsAgent(senderAgent);
        senderAgent.classCode = DEFAULT_SENDER_AGENT_CLASSCODE;
        MCCIMT000100BCOrganization senderRepresentedOrganization = new MCCIMT000100BCOrganization();
        senderAgent.representedOrganization = objectFactory.createMCCIMT000100BCAgentRepresentedOrganization(senderRepresentedOrganization);
        senderRepresentedOrganization.determinerCode = DEFAULT_SENDER_REPRESENTED_ORG_DETERMINERCODE;
        senderRepresentedOrganization.classCode = DEFAULT_SENDER_REPRESENTED_ORG_CLASSCODE;
        senderRepresentedOrganization.id = new II();
        senderRepresentedOrganization.id.root = DEFAULT_SENDER_REPRESENTED_ORG_ID_ROOT;
//        senderRepresentedOrganization.id.use = DEFAULT_BUSINESS_USE;

        // <controlActProcess>
        controlActProcess = new PRPAIN101004CAMFMIMT700799BCControlActProcess();
        controlActProcess.classCode = DEFAULT_CONTROL_ACT_PROCESS_CLASSCODE;
        controlActProcess.moodCode = DEFAULT_CONTROL_ACT_PROCESS_MOODCODE;

        controlActProcess.dataEnterer = new MFMIMT700799BCDataEnterer();
        controlActProcess.dataEnterer.typeCode = DEFAULT_CONTROL_ACT_PROCESS_DATA_ENTERER_TYPECODE;
        controlActProcess.dataEnterer.assignedPerson = new COCTMT090100BCAssignedPerson();
        controlActProcess.dataEnterer.assignedPerson.classCode = DEFAULT_CONTROL_ACT_PROCESS_ASSIGNED_PERSON_CLASSCODE;
        controlActProcess.dataEnterer.assignedPerson.id = new II();
        controlActProcess.dataEnterer.assignedPerson.id.root = DEFAULT_CONTROL_ACT_PROCESS_ASSIGNED_PERSON_ID_ROOT;
//        controlActProcess.dataEnterer.assignedPerson.id.use = DEFAULT_BUSINESS_USE;

        PRPAIN101004CAMFMIMT700799BCSubject subject = new PRPAIN101004CAMFMIMT700799BCSubject();
        controlActProcess.setSubject(subject);
        PRPAIN101004CAMFMIMT700799BCRegistrationEvent registrationEvent = new PRPAIN101004CAMFMIMT700799BCRegistrationEvent();
        subject.setRegistrationEvent(registrationEvent);
        subject.typeCode = DEFAULT_SUBJECT_TYPE_CODE;
        registrationEvent.classCode = DEFAULT_REGISTRATION_EVENT_CLASS_CODE;
        registrationEvent.moodCode = DEFAULT_REGISTRATION_EVENT_MOOD_CODE;
        PRPAIN101004CAMFMIMT700799BCSubject2 subject1 = new PRPAIN101004CAMFMIMT700799BCSubject2();
        registrationEvent.setSubject1(subject1);
        subject1.typeCode = DEFAULT_SUBJECT1_TYPECODE;
        PRPAMT101099BCIdentifiedPerson identifiedPerson = new PRPAMT101099BCIdentifiedPerson();
        subject1.setIdentifiedPerson(identifiedPerson);
        identifiedPerson.classCode = DEFAULT_IDENTIFIED_PERSON_CLASS_CODE;
        //replacement of MRNs
        MFMIMT700799BCReplacementOf replacementOf = new MFMIMT700799BCReplacementOf();
        replacementOf.typeCode = DEFAULT_REPLACE_OF_TYPE_CODE;        
        //prior registration subelement
        MFMIMT700799BCPriorRegistration priorRegistration = new MFMIMT700799BCPriorRegistration();
        priorRegistration.classCode = DEFAULT_PRIOR_REGISTRATION_CLASS_CODE;
        priorRegistration.moodCode = DEFAULT_PRIOR_REGISTRATION_MOOD_CODE;
        CS statusCode = new CS();
        statusCode.setCode(STATUS_CODE_OBSOLETE);
        priorRegistration.setStatusCode(statusCode);
        replacementOf.setPriorRegistration(priorRegistration);
        registrationEvent.getReplacementOf().add(replacementOf);

    }
    
    @Override
    public String getMessageId() {
        return this.id.extension;
    }
    
    @Override
    public CS getProcessModeCode() {
        return this.processingModeCode;
    }

}
