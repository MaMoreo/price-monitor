package com.solactive.monitor.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.solactive.monitor.domain.Tick;
import com.solactive.monitor.model.Statistics;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Service
@Data
@Slf4j
public class MonitorSortedServiceImpl implements MonitorService {
	
	
	@Value("${seconds.max.size:60}")
	private Integer maxSize;

	private Map<Timestamp, Tick> monitor = new TreeMap<>();
	
	private Statistics stats = new Statistics();


	@Override
	public Statistics getStatisticsForInstrument(String identifier) {
		
		DoubleSummaryStatistics summaryStatistics = monitor.entrySet()
				.stream()
				.filter(k -> k.getKey().before(Timestamp.valueOf(LocalDateTime.now())))
				.filter(k -> k.getKey()
						.after(new Timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime() - maxSize * 1000)))
				.filter(k -> k.getValue().getInstrument().equals(identifier))
				.mapToDouble( m -> { return m.getValue().getPrice();})
				.summaryStatistics();
		
		return  Statistics.builder()
		.avg(summaryStatistics.getAverage())
		.count(summaryStatistics.getCount())
		.max(summaryStatistics.getMax())
		.min(summaryStatistics.getMin())
		.build();
	}

	@Override
	public Statistics getStatisticsForAllInstrument() {
		return stats;
	}

	public Statistics addTickToInstrument(Tick tick ) {
		monitor.put(new Timestamp(tick.getTimestamp()), tick);
		return updateStatistics();
	}

	private synchronized Statistics updateStatistics() {
		
		System.out.println();
		
		DoubleSummaryStatistics summaryStatistics = monitor.entrySet()
		.stream()
		/*.filter(k -> {
			Timestamp now = Timestamp.valueOf(LocalDateTime.now());
			Timestamp sixtySecondsAgo = new Timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime() - maxSize * 1000);
			
			System.out.println("Compare: NOW:" + now + " > "+ k.getKey() + " > " + sixtySecondsAgo);
			
			return k.getKey().before(now) && k.getKey().after(sixtySecondsAgo);
		})*/
		.filter(k -> k.getKey().before(Timestamp.valueOf(LocalDateTime.now())))
		.filter(k -> k.getKey()
				.after(new Timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime() - maxSize * 1000)))
		.mapToDouble( m -> { return m.getValue().getPrice();})
		.summaryStatistics();
		
		return stats.withAvg(summaryStatistics.getAverage())
			.withCount(summaryStatistics.getCount())
			.withMax(summaryStatistics.getMax())
			.withMin(summaryStatistics.getMin());
	}

	@Override
	public boolean acceptTick(Tick tick) {

		Timestamp t = new Timestamp(tick.getTimestamp());
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp maxSecondsAgo = new Timestamp(now.getTime() - maxSize * 1000);

		return t.before(now) && t.after(maxSecondsAgo);
	}

	@Override
	public void cleanValue(int counter) {
		// TODO Auto-generated method stub

	}
}
