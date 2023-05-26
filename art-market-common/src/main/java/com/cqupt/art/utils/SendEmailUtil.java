package com.cqupt.art.utils;


import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Author lihongxing
 * @Date 2023/5/26 21:40
 */
public class SendEmailUtil {

    public static boolean send_qqmail(List<String> recipients, String subject, String content) {
        boolean flag = false;
        try {
            final Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.163.com");
            props.put("mail.user", "17325795684@163.com");
            props.put("mail.password", "*******");
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.starttls.enable", "true");
            // 设置邮箱认证
            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session session = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(session);
            // 设置发件人
            String username = props.getProperty("mail.user");

            InternetAddress fromAddress = new InternetAddress(username);
            message.setFrom(fromAddress);

            final int num = recipients.size();
            InternetAddress[] addresses = new InternetAddress[num];
            for (int i = 0; i < num; i++) {
                addresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(MimeMessage.RecipientType.TO, addresses);
            // 设置邮件标题
            message.setSubject(subject);
            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            flag = true;
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main(String[] args) {
        System.out.println("start……");
        boolean flag = send_qqmail(Arrays.asList("17898643582@qq.com", "18705799124@163.com"), "××剩余量提醒", "邮件内容");
        if(flag){
            System.out.println("发送成功！");
        }
    }

}
