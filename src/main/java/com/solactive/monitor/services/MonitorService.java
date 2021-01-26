package com.solactive.monitor.services;

import com.solactive.monitor.domain.Tick;
import com.solactive.monitor.model.CircularQueue;
import com.solactive.monitor.model.CircularQueue.Statistics;

public interface MonitorService {

	CircularQueue.Statistics getStatisticsForInstrument(String identifier);
	CircularQueue.Statistics getStatisticsForAllInstrument();
	Statistics addTickToInstrument(String identifier, Double price, int second);
	boolean acceptTick(Tick tick);
	void cleanValue(int counter);
}
