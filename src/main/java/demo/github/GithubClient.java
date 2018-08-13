package demo.github;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import demo.VaadinRestDemoProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubClient {

	private final RestTemplate restTemplate;

	public GithubClient(RestTemplateBuilder restTemplateBuilder,
			VaadinRestDemoProperties properties) {
		this.restTemplate = setupRestTemplate(restTemplateBuilder,
				properties.getGithub().getToken());
	}

	@Cacheable("github.commits")
	public List<Commit> getRecentCommits(String organization, String project) {
		String url = String.format(
				"https://api.github.com/repos/%s/%s/commits", organization, project);
		ResponseEntity<Commit[]> response = this.restTemplate.exchange(createRequestEntity(url), Commit[].class);
		return Arrays.asList(response.getBody());
	}

	private RequestEntity<?> createRequestEntity(String url) {
		try {
			return RequestEntity.get(new URI(url))
					.accept(MediaType.APPLICATION_JSON).build();
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException("Invalid URL " + url, ex);
		}
	}

	private RestTemplate setupRestTemplate(RestTemplateBuilder builder, String token) {
		if (StringUtils.hasText(token)) {
			String[] content = token.split(":");
			Assert.state(content.length == 2, "Invalid Github token");
			builder = builder.basicAuthorization(content[0], content[1]);
		}
		return builder.build();
	}

}
