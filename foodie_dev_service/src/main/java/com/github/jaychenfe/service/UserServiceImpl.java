package com.github.jaychenfe.service;

import com.github.jaychenfe.enmus.Sex;
import com.github.jaychenfe.mapper.UsersMapper;
import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.utils.DateUtil;
import com.github.jaychenfe.utils.Md5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author jaychenfe
 */
@Service
public class UserServiceImpl implements UserService {

    private UsersMapper usersMapper;
    private Sid sid;

    private static final String USER_FACE = "http://cdn.u2.huluxia.com/g3/M02/07/9A/wKgBOVppEEaAL9jpAAHkJnWvt-c68.jpeg";

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, Sid sid) {
        this.usersMapper = usersMapper;
        this.sid = sid;
    }

    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(userExample);
        return result != null;
    }

    @Override
    public Users createUser(UserBO userBO) {
        Users users = new Users();
        users.setId(sid.nextShort());
        users.setUsername(userBO.getUsername());
        users.setPassword(Md5Utils.getMd5Str(userBO.getPassword()));
        users.setNickname(userBO.getUsername());
        users.setFace(USER_FACE);
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        users.setSex(Sex.secret.type);
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);
        return users;
    }
}
