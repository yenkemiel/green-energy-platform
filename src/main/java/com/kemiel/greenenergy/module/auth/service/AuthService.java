package com.kemiel.greenenergy.module.auth.service;

import com.kemiel.greenenergy.module.auth.dto.LoginRequest;
import com.kemiel.greenenergy.module.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
