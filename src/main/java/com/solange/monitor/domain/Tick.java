package com.solange.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tick {

	private String instrument;
	private Double price;
	private Long timestamp;
}
