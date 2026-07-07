package com.kemiel.greenenergy.module.auth.service;

import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.config.JwtUtil;
import com.kemiel.greenenergy.module.auth.dto.LoginRequest;
import com.kemiel.greenenergy.module.auth.dto.LoginResponse;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 認證服務，處理使用者登入邏輯
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 使用者登入，驗證帳號密碼並回傳 JWT Token；帳號不存在或已停用一律回傳
     * INVALID_CREDENTIALS，不透露具體原因，避免帳號列舉攻擊
     */
    @Override
    public LoginResponse login(LoginRequest request){
        log.info("使用者登入，username={}", request.getUsername());

        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null || !user.getIsActive()) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        log.info("登入成功，userId={}, role={}", user.getId(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .build();
    }
}
