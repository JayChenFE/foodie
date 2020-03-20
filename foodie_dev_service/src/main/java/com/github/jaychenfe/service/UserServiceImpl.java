package com.github.jaychenfe.service;

import com.github.jaychenfe.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return false;
    }
}
