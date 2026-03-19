package org.sleepless_artery.user_service.repository;

import org.sleepless_artery.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.emailAddress LIKE :emailAddress")
    Optional<User> findByEmailAddress(@Param("emailAddress") String emailAddress);

    boolean existsByEmailAddress(String emailAddress);
}
