package com.houses_back.houses_back.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.houses_back.houses_back.repository.UserRepository;

@Service
public class CoinResetService {

    private final UserRepository userRepository;

    public CoinResetService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   //@Scheduled(cron = "0 */1 * * * *")
    @Scheduled(cron = "0 0 0 1 * *",zone = "Europe/Berlin")
    @Transactional
    public void resetCoinsMonthly() {
        userRepository.resetAllCoins();
        System.out.println("=== COINS RESET SUCCESSFULLY ===");
    }
}
