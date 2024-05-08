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

package org.apache.dolphinscheduler.api.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.dolphinscheduler.api.dto.menu.MenuItem;

import java.util.List;


/**
 * @Description RoleMenu
 * @Author tanksphere
 * @Date 2024/5/7 17:03
 * @Verson 1.0
 **/
@Data
@NoArgsConstructor
@Schema(name = "ROLE_MENU")
public class RoleMenuResponse {
    @Schema(description = "角色信息", required = true)
    private RoleResponse roleResponse;
    @Schema(description = "菜单项列表", required = false)
    private List<MenuItem> menuItem;
}
