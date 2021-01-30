package com.solactive.monitor.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.solactive.monitor.domain.Tick;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceControllerTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@BeforeEach
	void setUp() throws Exception {
		
	}
	
	
	
	@Ignore @Test
	void testGetStatistics() {
		
	}

	@Test
	@Ignore
	void testGetStatisticsForInstrument() {
		//fail("Not yet implemented");
	}

	@Test
	void testCreateTimeStampTooOld() {
		
		Tick repoRequest = new Tick().builder().instrument("IBM").price(0.0).timestamp(1611580501000L).build();  
		
		webTestClient.post().uri("/ticks")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .accept(MediaType.APPLICATION_JSON_UTF8)
          .body(Mono.just(repoRequest), Tick.class)
          .exchange()
          .expectStatus().isNoContent()
          .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
          .expectBody()
         // .jsonPath("$.instrument").isNotEmpty()
         // .jsonPath("$.instrument").isEqualTo("IBM")
          ;
	}
	
	@Test
	void testCreateTimeStampCorrect() {
		
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		
		Tick repoRequest = new Tick().builder().instrument("IBM").price(0.0).timestamp(now.getTime()).build();  
		
		webTestClient.post().uri("/ticks")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .accept(MediaType.APPLICATION_JSON_UTF8)
          .body(Mono.just(repoRequest), Tick.class)
          .exchange()
          .expectStatus().isOk()
          .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
          .expectBody()
         ;
	}

}
