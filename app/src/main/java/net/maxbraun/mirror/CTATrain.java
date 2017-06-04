package net.maxbraun.mirror;

/**
 * Created by David on 3/29/2017.
 */
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CTATrain extends DataUpdater <List<String>> {
    private static final String TAG = CTATrain.class.getSimpleName();

    /**
     * API Key, Call and two stop IDs for CTA Train API
     */
    private static final String APIKey = "?key=XXXXXXXXXXXXXXXXXXXXX"; //CTA train api key here
    private static final String stop1 = "&stpid=30251"; //ID for Redline Loyola North Bound towards Howard
    private static final String stop2 = "&stpid=30252"; //ID for Redline Loyola South Bound towards 95th
    private static final String APICall = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx";

    /**
     * The time in milliseconds between API calls to update
     */
    private static final long UPDATE_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(30);

    /**
     * A parser for XML.
     */
    private final XmlPullParser parser;

    public CTATrain(UpdateListener<List<String>> updateListener) {
        super(updateListener, UPDATE_INTERVAL_MILLIS);

        parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to initialize XML parser.", e);
        }
    }

    @Override
    protected List<String> getData() {
        List entries = new ArrayList();
        List<String> Time = new ArrayList<>();
        // Get the latest prediction times from cta API
        String Train[] = new  String[2];
        Train[0] = Network.get(APICall+APIKey+stop1+"&max=1");
        Train[1] = Network.get(APICall+APIKey+stop2+"&max=1");


        if (Train[0] == null || Train[1] == null) {
            Log.w(TAG, "Empty response.");
            return null;
        }


        try {
            for (int i = 0; i < 2; i++) {
                parser.setInput(new StringReader(Train[i]));
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "ctatt");

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("eta")) {

                        String time[] = readTitle(parser);

                        if(time[2] == "1") {

                            Time.add("DUE");
                        } else {                           //01234567891234567
                            System.out.println("mins");
                            int prdT = Integer.parseInt(time[0].substring(12, 14));

                            int arrT = Integer.parseInt(time[1].substring(12, 14));

                            String prdctd = Integer.toString(arrT - prdT);
                            if (arrT - prdT >= 2)
                                Time.add(prdctd + " mins");
                            else
                                Time.add("DUE");

                        }
                    }
                    else{
                        skipTags();
                    }
                }
            }
            return Time;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Parsing news XML response failed: " + Train[0] + Train[1], e);
            return null;
        }

    }



    private String[] readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "eta");
        String prdTime[] = new String[3];
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("prdt")) {
                prdTime[0] = readText("prdt");

            } else if (parser.getName().equals("arrT")) {
                prdTime[1] = readText("arrT");

            } else if (parser.getName().equals("isApp")) {
                prdTime[2] = readText("isApp");

            } else {
                skipTags();
            }
        }
        return prdTime;
    }

    private String readText(String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, name);
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, name);
        return text;
    }

    private void skipTags() throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException("Not skipping from a start tag.");
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    @Override
    protected String getTag() {
        return TAG;
    }
}


