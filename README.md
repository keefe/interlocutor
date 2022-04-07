# interlocutor
conversational API client for Slack

# Getting Started

1. Visit [Slack](https://api.slack.com/start) sign up for an account, take a look at the API and sign up for a workspace
2. Create a new slack app and generate various tokens. In the OAuth and Permissions Section, we'll need several scopes:
app_mentions:read
View messages that directly mention @categorizeus_interloc in conversations that the app is in

chat:write
Send messages as @categorizeus_interloc

commands
Add shortcuts and/or slash commands that people can use

channels:history
View messages and other content in public channels that categorize.us - interlocutor has been added to

channels:read
View basic information about public channels in a workspace


e.g. 
App ID 
Date of App Creation February 19, 2022
Client ID 
Client Secret 
Signing Secret 
Verification Token 
OAuth Tokens for Your Workspace 
Save these carefully somewhere.
3. Install jdk 11, eclipse or intellij
4. Edit your environment and create a slack App and AWS account and put credentials somewhere reasonable
kroeders@kroederss-MacBook-Pro interlocutor % cat ~/.zshenv 
export SLACK_BOT_TOKEN=
export SLACK_SIGNING_SECRET=
export PATH="/usr/local/opt/openjdk@11/bin:$PATH"
export JAVA_HOME=$(/usr/libexec/java_home)

5. run ngrok to get a temporary URI forwarded to 3000, e.g. 
                                          
Web Interface                 http://127.0.0.1:4040                                                                                                                    
Forwarding                    http://70e3-2600-1009-b1e6-a22f-a0b4-7cfc-6d81.ngrok.io -> http://localhost:3000                                                    
Forwarding                    https://70e3-2600-1009-b1e6-a22f-a0b4-7cfc-6d81.ngrok.io -> http://localhost:3000         
Unless you want to sign up for a paid plan, request URLs must be updated to the new UUID every time ngrok is started
5. In your app page, [e.g.](https://api.slack.com/apps/A033ESGMYTZ/general?) create a slash command /echo and register that to
https://<UUID>.ngrok.io/slack/events
don't include the name of the slash command in there.
I registered with ngrok.com and put my AuthToken in and my session seems to have run quite long, even persisting overnight.
6. Create two slash command with this slack/events URI as the request URL called /echo and /advise
7. Install this app to your workspace
8. run eclipse from the command line to get env variables e.g. 
open /Users/kroeders//eclipse/java-2021-12/Eclipse.app
run the code, should see hello in the home screen
9. Create a test channel and run /invite @botname to add the bot

