/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.moh.esb.common.entity;
import java.io.Serializable;

/**
 *
 * @author trevor.schiavone
 */
public class Author implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private String representedOrganization;
    private User user;

    public Author() {
        user = new User();
    }

    /**
     * @return the representedOrganization
     */
    public String getRepresentedOrganization() {
        return representedOrganization;
    }

    /**
     * @param representedOrganization the representedOrganization to set
     */
    public void setRepresentedOrganization(String representedOrganization) {
        this.representedOrganization = representedOrganization;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Author other = (Author) obj;
        if ((this.representedOrganization == null) ? (other.representedOrganization != null) : !this.representedOrganization.equals(other.representedOrganization)) {
            return false;
        }
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        return true;
    }

}
