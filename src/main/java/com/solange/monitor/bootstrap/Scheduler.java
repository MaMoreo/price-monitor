package com.solange.monitor.bootstrap;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.solange.monitor.services.MonitorService;

@Component
public class Scheduler implements CommandLineRunner {

	@Value("${seconds.max.size:60}")
	private int maxSize;
	
	@Autowired
	private MonitorService monitor;

	@Override
	public void run(String... args) throws Exception {

		Runnable slidingWindow = new Runnable() {

			// Sixty Seconds Ago
			int second = (LocalDateTime.now().getSecond() + 1) % (maxSize ) ;  
			
			public void run() {
				monitor.cleanValue(second);
				second = (second + 1 ) % maxSize;
			}
		};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(slidingWindow, 0, 1, TimeUnit.SECONDS);
	}
}
