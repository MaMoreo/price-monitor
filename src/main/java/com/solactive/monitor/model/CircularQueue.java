package com.solactive.monitor.model;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import lombok.Data;

/**
 * Simulates a Circular Queue
 */
@Data
public class CircularQueue {

	private Double[] circularQueueElements;
	private int maxSize; // Circular Queue maximum size
	private Statistics statistics = new Statistics();


	public CircularQueue(int maxSize) {
		super();
		this.maxSize = maxSize;
		this.circularQueueElements = new Double[this.maxSize];

		for (int i = 0; i < maxSize; i++) {
			this.circularQueueElements[i] = 0.0;
		}

		statistics = new Statistics(0.0, 0.0, 0.0, 0L);
	}

	private Double calculateMax() {

		Double max = Stream.of(circularQueueElements) //
				.mapToDouble(v -> v) //
				.max() //
				.orElse(0); //
		
		return max.doubleValue();
	}

	private Double calculateMin() {
		Double orElse = Stream.of(circularQueueElements) //
				.mapToDouble(v -> v) //
				.filter(l -> l != 0.0) //
				.min() //
				.orElse(0);
		
		return orElse.doubleValue();
	}

	private Double calculateAvg() {

		OptionalDouble optionalDouble = Stream.of(circularQueueElements) //
				.mapToDouble(v -> v) //
				.filter(l -> l != 0.0) //
				.average();

		return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0.0;
	}

	private void removeFromStatistics(Double value) {
		// calculate avg again
		Double max = statistics.getMax();
		Double min = statistics.getMin();
		Double avg = calculateAvg();
		// if value == max OR == min
		if (max == value.doubleValue()) {
			max = calculateMax();
		}
		if (min == value.doubleValue()) {
			min = calculateMin();
		}

		statistics.update(max, min, avg, statistics.getCount() - 1);
	}

	private void updateStatistics() {

		Double max = calculateMax();
		Double min = calculateMin();
		Double avg = calculateAvg();

		statistics.update(max, min, avg, statistics.getCount() + 1);
	}

	public synchronized Optional<Statistics> cleanValue(int position) {
		if (position < circularQueueElements.length) {
			// if
			if (circularQueueElements[position] != 0) {
				// update Statistics (Remove)
				Double value = circularQueueElements[position];
				circularQueueElements[position] = 0.0;
				removeFromStatistics(value);
				return Optional.of(this.statistics);
			} else {

				circularQueueElements[position] = 0.0;

			}
		}
		return Optional.empty();
	}

	public synchronized void setValue(Double value, int position) {
		if (position < circularQueueElements.length) {
			circularQueueElements[position] = value;
		}
		updateStatistics();
	}
}
