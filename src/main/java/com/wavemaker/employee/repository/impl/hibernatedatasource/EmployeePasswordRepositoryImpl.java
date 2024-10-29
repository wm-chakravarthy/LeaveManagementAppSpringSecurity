package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import jakarta.transaction.Transactional;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeePasswordRepositoryImpl implements EmployeePasswordRepository {
    private static final String GET_USER_BY_EMAIL_ID = "FROM EmployeePassword WHERE email = :email";

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Transactional
    public EmployeePassword findByEmailId(String email) {
        System.out.printf("HibernateTemplate is " + hibernateTemplate);
        return hibernateTemplate.execute(session -> {
            Query<EmployeePassword> query = session.createQuery(GET_USER_BY_EMAIL_ID, EmployeePassword.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        });
    }
}
