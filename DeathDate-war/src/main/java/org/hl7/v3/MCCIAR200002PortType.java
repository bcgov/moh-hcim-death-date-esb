
package org.hl7.v3;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "MCCI_AR200002_PortType", targetNamespace = "urn:hl7-org:v3")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface MCCIAR200002PortType {


    /**
     * 
     * @param body
     * @return
     *     returns org.hl7.v3.MCCIIN200101BC
     */
    @WebMethod(operationName = "MCCI_IN200100", action = "urn:hl7-org:v3/MCCI_IN200100")
    @WebResult(name = "MCCI_IN200101BC", targetNamespace = "urn:hl7-org:v3", partName = "body")
    public MCCIIN200101BC mcciIN200100(
        @WebParam(name = "MCCI_IN200100BC", targetNamespace = "urn:hl7-org:v3", partName = "body")
        MCCIIN200100BC body);

}
