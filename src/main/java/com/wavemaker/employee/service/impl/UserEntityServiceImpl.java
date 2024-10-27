package com.wavemaker.employee.service.impl;


import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.UserEntityRepository;
import com.wavemaker.employee.repository.impl.UserEntityRepositoryImpl;
import com.wavemaker.employee.service.UserEntityService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    private UserEntityRepository userEntityRepository;

    public UserEntityServiceImpl() {
        userEntityRepository = new UserEntityRepositoryImpl();
    }

    @Override
    public UserEntity authenticateUser(UserEntity userEntity) throws ServerUnavailableException {
        return userEntityRepository.authenticateUser(userEntity);
    }

    @Override
    public UserEntity getUserEntityById(int userId) throws ServerUnavailableException {
        return userEntityRepository.getUserEntityById(userId);
    }

    @Override
    public UserEntity addUserEntity(UserEntity userEntity) throws ServerUnavailableException {
        return userEntityRepository.addUserEntity(userEntity);
    }


}
