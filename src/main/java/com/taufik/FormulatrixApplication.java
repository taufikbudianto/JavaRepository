package com.taufik;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FormulatrixApplication {

	public static void main(String[] args) {
		initialisasi();
		SpringApplication.run(FormulatrixApplication.class, args);
	}
	
	public static void initialisasi() {
		File file = new File("C:\\Repository");
		if(!file.exists()) {
			if(file.mkdir()) {
				System.out.println("Sukses Membuat Folder RepoSitory");
			}
		}else {
			System.out.println("Folder RepoSitory is Ready");
		}
	}
}
