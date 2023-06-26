/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.moh.esb.common.entity;

import java.io.Serializable;
/**
 * In HSA/RTrans ESB projects this class extended a class Person but nothing ever used it. 
 * Removed the extends Person when porting this class into Core
 * @author trevor.schiavone
 */
public class User implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private String userId;
    
    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}