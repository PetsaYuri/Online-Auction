package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}