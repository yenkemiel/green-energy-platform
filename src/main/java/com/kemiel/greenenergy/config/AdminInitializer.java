package com.kemiel.greenenergy.config;

import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 系統啟動時自動初始化 ADMIN 帳號
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.init-password:admin123}")
    private String adminInitPassword;

    @Override
    public void run(ApplicationArguments args){
        User existing = userMapper.selectByUsername("admin");
        if (existing != null) {
            log.info("ADMIN 帳號已存在，略過初始化");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode(adminInitPassword));
        admin.setDisplayName("系統管理者");
        admin.setRole(RoleType.ADMIN);
        admin.setIsActive(true);

        userMapper.insert(admin);
        log.info("ADMIN 帳號初始化完成");
    }
}
