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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单信息表 t_ds_menu
 * </p>
 *
 * @author tanksphere 2024-05-06 17:17:51 353
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ds_menu")
public class Menu implements Serializable {
    /**
     * id 描述:主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer tenantId;

    /**
     * code 描述:路径编码
     */
    private String code;

    /**
     * title 描述:标题
     */
    private String title;

    /**
     * parent_id 描述:父级节点
     */
    private Long parentId;

    /**
     * href 描述:资源路径
     */
    private String href;

    /**
     * icon 描述:图标
     */
    private String icon;

    /**
     * type 描述:类型,1:菜单,0非菜单
     */
    private String type;

    /**
     * order_num 描述:排序
     */
    private Integer orderNum;

    /**
     * enabled 描述:状态 1:启用 2.冻结
     */
    private String enabled;

    /**
     * description 描述:描述
     */
    private String description;

    /**
     * create_user 描述:创建用户的登录名
     */
    private String createUser;

    /**
     * create_time 描述:创建时间
     */
    private Date createTime;

    /**
     * update_user 描述:最后更新用户的登录名
     */
    private String updateUser;

    /**
     * update_time 描述:更新时间
     */
    private Date updateTime;

}