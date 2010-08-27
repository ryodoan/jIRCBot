/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jircbot.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jibble.pircbot.PircBot;

/**
 * This abstract class framework is used for implementing asynchronous commands
 * that run in the background and can react to externally generated events.
 * @author vincenpt
 */
public abstract class jIBCommandThread extends jIBCommand implements Runnable {

    protected PircBot bot = null;
    protected String channel = "";
    protected AtomicBoolean isRunning = null;

    protected long loopDelay = 1000;
    protected String commandName = "";

    /**
     * Constructor method.
     * @param bot           The bot to which this command belongs.
     * @param commandName   The name of this command.  This should be unique to its channel.
     * @param channel       The channel that this command will run on.
     */
    public jIBCommandThread(PircBot bot, String commandName, String channel) {
        this(bot, commandName, channel, 1000);
    }

    /**
     * Constructor method.
     * @param bot           The bot to which this command belongs.
     * @param commandName   The name of this command.  This should be unique to its channel.
     * @param channel       The channel that this command will run on.
     * @param delay         The delay between times that the "loop()" method is called.
     */
    public jIBCommandThread(PircBot bot, String commandName, String channel, long delay) {
        this(bot, commandName, channel, delay, true);
    }

    /**
     * Constructor method.
     * @param bot           The bot to which this command belongs.
     * @param commandName   The name of this command.  This should be unique to its channel.
     * @param channel       The channel that this command will run on.
     * @param delay         The delay between times that the "loop()" method is called.
     * @param isRunning     Does this command start out running?
     */
    public jIBCommandThread(PircBot bot, String commandName, String channel,
            long delay, boolean isRunning) {
        this.bot = bot;
        this.commandName = commandName;
        this.channel = channel;
        this.loopDelay = delay;
        this.isRunning = new AtomicBoolean(isRunning);
    }
    
    /**
     * This is a wrapper for the bot.sendMessage command.  It automatically sends any
     * messages to the correct channel.  It also acts as a way to prevent the command
     * from sending messages before it is connected to the server.
     * 
     * @param message   A text message to send to the channel the command is based in.
     */
    public void sendMessage(String message) {
        if (bot.isConnected()) // Warning! This does not check to see if we are in channel.
        {
            bot.sendMessage(channel, message);
        } else {
            bot.log("WARNING! Bot not connected, tried to send: " + message);
        }
    }

    /**
     * This method sets the command running and calls the "loop" method after set "delay"
     * periods.
     */
    public void run() {
        setIsRunning(true); // when run is called, make sure that the running variable is true.
        bot.log("Started " + getCommandName());
        while (getIsRunning()) {
            loop();
            try {
                Thread.sleep(loopDelay);
            } catch (InterruptedException e) {
                bot.log("WARNING! - commandThread " + getCommandName()
                        + " interupted.\n" + e.toString());
            }
        }
        bot.log("Stopped " + getCommandName());
    }

    /**
     * This method is run every "delay" milliseconds.
     */
    protected abstract void loop();

    /**
     * This method is used to safely stop the commandThread.
     */
    public void stop() {
        bot.log("Stopping " + getCommandName());
        setIsRunning(false);
    }

    /**
     * This method is used to check if the commandThread is still running.
     * @return  True if the command thread is running.
     */
    public boolean getIsRunning() {
        // WARNING! This method is accessed from multiple threads
        //          and therefore implements an AtomicBoolean.
        return isRunning.get();
    }

    /**
     * This method safely changes the value of the isRunning private variable.
     * @param running   New internal status for the thread. True means it is
     *                  running, false means it is stopped.
     */
    private void setIsRunning(boolean running) {
        // WARNING! This method is accessed from multiple threads
        //          and therefore implements an AtomicBoolean.
        isRunning.set(running);
    }

    /**
     * Gets the channel the commandThread is running in.
     * @return
     */
    public String getChannel() {
        return channel;
    }

    /** 
     * Gets this commandThreads name.
     */
    public String getCommandName() {
        //TODO: commandThread::getCommandName() - Figure out a method for unique naming commandThreads.
        return commandName + getChannel();
    }
    
    /**
     * Returns a simple command name, minus the channel.
     * @return
     */
    public String getSimpleCommandName() {
    	return commandName;
    }
}
