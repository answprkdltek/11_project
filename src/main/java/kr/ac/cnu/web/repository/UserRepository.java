package kr.ac.cnu.web.repository;

import kr.ac.cnu.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by rokim on 2018. 5. 25..
 */
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByOrderByAccountDesc();
}
