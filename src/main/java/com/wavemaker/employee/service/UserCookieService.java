package com.wavemaker.employee.service;


import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.UserEntity;

public interface UserCookieService {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavailableException;

    public UserEntity getUserEntityByCookieValue(String cookieValue) throws ServerUnavailableException;

    public boolean deleteUserCookie(String cookieValue) throws ServerUnavailableException;
}
