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

package org.apache.dolphinscheduler.api.utils;


import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class StringUtils {
    public static final int INT_NEED = 4;
    public static final int SCALE_NEED = 3;
    public static final int NOT_NEED = 2;
    public static final int ALL_NEED = 1;
    public static int defaultType = 3;

    public StringUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(StringUtils.class);
    private static final String[] phonePrefix = { "86", "086", "0086" };
    private static final String REGX_ID_PATTERN_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    private static final String REGX_ID_PATTERN_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
    private final static Pattern mobilePtn = Pattern.compile("^1[3|4|5|6|7|8|9]\\d{9}$");
    private final static String EMAIL_REGULAR_EXP = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
    public static Random random = new Random();
    private static ScriptEngine engine;
    static {
        engine = new ScriptEngineManager().getEngineByName("javascript");
    }
    /**
     * Case insensitive && string trimmed match
     *
     * @param string1
     * @param string2
     * @return true if strings seem to have
     */
    public static boolean isSameTextValue(String string1, String string2) {

        if (string1 == null || string2 == null) {
            if (string1 == null && string2 == null) {
                return true;
            }
            else {
                return false;
            }
        }

        if (string1.trim().equalsIgnoreCase(string2.trim())) {
            return true;
        }

        return false;
    }

    public static boolean isEmpty(String s) {
        boolean flg = true;
        if (s != null && s.trim().length() > 0) {
            return false;
        }

        return flg;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String getString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String gUnzip(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = null;
        String result = null;
        try {
            gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            result = out.toString("UTF-8");
        } catch (Exception e) {
            LOG.error("gUnzip error.", e);
        } finally {
            if (gunzip != null) {
                try {
                    gunzip.close();
                    in.close();
                    out.close();
                } catch (IOException e) {
                    LOG.error("io close error.", e);
                }
            }
        }
        return result;
    }

    public static boolean isIdNoCorrect(String idNo) {
        if(Pattern.matches(REGX_ID_PATTERN_18, idNo)||Pattern.matches(REGX_ID_PATTERN_15, idNo)) {
            return true;
        }
        return false;
    }

    public static String getSexFromIdNo(String idNo) {
        String sex = "M";// 默认男性
        if (StringUtils.isNotEmpty(idNo)) {
            String sexStr = "";
            if (idNo.length() == 15) {
                sexStr = idNo.substring(14);
            } else {
                sexStr = idNo.substring(idNo.length() - 2, idNo.length() - 1);
            }
            sex = Integer.parseInt(sexStr) % 2 == 0 ? "F" : sex;
        }
        return sex;
    }

    public static String hideBankCard(String bankCard) {
        if (bankCard != null && bankCard.length() > 8) {
            String start = bankCard.substring(0, 4);
            String end = bankCard.substring(bankCard.length() - 4);
            String hide = bankCard.substring(5, bankCard.length() - 4);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i != hide.length(); i++) {
                sb.append("*");
            }
            return start + sb.toString() + end;
        }
        return "";
    }

    public static String hideMobile(String mobile) {
        if (mobile != null && mobile.length() > 7) {
            String start = mobile.substring(0, 3);
            String end = mobile.substring(mobile.length() - 4);
            String hide = mobile.substring(3, mobile.length() - 4);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i != hide.length(); i++) {
                sb.append("*");
            }
            return start + sb.toString() + end;
        }
        return "";
    }

    public static String hideIdNo(String idNo) {
        if (idNo != null && idNo.length() > 2) {
            String start = idNo.substring(0,2);
            String end = idNo.substring(idNo.length()-4);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i != 12; i++) {
                sb.append("*");
            }
            return start + sb.toString()+end;
        }
        return "";
    }

    public static String hideName(String name) {
        if (name != null && name.length() > 0) {
            String start = name.substring(0, 1);
            String hide = name.substring(1, name.length());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i != hide.length(); i++) {
                sb.append("*");
            }
            return start + sb.toString();
        }
        return "";
    }
    public static String firstToUppercase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getTelNo(String telNo) {
        telNo = telNo.replaceAll("[^0-9]", "");
        for (String string : phonePrefix) {
            if (telNo.startsWith(string)) {
                telNo = telNo.substring(string.length());
            }
        }
        if (telNo.length() > 11) {
            telNo = telNo.substring(telNo.length() - 11, telNo.length());
        }
        return telNo;
    }
    public static String formatTelNo(String telNo) {
        telNo = telNo.replaceAll("[^0-9]", "");
        for (String string : phonePrefix) {
            if (telNo.startsWith(string)) {
                telNo = telNo.substring(string.length());
            }
        }
        return telNo;
    }

    public static boolean isFixedTel(String telNo) {
        telNo = formatTelNo(telNo);
        if(telNo.startsWith("1")&&telNo.length()==11) {
            return false;
        }
        if(telNo.length()<7) {
            return false;
        }
        return true;
    }

    //根据身份证号判断年龄
    public  static int getAge(String CardCode){
        int age = -1;
        if(isEmpty(CardCode)||CardCode.length()<14) {
            return age;
        }
        String year = CardCode.substring(6).substring(0, 4);// 得到年份
        String yue = CardCode.substring(10).substring(0, 2);// 得到月份
        String day=CardCode.substring(12).substring(0,2);//得到日
        day = formatDay(day);
        Date date = new Date();// 得到当前的系统时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fyear = format.format(date).substring(0, 4);// 当前年份
        String fyue = format.format(date).substring(5, 7);// 月份
        String fday=format.format(date).substring(8,10);
        if (Integer.parseInt(yue) < Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生
            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
        } else if(Integer.parseInt(yue) == Integer.parseInt(fyue)){
            if(Integer.parseInt(fday) >Integer.parseInt(day)){
                age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
            }else{
                age = Integer.parseInt(fyear) - Integer.parseInt(year) ;
            }
        }else {// 当前用户还没过生
            age = Integer.parseInt(fyear) - Integer.parseInt(year);
        }
        return age;
    }
    private static String formatDay(String day) {
        if(isEmpty(day)||"**".equals(day)) {
            return "01";
        }else {
            return day.replace("*", "1");
        }
    }


    public static String getRandom(int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            boolean isChar = (random.nextInt(2) % 2 == 0);// 输出字母还是数字
            if (isChar) { // 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                ret.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                ret.append(Integer.toString(random.nextInt(10)));
            }
        }
        return ret.toString();
    }

    public static boolean isNumberic(String s) {
        if(isEmpty(s)) return false;
        Pattern pattern = Pattern.compile("-?[0-9]+(.[0-9]+)?");
        Matcher isNum = pattern.matcher(s);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    public static boolean isInt(String s) {
        if(isEmpty(s)) return false;
        Pattern pattern = Pattern.compile("-?[0-9]+");
        Matcher isNum = pattern.matcher(s);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static String filterEmoji(String source) {
        return filterEmoji(source, "");
    }
    public static String filterEmoji(String source, String target) {
        if(isNotEmpty(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", isNotEmpty(target) ? target : "");
        }else{
            return source;
        }
    }

    /**
     * 前补位
     * @param orginStr 原zifu
     * @param pack   补位字符
     * @param length 补位到长度
     * @return
     */
    public static String packString(String orginStr,String pack,int length) {
        return packString(orginStr, pack, length,false);
    }
    /**
     *
     * @param orginStr 原zifu
     * @param pack   补位字符
     * @param length 补位到长度
     * @param end    是否追加
     * @return
     */
    public static String packString(String orginStr,String pack,int length,boolean end) {
        if(orginStr==null) {
            return null;
        }
        if(pack==null) {
            return orginStr;
        }
        if(orginStr.length()>=length) {
            return orginStr;
        }
        if(end) {
            orginStr = orginStr.concat(pack);
        }else {
            orginStr =pack.concat(orginStr);
        }
        return packString(orginStr,pack,length,end);
    }

    public static Object calJSExpre(String expre,Object... params) {
        Object result = null;
        if (isNotEmpty(expre)) {
            try {
                String name = "javaScriptEngineResult";
                engine.eval("var "+name+" = "+expre);
                result =engine.get(name);
                if(engine instanceof Invocable) {
                    if(expre.toLowerCase().indexOf("function")!=-1) {
                        Invocable invoke = (Invocable)engine;
                        result = invoke.invokeFunction(name,params);
                    }
                }
            }catch(NoSuchMethodException e) {
                LOG.error("excel表达式解析异常,{" + expre + "}:{}", e);
            } catch (Exception e) {
                LOG.error("excel表达式解析异常,{" + expre + "}:{}", e);
            }
        }
        return result;
    }

    public static String filterSQLParams(String params){
        if(isNotEmpty(params)){
            return params.replace(";", " ");
        }
        return "";
    }

    /**
     * 掩字符串，默认替换为空字符
     * @param str 目标字符串
     * @param headFlag 前后标识，true-从头开始，false-从尾开始
     * @param count 掩码长度
     * @return
     */
    public static String mask(String str, boolean headFlag, int count) {
        if(isEmpty(str) || str.length() <= count) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str);
        if(headFlag) {
            for(int i = 0; i < count; i++) {
                builder.replace(i, i + 1, "*");
            }
        } else {
            for(int i = str.length() - 1, j = 0; j < count; i--,j++) {
                builder.replace(i, i + 1, "*");
            }
        }
        return builder.toString();
    }

    public static boolean checkMask(String str, boolean headFlag, int count) {
        if(isEmpty(str) || str.length() <= count) {
            return false;
        }
        // 判断*的个数
        int len = 0;
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '*') {
                len ++;
            }
        }
        if(len != count) {return false;}
        if(headFlag) {
            for(int i = 0; i < count; i++) {
                if('*' != str.charAt(i)) {
                    return false;
                }
            }
        } else {
            for(int i = str.length() - 1, j = 0; j < count; i--,j++) {
                if('*' != str.charAt(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getRandomInt(int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            ret.append(Integer.toString(random.nextInt(10)));
        }
        return ret.toString();
    }

    public static boolean checkDateFormat(String dateStr, String format) {
        if(isEmpty(format) || isEmpty(dateStr) || dateStr.length() != format.length()) {return false;}
        try {
            if(!checkCharacter(dateStr, format, new String[]{"-", " ", ":", "T"})) {
                return false;
            }
            int yyyyIndex = format.indexOf("yyyy");
            if(yyyyIndex > -1) {
                if(Integer.parseInt(dateStr.substring(yyyyIndex, yyyyIndex + 4)) < 1970) {
                    return false;
                }
            }
            int _MMIndex = format.indexOf("MM");
            if(_MMIndex > -1) {
                int MM = Integer.parseInt(dateStr.substring(_MMIndex, _MMIndex + 2));
                if(MM < 1 || MM > 12) {
                    return false;
                }
            }
            int ddIndex = format.indexOf("dd");
            if(ddIndex > -1) {
                int dd = Integer.parseInt(dateStr.substring(ddIndex, ddIndex + 2));
                if(dd < 1 || dd > 31) {
                    return false;
                }
            }
            int _HHIndex = format.indexOf("HH");
            if(_HHIndex > -1) {
                int HH = Integer.parseInt(dateStr.substring(_HHIndex, _HHIndex + 2));
                if(HH < 0 || HH > 24) {
                    return false;
                }
            }
            int mmIndex = format.indexOf("mm");
            if(mmIndex > -1) {
                int mm = Integer.parseInt(dateStr.substring(mmIndex, mmIndex + 2));
                if(mm < 0 || mm > 59) {
                    return false;
                }
            }
            int ssIndex = format.indexOf("ss");
            if(ssIndex >  -1) {
                int ss = Integer.parseInt(dateStr.substring(ssIndex, ssIndex + 2));
                if(ss < 0 || ss > 59) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("checkDateFormat error, dateStr=" + dateStr + ", format=" + format, e);
            return false;
        }
    }

    public static boolean checkCharacter(String dateStr, String format, String[] characters) {
        if(characters == null || characters.length == 0) {
            return false;
        }
        for(String character : characters) {
            int start = 0;
            int _index = format.indexOf(character, start);
            while (_index > -1 && _index < format.length()) {
                int target = dateStr.indexOf(character, start);
                if (target != _index) {
                    return false;
                }
                start = _index + 1;
                _index = format.indexOf(character, start);
            }
        }
        return true;
    }

    public static List<String> parseStrList(String source, String split) {
        List<String> list = new ArrayList<>();
        if(isEmpty(source) || isEmpty(split) || source.indexOf(split) == -1) {
            return Arrays.asList(source);
        }
        Pattern p = Pattern.compile(split);
        Matcher m = p.matcher(source);
        int lastIndex = -1;

        while(m.find()){
            list.add(source.substring(lastIndex+1, m.start()));
            lastIndex = m.start();
        }
        list.add(source.substring(lastIndex+1, source.length()));
        return list;
    }



    /**
     * 检查手机号是否正确
     */
    public static boolean checkMobile(String phone){
        if(phone == null){
            return false;
        }
        Matcher matcher = mobilePtn.matcher(phone);
        return matcher.matches();
    }

    public static String insertSplitChar(String originStr, String splitChar, int length) {
        if(isEmpty(originStr)) {
            return originStr;
        }
        if(originStr.length() <= length) {
            return originStr;
        }
        String result = "";
        for(int i = 0; i < originStr.length();) {
            int end = (i + length) > originStr.length() ? originStr.length() : (i+length);
            result += originStr.substring(i, end) + (end == originStr.length() ? "" : splitChar);
            i += length;
        }
        return result;
    }

    public static boolean checkEmail(String email) {
        if(isEmpty(email)) {
            return false;
        }
        return email.trim().matches(EMAIL_REGULAR_EXP);
    }

    public static String addParams(String url, String name, String value) {
        if(isEmpty(url) || isEmpty(name)) {
            return url;
        }
        if(url.indexOf("?") > -1) {
            url += "&" + name + "=" + value;
        } else {
            url += "?" + name + "=" + value;
        }
        return url;
    }

    public static String getParam(String url, String name) {
        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(name) || url.indexOf("?") == -1) {
            return "";
        }
        String params = url.substring(url.indexOf("?") + 1, url.length());
        Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
        return split.get(name);
    }

    public static String fillChar(String orgin, int maxLen, String ch) {
        if(isEmpty(orgin) || orgin.length() >= maxLen) {
            return orgin;
        }
        int fillLen = maxLen - orgin.length();
        String allChar = "";
        for(int i = 0; i < fillLen; i++) {
            allChar += ch;
        }
        return allChar + orgin;
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str != null && str.length() != 0) {
            StringBuffer buf = new StringBuffer(str.length());
            if (capitalize) {
                buf.append(Character.toUpperCase(str.charAt(0)));
            } else {
                buf.append(Character.toLowerCase(str.charAt(0)));
            }

            buf.append(str.substring(1));
            return buf.toString();
        } else {
            return str;
        }
    }

    public static String defaultIfEmpty(String str, String defaultString) {
        if (str == null) {
            return defaultString;
        } else {
            return str.trim().length() == 0 ? " " : str;
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Date) {
            return obj instanceof Timestamp ? DateUtils.toDateTimeStr((Timestamp)obj) : DateUtils.toDateStr((Date)obj);
        } else {
            return obj.toString();
        }
    }

    public static String getFormat(int intLength, int scaleLength) {
        return getFormat(intLength, scaleLength, defaultType);
    }

    public static String getFormat(int intLength, int scaleLength, int type) {
        StringBuffer formatStr = new StringBuffer(intLength + scaleLength + 1);
        int i;
        if (type == 4 || type == 1) {
            for(i = 0; i < intLength - 1; ++i) {
                formatStr.append("0");
            }
        }

        formatStr.append("0");
        if (scaleLength > 0) {
            formatStr.append(".");
            if (type != 3 && type != 1) {
                for(i = 0; i < scaleLength; ++i) {
                    if (i == 0) {
                        formatStr.append("0");
                    } else {
                        formatStr.append("#");
                    }
                }
            } else {
                for(i = 0; i < scaleLength; ++i) {
                    formatStr.append("0");
                }
            }
        }

        return formatStr.toString();
    }

    public static String toString(Object obj, int filedLength, int scaleLength) {
        int intLength = filedLength;
        if (scaleLength > 0) {
            intLength = filedLength - scaleLength - 1;
        }

        if (obj == null) {
            return null;
        } else {
            DecimalFormat format;
            if (obj instanceof Integer) {
                if (((Integer)obj).intValue() < 0) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength));
                return format.format((Integer)obj);
            } else if (obj instanceof Long) {
                if (((Long)obj).longValue() < 0L) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength));
                return format.format((Long)obj);
            } else if (obj instanceof Float) {
                if (((Float)obj).floatValue() < 0.0F) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength));
                return format.format((Float)obj);
            } else if (obj instanceof Double) {
                if (((Double)obj).doubleValue() < 0.0D) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength));
                return format.format((Double)obj);
            } else if (obj instanceof BigDecimal) {
                if (((BigDecimal)obj).doubleValue() < 0.0D) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength));
                return format.format((BigDecimal)obj);
            } else if (obj instanceof Date) {
                return obj instanceof Timestamp ? DateUtils.toDateTimeStr((Date)obj) : DateUtils.toDateStr((Date)obj);
            } else {
                return obj.toString();
            }
        }
    }

    public static String toString(Object obj, int filedLength, int scaleLength, int type) {
        int intLength = filedLength;
        if (scaleLength > 0) {
            intLength = filedLength - scaleLength - 1;
        }

        if (obj == null) {
            return null;
        } else {
            DecimalFormat format;
            if (obj instanceof Integer) {
                if (((Integer)obj).intValue() < 0) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength, type));
                return format.format((Integer)obj);
            } else if (obj instanceof Long) {
                if (((Long)obj).longValue() < 0L) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength, type));
                return format.format((Long)obj);
            } else if (obj instanceof Float) {
                if (((Float)obj).floatValue() < 0.0F) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength, type));
                return format.format((Float)obj);
            } else if (obj instanceof Double) {
                if (((Double)obj).doubleValue() < 0.0D) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength, type));
                return format.format((Double)obj);
            } else if (obj instanceof BigDecimal) {
                if (((BigDecimal)obj).doubleValue() < 0.0D) {
                    --intLength;
                }

                format = new DecimalFormat(getFormat(intLength, scaleLength, type));
                return format.format((BigDecimal)obj);
            } else {
                return obj instanceof Date ? DateUtils.toDateStr((Date)obj) : obj.toString();
            }
        }
    }

    public static boolean hasContent(String s) {
        if (s == null) {
            return false;
        } else {
            return s.trim().length() != 0;
        }
    }

    public static String compress(String str) {
        try {
            return compressByte(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static String compressByte(byte[] byteArray) {
        BufferedInputStream bis = null;
        GZIPOutputStream gzip = null;
        //修改处
        String var8 = null;
        ByteArrayOutputStream baos;
        try {
            int size = 1024;
            bis = new BufferedInputStream(new ByteArrayInputStream(byteArray));
            baos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(baos);
            byte[] buffer = new byte[size];

            int len;
            while((len = bis.read(buffer, 0, size)) != -1) {
                gzip.write(buffer, 0, len);
            }

            gzip.finish();
            byte[] bytes = baos.toByteArray();
//            String var8 = DatatypeConverter.printBase64Binary(bytes);
            var8 = DatatypeConverter.printBase64Binary(bytes);
//            return var8;
        } catch (Exception var22) {
            var22.printStackTrace();
            baos = null;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException var21) {
                var21.printStackTrace();
            }

            try {
                if (gzip != null) {
                    gzip.close();
                }
            } catch (IOException var20) {
                var20.printStackTrace();
            }

        }

//        return baos;
        return var8;
    }

    public static String decompress(String data) {
        try {
            return new String(decompressByte(data), "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("null")
    public static byte[] decompressByte(String data) {
        GZIPInputStream gzip = null;

        ByteArrayOutputStream baos;
        try {
            int size = 1024;
            gzip = new GZIPInputStream(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(data)));
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[size];

            int len;
            while((len = gzip.read(buffer, 0, size)) != -1) {
                baos.write(buffer, 0, len);
            }

            byte[] var6 = baos.toByteArray();
            return var6;
        } catch (Exception var16) {
            var16.printStackTrace();
            baos = null;
        } finally {
            try {
                if (gzip != null) {
                    gzip.close();
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }

        }
        byte[] bytes = baos.toByteArray();
        return bytes;
//        return (byte[])baos;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isNotBlank(String... trim) {
        if (trim.length <= 0) {
            return false;
        } else {
            String[] var1 = trim;
            int var2 = trim.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String s = var1[var3];
                if (s == null || s.length() == 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String removeLastLetter(String str) {
        return str != null && str.length() != 0 ? str.substring(0, str.length() - 1) : str;
    }

    public static boolean strInArray(String[] array, String target) {
        if(array == null || array.length == 0 || target == null) {
            return false;
        }
        for(String str : array) {
            if(str.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private static StringBuilder newStringBuilder(int noOfItems) {
        return new StringBuilder(noOfItems * 16);
    }

    public static String join(Object[] array, String separator) {
        return array == null ? null : join((Object[])array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }

            int noOfItems = endIndex - startIndex;
            if (noOfItems <= 0) {
                return "";
            } else {
                StringBuilder buf = newStringBuilder(noOfItems);

                for(int i = startIndex; i < endIndex; ++i) {
                    if (i > startIndex) {
                        buf.append(separator);
                    }

                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }

                return buf.toString();
            }
        }
    }

    // //去掉首尾指定字符串
    public static String trimChar(String source, String element) {
        if (source == null || source.length() == 0 || element == null || element.length() == 0) {
            return source;
        }
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do{
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;

            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();

            source = source.substring(beginIndex, endIndex);

            beginIndexFlag = (source.indexOf(element) == 0);

            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());

        } while (beginIndexFlag || endIndexFlag);

        return source;
    }
}
