package capstone.demo.repository;

import capstone.demo.entity.LoginHistory;
import capstone.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);

    @Query("SELECT h FROM LoginHistory h WHERE h.user = ?1 AND h.loginSuccess = false AND h.loginTime > ?2")
    List<LoginHistory> findFailedLoginAttemptsSince(User user, LocalDateTime since);

    @Query("SELECT h FROM LoginHistory h WHERE h.loginSuccess = false AND h.loginTime > ?1")
    List<LoginHistory> findAllFailedLoginAttemptsSince(LocalDateTime since);
}
