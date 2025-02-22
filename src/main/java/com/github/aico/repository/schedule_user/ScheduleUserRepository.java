package com.github.aico.repository.schedule_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleUserRepository extends JpaRepository<ScheduleUser,Long> {

}
