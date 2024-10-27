package com.wavemaker.employee.service;


import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserEntityService {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavailableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavailableException;

    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavailableException;
}
