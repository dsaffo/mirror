# Smart Mirror

This is my personal take on an android based smart mirror moffied from maxbbraun here:https://github.com/maxbbraun/mirror

# Features
  - Weather
  - Time/Date
  - Quote of the Day
  - 2 CTA Bus Times
  - 2 CTA Train Times
  - 3 AP top headlines 

# Building APK
 You will need to add your own API keys for the following classes 

  - Weather: https://darksky.net/dev/
  - CTABus: http://www.transitchicago.com/developers/bustracker.aspx
  - CTATrain: http://www.transitchicago.com/developers/ttdocs/

To change the bus stops edit the following lines in ```CTABus.java``` with your desired route, stop id's, and API key Then edit the labes in ```activity_home.xml```. Route and stop id numbers can be found in the CTA bus documentation: http://www.transitchicago.com/assets/1/developer_center/cta_Bus_Tracker_API_Developer_Guide_and_Documentation_20160929.pdf
```sh
private static final String APIKey = "?key=XXXXXXXXXXXXXXXXXXXXX";
private static final String stop1 = "&stpid=XXXXX"; 
private static final String stop2 = "&stpid=XXXXX";
private static final String route = "&rt=XXX"; 
```

To change the train stops edit the following lines in ```CTATrain.java``` with your desired stop id's, and API key (note no route id is required) Then edit the labes in ```activity_home.xml```. Route and stop id numbers can be found in the CTA train documentation: http://www.transitchicago.com/assets/1/developer_center/cta_Train_Tracker_API_Developer_Guide_and_Documentation_20160929.pdf

```sh
private static final String APIKey = "?key=XXXXXXXXXXXXXXXXXXXXX";
private static final String stop1 = "&stpid=XXXXX"; 
private static final String stop2 = "&stpid=XXXXX";
```

# Photos and Frame Guide
 Bellow is a photo of how everything should look onces its up and running. The easiest way to put it all together is to buy a acrylic two way mirror like this one:https://www.amazon.com/12-Acrylic-See-Through-Mirror/dp/B017ONH3EG and to take it and your tablet to your local frame shop. Remeber to explain your project and to make sure they add cut outs for the charging port.

![alt text](http://i.imgur.com/14sIoY8.jpg "hi")
