/*******************************************************************************
 * Copyright (c) 2013, 2014 Pivotal Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.flux.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.flux.client.IMessageHandler;
import org.eclipse.flux.client.MessageConnector;
import org.eclipse.flux.client.MessageHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Martin Lippert
 */
public class LiveEditCoordinator {
	
	private MessageConnector messagingConnector;
	private Collection<ILiveEditConnector> liveEditConnectors;
	private Collection<IMessageHandler> messageHandlers;
	
	public LiveEditCoordinator(MessageConnector messagingConnector) {
		this.messagingConnector = messagingConnector;
		this.liveEditConnectors = new CopyOnWriteArrayList<>();
		this.messageHandlers = new ArrayList<IMessageHandler>(4);
		
		IMessageHandler startLiveUnit = createStartLiveUnitMessageHandler();
		messagingConnector.addMessageHandler(startLiveUnit);
		messageHandlers.add(startLiveUnit);
		
		IMessageHandler startLiveUnitResponse = createStartLiveUnitResponseMessageHandler();
		messagingConnector.addMessageHandler(startLiveUnitResponse);
		messageHandlers.add(startLiveUnitResponse);
		
		IMessageHandler modelChangedHandler = createModelChangedHandler();
		messagingConnector.addMessageHandler(modelChangedHandler);
		messageHandlers.add(modelChangedHandler);
		
		// Listen to the internal broadcast channel to send out info about current live edit units
		IMessageHandler liveResourceRequestUnits = createLiveResourceRequestUnitsMessageHandler();
		messagingConnector.addMessageHandler(liveResourceRequestUnits);
		messageHandlers.add(liveResourceRequestUnits);
	}

	private IMessageHandler createLiveResourceRequestUnitsMessageHandler() {
		IMessageHandler liveResourceRequestUnits = new MessageHandler("getLiveResourcesRequest") {
			@Override
			public void handle(String messageType, JSONObject message) {
				sendLiveUnits(message);
			}
		};
		return liveResourceRequestUnits;
	}

	private IMessageHandler createModelChangedHandler() {
		IMessageHandler modelChangedHandler = new MessageHandler("liveResourceChanged") {
			@Override
			public void handle(String messageType, JSONObject message) {
				modelChanged(message);
			}
		};
		return modelChangedHandler;
	}

	private IMessageHandler createStartLiveUnitMessageHandler() {
		IMessageHandler startLiveUnit = new MessageHandler("liveResourceStarted") {
			@Override
			public void handle(String messageType, JSONObject message) {
				startLiveUnit(message);
			}
		};
		return startLiveUnit;
	}

	private IMessageHandler createStartLiveUnitResponseMessageHandler() {
		IMessageHandler startLiveUnitResponse = new MessageHandler("liveResourceStartedResponse") {
			@Override
			public void handle(String messageType, JSONObject message) {
				startLiveUnitResponse(message);
			}
		};
		return startLiveUnitResponse;
	}
	
