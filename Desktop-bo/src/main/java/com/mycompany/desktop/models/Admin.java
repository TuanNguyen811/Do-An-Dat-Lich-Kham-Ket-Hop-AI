/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;

/**
 *
 * @author admin
 */
public class Admin extends User{
    private int admin_id;

    public Admin() {
        super();
    }

    public Admin(int admin_id) {
        this.admin_id = admin_id;
    }

    public Admin(int admin_id, int user_id, String email, String full_name, String phone, String date_of_birth, String gender, String address, String avatar_url, String password) {
        super(user_id, email, full_name, phone, date_of_birth, gender, address, avatar_url, password);
        this.admin_id = admin_id;
    }

    
    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }
  
}
