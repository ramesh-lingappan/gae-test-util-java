package com.github.rameshl.appengine.testing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String userId;

    private String email;

    private String authDomain;

    private boolean isAdmin;

    private boolean isLoggedIn;

    /**
     * Create user user info.
     *
     * @param email  the email
     * @param userId the user id
     * @return the user info
     */
    public static UserInfo createUser(String email, String userId) {
        String authDomain = email.substring(email.indexOf('@') + 1);
        return new UserInfo(userId, email, authDomain, false, true);
    }

    /**
     * Create admin user info.
     *
     * @param email  the email
     * @param userId the user id
     * @return the user info
     */
    public static UserInfo createAdmin(String email, String userId) {
        String authDomain = email.substring(email.indexOf('@') + 1);
        return new UserInfo(userId, email, authDomain, true, true);
    }

    /**
     * Logged out user info.
     *
     * @return the user info
     */
    public static UserInfo loggedOut() {
        return new UserInfo("", "", "", false, false);
    }

}