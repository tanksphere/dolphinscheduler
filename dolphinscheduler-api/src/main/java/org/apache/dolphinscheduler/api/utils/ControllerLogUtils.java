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

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.api.anno.field.DolRequestBody;
import org.apache.dolphinscheduler.api.vo.DolRequestParams;
import org.apache.dolphinscheduler.api.vo.ParamValidateVO;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ControllerLogUtils {

    public static ParamValidateVO getParamsLog(HttpServletRequest request, ProceedingJoinPoint point, List<String> ignoreRequest, boolean checkParams) throws Exception {
        if(request == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#### checking pointcut....\n\tController  : ").append(point.getTarget().getClass().getName())
                .append(".").append(point.getSignature().getName()).append("(").append(point.getTarget().getClass().getSimpleName()).append(".java)");
        sb.append("\n\tRemoteIp    : " + WebUtils.getRemoteAddr(request) + "," + WebUtils.getRealRemoteAddr(request));
        String uri = request.getRequestURI();
        if (uri != null) {
            sb.append("\n\turl         : ").append(uri);
        }
        sb.append("\n\tContext     : " + request.getContextPath());
        sb.append("\n\tRequestTime : " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        sb.append("\n\tParameter   : ");
//        String channel = StringUtil.getString(request.getHeader("channel"));
//        String version = StringUtil.getString(request.getHeader("version"));
        String userAgent = (request.getHeader("user-agent"));
        String postArgs = "";
        String validateResult = "";
        DolRequestParams dolRequestParams;
        try {
            dolRequestParams = getRequestParams(point, request);
        } catch (Exception e) {
            return new ParamValidateVO(true, "", e.getMessage(), "", null);
        }
        if(dolRequestParams != null) {
            if(dolRequestParams.getDolOriginalRequestBody() != null) {
                if(checkParams) {
                    validateResult = ValidateUtils.validateObject(dolRequestParams.getDolRequestBody(), "", "");
                }
//                postArgs = dolRequestParams.getMthOriginalRequestBody();
                postArgs = getMthRequestBodyArgs(uri, ignoreRequest, dolRequestParams);
            }
            if(dolRequestParams.getParamMap() != null) {
                postArgs += ("".equals(postArgs) ? "" : ",") + JSONUtils.toJsonString(dolRequestParams.getParamMap());
            }
        }

        sb.append(postArgs);
//        if(!"".equals(channel)) {
//            sb.append("channel=" + channel + ",");
//        }
//        if(!"".equals(version)) {
//            sb.append("version=" + version + ",");
//        }
        if(!"".equals(userAgent)) {
            sb.append(" ,userAgent=").append(userAgent).append(",");
        }
        if(dolRequestParams != null && dolRequestParams.getHasCustomBody() != null && dolRequestParams.getHasCustomBody()
                && dolRequestParams.getDolRequestBodyRequired() && StringUtils.isEmpty(dolRequestParams.getDolOriginalRequestBody())) {
            return new ParamValidateVO(true, sb.toString(), "请求参数不能为空", dolRequestParams.getDolOriginalRequestBody(), dolRequestParams.getDolRequestBody());
        }
        if(StringUtils.isNotBlank(validateResult)) {
            return new ParamValidateVO(true, sb.toString(), validateResult, dolRequestParams != null ? dolRequestParams.getDolOriginalRequestBody() : null,
                    dolRequestParams != null ? dolRequestParams.getDolRequestBody() : null);
        }
        return new ParamValidateVO(false, sb.toString(), validateResult, dolRequestParams != null ? dolRequestParams.getDolOriginalRequestBody() : null,
                dolRequestParams != null ? dolRequestParams.getDolRequestBody() : null);
    }

    // ignoreRequest项格式uri[:][param1][,param2]
    private static String getMthRequestBodyArgs(String uri, List<String> ignoreRequest, DolRequestParams dolRequestParams) {
        if(ignoreRequest == null || ignoreRequest.size() == 0) {
            return dolRequestParams.getDolOriginalRequestBody();
        }
        for(String ignoreUri : ignoreRequest) {
            if(!ignoreUri.contains(uri)) {
                continue;
            }
            String[] uriParams = ignoreUri.split(":");
            if(!uriParams[0].equals(uri)) {
                continue;
            }
            log.info("getMthRequestBodyArgs ignoreUri={}", ignoreUri);
            if(uriParams.length == 1) {
                return "请求参数忽略";
            }
            ObjectNode paramsObject = JSONUtils.parseObject(dolRequestParams.getDolOriginalRequestBody());
            boolean replaceIgnore = false;
            uriParams = uriParams[1].split(",");
            for(int i = 0; i < uriParams.length; i++) {
                if(uriParams[i] == null || uriParams[i].trim().length() == 0) {
                    continue;
                }
                if(paramsObject.get(uriParams[i]) == null) {
                    continue;
                }
                paramsObject.put(uriParams[i], "该参数字段忽略");
                replaceIgnore = true;
            }
            if(replaceIgnore) {
                return paramsObject.toString();
            }
            return dolRequestParams.getDolOriginalRequestBody();
        }
        return dolRequestParams.getDolOriginalRequestBody();
    }

    private static boolean checkMthRequestBody(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] argValues = point.getArgs();
        if(parameters == null || parameters.length == 0) {
            return false;
        }
        for(int i = 0; i < argValues.length; i++) {
            if(argValues[i] instanceof ServletRequest || argValues[i] instanceof ServletResponse
                    || argValues[i] instanceof HttpSession) {
                continue;
            }
            if(checkValidateParam(parameters[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验请求的参数
     * @param point
     * @return
     */
    public static DolRequestParams getRequestParams(ProceedingJoinPoint point, HttpServletRequest request) {
        try {
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Object[] argValues = point.getArgs();
            String[] argNames = methodSignature.getParameterNames();
            Parameter[] parameters = methodSignature.getMethod().getParameters();

            DolRequestParams dolRequestParams = null;
            if(argNames != null && argNames.length > 0) {
                dolRequestParams = new DolRequestParams();
                dolRequestParams.setHasCustomBody(false);
                Map<String, Object> paramMap = new HashMap<>();
                dolRequestParams.setParamMap(paramMap);
                for(int i = 0; i < argNames.length; i++) {
                    if(argValues[i] instanceof ServletRequest || argValues[i] instanceof ServletResponse
                            || argValues[i] instanceof HttpSession || argValues[i] instanceof MultipartFile
                            || argValues[i] instanceof MultipartFile[]) {
                        continue;
                    }
                    if(checkValidateParam(parameters[i])) {
                        String requestBody = WebUtils.getBody(request);
                        argValues[i] = JSONUtils.parseObject(requestBody, argValues[i].getClass());
                        dolRequestParams.setDolRequestBody(argValues[i]);
                        dolRequestParams.setDolOriginalRequestBody(requestBody);
                        dolRequestParams.setHasCustomBody(true);
                        dolRequestParams.setDolRequestBodyRequired(parameters[i].getAnnotation(DolRequestBody.class).required());
                    } else {
                        paramMap.put(argNames[i], argValues[i]);
                    }
                }
                return dolRequestParams;
            }
        } catch (Exception e) {
            log.error("get post params error.", e);
            throw e;
        }
        return null;
    }

    /**
     * 获取给 "方法参数" 进行注解的值
     *
     * @return 按参数顺序排列的参数名列表
     */
    public static boolean checkValidateParam(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        if(annotations == null || annotations.length == 0) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof DolRequestBody) {
                return true;
            }
        }
        return false;
    }
}
