/*
 * Weather Underground Info:
 * - General API documentation: http://www.wunderground.com/weather/api/d/documentation.html
 * - Java API page: http://code.google.com/p/wunderground-core/
 * - Wunderground API Key: ad6ac91937e9a86c/
 * IMPORTANT: API key is limited to 10 requests / minute, 500 requests / day
 * 
 */
package org.hive13.jircbotx.listener;

import org.hive13.jircbotx.support.BotProperties;
import org.hive13.jircbotx.support.WUndergroundAPI;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Temperature extends ListenerAdapter<PircBotX> {
   private URL tempFeedURL = null;

   public void onMessage(MessageEvent<PircBotX> event) throws Exception {
      if(event.getMessage().toLowerCase().startsWith("!temp"))
      {
         // Retrieve the current Hive13 temperature
         double dSpaceTemp = getTemperatureFromRSS();
   
         // Retrieve the current local temperature
         double dLocalTemp = WUndergroundAPI.getTemperature();
   
         // Send a message to the channel w/ the current Hive13 & Local
         // temperature.
         event.getChannel().sendMessage("Hackerspace: " + dSpaceTemp +
               " -- Outdoors: " + dLocalTemp);
      }
   }
   
   private double getTemperatureFromRSS() {
      double result = -1;
      // Here we pretend to be the google bot to fake out User-Agent
      // sniffing programs.
      try {
         tempFeedURL = new URL("http://www.hive13.org/isOpen/RSS.php?temp=0");
         URLConnection conn = tempFeedURL.openConnection();
         conn.setRequestProperty("User-Agent", BotProperties.getInstance()
               .getUserAgentString());

         // Create a feed off of the URL and get the latest news.
         SyndFeedInput input = new SyndFeedInput();
         SyndFeed feed = input.build(new XmlReader(conn));

         // Get the feed's list of entries
         @SuppressWarnings("unchecked")
         List<SyndEntry> entryList = feed.getEntries();
         Collections.sort(entryList, new SyndEntryComparator());

         if (entryList.size() > 0) {
            // We have found an RSS feed at least, is it formatted
            // correctly?
            result = Double.parseDouble(entryList.get(0).getTitle());
         } else {
            // We have not found an RSS feed for the hackerspace
            // tempreature.
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (FeedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return result;
   }

   /**
    * 
    * This class is simply here to sort lists of SyndEntries.
    * 
    */
   class SyndEntryComparator implements Comparator<SyndEntry> {
      public int compare(SyndEntry o1, SyndEntry o2) {
         int pubDateCompare = o2.getPublishedDate().compareTo(
               o1.getPublishedDate());
         return pubDateCompare;
      }
   }
   
}
