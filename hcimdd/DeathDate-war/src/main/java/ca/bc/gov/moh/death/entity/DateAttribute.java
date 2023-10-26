package ca.bc.gov.moh.death.entity;

import java.util.Date;

/**
 * used to store date and time information for a record.
 *
 * @author patrick.weckermann
 * @version 1.0
 * @created 26-Aug-2014 2:49:31 PM
 */
public class DateAttribute extends Attribute {

    private static final long serialVersionUID = 7526472295622776147L;

    private Date value;
    private String rawValue;

    public DateAttribute() {

    }

    public DateAttribute(Date value) {
        this.value = value;
    }
    
    public DateAttribute(String rawValue) {
        this.rawValue = rawValue;
    }

    /**
     * @return the value
     */
    public Date getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Date value) {
        this.value = value;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }
    
    
}//end DateAttribute
