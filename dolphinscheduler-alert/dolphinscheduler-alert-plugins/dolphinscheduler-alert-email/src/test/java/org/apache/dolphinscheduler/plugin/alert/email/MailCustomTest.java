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

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.dolphinscheduler.alert.api.AlertConstants;
import org.apache.dolphinscheduler.alert.api.ShowType;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.alert.email.template.AlertTemplate;
import org.apache.dolphinscheduler.plugin.alert.email.template.DefaultHTMLTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Disabled("The test case makes no sense")
public class MailCustomTest {

    @Test
            void testSendEmail() throws MessagingException, GeneralSecurityException {
                //创建一个配置文件并保存
                Properties properties = new Properties();

                properties.setProperty("mail.host","smtp.163.com");

                properties.setProperty("mail.transport.protocol","smtp");

                properties.setProperty("mail.smtp.auth","true");
                EmailAuthInfo emailAuthInfo = new EmailAuthInfo();

                //QQ存在一个特性设置SSL加密
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.ssl.socketFactory", sf);

                //创建一个session对象
                Session session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailAuthInfo.getUserCount(),emailAuthInfo.getAuthCode());
                    }
                });

                //开启debug模式
                session.setDebug(true);

                //获取连接对象
                Transport transport = session.getTransport();

                //连接服务器
                transport.connect(emailAuthInfo.getSmtpAdr(),emailAuthInfo.getUserCount(),emailAuthInfo.getAuthCode());

                //创建邮件对象
                MimeMessage mimeMessage = new MimeMessage(session);

                //邮件发送人
                mimeMessage.setFrom(new InternetAddress(emailAuthInfo.getUserCount()));

                //邮件接收人
                mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(emailAuthInfo.getSendTo()));

                //邮件标题
                mimeMessage.setSubject("HAMT“超大预警”");

                //邮件内容
                mimeMessage.setContent("尊敬的信管专员:用户WX1111680发送超大附件xxxx，具体信息如下。请您审核","text/html;charset=UTF-8");

                //发送邮件
                transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

                //关闭连接
                transport.close();
//        log.info("邮件发送成功");
                System.out.println("邮件发送成功");
            }

}
