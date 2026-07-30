package com.beautysalon;

import org.springframework.boot.SpringApplication;

public class TestBeautySalonApplication {

    public static void main(String[] args) {
        SpringApplication.from(BeautySalonApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
