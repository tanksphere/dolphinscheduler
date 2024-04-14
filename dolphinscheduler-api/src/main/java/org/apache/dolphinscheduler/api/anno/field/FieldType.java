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

package org.apache.dolphinscheduler.api.anno.field;

public enum FieldType {

    EMAIL("email", "邮箱"), MOBILE("mobile", "手机号"), IDCARD("idcard", "身份证号"), STRING("string", "普通字符串"),
    INTEGER("integer", "整数"), LONG("long", "长整数"), BIGDECIMAL("bigdecimal", "小数"), DATE("date", "日期"),
    ENUM("enum", "枚举"), BETWEEN("between", "区间，仅指数字")
    ;
    // 成员变量
    private String name;
    private String desc;

    // 构造方法
    private FieldType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}
