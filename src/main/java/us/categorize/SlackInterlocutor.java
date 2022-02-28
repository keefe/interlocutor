package us.categorize;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.view.Views.view;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.MessageEvent;

import us.categorize.advice.Advisor;
import us.categorize.advice.SentimentAdvice;
import us.categorize.advice.aws.comprehend.ComprehendAdvisor;
import us.categorize.conversation.slack.SlackMessageEvent;
import us.categorize.model.Channel;
import us.categorize.model.Conversation;
import us.categorize.model.Message;
import us.categorize.model.simple.SimpleChannel;
import us.categorize.model.simple.SimpleCriteria;

//TODO feels like an interface Interlocutor wants to come out here, that listens and responds
public class SlackInterlocutor {
	
	private Logger logger = LoggerFactory.getLogger(SlackInterlocutor.class);
	
	public static void main(String[] args) throws Exception {
		SlackInterlocutor interlocutor = new SlackInterlocutor();
		interlocutor.configureSlack();
	}

	private final Map<String, Channel> id2Channel = new HashMap<>();
	
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
			Channel<SimpleCriteria> channel = id2Channel.get(ctx.getChannelId());
			Conversation conversation = channel.filter(new SimpleCriteria()).get(0);
			for(us.categorize.model.Message m : conversation.content())
				logger.info(m.getText());
			SentimentAdvice sentiment = advisor.detectSentiment(conversation);
			return ctx.ack("General Sentiment " + sentiment.getSentiment());
		});

		var server = new SlackAppServer(app);
		var logger = LoggerFactory.getLogger(PrototypeSentimentAdvisor.class);
		logger.info("Listening to Slack Workspace");
		server.start();

	}

	private void listen(String channel, Message message) {
		if (!id2Channel.containsKey(channel)) {
			id2Channel.put(channel, new SimpleChannel(channel));
		}
		id2Channel.get(channel).listen(message);
	}

}
