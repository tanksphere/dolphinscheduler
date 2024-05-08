package org.apache.dolphinscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.api.dto.role.RoleResponse;
import org.apache.dolphinscheduler.api.dto.role.UserRoleResponse;
import org.apache.dolphinscheduler.api.dto.user.UserResponse;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.UserRoleService;
import org.apache.dolphinscheduler.dao.entity.Role;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.entity.UserRole;
import org.apache.dolphinscheduler.dao.entity.UserRoleLog;
import org.apache.dolphinscheduler.dao.mapper.RoleMapper;
import org.apache.dolphinscheduler.dao.mapper.UserMapper;
import org.apache.dolphinscheduler.dao.mapper.UserRoleLogMapper;
import org.apache.dolphinscheduler.dao.mapper.UserRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 用户角色服务实现类
 * @Author tanksphere
 * @Date 2024/5/7 18:02
 * @Verson 1.0
 **/
@Service
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleLogMapper userRoleLogMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    public UserRoleResponse queryUserRoleList(Long userId, User loginUser) {
        UserRoleResponse response = new UserRoleResponse();
        User user = userMapper.selectById(userId);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        response.setUserResponse(userResponse);

        List<UserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId).eq("tenant_id", loginUser.getTenantId()));

        List<Role> roleList = roleMapper.selectBatchIds(userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList()));

        List<RoleResponse> roleResponseList = new ArrayList<>();
        roleList.stream().forEach(role -> {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());
            roleResponse.setDescription(role.getDescription());
            roleResponseList.add(roleResponse);
        });
        response.setRoleResponse(roleResponseList);
        return response;
    }

    @Transactional
    public UserRoleResponse userRoleSave(Long userId, List<Long> roleIdList, User loginUser) {
        checkAdminPermissions(loginUser);
        checkSameTenant(userMapper.selectById(userId), loginUser);
        checkRoleTenant(roleIdList, loginUser.getTenantId());
        // 1. saveLog
        List<UserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId).eq("tenant_id", loginUser.getTenantId()));
        if (!userRoleList.isEmpty()) {
            userRoleList.stream().forEach(userRole -> {
                UserRoleLog log = new UserRoleLog();
                BeanUtils.copyProperties(userRole, log, "id");
                log.setUserRoleId(userRole.getId());
                userRoleLogMapper.insertSelective(log);
            });
        }

        // 2. delete
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId).eq("tenant_id", loginUser.getTenantId()));

        // 3. save
        roleIdList.stream().forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userRole.setTenantId(loginUser.getTenantId());
            userRole.setCreateUser(loginUser.getId().toString());
            userRole.setUpdateUser(loginUser.getId().toString());
            userRoleMapper.insertSelective(userRole);
        });

        // 4. query user role
        return queryUserRoleList(userId, loginUser);
    }

    private void checkRoleTenant(List<Long> roleIdList, int tenantId) {
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>().in("id", roleIdList).eq("tenant_id", tenantId));
        if (roleList.size() != roleIdList.size()) {
            throw new ServiceException(Status.ROLE_INVALID_ERROR);
        }
    }

}
