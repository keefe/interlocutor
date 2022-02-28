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

import java.util.HashMap;
import java.util.Map;

import com.slack.api.bolt.App;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.MessageEvent;

import us.categorize.conversation.slack.SlackMessage;
import us.categorize.model.Channel;
import us.categorize.model.Message;

public class SlackInterlocutor {
	public static void main(String[] args) throws Exception {
		SlackInterlocutor interlocutor = new SlackInterlocutor();
		interlocutor.configureSlack();
	}
	
	private final Map<String, Channel> id2Channel = new HashMap<>();

	public void configureSlack() throws Exception {
        var app = new App();
        
        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
      	  var appHomeView = view(view -> view
      	    .type("home")
      	    .blocks(asBlocks(
      	      section(section -> section.text(markdownText(mt -> mt.text("*This is running in eclipse :tada: Change Check")))),
      	      divider(),
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
      	  return ctx.ack(commandArgText); // respond with 200 OK
      	});

        

		app.event(MessageEvent.class, (payload, ctx) -> {
			System.out.println(payload.getEvent().getText());
			listen(new SlackMessage(null));
			return ctx.ack();
		});
	}
	
	private void listen(Message message)
	{
		
	}

}
