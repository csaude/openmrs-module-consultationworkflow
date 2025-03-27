package org.openmrs.module.consultationworkflow;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodePresent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

@Testcontainers
public class WorkflowConfigResourceIT {

	private static final int OPENMRS_PORT = 8080;

	private static final WaitStrategy waitStrategy = Wait.forHttp("/openmrs")
			.forResponsePredicate(p -> p.contains("Not logged in"))
			.withStartupTimeout(Duration.ofSeconds(120));

	@Container
	private static final ComposeContainer openmrs = new ComposeContainer(getComposeFile())
			.withExposedService("api", OPENMRS_PORT, waitStrategy)
			.withLogConsumer("api", outputFrame -> System.out.print(outputFrame.getUtf8String()));

	private static File getComposeFile() {
		URL resource = WorkflowConfigResourceIT.class.getClassLoader().getResource("docker-compose.yml");
		return new File(resource.getFile());
	}

	private RestTemplate restTemplate;
	private String host;
	private int port;

	@BeforeEach
	public void setUp() {
		restTemplate = new RestTemplate();
		host = openmrs.getServiceHost("api", OPENMRS_PORT);
		port = openmrs.getServicePort("api", OPENMRS_PORT);
	}

	@Test
	public void getShouldReturnAListOfResults() {
		String url = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		RequestEntity<Void> request = RequestEntity.get(url, host, port).headers(getHttpHeaders()).build();
		ResponseEntity<String> resp = restTemplate.exchange(request, String.class);
		assertThat(resp.getStatusCode(), is(HttpStatus.OK));
		assertThat(resp.getBody(), jsonNodePresent("results"));
	}

	@Test
	public void postShouldCreateNewResource() {
		// Step 1: Upload steps to /openmrs/ws/rest/v1/clobdata
		String clobdataUrl = "http://{host}:{port}/openmrs/ws/rest/v1/clobdata";
		String fileContent = "{\"steps\": [{\"uuid\":\"some-uuid\",\"type\":\"form\",\"formUuid\":\"some-form-uuid\"}]}";

		// Create the multipart body manually
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new ByteArrayResource(fileContent.getBytes()) {
			@Override
			public String getFilename() {
				return "blob"; // Set the filename
			}
		});

		HttpHeaders clobHeaders = getHttpHeaders();
		clobHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> clobRequest = new HttpEntity<>(body, clobHeaders);
		ResponseEntity<String> clobResponse = restTemplate.postForEntity(clobdataUrl, clobRequest, String.class, host,
				port);
		assertThat(clobResponse.getStatusCode(), is(HttpStatus.CREATED));

		// Extract the UUID of the uploaded file from the response
		String clobUuid = clobResponse.getBody();

		// Step 2: Create a workflowconfig using the uploaded steps UUID
		String url = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		String requestBody = getWorkflowConfigJson(clobUuid);
		RequestEntity<String> createRequest = RequestEntity.post(url, host, port).headers(getHttpHeaders())
				.body(requestBody);
		ResponseEntity<String> createResp = restTemplate.exchange(createRequest, String.class);
		assertThat(createResp.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(createResp.getBody(), jsonPartEquals("name", "Test Workflow"));

		// Extract UUID from the response
		String uuid = extractUuidFromResponse(createResp.getBody());

		String getUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		RequestEntity<Void> getRequest = RequestEntity.get(getUrl, host, port, uuid).headers(getHttpHeaders()).build();
		ResponseEntity<String> getResp = restTemplate.exchange(getRequest, String.class);
		assertThat(getResp.getBody(), jsonPartEquals("steps",
				"[{\"uuid\":\"some-uuid\",\"type\":\"form\",\"formUuid\":\"some-form-uuid\"}]"));
	}

	@Test
	public void getByUuidShouldReturnResource() {
		// Create a resource first
		String createUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		String createRequestBody = getWorkflowConfigJson(null);
		RequestEntity<String> createRequest = RequestEntity.post(createUrl, host, port).headers(getHttpHeaders())
				.body(createRequestBody);
		ResponseEntity<String> createResp = restTemplate.exchange(createRequest, String.class);
		assertThat(createResp.getStatusCode(), is(HttpStatus.CREATED));

		// Extract UUID from the response
		String uuid = extractUuidFromResponse(createResp.getBody());

		// Fetch the resource by UUID
		String getUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		RequestEntity<Void> getRequest = RequestEntity.get(getUrl, host, port, uuid).headers(getHttpHeaders()).build();
		ResponseEntity<String> getResp = restTemplate.exchange(getRequest, String.class);
		assertThat(getResp.getStatusCode(), is(HttpStatus.OK));
		assertThat(getResp.getBody(), jsonPartEquals("uuid", uuid));
	}

