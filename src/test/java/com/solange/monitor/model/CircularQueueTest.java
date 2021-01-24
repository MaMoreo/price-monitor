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
		
		assertEquals(0.0, q.getMax());
		assertEquals(0.0, q.getMin());
		assertEquals(0.0, q.getAvg());
		assertEquals(0L, q.getCount());
	}
	
	
	// ADD Swagger
	// ADD Synchronized methods
	
	@Test
	void testOneValueQueueStatistics() {

		q.setValue(5L, 5);
		
		assertEquals(5.0, q.getMax());
		assertEquals(5.0, q.getMin());
		assertEquals(5.0, q.getAvg());
		assertEquals(1L, q.getCount());
	}
	
	@Test
	void testValuesQueueStatistics() {

		q.setValue(5L, 5);
		q.setValue(10L, 6);
		q.setValue(20L, 9);
		
		assertEquals(20.0, q.getMax());
		assertEquals(5.0, q.getMin());
		assertEquals(11.67, Math.round(q.getAvg()),2);
		assertEquals(3L, q.getCount());
		
		q.cleanValue(5);
		
		assertEquals(20.0, q.getMax());
		assertEquals(10.0, q.getMin());
		assertEquals(15, q.getAvg());
		assertEquals(2L, q.getCount());
	}

}
