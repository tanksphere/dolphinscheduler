package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.RoleComponents;

public interface RoleComponentsMapper extends BaseMapper<RoleComponents> {

    int insertSelective(RoleComponents record);

    int updateByIdSelective(RoleComponents record);

}