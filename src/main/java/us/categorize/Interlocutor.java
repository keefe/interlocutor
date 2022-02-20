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

import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.model.event.AppHomeOpenedEvent;
/**
 * Hello world!
 *
 */
public class Interlocutor 
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
        	          button(b -> b.text(plainText(pt -> pt.text("Click me!"))).value("button1").actionId("button_1"))
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
        	  String text = "You said " + commandArgText + " at <#" + channelId + "|" + channelName + ">";
        	  return ctx.ack(text); // respond with 200 OK
        	});
        
        for(String key : System.getenv().keySet()) {
        	System.out.println(key + ","+System.getenv(key));
        }
        var server = new SlackAppServer(app);
        server.start();
    }
}
