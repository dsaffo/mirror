package net.maxbraun.mirror;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import net.maxbraun.mirror.DataUpdater.UpdateListener;
import net.maxbraun.mirror.Weather.WeatherData;

/**
 * The main {@link Activity} class and entry point into the UI.
 */
public class HomeActivity extends Activity {

  /**
   * The IDs of {@link TextView TextViews} in {@link R.layout#activity_home} which contain the news
   * headlines.
   */
  private static final int[] NEWS_VIEW_IDS = new int[]{
      R.id.news_1,
      R.id.news_2,
      R.id.news_3,
      //R.id.news_4,
  };

  private static final int[] CTABus_VIEW_IDS = new int[]{
          R.id.cta_bus_south,
          R.id.cta_bus_north,

  };

  private static final int[] CTATrain_VIEW_IDS = new int[]{
          R.id.cta_train_south,
          R.id.cta_train_north,

  };


  /**
   * The listener used to populate the UI with weather data.
   */
  private final UpdateListener<WeatherData> weatherUpdateListener =
      new UpdateListener<WeatherData>() {
    @Override
    public void onUpdate(WeatherData data) {
      if (data != null) {

        // Populate the current temperature rounded to a whole number.
        String temperature = String.format(Locale.US, "%d°",
            Math.round(getLocalizedTemperature(data.currentTemperature)));
        temperatureView.setText(temperature);

        // Populate the 24-hour forecast summary, but strip any period at the end.
        String summary = util.stripPeriod(data.daySummary);
        weatherSummaryView.setText(summary);

        // Populate the precipitation probability as a percentage rounded to a whole number.
        String precipitation =
            String.format(Locale.US, "%d%%", Math.round(100 * data.dayPrecipitationProbability));
        precipitationView.setText(precipitation);

        // Populate the icon for the current weather.
        iconView.setImageResource(data.currentIcon);

        // Show all the views.
        temperatureView.setVisibility(View.VISIBLE);
        weatherSummaryView.setVisibility(View.VISIBLE);
        precipitationView.setVisibility(View.VISIBLE);
        iconView.setVisibility(View.VISIBLE);
      } else {

        // Hide everything if there is no data.
        temperatureView.setVisibility(View.GONE);
        weatherSummaryView.setVisibility(View.GONE);
        precipitationView.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
      }
    }
  };

  /**
   * The listener used to populate the UI with news headlines.
   */
  private final UpdateListener<List<String>> newsUpdateListener =
      new UpdateListener<List<String>>() {
    @Override
    public void onUpdate(List<String> headlines) {

      // Populate the views with as many headlines as we have and hide the others.
      for (int i = 0; i < NEWS_VIEW_IDS.length; i++) {
        if ((headlines != null) && (i < headlines.size())) {
          newsViews[i].setText(headlines.get(i));
          newsViews[i].setVisibility(View.VISIBLE);
        } else {
          newsViews[i].setVisibility(View.GONE);
        }
      }
    }
  };

  private final UpdateListener <List<String>> ctaBusUpdateListener =
          new UpdateListener <List<String>>() {
            @Override
            public void onUpdate(List<String> entries) {
              for (int i = 0; i < 2; i++) {
                if ((entries != null) && (i < entries.size()) && (entries.get(i).contains("DUE"))) {
                  ctaBusViews[i].setText(entries.get(i));
                  ctaBusViews[i].setVisibility(View.VISIBLE);
                }
                else if ((entries != null) && (i < entries.size()) && (entries.get(i).contains("DLY"))){
                  ctaBusViews[i].setText(entries.get(i));
                  ctaBusViews[i].setVisibility(View.VISIBLE);
                }
                else if ((entries != null) && (i < entries.size()) && (entries.get(i).contains("DUE") == false)){
                  ctaBusViews[i].setText(entries.get(i) + " mins");
                  ctaBusViews[i].setVisibility(View.VISIBLE);
                }
                else {
                  ctaBusViews[i].setText("STPD");
                  ctaBusViews[i].setVisibility(View.VISIBLE);
                }
              }

            }
          };

