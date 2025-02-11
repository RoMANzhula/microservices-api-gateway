package org.romanzhula.user_service.repositories;

import org.romanzhula.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
