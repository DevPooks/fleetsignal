package dev.devpooks.fleetsignal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	OpenAPI fleetSignalOpenApi() {
		return new OpenAPI().info(new Info().title("FleetSignal API").version("v1")
				.description("Synthetic telematics ingestion and deterministic analytics."));
	}
}
