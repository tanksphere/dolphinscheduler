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

import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.anno.JSONField;
import org.apache.dolphinscheduler.api.anno.field.FieldFormat;
import org.apache.dolphinscheduler.api.anno.field.FieldType;
import org.apache.dolphinscheduler.api.anno.field.ValidataFieldAnnotation;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class ValidateUtils {

    private static final List<String> igonreFields = Arrays.asList("serialVersionUID", "");

    public static String validateObject(Object object, String prefix, String appId) {
        if (object == null) {
//            return "请求参数不能为空";
            return "";
        }
        try {
            return validateField(object, prefix, appId);
        } catch (Exception e) {
            log.error("validateObject error,", e);
            return "系统异常";
        }
    }

    public static String validateField(Object object, String prefix, String appId) {
        if (object == null) {
            return "";
        }
        if (object.getClass().getName().equals(Object.class.getName())) {
            return "";
        }
        if (object instanceof List) {
            for (Object obj : (List) object) {
                String result = validateField(obj, prefix, appId);
                if (StringUtils.isNotEmpty(result)) {
                    return result;
                }
            }
        }

        prefix = StringUtils.isNotEmpty(prefix) ? prefix + "." : "";
        List<Field> fields = new ArrayList<>();
        ReflectUtils.getAllFields(fields, object.getClass());
        boolean customDesc = false;
        for (Field field : fields) {
            if (igonreFields.contains(field.getName())) {
                continue;
            }
            JSONField jsonField = field.getAnnotation(JSONField.class);
            String fieldName = (jsonField != null && StringUtils.isNotEmpty(jsonField.name()) ? jsonField.name()
                    : field.getName());
            if (field.getName().equals("bizData")) {
                Object bizData = ReflectUtils.getGetMethodValue(object, "getBizData");
                if (bizData == null) {
                    return prefix + fieldName + "不能为空";
                }
                String result = validateField(bizData, prefix + fieldName, appId);
                if (StringUtils.isNotEmpty(result)) {
                    return result;
                }
                continue;
            }
            Object fieldValue;
            try {
                field.setAccessible(true);
                fieldValue = field.get(object);
            } catch (Exception e) {
                log.error("get value [" + object.getClass() + "." + field.getName() + " error", e);
                continue;
            }
            ValidataFieldAnnotation validataFieldAnnotation = field.getAnnotation(ValidataFieldAnnotation.class);
            String errorMsg = "【" + prefix + fieldName + "】";
            if (validataFieldAnnotation != null && StringUtils.isNotEmpty(validataFieldAnnotation.desc())) {
                String desc = validataFieldAnnotation.desc();
                if(StringUtils.isNotBlank(desc)) {
                    errorMsg = desc;
                    customDesc = true;
                } else {
                    errorMsg = "【" + prefix + (StringUtils.isEmpty(desc) ? fieldName : desc) + "】";
                    customDesc = false;
                }
            }
            if ((validataFieldAnnotation == null || validataFieldAnnotation.required() == false)
                    && fieldValue != null) {
                if (fieldValue instanceof List) {
                    for (Object obj : (List) fieldValue) {
                        String result = validateField(obj, prefix + fieldName, appId);
                        if (StringUtils.isNotEmpty(result)) {
                            return result;
                        }
                    }
                    continue;
                } else {
                    if (!(fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof Byte
                            || fieldValue instanceof Short || fieldValue instanceof Double
                            || fieldValue instanceof BigDecimal || fieldValue instanceof Date
                            || fieldValue instanceof Float || fieldValue instanceof Boolean || fieldValue instanceof Long)) {
                        String result = validateField(fieldValue, prefix + fieldName, appId);
                        if (StringUtils.isNotEmpty(result)) {
                            return result;
                        }
                    }
                }
            }
            if (validataFieldAnnotation != null && (!StringUtils.strInArray(validataFieldAnnotation.excludes(), appId))) {
                if (fieldValue == null || (fieldValue instanceof String && "".equals(fieldValue))) {
                    return customDesc ? errorMsg : (errorMsg + "不能为空");
                }
                if (fieldValue instanceof List) {
                    for (Object obj : (List) fieldValue) {
                        String result = validateField(obj, prefix + fieldName, appId);
                        if (StringUtils.isNotEmpty(result)) {
                            return result;
                        }
                    }
                    continue;
                } else {
                    if (!(fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof Byte
                            || fieldValue instanceof Short || fieldValue instanceof Double
                            || fieldValue instanceof BigDecimal || fieldValue instanceof Date
                            || fieldValue instanceof Float || fieldValue instanceof Boolean || fieldValue instanceof Long)) {
                        String result = validateField(fieldValue, prefix + fieldName, appId);
                        if (StringUtils.isNotEmpty(result)) {
                            return result;
                        }
                    }
                }
            }
            FieldFormat fieldFormat = field.getAnnotation(FieldFormat.class);
            if (fieldFormat != null && fieldValue != null) {
                if(StringUtils.isNotBlank(fieldFormat.desc())) {
                    errorMsg = fieldFormat.desc();
                }
                if ((fieldFormat.fieldType() == FieldType.INTEGER || fieldFormat.fieldType() == FieldType.LONG) && !StringUtils.isInt(fieldValue.toString())) {
                    return errorMsg + "格式错误";
                }
                if (fieldFormat.fieldType() == FieldType.MOBILE && !StringUtils.checkMobile(fieldValue.toString())) {
                    return errorMsg + "格式错误";
                }
                if (fieldFormat.fieldType() == FieldType.IDCARD && !StringUtils.isIdNoCorrect(fieldValue.toString())) {
                    return errorMsg + "格式错误";
                }
                if(fieldFormat.fieldType() == FieldType.DATE && !StringUtils.checkDateFormat(fieldValue.toString(), fieldFormat.dateFormat())) {
                    return errorMsg + "格式错误";
                }
                if(fieldFormat.fieldType() == FieldType.EMAIL && !StringUtils.checkEmail(fieldValue.toString())) {
                    return errorMsg + "格式错误";
                }
                if(fieldFormat.fieldType() == FieldType.ENUM && fieldFormat.values().length > 0 && !Arrays.asList(fieldFormat.values()).contains(fieldValue.toString())) {
                    return errorMsg + "传值错误，正确值：【" + StringUtils.join(fieldFormat.values(), ",") + "】";
                }
                if(fieldFormat.fieldType() == FieldType.BETWEEN && fieldFormat.interval().length() > 0 && fieldFormat.interval().contains(",")) {
                    String[] vv = fieldFormat.interval().replaceAll(" ", "").split(",", 2);
                    BigDecimal target = fieldValue instanceof BigDecimal ? (BigDecimal) fieldValue : new BigDecimal(fieldValue.toString());
                    if(vv[0].length() > 0) {
                        if(vv[0].startsWith("(") && vv[0].length() > 1 && new BigDecimal(vv[0].replace("(", "")).compareTo(target) >= 0) {
                            return errorMsg + "传值错误，正确值：" + fieldFormat.interval();
                        } else if(vv[0].startsWith("[") && vv[0].length() > 1 && new BigDecimal(vv[0].replace("[", "")).compareTo(target) > 0) {
                            return errorMsg + "传值错误，正确值：" + fieldFormat.interval();
                        }
                    }
                    if(vv[1].length() > 0) {
                        if(vv[1].endsWith(")") && vv[1].length() > 1 && new BigDecimal(vv[1].replace(")", "")).compareTo(target) <= 0) {
                            return errorMsg + "传值错误，正确值：" + fieldFormat.interval();
                        } else if(vv[1].endsWith("]") && vv[1].length() > 1 && new BigDecimal(vv[1].replace("]", "")).compareTo(target) < 0) {
                            return errorMsg + "传值错误，正确值：" + fieldFormat.interval();
                        }
                    }
                }
            }
        }

        return "";
    }

}
