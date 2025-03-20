package org.openmrs.module.consultationworkflow;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Unit test for simple App.
 */
@Testcontainers
public class WorkflowConfigResourceIT {

	private static final int OPENMRS_PORT = 8080;

	private static final WaitStrategy waitStrategy = Wait.forHttp("/openmrs")
			.forResponsePredicate(p -> p.contains("Not logged in"))
			.withStartupTimeout(Duration.ofSeconds(120));

	@Container
	private static final ComposeContainer openmrs = new ComposeContainer(getComposeFile())
			.withExposedService("api", OPENMRS_PORT, waitStrategy);

	private static File getComposeFile() {
		URL resource = WorkflowConfigResourceIT.class.getClassLoader().getResource("docker-compose.yml");
		return new File(resource.getFile());
	}

	@Test
	public void getShouldReturnAListOfResults() {
		RestTemplate restTemplate = new RestTemplate();
		String host = openmrs.getServiceHost("api", OPENMRS_PORT);
		int port = openmrs.getServicePort("api", OPENMRS_PORT);
		String url = "http://" + host + ":" + port + "/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		RequestEntity<Void> request = RequestEntity.get(url).headers(getHttpHeaders()).build();
		ResponseEntity<String> resp = restTemplate.exchange(request, String.class);
		assertThat(resp.getStatusCode(), is(HttpStatus.OK));
		assertThat(resp.getBody(), jsonEquals("{\"results\":[]}"));
	}

	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("Admin", "Admin123");
		return headers;
	}
}
