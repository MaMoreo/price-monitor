package com.solange.monitor.services;

import com.solange.monitor.model.CircularQueue;
import com.solange.monitor.model.CircularQueue.Statistics;

public interface MonitorService {

	CircularQueue.Statistics getStatisticsForInstrument(String identifier);
	CircularQueue.Statistics getStatisticsForAllInstrument();
	Statistics addTickToInstrument(String identifier, Double price, int second);

}
