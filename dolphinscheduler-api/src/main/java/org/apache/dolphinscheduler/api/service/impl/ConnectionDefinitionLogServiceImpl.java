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
import org.apache.dolphinscheduler.api.service.ConnectionDefinitionLogService;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinition;
import org.apache.dolphinscheduler.dao.entity.ConnectionDefinitionLog;
import org.apache.dolphinscheduler.dao.mapper.ConnectionDefinitionLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConnectionDefinitionLogServiceImpl implements ConnectionDefinitionLogService {

    @Autowired
    private ConnectionDefinitionLogMapper connectionDefinitionLogMapper;

    @Override
    public boolean saveLog(ConnectionDefinition definition) {
        return connectionDefinitionLogMapper.insert(copy(definition)) > 0;
    }

    private ConnectionDefinitionLog copy(ConnectionDefinition definition) {
        ConnectionDefinitionLog log = new ConnectionDefinitionLog();
        BeanUtils.copyProperties(definition, log, "id", "createTime", "updateTime");
        log.setConnectionDefinitionId(definition.getId());
        return log;
    }
}
