package com.solange.monitor.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.solange.monitor.domain.Tick;
import com.solange.monitor.model.CircularQueue;
import com.solange.monitor.model.CircularQueue.Statistics;

import lombok.Data;

@Service
@Data
public class MonitorServiceImpl implements MonitorService {

	private static final Integer MAX_SIZE = 60;

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
	public Statistics addTickToInstrument(String identifier, Double price, int second) {
		if (!monitor.containsKey(identifier)) {
			monitor.put(identifier, new CircularQueue(MAX_SIZE));
		}

		monitor.get(identifier).setValue(price, second);
		Statistics statistics = monitor.get(identifier).getStatistics();
		updateGlobalStatistics(statistics);
		return statistics;
	}

	private void updateGlobalStatistics(Statistics localStatistics) {
		globalStatistics.setCount(globalStatistics.getCount() + 1);
		if (globalStatistics.getMax() < localStatistics.getMax()) {
			globalStatistics.setMax(localStatistics.getMax());
		}
		if (globalStatistics.getMin() == 0.0 || (globalStatistics.getMin() > localStatistics.getMin())) {
			globalStatistics.setMin(localStatistics.getMin());
		}

		Double globalAvg = 0.0;
		for (Map.Entry<String, CircularQueue> entry : monitor.entrySet()) {
			
			CircularQueue elements = entry.getValue();

			globalAvg +=  Stream.of(elements.getCircularQueueElements())
					.mapToDouble(v -> v)
					.filter(l -> l != 0.0)
					.sum();
			
		}

		globalAvg = globalAvg/ globalStatistics.getCount();
		globalStatistics.setAvg(globalAvg);
	}

	@Override
	public boolean acceptTick(Tick tick) {
		Timestamp t = new Timestamp(tick.getTimestamp());
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp sixtySecondsAgo = new Timestamp(now.getTime() - 60 * 1000);
		
		boolean result =  t.before(now) && t.after(sixtySecondsAgo);
		return result;
	}
}
