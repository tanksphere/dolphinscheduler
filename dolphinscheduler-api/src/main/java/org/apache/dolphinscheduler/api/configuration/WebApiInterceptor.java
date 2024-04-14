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

package org.apache.dolphinscheduler.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.utils.ControllerLogUtils;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.api.vo.ParamValidateVO;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Order(-1)
@Configuration("DolWebApiInterceptor")
@Slf4j
//@RefreshScope
// @ConditionalOnProperty(prefix = "fast.common.web.api.interceptor", name = "enable", havingValue = "true", matchIfMissing=true)
public class WebApiInterceptor implements InitializingBean {

//	@Autowired(required = false)
//	private RequireLimitAspect requireLimitAspect;
//
//	@Value("${dol.web.api.log.except.uris:}")
//	private String logIgnoreUris;
//
//	@Value("${dol.web.api.log.except.uris:}")
//	private List<String> mthLogIgnoreUriList;

	private String[] logIgnoreUriArys;

//	@Value("${dol.web.hearder.channel}")
//	private String channelName;
//
//	@Value("${dol.web.hearder.userId}")
//	private String userIdName;

	@Pointcut("execution(public * org.apache.dolphinscheduler.api..controller.*.*(..))")
	public void dolExpression() {

	}

	@Around("dolExpression()")
	public Object dolExpression(ProceedingJoinPoint point) throws Throwable {
		String preLog = "";
		Object result = null;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		boolean ignore = checkIgonrePrint(point);
//		RequireLimitResult requireLimitResult = null;
		try {
			if(ignore) {
//				List<String> ignoreUriList = (commonDynamicProperties != null && commonDynamicProperties.getIgnoreLogUriList() != null) ? commonDynamicProperties.getIgnoreLogUriList() : null;
				ParamValidateVO paramValidateVO = ControllerLogUtils.getParamsLog(currentRequest(), point, null, true);
				preLog = paramValidateVO.getParams();
				if(paramValidateVO.isFail()) {
					result = fillFailResponse(point.getTarget().getClass().getName(), paramValidateVO.getDesc());
					return result;
				}
			}
//			if(requireLimitAspect != null) {
//				requireLimitResult = requireLimitAspect.handleLimit(point);
//			}
			result = point.proceed(point.getArgs());
			return result;
		} catch (Exception e) {
			log.error("dolExpression handle error", e);
			result = new Result(Status.INTERNAL_SERVER_ERROR_ARGS.getCode(), e.getMessage());
			return result;
		} finally{
//			if(requireLimitAspect != null && requireLimitResult != null && requireLimitResult.getRequireLimitKey() != null && !requireLimitResult.isForceLimit()) {
//				requireLimitAspect.delLimit(requireLimitResult.getRequireLimitKey());
//			}
			if(ignore) {
				stopWatch.stop();
				log.info(preLog + "\n\treturn value is " + getStr(result) + "\n\t" +
						"########## request info : " + stopWatch.getTotalTimeMillis() + "ms#");
			}
		}
	}

	private String getStr(Object result){
		if(result==null){
			return null;
		}
//		String s = JSONUtils.toJsonString(result,new ValueFilter(){
//			@Override
//			public Object process(Object object, String name, Object value) {
//				if(value!=null && value instanceof String){
//					String v = String.valueOf(value);
//					if(v.length()>1000){
//						v = v.substring(0,900);
//						v+="...";
//					}
//					value = v;
//				}
//				return value;
//			}
//
//		});
//		if(s!=null&&s.length()>2000){
//			s = s.substring(0,1990)+"...";
//		}

		String s = JSONUtils.toJsonString(result);

		return s;
	}

	private Object fillFailResponse(String packageName, String subMessage) {
		// 可根据包名判断不同的返回类型
//		return ResponseUtils.create(CommonCodeEnum.VALIDATE_API_ERROR.getCode(), CommonCodeEnum.VALIDATE_API_ERROR.getMessage(), subMessage);
		Map<String, Object> result = new HashMap<>();
		result.put(Constants.STATUS, "400000");
		result.put("msg", subMessage);
		return result;
	}

	private HttpServletRequest currentRequest() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		if (ra == null) {
			return null;
		}
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		return sra.getRequest();
	}

	private boolean checkIgonrePrint(ProceedingJoinPoint point) {
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();

		Annotation anno = AnnotationUtils.findAnnotation(method, PostMapping.class);
		if(anno != null) {
			return true;
		}
		anno = AnnotationUtils.findAnnotation(method, GetMapping.class);
		if(anno != null) {
			return true;
		}
		anno = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		if(anno != null) {
			return true;
		}
		return false;
	}

