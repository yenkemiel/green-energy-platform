package com.kemiel.greenenergy.module.notification.mapper;

import com.kemiel.greenenergy.module.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系統內通知 Mapper
 */
@Mapper
public interface NotificationMapper {

    List<Notification> selectList(@Param("recipientId") Long recipientId,
                                  @Param("type") String type,
                                  @Param("isRead") Integer isRead);

    Notification selectById(Long id);

    long countUnread(@Param("recipientId") Long recipientId);

    boolean existsSolarAnomaly(@Param("deviceId") Long deviceId,
                               @Param("recordYear") Integer recordYear,
                               @Param("recordMonth") Integer recordMonth);

    boolean existsContractExpiry(@Param("contractId") Long contractId,
                                 @Param("recipientId") Long recipientId);

    int insert(Notification notification);

    int updateReadById(@Param("id") Long id);

    int updateReadAllByRecipient(@Param("recipientId") Long recipientId);
}
