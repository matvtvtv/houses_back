package com.houses_back.houses_back.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.houses_back.houses_back.model.UserDailyStats;

@Repository
public interface UserDailyStatsRepository
        extends JpaRepository<UserDailyStats, Long> {

    Optional<UserDailyStats> findByUserLoginAndChatLoginAndDate(
            String userLogin,
            String chatLogin,
            LocalDate date
    );
   List<UserDailyStats> findAllByUserLoginAndChatLoginAndDateBetween( String userLogin, String chatLogin, LocalDate from, LocalDate to);
}
