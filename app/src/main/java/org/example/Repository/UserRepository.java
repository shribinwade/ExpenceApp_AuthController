package org.example.Repository;

import org.example.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {

   Optional<UserInfo> findByUsername(String username);
}
