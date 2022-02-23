package us.categorize;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.view.Views.view;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.Conversation;
import com.slack.api.model.Message;
import com.slack.api.model.event.AppHomeOpenedEvent;

import us.categorize.conversation.slack.SlackMessage;
import us.categorize.model.simple.SimpleConversation;
/**
 * Hello world!
 *
 */
public class SentimentAdvisor 
{
    public static void main( String[] args ) throws Exception
    {
        var app = new App();
        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
        	  var appHomeView = view(view -> view
        	    .type("home")
        	    .blocks(asBlocks(
        	      section(section -> section.text(markdownText(mt -> mt.text("*This is running in eclipse :tada: Change Check")))),
        	      divider(),
        	      section(section -> section.text(markdownText(mt -> mt.text("This button won't do much for now but you can set up a listener for it using the `actions()` method and passing its unique `action_id`. See an example on <https://slack.dev/java-slack-sdk/guides/interactive-components|slack.dev/java-slack-sdk>.")))),
        	      actions(actions -> actions
        	        .elements(asElements(
        	          button(b -> b.text(plainText(pt -> pt.text("Click!"))).value("button1").actionId("button_1"))
        	        ))
        	      )
        	    ))
        	  );

        	  var res = ctx.client().viewsPublish(r -> r
        	    .userId(payload.getEvent().getUser())
        	    .view(appHomeView)
        	  );

        	  return ctx.ack();
        	});
        
        app.command("/echo", (req, ctx) -> {
        	  String commandArgText = req.getPayload().getText();
        	  String channelId = req.getPayload().getChannelId();
        	  String channelName = req.getPayload().getChannelName();
        	  String text = "Test said " + commandArgText + " at <#" + channelId + "|" + channelName + "> API thinks it is " + findConversation(channelName);
        	  List<Message> messages = fetchHistory(findConversation(channelName));
        	  for(Message m : messages) {
        		  System.out.println(m.getText());
        	  }
        	  return ctx.ack(text); // respond with 200 OK
        	});

        
        
        app.command("/advise", (req, ctx) -> {
      	  List<Message> messages = fetchHistory(findConversation(req.getPayload().getChannelName()));
          us.categorize.model.Conversation conversation = new SimpleConversation();
      	  StringBuilder text = new StringBuilder();
      	  int length = 0;
      	  for(Message m : messages) {
      		  System.out.println(m.getText());
      		  conversation.listen(new SlackMessage(m));
      		  text.append(m.getText() + "\n");
      		  length += m.getText().length();
      	  }
      	  System.out.println("Total chars " + length + " total units " + (length / 300.0) + " cost " + 0.0001*Math.max(length / 300.0,3.0));
          // Create credentials using a provider chain. For more information, see
          // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
          AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
          final AmazonComprehend comprehendClient =
              AmazonComprehendClientBuilder.standard()
                                           .withCredentials(awsCreds)
                                           .build();
          DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(text.toString())
                                                                                      .withLanguageCode("en");
          DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
          System.out.println(detectSentimentResult);

      	  return ctx.ack("General Sentiment " + detectSentimentResult.getSentiment()); // respond with 200 OK
      	});
        
        var server = new SlackAppServer(app);
        server.start();
    }
    
    /**
     * Find conversation ID using the conversations.list method
     */
    static String findConversation(String name) {
        // you can get this instance via ctx.client() in a Bolt app
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");
        try {
            // Call the conversations.list method using the built-in WebClient
            var result = client.conversationsList(r -> r
                // The token you used to initialize your app
                .token(System.getenv("SLACK_BOT_TOKEN"))
            );
            for (Conversation channel : result.getChannels()) {
                if (channel.getName().equals(name)) {
                    var conversationId = channel.getId();
                    // Print result
                    logger.info("Found conversation ID: {}", conversationId);
                    // Break from for loop
                    return conversationId;
                }
            }
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Fetch conversation history using ID from last example
     */
    static List<Message> fetchHistory(String id) {
    	Optional<List<Message>> conversationHistory = Optional.empty();
        // you can get this instance via ctx.client() in a Bolt app
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");
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
