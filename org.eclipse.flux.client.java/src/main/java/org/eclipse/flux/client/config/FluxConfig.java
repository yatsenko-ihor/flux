/*******************************************************************************
 * Copyright (c) 2014 Pivotal Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.flux.client.config;

import org.eclipse.flux.client.FluxClient;
import org.eclipse.flux.client.MessageConnector;


/**
 * FluxConfig contains information needed to create a connection to flux bus.
 * <p>
 * This is a marker interface, allowing for different classes specific to 
 * specific clients to implement whatever kind of data objects they need.
 * 
 * @author Kris De Volder
 */
public interface FluxConfig {

	MessageConnector connect(FluxClient fluxClient);
	String getUser();

	/**
	 * Convert this config into a equivalent SocketIO config, thus allowing
	 * a client running in an environment that does not have direct access
	 * to RabbitMQ to connect using the websocket-based implementation.
	 */
	SocketIOFluxConfig toSocketIO();
	
}
