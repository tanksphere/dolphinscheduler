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
 * 角色基本信息表 t_ds_role
 * </p> 
 * 
 * @author tanksphere 2024-05-07 10:53:01 159
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ds_role")
public class Role implements Serializable {
    /**
     * id 描述:主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * tenant_id 描述:tenant id
     */
    private Integer tenantId;

    /**
     * name 描述:角色名称
     */
    private String name;

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