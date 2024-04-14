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

package org.apache.dolphinscheduler.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionCreateRequest;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionDefinitionResponse;
import org.apache.dolphinscheduler.api.dto.connection.ConnectionQueryRequest;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.ConnectionDefinitionService;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.plugin.task.api.utils.ParameterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.apache.dolphinscheduler.api.enums.Status.CREATE_CONNECTION_DEFINITION_ERROR;

@Tag(name = "连接相关操作")
@RestController
@RequestMapping("connections/definition")
@Slf4j
public class ConnectionDefinitionController extends BaseController {

    @Autowired
    private ConnectionDefinitionService connectionDefinitionService;

    @Operation(summary = "saveConnectionDefinition", description = "创建连接")
    @PostMapping(value = "save", consumes = {"application/json"})
    @ApiException(CREATE_CONNECTION_DEFINITION_ERROR)
    public Result saveConnectionDefinition(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @RequestBody ConnectionCreateRequest connectionCreateRequest) {
        Map<String, Object> result = connectionDefinitionService.saveConnectionDefinition(loginUser, connectionCreateRequest);
        return returnDataList(result);
    }

    /**
     * query all connection list
     *
     * @param loginUser login user
     * @return all connection list
     */
    @Operation(summary = "queryById", description = "根据id查询")
    @Parameters({
            @Parameter(name = "id", description = "连接id", schema = @Schema(implementation = Integer.class, example = "1")),
    })
    @GetMapping(value = "/queryById")
    @ResponseStatus(HttpStatus.OK)
    public Result queryById(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                            @RequestParam(value = "id", required = true) Integer id) {
        return connectionDefinitionService.queryById(loginUser, id);
    }

    /**
     * query connection list paging
     *
     * @param loginUser       login user
     * @param connectionQueryReq connectionQueryReq
     * @return project list which the login user have permission to see
     */
    @Operation(summary = "queryListPaging", description = "分页查询")
    @Parameters({
            @Parameter(name = "searchVal", description = "SEARCH_VAL", schema = @Schema(implementation = String.class, example = "test")),
            @Parameter(name = "pageSize", description = "PAGE_SIZE", required = true, schema = @Schema(implementation = int.class, example = "10")),
            @Parameter(name = "pageNo", description = "PAGE_NO", required = true, schema = @Schema(implementation = int.class, example = "1"))
    })
    @GetMapping(value = "queryListPaging")
    @ResponseStatus(HttpStatus.OK)
    public Result<PageInfo<ConnectionDefinitionResponse>> queryProjectListPaging(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                                                 ConnectionQueryRequest connectionQueryReq) {
        checkPageParams(connectionQueryReq.getPageNo(), connectionQueryReq.getPageSize());
        String searchVal = ParameterUtils.handleEscapes(connectionQueryReq.getSearchVal());
        return connectionDefinitionService.queryListPaging(loginUser, searchVal, loginUser.getId(),
                connectionQueryReq.getPageNo(), connectionQueryReq.getPageSize());
    }

    /**
     * query all connection list
     *
     * @param loginUser login user
     * @return all connection list
     */
    @Operation(summary = "queryList", description = "列表查询")
    @Parameters({
            @Parameter(name = "searchVal", description = "SEARCH_VAL", schema = @Schema(implementation = String.class, example = "test")),
    })
    @GetMapping(value = "/queryList")
    @ResponseStatus(HttpStatus.OK)
    public Result queryList(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal) {
        return connectionDefinitionService.queryListByUser(loginUser, searchVal, loginUser.getId());
    }
}
