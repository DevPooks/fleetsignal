package dev.devpooks.fleetsignal.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> items, int page, int pageSize, long totalItems, int totalPages) {
	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages());
	}
}
