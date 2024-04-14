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


import lombok.Data;
import org.apache.dolphinscheduler.common.enums.HttpContentType;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpRequestDataFormat;

import java.util.List;
import java.util.Map;

@Data
public class ConnectionDefinitionResponse {

    private Integer id;

    /**
     * name
     */
    private String name;

    private String url;

    private HttpMethod httpMethod;

    private HttpContentType httpContentType;

    private HttpRequestDataFormat httpRequestDataFormat;

    private List<ConnectionCreateRequest.HttpParam> httpParams;

    private Map<String, Object> httpBody;

    private String httpCheckCondition;

    private String description;

    /**
     * process warning time out. unit: minute
     */
    private int timeout;

    private List<ConnectionCreateRequest.ConnectionEnv> envList;
}
