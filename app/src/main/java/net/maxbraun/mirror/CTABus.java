package net.maxbraun.mirror;

/**
 * Created by David on 3/13/2017.
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

/**
 * A helper class to regularly retrieve cta bus arrival times.
 */
public class CTABus extends DataUpdater <List<String>> {
    private static final String TAG = CTABus.class.getSimpleName();

    /**
     * The cta Bus api keys and parameters for one route and two stops
     */
    private static final String APIKey = "?key=XXXXXXXXXXXXXXXXXXXXX"; //Your API key here after ?key=
    private static final String stop1 = "&stpid=1204"; //147 Loyola and Sheridan North bound
    private static final String stop2 = "&stpid=1027"; //147 Loyola and Sheridan South bound
    private static final String route = "&rt=147"; //route 147
    private static final String CTA_Prediction_Link = "http://www.ctabustracker.com/bustime/api/v2/getpredictions"; //api call for arrival times

    /**
     * The time in milliseconds between API calls to update.
     */
    private static final long UPDATE_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(30);

    /**
     * A parser for XML
     */
    private final XmlPullParser parser;

    public CTABus(UpdateListener<List<String>> updateListener) {
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
        // Get the latest headlines from the AP news feed.
        String Bus[] = new  String[2];
        Bus[0] = Network.get(CTA_Prediction_Link+APIKey+stop1+route+"&top=1");
        Bus[1] = Network.get(CTA_Prediction_Link+APIKey+stop2+route+"&top=1");


        if (Bus[0] == null || Bus[1] == null) {
            Log.w(TAG, "Empty response.");
            return null;
        }


        try {
            for (int i = 0; i < 2; i++) {
                parser.setInput(new StringReader(Bus[i]));
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "bustime-response");

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("prd")) {
                        String time = readTitle(parser);
                        Time.add(time);
                    }

                }
            }
            return Time;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Parsing news XML response failed: " + Bus[0] + Bus[1], e);
            return null;
        }

    }



    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "prd");
        String title = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("prdctdn")) {
                title = readText("prdctdn");
            } else {
                skipTags();
            }
        }
        return title;
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
