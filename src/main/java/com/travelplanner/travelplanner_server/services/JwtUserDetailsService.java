package com.travelplanner.travelplanner_server.services;

import com.travelplanner.travelplanner_server.model.User;
import com.travelplanner.travelplanner_server.mongodb.dal.UserDAL;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// how to setup UserDetailService https://www.cnblogs.com/zyly/p/12286426.html

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAL userDAL;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAL.findUserByUsername(username);
//        user.setUsername(username);
//        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin")
                );
    }
}
