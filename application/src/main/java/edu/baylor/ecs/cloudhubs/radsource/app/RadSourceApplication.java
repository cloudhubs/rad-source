package edu.baylor.ecs.cloudhubs.radsource.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"edu.baylor.ecs.cloudhubs.radsource"})
public class RadSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadSourceApplication.class, args);
    }

}
