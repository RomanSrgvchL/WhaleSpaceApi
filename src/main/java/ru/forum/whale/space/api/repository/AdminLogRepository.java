package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.AdminLog;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
