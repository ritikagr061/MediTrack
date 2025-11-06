package com.meditrack.authservice.service;

import com.meditrack.authservice.dto.UserLoginRequest;
import com.meditrack.authservice.dto.UserLoginResponse;
import com.meditrack.authservice.dto.UserRegisterResponse;
import com.meditrack.authservice.entity.UserEntity;
import com.meditrack.authservice.jwt.JwtService;
import com.meditrack.authservice.repository.UserLoginSignupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserLoginSignupRepo repo;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public UserLoginResponse login(UserLoginRequest request){
        UserEntity currentUser = null;

        //handling the case where user passes emailId instead of userName so userName will be empty
        if(request.getUserName()==null||request.getUserName().trim().isEmpty()){
            currentUser=gerUserByEmail(request.getEmailId());
            request.setUserName(currentUser.getUserName());
        }
        UserLoginResponse ans = new UserLoginResponse();
        try {
            /*In the login method, authManager.authenticate() triggers Spring Security, which calls MyUserDetailService.loadUserByUsername()
             to fetch user data from DB. It returns a UserPrincipal (implementing UserDetails). Spring Security then internally uses
             encoder.matches(raw, hashed) to validate the password. If authentication passes, a JWT token is generated and returned.
             but how does spring know that I have used bcrypt with the strength of 11 in this project?
             when you encode a password, BCrypt stores the strength inside the hash itself! The resulting string looks like: $2a$10$Kwefp0xZbBYmFYlfh72hWeED0KZRkceEjh9W3vWh.1F4KzTrHnXhO  ← the `11` is embedded right after `$2a$`
            */
/*           you could have question such as how does AuthenticationManager know details of database basically we have MyUserDetailService
             which implements UserDetailsService and is annotated with @Service so spring automatically picks it up as a bean
             and AuthenticationManager internally uses this bean to fetch user details from DB.
             Also, UserDetailsService has only one method loadUserByUsername which returns UserDetails object which has multiple methods 2 of these methods are getUsername() and getPassword()
*/
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
            if (auth.isAuthenticated()) {
                if(currentUser==null)
                    currentUser = getUser(request.getUserName());

                if (currentUser != null) {
                    ans.setMainCode(200);
                    ans.setMessage("user fetched successfully");
                    ans.setUserName(currentUser.getUserName());
                    ans.setEmailId(currentUser.getEmailId());
                    //ans.setRoles(currentUser.getRoles());
                } else {
                    ans.setMainCode(404);
                    ans.setMessage("user not found");
                }
                String token = jwtService.generateToken(request.getUserName(), currentUser);
                ans.setToken(token);
                return ans;
            } else {
                ans.setMainCode(404);
                ans.setMessage("user not found");
                return ans;
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public UserEntity getUser(String userName){
        //UserLoginResponse ans = new UserLoginResponse();
        Optional<UserEntity> user=repo.findByUserName(userName);
        return user.orElse(null);
    }

    public UserEntity gerUserByEmail(String emailId){
        Optional<UserEntity> user=repo.findByEmailId(emailId);
        return user.orElse(null);
    }

    public UserRegisterResponse saveUser(UserEntity myUser){
        UserRegisterResponse ans = new UserRegisterResponse();
        if(repo.existsByUserName(myUser.getUserName())){
            ans.setMessage("userName has already been taken");
            ans.setMainCode(403);
            ans.setErrorMessage("userName has already been taken");
            return ans;
        }
        if(repo.existsByEmailId(myUser.getEmailId())){
            ans.setMessage("account with the email id already exists");
            ans.setMainCode(403);
            ans.setErrorMessage("account with the email id already exists");
            return ans;
        }
        String currPassword=myUser.getPassword();
        myUser.setPassword(encoder.encode(currPassword)); //while storing we are converting the password to hash
        //note that while authenticating the user password(done by spring security in the project)
        //we do not encrypt the password again and check instead we internally use : encoder.matches(enteredPasswordInEnglish, storedHashedPassword);
        //in $2a$10$Kwefp0xZbBYmFYlfh72hWeED0KZRkceEjh9W3vWh.1F4KzTrHnXhO , 2a is the algo of bycrypt, 10 is the strength, after $ first 22 chars are the salt(base 64 encoded) and remaining is the hashed password(again base 64).
        //reason is because every time the hash changes because of different random salt.
        UserEntity newMyUser = repo.save(myUser);
        ans.setMainCode(200);
        ans.setMessage("The account with userName "+newMyUser.getUserName()+" has been registered. Please login to your account");
        return ans;
    }


}
