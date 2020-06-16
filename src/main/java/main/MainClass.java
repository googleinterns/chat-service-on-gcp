package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication(scanBasePackages = {"entity", "controller", "dbaccessor", "exceptionhandler", "helper", "main"})
public class MainClass {
    public static void main(String[] args) {
        /*
         * Starts running the application.
        */
        SpringApplication.run(MainClass.class, args);
    }
}
