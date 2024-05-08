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

/**
 * @Description 菜单
 * @Author tanksphere
 * @Date 2024/5/7 11:16
 * @Verson 1.0
 **/
@Data
@Schema
public class MenuRequest {
    @Schema(required = false, description = "id")
    private Long id;
    @Schema(description = "code：菜单编码", required = true)
    private String code;
    @Schema(description = "title：菜单名称", required = true)
    private String title;
    @Schema(description = "parentId，父节点id", required = false)
    private Long parentId;
    @Schema(description = "href：菜单引用", required = true)
    private String href;
    @Schema(description = "type, 1：菜单，0：非菜单，默认0", required = false, requiredProperties={"1", "0"})
    private String type;
    @Schema(description = "orderNum", required = false)
    private Integer orderNum;
    @Schema(description = "enabled, 状态 1:启用 2.冻结", required = false)
    private String enabled;
    @Schema(description = "description，描述", required = false)
    private String description;
}
