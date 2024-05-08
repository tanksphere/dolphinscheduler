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

package org.apache.dolphinscheduler.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.dolphinscheduler.common.enums.UserType;

/**
 * @Description UserResponse
 * @Author tanksphere
 * @Date 2024/5/7 17:56
 * @Verson 1.0
 **/
@Data
@Schema(name = "USER_RESPONSE")
public class UserResponse {

    @Schema(description = "id", required = true)
    private Integer id;

    @Schema(description = "userName", required = true)
    private String userName;

    @Schema(description = "email", required = false)
    private String email;

    @Schema(description = "phone", required = false)
    private String phone;

    @Schema(description = "userType", required = true)
    private UserType userType;

    @Schema(description = "tenantId", required = true)
    private int tenantId;

    @Schema(description = "state 0:disable 1:enable", required = true)
    private int state;

    private String queue;

    private String timeZone;
}
