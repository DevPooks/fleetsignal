package dev.devpooks.fleetsignal.integration;

import static org.assertj.core.api.Assertions.assertThat;

import dev.devpooks.fleetsignal.FleetSignalApplication;
import dev.devpooks.fleetsignal.entity.Driver;
import dev.devpooks.fleetsignal.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = FleetSignalApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class MySqlMigrationIntegrationTest {
	@Container
	static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4").withDatabaseName("fleetsignal")
			.withUsername("fleetsignal").withPassword("test-password");

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL::getUsername);
		registry.add("spring.datasource.password", MYSQL::getPassword);
	}

	@Autowired
	DriverRepository repository;

	@Test
	void flywaySchemaAcceptsAndReadsADriver() {
		Driver saved = repository.save(new Driver("drv-mysql", "MySQL Test Driver"));
		assertThat(repository.findById(saved.getId())).get().extracting(Driver::getExternalReference)
				.isEqualTo("drv-mysql");
	}
}
