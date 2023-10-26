/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.moh.death.service;

import ca.bc.gov.moh.death.converter.BatchFileToRevisePersonEntityConverter;
import ca.bc.gov.moh.death.entity.GenderValues;
import ca.bc.gov.moh.death.entity.IdentifierTypes;
import ca.bc.gov.moh.death.transaction.RevisePerson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
public class BatchFileToRevisePersonEntityConverterTest {

    private static List<BatchFile> batchFiles;
    private static BatchFile batchFile;

    @BeforeClass
    public static void initBatchFile() throws IOException {
        batchFiles = new ArrayList<>();

        //Good values
        BatchFile batchFile1 = new BatchFile();
        batchFile1.setPhn("9458754621");
        batchFile1.setSurnm("Smeagol");
        batchFile1.setFirst("Gimli");
        batchFile1.setSecnd("Legolas");
        batchFile1.setThird("Aragorn");
        batchFile1.setSex("M");
        batchFile1.setBirth("18450210");
        batchFile1.setDeath("19240214");
        batchFile1.setPostal("postal"); //Won't be used
        batchFile1.setTimestamp("154685135487465"); //Won't be used
        batchFile1.setStubExtension("plop");
        batchFiles.add(batchFile1);

        //Wrong Values
        BatchFile batchFile2 = new BatchFile();
        batchFile2.setPhn("9458754621");
        batchFile2.setSurnm("DarthVader1");
        batchFile2.setFirst("Luke2");
        batchFile2.setSecnd("Leia3");
        batchFile2.setThird("Han4");
        batchFile2.setSex("D");
        batchFile2.setBirth("02105786");
        batchFile2.setDeath("DFERT214");
        batchFile2.setPostal("postal"); //Won't be used
        batchFile2.setTimestamp("654787135487465"); //Won't be used
        batchFile2.setStubExtension("plip");
        batchFiles.add(batchFile2);
        
        //Everything null
        BatchFile batchFile3 = new BatchFile();
        batchFile3.setPhn(null);
        batchFile3.setSurnm(null);
        batchFile3.setFirst(null);
        batchFile3.setSecnd(null);
        batchFile3.setThird(null);
        batchFile3.setSex(null);
        batchFile3.setBirth(null);
        batchFile3.setDeath(null);
        batchFile3.setPostal(null); //Won't be used
        batchFile3.setTimestamp(null); //Won't be used
        batchFile3.setStubExtension(null);
        batchFiles.add(batchFile3);
        
        
        //Not included in the list (testing Female)
        batchFile = new BatchFile();
        batchFile.setPhn("9458754621");
        batchFile.setSurnm("Smeagol");
        batchFile.setFirst("Gimli");
        batchFile.setSecnd("Legolas");
        batchFile.setThird("Aragorn");
        batchFile.setSex("F");
        batchFile.setBirth("18450210");
        batchFile.setDeath("19240214");
        batchFile.setPostal("postal"); //Won't be used
        batchFile.setTimestamp("154685135487465"); //Won't be used
        batchFile.setStubExtension("plop");
    }

