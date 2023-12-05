package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findByEmailContainsIgnoreCase(String email, Pageable pageable);

    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);

    Page<User> findByLastNameContainsIgnoreCase(String lastName, Pageable pageable);

    Page<User> findByIsBlocked(boolean isBlocked, Pageable pageable);

    Page<User> findByRole(String role, Pageable pageable);

    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndIsBlockedAndRole(String firstName, String lastName, boolean isBlocked,
                                                                                                 String role, Pageable pageable);

    Page<User> findByLastNameContainsIgnoreCaseAndIsBlockedAndRole(String lastName, boolean isBlocked, String role, Pageable pageable);

    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndIsBlocked(String firstName, String lastName, boolean isBlocked, Pageable pageable);

    Page<User> findByLastNameContainsIgnoreCaseAndIsBlocked(String lastName, boolean isBlocked, Pageable pageable);

    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndRole(String firstName, String lastName, String role, Pageable pageable);

    Page<User> findByLastNameContainsIgnoreCaseAndRole(String lastName, String role, Pageable pageable);

    Page<User> findByIsBlockedAndRole(boolean isBlocked, String role, Pageable pageable);
}