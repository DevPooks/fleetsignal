package dev.devpooks.fleetsignal.controller;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	private final JdbcTemplate jdbcTemplate;

	public HealthController(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@GetMapping("/health")
	public ResponseEntity<Map<String, String>> health() {
		try {
			jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			return ResponseEntity.ok(Map.of("status", "UP", "database", "UP"));
		} catch (RuntimeException exception) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(Map.of("status", "DOWN", "database", "DOWN"));
		}
	}
}
