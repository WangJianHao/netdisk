/*
 Navicat Premium Data Transfer
 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : quartz
 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001
 Date: 21/04/2023 11:17:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名',
                                       `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                       `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                       `blob_data` blob,
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       INDEX `sched_name`(`sched_name`) USING BTREE,
                                       CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '以blob 类型存储的触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
                                   `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                   `calendar_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                   `calendar` blob NOT NULL,
                                   PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                       `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                       `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                       `cron_expression` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '时间表达式',
                                       `time_zone_id` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '时区id',
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
                                        `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                        `entry_id` varchar(95) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组标识',
                                        `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                        `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                        `instance_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '当前实例的名称',
                                        `fired_time` bigint(0) NOT NULL COMMENT '当前执行时间',
                                        `sched_time` bigint(0) NOT NULL COMMENT '计划时间',
                                        `priority` int(0) NOT NULL COMMENT '权重',
                                        `state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态',
                                        `job_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作业名称',
                                        `job_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作业组',
                                        `is_nonconcurrent` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '是否并行',
                                        `requests_recovery` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '是否要求唤醒',
                                        PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE,
                                        INDEX `idx_qrtz_ft_trig_inst_name`(`sched_name`, `instance_name`) USING BTREE,
                                        INDEX `idx_qrtz_ft_inst_job_req_rcvry`(`sched_name`, `instance_name`, `requests_recovery`) USING BTREE,
                                        INDEX `idx_qrtz_ft_j_g`(`sched_name`, `job_name`, `job_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_jg`(`sched_name`, `job_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_t_g`(`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_tg`(`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '保存已经触发的触发器状态信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
                                     `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                     `job_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称',
                                     `job_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业组',
                                     `description` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
                                     `job_class_name` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业程序类名',
                                     `is_durable` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否持久',
                                     `is_nonconcurrent` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否并行',
                                     `is_update_data` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否更新',
                                     `requests_recovery` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否要求唤醒',
                                     `job_data` blob COMMENT '作业名称',
                                     PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE,
                                     INDEX `idx_qrtz_j_req_recovery`(`sched_name`, `requests_recovery`) USING BTREE,
                                     INDEX `idx_qrtz_j_grp`(`sched_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'job 详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
                               `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                               `lock_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '锁名称',
                               PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '存储程序的悲观锁的信息(假如使用了悲观锁) ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
                                             `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                             `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                             PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '存放暂停掉的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                         `instance_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '实例名称',
                                         `last_checkin_time` bigint(0) NOT NULL COMMENT '最后的检查时间',
                                         `checkin_interval` bigint(0) NOT NULL COMMENT '检查间隔',
                                         PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                         `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                         `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                         `repeat_count` bigint(0) NOT NULL COMMENT '重复次数',
                                         `repeat_interval` bigint(0) NOT NULL COMMENT '重复间隔',
                                         `times_triggered` bigint(0) NOT NULL COMMENT '触发次数',
                                         PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                         CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '简单的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
                                          `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                          `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                          `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                          `str_prop_1` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '计划名称',
                                          `str_prop_2` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '计划名称',
                                          `str_prop_3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '计划名称',
                                          `int_prop_1` int(0) DEFAULT NULL,
                                          `int_prop_2` int(0) DEFAULT NULL,
                                          `long_prop_1` bigint(0) DEFAULT NULL,
                                          `long_prop_2` bigint(0) DEFAULT NULL,
                                          `dec_prop_1` decimal(13, 4) DEFAULT NULL,
                                          `dec_prop_2` decimal(13, 4) DEFAULT NULL,
                                          `bool_prop_1` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                          `bool_prop_2` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                          PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                          CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '存储两种类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
                                  `sched_name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计划名称',
                                  `trigger_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
                                  `trigger_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组',
                                  `job_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称',
                                  `job_group` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业组',
                                  `description` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
                                  `next_fire_time` bigint(0) DEFAULT NULL COMMENT '下次执行时间',
                                  `prev_fire_time` bigint(0) DEFAULT NULL COMMENT '前一次',
                                  `priority` int(0) DEFAULT NULL COMMENT '优先权',
                                  `trigger_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器状态',
                                  `trigger_type` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器类型',
                                  `start_time` bigint(0) NOT NULL COMMENT '开始时间',
                                  `end_time` bigint(0) DEFAULT NULL COMMENT '结束时间',
                                  `calendar_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '日历名称',
                                  `misfire_instr` smallint(0) DEFAULT NULL COMMENT '失败次数',
                                  `job_data` blob COMMENT '作业数据',
                                  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_j`(`sched_name`, `job_name`, `job_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_jg`(`sched_name`, `job_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_c`(`sched_name`, `calendar_name`) USING BTREE,
                                  INDEX `idx_qrtz_t_g`(`sched_name`, `trigger_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_state`(`sched_name`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_n_state`(`sched_name`, `trigger_name`, `trigger_group`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_n_g_state`(`sched_name`, `trigger_group`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_next_fire_time`(`sched_name`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st`(`sched_name`, `trigger_state`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st_misfire_grp`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_group`, `trigger_state`) USING BTREE,
                                  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '触发器表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;