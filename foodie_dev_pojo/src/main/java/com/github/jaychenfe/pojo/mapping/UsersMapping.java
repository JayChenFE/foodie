package com.github.jaychenfe.pojo.mapping;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.utils.DateUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

/**
 * @author jaychenfe
 */
@Mapper(componentModel = "spring",
        imports = {Date.class, DateUtil.class})
public interface UsersMapping {
    /**
     * userBO转换成Users
     *
     * @param userBO userBO
     * @return Users
     */
    @Mapping(target = "nickname", source = "username")
    @Mapping(target = "birthday", expression = "java(DateUtil.stringToDate(\"1900-01-01\"))")
    @Mapping(target = "createdTime", expression = "java(new Date())")
    @Mapping(target = "updatedTime", expression = "java(new Date())")
    Users userBoToUsers(UserBO userBO);

}