	@Test
	public void postByUuidShouldEditResource() {
		// Create a resource first
		String createUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		String createRequestBody = getWorkflowConfigJson(null);
		RequestEntity<String> createRequest = RequestEntity.post(createUrl, host, port).headers(getHttpHeaders())
				.body(createRequestBody);
		ResponseEntity<String> createResp = restTemplate.exchange(createRequest, String.class);
		assertThat(createResp.getStatusCode(), is(HttpStatus.CREATED));

		// Extract UUID from the response
		String uuid = extractUuidFromResponse(createResp.getBody());

		// Edit the resource by UUID
		String editUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		String editRequestBody = "{"
				+ "\"description\": \"Updated Description\","
				+ "\"criteria\": []"
				+ "}";
		RequestEntity<String> editRequest = RequestEntity.post(editUrl, host, port, uuid).headers(getHttpHeaders())
				.body(editRequestBody);
		ResponseEntity<String> editResp = restTemplate.exchange(editRequest, String.class);
		assertThat(editResp.getStatusCode(), is(HttpStatus.OK));

		// Fetch the resource by UUID
		String getUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		RequestEntity<Void> getRequest = RequestEntity.get(getUrl, host, port, uuid).headers(getHttpHeaders()).build();
		ResponseEntity<String> getResp = restTemplate.exchange(getRequest, String.class);
		assertThat(getResp.getStatusCode(), is(HttpStatus.OK));
		assertThat(getResp.getBody(), jsonPartEquals("description", "Updated Description"));
		assertThat(getResp.getBody(), jsonPartEquals("criteria", "[]"));
	}

	@Test
	@Disabled
	public void deleteByUuidShouldRemoveResource() {
		// Create a resource first
		String createUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig";
		String createRequestBody = getWorkflowConfigJson(null);
		RequestEntity<String> createRequest = RequestEntity.post(createUrl, host, port).headers(getHttpHeaders())
				.body(createRequestBody);
		ResponseEntity<String> createResp = restTemplate.exchange(createRequest, String.class);
		assertThat(createResp.getStatusCode(), is(HttpStatus.CREATED));

		// Extract UUID from the response
		String uuid = extractUuidFromResponse(createResp.getBody());

		// Delete the resource by UUID
		String deleteUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		RequestEntity<Void> deleteRequest = RequestEntity.delete(deleteUrl, host, port, uuid).headers(getHttpHeaders())
				.build();
		ResponseEntity<Void> deleteResp = restTemplate.exchange(deleteRequest, Void.class);
		assertThat(deleteResp.getStatusCode(), is(HttpStatus.NO_CONTENT));

		// Verify the resource no longer exists
		String getUrl = "http://{host}:{port}/openmrs/ws/rest/v1/consultationworkflow/workflowconfig/{uuid}";
		RequestEntity<Void> getRequest = RequestEntity.get(getUrl, host, port, uuid).headers(getHttpHeaders()).build();
		ResponseEntity<String> getResp = restTemplate.exchange(getRequest, String.class);
		assertThat(getResp.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	private String getWorkflowConfigJson(String resourceValueReference) {
		return "{"
				+ "\"name\": \"Test Workflow\","
				+ "\"description\": \"Test Description\","
				+ "\"published\": true,"
				+ "\"version\": \"1.0\","
				+ "\"resourceValueReference\":"
				+ (resourceValueReference != null ? "\"" + resourceValueReference + "\"" : "null") + ","
				+ "\"criteria\": ["
				+ "    {"
				+ "        \"condition\": \"age > 13\","
				+ "        \"criteriaType\": \"PATIENT_DEMOGRAPHICS\""
				+ "    }"
				+ "]"
				+ "}";
	}

	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth("Admin", "Admin123");
		return headers;
	}

	private String extractUuidFromResponse(String responseBody) {
		return JsonPath.parse(responseBody).read("$.uuid");
	}
}
