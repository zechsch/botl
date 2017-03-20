# botl
A location based social messaging service. Created by:

+ [Stephen Kline](mailto:srkline@umich.edu) 
+ [Kevin Kuang](mailto:kkuang@umich.edu) 
+ [Bahru Negash](mailto:bahrut@umich.edu) 
+ [Zechariah Schneider](zechsch@umich.edu) 
+ [Joshua Spigelman](jlspige@umich.edu) 

Setup: <br/>
Make sure to use an updated Android Studio (version 2.3)
1. Open the botl App on Android Studio.
2. Install the volley library from the google github
3. Delete the current volley folder in the application.
4. Click file -> new -> import module and import the volley file just downloaded.
5. Build the app and run it on the emulator (We use Nexus 6 API 24)
6. Run the emulator.

Front-end Features:
+ Post new messages by clicking the + button.
+ A marker will appear at the user's latitude/longitude location where the message was posted. 
+ Click on a message on the live feed or the info window of a marker on the map to view the message, view the replies to the message, rate the message, and to reply to the message. 
+ Custom markers to reveal messages (Can change to other images for sponsored messages)

Parameters: <br/>
+ Currently 50 messages within 1000 miles of the user's location will show up (Debugging purposes)
