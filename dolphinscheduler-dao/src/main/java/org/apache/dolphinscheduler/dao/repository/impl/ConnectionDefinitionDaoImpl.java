package org.apache.dolphinscheduler.dao.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.NonNull;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinition;
import org.apache.dolphinscheduler.dao.mapper.ConnectionDefinitionMapper;
import org.apache.dolphinscheduler.dao.model.PageListingResult;
import org.apache.dolphinscheduler.dao.repository.BaseDao;
import org.apache.dolphinscheduler.dao.repository.ConnectionDefinitionDao;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConnectionDefinitionDaoImpl extends BaseDao<ConnectionDefinition, ConnectionDefinitionMapper>
        implements ConnectionDefinitionDao {

    public ConnectionDefinitionDaoImpl(@NonNull ConnectionDefinitionMapper connectionDefinitionMapper) {
        super(connectionDefinitionMapper);
    }

    @Override
    public PageListingResult<ConnectionDefinition> listingConnectionsDefinition(int pageNumber, int pageSize, @Nullable String searchVal, int userId) {
        Page<ConnectionDefinition> page = new Page<>(pageNumber, pageSize);
        IPage<ConnectionDefinition> connectionDefinitions =
                mybatisMapper.queryDefineListPaging(page, searchVal, 0);

        return PageListingResult.<ConnectionDefinition>builder()
                .totalCount(connectionDefinitions.getTotal())
                .currentPage(pageNumber)
                .pageSize(pageSize)
                .records(connectionDefinitions.getRecords())
                .build();
    }

    @Override
    public List<ConnectionDefinition> queryByUserId(@Nullable String searchVal, int userId) {
        return mybatisMapper.queryDefineListByUserId(searchVal, 0);
    }
}
