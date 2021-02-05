package com.solactive.monitor.services;

import com.solactive.monitor.domain.Tick;
import com.solactive.monitor.model.Statistics;

public interface MonitorService {

	Statistics getStatisticsForInstrument(String identifier);
	
	/**
	 * Requirement: Should be O(1)
	 * @return
	 */
	Statistics getStatisticsForAllInstrument();
	
	Statistics addTickToInstrument(Tick tick);
	boolean acceptTick(Tick tick);
	void cleanValue(int counter);
}
