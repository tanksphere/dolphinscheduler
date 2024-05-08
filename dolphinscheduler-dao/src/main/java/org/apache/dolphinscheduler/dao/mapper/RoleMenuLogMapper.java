package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.RoleMenuLog;

public interface RoleMenuLogMapper extends BaseMapper<RoleMenuLog> {

    int insertSelective(RoleMenuLog record);
}