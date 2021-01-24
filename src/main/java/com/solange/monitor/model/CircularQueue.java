package com.solange.monitor.model;

import java.util.OptionalDouble;
import java.util.stream.Stream;

import lombok.Data;

/**
 * This is NOT a QUEUE
 * 
 */
@Data
public class CircularQueue {

	private Long[] circularQueueElements;
	private int maxSize; // Circular Queue maximum size
	// statistics
	private Double avg;
	private Double max;
	private Double min;
	private Long count;

	public CircularQueue(int maxSize) {
		super();
		this.maxSize = maxSize;
		this.circularQueueElements = new Long[this.maxSize];

		for (int i = 0; i < maxSize; i++) {
			this.circularQueueElements[i] = 0L;
		}

		max = 0.0;
		min = 0.0;
		count = 0L;
		avg = 0.0;

	}

	private Double calculateMax() {

		Long max = Stream.of(circularQueueElements).mapToLong(v -> v).max().orElse(0);
		return max.doubleValue();
	}

	private Double calculateMin() {
		Long orElse = Stream.of(circularQueueElements).mapToLong(v -> v).filter(l -> l != 0L).min().orElse(0);
		return orElse.doubleValue();
	}

	private Double calculateAvg() {

		OptionalDouble optionalDouble = Stream.of(circularQueueElements).mapToLong(v -> v).filter(l -> l != 0L)
				.average();

		return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0.0;
	}

	private void removeFromStatistics(Long value) {
		count--;
		// calculate avg again
		avg = calculateAvg();
		// if value == max OR == min
		if (max == value.doubleValue()) {
			max = calculateMax();
		}
		if (min == value.doubleValue()) {
			min = calculateMin();
		}
	}

	/**
	 * Sets values to -1
	 * 
	 * @param position
	 */
	public void cleanValue(int position) {
		if (position < circularQueueElements.length) {
			// if
			if (circularQueueElements[position] != 0) {
				// update Statistics (Remove)
				Long value = circularQueueElements[position];
				circularQueueElements[position] = 0L;
				removeFromStatistics(value);
			} else {

				circularQueueElements[position] = 0L;
			}
		}
	}

	/**
	 * Set position to this value
	 * 
	 * @param value
	 * @param position
	 */
	public void setValue(Long value, int position) {
		if (position < circularQueueElements.length) {
			circularQueueElements[position] = value;
		}
		updateStatistics();
	}

	private void updateStatistics() {
		count++;
		avg = calculateAvg();
		max = calculateMax();
		min = calculateMin();
	}
}