	protected void startLiveUnit(JSONObject message) {
		try {
			String requestSenderID = message.getString("requestSenderID");
			int callbackID = message.getInt("callback_id");
			String username = message.getString("username");
			String projectName = message.getString("project");
			String resourcePath = message.getString("resource");
			String hash = message.getString("hash");
			long timestamp = message.getLong("timestamp");

			String liveEditID = projectName + "/" + resourcePath;
			for (ILiveEditConnector connector : liveEditConnectors) {
				connector.liveEditingStarted(requestSenderID, callbackID, username, liveEditID, hash, timestamp);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void startLiveUnitResponse(JSONObject message) {
		try {
			String requestSenderID = message.getString("requestSenderID");
			int callbackID = message.getInt("callback_id");
			String username = message.getString("username");
			String projectName = message.getString("project");
			String resourcePath = message.getString("resource");
			String savePointHash = message.getString("savePointHash");
			long savePointTimestamp = message.getLong("savePointTimestamp");
			String content = message.getString("liveContent");

			for (ILiveEditConnector connector : liveEditConnectors) {
				connector.liveEditingStartedResponse(requestSenderID, callbackID, username, projectName, resourcePath, savePointHash, savePointTimestamp, content);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void modelChanged(JSONObject message) {
		try {
			String username = message.getString("username");
			String projectName = message.getString("project");
			String resourcePath = message.getString("resource");

			int offset = message.getInt("offset");
			int removedCharCount = message.getInt("removedCharCount");
			String addedChars = message.has("addedCharacters") ? message.getString("addedCharacters") : "";

			String liveEditID = projectName + "/" + resourcePath;

			for (ILiveEditConnector connector : liveEditConnectors) {
				connector.liveEditingEvent(username, liveEditID, offset, removedCharCount, addedChars);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void sendLiveUnits(JSONObject message) {
		try {
			String username = message.has("username") ? message.getString("username") : null;
			String requestSenderID = message.getString("requestSenderID");
			String projectRegEx = message.has("projectRegEx") ? message.getString("projectRegEx") : null;
			String resourceRegEx = message.has("resourceRegEx") ? message.getString("resourceRegEx") : null;
			int callbackID = message.getInt("callback_id");
			for (ILiveEditConnector connector : liveEditConnectors) {
				connector.liveEditors(requestSenderID, callbackID, username, projectRegEx, resourceRegEx);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addLiveEditConnector(ILiveEditConnector connector) {
		liveEditConnectors.add(connector);
	}
	
	public void removeLiveEditConnector(ILiveEditConnector connector) {
		liveEditConnectors.remove(connector);
	}
	
	public void sendModelChangedMessage(String changeOriginID, String username, String projectName, String resourcePath, int offset, int removedCharactersCount, String newText) {
		try {
			JSONObject message = new JSONObject();
			message.put("username", username);
			message.put("project", projectName);
			message.put("resource", resourcePath);
			message.put("offset", offset);
			message.put("offset", offset);
			message.put("removedCharCount", removedCharactersCount);
			message.put("addedCharacters", newText != null ? newText : "");

			this.messagingConnector.send("liveResourceChanged", message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		String fullResourcePath = projectName + "/" + resourcePath;
		
		for (ILiveEditConnector connector : this.liveEditConnectors) {
			if (!connector.getConnectorID().equals(changeOriginID)) {
				connector.liveEditingEvent(username, fullResourcePath, offset, removedCharactersCount, newText);
			}
		}
	}

	public void sendLiveEditStartedMessage(String changeOriginID, String username, String projectName, String resourcePath, String hash, long timestamp) {
		try {
			JSONObject message = new JSONObject();
			message.put("callback_id", 0);
			message.put("username", username);
			message.put("project", projectName);
			message.put("resource", resourcePath);
			message.put("hash", hash);
			message.put("timestamp", timestamp);
			
			this.messagingConnector.send("liveResourceStarted", message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		String path = projectName + '/' + resourcePath;
		for (ILiveEditConnector connector : this.liveEditConnectors) {
			if (!connector.getConnectorID().equals(changeOriginID)) {
				connector.liveEditingStarted("local", 0, username, path, hash, timestamp);
			}
		}
	}
	
	public void sendLiveEditStartedResponse(String responseOriginID, String requestSenderID, int callbackID, String username, String projectName, String resourcePath, String savePointHash, long savePointTimestamp, String content) {
		try {
			JSONObject message = new JSONObject();
			message.put("requestSenderID", requestSenderID);
			message.put("callback_id", callbackID);
			message.put("username", username);
			message.put("project", projectName);
			message.put("resource", resourcePath);
			message.put("savePointTimestamp", savePointTimestamp);
			message.put("savePointHash", savePointHash);
			message.put("liveContent", content);
	
			this.messagingConnector.send("liveResourceStartedResponse", message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ILiveEditConnector connector : this.liveEditConnectors) {
			if (!connector.getConnectorID().equals(responseOriginID)) {
				connector.liveEditingStartedResponse(requestSenderID, callbackID, username, projectName, resourcePath, savePointHash, savePointTimestamp, content);
			}
		}
	}
	
	public void sendLiveResourcesResponse(String requestSenderID,
			int callbackID, String username,
			Map<String, List<ResourceData>> liveUnits) {
		// Don't send anything if there is nothing to send
		if (liveUnits.isEmpty()) {
			return;
		}
		try {
			JSONObject message = new JSONObject();
			message.put("requestSenderID", requestSenderID);
			message.put("callback_id", callbackID);
			message.put("username", username);
			JSONObject liveEditUnits = new JSONObject();
			for (Map.Entry<String, List<ResourceData>> entry : liveUnits.entrySet()) {
				liveEditUnits.put(entry.getKey(), new JSONArray(entry.getValue()));
			}
			message.put("liveEditUnits", liveEditUnits);

			this.messagingConnector.send("getLiveResourcesResponse", message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class ResourceData extends JSONObject {

		public ResourceData(String path, String hash, long timestamp) {
			super();
			try {
				put("resource", path);
				put("savePointHash", hash);
				put("savePointTimestamp", timestamp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void dispose() {
		for (IMessageHandler messageHanlder : messageHandlers) {
			messagingConnector.removeMessageHandler(messageHanlder);
		}
	}

}
