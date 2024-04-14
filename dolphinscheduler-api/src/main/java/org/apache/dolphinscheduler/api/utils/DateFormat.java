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

public enum DateFormat {

	YEAR("yyyy"),

	MOUTH("yyyy-MM"),

	DATE("yyyy-MM-dd"),

	DATA_TIME("yyyy-MM-dd HH:mm:ss"),

	DATA_TIME_MIS("yyyy-MM-dd HH:mm:ss SSS"),

	DATA_TIME_DOT_MIS("yyyy-MM-dd HH:mm:ss.SSS"),

	DATA_6BIT("yyyyMM"),

	DATE_8BIT("yyyyMMdd"),

	DATE_DOT_8BIT("yyyy.MM.dd"),

	DATE_14BIT("yyyyMMddHHmmss"),

	DATE_17BIT("yyyyMMddHHmmssSSS"),

	DAY_00("yyyy-MM-dd 00:00:00"),

	DAY_18("yyyy-MM-dd 18:00:00"),

	DAY_24("yyyy-MM-dd 24:00:00"),

	HOUR_MM("HH:mm");

	private String format;

	private DateFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
