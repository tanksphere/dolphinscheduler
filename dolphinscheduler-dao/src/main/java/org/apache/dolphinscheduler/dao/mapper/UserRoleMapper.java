package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    int insertSelective(UserRole record);

    int updateByIdSelective(UserRole record);
}