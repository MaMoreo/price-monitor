package com.solange.monitor.controllers;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.solange.monitor.domain.Tick;
import com.solange.monitor.model.CircularQueue;
import com.solange.monitor.services.MonitorService;

import reactor.core.publisher.Mono;

@RestController
public class PriceController {

	// insert the service here
	private MonitorService monitorService;
	
	public PriceController(MonitorService monitorService) { // inject service
		this.monitorService = monitorService;
	}
	
	
	@GetMapping("/statistics")
	public Mono<CircularQueue.Statistics> getStatistics() {
		return Mono.just(monitorService.getStatisticsForAllInstrument());
	}
	
	@GetMapping("/statistics/{instrument_identifier}")
	public Mono<CircularQueue.Statistics> getStatisticsForInstrument(@PathVariable("instrument_identifier" ) String identifier) {  
		return Mono.just(monitorService.getStatisticsForInstrument(identifier));
	}
	
	@PostMapping("/ticks")
	public ResponseEntity<Mono<String>> create(@RequestBody Tick tick){
		
		if (!monitorService.acceptTick(tick)){
			return ResponseEntity
				      .status(HttpStatus.NO_CONTENT)
				      .body(Mono.just("No Content"));
		}

		Timestamp t = new Timestamp(tick.getTimestamp());
		monitorService.addTickToInstrument(tick.getInstrument(), tick.getPrice(), t.toLocalDateTime().getSecond()) ;
		return ResponseEntity
			      .status(HttpStatus.OK)
			      .body(Mono.just("OK"));
	}
}
