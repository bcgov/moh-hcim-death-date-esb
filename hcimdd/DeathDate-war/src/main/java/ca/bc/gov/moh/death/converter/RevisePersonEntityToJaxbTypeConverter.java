package ca.bc.gov.moh.death.converter;

import ca.bc.gov.moh.death.entity.DateAttribute;
import ca.bc.gov.moh.death.entity.GenderAttribute;
import ca.bc.gov.moh.death.entity.IdentifierAttribute;
import ca.bc.gov.moh.death.entity.Person;
import ca.bc.gov.moh.death.entity.PersonNameAttribute;
import ca.bc.gov.moh.death.transaction.RevisePerson;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.camel.Converter;
import org.apache.cxf.common.util.StringUtils;
import org.hl7.v3.BL;
import org.hl7.v3.CE;
import org.hl7.v3.CS;
import org.hl7.v3.HCIMINPersonRevised;
import org.hl7.v3.II;
import org.hl7.v3.NullFlavor;
import org.hl7.v3.PN;
import org.hl7.v3.PRPAMT101002BCIdentifiedPerson;
import org.hl7.v3.PRPAMT101002BCPerson;
import org.hl7.v3.TS;

/**
 *
 * @author conrad.gustafson
 */
@Converter
public class RevisePersonEntityToJaxbTypeConverter extends EntityToJaxbTypeConverter {

    @Converter
    public static HCIMINPersonRevised convert(RevisePerson request)
            throws InstantiationException, IllegalAccessException {

        HCIMINPersonRevised jaxb = new HCIMINPersonRevised();

        convertCommonFields(jaxb, request);
        setStatusCode(jaxb);
        setPersonId(jaxb, request);
        setIdentifiedPerson(jaxb, request);

        return jaxb;
    }

    private static PRPAMT101002BCIdentifiedPerson getIdentifiedPerson(HCIMINPersonRevised jaxb) {
        return jaxb.getControlActProcess().getSubject().getRegistrationEvent().getSubject1().getIdentifiedPerson();
    }

    private static void setStatusCode(HCIMINPersonRevised jaxb) {
        CS statusCodeCS = new CS();
        statusCodeCS.setCode(STATUS_CODE_ACTIVE);
        jaxb.getControlActProcess().getSubject().getRegistrationEvent().setStatusCode(statusCodeCS);
    }

    private static void setPersonId(HCIMINPersonRevised jaxb, RevisePerson request) {
        List<IdentifierAttribute> personIdentifierList = request.getPerson().getIdentifier();
        if (personIdentifierList == null || personIdentifierList.isEmpty()) {
            return;
        }

        PRPAMT101002BCIdentifiedPerson identifiedPerson = getIdentifiedPerson(jaxb);

        boolean found = false;
        II personIdValue = new II();
        for (IdentifierAttribute personIdentifier : personIdentifierList) {
            if (!found) {
//            if (!found && personIdentifier.isMRN()) {
                String mrn = personIdentifier.getValue();
                personIdValue.setExtension(mrn);
                personIdValue.setRoot(HCIMINPersonRevised.DEFAULT_REGISTRATION_IDENTIFIER_ROOT);
                found = true;
            }
        }

        identifiedPerson.setId(personIdValue);
    }

    private static void setIdentifiedPersonEffectiveTime(PRPAMT101002BCIdentifiedPerson identifiedPerson, RevisePerson request) {

        Date eventTime = request.getEventTime();
        if (eventTime != null) {
            TS ts = new TS();
            ts.setValue(convertToTS(eventTime));
            identifiedPerson.setEffectiveDate(ts);
        }
    }

