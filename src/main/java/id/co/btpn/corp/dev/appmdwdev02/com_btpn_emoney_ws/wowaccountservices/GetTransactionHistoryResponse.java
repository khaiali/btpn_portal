//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.13 at 11:41:31 AM ICT 
//


package id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowaccountservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTransactionHistoryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTransactionHistoryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getTransactionHistoryOutput" type="{http://APPMDWDEV02.dev.corp.btpn.co.id/com.btpn.emoney.ws:wowAccountServices}getTransactionHistoryOutput"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransactionHistoryResponse", propOrder = {
    "getTransactionHistoryOutput"
})
@XmlRootElement(name = "getTransactionHistoryResponse")
public class GetTransactionHistoryResponse {

    @XmlElement(required = true, nillable = true)
    protected GetTransactionHistoryOutput getTransactionHistoryOutput;

    /**
     * Gets the value of the getTransactionHistoryOutput property.
     * 
     * @return
     *     possible object is
     *     {@link GetTransactionHistoryOutput }
     *     
     */
    public GetTransactionHistoryOutput getGetTransactionHistoryOutput() {
        return getTransactionHistoryOutput;
    }

    /**
     * Sets the value of the getTransactionHistoryOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetTransactionHistoryOutput }
     *     
     */
    public void setGetTransactionHistoryOutput(GetTransactionHistoryOutput value) {
        this.getTransactionHistoryOutput = value;
    }

}
