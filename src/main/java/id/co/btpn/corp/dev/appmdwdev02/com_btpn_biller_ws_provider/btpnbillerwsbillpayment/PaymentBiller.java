//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.13 at 11:41:31 AM ICT 
//


package id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paymentBiller complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paymentBiller">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="commonParam" type="{http://APPMDWDEV02.dev.corp.btpn.co.id/com.btpn.biller.ws.provider:BtpnBillerWsBillPayment}CommonParam2"/>
 *         &lt;element name="debitType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="unitId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountNo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="billerCustNo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="institutionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalData1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalData2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalData3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paymentBiller", propOrder = {
    "commonParam",
    "debitType",
    "unitId",
    "accountNo",
    "billerCustNo",
    "institutionCode",
    "productID",
    "additionalData1",
    "additionalData2",
    "additionalData3",
	"processingCodeBiller"
})
@XmlRootElement(name = "paymentBiller")
public class PaymentBiller {

    @XmlElement(required = true, nillable = true)
    protected CommonParam2 commonParam;
    @XmlElement(required = true, nillable = true)
    protected String debitType;
    @XmlElement(required = true, nillable = true)
    protected String unitId;
    @XmlElement(required = true, nillable = true)
    protected String accountNo;
    @XmlElement(required = true, nillable = true)
    protected String billerCustNo;
    @XmlElement(required = true, nillable = true)
    protected String institutionCode;
    @XmlElement(required = true, nillable = true)
    protected String productID;
    @XmlElement(required = true, nillable = true)
    protected String additionalData1;
    @XmlElement(required = true, nillable = true)
    protected String additionalData2;
    @XmlElement(required = true, nillable = true)
    protected String additionalData3;
	@XmlElement(required = true, nillable = true)
    protected String processingCodeBiller;

    /**
     * Gets the value of the commonParam property.
     * 
     * @return
     *     possible object is
     *     {@link CommonParam2 }
     *     
     */
    public CommonParam2 getCommonParam() {
        return commonParam;
    }

    /**
     * Sets the value of the commonParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonParam2 }
     *     
     */
    public void setCommonParam(CommonParam2 value) {
        this.commonParam = value;
    }

    /**
     * Gets the value of the debitType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebitType() {
        return debitType;
    }

    /**
     * Sets the value of the debitType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebitType(String value) {
        this.debitType = value;
    }

    /**
     * Gets the value of the unitId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * Sets the value of the unitId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitId(String value) {
        this.unitId = value;
    }

    /**
     * Gets the value of the accountNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * Sets the value of the accountNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNo(String value) {
        this.accountNo = value;
    }

    /**
     * Gets the value of the billerCustNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillerCustNo() {
        return billerCustNo;
    }

    /**
     * Sets the value of the billerCustNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillerCustNo(String value) {
        this.billerCustNo = value;
    }

    /**
     * Gets the value of the institutionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutionCode() {
        return institutionCode;
    }

    /**
     * Sets the value of the institutionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutionCode(String value) {
        this.institutionCode = value;
    }

    /**
     * Gets the value of the productID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductID() {
        return productID;
    }

    /**
     * Sets the value of the productID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductID(String value) {
        this.productID = value;
    }

    /**
     * Gets the value of the additionalData1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalData1() {
        return additionalData1;
    }

    /**
     * Sets the value of the additionalData1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalData1(String value) {
        this.additionalData1 = value;
    }

    /**
     * Gets the value of the additionalData2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalData2() {
        return additionalData2;
    }

    /**
     * Sets the value of the additionalData2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalData2(String value) {
        this.additionalData2 = value;
    }

    /**
     * Gets the value of the additionalData3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalData3() {
        return additionalData3;
    }

    /**
     * Sets the value of the additionalData3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalData3(String value) {
        this.additionalData3 = value;
    }
	
	/**
     * Gets the value of the processingCodeBiller property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessingCodeBiller() {
        return processingCodeBiller;
    }

    /**
     * Sets the value of the processingCodeBiller property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessingCodeBiller(String value) {
        this.processingCodeBiller = value;
    }

}
