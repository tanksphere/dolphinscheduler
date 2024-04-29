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

package org.apache.dolphinscheduler.server.master.runner.task.forr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.dolphinscheduler.common.enums.CommandType;
import org.apache.dolphinscheduler.common.enums.WorkflowExecutionStatus;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.dolphinscheduler.dao.mapper.CommandMapper;
import org.apache.dolphinscheduler.dao.mapper.ProcessDefinitionMapper;
import org.apache.dolphinscheduler.dao.repository.ProcessInstanceDao;
import org.apache.dolphinscheduler.dao.repository.TaskInstanceDao;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.ForrInputParameter;
import org.apache.dolphinscheduler.plugin.task.api.parameters.ForrParameters;
import org.apache.dolphinscheduler.service.process.ProcessService;
import org.apache.dolphinscheduler.service.subworkflow.SubWorkflowService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ForrLogicTaskTest {

    @Mock
    private ProcessInstanceDao processInstanceDao;

    @Mock
    private TaskInstanceDao taskInstanceDao;

    @Mock
    private SubWorkflowService subWorkflowService;

    @Mock
    private ProcessService processService;

    @Mock
    private ProcessDefinitionMapper processDefineMapper;

    @Mock
    private CommandMapper commandMapper;

    private ForrParameters forrParameters;

    private ProcessInstance processInstance;

    private TaskExecutionContext taskExecutionContext;

    private ForrLogicTask forrLogicTask;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Set up your test environment before each test.
        forrParameters = new ForrParameters();
        taskExecutionContext = Mockito.mock(TaskExecutionContext.class);
        objectMapper = new ObjectMapper();
        processInstance = new ProcessInstance();
        Mockito.when(processInstanceDao.queryById(Mockito.any())).thenReturn(processInstance);
        forrLogicTask = new ForrLogicTask(
                taskExecutionContext,
                processInstanceDao,
                taskInstanceDao,
                subWorkflowService,
                processService,
                processDefineMapper,
                commandMapper);
    }

    @Test
    void testGenerateParameterGroup() throws Exception {
        ForrInputParameter forrInputParameter1 = new ForrInputParameter();
        forrInputParameter1.setName("param1");
        forrInputParameter1.setValue("a,b,c");
        forrInputParameter1.setSeparator(",");

        ForrInputParameter forrInputParameter2 = new ForrInputParameter();
        forrInputParameter2.setName("param2");
        forrInputParameter2.setValue("1. 2 . 3");
        forrInputParameter2.setSeparator(".");

        List<ForrInputParameter> forrInputParameters =
                Arrays.asList(forrInputParameter1, forrInputParameter2);
        forrParameters.setListParameters(forrInputParameters);
        forrParameters.setFilterCondition("b,2");

        Mockito.when(taskExecutionContext.getPrepareParamsMap()).thenReturn(new HashMap<>());
        Mockito.when(taskExecutionContext.getTaskParams())
                .thenReturn(objectMapper.writeValueAsString(forrParameters));
        taskExecutionContext.setTaskParams(objectMapper.writeValueAsString(forrParameters));

        forrLogicTask = new ForrLogicTask(
                taskExecutionContext,
                processInstanceDao,
                taskInstanceDao,
                subWorkflowService,
                processService,
                processDefineMapper,
                commandMapper);

        List<Map<String, String>> parameterGroup = forrLogicTask.generateParameterGroup();

        Assertions.assertEquals(4, parameterGroup.size()); // expected cartesian product without filtered values is 6

        // Assert the value of parameter groups. Adjust these according to your expectations.
        // Here we only check for a few representative cases to keep the test concise.
        Map<String, String> expectedMap1 = new HashMap<>();
        expectedMap1.put("param1", "a");
        expectedMap1.put("param2", "1");

        Map<String, String> expectedMap2 = new HashMap<>();
        expectedMap2.put("param1", "a");
        expectedMap2.put("param2", "3");

        Map<String, String> expectedMap3 = new HashMap<>();
        expectedMap3.put("param1", "c");
        expectedMap3.put("param2", "1");

        Map<String, String> expectedMap4 = new HashMap<>();
        expectedMap4.put("param1", "c");
        expectedMap4.put("param2", "3");

        assert (parameterGroup.containsAll(Arrays.asList(expectedMap1, expectedMap2, expectedMap3, expectedMap4)));
    }

    @Test
    void testSingleParameterGroup() throws Exception {
        ForrInputParameter forrInputParameter1 = new ForrInputParameter();
        forrInputParameter1.setName("param1");
        forrInputParameter1.setValue("a,b,c");
        forrInputParameter1.setSeparator(",");

        List<ForrInputParameter> forrInputParameters =
                Arrays.asList(forrInputParameter1);

        List<List<ForrInputParameter>> allParameters = new ArrayList<>();
        for (ForrInputParameter forrInputParameter : forrInputParameters) {
            List<ForrInputParameter> singleParameters = new ArrayList<>();
            String value = forrInputParameter.getValue();
            String separator = forrInputParameter.getSeparator();
            List<String> valueList =
                    Arrays.stream(StringUtils.split(value, separator)).map(String::trim).collect(Collectors.toList());

            for (String valueItem : valueList) {
                ForrInputParameter singleParameter = new ForrInputParameter();
                singleParameter.setName(forrInputParameter.getName());
                singleParameter.setValue(valueItem);
                singleParameters.add(singleParameter);
            }
            allParameters.add(singleParameters);
        }
        List<List<ForrInputParameter>> cartesianProduct = Lists.cartesianProduct(allParameters);
        System.out.println(cartesianProduct);
    }

    @Test
    void testResetProcessInstanceStatus_RepeatRunning() {
        processInstance.setCommandType(CommandType.REPEAT_RUNNING);
        ProcessInstance subProcessInstance = new ProcessInstance();
        List<ProcessInstance> subProcessInstances = Arrays.asList(subProcessInstance);

        forrLogicTask.resetProcessInstanceStatus(subProcessInstances);

        Mockito.verify(processInstanceDao).updateById(subProcessInstance);
        Assertions.assertEquals(WorkflowExecutionStatus.WAIT_TO_RUN, subProcessInstance.getState());
    }

    @Test
    void testResetProcessInstanceStatus_StartFailureTaskProcess() {
        processInstance.setCommandType(CommandType.START_FAILURE_TASK_PROCESS);
        ProcessInstance failedSubProcessInstance = new ProcessInstance();
        failedSubProcessInstance.setState(WorkflowExecutionStatus.FAILURE);
        List<ProcessInstance> subProcessInstances = Arrays.asList(failedSubProcessInstance);
        Mockito.when(subWorkflowService.filterFailedProcessInstances(subProcessInstances))
                .thenReturn(Arrays.asList(failedSubProcessInstance));

        forrLogicTask.resetProcessInstanceStatus(subProcessInstances);

        Mockito.verify(processInstanceDao).updateById(failedSubProcessInstance);
        Assertions.assertEquals(WorkflowExecutionStatus.WAIT_TO_RUN, failedSubProcessInstance.getState());
    }

}
