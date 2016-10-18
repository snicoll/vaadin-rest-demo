package demo.github;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(GithubClient.class)
public class GithubClientTest {

	@Autowired
	private GithubClient githubClient;

	@Autowired
	private MockRestServiceServer mockServer;

	@Test
	public void getRecentCommits() {
		expectJson("https://api.github.com/repos/vaadin/spring/commits",
				"github/vaadin-spring-commits.json");
		List<Commit> recentCommits = this.githubClient.getRecentCommits(
				"vaadin", "spring");
		this.mockServer.verify();
		assertThat(recentCommits).hasSize(5);
		assertCommit(recentCommits.get(0), "957a709",
				"Make Spring Boot tests more robust",
				"2016-10-14T11:56:09Z",
				"elmot", "Ilia Motornyi", "https://avatars.githubusercontent.com/u/5366945?v=3");
	}

	@Test
	public void getRecentCommitsNoCommit() {
		expectJson("https://api.github.com/repos/vaadin/spring2/commits",
				"github/no-commit.json");
		List<Commit> latestCommit = this.githubClient.getRecentCommits("vaadin", "spring2");
		assertThat(latestCommit).hasSize(0);
	}

	private void expectJson(String url, String bodyPath) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.mockServer.expect(requestTo(url))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
				.andRespond(withStatus(HttpStatus.OK)
						.body(new ClassPathResource(bodyPath))
						.headers(httpHeaders));
	}

	private void assertCommit(Commit commit, String sha, String message, String date,
			String committerId, String committerName, String committerAvatar) {
		assertThat(commit).isNotNull();
		assertThat(commit.getSha()).isEqualTo(sha);
		assertThat(commit.getMessage()).isEqualTo(message);
		assertThat(commit.getDate().toString()).isEqualTo(date);
		Commit.Committer committer = commit.getCommitter();
		assertThat(committer).isNotNull();
		assertThat(committer.getId()).isEqualTo(committerId);
		assertThat(committer.getName()).isEqualTo(committerName);
		assertThat(committer.getAvatarUrl()).isEqualTo(committerAvatar);
	}

}
