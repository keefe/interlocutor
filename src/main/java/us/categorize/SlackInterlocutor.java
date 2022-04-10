package us.categorize;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.view.Views.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.MessageEvent;

import us.categorize.advice.Advisor;
import us.categorize.advice.SentimentAdvice;
import us.categorize.advice.aws.comprehend.ComprehendAdvisor;
import us.categorize.conversation.Interlocutor;
import us.categorize.conversation.slack.SlackMessage;
import us.categorize.conversation.slack.SlackMessageEvent;
import us.categorize.model.Conversation;
import us.categorize.model.Message;
import us.categorize.model.simple.SimpleConversation;
import us.categorize.model.simple.SimpleCriteria;

//TODO feels like an interface Interlocutor wants to come out here, that listens and responds
public class SlackInterlocutor implements Interlocutor{
	
	private Logger logger = LoggerFactory.getLogger(SlackInterlocutor.class);
	
	public static void main(String[] args) throws Exception {
		SlackInterlocutor interlocutor = new SlackInterlocutor();
		interlocutor.configureSlack();
	}

	private final Map<String, SimpleConversation> id2Channel = new HashMap<>();
	
	//TODO really don't like how I'm handling generics here
	private Advisor<SimpleCriteria> advisor = new ComprehendAdvisor<SimpleCriteria>();

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
			listen(new SlackMessageEvent(ctx.getChannelId(), payload.getEvent()));
			return ctx.ack();
		});

		app.command("/advise", (req, ctx) -> {
			if("historical".equals(req.getPayload().getText())) {
				String channelId = ctx.getChannelId();
				readChannelHistory(channelId);
			}
			Conversation<SimpleCriteria> channel = id2Channel.get(ctx.getChannelId());
			
			//TODO NPE here with empty convo
			Conversation<SimpleCriteria> conversation = channel.filter(new SimpleCriteria()).get(0);
			for(us.categorize.model.Message m : channel.content())
				logger.info(m.getId() + " | " + m.getText() + " | " + m.getRepliesToId());
			
			
			SentimentAdvice sentiment = advisor.detectSentiment(conversation);
			return ctx.ack("General Sentiment " + sentiment.getSentiment());
		});

		var server = new SlackAppServer(app);
		logger.info("Listening to Slack Workspace");
		server.start();
	}

	//TODO how to handle pagination parameters here?
	private void readChannelHistory(String channelId) {
		logger.info("Loading History in Channel");
		List<com.slack.api.model.Message> messages = fetchHistory(channelId);
		for(com.slack.api.model.Message slackMessage : messages) {
			SlackMessage newMessage = new SlackMessage(channelId, slackMessage);
			listen(newMessage);
		}
	}

	public void listen(Message message) {
		String channel = message.getChannel();
		if (!id2Channel.containsKey(channel)) {
			id2Channel.put(channel, new SimpleConversation(channel));
		}
		id2Channel.get(channel).listen(message);
	}
	
    /**
     * Fetch conversation history using ID from last example
     */
    private List<com.slack.api.model.Message> fetchHistory(String id) {
    	var logger = LoggerFactory.getLogger(SlackInterlocutor.class);
    	List<com.slack.api.model.Message> conversationHistory = new ArrayList<>();
        // you can get this instance via ctx.client() in a Bolt app
        var client = Slack.getInstance().methods();
        try {
            // Call the conversations.history method using the built-in WebClient
            var result = client.conversationsHistory(r -> r
                // The token you used to initialize your app
                .token(System.getenv("SLACK_BOT_TOKEN"))
                .channel(id)
            );

            if(result.getMessages()!=null) {
            	conversationHistory.addAll(result.getMessages());
            }

            for(com.slack.api.model.Message sm : result.getMessages()) {
            	if(sm.getThreadTs()!=null && sm.getThreadTs().equals(sm.getTs())) {
            		var repliesResult = client.conversationsReplies(r -> r
                            // The token you used to initialize your app
                            .token(System.getenv("SLACK_BOT_TOKEN"))
                            .ts(sm.getTs())
                            .channel(id));
            		conversationHistory.addAll(repliesResult.getMessages());
            	}
            }
            // Print results
            logger.info("{} messages found in {}", conversationHistory.size(), id);
            
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
        
        return conversationHistory;
    }
}

