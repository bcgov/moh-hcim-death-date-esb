package ca.bc.gov.moh.death.service;

import java.io.Serializable;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

/**
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
@FixedLengthRecord(ignoreTrailingChars = true, length = 214)
public class BatchFile implements Serializable {

    public static final int RECORD_LENGTH = 214;
    
    @DataField(pos = 1, trim=true, length = 10)
    private String phn;
    @DataField(pos = 2, trim=true, length = 50)
    private String surnm;
    @DataField(pos = 3, trim=true, length = 25)
    private String first;
    @DataField(pos = 4, trim=true, length = 25)
    private String secnd;
    @DataField(pos = 5, trim=true, length = 25)
    private String third;
    @DataField(pos = 6, trim=true, length = 1)
    private String sex;
    @DataField(pos = 7, trim=true, length = 10)
    private String birth;
    @DataField(pos = 8, trim=true, length = 10)
    private String death;
    @DataField(pos = 9, trim=true, length = 7)
    private String postal;
    @DataField(pos = 10, trim=true, length = 26)
    private String timestamp;
    @DataField(pos = 11, trim=true, length = 25)
    private String stubExtension;

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }

    public String getSurnm() {
        return surnm;
    }

    public void setSurnm(String surnm) {
        this.surnm = surnm;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecnd() {
        return secnd;
    }

    public void setSecnd(String secnd) {
        this.secnd = secnd;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDeath() {
        return death;
    }

    public void setDeath(String death) {
        this.death = death;
    }

    public String getStubExtension() {
        return stubExtension;
    }

    public void setStubExtension(String stubExtension) {
        this.stubExtension = stubExtension;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
