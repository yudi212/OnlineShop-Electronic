package com.yudiind.OnlineShop_Electronic.security;

import com.yudiind.OnlineShop_Electronic.model.entity.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String password;
    private final String email;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    @Autowired
    public UserPrincipal(Long id, String password, String email, boolean active, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user){

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getPassword(),
                user.getEmail(),
                user.isActive(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return active;
    }
}
