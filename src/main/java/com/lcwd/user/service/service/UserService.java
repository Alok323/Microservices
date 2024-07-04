package com.lcwd.user.service.service;

import com.lcwd.user.service.entities.User;
import java.util.List;

public interface UserService {

    //user operations

    //create
    User saveUser(User user);

    //get all user
    List<User> getAllUser();

    //get user by userId

    User getUser(String userId);

    //TODO:Delete
    //TODO:Update

}
