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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.dto.role.RoleMenuResponse;
import org.apache.dolphinscheduler.api.dto.role.RoleRequest;
import org.apache.dolphinscheduler.api.dto.role.RoleResponse;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.MenuService;
import org.apache.dolphinscheduler.api.service.RoleService;
import org.apache.dolphinscheduler.dao.entity.Role;
import org.apache.dolphinscheduler.dao.entity.RoleMenu;
import org.apache.dolphinscheduler.dao.entity.RoleMenuLog;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.RoleMapper;
import org.apache.dolphinscheduler.dao.mapper.RoleMenuLogMapper;
import org.apache.dolphinscheduler.dao.mapper.RoleMenuMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description roleservice
 * @Author tanksphere
 * @Date 2024/5/7 16:01
 * @Verson 1.0
 **/
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleMenuLogMapper roleMenuLogMapper;

    @Autowired
    private MenuService menuService;

    public RoleMenuResponse queryRoleMenus(Long roleId, User loginUser) {
        List<RoleMenu> newRoleMenuList = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("role_id", roleId).eq("tenant_id", loginUser.getTenantId()));
        RoleMenuResponse response = new RoleMenuResponse();
        if (newRoleMenuList.isEmpty()) {
            return response;
        }
        response.setRoleResponse(queryById(roleId, loginUser));
        response.setMenuItem(menuService.queryMenuTreesByIds(newRoleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList())));
        return response;
    }

    @Transactional
    public RoleMenuResponse menuSave(Long roleId, List<Long> menuIdList, User loginUser) {
        checkAdminPermissions(loginUser);
        // 1. saveLog
        List<RoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("role_id", roleId).eq("tenant_id", loginUser.getTenantId()));
        if (!roleMenuList.isEmpty()) {
            roleMenuList.stream().forEach(roleMenu -> {
                RoleMenuLog log = new RoleMenuLog();
                BeanUtils.copyProperties(roleMenu, log, "id");
                log.setRoleMenuId(roleMenu.getId());
                roleMenuLogMapper.insertSelective(log);
            });
        }

        // 2. delete
        roleMenuMapper.delete(new QueryWrapper<RoleMenu>().eq("role_id", roleId).eq("tenant_id", loginUser.getTenantId()));

        // 3. save
        menuIdList.stream().forEach(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setTenantId(loginUser.getTenantId());
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateUser(loginUser.getId().toString());
            roleMenu.setUpdateUser(loginUser.getId().toString());
            roleMenuMapper.insertSelective(roleMenu);
        });

        // 4. query role menu
        return queryRoleMenus(roleId, loginUser);
    }

    @Override
    public RoleResponse queryById(Long roleId, User loginUser) {
        checkAdminPermissions(loginUser);
        Role role = roleMapper.selectByIdAndTenantId(roleId, loginUser.getTenantId());
        if (role == null) {
            return null;
        }
        return new RoleResponse(role.getId(), role.getName(), role.getDescription());
    }

    @Override
    public List<RoleResponse> queryByUser(User loginUser) {
        checkAdminPermissions(loginUser);
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>().eq("tenant_id", loginUser.getTenantId()));
        if(roleList.isEmpty()) {
            return Collections.emptyList();
        }
        return roleList.stream()
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public Role save(User loginUser, RoleRequest roleRequest) {
        checkAdminPermissions(loginUser);
        Role role = new Role();
        role.setId(roleRequest.getId());
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setTenantId(loginUser.getTenantId());
        if(roleRequest.getId() == null) {
            role.setCreateUser(loginUser.getId().toString());
        }
        role.setUpdateUser(loginUser.getId().toString());
        checkRepeat(role);
        if(roleRequest.getId() == null) {
            roleMapper.insertSelective(role);
        } else {
            roleMapper.updateByIdSelective(role);
        }
        return role;
    }

    private void checkRepeat(Role targetRole) {
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("name", targetRole.getName()).eq("tenant_id", targetRole.getTenantId()));
        if (targetRole.getId() != null) {
            if(role != null && !role.getId().equals(targetRole.getId())) {
                throw new ServiceException(Status.ROLE_REPEAT_ERROR);
            }
        } else {
            if(role != null) {
                throw new ServiceException(Status.ROLE_REPEAT_ERROR);
            }
        }
    }
}
