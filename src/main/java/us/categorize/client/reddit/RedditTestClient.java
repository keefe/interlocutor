package us.categorize.client.reddit;

import java.net.URI;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.http.HttpMethod;

public class RedditTestClient {
	//probably, eventually want to do a full user grant here
	//want to grab some data and then expand this, so put it in a
	//side package, probably need to rationalize what slack vs reddit vs discord is
	public static void main(String[] args) throws Exception {
		// Instantiate HttpClient.
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		httpClient.setFollowRedirects(false);
		System.out.println(System.getenv("REDDIT_ID"));
		System.out.println(System.getenv("REDDIT_SECRET"));

		httpClient.getAuthenticationStore().addAuthentication(new BasicAuthentication(URI.create("https://www.reddit.com/api/v1/access_token"), "reddit", System.getenv("REDDIT_ID"), System.getenv("REDDIT_SECRET")));
		// Start HttpClient.
		httpClient.start();
		ContentResponse response = httpClient.newRequest("https://www.reddit.com/api/v1/access_token")
			    .method(HttpMethod.POST)
			    .param("grant_type", "client_credentials")
			    
			    .agent("EmotionalJaguar13 - categorize.us interlocutor")
			    .send();
		System.out.println(response.getContentAsString());
		// Stop HttpClient.
		httpClient.stop();
	}

}
