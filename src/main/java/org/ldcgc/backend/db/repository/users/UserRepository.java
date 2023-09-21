package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
