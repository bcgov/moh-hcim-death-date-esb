package ca.bc.gov.moh.death.converter;

import ca.bc.gov.moh.death.transaction.RevisePerson;
import ca.bc.gov.moh.death.entity.BooleanAttribute;
import ca.bc.gov.moh.death.entity.GenderAttribute;
import ca.bc.gov.moh.death.entity.GenderValues;
import ca.bc.gov.moh.death.entity.IdentifierAttribute;
import ca.bc.gov.moh.death.entity.IdentifierTypes;
import ca.bc.gov.moh.death.entity.PersonNameAttribute;
import ca.bc.gov.moh.death.entity.DateAttribute;
import ca.bc.gov.moh.death.service.BatchFile;
import ca.bc.gov.moh.esb.common.entity.Author;
import ca.bc.gov.moh.esb.common.entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.camel.Converter;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
public class BatchFileToRevisePersonEntityConverter {
    
    @Value("${senderSystem}")
    private String senderSystem;
    @Value("${senderOrg}")
    private String senderOrg;
    @Value("${userId}")
    private String userId;
    
    private static final String MALE = "M";
    private static final String FEMALE = "F";
    

    @Converter
    public List<RevisePerson> convert2(List<BatchFile> batchFile) {
        
        List<RevisePerson> revisePersonList = new ArrayList<>();
        
        for (BatchFile singleFile : batchFile) {
            revisePersonList.add(convert(singleFile));
        }
        
        return revisePersonList;
    }
    
    @Converter
    public RevisePerson convert(BatchFile batchFile) {
        RevisePerson revisePerson = new RevisePerson();
        
        revisePerson.setCreationTime(new Date());
        revisePerson.setMessageId(UUID.randomUUID().toString());
        
        //Set the identifier
        revisePerson.getPerson().getIdentifier().add(new IdentifierAttribute(batchFile.getPhn(), "", IdentifierTypes.BCPHN));
       
        //Set the Names
        PersonNameAttribute personNameAttribute = new PersonNameAttribute();
        
        personNameAttribute.setFirstName(batchFile.getFirst());
        personNameAttribute.setMiddleName(batchFile.getSecnd());
        personNameAttribute.setTitle(batchFile.getThird());
        personNameAttribute.setLastName(batchFile.getSurnm());
  
        revisePerson.getPerson().getName().add(personNameAttribute);

        //Set birth and death dates
        String birthDate = batchFile.getBirth() == null ? null : batchFile.getBirth().replace("-", "");
        String deathDate = batchFile.getDeath() == null ? null : batchFile.getDeath().replace("-", "");
        revisePerson.getPerson().setBirthDate(new DateAttribute(birthDate));
        revisePerson.getPerson().setDeathDate(new DateAttribute(deathDate));
        
        //Set the death date indicator
        revisePerson.getPerson().setDeathVerified(new BooleanAttribute(true));
        
        //Set sex. Anything other than M and F should be mapped to Unknown
        if(batchFile.getSex() != null){
            switch (batchFile.getSex()) {
                case MALE:
                    revisePerson.getPerson().setGender(new GenderAttribute(GenderValues.Male));
                    break;
                case FEMALE:
                    revisePerson.getPerson().setGender(new GenderAttribute(GenderValues.Female));
                    break;
                default:
                    revisePerson.getPerson().setGender(new GenderAttribute(GenderValues.Unknown));
                    break;
            }
        }else{
            revisePerson.getPerson().setGender(new GenderAttribute(GenderValues.Unknown));
        }
        
        //Set sender and receiver
        revisePerson.getSender().setSystemName(senderSystem);
        revisePerson.getSender().setOrganization(senderOrg);
        revisePerson.getReceiver().get(0).setSystemName("BCHCIM");
        revisePerson.getReceiver().get(0).setOrganization("BCHCIM");
              
        //Set the author
        revisePerson.setAuthor(new Author());
        User user = new User();
        user.setUserId(userId);
        revisePerson.getAuthor().setUser(user);
        
        revisePerson.setEventTime(new Date());
        
        //Set HCIM-Stub extension
        if (batchFile.getStubExtension() != null) {
            revisePerson.setStubExtension(batchFile.getStubExtension().trim());
        }
        return revisePerson;
    }

}
