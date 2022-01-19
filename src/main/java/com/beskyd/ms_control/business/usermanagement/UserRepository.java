package com.beskyd.ms_control.business.usermanagement;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<User, String>{
    
    Optional<User> findByRecoveryToken(String recoveryToken);
    
    Optional<User> findByToken(String token);
    
    List<User> findByCity(String city);
    
    @Transactional
    @Modifying
    @Query("update User u set u.token=:token, u.lastLogInTime=:lastLogInTime where u.userEmail=:userEmail")
    void updateUserTokenAndLogInDatetime(@Param("token") String token, @Param("lastLogInTime") Timestamp lastLogInTime, @Param("userEmail") String userEmail);
    
    @Transactional
    @Modifying
    @Query("update User u set u.recoveryToken = ?2 where u.userEmail = ?1")
    void setRecoveryToken(String email, String token);

    @Transactional
    @Modifying
    @Query("update User u set u.lastLogInTime=:lastLogInTime where u.userEmail=:userEmail")
    void updateUserLogInDatetime(@Param("lastLogInTime") Timestamp lastLogInTime, @Param("userEmail") String userEmail);
}
