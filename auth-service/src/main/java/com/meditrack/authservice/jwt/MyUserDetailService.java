package com.meditrack.authservice.jwt;

import com.meditrack.authservice.entity.UserEntity;
import com.meditrack.authservice.repository.UserLoginSignupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    UserLoginSignupRepo repo;
    private UserEntity myUserObj;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = repo.findByUserName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with user name " + username + " was not found");
        }

        this.myUserObj = user.get();
        //we want to return an Object of type UserDetails which is an interface.
        //therefore we create UserPrincipal which implements UserDetails
        return new UserPrincipal(myUserObj);
    }
}
