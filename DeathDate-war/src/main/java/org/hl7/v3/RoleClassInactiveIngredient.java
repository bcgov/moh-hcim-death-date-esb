//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.05 at 09:56:19 AM PDT 
//


package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoleClassInactiveIngredient.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleClassInactiveIngredient">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="IACT"/>
 *     &lt;enumeration value="COLR"/>
 *     &lt;enumeration value="FLVR"/>
 *     &lt;enumeration value="PRSV"/>
 *     &lt;enumeration value="STBL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleClassInactiveIngredient")
@XmlEnum
public enum RoleClassInactiveIngredient {

    IACT,
    COLR,
    FLVR,
    PRSV,
    STBL;

    public String value() {
        return name();
    }

    public static RoleClassInactiveIngredient fromValue(String v) {
        return valueOf(v);
    }

}
