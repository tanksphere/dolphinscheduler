/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.alert.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * 发送邮件
 *
 * @Author zhangchiming
 */
@Slf4j
@Service
public class EmailService {

    @Value("${email.smtp.port:25}")
    private Integer port;
    @Value("${email.smtp.ssl.port:465}")
    private Integer sslPort;
    @Value("${email.smtp.host}")
    private String host;
    @Value("${email.smtp.username}")
    private String username;
    @Value("${email.smtp.password}")
    private String password;

    public EmailService() {}
    public EmailService(int port, int sslPort, String host, String username, String password) {
        this.port = port;
        this.sslPort = sslPort;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * <p>Title: 发送邮件</p>
     * <p>Description: </p>
     *
     * @param to       邮件接收者
     * @param subject  邮件主题
     * @param msgStr      邮件内容
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public void sendEmail(String to, String subject, String msgStr) throws Exception {
        try {
            log.info("发送邮件{}：{}", to, msgStr);
            if (StringUtils.isBlank(to) || StringUtils.isBlank(msgStr)) {
                return;
            }
            Properties props = new Properties();
            String fromMail = username;
            String[] tos = new String[]{to};
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.timeout", "3000");//设置链接超时
            props.put("mail.smtp.port", port);
            props.put("mail.debug", "false");
            if(sslPort!=null&&sslPort!=0){
                props.put("mail.smtp.socketFactory.port", sslPort);
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            Session session = Session.getInstance(props);
            Message msg = new MimeMessage(session);
            InternetAddress address = new InternetAddress(fromMail);
            Address[] tos1 = new Address[tos.length];
            for (int i = 0; i < tos.length; i++) {
                tos1[i] = new InternetAddress(tos[i]);
            }
            msg.setFrom(address);
            msg.setRecipients(Message.RecipientType.TO, tos1);
            msg.setSubject(subject);
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(msgStr);
            mbp1.setContent(msgStr,"text/html;charset=utf-8");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            msg.setContent(mp);
            msg.saveChanges();
            Transport.send(msg, username, password);
            log.info("邮件：" + subject + "发送成功！");
        } catch (Exception e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }

    public void sendFileEmail(String to, String subject, String msg, String filePath) throws Exception {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(filePath);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("excel文件");
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        attachment.setName(fileName);

        try {
            log.info("发送邮件{}：{}", to, msg);
            if (StringUtils.isBlank(to) || StringUtils.isBlank(msg)) {
                return;
            }

            MultiPartEmail mail = new MultiPartEmail();
            // 设置邮箱服务器信息
            mail.setTLS(true);
            mail.setSSL(true);
            mail.setSmtpPort(port);
            mail.setHostName(host);
            // 设置密码验证器
            mail.setAuthentication(username, password);
            // 设置邮件发送者
            mail.setFrom(username);
            // 设置邮件接收者
            // mail.addTo(to);
            String[] toos = to.split(",");
            for(String too : toos) {
                if(too != null && too.trim().length() > 0) {
                    mail.addTo(too.trim());
                }
            }
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject(subject);
            // 设置邮件内容
            mail.setMsg(msg);
            // 附件
            mail.attach(attachment);
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
            log.info("邮件：" + subject + "发送成功！");
        } catch (EmailException e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }

    public void sendFileEmailWithUrl(String name, String pass, String to, String subject, String msg, String url) throws Exception {
        try {
            log.info("发送邮件{}：{}", to, msg);
            if (StringUtils.isBlank(to) || StringUtils.isBlank(msg)) {
                return;
            }

            MultiPartEmail mail = new MultiPartEmail();
            // 设置邮箱服务器信息
            mail.setTLS(true);
            mail.setSSL(true);
            mail.setSmtpPort(port);
            mail.setHostName(host);
            // 设置密码验证器
            if (StringUtils.isBlank(name) && StringUtils.isBlank(pass)) {
            	mail.setAuthentication(username, password);
            	mail.setFrom(username);
            }
            else {
            	 mail.setAuthentication(name, pass);
                 // 设置邮件发送者
                 mail.setFrom(name);
            }
            // 设置邮件接收者
            // mail.addTo(to);
            String[] toos = to.split(",");
            for(String too : toos) {
                if(too != null && too.trim().length() > 0) {
                    mail.addTo(too.trim());
                }
            }
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject(subject);
            // 设置邮件内容
            mail.setMsg(msg);
            // 附件
            if (!StringUtils.isBlank(url)) {
            	log.info("发送给邮件文件url：" + url + "发送成功！");
        		File file = getFileByUrl(url);
        		mail.attach(file);
        	}
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
            log.info("邮件：" + subject + "发送成功！");
        } catch (EmailException e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }

    private File getFileByUrl(String fileUrl) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BufferedOutputStream stream = null;
        InputStream inputStream = null;
        File file = null;
        try {
            URL imageUrl = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            String suffix="";
            if(!fileUrl.substring(fileUrl.lastIndexOf("."), fileUrl.length()).equals(".jpg")){
                suffix=".jpg";
            }else {
                suffix=fileUrl.substring(fileUrl.lastIndexOf("."), fileUrl.length());
            }
            file = File.createTempFile("file", suffix);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fileOutputStream);
            stream.write(outStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (stream != null) {
                    stream.close();
                }
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void sendReminderEmail(SendEmailReq req) throws Exception {

        log.info("开始发送邮件。...");
        Properties props = new Properties();
        String fromMail = "ysd_project@meditrusthealth.cn";
        String[] to = req.getToEmail().split(",");
        String cc = req.getCcEmail();
        // String[] to = new String[] { "zheli.shi@meditrusthealth.com" };
        // String[] cc = new String[] { "zheli.shi@meditrusthealth.com" };
//        String[] to = new String[] { "na.li@meditrusthealth.com","guanyan.li@meditrusthealth.com" };
//        String[] cc = new String[] { "zheli.shi@meditrusthealth.com", "oland.chen@meditrusthealth.com","jie.ma@meditrusthealth.com" };
        final String userName = "ysd_project@meditrusthealth.cn";
        final String pwd = "123Qaz!";
        String host = "smtp.exmail.qq.com";
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.debug", "false");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(props);
        Message msg = new MimeMessage(session);
        try {
            InternetAddress address = new InternetAddress(fromMail);
            msg.setFrom(address);
            Address[] tos = new Address[to.length];
            for (int i = 0; i < to.length; i++) {
                tos[i] = new InternetAddress(to[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, tos);
            if (!StringUtils.isBlank(cc)) {
                String[] ccc = cc.split(",");
                Address[] ccs = new Address[ccc.length];
                for (int i = 0; i < ccc.length; i++) {
                    ccs[i] = new InternetAddress(ccc[i]);
                }
                msg.addRecipients(Message.RecipientType.CC, ccs);
            }
            msg.setSubject(req.getSubject());
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(req.getContent());
            MimeBodyPart mbp2 = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource(req.getFileBytes(), "application/octet-stream");
            DataHandler dataHandler = new DataHandler(ds);
            mbp2.setDataHandler(dataHandler);
            mbp2.setFileName(req.getFileName());
//            mbp2.attachFile(filePath);
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            msg.setContent(mp);
            msg.saveChanges();
            Transport.send(msg, userName, pwd);
            log.info("已成功发送邮件。...");
        } catch (Exception e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }

    public void sendCCEmail(String to, String subject, String msgStr,String cc) throws Exception {
        try {
            log.info("发送邮件{}：{}", to, msgStr);
            if (StringUtils.isBlank(to) || StringUtils.isBlank(msgStr)) {
                return;
            }
            Properties props = new Properties();
            String fromMail = username;
            String[] tos = to.split(",");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.timeout", "3000");//设置链接超时
            props.put("mail.smtp.port", port);
            props.put("mail.debug", "false");
            if(sslPort!=null&&sslPort!=0){
                props.put("mail.smtp.socketFactory.port", sslPort);
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            Session session = Session.getInstance(props);
            Message msg = new MimeMessage(session);
            InternetAddress address = new InternetAddress(fromMail);
            Address[] tos1 = new Address[tos.length];
            for (int i = 0; i < tos.length; i++) {
                tos1[i] = new InternetAddress(tos[i]);
            }
            msg.setFrom(address);
            msg.setRecipients(Message.RecipientType.TO, tos1);
            if (!StringUtils.isBlank(cc)) {
                String[] ccc = cc.split(",");
                Address[] ccs = new Address[ccc.length];
                for (int i = 0; i < ccc.length; i++) {
                    ccs[i] = new InternetAddress(ccc[i]);
                }
                msg.addRecipients(Message.RecipientType.CC, ccs);
            }
            msg.setSubject(subject);
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(msgStr);
            mbp1.setContent(msgStr,"text/html;charset=utf-8");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            msg.setContent(mp);
            msg.saveChanges();
            Transport.send(msg, username, password);
            log.info("邮件：" + subject + "发送成功！");
        } catch (Exception e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }

    public void sendCCFileEmail(SendEmailReq req) throws Exception {
        String to = req.getToEmail();
        String cc = req.getCcEmail();
        String subject = req.getSubject();
        String msg = req.getContent();
        String filePath = req.getFileName();
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(filePath);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("excel文件");
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        attachment.setName(fileName);

        try {
            log.info("发送邮件{},抄送{}：{}", to,cc, msg);
            if (StringUtils.isBlank(to) || StringUtils.isBlank(msg)) {
                return;
            }

            MultiPartEmail mail = new MultiPartEmail();
            // 设置邮箱服务器信息
            mail.setTLS(true);
            mail.setSSL(true);
            mail.setSmtpPort(port);
            mail.setHostName(host);
            // 设置密码验证器
            mail.setAuthentication(username, password);
            // 设置邮件发送者
            mail.setFrom(username);
            // 设置邮件接收者
            // mail.addTo(to);
            String[] toos = to.split(",");
            for(String too : toos) {
                if(too != null && too.trim().length() > 0) {
                    mail.addTo(too.trim());
                }
            }
            // 设置邮件抄送者
            String[] ccs = cc.split(",");
            for(String csr : ccs) {
                if(csr != null && csr.trim().length() > 0) {
                    mail.addCc(csr.trim());
                }
            }
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject(subject);
            // 设置邮件内容
            mail.setMsg(msg);
            // 附件
            mail.attach(attachment);
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
            log.info("邮件：" + subject + "发送成功！");
        } catch (EmailException e) {
            log.error("发送邮件失败，失败原因：" + e.getMessage(), e);
            throw new Exception("发送邮件失败");
        }
    }
}
