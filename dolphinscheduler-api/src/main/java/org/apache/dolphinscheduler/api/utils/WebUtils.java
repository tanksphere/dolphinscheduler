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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;

public class WebUtils {

	public static final Logger LOG = LoggerFactory.getLogger(WebUtils.class);

	public static String getReqContext(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(request.getServletContext().getContextPath()).append("/").toString();
	}

	public static String getRealRemoteAddr(HttpServletRequest request) {
		return request.getHeader("X-Fast-RemoteIp");
	}

	public static String getRemoteAddr(HttpServletRequest request) {
		String from = "X-Forwarded-For";
		String ip = request.getHeader(from);
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			from = "X-Real-IP";
			ip = request.getHeader(from);
		}

		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			from = "WL-Proxy-Client-IP";
			ip = request.getHeader(from);
		}

		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			from = "HTTP_CLIENT_IP";
			ip = request.getHeader(from);
		}

		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			from = "HTTP_X_FORWARDED_FOR";
			ip = request.getHeader(from);
		}

		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			from = "getRemoteAddr";
		}

		if (ip.length() > 15) {
			String[] ips = ip.split(",");

			for (int index = 0; index < ips.length; ++index) {
				String strIp = ips[index];
				if (!"unknown".equalsIgnoreCase(strIp)) {
					ip = strIp;
					break;
				}
			}
			if(ip.length() > 40)  {
				return ip.substring(0, 40);
			}
		}
		return ip;
	}

	public static Map<String, Object> getRequestParams(HttpServletRequest request, String encoding) {
		Map<String, Object> map = new HashMap<>();
		try {
			String parameterStr =request.getQueryString();
//			LOG.info("getRequestParams queryStr=" + parameterStr);
			if(StringUtils.isEmpty(parameterStr)) {
				return map;
			}
			String[] parameterArr = parameterStr.split("&");
			for(String parameterSingle : parameterArr) {
				if(StringUtils.isEmpty(parameterSingle)) {
					continue;
				}
				String[] parameters = parameterSingle.split("=");
				if(parameters.length == 1) {
					map.put(parameters[0], "");
				} else {
					map.put(parameters[0], parameters[1]);
				}
			}
			return map;
		} catch (Exception e) {
			LOG.error("getRequestParams error.", e);
		}
		return null;
	}

	private static <T> BinaryOperator<T> throwingMerger() {
		return (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		};
	}

	public static String getDeviceId(HttpServletRequest request) {
		return request.getHeader("deviceId");
	}

	public static String getToken(HttpServletRequest request) {
		return request.getHeader("token");
	}

	public static String getBody(ServletRequest request) {
		return getBody(request, "utf-8");
	}

	/**
	 * 获取request body
	 * 
	 * @param request
	 * @return
	 */
	public static String getBody(ServletRequest request, String encoding) {
		String acceptjson = "";
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader((ServletInputStream) request.getInputStream(), encoding));
			StringBuffer sb = new StringBuffer("");
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			acceptjson = sb.toString();
		} catch (Exception e) {
			LOG.error("sign filter get body error.", e);
		}

		return acceptjson;
	}

	public static Map<String, Object> getParamsMapFromUrl(String paramsStr) {
		if (paramsStr == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if(paramsStr.indexOf("?")!=-1) {
			paramsStr = paramsStr.substring(paramsStr.indexOf("?") + 1);
		}
		String[] strs = paramsStr.split("&");
		String[] valuePair = null;
		for (int i = 0; i != strs.length; i++) {
			valuePair = strs[i].split("=");
			if (valuePair != null && valuePair.length >= 1) {
				if (valuePair.length == 1) {
					map.put(valuePair[0], null);
				} else {
					if (valuePair[1].indexOf("%") != -1) {
						try {
							valuePair[1] = URLDecoder.decode(valuePair[1], "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					map.put(valuePair[0], valuePair[1]);
				}
			}
		}
		return map;
	}
	
	public static Map<String, String> getParamsMapStringFromUrl(String paramsStr) {
		if (paramsStr == null) {
			return null;
		}
		Map<String, String> map = new HashMap<>();
		if(paramsStr.indexOf("?")!=-1) {
			paramsStr = paramsStr.substring(paramsStr.indexOf("?") + 1);
		}
		String[] strs = paramsStr.split("&");
		String[] valuePair = null;
		for (int i = 0; i != strs.length; i++) {
			valuePair = strs[i].split("=");
			if (valuePair != null && valuePair.length >= 1) {
				if (valuePair.length == 1) {
					map.put(valuePair[0], null);
				} else {
					if (valuePair[1].indexOf("%") != -1) {
						try {
							valuePair[1] = URLDecoder.decode(valuePair[1], "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					map.put(valuePair[0], valuePair[1]);
				}
			}
		}
		return map;
	}
	
	public static Map<String,Object> getParamsMap(HttpServletRequest req){
		Map<String,String[]> params = req.getParameterMap();
		Map<String,Object> params1 = new HashMap<>();
		String[] values = null;
		for (Entry<String, String[]> entry : params.entrySet()) {
			values = entry.getValue();
			if(values.length==1) {
				params1.put(entry.getKey(), values[0]);
			}else {
				params1.put(entry.getKey(), values);
			}
		}
		return params1;
	}
	
	public static Map<String,String> getParamsStringMap(HttpServletRequest req){
		Map<String,String[]> params = req.getParameterMap();
		Map<String,String> params1 = new HashMap<>();
		String[] values = null;
		for (Entry<String, String[]> entry : params.entrySet()) {
			values = entry.getValue();
				params1.put(entry.getKey(), values[0]);
		}
		return params1;
	}

	public static boolean isAjax(HttpServletRequest req) {
		if (req.getHeader("x-requested-with") != null
				&& req.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
			return true;
		}
		return false;
	}
}
