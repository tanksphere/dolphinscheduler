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

package org.apache.dolphinscheduler.api.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionCreateRequest;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionDefinitionResponse;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.ConnectionDefinitionLogService;
import org.apache.dolphinscheduler.api.service.ConnectionDefinitionService;
import org.apache.dolphinscheduler.api.service.ConnectionEnvDefinitionService;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.common.enums.HttpContentType;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpRequestDataFormat;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinition;
import org.apache.dolphinscheduler.dao.entity.ConnectionEnvDefinition;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.ConnectionDefinitionMapper;
import org.apache.dolphinscheduler.dao.mapper.ConnectionEnvDefinitionMapper;
import org.apache.dolphinscheduler.dao.model.PageListingResult;
import org.apache.dolphinscheduler.dao.repository.ConnectionDefinitionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * collections definition service
 */
@Service
@Slf4j
public class ConnectionDefinitionServiceImpl extends BaseServiceImpl implements ConnectionDefinitionService {

    @Autowired
    private ConnectionDefinitionDao connectionsDefinitionDao;

    @Autowired
    private ConnectionDefinitionMapper connectionDefinitionMapper;

    @Autowired
    private ConnectionEnvDefinitionMapper connectionEnvDefinitionMapper;

    @Autowired
    private ConnectionEnvDefinitionService connectionEnvDefinitionService;

    @Autowired
    private ConnectionDefinitionLogService connectionDefinitionLogService;

    @Transactional
    public Map<String, Object> saveConnectionDefinition(User loginUser, ConnectionCreateRequest request) {
        return create(loginUser, request.getId(), request.getName(), request.getUrl(),
                request.getHttpMethod(), request.getHttpContentType(), request.getRequestDataFormat(),
                JSONUtils.toJsonString(request.getHttpParams()),
                JSONUtils.toJsonString(request.getHttpBody()),
                request.getHttpCheckCondition(),
                request.getDescription(),
                request.getEnvList(),
                request.getTimeout());
    }

