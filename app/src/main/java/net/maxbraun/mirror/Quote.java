package net.maxbraun.mirror;

/**
 * Created by David on 3/30/2017.
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

public class Quote extends DataUpdater <String> {
    private static final String TAG = Quote.class.getSimpleName();

    /**
     * The "Top Headlines" news feed from the Associated Press.
     */


    /**
     * The time in milliseconds between API calls to update the news.
     */
    private static final long UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1440);

    /**
     * A parser for the news feed XML.
     */
    private final XmlPullParser parser;

    public Quote(UpdateListener <String> updateListener) {
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
        String quote = "";
        List<String> Time = new ArrayList<>();
        // Get the latest headlines from the AP news feed.
        String  url = Network.get("https://www.quotesdaddy.com/feed/tagged/Inspirational");


        if (url == null) {
            Log.w(TAG, "Empty response.");
            return null;
        }

        // Parse just the headlines from the XML.
        try {

                parser.setInput(new StringReader(url));
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "rss");

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("channel")) {
                        System.out.println("CHANNEL");
                        //quote = readTitle(parser);
                         parser.next();
                    }
                    else if (name.equals("item")){
                        quote = readTitle(parser);
                    }
                    else{
                        skipTags();
                    }


                }
            return quote;

        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Parsing news XML response failed: " + url, e);
            return null;
        }

    }



    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("title")) {
                title = readText("title");
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
