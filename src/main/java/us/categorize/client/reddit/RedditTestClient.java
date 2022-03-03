package us.categorize.client.reddit;

import java.net.URI;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
class AuthResult {
	private String access_token, token_type, expires_in, scope;

	public AuthResult() {

	}

	public String getAccess_token() {
		return access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public String getScope() {
		return scope;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Listing {
	private String kind;

	private ListingData data;

	public ListingData getData() {
		return data;
	}

	public String getKind() {
		return kind;
	}

	public void setData(ListingData data) {
		this.data = data;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ListingData {
	private String kind;
	private String after;

	private List<ListingItemWrapper> children;

	public String getAfter() {
		return after;
	}

	public List<ListingItemWrapper> getChildren() {
		return children;
	}

	public String getKind() {
		return kind;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public void setChildren(List<ListingItemWrapper> children) {
		this.children = children;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
@JsonIgnoreProperties(ignoreUnknown = true)
class ListingItemWrapper {
	private String kind;
	private ListingItem data;
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public ListingItem getData() {
		return data;
	}
	public void setData(ListingItem data) {
		this.data = data;
	}
}
@JsonIgnoreProperties(ignoreUnknown = true)
class ListingItem {
	private String selftext, url, permalink, title, name;

	public String getName() {
		return name;
	}

	public String getPermalink() {
		return permalink;
	}

	public String getSelftext() {
		return selftext;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public void setSelftext(String selftext) {
		this.selftext = selftext;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

public class RedditTestClient {
	// probably, eventually want to do a full user grant here
	// want to grab some data and then expand this, so put it in a
	// side package, probably need to rationalize what slack vs reddit vs discord is
	public static void main(String[] args) throws Exception {
		// Instantiate HttpClient.
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		httpClient.setFollowRedirects(false);

		httpClient.getAuthenticationStore()
				.addAuthentication(new BasicAuthentication(URI.create("https://www.reddit.com/api/v1/access_token"),
						"reddit", System.getenv("REDDIT_ID"), System.getenv("REDDIT_SECRET")));
		// Start HttpClient.
		httpClient.start();
		ContentResponse response = httpClient.newRequest("https://www.reddit.com/api/v1/access_token")
				.method(HttpMethod.POST).param("grant_type", "client_credentials")
				.agent("EmotionalJaguar13 - categorize.us interlocutor").send();

		ObjectMapper mapper = new ObjectMapper();
		System.out.println(response.getContentAsString());
		AuthResult result = mapper.readValue(response.getContentAsString(), AuthResult.class);

		ContentResponse posts = httpClient.newRequest("https://oauth.reddit.com/r/javascript/hot.json")
				.method(HttpMethod.GET).header("Authorization", "bearer " + result.getAccess_token())
				.agent("EmotionalJaguar13 - categorize.us interlocutor").send();
		System.out.println(posts.getStatus());
		System.out.println(posts.getContentAsString());
		Listing listing = mapper.readValue(posts.getContentAsString(), Listing.class);
		// Stop HttpClient.
		httpClient.stop();
	}

}