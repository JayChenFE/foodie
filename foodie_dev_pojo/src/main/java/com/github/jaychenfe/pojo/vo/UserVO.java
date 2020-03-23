package com.github.jaychenfe.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class UserVO {
    private String username;
    private String face;
    private Date birthday;
}
