package com.bingo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Main {
    static void main(String[] args) {
        assert args.size() != 0: "Should pass config path!"
        ApplicationContext context = SpringApplication.run(Main.class, args)
        BingoBot bingoBot = context.getBean(BingoBot.class)
        bingoBot.init(args[0])  // pass config path
    }
}
