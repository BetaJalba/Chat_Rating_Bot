
## Introduction

What will follow is a thorough and straightforward documentation on the description, support
and deployment of an automatic chat detection and rating bot. Said bot will only require a manual
startup procedure and will then automatically detect, gather and organize all of the data it needs.
Before proceeding, it will be of great use having a brief description of the project. The bot’s
purpose is to automatically detect the behavior of the parties in a group chat to then rate how well they
behave and how educated they are. <s>In such matters, the context and personality traits of the
individuals are of important relevance and it will be necessary to first gather data about their traits.
After this, which we shall refer to as the “Identifying Phase”,</s> the bot will <s>then proceed to</s> listen to
messages said in the chat and give them scores, by utilizing APIs implemented in social or game chat
detection and filtering, based on editable parameters such as: aggressiveness, intent, irony, sarcasm.
**The bot will only store the scores and completely discard the message right after analysis.** Next,
all of the processed data will be added to the user’s database and be confronted with their personality
traits so to see how to adjust the scores. <s>The user’s data, which will ultimately be organized in ratings
called “Chat Scores”, will have different timestamps such as: lifetime, daily, weekly, during
arguments.</s> The chat scores can be consulted by users at any time <s>and can be erased, but not altered.</s>


## APIs Implemented

While this project initially foresaw the use of multiple APIs, technical limitations made me
decide to drop the personality analysis due to time and financial constraints. The project currently
implements Perspective, a decently powerful machine learning REST API that allows users to receive
customized and detailed ratings on the toxicity or politeness of chat messages in multiple languages.

API link: [Click here](https://support.perspectiveapi.com/s/?language=en_US)

API documentation: [Click here](https://support.perspectiveapi.com/s/docs?language=en_US)


## Setup Guide

The setup procedure is pretty straightforward. Once the repository, found at
[Here](https://github.com/BetaJalba/Chat_Rating_Bot), has been forked it is necessary to first create a
config.properties file inside of /src/main/resources

The config file contains the bot’s token (BOT_TOKEN) and the API’s key
(PERSPECTIVE_API_KEY); but it also contains many parameters that can be customized to the
user’s preferences. A moderate array of variables can be altered such as toxicity biases, message
languages and the types of toxicity the API will look for. Here follow all of the variables that can be
customized:

- BOT_NAME, alters the welcome message on the program’s console on startup
- WELCOME_MESSAGE, allows the developer to set a welcome message for the user’s first
  interaction with the bot (can be set to null)
- RATE_MESSAGE, tells the API which traits to look for inside a message, for more info
  consult the API’s documentation
- MESSAGE_LANGUAGES, tells the API which languages to look for
- SCORE_ALPHA, tells the bot how much impact the messages have on the overall score (as it
  is a weighted sum)
- MESSAGE_BIAS, tell the bot the rate at which a message’s influence on the overall score
  decays (its effect is much less noticeable that that of the alpha)


## Commands List and Description

The bot’s commands mainly revolve around viewing the scores, either as a leaderboard or as a
per user or per chat basis; the bot also keeps track of the amount of messages an user has sent in each
chat. Here’s a list of all the available commands, each with a brief description:

/leaderboard - chat leaderboard

/score - user score in the current chat

/help - displays all commands

/chat_messages - number of messages sent in the current chat

/all_user_messages - number of messages sent globally by the user

/mean_user_behavior - mean score of the user globally

/mean global_behavior - mean score of all chats globally
