package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.Menu;

public interface MenuMapper extends BaseMapper<Menu> {

    int insertSelective(Menu record);

    int updateByIdSelective(Menu record);
}