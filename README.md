# botl
A location based social messaging service. Created by:

+ [Stephen Kline](mailto:srkline@umich.edu) 
+ [Kevin Kuang](mailto:kkuang@umich.edu) 
+ [Bahru Negash](mailto:bahrut@umich.edu) 
+ [Zechariah Schneider](zechsch@umich.edu) 
+ [Joshua Spigelman](jlspige@umich.edu) 

# Setup:
0. Make sure to use an updated Android Studio (version 2.3)
1. Open the botl App on Android Studio.
2. Install the volley library from the google github (https://github.com/google/volley)
3. Delete the current volley folder in the application.
4. Click file -> new -> import module and import the volley file just downloaded.
5. Build the app.
6. Run the emulator (We use Nexus 6 API 24).

# Front-end Features:
+ Split/Multi-pane view with map on top half of the screen and live feed on the bottom half of the screen.
+ Post new messages by clicking the "+" button.
+ A marker will appear at the user's latitude/longitude location where the message was posted. 
+ Messages will appear on the live feed at the bottom half of the screen.
+ Click on a message on the live feed or the info window of a marker on the map to view the message, view the replies to the message, rate the message, and to reply to the message. 
+ Custom markers to reveal messages (Can change to other images for sponsored messages)
+ Refresh button to update new messages/replies/ratings.
+ Hold onto a live feed message to make map look at the message.

# Parameters:
+ Currently 50 messages within 1000 miles of the user's location will show up (Debugging purposes)
+ No messages expire for debugging purposes (although we plan to have messages expire after 24 hours).

# Current Bugs:
+ Needs to update user location on the map after sending in new coords.
+ Multiple posts at the same time
+ App initially crashes on the phone until user allows location
+ App doesn't work correctly when the user opens the app for the first time, (mainly due to the answer for the permissions). I was thinking if the user hasn't already granted permission we bring them to a splash screen that asks for their permission, otherwise just have that splash screen bring them directly to our main activity.
