package com.houses_back.houses_back.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.houses_back.houses_back.model.UserDailyStats;

@Repository
public interface UserDailyStatsRepository extends JpaRepository<UserDailyStats, Long> {
    
    Optional<UserDailyStats> findByUserLoginAndChatLoginAndDate(String userLogin, String chatLogin, LocalDate date);
    
    List<UserDailyStats> findByUserLoginAndChatLoginAndDateBetweenOrderByDateDesc(
            String userLogin, String chatLogin, LocalDate from, LocalDate to);
    
    // Сумма за период
    @Query("SELECT COALESCE(SUM(u.earnedMoney), 0) FROM UserDailyStats u " +
           "WHERE u.userLogin = ?1 AND u.chatLogin = ?2 AND u.date BETWEEN ?3 AND ?4")
    int sumMoneyByPeriod(String userLogin, String chatLogin, LocalDate from, LocalDate to);
    
    @Query("SELECT COALESCE(SUM(u.completedTasksCount), 0) FROM UserDailyStats u " +
           "WHERE u.userLogin = ?1 AND u.chatLogin = ?2 AND u.date BETWEEN ?3 AND ?4")
    int sumTasksByPeriod(String userLogin, String chatLogin, LocalDate from, LocalDate to);
    
    // Данные для лидерборда за период
    @Query("SELECT u.userLogin, SUM(u.earnedMoney), SUM(u.completedTasksCount) " +
           "FROM UserDailyStats u WHERE u.chatLogin = ?1 AND u.date BETWEEN ?2 AND ?3 " +
           "GROUP BY u.userLogin ORDER BY SUM(u.earnedMoney) DESC")
    List<Object[]> getLeaderboardByMoney(String chatLogin, LocalDate from, LocalDate to);
    
    @Query("SELECT u.userLogin, SUM(u.earnedMoney), SUM(u.completedTasksCount) " +
           "FROM UserDailyStats u WHERE u.chatLogin = ?1 AND u.date BETWEEN ?2 AND ?3 " +
           "GROUP BY u.userLogin ORDER BY SUM(u.completedTasksCount) DESC")
    List<Object[]> getLeaderboardByTasks(String chatLogin, LocalDate from, LocalDate to);
}