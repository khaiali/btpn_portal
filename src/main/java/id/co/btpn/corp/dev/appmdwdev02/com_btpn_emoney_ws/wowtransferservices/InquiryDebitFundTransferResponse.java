//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.13 at 11:41:31 AM ICT 
//


package id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for inquiryDebitFundTransferResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inquiryDebitFundTransferResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="feeCurrency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sourceHolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="beneficiaryHolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inquiryDebitFundTransferResponse", propOrder = {
    "fee",
    "feeCurrency",
    "sourceHolderName",
    "beneficiaryHolderName",
    "responseCode",
    "responseDesc"
})
@XmlRootElement(name = "inquiryDebitFundTransferResponse")
public class InquiryDebitFundTransferResponse {

    @XmlElement(required = true, nillable = true)
    protected String fee;
    @XmlElement(required = true, nillable = true)
    protected String feeCurrency;
    @XmlElement(required = true, nillable = true)
    protected String sourceHolderName;
    @XmlElement(required = true, nillable = true)
    protected String beneficiaryHolderName;
    @XmlElement(required = true, nillable = true)
    protected String responseCode;
    @XmlElement(required = true, nillable = true)
    protected String responseDesc;

    /**
     * Gets the value of the fee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFee() {
        return fee;
    }

    /**
     * Sets the value of the fee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFee(String value) {
        this.fee = value;
    }

    /**
     * Gets the value of the feeCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeeCurrency() {
        return feeCurrency;
    }

    /**
     * Sets the value of the feeCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeCurrency(String value) {
        this.feeCurrency = value;
    }

    /**
     * Gets the value of the sourceHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceHolderName() {
        return sourceHolderName;
    }

    /**
     * Sets the value of the sourceHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceHolderName(String value) {
        this.sourceHolderName = value;
    }

    /**
     * Gets the value of the beneficiaryHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeneficiaryHolderName() {
        return beneficiaryHolderName;
    }

    /**
     * Sets the value of the beneficiaryHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeneficiaryHolderName(String value) {
        this.beneficiaryHolderName = value;
    }

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

}
