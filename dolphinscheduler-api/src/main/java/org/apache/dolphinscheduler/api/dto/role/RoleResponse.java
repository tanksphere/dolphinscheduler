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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 角色响应参数
 * @Author tanksphere
 * @Date 2024/5/7 15:57
 * @Verson 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ROLE")
public class RoleResponse {
    @Schema(required = true, description = "角色id")
    private Long id;
    @Schema(required = true, description = "角色名称")
    private String name;
    @Schema(required = true, description = "角色描述")
    private String description;
}

