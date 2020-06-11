package Main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
//This annotation tells that this class is the starting point of our application
//The argument passed are the packages which we would like Spring to scan for Spring components.
//Refer: https://smarterco.de/java-spring-boot-mvc-ontroller-not-called/
@SpringBootApplication(scanBasePackages = {"Entity", "Controller", "DBAccesser", "Helper", "Main"})
public class MainClass {
    public static void main(String[] args) {
        //To start running the application
        SpringApplication.run(MainClass.class, args);
    }
}
