//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.26 at 03:25:04 PM ICT 
//


package id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for topupBillerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="topupBillerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bankTransactionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalData" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "topupBillerResponse", propOrder = {
    "responseCode",
    "responseDesc",
    "bankTransactionID",
    "additionalData"
})
@XmlRootElement(name = "topupBillerResponse")
public class TopupBillerResponse {

    @XmlElement(required = true, nillable = true)
    protected String responseCode;
    @XmlElement(required = true, nillable = true)
    protected String responseDesc;
    @XmlElement(required = true, nillable = true)
    protected String bankTransactionID;
    @XmlElement(required = true, nillable = true)
    protected String additionalData;

    /**
     * Gets the value of the responseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the value of the responseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCode(String value) {
        this.responseCode = value;
    }

    /**
     * Gets the value of the responseDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseDesc() {
        return responseDesc;
    }

    /**
     * Sets the value of the responseDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseDesc(String value) {
        this.responseDesc = value;
    }

    /**
     * Gets the value of the bankTransactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankTransactionID() {
        return bankTransactionID;
    }

    /**
     * Sets the value of the bankTransactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankTransactionID(String value) {
        this.bankTransactionID = value;
    }

    /**
     * Gets the value of the additionalData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the value of the additionalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalData(String value) {
        this.additionalData = value;
    }

}
