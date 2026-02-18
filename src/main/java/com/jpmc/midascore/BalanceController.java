package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jpmc.midascore.repository.UserRepository;




@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Balance(user.getBalance()))
                .orElse(new Balance(0.0f));
    }
}
