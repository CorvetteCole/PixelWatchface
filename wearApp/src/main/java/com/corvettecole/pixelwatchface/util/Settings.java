package com.corvettecole.pixelwatchface.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.corvettecole.pixelwatchface.util.Constants.UpdatesRequired;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;

public class Settings {


  private static volatile Settings instance;

  private boolean showTemperature, showWeatherIcon, useCelsius,  
  useThin, useThinAmbient, useGrayInfoAmbient, showInfoBarAmbient, showTemperatureFractional,
  showBattery, showWearIcon, useDarkSky, weatherChangeNotified, companionAppNotified, advanced;

  private String darkSkyAPIKey;
  private SharedPreferences sharedPreferences;

  // TODO consider the following
  /* Singletons are generally bad design, although in this case I am using one to share memory on
     a resource constrained device - a watch. Whether this is needed should be thought over.
   */
  private Settings(Context context) {
    if (instance != null) {
      throw new RuntimeException(
          "Use getInstance() method to get the single instance of this class");
    } else {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      loadPreferences();

    }
  }

  public static Settings getInstance(Context context) {
    if (instance == null) {
      synchronized (Settings.class) {
        if (instance == null) {
          instance = new Settings(context.getApplicationContext());
        }
      }
    }
    return instance;
  }

  public boolean isShowTemperature() {
    return showTemperature;
  }

  public boolean isShowWeatherIcon() {
    return showWeatherIcon;
  }

  public boolean isUseCelsius() {
    return useCelsius;
  }

  public void setUseCelsius(boolean value) {
    useCelsius = value;
  }

  public boolean isUseThin() {
    return useThin;

  }


  public boolean isUseThinAmbient() { return useThinAmbient; }

  public boolean isUseGrayInfoAmbient() { return useGrayInfoAmbient; }


  public boolean isShowInfoBarAmbient() {
    return showInfoBarAmbient;
  }

  public boolean isShowTemperatureFractional() {
    return showTemperatureFractional;
  }

  public boolean isShowBattery() {
    return showBattery;
  }

  public boolean isShowWearIcon() {
    return showWearIcon;
  }

  public boolean isUseDarkSky() {
    return useDarkSky;
  }

  public boolean isWeatherChangeNotified() {
    return weatherChangeNotified;
  }

  public void setWeatherChangeNotified(boolean weatherChangeNotified) {
    this.weatherChangeNotified = weatherChangeNotified;
    savePreferences();
  }

  public boolean isCompanionAppNotified() {
    return companionAppNotified;
  }

  public void setCompanionAppNotified(boolean companionAppNotified) {
    this.companionAppNotified = companionAppNotified;
    savePreferences();
  }

  public boolean isWeatherDisabled() {
    if (!isAdvanced()) {
      return true;
    } else {
      return (!showWeatherIcon && !showTemperature);
    }
  }

  public void setWeatherDisabled() {
    showWeatherIcon = false;
    showTemperature = false;
    savePreferences();
  }

  public boolean isAdvanced() {
    //return true;
    return advanced;
  }

  public ArrayList<UpdatesRequired> updateSettings(
      DataMap dataMap) {  // returns if weather update required
    String TAG = "updateSettings";
    boolean tempShowTemperature = showTemperature;
    boolean tempShowWeatherIcon = showWeatherIcon;
    boolean tempUseDarkSky = useDarkSky;

    boolean tempUseThin = useThin;
    boolean tempUseThinAmbient = useThinAmbient;
    boolean tempUseGrayInfoAmbient = useGrayInfoAmbient;

    ArrayList<UpdatesRequired> updatesRequired = new ArrayList<>();

    Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));

    showTemperature = dataMap.getBoolean("show_temperature", false);
    useCelsius = dataMap.getBoolean("use_celsius");
    showWeatherIcon = dataMap.getBoolean("show_weather", false);
    darkSkyAPIKey = dataMap.getString("dark_sky_api_key");

    showTemperatureFractional = dataMap.getBoolean("show_temperature_decimal");

    useThin = dataMap.getBoolean("use_thin");
    useThinAmbient = dataMap.getBoolean("use_thin_ambient");
    useGrayInfoAmbient = dataMap.getBoolean("use_gray_info_ambient");
    showInfoBarAmbient = dataMap.getBoolean("show_infobar_ambient");

    showBattery = dataMap.getBoolean("show_battery", true);
    showWearIcon = dataMap.getBoolean("show_wear_icon", false);

    useDarkSky = dataMap.getBoolean("use_dark_sky", false);

    advanced = dataMap.getBoolean("advanced", false);

    savePreferences();
    if (tempUseDarkSky != useDarkSky || showTemperature != tempShowTemperature || showWeatherIcon
        != tempShowWeatherIcon) {  //detect if weather related settings has changed
      updatesRequired.add(UpdatesRequired.WEATHER);
    }

    if (tempUseThin != useThin || tempUseThinAmbient != useThinAmbient || tempUseGrayInfoAmbient != useGrayInfoAmbient) { // check if font needs update

      updatesRequired.add(UpdatesRequired.FONT);
    }

    return updatesRequired;

  }

  private void loadPreferences() {
    showTemperature = sharedPreferences.getBoolean("show_temperature", false);
    showWeatherIcon = sharedPreferences.getBoolean("show_weather", false);
    useCelsius = sharedPreferences.getBoolean("use_celsius", true);

    useThin = sharedPreferences.getBoolean("use_thin", false);
    useThinAmbient = sharedPreferences.getBoolean("use_thin_ambient", false);
    useGrayInfoAmbient = sharedPreferences.getBoolean("use_gray_info_ambient", true);
    showInfoBarAmbient = sharedPreferences.getBoolean("show_infobar_ambient", true);

    showTemperatureFractional = sharedPreferences.getBoolean("show_temperature_decimal", false);

    darkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
    useDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);

    showBattery = sharedPreferences.getBoolean("show_battery", true);
    showWearIcon = sharedPreferences.getBoolean("show_wear_icon", false);

    weatherChangeNotified = sharedPreferences.getBoolean("weather_change_notified", false);
    companionAppNotified = sharedPreferences.getBoolean("companion_app_notified", false);

    advanced = sharedPreferences.getBoolean("advanced", false);
  }

  private void savePreferences() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("show_temperature", showTemperature);
    editor.putBoolean("use_celsius", useCelsius);
    editor.putBoolean("show_weather", showWeatherIcon);
    editor.putBoolean("show_temperature_decimal", showTemperatureFractional);
    editor.putBoolean("use_thin", useThin);
    editor.putBoolean("use_thin_ambient", useThinAmbient);
    editor.putBoolean("use_gray_info_ambient", useGrayInfoAmbient);
    editor.putBoolean("show_infobar_ambient", showInfoBarAmbient);
    editor.putBoolean("show_battery", showBattery);
    editor.putBoolean("show_wear_icon", showWearIcon);

    editor.putString("dark_sky_api_key", darkSkyAPIKey);
    editor.putBoolean("use_dark_sky", useDarkSky);

    editor.putBoolean("weather_change_notified", weatherChangeNotified);
    editor.putBoolean("companion_app_notified", companionAppNotified);

    editor.putBoolean("advanced", advanced);

    editor.apply();
  }


}
