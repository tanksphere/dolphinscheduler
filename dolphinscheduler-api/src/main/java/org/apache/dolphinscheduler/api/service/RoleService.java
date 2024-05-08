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

import org.apache.dolphinscheduler.api.dto.role.RoleMenuResponse;
import org.apache.dolphinscheduler.api.dto.role.RoleRequest;
import org.apache.dolphinscheduler.api.dto.role.RoleResponse;
import org.apache.dolphinscheduler.api.dto.role.UserRoleResponse;
import org.apache.dolphinscheduler.dao.entity.Role;
import org.apache.dolphinscheduler.dao.entity.User;

import java.util.List;

public interface RoleService {
    Role save(User loginUser, RoleRequest roleRequest);
    List<RoleResponse> queryByUser(User loginUser);
    RoleResponse queryById(Long roleId, User loginUser);

    RoleMenuResponse menuSave(Long roleId, List<Long> menuIdList, User loginUser);
    RoleMenuResponse queryRoleMenus(Long roleId, User loginUser);
}
