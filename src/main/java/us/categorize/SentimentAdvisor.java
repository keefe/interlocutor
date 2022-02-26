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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.Conversation;
import com.slack.api.model.Message;
import com.slack.api.model.event.AppHomeOpenedEvent;

import us.categorize.advice.Advisor;
import us.categorize.advice.SentimentAdvice;
import us.categorize.advice.aws.comprehend.ComprehendAdvisor;
import us.categorize.conversation.slack.SlackMessage;
import us.categorize.model.simple.SimpleConversation;

public class SentimentAdvisor 
{
	
	private  us.categorize.model.Conversation currentConversation;
	
	private Map<us.categorize.model.Conversation, SentimentAdvice> conversationAdvice;
	
	private Advisor sentimentAdvisor;
	
	private static final int CONVERSATION_DELTA = 60*10;//seconds to make it a new convo
		
	private static final SentimentAdvice noopAdvice = new SentimentAdvice() {
		
		@Override
		public String getSentiment() {
			// TODO Auto-generated method stub
			return "DEFAULT : No Analysis Performed";
		}
		
		@Override
		public double getPositive() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public double getNeutral() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public double getNegative() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public double getMixed() {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	public SentimentAdvisor()
	{
		currentConversation = new SimpleConversation();
		conversationAdvice = new HashMap<>();
		sentimentAdvisor = new ComprehendAdvisor();
	}
	
	
    public static void main( String[] args ) throws Exception
    {
    	SentimentAdvisor sentimenetAdvisor = new SentimentAdvisor();
    	sentimenetAdvisor.listenToSlack();
    }
    
    public void listenToSlack() throws Exception {
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
          //needs to break it down into channel or this will mix stuff up
    	  currentConversation = new SimpleConversation();
		  conversationAdvice = new HashMap<>();

      	  List<Message> messages = fetchHistory(findConversation(req.getPayload().getChannelName()));
      	  for(Message m : messages) {
      		addMessage(m);
      	  }
      	  if("all".equals(req.getPayload().getText())) {
      		  for(us.categorize.model.Conversation c : conversationAdvice.keySet()) {
      			  SentimentAdvice advice = conversationAdvice.get(c);
      			  if(advice == null || advice == noopAdvice) {
      				  advice = (SentimentAdvice) sentimentAdvisor.advise(c);
      				  conversationAdvice.put(c, advice);
      			  }
      		  }
      	  }
      		  
      	  SentimentAdvice advice = findAdvice();
      	  
      	  return ctx.ack("General Sentiment " + advice.getSentiment()); // respond with 200 OK
      	});
        System.out.println("before start");
        var server = new SlackAppServer(app);
        server.start();
        System.out.println("After Start");
        var logger = LoggerFactory.getLogger(SentimentAdvisor.class);
        logger.info("Listening to Slack Workspace");
    }
    
    
    private SentimentAdvice findAdvice() {
    	us.categorize.model.Conversation advisedConversation = currentConversation;
    	for(us.categorize.model.Conversation conversation : conversationAdvice.keySet()) {
    		if(advisedConversation == null) advisedConversation = conversation; //won't happen as written but for convenience
    		else {
    			us.categorize.model.Message currentLatest = advisedConversation.latest();
    			us.categorize.model.Message checkLatest = conversation.latest();
    			//a sliding window approach here could be useful, but expensive
    			//should this stuff be moved into conversation, break this conversation down by time?
    			//or should we have a channel class?
    			if(currentLatest == null || checkLatest.getTimestampSeconds() > currentLatest.getTimestampSeconds()) {
    				advisedConversation = conversation;
    			}
    		}
    	}
    	if(conversationAdvice.containsKey(advisedConversation) && conversationAdvice.get(advisedConversation) != noopAdvice) {
    		return conversationAdvice.get(advisedConversation);
    	}
    	SentimentAdvice advice = (SentimentAdvice) sentimentAdvisor.advise(advisedConversation);
    	if(advisedConversation!=currentConversation)
    		conversationAdvice.put(advisedConversation, advice);
    	return advice;
    }
    
    private void addMessage(Message message) {
    	SlackMessage newMessage = new SlackMessage(message);
    	us.categorize.model.Message lastKnown = currentConversation.latest();
    	
    	//this assumes we are getting messages in order and don't need to iterate through past conversations to find the place of a new message
    	//this may or may not be true of threaded messages
    	//and certainly depends on ordering, in slack case we get them in reverse chrono so this value should always be negative
    	if(lastKnown != null && Math.abs(newMessage.getTimestampSeconds() - lastKnown.getTimestampSeconds()) > CONVERSATION_DELTA) {
    		conversationAdvice.put(currentConversation, noopAdvice);
    		currentConversation = new SimpleConversation();
    	}
    	currentConversation.listen(newMessage);
    }
    
    /**
     * Find conversation ID using the conversations.list method
     */
    private String findConversation(String name) {
        // you can get this instance via ctx.client() in a Bolt app
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger(SentimentAdvisor.class);
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
    private List<Message> fetchHistory(String id) {
    	var logger = LoggerFactory.getLogger(SentimentAdvisor.class);
    	Optional<List<Message>> conversationHistory = Optional.empty();
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
