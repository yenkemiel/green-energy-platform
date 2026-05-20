package com.kemiel.greenenergy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 權限不足例外處理器，當已登入使用者存取無權限資源時回傳 403 與統一錯誤格式。
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 處理權限不足請求，回傳 HTTP 403 與 ApiResponse 格式的錯誤訊息。
     *
     * @param request               HTTP 請求
     * @param response              HTTP 回應
     * @param accessDeniedException 權限不足例外
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(ApiResponse.success(ErrorCode.FORBIDDEN))
        );
    }


}
