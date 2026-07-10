package com.edu.elearning.repository;

import com.edu.elearning.entity.User;
import com.edu.elearning.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUserDetails(UserDetails userDetails) ;

    Optional<User> findByUsername(String userName);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByResetToken(String token);

    @Query("SELECT u FROM User u WHERE u.userDetails.id = :id")
    Optional<User> findByUserDetailsId(Long id);
}
