package model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import daos.UserDAO;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Sara on 9/30/2016.
 */
@Entity
@Table(name="user")
@Indexed
public class User {

    @Id
    private UUID userId;
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    private String emailAddress;
    private String password;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String lastName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonManagedReference
    private List<StoryRecord> storyRecords = new ArrayList<>();

    public User(){
        this.userId = UUID.randomUUID();
    }

    public boolean isPasswordValid(String password){
        return BCrypt.checkpw(password, this.getPassword());
    }

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password){
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void setUserId(UUID userId){
        this.userId = userId;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public String getPassword(){
        return password;
    }

    public UUID getUserId(){
        return userId;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setStoryRecords(List<StoryRecord> storyRecords){
        this.storyRecords = storyRecords;
    }

    public List<StoryRecord> getStoryRecords(){
        return storyRecords;
    }

}
