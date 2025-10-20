package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String username;
    private String email;
}