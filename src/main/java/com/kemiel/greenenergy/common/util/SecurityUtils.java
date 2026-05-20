package com.kemiel.greenenergy.common.util;

import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 從 SecurityContext 取得當前使用者資訊的工具類
 */
public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * 取得當前登入使用者的 userId
     *
     * @return userId
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }
}
