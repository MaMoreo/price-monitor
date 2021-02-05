package com.solactive.monitor.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.solactive.monitor.domain.Tick;
import com.solactive.monitor.model.Statistics;

class MonitorSortedServiceImplTest {

	private MonitorSortedServiceImpl monitor;

	@BeforeEach
	void setUp() throws Exception {
		monitor = new MonitorSortedServiceImpl();
		monitor.setMaxSize(60);
	}

	@Test
	void testAcceptTick() {

		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp thirtySecondsAgo = new Timestamp(now.getTime() - 30 * 1000);
		Timestamp sixtySecondsAgo = new Timestamp(now.getTime() - 60 * 1000);
		Timestamp ninetySecondsAgo = new Timestamp(now.getTime() - 90 * 1000);

		assertTrue(monitor.acceptTick(Tick.builder().timestamp(now.getTime()).build()));
		assertTrue(monitor.acceptTick(Tick.builder().timestamp(thirtySecondsAgo.getTime()).build()));
		assertFalse(monitor.acceptTick(Tick.builder().timestamp(sixtySecondsAgo.getTime()).build()));
		assertFalse(monitor.acceptTick(Tick.builder().timestamp(ninetySecondsAgo.getTime()).build()));
	}
	
	@Test
	void testAddTics() {
		
		
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp thirtySecondsAgo = new Timestamp(now.getTime() - 30 * 1000);
		Timestamp sixtySecondsAgo = new Timestamp(now.getTime() - 60 * 1000);
		Timestamp ninetySecondsAgo = new Timestamp(now.getTime() - 90 * 1000);
		
		
		Tick tick = Tick.builder().timestamp(now.getTime()).instrument("BC").price(100.1).build();
		Tick tick30 = Tick.builder().timestamp(thirtySecondsAgo.getTime()).instrument("BC").price(100.1).build();
		Tick tick60 = Tick.builder().timestamp(sixtySecondsAgo.getTime()).instrument("BC").price(100.1).build();
		Tick tick90 = Tick.builder().timestamp(ninetySecondsAgo.getTime()).instrument("BC").price(100.1).build();
		
		monitor.addTickToInstrument(tick);
		monitor.addTickToInstrument(tick30);
		monitor.addTickToInstrument(tick60);
		monitor.addTickToInstrument(tick90);
		assertEquals( 4, monitor.getMonitor().size());
	}
	
	
	@Test
	void testGlobalStatistics() {
		
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp thirtySecondsAgo = new Timestamp(now.getTime() - 30 * 1000);
		Timestamp fortyFiveSecondsAgo = new Timestamp(now.getTime() - 45 * 1000);
		Timestamp fiftyFiveSecondsAgo = new Timestamp(now.getTime() - 55 * 1000);
		
		Tick tick = Tick.builder().timestamp(now.getTime()).instrument("BC").price(100.1).build();
		Tick tick30 = Tick.builder().timestamp(thirtySecondsAgo.getTime()).instrument("BC").price(130.1).build();
		Tick tick45 = Tick.builder().timestamp(fortyFiveSecondsAgo.getTime()).instrument("BurntCity").price(250.15).build();
		Tick tick55 = Tick.builder().timestamp(fiftyFiveSecondsAgo.getTime()).instrument("SmartFrog").price(10.01).build();
		
		monitor.addTickToInstrument(tick);
		monitor.addTickToInstrument(tick30);
		monitor.addTickToInstrument(tick45);
		monitor.addTickToInstrument(tick55);
		assertEquals( 4, monitor.getMonitor().size());
		
		Statistics global =  monitor.getStatisticsForAllInstrument();
		assertEquals(4, global.getCount());
		assertEquals(122.59, global.getAvg());
		assertEquals(250.15, global.getMax());
		assertEquals(10.01, global.getMin());
	}
	
	
	@Test
	void testGlobalStatisticsForInstrument() {
		
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		Timestamp thirtySecondsAgo = new Timestamp(now.getTime() - 30 * 1000);
		Timestamp fortyFiveSecondsAgo = new Timestamp(now.getTime() - 45 * 1000);
		Timestamp fiftyFiveSecondsAgo = new Timestamp(now.getTime() - 55 * 1000);
		
		Tick tick = Tick.builder().timestamp(now.getTime()).instrument("BC").price(100.1).build();
		Tick tick30 = Tick.builder().timestamp(thirtySecondsAgo.getTime()).instrument("BC").price(130.1).build();
		Tick tick45 = Tick.builder().timestamp(fortyFiveSecondsAgo.getTime()).instrument("BurntCity").price(250.15).build();
		Tick tick55 = Tick.builder().timestamp(fiftyFiveSecondsAgo.getTime()).instrument("SmartFrog").price(10.01).build();
		
		monitor.addTickToInstrument(tick);
		monitor.addTickToInstrument(tick30);
		monitor.addTickToInstrument(tick45);
		monitor.addTickToInstrument(tick55);
		assertEquals( 4, monitor.getMonitor().size());
		
		Statistics bcStats =  monitor.getStatisticsForInstrument("BC");
		assertEquals(2, bcStats.getCount());
		assertEquals(115.1, bcStats.getAvg());
		assertEquals(130.1, bcStats.getMax());
		assertEquals(100.1, bcStats.getMin());
		
		Statistics burntcityStats =  monitor.getStatisticsForInstrument("BurntCity");
		assertEquals(1, burntcityStats.getCount());
		assertEquals(250.15, burntcityStats.getAvg());
		assertEquals(250.15, burntcityStats.getMax());
		assertEquals(250.15, burntcityStats.getMin());
		
		Statistics smartfrogStats =  monitor.getStatisticsForInstrument("SmartFrog");
		assertEquals(1, smartfrogStats.getCount());
		assertEquals(10.01, smartfrogStats.getAvg());
		assertEquals(10.01, smartfrogStats.getMax());
		assertEquals(10.01, smartfrogStats.getMin());
	}
}
