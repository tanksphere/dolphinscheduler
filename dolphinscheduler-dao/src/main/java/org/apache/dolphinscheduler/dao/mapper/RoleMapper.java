package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {

    int insertSelective(Role record);

    int updateByIdSelective(Role record);

    Role selectByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Integer tenantId);
}