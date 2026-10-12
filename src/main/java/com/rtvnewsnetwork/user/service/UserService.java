package com.rtvnewsnetwork.user.service;

import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.transaction.model.TransactionModel;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;
import com.rtvnewsnetwork.user.model.UserSearchDto;

import java.util.List;

public interface UserService {
    User getUserById(String id);
    User findByUsernameElseCreate(String phoneNumber);
    User updateUser(String id, UserDto userDto);
    User findByPhoneNumber(String phoneNumber);
    public boolean updateUserWallet(TransactionModel transactionModel);
     void addFcmToken(FcmModel fcmToken, User user);
     void removeToken(FcmModel fcmToken, User user);
     List<UserSearchDto> searchUsers(String name, User loggedInUser);
}
