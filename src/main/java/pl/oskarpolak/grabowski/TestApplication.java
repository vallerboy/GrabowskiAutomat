package pl.oskarpolak.grabowski;

import com.fleetboard.data.*;
import com.fleetboard.ws.basicservice.BasicService;
import com.fleetboard.ws.basicservice.BasicService_Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.ws.BindingProvider;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
}
