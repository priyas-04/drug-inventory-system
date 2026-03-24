package com.drugInventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Drug Inventory and Supply Chain Management System.
 *
 * @SpringBootApplication enables:
 * - @Configuration: Marks this as a configuration class
 * - @EnableAutoConfiguration: Auto-configures Spring beans based on classpath
 * - @ComponentScan: Scans for Spring components in this package and sub-packages
 */
@SpringBootApplication
public class DrugInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugInventoryApplication.class, args);
        System.out.println("===========================================");
        System.out.println(" Drug Inventory System Started Successfully");
        System.out.println(" API running at: http://localhost:8080");
        System.out.println("===========================================");
    }
}