    /**
     * Set values from the entity RP request to JAXB specific structure. Added
     * conditions to set playedOtherIds subelements for the alternate
     * identifiers
     *
     * @param jaxb HCIMINPersonRevised
     * @param request RevisePerson entity business class
     * @throws InstantiationException, IllegalAccessException
     */
    private static void setIdentifiedPerson(HCIMINPersonRevised jaxb, RevisePerson request)
            throws InstantiationException, IllegalAccessException {
        // Initialize identifiedPersonInner
        PRPAMT101002BCIdentifiedPerson identifiedPerson = getIdentifiedPerson(jaxb);
        PRPAMT101002BCPerson identifiedPersonInner = identifiedPerson.getIdentifiedPerson();
        if (identifiedPersonInner == null) {
            identifiedPersonInner = new PRPAMT101002BCPerson();
            identifiedPersonInner.setClassCode(HCIMINPersonRevised.DEFAULT_IDENTIFIED_PERSON_INNER_CLASS_CODE);
            identifiedPersonInner.setDeterminerCode(HCIMINPersonRevised.DEFAULT_IDENTIFIED_PERSON_INNER_DETERMINER_CODE);
            identifiedPerson.setIdentifiedPerson(identifiedPersonInner);
        }
        setID(identifiedPersonInner, request.getPerson());
        setGender(identifiedPersonInner, request.getPerson());
        setBirthDate(identifiedPersonInner, request.getPerson());
        setDeathDate(identifiedPersonInner, request.getPerson());
        setName(identifiedPersonInner, request.getPerson());
        setIdentifiedPersonEffectiveTime(identifiedPerson, request);
        
    }

    private static void setGender(PRPAMT101002BCPerson identifiedPersonInner, Person person) {
        GenderAttribute genderAttribute = person.getGender();
        if (genderAttribute == null) {
            return;
        }
        CE ce = convertGenderAttributeToCE(genderAttribute);
        identifiedPersonInner.setAdministrativeGenderCode(ce);
    }

    private static void setBirthDate(PRPAMT101002BCPerson identifiedPersonInner, Person person) {
        DateAttribute birthDateAttribute = person.getBirthDate();
        TS birthDateTS = convertDateAttributeToTS(birthDateAttribute);
        identifiedPersonInner.setBirthTime(birthDateTS);
    }

    private static void setDeathDate(PRPAMT101002BCPerson identifiedPersonInner, Person person) {
        DateAttribute deathDateAttribute = person.getDeathDate();
        TS deathDateTS = convertDateAttributeToTS(deathDateAttribute);
        identifiedPersonInner.setDeceasedTime(deathDateTS);
        
        BL deathIndicator = convertBooleanAttributeToBL(person.getDeathVerified());
        identifiedPersonInner.setDeceasedInd(deathIndicator);
    }

    private static void setName(PRPAMT101002BCPerson identifiedPersonInner, Person person) {
        List<PersonNameAttribute> nameAttributeList = person.getName();

        if (nameAttributeList == null || nameAttributeList.isEmpty()) {
            return;
        }

        List<PN> namePNList = identifiedPersonInner.getName();
        if (namePNList == null) {
            namePNList = new ArrayList<>();
            identifiedPersonInner.setName(namePNList);
        }

        for (PersonNameAttribute personNameAttribute : nameAttributeList) {
            PN pn = convertNameAttributeToPN(personNameAttribute);
            namePNList.add(pn);
        }
    }

    private static void setID(PRPAMT101002BCPerson identifiedPersonInner, Person person) {
        List<IdentifierAttribute> identifier = person.getIdentifier();

        II idII = new II();
        idII.setRoot(HCIMINPersonRevised.DEFAULT_INNER_PERSON_ID_ROOT);
        idII.setUse(HCIMINPersonRevised.DEFAULT_BUSINESS_USE);

        boolean phnFound = false;

        for (IdentifierAttribute identifierAttribute : identifier) {

            if (identifierAttribute.isBCPHN()) {
                String value = identifierAttribute.getValue();
                if (!StringUtils.isEmpty(value)) {
                    idII.setExtension(value);
                    phnFound = true;
                }
            }
        }

        if (!phnFound) {
            idII.setNullFlavor(NullFlavor.NI);
        }

        identifiedPersonInner.setId(idII);
    }
}
