package com.travelplanner.travelplanner_server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(collection="user")
@Getter
@Setter
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    //@Transient
    private String passwordConfirmation;
    private String firstname;
    private String lastname;
    private String email;
    private String profileUrlId;

    @DBRef
    private List<String> plans;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updatedAt;

    // constructor
    public User(String username, String password, String passwordConfirmation, String firstname, String lastname, String email, String profileUrlId){
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.profileUrlId = profileUrlId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
