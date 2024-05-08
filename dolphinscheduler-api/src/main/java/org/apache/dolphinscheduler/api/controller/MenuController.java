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
import org.apache.dolphinscheduler.api.dto.menu.MenuItem;
import org.apache.dolphinscheduler.api.dto.menu.MenuRequest;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.MenuService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.dao.entity.Menu;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.common.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.dolphinscheduler.api.enums.Status.MENU_TREE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.USER_MENU_TREE_ERROR;


@Slf4j
@Tag(name = "菜单相关操作")
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

	private static final int EXPIRE_SECONDS = 30 * 60;

	@Autowired
	private MenuService menuService;

	@Operation(summary = "tree", description = "菜单树查询")
	@PostMapping(value = "/tree")
	@ResponseStatus(HttpStatus.OK)
	@ApiException(MENU_TREE_ERROR)
	public Result<List<MenuItem>> tree(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
		return Result.success(menuService.createMenuTrees(loginUser));
	}

	@Operation(summary = "userTree", description = "用户菜单树查询")
	@GetMapping(value = "/userTree")
	@ResponseStatus(HttpStatus.OK)
	@ApiException(USER_MENU_TREE_ERROR)
	public Result<List<MenuItem>> userTree(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
		return Result.success(menuService.createUserMenuTrees(loginUser));
	}

	@Operation(summary = "save", description = "菜单保存")
	@PostMapping(value = "/save")
	@ResponseStatus(HttpStatus.OK)
	public Result<Menu> save(@Parameter(hidden = true) @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
							@RequestBody MenuRequest menuRequest) {
		return Result.success(menuService.save(loginUser, menuRequest));
	}

}
