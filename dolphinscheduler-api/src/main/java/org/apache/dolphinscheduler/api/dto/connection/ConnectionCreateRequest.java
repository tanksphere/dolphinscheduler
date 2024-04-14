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

package org.apache.dolphinscheduler.api.dto.connection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.dolphinscheduler.common.enums.HttpContentType;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpRequestDataFormat;

import java.util.List;
import java.util.Map;


@Data
public class ConnectionCreateRequest {

    @Schema(required = false, description = "id")
    private Integer id;
    @Schema(description = "name", required = true)
    private String name;
    @Schema(description = "url", required = true)
    private String url;
    @Schema(description = "httpMethod", required = true)
    private HttpMethod httpMethod;
    @Schema(description = "httpContentType", required = false, requiredProperties={"form-data", "x-www-form-urlencoded", "raw"})
    private HttpContentType httpContentType;
    @Schema(description = "httpContentType", required = false, requiredProperties={"json", "xml", "text"})
    private HttpRequestDataFormat requestDataFormat;
    @Schema(description = "http参数", required = false)
    private List<HttpParam> httpParams;
    @Schema(description = "http请求体，格式json对象", required = false)
    private Map<String, Object> httpBody;
    @Schema(description = "http响应校验条件", required = false)
    private String httpCheckCondition;
    @Schema(description = "连接描述", required = false)
    private String description;
    @Schema(description = "连接环境", required = false)
    private List<ConnectionEnv> envList;
    @Schema(description = "请求超时时间", required = true)
    private Integer timeout;

    @Data
    public static class HttpParam {
        @Schema(description = "prop", required = true)
        private String prop;
        @Schema(description = "参数类型", required = true, requiredProperties = {"PARAMETER", "HEADER"})
        private String httpParametersType;
        @Schema(description = "参数值", required = true)
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionEnv {
        @Schema(description = "域名", required = true)
        private String domain;
        @Schema(description = "环境", required = true, requiredProperties = {"dev", "local", "sit", "uat", "prod"})
        private String env;
        @Schema(description = "连接定义id，前端不需要传值", required = false)
        private Integer connectionDefinitionId;
    }
}
