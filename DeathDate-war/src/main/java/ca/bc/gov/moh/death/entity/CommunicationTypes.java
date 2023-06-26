package ca.bc.gov.moh.death.entity;

/**
 * @author patrick.weckermann
 * @version 1.0
 * @created 26-Aug-2014 2:49:31 PM
 */
public enum CommunicationTypes {

    Home;

    public String value() {
        return name();
    }

    public static CommunicationTypes fromV2Value(String v) {
        if (v == null) {
            return null;
        }
        if (v.equalsIgnoreCase("PH")) {
            return Home;
        }  else {
            return null;
        }
    }

    public static CommunicationTypes fromValue(String v) {
        if (v == null) {
            return null;
        }
        if (v.equalsIgnoreCase("H")) {
            return Home;
        } else {
            return null;
        }
    }
}