  private final UpdateListener <List<String>> ctaTrainUpdateListener =
          new UpdateListener <List<String>>() {
            @Override
            public void onUpdate(List<String> entries) {
              for (int i = 0; i < 2; i++) {
                if ((entries != null) && (i < entries.size())) {
                  ctaTrainViews[i].setText(entries.get(i));
                  ctaTrainViews[i].setVisibility(View.VISIBLE);
                }

                else {
                  ctaTrainViews[i].setText("STPD");
                  ctaTrainViews[i].setVisibility(View.VISIBLE);

                }
              }

            }
          };

  private final UpdateListener <String> quoteUpdateListener =
          new UpdateListener <String>() {
            @Override
            public void onUpdate(String quote) {

                if (quote != null)  {
                  String Q = quote;
                  quoteView.setText(Q.substring(0,Q.lastIndexOf("-")));
                  quoteView.setVisibility(View.VISIBLE);
                  authorView.setText(Q.substring(Q.lastIndexOf("-")));
                  authorView.setVisibility(View.VISIBLE);
                }

                else {
                  quoteView.setText("ERR");
                  quoteView.setVisibility(View.VISIBLE);
                  authorView.setText("ERR");
                  authorView.setVisibility(View.VISIBLE);

                }




            }
          };

  /**
   * The listener used to populate the UI with body measurements.
   */
  private TextView authorView;
  private TextView quoteView;
  private TextView[]ctaTrainViews = new TextView[CTATrain_VIEW_IDS.length];
  private TextView[] ctaBusViews = new TextView[CTABus_VIEW_IDS.length];
  private TextView temperatureView;
  private TextView weatherSummaryView;
  private TextView precipitationView;
  private ImageView iconView;
  private TextView[] newsViews = new TextView[NEWS_VIEW_IDS.length];

  private Quote quote;
  private CTATrain ctaTrain;
  private CTABus ctaBus;
  private Weather weather;
  private News news;
   Util util;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    for (int i = 0; i < CTABus_VIEW_IDS.length; i++) {
      ctaBusViews[i] = (TextView) findViewById(CTABus_VIEW_IDS[i]);
    }
    for (int i = 0; i < CTATrain_VIEW_IDS.length; i++) {
      ctaTrainViews[i] = (TextView) findViewById(CTATrain_VIEW_IDS[i]);
    }
    temperatureView = (TextView) findViewById(R.id.temperature);
    weatherSummaryView = (TextView) findViewById(R.id.weather_summary);
    precipitationView = (TextView) findViewById(R.id.precipitation);
    iconView = (ImageView) findViewById(R.id.icon);
    for (int i = 0; i < NEWS_VIEW_IDS.length; i++) {
      newsViews[i] = (TextView) findViewById(NEWS_VIEW_IDS[i]);
    }
    quoteView = (TextView) findViewById(R.id.Quote);
    authorView = (TextView) findViewById(R.id.Author);

    quote = new Quote(quoteUpdateListener);
    ctaTrain = new CTATrain(ctaTrainUpdateListener);
    ctaBus = new CTABus(ctaBusUpdateListener);
    weather = new Weather(weatherUpdateListener);
    news = new News(newsUpdateListener);

    util = new Util(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    ctaBus.start();
    weather.start();
    news.start();
    ctaTrain.start();
    quote.start();

  }

  @Override
  protected void onStop() {
    ctaBus.stop();
    weather.stop();
    news.stop();
    ctaTrain.stop();
    quote.stop();

    super.onStop();
  }

  @Override
  protected void onResume() {
    super.onResume();
    util.hideNavigationBar(temperatureView);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return util.onKeyUp(keyCode, event);
  }

  /**
   * Converts a temperature in degrees Fahrenheit to degrees Celsius, depending on the
   * {@link Locale}.
   */
  private double getLocalizedTemperature(double temperatureFahrenheit) {
    // First approximation: Fahrenheit for US and Celsius anywhere else.
    return Locale.US.equals(Locale.getDefault()) ?
        temperatureFahrenheit : (temperatureFahrenheit - 32.0) / 1.8;
  }
}
