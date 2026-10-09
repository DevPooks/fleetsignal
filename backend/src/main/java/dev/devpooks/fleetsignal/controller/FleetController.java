package dev.devpooks.fleetsignal.controller;

import dev.devpooks.fleetsignal.dto.FleetSummaryResponse;
import dev.devpooks.fleetsignal.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fleet")
public class FleetController {
	private final AnalyticsService analyticsService;

	public FleetController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	@GetMapping("/summary")
	public FleetSummaryResponse summary() {
		return analyticsService.fleetSummary();
	}
}
