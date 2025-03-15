package com.github.aico.repository.notifications;

import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findAllByTeamUserInAndIsReadFalse(List<TeamUser> teamUsers);

    @Modifying
    @Query("UPDATE Notifications n SET n.isRead = true WHERE n.notificationsId IN :ids AND n.teamUser IN (SELECT tu FROM TeamUser tu WHERE tu.user = :user)")
    int markAsReadByIdsAndUser(@Param("ids") List<Long> ids, @Param("user") User user);
}