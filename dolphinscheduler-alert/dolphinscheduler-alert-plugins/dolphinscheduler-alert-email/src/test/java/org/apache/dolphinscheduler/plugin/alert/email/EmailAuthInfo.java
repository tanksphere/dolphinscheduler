package org.apache.dolphinscheduler.plugin.alert.email;

/**
 * @Description TODO
 * @Author fangjialiang
 * @Date 2024/4/28 17:58
 * @Verson 1.0
 **/
public class EmailAuthInfo {
        private String userCount;//用户账户
        private String authCode;//授权码
        private String smtpAdr;//smtp地址
        private String sendTo;//收件人

        EmailAuthInfo(){
            this.userCount = "cc@163.com";
            this.smtpAdr  = "smtp.163.com";
            this.authCode = "xxxxxx";
            this.sendTo = "ccxxc@163.com";
        }

        public String getUserCount() {
            return userCount;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getSmtpAdr() {
            return smtpAdr;
        }

        public void setSmtpAdr(String smtpAdr) {
            this.smtpAdr = smtpAdr;
        }

        public String getSendTo() {
            return sendTo;
        }

        public void setSendTo(String sendTo) {
            this.sendTo = sendTo;
        }
}
