package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.RoleMenu;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    int insertSelective(RoleMenu record);

    int updateByIdSelective(RoleMenu record);

}