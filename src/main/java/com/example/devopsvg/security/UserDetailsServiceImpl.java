package com.example.devopsvg.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Användare hittades inte. ");
        }

        return new ConcreteUserDetails(user);
    }

    public Boolean userAlreadyExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public String createUser(String username, String password) {
        StringBuilder errorMess = new StringBuilder();
        errorMess.append(validUsername(username));
        errorMess.append(validPassword(password));

        if (errorMess.isEmpty()) {
            return addUser(username, password, "Admin");
        } else {
            return errorMess.toString();
        }
    }

    private String addUser(String username, String password, String group) {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(group));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        User user = User.builder().enabled(true).password(hash).username(username).roles(roles).build();
        userRepository.save(user);

        User findUser = userRepository.findByUsername(username);
        if (findUser != null) {
            return "Konto " + username + " har skapats.";
        } else {
            return "Något gick fel. Kontot kunde inte skapas. ";
        }
    }

    public String validUsername(String username) {
        StringBuilder errorMessage = new StringBuilder();
        if (username.length() < 2) {
            errorMessage.append("Användarnamn för kort. ");
        }
        if (username.length() > 20) {
            errorMessage.append("Användarnamn för långt. ");
        }
        return errorMessage.toString();
    }

    public String validPassword(String password) {
        StringBuilder errorMessage = new StringBuilder();

        if (password.length() < 8) {
            errorMessage.append("Lösenord för kort (minst 8 tecken). ");
        }
        if (password.length() > 40) {
            errorMessage.append("Lösenord för långt (max 40 tecken). ");
        }

        boolean hasDigit = false;
        boolean hasLetter = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (specialCharacters.indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }
        if (!hasDigit) {
            errorMessage.append("Lösenord måste innehålla siffra. ");
        }
        if (!hasLetter) {
            errorMessage.append("Lösenord måste innehålla bokstav. ");
        }
        if (!hasSpecialChar) {
            errorMessage.append("Lösenord måste innehållet specialtecken. ");
        }

        return errorMessage.toString();
    }
}