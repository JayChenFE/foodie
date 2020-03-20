package com.github.jaychenfe.service;

import com.github.jaychenfe.mapper.UsersMapper;
import com.github.jaychenfe.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author jaychenfe
 */
@Service
public class UserServiceImpl implements UserService {

    private UsersMapper usersMapper;

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(userExample);
        return result != null;
    }
}
