package com.solange.monitor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircularQueueTest {

	private CircularQueue q;

	@BeforeEach
	void setUp() throws Exception {
		q = new CircularQueue(10);
	}

	@Test
	void testEmptyQueueStatistics() {
		
		assertEquals(0.0, q.getStatistics().getMax());
		assertEquals(0.0, q.getStatistics().getMin());
		assertEquals(0.0, q.getStatistics().getAvg());
		assertEquals(0L, q.getStatistics().getCount());
	}
	
	
	@Test
	void testOneValueQueueStatistics() {

		q.setValue(5.0, 5);
		
		assertEquals(5.0, q.getStatistics().getMax());
		assertEquals(5.0, q.getStatistics().getMin());
		assertEquals(5.0, q.getStatistics().getAvg());
		assertEquals(1L, q.getStatistics().getCount());
	}
	
	@Test
	void testValuesQueueStatistics() {

		q.setValue(5.0, 5);
		q.setValue(10.0, 6);
		q.setValue(20.0, 9);
		
		assertEquals(20.0, q.getStatistics().getMax());
		assertEquals(5.0, q.getStatistics().getMin());
		assertEquals(11.67, Math.round(q.getStatistics().getAvg()), 2);
		assertEquals(3L, q.getStatistics().getCount());
		
		q.cleanValue(5);
		
		assertEquals(20.0, q.getStatistics().getMax());
		assertEquals(10.0, q.getStatistics().getMin());
		assertEquals(15, q.getStatistics().getAvg());
		assertEquals(2L, q.getStatistics().getCount());
	}

}
