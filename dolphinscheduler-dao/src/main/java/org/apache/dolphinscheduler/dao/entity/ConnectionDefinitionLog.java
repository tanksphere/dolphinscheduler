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

package org.apache.dolphinscheduler.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.dolphinscheduler.common.enums.HttpMethod;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ds_connection_definition_log")
public class ConnectionDefinitionLog {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * connectionsDefinitionId
     */
    private Integer connectionDefinitionId;

    /**
     * name
     */
    private String name;

    /**
     * project code
     */
    private long projectCode;

    /**
     * project name
     */
    @TableField(exist = false)
    private String projectName;

    private int type;

    private String url;

    private HttpMethod httpMethod;

    private String httpMedia;

    private String httpParams;

    private String httpBody;

    private String httpCheckCondition;

    /**
     * process user id
     */
    private int userId;

    /**
     * create user name
     */
    @TableField(exist = false)
    private String userName;

    private int version;

    private String description;

    private int flag;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * process warning time out. unit: minute
     */
    private int timeout;

    @TableField(exist = false)
    private List<ConnectionEnvDefinition> envList;
}
