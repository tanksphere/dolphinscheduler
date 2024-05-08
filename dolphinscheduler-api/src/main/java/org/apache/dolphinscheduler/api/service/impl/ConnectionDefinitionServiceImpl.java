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

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.apache.dolphinscheduler.api.utils.StringUtils;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.common.enums.HttpContentType;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpRequestDataFormat;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinition;
import org.apache.dolphinscheduler.dao.entity.ConnectionEnvDefinition;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.ConnectionDefinitionMapper;
import org.apache.dolphinscheduler.dao.mapper.ConnectionEnvDefinitionMapper;
import org.apache.dolphinscheduler.dao.model.PageListingResult;
import org.apache.dolphinscheduler.dao.repository.ConnectionDefinitionDao;
import org.apache.dolphinscheduler.plugin.task.api.TaskException;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.apache.dolphinscheduler.plugin.task.api.utils.ParameterUtils;
import org.apache.dolphinscheduler.plugin.task.http.HttpParametersType;
import org.apache.dolphinscheduler.plugin.task.http.HttpProperty;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public Map<String, Object> testConnectionDefinition(User loginUser, ConnectionCreateRequest request) {
        Map<String, Object> result = new HashMap<>();

        result.put(Constants.STATUS, Status.SUCCESS);
        result.put(Constants.DATA_LIST, testHttpRequest(request));
        return result;
    }

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
            ConnectionDefinition byNameDefinition = connectionDefinitionMapper.selectByNameAndTenantId(name, loginUser.getTenantId());
            if(byNameDefinition != null && byNameDefinition.getId() != id.intValue()) {
                log.warn("connection definition with the same name already exists, connectionsDefinitionName:{}.", name);
                throw new ServiceException(Status.CONNECTION_DEFINITION_NAME_EXIST, name);
            }
        } else {
            ConnectionDefinition byNameDefinition = connectionDefinitionMapper.selectByNameAndTenantId(name, loginUser.getTenantId());
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
            definition.setUserId(loginUser.getId());
            definition.setTenantId(loginUser.getTenantId());
            affectRow = connectionDefinitionMapper.updateSelectiveById(definition);
        } else {
            definition = new ConnectionDefinition(name, url, httpMethod, httpContentType, requestDataFormat, httpParams,
                    httpBodyJson, httpCheckCondition, loginUser.getId(), loginUser.getTenantId(), description);
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
                connectionsDefinitionDao.listingConnectionsDefinition(pageNo, pageSize, searchVal, loginUser.getTenantId());
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
//        ConnectionDefinition definition = connectionDefinitionMapper.selectById(id);
        ConnectionDefinition definition = connectionDefinitionMapper.selectByIdAndTenantId(id, loginUser.getTenantId());
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
        result.setData(connectionDefinitions2Resp(connectionsDefinitionDao.queryByTenantId(searchVal, userId)));
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


    /**                        以下是httpRequest测试请求代码                         **/
    /**                        以下是httpRequest测试请求代码                         **/
    /**                        以下是httpRequest测试请求代码                         **/

    public Map<String, Object> testHttpRequest(ConnectionCreateRequest requestDefinition) throws TaskException {
        Map<String, Object> resultMap = new HashMap<>();
        long startTime = System.currentTimeMillis();
        String formatTimeStamp = DateUtils.formatTimeStamp(startTime);
        String statusCode = null;
        String body = null;
        try (
                CloseableHttpClient client = createHttpClient(requestDefinition.getTimeout());
                CloseableHttpResponse response = sendRequest(client, requestDefinition)) {
            statusCode = String.valueOf(getStatusCode(response));
            body = getResponseBody(response);
            long costTime = System.currentTimeMillis() - startTime;
            log.info(
                    "testHttpRequest startTime: {}, httpUrl: {}, httpMethod: {}, costTime : {} milliseconds, statusCode : {}, body : {}, log",
                    formatTimeStamp, requestDefinition.getUrl(),
                    requestDefinition.getHttpMethod(), costTime, statusCode, body);
            resultMap.put("httpCode", statusCode);
            resultMap.put("response", StringUtils.isEmpty(body) && !JSONUtils.checkJsonValid(body) ? body : JSONUtils.parseObject(body, Map.class));
        } catch (Exception e) {
            log.error("testHttpRequest httpUrl[" + requestDefinition.getUrl() + "] connection failed：" + body, e);
            resultMap.put("httpCode", "500");
            resultMap.put("response", e.getMessage());
        }

        return resultMap;
    }

    /**
     * send request
     *
     * @param client client
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    protected CloseableHttpResponse sendRequest(CloseableHttpClient client, ConnectionCreateRequest createRequest) throws IOException {
        RequestBuilder builder = createRequestBuilder(createRequest.getHttpMethod());

        addRequestParams(builder, createRequest.getHttpParams(), createRequest.getHttpBody());
        String requestUrl = createRequest.getUrl();
        HttpUriRequest request = builder.setUri(requestUrl).build();
        setHeaders(request, createRequest.getHttpParams());
        return client.execute(request);
    }

    /**
     * set headers
     *
     * @param request request
     * @param httpPropertyList http property list
     */
    protected void setHeaders(HttpUriRequest request, List<ConnectionCreateRequest.HttpParam> httpPropertyList) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(httpPropertyList)) {
            for (ConnectionCreateRequest.HttpParam property : httpPropertyList) {
                if ("HEADER".equals(property.getHttpParametersType())) {
                    request.addHeader(property.getProp(), property.getValue());
                }
            }
        }
    }

    /**
     * create http client
     *
     * @return CloseableHttpClient
     */
    protected CloseableHttpClient createHttpClient(int timeout) {
        final RequestConfig requestConfig = requestConfig(timeout);
        HttpClientBuilder httpClientBuilder;
        httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        return httpClientBuilder.build();
    }

    /**
     * request config
     *
     * @return RequestConfig
     */
    private RequestConfig requestConfig(int timeout) {
        return RequestConfig.custom().setSocketTimeout(timeout)
                .setConnectTimeout(timeout).build();
    }

    /**
     * create request builder
     *
     * @return RequestBuilder
     */
    protected RequestBuilder createRequestBuilder(HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.GET) {
            return RequestBuilder.get();
        } else if (httpMethod == HttpMethod.POST) {
            return RequestBuilder.post();
        } else if (httpMethod == HttpMethod.HEAD) {
            return RequestBuilder.head();
        } else if (httpMethod == HttpMethod.PUT) {
            return RequestBuilder.put();
        } else if (httpMethod == HttpMethod.DELETE) {
            return RequestBuilder.delete();
        } else {
            return null;
        }

    }

    /**
     * add request params
     *
     * @param builder buidler
     * @param httpParamList http param list
     * @param httpBodyMap http body map
     */
    protected void addRequestParams(RequestBuilder builder, List<ConnectionCreateRequest.HttpParam> httpParamList, Map<String, Object> httpBodyMap) {
        if (httpBodyMap != null && !httpBodyMap.isEmpty()) {
            builder.setEntity(new StringEntity(
                    JSONUtils.toJsonString(httpBodyMap),
                    ContentType.create(ContentType.APPLICATION_JSON.getMimeType(),
                            StandardCharsets.UTF_8)));
        }

        if (!CollectionUtils.isEmpty(httpParamList)) {
            ObjectNode jsonParam = JSONUtils.createObjectNode();
            for (ConnectionCreateRequest.HttpParam property : httpParamList) {
                if (property.getHttpParametersType() != null) {
                    if (property.getHttpParametersType().equals("PARAMETER")) {
                        builder.addParameter(property.getProp(), property.getValue());
                    }
                }
            }
            if (builder.getEntity() == null) {
                builder.setEntity(new StringEntity(
                        jsonParam.toString(),
                        ContentType.create(ContentType.APPLICATION_JSON.getMimeType(),
                                StandardCharsets.UTF_8)));
            }
        }
    }

    /**
     * get response body
     *
     * @param httpResponse http response
     * @return response body
     * @throws ParseException parse exception
     * @throws IOException io exception
     */
    protected String getResponseBody(CloseableHttpResponse httpResponse) throws ParseException, IOException {
        if (httpResponse == null) {
            return null;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
    }

    /**
     * get status code
     *
     * @param httpResponse http response
     * @return status code
     */
    protected int getStatusCode(CloseableHttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }
}
