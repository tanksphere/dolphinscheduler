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
import org.apache.dolphinscheduler.api.dto.menu.MenuItem;
import org.apache.dolphinscheduler.api.dto.menu.MenuRequest;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.MenuService;
import org.apache.dolphinscheduler.dao.entity.Menu;
import org.apache.dolphinscheduler.dao.entity.RoleMenu;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.entity.UserRole;
import org.apache.dolphinscheduler.dao.mapper.MenuMapper;
import org.apache.dolphinscheduler.dao.mapper.RoleMenuMapper;
import org.apache.dolphinscheduler.dao.mapper.UserRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://blog.csdn.net/weixin_42512937/article/details/101106950
 * @Description 菜单service
 * @Author tanksphere
 * @Date 2024/5/6 16:58
 * @Verson 1.0
 **/
@Service
@Slf4j
public class MenuServiceImpl extends BaseServiceImpl implements MenuService {

    public static final String TOP_PARENT_MENU_ID = "0";

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    public List<MenuItem> queryMenuTreesByIds(List<Long> ids) {
        List<Menu> menus = menuMapper.selectBatchIds(ids);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        return createMenuTrees(menus, TOP_PARENT_MENU_ID);
    }

    public Menu save(User loginUser, MenuRequest menuRequest) {
        checkSuperAdminPermissions(loginUser);
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuRequest, menu);
        if (menu.getId() == null) {
            menu.setCreateUser(loginUser.getId().toString());
        }
        menu.setTenantId(loginUser.getTenantId());
        menu.setUpdateUser(loginUser.getId().toString());
        checkRepeat(menu);
        if (menu.getId() == null) {
            menuMapper.insertSelective(menu);
        } else {
            menuMapper.updateByIdSelective(menu);
        }
        return menuMapper.selectOne(new QueryWrapper<Menu>().eq("code", menu.getCode()).eq("enabled", "1"));
    }
    private void checkRepeat(Menu targetMenu) {
        Menu menu = menuMapper.selectOne(new QueryWrapper<Menu>().eq("code", targetMenu.getCode()).eq("enabled", "1").eq("tenant_id", targetMenu.getTenantId()));
        if (targetMenu.getId() != null) {
            if(menu != null && !menu.getId().equals(targetMenu.getId())) {
//                throw new RuntimeException("code is repeat");
                throw new ServiceException(Status.MENU_REPEAT_ERROR);
            }
        } else {
            if(menu != null) {
//                throw new RuntimeException("code is repeat");
                throw new ServiceException(Status.MENU_REPEAT_ERROR);
            }
        }
    }

    public List<MenuItem> createUserMenuTrees(User loginUser) {
        List<Menu> menus;
        if (isAdmin(loginUser)) {
            menus = menuMapper.selectList(new QueryWrapper<Menu>().eq("tenant_id", loginUser.getTenantId()).eq("enabled", 1));
        } else {
            // 1. query roles
            List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", loginUser.getId()).eq("tenant_id", loginUser.getTenantId()).eq("enabled", 1));
            if(userRoles.isEmpty()) {
                return Collections.emptyList();
            }
            // 2. query menus by roles, and filter repeat
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().in("role_id", userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList())).eq("tenant_id", loginUser.getTenantId()));
            if(roleMenus.isEmpty()) {
                return Collections.emptyList();
            }
            menus = menuMapper.selectList(new QueryWrapper<Menu>().in("id", roleMenus.stream().map(RoleMenu::getMenuId).distinct().collect(Collectors.toList())));
        }
        // 3. create menu tree
        return createMenuTrees(menus, TOP_PARENT_MENU_ID);
    }

    @Override
    public List<MenuItem> createMenuTrees(User loginUser) {
        checkAdminPermissions(loginUser);
        List<Menu> menus = menuMapper.selectList(new QueryWrapper<Menu>().eq("enabled", "1").eq("tenant_id", loginUser.getTenantId()));
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        return createMenuTrees(menus, TOP_PARENT_MENU_ID);
    }

    private List<MenuItem> createMenuTrees(List<Menu> menus, String parentMenuId) {
        List<MenuItem> menuTrees = new LinkedList<>();
        for (Menu menu : menus) {
            if (parentMenuId.equals(menu.getParentId().toString())) {
                MenuItem item = new MenuItem();
                item.setId(menu.getId());
                item.setCode(menu.getCode());
                item.setTitle(menu.getTitle());
                item.setDescription(menu.getDescription());
                item.setHref(menu.getHref());
                item.setIcon(menu.getIcon());
                item.setOrderNum(menu.getOrderNum());
                List<MenuItem> subMenus = createMenuTrees(menus, item.getId().toString());
                if (!CollectionUtils.isEmpty(subMenus)) {
                    item.setSubMenus(subMenus);
                }
                menuTrees.add(item);
            }
        }
        if (!menuTrees.isEmpty()) {
            // 根据orderNum排序menuTrees
            menuTrees.sort((o1, o2) -> o2.getOrderNum() - o1.getOrderNum());
        }
        return menuTrees;
    }
}
