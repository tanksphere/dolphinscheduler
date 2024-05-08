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

package org.apache.dolphinscheduler.api.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description 菜单项
 * @Author tanksphere
 * @Date 2024/5/6 16:48
 * @Verson 1.0
 **/
@Data
@Schema(name = "MENU_ITEM")
public class MenuItem {
    @Schema(required = true, description = "菜单id")
    private Long id;
    @Schema(required = true, description = "菜单code")
    private String code;
    @Schema(required = true, description = "菜单名称")
    private String title;
    @Schema(required = true, description = "菜单描述")
    private String description;
    @Schema(required = true, description = "跳转地址")
    private String href;
    @Schema(required = true, description = "菜单图标")
    private String icon;
    @Schema(required = true, description = "排序值，值越大越靠前")
    private Integer orderNum;
    @Schema(required = false, description = "子菜单列表")
    private List<MenuItem> subMenus;
}
