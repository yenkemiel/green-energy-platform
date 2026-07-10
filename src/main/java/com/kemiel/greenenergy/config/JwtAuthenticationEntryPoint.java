package com.kemiel.greenenergy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 未認證例外處理器，當請求未帶有效 Token 時回傳 401 與統一錯誤格式
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 處理未認證請求，回傳 HTTP 401 與 ApiResponse 格式的錯誤訊息
     *
     * @param request       HTTP 請求
     * @param response      HTTP 回應
     * @param authException 未認證例外
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            objectMapper.writeValueAsString(ApiResponse.error(ErrorCode.UNAUTHORIZED))
        );
    }
}
