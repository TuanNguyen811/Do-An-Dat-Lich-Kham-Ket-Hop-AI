/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author admin
 */
public class TokenResponse {
     @SerializedName("access_token")
    private String accessToken;
     
    @SerializedName("user_id")
    private int user_id;
    
    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getUser_id() {
        return user_id;
    }
    

    public String getFullToken() {
        return tokenType + " " + accessToken;
    } 
}
