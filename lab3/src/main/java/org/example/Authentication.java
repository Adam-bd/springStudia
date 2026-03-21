package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {
    private IUserRepository userRepository;


    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);

        if(user == null) {
            return null;
        }

        String hashedPassword = DigestUtils.sha256Hex(password);
        if(hashedPassword.equals(user.getPassword())) {
            return user;
        }

        return null;
    }

    public static String hashPassword(String password) {
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
    }
}
