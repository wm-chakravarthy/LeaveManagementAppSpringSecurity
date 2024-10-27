package com.wavemaker.employee.repository;


import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserEntityRepository {
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavailableException;

    public UserEntity getUserEntityById(int userId) throws ServerUnavailableException;

    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavailableException;

}
