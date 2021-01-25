package com.solange.monitor.bootstrap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.solange.monitor.services.MonitorService;

@Component
public class Scheduler implements CommandLineRunner {

	
	private static final int MAX_SIZE = 60; // FIXME move to a property @Value
	
	@Autowired
	private MonitorService monitor;

	@Override
	public void run(String... args) throws Exception {

		Runnable slidingWindow = new Runnable() {

			//CircularQueue queue = new CircularQueue(MAX_SIZE);
			int counter = 0;  // FIXME: starts in second from NOW

			public void run() {
			//	System.out.println( monitor.getStatisticsForAllInstrument());
				monitor.cleanValue(counter);
				//queue.cleanValue(counter);
				counter = (counter + 1 ) % MAX_SIZE;
				//System.out.println(queue.toString());
			}
		};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(slidingWindow, 0, 1, TimeUnit.SECONDS);
	}
}
