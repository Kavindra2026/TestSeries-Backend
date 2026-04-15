package com.textseries.dto;

import com.textseries.model.User;
import lombok.Data;

@Data
public class RegisterRequest {

    private User user;
    private String otp;

}