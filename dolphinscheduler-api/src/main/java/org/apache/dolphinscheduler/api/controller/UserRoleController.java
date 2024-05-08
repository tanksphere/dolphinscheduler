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
import org.apache.dolphinscheduler.api.dto.role.UserRoleResponse;
import org.apache.dolphinscheduler.api.service.UserRoleService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 用户角色控制器
 * @Author tansphere
 * @Date 2024/5/7 15:54
 * @Verson 1.0
 **/
@Slf4j
@Tag(name = "用户角色相关操作")
@RestController
@RequestMapping("/user/role")
public class UserRoleController extends BaseController {

    @Autowired
    private UserRoleService userRoleService;

    @Parameters({
            @Parameter(name = "userId", description = "用户id", schema = @Schema(implementation = Long.class, example = "1")),
            @Parameter(name = "roleIdList", description = "角色id列表", schema = @Schema(implementation = List.class, example = "[1,2,3]"))
    })
    @Operation(summary = "userSave", description = "保存用户角色列表")
    @PostMapping(value = "/save/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Result<UserRoleResponse> userSave(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable Long userId,
                                             @RequestBody List<Long> roleIdList) {
        return Result.success(userRoleService.userRoleSave(userId, roleIdList, loginUser));
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户id", schema = @Schema(implementation = Long.class, example = "1")),
    })
    @Operation(summary = "queryUserRoles", description = "查询用户角色列表")
    @GetMapping(value = "/query/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Result<UserRoleResponse> queryUserRoles(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable Long userId) {
        return Result.success(userRoleService.queryUserRoleList(userId, loginUser));
    }
}
