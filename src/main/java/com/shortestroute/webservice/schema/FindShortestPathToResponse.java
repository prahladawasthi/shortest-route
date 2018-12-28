
package com.shortestroute.webservice.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="distanceTravelled" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "path",
    "distanceTravelled"
})
@XmlRootElement(name = "findShortestPathToResponse", namespace = "http://spring.io/guides/gs-producing-web-service")
public class FindShortestPathToResponse {

    @XmlElement(namespace = "http://spring.io/guides/gs-producing-web-service", required = true)
    protected String path;
    @XmlElement(namespace = "http://spring.io/guides/gs-producing-web-service", required = true)
    protected String distanceTravelled;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the distanceTravelled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    /**
     * Sets the value of the distanceTravelled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistanceTravelled(String value) {
        this.distanceTravelled = value;
    }

}
