package com.sen.netdisk.service;

import com.sen.netdisk.dto.MailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 17:45
 */
@Slf4j
@Service
public class MailService {

//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username")
//    private String from;
//
//    public MailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    @Async(value = "asyncServiceExecutor")
//    public void sendMail(MailDTO mail) {
//        try {
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
//            if (mail.getFrom() == null || mail.getFrom().isEmpty()) {
//                mail.setFrom("xxx");
//            }
//            //邮件发信人
//            messageHelper.setFrom(from + '(' + mail.getFrom() + ')');
//            //邮件收信人
//            messageHelper.setTo(mail.getTo().split(","));
//            //邮件主题
//            messageHelper.setSubject(mail.getSubject());
//            //邮件内容
//            messageHelper.setText(mail.getText());
//            //发送邮件
//            mailSender.send(messageHelper.getMimeMessage());
//        } catch (Exception e) {
//            log.error("邮件发送失败：{}", e.getMessage(), e);
//        }
//    }


}
