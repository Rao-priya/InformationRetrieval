package services;

import javax.inject.*;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserManagement {
    public static class UserDetails{
        String userName;
        String email;
        String password;
    }

    private Map<String, UserDetails> userDetailsMap= new HashMap<>();

    @Inject
    public UserManagement() {

    }


    public boolean signupUser(String userName, String email, String password) {
        if(!userDetailsMap.containsKey(userName)){
            final UserDetails userDetails = new UserDetails();
            userDetails.userName = userName;
            userDetails.email = email;
            userDetails.password = password;
            userDetailsMap.put(userName, userDetails);
            return true;
        } else {
            return false;
        }
    }

    public boolean signInUser(String userName, String password) {
        return userDetailsMap.containsKey(userName) && userDetailsMap.get(userName).password.equals(password);
    }



}
