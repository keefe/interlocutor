# interlocutor
conversational API client for Slack

# Getting Started

1. Visit [Slack](https://api.slack.com/start) sign up for an account, take a look at the API and ping me your email
2. Create a new slack app and generate various tokens, e.g. 
App ID 
Date of App Creation February 19, 2022
Client ID 
Client Secret 
Signing Secret 
Verification Token 
OAuth Tokens for Your Workspace 
Save these carefully somewhere.
3. Install jdk 11, eclipse or intellij
4. Edit your environment
kroeders@kroederss-MacBook-Pro interlocutor % cat ~/.zshenv 
export SLACK_BOT_TOKEN=
export SLACK_SIGNING_SECRET=
export PATH="/usr/local/opt/openjdk@11/bin:$PATH"
export JAVA_HOME=$(/usr/libexec/java_home)
5. run ngrok to get a temporary URI forwarded to 3000, e.g. 
                                          
Web Interface                 http://127.0.0.1:4040                                                                                                                    
Forwarding                    http://70e3-2600-1009-b1e6-a22f-a0b4-7cfc-6d81.ngrok.io -> http://localhost:3000                                                    
Forwarding                    https://70e3-2600-1009-b1e6-a22f-a0b4-7cfc-6d81.ngrok.io -> http://localhost:3000         

6. run eclipse from the command line to get env variables e.g. 
open /Users/kroeders//eclipse/java-2021-12/Eclipse.app
run the code, should see hello in the home screen