    /**
     * Test of convert2 method, of class BatchFileToRevisePersonEntityConverter.
     */
    @Test
    public void testConvert2() {
        BatchFileToRevisePersonEntityConverter converter = new BatchFileToRevisePersonEntityConverter();
        List<RevisePerson> result = converter.convert2(batchFiles);
        
        //First Batch File
        assertEquals(1, result.get(0).getPerson().getIdentifier().size());
        assertEquals(batchFiles.get(0).getPhn(), result.get(0).getPerson().getIdentifier().get(0).getValue());
        assertEquals(IdentifierTypes.BCPHN, result.get(0).getPerson().getIdentifier().get(0).getType());
        assertEquals(batchFiles.get(0).getPhn(), result.get(0).getPerson().getPHN());
        
        assertEquals(1, result.get(0).getPerson().getName().size());
        assertEquals(batchFiles.get(0).getFirst(), result.get(0).getPerson().getName().get(0).getFirstName());
        assertEquals(batchFiles.get(0).getSecnd(), result.get(0).getPerson().getName().get(0).getMiddleName());
        assertEquals(batchFiles.get(0).getThird(), result.get(0).getPerson().getName().get(0).getTitle());
        assertEquals(batchFiles.get(0).getSurnm(), result.get(0).getPerson().getName().get(0).getLastName());
        
        assertEquals(batchFiles.get(0).getBirth(), result.get(0).getPerson().getBirthDate().getRawValue());
        assertEquals(null, result.get(0).getPerson().getBirthDate().getValue());
        assertEquals(batchFiles.get(0).getDeath(), result.get(0).getPerson().getDeathDate().getRawValue());
        assertEquals(null, result.get(0).getPerson().getDeathDate().getValue());
        
        assertEquals(true, result.get(0).getPerson().getDeathVerified().getValue());
        
        assertEquals(GenderValues.Male, result.get(0).getPerson().getGender().getValue());

        assertEquals(null, result.get(0).getSender().getSystemName());
        assertEquals(null, result.get(0).getSender().getOrganization());
        assertEquals(1, result.get(0).getReceiver().size());
        assertEquals("BCHCIM", result.get(0).getReceiver().get(0).getSystemName());
        assertEquals("BCHCIM", result.get(0).getReceiver().get(0).getOrganization());
        
        assertEquals(null, result.get(0).getAuthor().getUser().getUserId());
        
        assertNotEquals(batchFiles.get(0).getTimestamp(), result.get(0).getEventTime().getTime());

        assertEquals(batchFiles.get(0).getStubExtension(), result.get(0).getStubExtension());
        
        //Second Batch File
        assertEquals(1, result.get(1).getPerson().getIdentifier().size());
        assertEquals(batchFiles.get(1).getPhn(), result.get(1).getPerson().getIdentifier().get(0).getValue());
        assertEquals(IdentifierTypes.BCPHN, result.get(1).getPerson().getIdentifier().get(0).getType());
        assertEquals(batchFiles.get(1).getPhn(), result.get(1).getPerson().getPHN());
        
        assertEquals(1, result.get(1).getPerson().getName().size());
        assertEquals(batchFiles.get(1).getFirst(), result.get(1).getPerson().getName().get(0).getFirstName());
        assertEquals(batchFiles.get(1).getSecnd(), result.get(1).getPerson().getName().get(0).getMiddleName());
        assertEquals(batchFiles.get(1).getThird(), result.get(1).getPerson().getName().get(0).getTitle());
        assertEquals(batchFiles.get(1).getSurnm(), result.get(1).getPerson().getName().get(0).getLastName());
        
        assertEquals(batchFiles.get(1).getBirth(), result.get(1).getPerson().getBirthDate().getRawValue());
        assertEquals(null, result.get(1).getPerson().getBirthDate().getValue());
        assertEquals(batchFiles.get(1).getDeath(), result.get(1).getPerson().getDeathDate().getRawValue());
        assertEquals(null, result.get(1).getPerson().getDeathDate().getValue());
        
        assertEquals(true, result.get(1).getPerson().getDeathVerified().getValue());
        
        assertEquals(GenderValues.Unknown, result.get(1).getPerson().getGender().getValue());

        assertEquals(null, result.get(1).getSender().getSystemName());
        assertEquals(null, result.get(1).getSender().getOrganization());
        assertEquals(1, result.get(1).getReceiver().size());
        assertEquals("BCHCIM", result.get(1).getReceiver().get(0).getSystemName());
        assertEquals("BCHCIM", result.get(1).getReceiver().get(0).getOrganization());
        
        assertEquals(null, result.get(1).getAuthor().getUser().getUserId());
        
        assertNotEquals(batchFiles.get(1).getTimestamp(), result.get(1).getEventTime().getTime());

        assertEquals(batchFiles.get(1).getStubExtension(), result.get(1).getStubExtension());
        
        //Third Batch File
        assertEquals(1, result.get(2).getPerson().getIdentifier().size());
        assertEquals(batchFiles.get(2).getPhn(), result.get(2).getPerson().getIdentifier().get(0).getValue());
        assertEquals(IdentifierTypes.BCPHN, result.get(2).getPerson().getIdentifier().get(0).getType());
        assertEquals(batchFiles.get(2).getPhn(), result.get(2).getPerson().getPHN());
        
        assertEquals(1, result.get(2).getPerson().getName().size());
        assertEquals(batchFiles.get(2).getFirst(), result.get(2).getPerson().getName().get(0).getFirstName());
        assertEquals(batchFiles.get(2).getSecnd(), result.get(2).getPerson().getName().get(0).getMiddleName());
        assertEquals(batchFiles.get(2).getThird(), result.get(2).getPerson().getName().get(0).getTitle());
        assertEquals(batchFiles.get(2).getSurnm(), result.get(2).getPerson().getName().get(0).getLastName());
        
        assertEquals(batchFiles.get(2).getBirth(), result.get(2).getPerson().getBirthDate().getRawValue());
        assertEquals(null, result.get(2).getPerson().getBirthDate().getValue());
        assertEquals(batchFiles.get(2).getDeath(), result.get(2).getPerson().getDeathDate().getRawValue());
        assertEquals(null, result.get(2).getPerson().getDeathDate().getValue());
        
        assertEquals(true, result.get(2).getPerson().getDeathVerified().getValue());
        
        assertEquals(GenderValues.Unknown, result.get(2).getPerson().getGender().getValue());

        assertEquals(null, result.get(2).getSender().getSystemName());
        assertEquals(null, result.get(2).getSender().getOrganization());
        assertEquals(1, result.get(2).getReceiver().size());
        assertEquals("BCHCIM", result.get(2).getReceiver().get(0).getSystemName());
        assertEquals("BCHCIM", result.get(2).getReceiver().get(0).getOrganization());
        
        assertEquals(null, result.get(2).getAuthor().getUser().getUserId());
        
        assertNotEquals(batchFiles.get(2).getTimestamp(), result.get(2).getEventTime().getTime());

        assertEquals(batchFiles.get(2).getStubExtension(), result.get(2).getStubExtension());

    }

