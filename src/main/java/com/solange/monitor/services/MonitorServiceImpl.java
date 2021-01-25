package com.solange.monitor.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.solange.monitor.domain.Tick;
import com.solange.monitor.model.CircularQueue;
import com.solange.monitor.model.CircularQueue.Statistics;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class MonitorServiceImpl implements MonitorService {

	@Value("${seconds.max.size:60}")
	private static Integer MAX_SIZE;

	private CircularQueue.Statistics globalStatistics = new CircularQueue(0).getStatistics();

	// String is the identifier
	// CircularQueue is the Object with the statistics
	private Map<String, CircularQueue> monitor = new HashMap<>();

	private Tick block;

	@Override
	public CircularQueue.Statistics getStatisticsForInstrument(String identifier) {

		if (!monitor.containsKey(identifier)) {
			CircularQueue queue = new CircularQueue(0);
			return queue.getStatistics();
		}

		return monitor.get(identifier).getStatistics();
	}

	@Override
	public Statistics getStatisticsForAllInstrument() {
		return globalStatistics;
	}

	@Override
	public synchronized Statistics addTickToInstrument(String identifier, Double price, int second) {
		if (!monitor.containsKey(identifier)) {
			monitor.put(identifier, new CircularQueue(MAX_SIZE));
		}

		monitor.get(identifier).setValue(price, second);
		Statistics statistics = monitor.get(identifier).getStatistics();
		updateGlobalStatisticsAfterAdd(statistics);
		return statistics;
	}

	private void updateGlobalStatisticsAfterAdd(Statistics localStatistics) {
		globalStatistics.setCount(globalStatistics.getCount() + 1);
		updateGlobalStatistics(localStatistics);
	}

	private void updateGlobalStatistics(Statistics localStatistics) {
		if (globalStatistics.getMax() < localStatistics.getMax()) {
			globalStatistics.setMax(localStatistics.getMax());
		}
		if (globalStatistics.getMin() == 0.0 || (globalStatistics.getMin() > localStatistics.getMin())) {
			globalStatistics.setMin(localStatistics.getMin());
		}

		Double globalAvg = 0.0;
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {

			CircularQueue elements = entry.getValue();

			globalAvg += Stream.of(elements.getCircularQueueElements()).mapToDouble(v -> v).filter(l -> l != 0.0).sum();

		}

		globalAvg = globalAvg / globalStatistics.getCount();
		globalStatistics.setAvg(globalAvg);
	}

	private void updateGlobalStatisticsAfterClean() {
		globalStatistics.setCount(globalStatistics.getCount() - 1);

		Double globalMax = 0.0;
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {

			CircularQueue elements = entry.getValue();
			if (elements.getStatistics().getMax() > globalMax) {
				globalMax = elements.getStatistics().getMax();
			}
		}

		globalStatistics.setMax(globalMax);

		
		Double globalMin = Double.MAX_VALUE;
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {

			CircularQueue elements = entry.getValue();
			if (elements.getStatistics().getMin() < globalMin) {
				globalMin = elements.getStatistics().getMin();
			}
		}

		globalStatistics.setMin(globalMin);
		
		
		
		Double globalAvg = 0.0;
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {

			CircularQueue elements = entry.getValue();

			globalAvg += Stream.of(elements.getCircularQueueElements()).mapToDouble(v -> v).filter(l -> l != 0.0).sum();

		}

		globalAvg = globalAvg / globalStatistics.getCount();
		if (globalAvg.isNaN()) {
			globalAvg = 0.0;
		}
		globalStatistics.setAvg(globalAvg);
	}

	@Override
	public boolean acceptTick(Tick tick) {
		Timestamp t = new Timestamp(tick.getTimestamp());
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp sixtySecondsAgo = new Timestamp(now.getTime() - 60 * 1000);

		return t.before(now) && t.after(sixtySecondsAgo);
	}

	@Override
	public synchronized void cleanValue(int counter) {
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {

			CircularQueue elements = entry.getValue();
			Optional<Statistics> localStatistics = elements.cleanValue(counter);
			if (localStatistics.isPresent()) {
				log.debug("CLEANED QUEUE: " + entry.getKey() + " position: " + counter);
				updateGlobalStatisticsAfterClean();
			}
		}
	}
}
