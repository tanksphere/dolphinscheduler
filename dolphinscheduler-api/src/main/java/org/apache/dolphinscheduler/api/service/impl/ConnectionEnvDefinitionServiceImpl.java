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

package org.apache.dolphinscheduler.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.service.ConnectionEnvDefinitionService;
import org.apache.dolphinscheduler.dao.entity.ConnectionEnvDefinition;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.ConnectionEnvDefinitionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ConnectionEnvDefinitionServiceImpl implements ConnectionEnvDefinitionService {

    @Autowired
    private ConnectionEnvDefinitionMapper connectionEnvDefinitionMapper;

    @Override
    public List<ConnectionEnvDefinition> queryCollectionEnvDefinitionList(User loginUser, int connectionDefinitionId) {
        return connectionEnvDefinitionMapper.queryByConnectionDefinitionId(connectionDefinitionId);
    }

    @Override
    public boolean saveConnectionEnvDefinition(List<ConnectionEnvDefinition> envList) {
        connectionEnvDefinitionMapper.deleteByConnectionDefinitionId(envList.get(0).getConnectionDefinitionId());
        AtomicInteger affectRow = new AtomicInteger();
        envList.forEach(env -> {
            affectRow.addAndGet(connectionEnvDefinitionMapper.insert(env));
        });
        return affectRow.get() == envList.size();
    }
}
