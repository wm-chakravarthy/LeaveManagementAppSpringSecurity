package com.wavemaker.employee.repository;


import com.wavemaker.employee.exception.ServerUnavailableException;

public interface UserCookieRepository {
    public boolean addCookie(String cookieValue, int userId) throws ServerUnavailableException;

    public int getUserIdByCookieValue(String cookieValue) throws ServerUnavailableException;

    public boolean deleteUserCookie(String cookieValue) throws ServerUnavailableException;

}
