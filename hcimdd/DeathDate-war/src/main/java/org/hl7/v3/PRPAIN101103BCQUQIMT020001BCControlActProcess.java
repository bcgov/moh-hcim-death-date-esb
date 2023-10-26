//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.05 at 09:56:19 AM PDT 
//
package org.hl7.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.hl7.v3.api.JaxbRequestControlActProcess;

/**
 * <p>
 * Java class for PRPA_IN101103BC.QUQI_MT020001BC.ControlActProcess complex
 * type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="PRPA_IN101103BC.QUQI_MT020001BC.ControlActProcess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="dataEnterer" type="{urn:hl7-org:v3}QUQI_MT020001BC.DataEnterer"/>
 *         &lt;element name="queryByParameter" type="{urn:hl7-org:v3}PRPA_IN101103BC.QUQI_MT020001BC.QueryByParameter" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="classCode" type="{urn:hl7-org:v3}ActClass" fixed="CACT" />
 *       &lt;attribute name="moodCode" type="{urn:hl7-org:v3}ActMood" fixed="EVN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PRPA_IN101103BC.QUQI_MT020001BC.ControlActProcess", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "dataEnterer",
    "queryByParameter"
})
public class PRPAIN101103BCQUQIMT020001BCControlActProcess
        implements JaxbRequestControlActProcess {

    protected List<CS> realmCode;
    protected II typeId;
    protected List<II> templateId;
    @XmlElement(required = true)
    protected QUQIMT020001BCDataEnterer dataEnterer;
    @XmlElementRef(name = "queryByParameter", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<PRPAIN101103BCQUQIMT020001BCQueryByParameter> queryByParameter;
    @XmlAttribute(name = "classCode")
    protected List<String> classCode;
    @XmlAttribute(name = "moodCode")
    protected List<String> moodCode;

    /**
     * Gets the value of the realmCode property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the realmCode property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealmCode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link CS }
     *
     *
     */
    public List<CS> getRealmCode() {
        if (realmCode == null) {
            realmCode = new ArrayList<CS>();
        }
        return this.realmCode;
    }

    /**
     * Gets the value of the typeId property.
     *
     * @return possible object is {@link II }
     *
     */
    public II getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     *
     * @param value allowed object is {@link II }
     *
     */
    public void setTypeId(II value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the templateId property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the templateId property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTemplateId().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link II }
     *
     *
     */
    public List<II> getTemplateId() {
        if (templateId == null) {
            templateId = new ArrayList<II>();
        }
        return this.templateId;
    }

    /**
     * Gets the value of the dataEnterer property.
     *
     * @return possible object is {@link QUQIMT020001BCDataEnterer }
     *
     */
    public QUQIMT020001BCDataEnterer getDataEnterer() {
        return dataEnterer;
    }

    /**
     * Sets the value of the dataEnterer property.
     *
     * @param value allowed object is {@link QUQIMT020001BCDataEnterer }
     *
     */
    public void setDataEnterer(QUQIMT020001BCDataEnterer value) {
        this.dataEnterer = value;
    }

    /**
     * Gets the value of the queryByParameter property.
     *
     * @return possible object is
     * {@link JAXBElement }{@code <}{@link PRPAIN101103BCQUQIMT020001BCQueryByParameter }{@code >}
     *
     */
    public JAXBElement<PRPAIN101103BCQUQIMT020001BCQueryByParameter> getQueryByParameter() {
        return queryByParameter;
    }

    /**
     * Sets the value of the queryByParameter property.
     *
     * @param value allowed object is
     * {@link JAXBElement }{@code <}{@link PRPAIN101103BCQUQIMT020001BCQueryByParameter }{@code >}
     *
     */
    public void setQueryByParameter(JAXBElement<PRPAIN101103BCQUQIMT020001BCQueryByParameter> value) {
        this.queryByParameter = value;
    }

    /**
     * Gets the value of the classCode property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the classCode property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassCode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getClassCode() {
        if (classCode == null) {
            classCode = new ArrayList<String>();
        }
        return this.classCode;
    }

    /**
     * Gets the value of the moodCode property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the moodCode property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMoodCode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getMoodCode() {
        if (moodCode == null) {
            moodCode = new ArrayList<String>();
        }
        return this.moodCode;
    }

}