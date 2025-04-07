package com.example.myapplicationpep;

public class UserSessionSingleton {
    private boolean isLogged = false;
    private String email;
    private static UserSessionSingleton instance;

    private UserSessionSingleton(){}

    public static synchronized UserSessionSingleton getInstance(){

        if(instance == null)
            instance = new UserSessionSingleton();
        return instance;

    }

    public void setIsLogged(boolean isLogged){this.isLogged = isLogged;}



    public boolean getIsLogged() {
        return isLogged;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
