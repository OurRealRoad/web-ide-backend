package com.jinro.webide.login.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Entity
public class Member implements UserDetails {

    @Id
    @Column(name = "member_uuid")
    private String id;
    @Column(name = "member_email")
    private String email;
    @Column(name = "member_name")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "member_password")
    private String password;
    @Column(name = "member_picture")
    private String picture;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "reg_date")
    private String createdDate;
    @Column(name = "chg_date")
    private String updatedDate;

    public Member() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
