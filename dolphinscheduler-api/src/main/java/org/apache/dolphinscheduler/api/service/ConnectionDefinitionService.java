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

package org.apache.dolphinscheduler.api.service;

import org.apache.dolphinscheduler.api.dto.connection.ConnectionCreateRequest;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionDefinitionResponse;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.enums.HttpContentType;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpRequestDataFormat;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinition;
import org.apache.dolphinscheduler.dao.entity.User;

import java.util.List;
import java.util.Map;

/**
 * collections definition service
 */
public interface ConnectionDefinitionService {

    Map<String, Object> saveConnectionDefinition(User loginUser, ConnectionCreateRequest request);

    /**
     * create process definition
     *
     * @param loginUser login user
     * @param name http definition name
     * @param httpMethod http method
     * @param httpContentType http content type
     * @param requestDataFormat http request data format
     * @param httpParams http params
     * @param httpBodyJson http body json
     * @param httpCheckCondition http check condition
     * @param description description
     * @param timeout timeout
     * @return create result code
     */
    Map<String, Object> create(User loginUser,
                                                    Integer id,
                                                    String name,
                                                    String url,
                                                    HttpMethod httpMethod,
                                                    HttpContentType httpContentType,
                                                    HttpRequestDataFormat requestDataFormat,
                                                    String httpParams,
                                                    String httpBodyJson,
                                                    String httpCheckCondition,
                                                    String description,
                                                    List<ConnectionCreateRequest.ConnectionEnv> envList,
                                                    int timeout);

    /**
     * query collections definition list paging
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @param userId user id
     * @return process definition page
     */
    Result<PageInfo<ConnectionDefinitionResponse>> queryListPaging(User loginUser,
                                                           String searchVal,
                                                           Integer userId,
                                                           Integer pageNo,
                                                           Integer pageSize);

    /**
     * query collections definition list
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param userId user id
     * @return process definition page
     */
    Result<List<ConnectionDefinitionResponse>> queryListByUser(User loginUser,
                                                               String searchVal,
                                                               Integer userId);

    Result queryById(User loginUser, Integer id);
}
