/*
 * *********************************************************************************************************************
 *  Copyright (c) 2018, Ministry of Health, BC.                                                 *
 *                                                                                                                     *
 *  All rights reserved.                                                                                               *
 *    This information contained herein may not be used in whole                                                       *
 *    or in part without the express written consent of the                                                            *
 *    Government of British Columbia, Canada.                                                                          *
 *                                                                                                                     *
 *  Revision Control Information                                                                                       *
 *  File:                $Id::                                                                                       $ *
 *  Date of Last Commit: $Date::                                                                                     $ *
 *  Revision Number:     $Rev::                                                                                      $ *
 *  Last Commit by:      $Author::                                                                                   $ *
 *                                                                                                                     *
 * *********************************************************************************************************************
 */
package ca.bc.gov.moh.esb.util.audit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Handles persistence for ESB database audits.
 * 
 * @author joshua.burton
 */
public class AuditProcessor {
    
    public <T> T insert(T record) {
        EntityManager em = Persistence.createEntityManagerFactory("ESBPU").createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        em.persist(record);
        et.commit();
        
        return record;
    }
    
}

