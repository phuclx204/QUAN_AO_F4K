package org.example.quan_ao_f4k.service;

import org.example.quan_ao_f4k.model.authentication.User;

public interface AdminService {

    User login(String userName, String password);

    User getUserDetailById(Integer loginUserId);

    Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    Boolean updateName(Integer loginUserId, String loginUserName, String nickName);
}