    /**
     * create process definition
     *
     * @param loginUser login user
     * @param name http definition name
     * @param httpMethod http method
     * @param httpContentType http content type
     * @param requestDataFormat request data format
     * @param httpParams http params
     * @param httpBodyJson http body json
     * @param httpCheckCondition http check condition
     * @param description description
     * @param timeout timeout
     * @return create result code
     */
    @Override
    @Transactional
    public Map<String, Object> create(User loginUser,
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
                                                       int timeout) {
        // check if user have write perm for httpConnections
        Map<String, Object> result = new HashMap<>();
//        boolean hasProjectAndWritePerm = projectService.hasProjectAndWritePerm(loginUser, project, result);
//        if (!hasProjectAndWritePerm) {
//            return result;
//        }
        if (checkDescriptionLength(description)) {
            log.warn("Parameter description is too long.");
            throw new ServiceException(Status.DESCRIPTION_TOO_LONG_ERROR);
        }
        // check whether the new http connections define name exist
        ConnectionDefinition definition = null;
        if(id != null && id > 0) {
            definition = connectionDefinitionMapper.selectById(id);
            if(definition == null) {
                log.warn("connection definition with the id does not exists, id:{}.", id);
                throw new ServiceException(Status.CONNECTION_DEFINITION_NOT_EXIST, id);
            }
            ConnectionDefinition byNameDefinition = connectionDefinitionMapper.selectByName(name);
            if(byNameDefinition != null && byNameDefinition.getId() != id.intValue()) {
                log.warn("connection definition with the same name already exists, connectionsDefinitionName:{}.", name);
                throw new ServiceException(Status.CONNECTION_DEFINITION_NAME_EXIST, name);
            }
        } else {
            ConnectionDefinition byNameDefinition = connectionDefinitionMapper.selectByName(name);
            if(byNameDefinition != null) {
                log.warn("connection definition with the same name already exists, connectionsDefinitionName:{}.", name);
                throw new ServiceException(Status.CONNECTION_DEFINITION_NAME_EXIST, name);
            }
        }
        int affectRow;
        if(id != null && id > 0) {
            connectionDefinitionLogService.saveLog(definition);
            definition.setName(name);
            definition.setUrl(url);
            definition.setHttpMethod(httpMethod);
            definition.setHttpContentType(httpContentType);
            definition.setHttpRequestDataFormat(requestDataFormat);
            definition.setHttpParams(httpParams);
            definition.setHttpBody(httpBodyJson);
            definition.setHttpCheckCondition(httpCheckCondition);
            definition.setDescription(description);
            affectRow = connectionDefinitionMapper.updateSelectiveById(definition);
        } else {
            definition = new ConnectionDefinition(name, url, httpMethod, httpContentType, requestDataFormat, httpParams,
                    httpBodyJson, httpCheckCondition, description);
            affectRow = connectionDefinitionMapper.insert(definition);
        }
        if(affectRow < 1) {
            log.warn("connection definition insert or update error, id={}, name={}", id, name);
            throw new ServiceException(Status.CONNECTION_DEFINITION_UPDATE_ERROR, name);
        }
        if(!CollectionUtils.isEmpty(envList)) {
            List<ConnectionEnvDefinition> envDefinitionList = new ArrayList<>();
            ConnectionDefinition finalDefinition = definition;
            envList.forEach(env -> envDefinitionList.add(new ConnectionEnvDefinition(finalDefinition.getId(), env.getDomain(), env.getEnv())));
            connectionEnvDefinitionService.saveConnectionEnvDefinition(envDefinitionList);
        }
        result.put(Constants.STATUS, Status.SUCCESS);
        result.put(Constants.DATA_LIST, definition);
        return result;
    }

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
    @Override
    public Result<PageInfo<ConnectionDefinitionResponse>> queryListPaging(User loginUser,
                                                          String searchVal,
                                                          Integer userId,
                                                          Integer pageNo,
                                                          Integer pageSize) {
        // check user access for project
//        projectService.checkProjectAndAuthThrowException(loginUser, CONNECTIONS_DEFINITION);
        Result result = new Result();
        PageListingResult<ConnectionDefinition> connectionsDefinitionsPageListingResult =
                connectionsDefinitionDao.listingConnectionsDefinition(
                        pageNo, pageSize, searchVal, userId);
        List<ConnectionDefinition> connectionsDefinitions = connectionsDefinitionsPageListingResult.getRecords();

        PageInfo<ConnectionDefinitionResponse> pageInfo = new PageInfo<>(pageNo, pageSize);
        pageInfo.setTotal((int) connectionsDefinitionsPageListingResult.getTotalCount());
        pageInfo.setTotalList(connectionDefinitions2Resp(connectionsDefinitions));
        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result queryById(User loginUser, Integer id) {
        Result result = new Result();
        putMsg(result, Status.SUCCESS);
        ConnectionDefinition definition = connectionDefinitionMapper.selectById(id);
        if(definition == null) {
            return result;
        }
        result.setData(connectionDefinitions2Resp(Lists.newArrayList(definition)));
        return result;
    }

    /**
     * query collections definition list
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param userId user id
     * @return process definition page
     */
    @Override
    public Result<List<ConnectionDefinitionResponse>> queryListByUser(User loginUser,
                                                                      String searchVal,
                                                                      Integer userId) {
        Result result = new Result();
        result.setData(connectionDefinitions2Resp(connectionsDefinitionDao.queryByUserId(searchVal, userId)));
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public List<ConnectionDefinitionResponse> connectionDefinitions2Resp(List<ConnectionDefinition> connections) {
        List<ConnectionDefinitionResponse> respList = new ArrayList<>();
        if(connections == null || connections.isEmpty()) {
            return respList;
        }
        for(ConnectionDefinition definition : connections) {
            ConnectionDefinitionResponse resp = new ConnectionDefinitionResponse();
            resp.setEnvList(connectionEnv2Resp(connectionEnvDefinitionMapper.queryByConnectionDefinitionId(definition.getId())));

            resp.setId(definition.getId());
            resp.setName(definition.getName());
            resp.setUrl(definition.getUrl());
            resp.setHttpMethod(definition.getHttpMethod());
            resp.setHttpContentType(definition.getHttpContentType());
            resp.setHttpRequestDataFormat(definition.getHttpRequestDataFormat());
            resp.setHttpParams(JSONUtils.parseArray(definition.getHttpParams(), ConnectionCreateRequest.HttpParam.class));
            resp.setHttpBody(JSONUtils.parseObject(definition.getHttpBody(), Map.class));
            resp.setDescription(definition.getDescription());
            resp.setTimeout(definition.getTimeout());

            respList.add(resp);
        }
        return respList;
    }

    public List<ConnectionCreateRequest.ConnectionEnv> connectionEnv2Resp(List<ConnectionEnvDefinition> envs) {
        List<ConnectionCreateRequest.ConnectionEnv> respList = new ArrayList<>();
        if(envs == null || envs.isEmpty()) {
            return respList;
        }
        for(ConnectionEnvDefinition env : envs) {
            respList.add(new ConnectionCreateRequest.ConnectionEnv(env.getDomain(), env.getEnv(), 0));
        }
        return respList;
    }
}
