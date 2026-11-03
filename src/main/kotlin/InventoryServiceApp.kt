package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
object InventoryServiceApp {
    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(InventoryServiceApp::class.java, *args)
    }
}
