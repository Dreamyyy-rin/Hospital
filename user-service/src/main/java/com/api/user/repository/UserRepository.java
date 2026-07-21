package com.api.user.repository;

import com.api.user.model.UserModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
  Optional<UserModel> findByEmail(String email);
}
