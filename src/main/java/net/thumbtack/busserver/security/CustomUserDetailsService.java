package net.thumbtack.busserver.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.model.UserSecurity;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserSecurity(user.getLogin(), user.getPassword(), user.isEnabled(), Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())));
    }
    
}
