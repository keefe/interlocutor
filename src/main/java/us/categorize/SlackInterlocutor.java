package us.categorize;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.view.Views.view;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.WebEndpoint;
import com.slack.api.bolt.handler.WebEndpointHandler;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.bolt.servlet.SlackAppServlet;
import com.slack.api.bolt.servlet.SlackOAuthAppServlet;
import com.slack.api.bolt.servlet.WebEndpointServlet;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.MessageEvent;

import us.categorize.advice.Advisor;
import us.categorize.advice.SentimentAdvice;
import us.categorize.advice.aws.comprehend.ComprehendAdvisor;
import us.categorize.conversation.slack.SlackMessage;
import us.categorize.conversation.slack.SlackMessageEvent;
import us.categorize.model.Conversation;
import us.categorize.model.Message;
import us.categorize.model.simple.SimpleConversation;
import us.categorize.model.simple.SimpleCriteria;

//TODO feels like an interface Interlocutor wants to come out here, that listens and responds
public class SlackInterlocutor {
	
	private Logger logger = LoggerFactory.getLogger(SlackInterlocutor.class);
	
	public static void main(String[] args) throws Exception {
		SlackInterlocutor interlocutor = new SlackInterlocutor();
		interlocutor.configureSlack();
	}

	private final Map<String, SimpleConversation> id2Channel = new HashMap<>();
	
	private Advisor advisor = new ComprehendAdvisor();

	public void configureSlack() throws Exception {
		var app = new App();

		app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
			var appHomeView = view(view -> view.type("home").blocks(asBlocks(section(section -> section
					.text(markdownText(mt -> mt.text("*this is where app configuration will go :tada:")))))));

			var res = ctx.client().viewsPublish(r -> r.userId(payload.getEvent().getUser()).view(appHomeView));

			return ctx.ack();
		});

		app.command("/echo", (req, ctx) -> {
			String commandArgText = req.getPayload().getText();
			String channelId = req.getPayload().getChannelId();
			String channelName = req.getPayload().getChannelName();
			return ctx.ack(commandArgText); // respond with 200 OK
		});

		app.event(MessageEvent.class, (payload, ctx) -> {
			// note, when debugging may see repeated posts as it retries
			System.out.println(payload.getEvent().getText());
			listen(ctx.getChannelId(), new SlackMessageEvent(payload.getEvent()));
			return ctx.ack();
		});

		app.command("/advise", (req, ctx) -> {
			List<com.slack.api.model.Message> messages = fetchHistory(ctx.getChannelId());
			Collections.reverse(messages);
			for(com.slack.api.model.Message slackMessage : messages) {
		    	SlackMessage newMessage = new SlackMessage(slackMessage);
		    	listen(ctx.getChannelId(), newMessage);
			}
			Conversation<SimpleCriteria> channel = id2Channel.get(ctx.getChannelId());
			//TODO restore live conversation monitoring
			
			Conversation conversation = channel.filter(new SimpleCriteria()).get(0);
			for(us.categorize.model.Message m : channel.content())
				logger.info(m.getText());
			SentimentAdvice sentiment = advisor.detectSentiment(conversation);
			return ctx.ack("General Sentiment " + sentiment.getSentiment());
		});

		var server = new SlackAppServer(app);
		logger.info("Listening to Slack Workspace");
		server.start();
	}

	private void listen(String channel, Message message) {
		if (!id2Channel.containsKey(channel)) {
			id2Channel.put(channel, new SimpleConversation(channel));
		}
		id2Channel.get(channel).listen(message);
	}
	
    /**
     * Fetch conversation history using ID from last example
     */
    private List<com.slack.api.model.Message> fetchHistory(String id) {
    	var logger = LoggerFactory.getLogger(PrototypeSentimentAdvisor.class);
    	Optional<List<com.slack.api.model.Message>> conversationHistory = Optional.empty();
        // you can get this instance via ctx.client() in a Bolt app
        var client = Slack.getInstance().methods();
        try {
            // Call the conversations.history method using the built-in WebClient
            var result = client.conversationsHistory(r -> r
                // The token you used to initialize your app
                .token(System.getenv("SLACK_BOT_TOKEN"))
                .channel(id)
            );
            conversationHistory = Optional.ofNullable(result.getMessages());
            // Print results
            logger.info("{} messages found in {}", conversationHistory.orElse(emptyList()).size(), id);
            //why isn't logger working?
            
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
        
        return conversationHistory.orElse(emptyList());
    }
}

