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

/**
 * @Description 角色请求参数
 * @Author tanksphere
 * @Date 2024/5/7 15:57
 * @Verson 1.0
 **/
@Data
@Schema(name = "ROLE")
public class RoleRequest {
    @Schema(required = false, description = "角色id，新增为null，更新为真实角色id值")
    private Long id;
    @Schema(required = true, description = "角色名称")
    private String name;
    @Schema(required = true, description = "角色描述")
    private String description;
}

