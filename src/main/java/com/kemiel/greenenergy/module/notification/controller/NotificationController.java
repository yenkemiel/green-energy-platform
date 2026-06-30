package com.kemiel.greenenergy.module.notification.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.notification.dto.NotificationResponse;
import com.kemiel.greenenergy.module.notification.dto.UnreadCountResponse;
import com.kemiel.greenenergy.module.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系統內通知 Controller
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "系統內通知模組")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-050] 查詢通知清單")
    public ResponseEntity<ApiResponse<PageResult<NotificationResponse>>> listNotifications(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long recipientId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.listNotifications(recipientId, type, isRead, page, size)));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-051] 未讀通知數量")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount() {
        Long recipientId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(recipientId)));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-052] 標記通知已讀")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        Long recipientId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(id, recipientId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-053] 全部標記已讀")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long recipientId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
