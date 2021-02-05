package com.solactive.monitor.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Statistics {
	
	public Double avg;
	public Double max;
	public Double min;
	public Long count;

	public void update(Double max, Double min, Double avg, Long count) {
		this.count = count;
		this.avg = avg;
		this.max = max;
		this.min = min;
	}
	
	public Statistics withAvg(Double avg) {
		this.avg = avg;
		return this;
	}
	
	public Statistics withCount(Long count) {
		this.count = count;
		return this;
	}
	
	public Statistics withMax(Double max) {
		this.max = max;
		return this;
	}
	
	public Statistics withMin(Double min) {
		this.min = min;
		return this;
	}
}