    /**
     * Test of convert method, of class BatchFileToRevisePersonEntityConverter.
     */
    @Test
    public void testConvert_BatchFile() {
        BatchFileToRevisePersonEntityConverter converter = new BatchFileToRevisePersonEntityConverter();
        RevisePerson result = converter.convert(batchFile);
        
        //First Batch File
        assertEquals(1, result.getPerson().getIdentifier().size());
        assertEquals(batchFiles.get(0).getPhn(), result.getPerson().getIdentifier().get(0).getValue());
        assertEquals(IdentifierTypes.BCPHN, result.getPerson().getIdentifier().get(0).getType());
        assertEquals(batchFiles.get(0).getPhn(), result.getPerson().getPHN());
        
        assertEquals(1, result.getPerson().getName().size());
        assertEquals(batchFiles.get(0).getFirst(), result.getPerson().getName().get(0).getFirstName());
        assertEquals(batchFiles.get(0).getSecnd(), result.getPerson().getName().get(0).getMiddleName());
        assertEquals(batchFiles.get(0).getThird(), result.getPerson().getName().get(0).getTitle());
        assertEquals(batchFiles.get(0).getSurnm(), result.getPerson().getName().get(0).getLastName());
        
        assertEquals(batchFiles.get(0).getBirth(), result.getPerson().getBirthDate().getRawValue());
        assertEquals(null, result.getPerson().getBirthDate().getValue());
        assertEquals(batchFiles.get(0).getDeath(), result.getPerson().getDeathDate().getRawValue());
        assertEquals(null, result.getPerson().getDeathDate().getValue());
        
        assertEquals(true, result.getPerson().getDeathVerified().getValue());
        
        assertEquals(GenderValues.Female, result.getPerson().getGender().getValue());

        assertEquals(null, result.getSender().getSystemName());
        assertEquals(null, result.getSender().getOrganization());
        assertEquals(1, result.getReceiver().size());
        assertEquals("BCHCIM", result.getReceiver().get(0).getSystemName());
        assertEquals("BCHCIM", result.getReceiver().get(0).getOrganization());
        
        assertEquals(null, result.getAuthor().getUser().getUserId());
        
        assertNotEquals(batchFiles.get(0).getTimestamp(), result.getEventTime().getTime());

        assertEquals(batchFiles.get(0).getStubExtension(), result.getStubExtension());
                
        assertEquals(1, result.getAuditableIdentifiers().size());
        assertEquals(IdentifierTypes.BCPHN, result.getAuditableIdentifiers().get(0).getType());
        assertEquals(batchFiles.get(0).getPhn(), result.getAuditableIdentifiers().get(0).getValue());
        assertEquals(null, result.getMessageGroupId());
        assertEquals(0, result.getAuditableResponseMessageList().size());
        
    }

}
