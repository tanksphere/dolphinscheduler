package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.UserRoleLog;

public interface UserRoleLogMapper extends BaseMapper<UserRoleLog> {

    int insertSelective(UserRoleLog record);

}