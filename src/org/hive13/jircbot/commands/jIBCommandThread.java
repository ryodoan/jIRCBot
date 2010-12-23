package org.hive13.jircbot.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import org.hive13.jircbot.jIRCBot;
import org.hive13.jircbot.jIRCBot.eLogLevel;
import org.hive13.jircbot.support.jIRCTools.eMsgTypes;
import org.hive13.jircbot.support.jIRCUser.eAuthLevels;

/**
 * This abstract class framework is used for implementing asynchronous commands
 * that run in the background and can react to externally generated events.
 * 
 * @author vincenpt
 */
public abstract class jIBCommandThread extends jIBCommand {
	protected jIRCBot bot = null;
	protected String commandName = "";
	protected String channel = "";
	protected long loopDelay = 30000; // 30 seconds

	protected commandThreadRunnable commandThreadChild = null;

	/**
	 * Initializes a new CommandThread with a default loopDelay of 30 seconds.
	 * 
	 * @param bot
	 *            Bot that will be running the command thread.
	 * @param commandName
	 *            The base name for this command.
	 * @param channel
	 *            The channel that this command is active in.
	 */
	public jIBCommandThread(jIRCBot bot, String commandName, String channel) {
		this(bot, commandName, channel, 30000, eAuthLevels.unauthorized);
	}

	/**
	 * Initializes a new CommandThread.
	 * 
	 * @param bot
	 *            Bot that will be running the command thread.
	 * @param commandName
	 *            The base name for this command.
	 * @param channel
	 *            The channel that this command is active in.
	 * @param loopDelay
	 *            The time between calls to 'loop()'
	 * @param authLevel
	 * 			  The level of authentication required to run
	 * 			  this command.
	 */
	public jIBCommandThread(jIRCBot bot, String commandName, String channel,
			long loopDelay, eAuthLevels authLevel) {
		super(authLevel);
		this.bot = bot;
		this.commandName = commandName;
		this.channel = channel;
		this.loopDelay = loopDelay;
	}

	/**
	 * This method is run every "loopDelay" milliseconds. WARNING! This method
	 * WILL be called by asynchronous threads. Everything this function touches
	 * MUST be thread safe.
	 */
	public abstract void loop();

	@Override
	public void handleMessage(jIRCBot bot, String channel, String sender,
			String message) {
		if (channel.equals(this.channel)) {
			if (message.equals("stop")) {
				stopCommandThread();
			} else if (message.equals("start")){
				startCommandThread();
			}
		} else {
			this.bot.log("commandThread - handleMessage called for channel: "
					+ channel + " when cmdThrd is actually in " + this.channel,
					eLogLevel.warning);
		}
	}

	/**
	 * This is a wrapper for the bot.sendMessage command. It automatically sends
	 * any messages to the correct channel. It also acts as a way to prevent the
	 * command from sending messages before it is connected to the server.
	 * 
	 * @param message
	 *            A text message to send to the channel the command is based in.
	 */
	public void sendMessage(String message) {
		this.sendMessage(message, eMsgTypes.LogFreeMsg);
	}
	
	/**
	 * This is a wrapper for the bot.sendMessage command. It automatically sends
	 * any messages to the correct channel. It also acts as a way to prevent the
	 * command from sending messages before it is connected to the server.
	 * 
	 * @param message
	 *            A text message to send to the channel the command is based in.
	 */
	public void sendMessage(String message, eMsgTypes msgType) {
		if (bot.isConnected()) {
			bot.sendMessage(channel, message, msgType);
		} else {
			bot.log("Bot not connected, tried to send: " + message,
					eLogLevel.warning);
		}
	}

	/**
	 * This method will safely start an instance of the command thread. If an
	 * instance of the commandThread exists but is not running, it restarts that
	 * instance.
	 */
	public void startCommandThread() {
		if (commandThreadChild == null) {
			commandThreadChild = new commandThreadRunnable(loopDelay);
		}
		if (!commandThreadChild.getIsRunning()) {
			sendMessage("Starting the " + getSimpleCommandName() + " command thread.", eMsgTypes.publicMsg);
			new Thread(commandThreadChild).start();
		} else {
			sendMessage("The " + getSimpleCommandName() + " command thread is already running.", eMsgTypes.publicMsg);
		}
	}

	/**
	 * This method is more of a 'pause' commandThread. It stops the running java
	 * Thread but does not delete the instance of the commandThread.
	 */
	public void stopCommandThread() {
		if (commandThreadChild != null && commandThreadChild.getIsRunning()) {
			sendMessage("Stopping the " + getSimpleCommandName() + " command thread.", eMsgTypes.publicMsg);
			commandThreadChild.stop();
		} else {
			sendMessage("The " + getSimpleCommandName() + " command thread is not running.", eMsgTypes.publicMsg);
		}
	}

	@Override
	public String getCommandName() {
		return commandName + getChannel();
	}

	/**
	 * Gets the channel the commandThread is running in.
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Returns a simple command name, minus the channel.
	 */
	public String getSimpleCommandName() {
		return commandName;
	}

	protected class commandThreadRunnable implements Runnable {
		private AtomicBoolean isRunning;
		private long delay;

		public commandThreadRunnable(long delay) {
			this.isRunning = new AtomicBoolean(false);
			this.delay = delay;
		}

		@Override
		public void run() {
			setIsRunning(true);
			bot.log("Started " + getCommandName(), eLogLevel.info);
			while (getIsRunning()) {
				loop();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					bot.log("commandThread " + getCommandName()
							+ " interrupted.\n", eLogLevel.error);
					e.printStackTrace();
				}

			}
			setIsRunning(false);
			bot.log("Stopped " + getCommandName(), eLogLevel.info);

		}

		public void stop() {
			bot.log("Stopping " + getCommandName(), eLogLevel.info);
			setIsRunning(false);
		}

		public boolean getIsRunning() {
			return isRunning.get();
		}

		private void setIsRunning(boolean isRunning) {
			this.isRunning.set(isRunning);
		}

	}

}
