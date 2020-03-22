package com.github.jaychenfe.service.impl;

import com.github.jaychenfe.enmus.Sex;
import com.github.jaychenfe.mapper.UsersMapper;
import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.pojo.mapping.UsersMapping;
import com.github.jaychenfe.pojo.vo.UserVO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.Md5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Optional;

/**
 * @author jaychenfe
 */
@Service
public class UserServiceImpl implements UserService {

    private UsersMapper usersMapper;
    private UsersMapping usersMapping;
    private Sid sid;

    private static final String USER_FACE = "http://cdn.u2.huluxia.com/g3/M02/07/9A/wKgBOVppEEaAL9jpAAHkJnWvt-c68.jpeg";

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, UsersMapping usersMapping, Sid sid) {
        this.usersMapper = usersMapper;
        this.usersMapping = usersMapping;
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
    public UserVO createUser(UserBO userBO) {
        Users users = usersMapping.userBoToUser(userBO);
        users.setId(sid.nextShort());
        users.setPassword(Md5Utils.getMd5Str(userBO.getPassword()));
        users.setFace(USER_FACE);
        users.setSex(Sex.secret.type);

        usersMapper.insert(users);
        return usersMapping.userToUserVO(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Optional<UserVO> queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        Users users = usersMapper.selectOneByExample(userExample);
        UserVO userVO = usersMapping.userToUserVO(users);

        return Optional.of(userVO);
    }
}
