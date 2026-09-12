package com.example.cab302project.Authentication;

public interface IUserDAO {
    boolean registerUser(User user);
    User loginUser(String username, String password);
    boolean emailExists(String email);
    boolean usernameExists(String username);
    User getUserId(int userId);
    boolean updateEmail(int userId, String email);
    boolean updateUsername(int userId, String username);
    boolean updatePassword(int userId, String password);
}