package ClinSys.Os;

import org.springframework.boot.SpringApplication;

public class TestOsApplication {

	public static void main(String[] args) {
		SpringApplication.from(OsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