//	@Around("dolExpression()")
//	public Object argsLog(ProceedingJoinPoint point) throws Throwable {
//
//		StopWatch watch = new StopWatch();
//		watch.start();
//		String methodName = point.getSignature().getName();
//		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//		if (ra == null) {
//			return new Object();
//		}
//		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//		HttpServletRequest request = sra.getRequest();
//		String uri = request.getRequestURI();
////		boolean ignoreParamsLog = ignoreParamsLog(uri);
//		boolean ignoreParamsLog = false;
////		String channel = request.getHeader(channelName);
////		String userId = request.getHeader(userIdName);
//
//		if (ignoreParamsLog) {
//			if (log.isDebugEnabled()) {
//				String requestMesssage = serializeArgs(point);
//				log.debug("资源[{}] , 方法名[{}] , 请求[{}]", uri, methodName,
//						requestMesssage);
//			} else {
//				log.info("资源[{}] , 方法名[{}], 请求忽略", uri, methodName);
//			}
//		} else {
//			String requestMesssage = serializeArgs(point);
//			log.info("资源[{}] , 方法名[{}], 请求[{}]", uri, methodName,
//					requestMesssage);
//		}
//
//		Object proceed = point.proceed();
//
//		watch.stop();
//		long costTime = watch.getTotalTimeMillis();
//		if (ignoreParamsLog) {
//			if (log.isDebugEnabled()) {
//				String requestMesssage = serializeArgs(point);
//				String responseMessage = proceed == null ? null : JSONUtils.toJsonString(proceed);
//				log.debug("资源[{}] , 方法名[{}] , ] , 耗时[{}毫秒] , 请求[{}] , 响应[{}]", uri, methodName,
//						costTime, requestMesssage, responseMessage);
//			} else {
//				log.info("资源[{}] , 方法名[{}]] , 耗时[{}毫秒] , 请求忽略 , 响应忽略", uri, methodName,
//						costTime);
//			}
//		} else {
//			String requestMesssage = serializeArgs(point);
////			String responseMessage = proceed == null ? null : JSONUtils.toJsonString(proceed,new ValueFilter(){
////
////				@Override
////				public Object process(Object object, String name, Object value) {
////					if(value!=null && value instanceof String){
////						String v = String.valueOf(value);
////						if(v.length()>1000){
////							v = v.substring(0,900);
////							v+="...";
////						}
////						value = v;
////					}
////					return value;
////				}
////
////			});
////			if(responseMessage!=null&&responseMessage.length()>2000){
////				responseMessage = responseMessage.substring(0,1990)+"...";
////			}
//			String responseMessage = proceed == null ? null : JSONUtils.toJsonString(proceed);
//					log.info("资源[{}] , 方法名[{}] , 耗时[{}毫秒] , 请求[{}] , 响应[{}]", uri, methodName,
//					costTime, requestMesssage, responseMessage);
//		}
//		return proceed;
//	}

	private String serializeArgs(ProceedingJoinPoint point) {
		Object[] objs = point.getArgs();
		if (objs == null || objs.length == 0) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0, k = 0; i < objs.length; i++) {
			if (objs[i] instanceof HttpServletRequest) {
				continue;
			}
			if (objs[i] instanceof HttpServletResponse) {
				continue;
			}
			if (k++ > 0) {
				builder.append(", ");
			}
			try {
				// builder.append(JSON.toJSONString(objs[i]));
				String v = String.valueOf(objs[i]);
				if(v.length()>1000){
						v= v.substring(0,900);
						v +="...";
				}
				builder.append(v);
			} catch (Exception e) {
				log.error("serializeArgs json parse error, param={}", String.valueOf(objs[i]), e);
			}
		}
		return builder.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

//	private boolean ignoreParamsLog(String uri) {
//		if (Tools.isBlank(logIgnoreUris)) {
//			return false;
//		}
//		for (String s : logIgnoreUriArys) {
//			if (Tools.isBlank(s)) {
//				continue;
//			}
//			if (uri.startsWith(s.trim()))
//				return true;
//		}
//		return false;
//	}
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		logIgnoreUriArys = logIgnoreUris.split(",");
//		String ignoreUris = logIgnoreUriArys == null ? "" : Arrays.toString(logIgnoreUriArys);
//		log.info("logIgnoreUris : {} , logIgnoreUriArys = {}", logIgnoreUris, ignoreUris);
//		log.info("mthLogIgnoreUriList = {}", mthLogIgnoreUriList);
//	}

}
