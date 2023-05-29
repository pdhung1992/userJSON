package com.user;

public class MainClass {
    public static void main(String[] args) {
        UserManagement userManagement = new UserManagement();
        try {
            userManagement.readUserAPI();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
