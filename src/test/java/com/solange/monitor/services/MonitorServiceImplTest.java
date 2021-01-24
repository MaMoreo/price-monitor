package com.solange.monitor.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.solange.monitor.model.CircularQueue.Statistics;

class MonitorServiceImplTest {

	private MonitorService monitor;

	@BeforeEach
	void setUp() throws Exception {
		monitor = new MonitorServiceImpl();
	}

	@Test
	void testNonExistingInstrument() {
		Statistics statisticsForInstrument = monitor.getStatisticsForInstrument("NonExits");

		assertEquals(0.0, statisticsForInstrument.getMax());
		assertEquals(0.0, statisticsForInstrument.getMin());
		assertEquals(0.0, statisticsForInstrument.getAvg());
		assertEquals(0L, statisticsForInstrument.getCount());
	}

	@Test
	void testAddTickToInstrument() {
		
		String identifier = "IBM";
		Double price = 15.0;
		int second = 5;
		Statistics statisticsForInstrument = monitor.addTickToInstrument(identifier, price, second);

		assertEquals(15.0, statisticsForInstrument.getMax());
		assertEquals(15.0, statisticsForInstrument.getMin());
		assertEquals(15.0, statisticsForInstrument.getAvg());
		assertEquals(1L, statisticsForInstrument.getCount());
		
		price = 30.0;
		second = 8;
		statisticsForInstrument = monitor.addTickToInstrument(identifier, price, second);
		
		assertEquals(30.0, statisticsForInstrument.getMax());
		assertEquals(15.0, statisticsForInstrument.getMin());
		assertEquals(22.5, statisticsForInstrument.getAvg());
		assertEquals(2L, statisticsForInstrument.getCount());
	}
	
	@Test
	void testAllStatistics() {
		Statistics globalStatistics = monitor.getStatisticsForAllInstrument();

		assertEquals(0.0, globalStatistics.getMax());
		assertEquals(0.0, globalStatistics.getMin());
		assertEquals(0.0, globalStatistics.getAvg());
		assertEquals(0L, globalStatistics.getCount());
		
		String identifier = "IBM";
		Double price = 15.0;
		int second = 5;
		
		monitor.addTickToInstrument(identifier, price, second);
		
		globalStatistics = monitor.getStatisticsForAllInstrument();

		assertEquals(15.0, globalStatistics.getMax());
		assertEquals(15.0, globalStatistics.getMin());
		assertEquals(15.0, globalStatistics.getAvg());
		assertEquals(1L, globalStatistics.getCount());
		
		identifier = "Amazon";
		price = 30.0;
		second = 8;
		monitor.addTickToInstrument(identifier, price, second);
		globalStatistics = monitor.getStatisticsForAllInstrument();
		assertEquals(30.0, globalStatistics.getMax());
		assertEquals(15.0, globalStatistics.getMin());
		assertEquals(22.5, globalStatistics.getAvg());
		assertEquals(2L, globalStatistics.getCount());
		
		identifier = "Amazon";
		price = 34.0;
		second = 4;
		monitor.addTickToInstrument(identifier, price, second);
		globalStatistics = monitor.getStatisticsForAllInstrument();
		assertEquals(34.0, globalStatistics.getMax());
		assertEquals(15.0, globalStatistics.getMin());
		assertEquals(26.33, Math.round(globalStatistics.getAvg()), 2 );
		assertEquals(3L, globalStatistics.getCount());
		
	}
	
}
