package com.chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	/** Задаем дефолтное расположение файлов, для тестов */
	public static String PATH = "test-folderTorT";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Input name of directory");
		} else {
			PATH = args[0];
			SpringApplication.run(DemoApplication.class, args);
		}
	}

}
