package com.example.cab302project.Authentication;

public interface IUserDAO {
    boolean registerUser(User user);
    User loginUser(String username, String password);
    boolean emailExists(String email);
    boolean usernameExists(String username);
}