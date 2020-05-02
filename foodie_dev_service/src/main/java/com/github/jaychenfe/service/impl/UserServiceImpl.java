package com.github.jaychenfe.service.impl;

import com.github.jaychenfe.enmus.Sex;
import com.github.jaychenfe.mapper.UsersMapper;
import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.DateUtil;
import com.github.jaychenfe.utils.Md5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author jaychenfe
 */
@Service
public class UserServiceImpl implements UserService {

    private final UsersMapper usersMapper;
    private final Sid sid;

    private static final String USER_FACE = "http://cdn.u2.huluxia.com/g3/M02/07/9A/wKgBOVppEEaAL9jpAAHkJnWvt-c68.jpeg";

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, Sid sid) {
        this.usersMapper = usersMapper;
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(userExample);
        return result != null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Users createUser(UserBO userBO) {
        Users users = new Users();
        BeanUtils.copyProperties(userBO, users);
        users.setId(sid.nextShort());
        users.setPassword(Md5Utils.getMd5Str(userBO.getPassword()));
        // 默认使用昵称=用户名
        users.setNickname(userBO.getUsername());
        users.setFace(USER_FACE);
        users.setSex(Sex.secret.type);
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);

        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        return usersMapper.selectOneByExample(userExample);
    }
}
