package dev.devpooks.fleetsignal.service;

import static org.assertj.core.api.Assertions.assertThat;

import dev.devpooks.fleetsignal.dto.PageResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PageResponseTest {
	@Test
	void mapsSpringPageMetadataWithoutChangingTheItems() {
		var source = new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2), 5);

		PageResponse<String> result = PageResponse.from(source);

		assertThat(result.items()).containsExactly("a", "b");
		assertThat(result.page()).isEqualTo(1);
		assertThat(result.pageSize()).isEqualTo(2);
		assertThat(result.totalItems()).isEqualTo(5);
		assertThat(result.totalPages()).isEqualTo(3);
	}
}
