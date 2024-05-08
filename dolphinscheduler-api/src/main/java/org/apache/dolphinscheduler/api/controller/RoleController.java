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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.dto.role.RoleMenuResponse;
import org.apache.dolphinscheduler.api.dto.role.RoleRequest;
import org.apache.dolphinscheduler.api.dto.role.RoleResponse;
import org.apache.dolphinscheduler.api.dto.role.UserRoleResponse;
import org.apache.dolphinscheduler.api.service.RoleService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.dao.entity.Role;
import org.apache.dolphinscheduler.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 角色控制器
 * @Author tansphere
 * @Date 2024/5/7 15:54
 * @Verson 1.0
 **/
@Slf4j
@Tag(name = "角色相关操作")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "save", description = "角色保存")
    @PostMapping(value = "/save")
    @ResponseStatus(HttpStatus.OK)
    public Result<Role> save(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                             @RequestBody RoleRequest roleRequest) {
        return Result.success(roleService.save(loginUser, roleRequest));
    }

    @Operation(summary = "queryAll", description = "查询所有角色")
    @GetMapping(value = "/queryAll")
    @ResponseStatus(HttpStatus.OK)
    public Result<List<RoleResponse>> queryAll(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return Result.success(roleService.queryByUser(loginUser));
    }

    @Operation(summary = "queryById", description = "根据角色id查询角色详情")
    @GetMapping(value = "/queryById")
    @ResponseStatus(HttpStatus.OK)
    public Result<RoleResponse> queryById(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                          @RequestParam Long id) {
        return Result.success(roleService.queryById(id, loginUser));
    }

    @Operation(summary = "menuSave", description = "角色菜单权限保存")
    @PostMapping(value = "/menu/save/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Result<RoleMenuResponse> menuSave(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable Long roleId,
                                             @RequestBody List<Long> menuIdList) {
        return Result.success(roleService.menuSave(roleId, menuIdList, loginUser));
    }
    @Operation(summary = "queryRoleMenus", description = "查询角色菜单")
    @GetMapping(value = "/menu/query/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Result<RoleMenuResponse> queryRoleMenus(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable Long roleId) {
        return Result.success(roleService.queryRoleMenus(roleId, loginUser));
    }
}
