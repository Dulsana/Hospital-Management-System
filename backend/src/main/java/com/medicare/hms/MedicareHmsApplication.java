package com.medicare.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicareHmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicareHmsApplication.class, args);
        System.out.println("==================================================");
        System.out.println(" Medicare Hospital Management Backend is Running! ");
        System.out.println(" Server: http://localhost:8080             ");
        System.out.println("==================================================");
    }
}
