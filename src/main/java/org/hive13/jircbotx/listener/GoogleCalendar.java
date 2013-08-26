package org.hive13.jircbotx.listener;

import org.hive13.jircbotx.JircBotX;
import org.hive13.jircbotx.ListenerThreadX;

public class GoogleCalendar extends ListenerThreadX {

   // https://code.google.com/p/google-api-java-client/source/browse/calendar-cmdline-sample/src/main/java/com/google/api/services/samples/calendar/cmdline/CalendarSample.java?repo=samples
   // https://code.google.com/apis/console/?api=calendar#project:300454065759:access
   // http://samples.google-api-java-client.googlecode.com/hg/calendar-cmdline-sample/instructions.html
   // https://code.google.com/p/google-api-java-client/wiki/APIs
   public GoogleCalendar(JircBotX bot, String channelList, long loopDelay) {
      super(bot, channelList, loopDelay);
      // TODO Auto-generated constructor stub
   }

   @Override
   public void loop() {
   }

   @Override
   public String getCommandName() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getHelp() {
      // TODO Auto-generated method stub
      return null;
   }

}
