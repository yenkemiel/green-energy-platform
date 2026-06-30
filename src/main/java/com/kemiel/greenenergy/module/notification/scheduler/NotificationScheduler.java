package com.kemiel.greenenergy.module.notification.scheduler;

import com.kemiel.greenenergy.common.enums.NotificationType;
import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.module.contract.entity.Contract;
import com.kemiel.greenenergy.module.contract.mapper.ContractMapper;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import com.kemiel.greenenergy.module.notification.entity.Notification;
import com.kemiel.greenenergy.module.notification.mapper.NotificationMapper;
import com.kemiel.greenenergy.module.notification.service.NotificationService;
import com.kemiel.greenenergy.module.solar.mapper.SolarMonthlyRecordMapper;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知排程，負責 MISSING_DATA 與 CONTRACT_EXPIRY 兩種定期觸發通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final UserMapper userMapper;
    private final SolarMonthlyRecordMapper solarMonthlyRecordMapper;
    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;
    private final ContractMapper contractMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;

    /**
     * 每月 1 號 08:00 執行，檢查上月太陽能或用電量是否未填，寫入 MISSING_DATA 通知給 EMPLOYEE
     */
    @Scheduled(cron = "0 0 8 1 * *")
    public void checkMissingData() {
        log.info("執行 MISSING_DATA 排程");

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();

        boolean solarFilled = solarMonthlyRecordMapper.existsByYearAndMonth(year, month);
        ElectricityUsageRecord usageRecord =
                electricityUsageRecordMapper.selectByYearAndMonth(year, month);
        boolean usageFilled = (usageRecord != null);

        if (solarFilled && usageFilled) {
            log.info("上月資料已完整，跳過 MISSING_DATA 通知");
            return;
        }

        List<String> missingItems = new ArrayList<>();
        if (!solarFilled) {
            missingItems.add(String.format("太陽能發電（%d-%02d）", year, month));
        }
        if (!usageFilled) {
            missingItems.add(String.format("用電量（%d-%02d）", year, month));
        }

        String message = String.format("%d 年 %d 月尚未填寫：%s，請盡快補填。",
                year, month, String.join("、", missingItems));

        List<User> employees = userMapper.selectList(RoleType.EMPLOYEE);
        for (User employee : employees) {
            Notification notification = new Notification();
            notification.setType(NotificationType.MISSING_DATA.name());
            notification.setTitle("資料未填寫提醒");
            notification.setMessage(message);
            notification.setRecipientId(employee.getId());
            notificationMapper.insert(notification);
        }

        log.info("MISSING_DATA 通知已發送，共 {} 位 EMPLOYEE", employees.size());
    }

    /**
     * 每日 08:00 執行，檢查 30 天內即將到期合約，寫入 CONTRACT_EXPIRY 通知給 MANAGER，
     * 同合約同收件人只觸發一次
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkContractExpiry() {
        log.info("執行 CONTRACT_EXPIRY 排程");

        LocalDate thresholdDate = LocalDate.now().plusDays(30);
        List<Contract> expiringContracts = contractMapper.selectExpiringContracts(thresholdDate);

        if (expiringContracts.isEmpty()) {
            log.info("無即將到期合約，跳過 CONTRACT_EXPIRY 通知");
            return;
        }

        List<User> managers = userMapper.selectList(RoleType.MANAGER);
        int sentCount = 0;

        for (Contract contract : expiringContracts) {
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate());
            String message = String.format(
                    "%s 的 %s 合約將於 %s 到期（剩餘 %d 天），請提前規劃續約。",
                    contract.getSupplierName(),
                    contract.getContractType(),
                    contract.getEndDate(),
                    daysUntilExpiry);

            for (User manager : managers) {
                if (notificationService.existsContractExpiryNotification(contract.getId(), manager.getId())) {
                    continue;
                }

                Notification notification = new Notification();
                notification.setType(NotificationType.CONTRACT_EXPIRY.name());
                notification.setTitle("合約即將到期提醒");
                notification.setMessage(message);
                notification.setRecipientId(manager.getId());
                notification.setRefId(contract.getId());
                notification.setRefType("CONTRACT");
                notificationMapper.insert(notification);
                sentCount++;
            }
        }

        log.info("CONTRACT_EXPIRY 通知已發送，共 {} 筆（已排除重複）", sentCount);
    }
}