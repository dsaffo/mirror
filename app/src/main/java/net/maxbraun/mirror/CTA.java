package net.maxbraun.mirror;

/**
 * Created by David on 3/13/2017.
 */

import android.service.chooser.ChooserTarget;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.Console;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



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
 * A helper class to regularly retrieve news headlines.
 */
public class CTA extends DataUpdater<String> {
    private static final String TAG = CTA.class.getSimpleName();

    /**
     * The "Top Headlines" news feed from the Associated Press.
     */
    private static final String AP_TOP_HEADLINES_URL =
            "http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305";



    private static final String CTA_API_KEY = "?key=4c9YFqurKs5YsVGcJD7fUnGk7";
    private static final String CTA_TIME_LINK = "http://www.ctabustracker.com/bustime/api/v2/gettime";
    private static final String CTA_stid_GranvilleSheridanSouth = "&stpid=1033";
    private static final String CTA_Prediction_Link = "http://www.ctabustracker.com/bustime/api/v2/getpredictions";

    /**
     * The time in milliseconds between API calls to update the news.
     */
    private static final long UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1);

    /**
     * A parser for the news feed XML.
     */
    private final XmlPullParser parser;

    public CTA(UpdateListener<String> updateListener) {
        super(updateListener, UPDATE_INTERVAL_MILLIS);

        parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to initialize XML parser.", e);
        }
    }

    @Override
    protected String getData() {
        List entries = new ArrayList();
        // Get the latest headlines from the AP news feed.
        String response = Network.get(CTA_Prediction_Link+CTA_API_KEY+CTA_stid_GranvilleSheridanSouth+"&top=1");
        if (response == null) {
            Log.w(TAG, "Empty response.");
            return null;
        }

        // Parse just the headlines from the XML.
        try {
            parser.setInput(new StringReader(response));
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "bustime-response");
            String Time = "";
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("prd")) {
                    Time = readTitle(parser);
                }

            }
            return Time;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Parsing news XML response failed: " + response, e);
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
