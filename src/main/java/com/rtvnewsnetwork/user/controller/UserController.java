package com.rtvnewsnetwork.user.controller;

import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;
import com.rtvnewsnetwork.user.model.UserSearchDto;
import com.rtvnewsnetwork.user.model.UserWallet;
import com.rtvnewsnetwork.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController implements AuthDetailsHelper {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user")
    public User updateUser( @Valid @RequestBody UserDto user) {
        return userService.updateUser(getUserId(), user);
    }

    @GetMapping("/user")
    public User getUserByToken(){
        return getUser();
    }

    @PatchMapping("/user/fcmtoken")
    public void addOrRemoveFcmToken(
            @RequestBody FcmModel fcmModel,
            @RequestParam boolean add) {

        User user = getUser(); // assuming this is a helper method in your controller/service

        if (add) {
            userService.addFcmToken(fcmModel, user);
        } else {
            userService.removeToken(fcmModel, user);
        }
    }

    @GetMapping("/user/search/{query}")
    public List<UserSearchDto> searchUsers(@PathVariable String query) {
        return userService.searchUsers(query, getUser());
    }

    @GetMapping("/user/coin")
   public UserWallet getCoinByUser(){
       return getUser().getUserWallet();
   }


}
