/*
 * Copyright 2014 Massachusetts General Hospital
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.annotopia.grails.connectors

import grails.converters.JSON
import groovy.sql.DataSet

import java.text.SimpleDateFormat

import javax.servlet.http.HttpServletRequest


/**
 * Basic methods for storage controllers.
 * 
 * Note: the references to grailsApplication and log work because of inheritance with the actual controller.
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BaseConnectorController {

	// Date format for all Open Annotation date content
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
	
	/** Retrieve the API key value.
	 * @param startTime The time this execution was started.
	 * @return The value of the API key, or null if it is not set. */
	private String retrieveApiKey(final long startTime) {
		// retrieve the API key
		def apiKey = retrieveValue(request.JSON.apiKey, params.apiKey,
				"Missing required parameter 'apiKey'.", startTime);
		if(!apiKeyAuthenticationService.isApiKeyValid(request.getRemoteAddr( ), apiKey)) {
			invalidApiKey(request.getRemoteAddr( ));
			return null;
		}
		
		return apiKey;
	}
	
	/**
	 * Logging and message for invalid API key.
	 * @param ip	Ip of the client that issued the request
	 */
	public void invalidApiKey(def ip) {
		log.warn("Unauthorized request performed by IP: " + ip)
		def json = JSON.parse('{"status":"rejected" ,"message":"Api Key missing or invalid"}');
		render(status: 401, text: json, contentType: "text/json", encoding: "UTF-8");
	}
	
	/** Retrieve the user parameter.
	 * @param requestParam The parameter if it exists in the POST data.
	 * @param param The parameter if it exists in the URL.
	 * @param defaultValue The default value to use if the parameter is not set.
	 * @return The value of this parameter. */
	private String retrieveValue(final String requestParam, final String param,
			final String defaultValue)
	{
		if(requestParam != null) {
			return requestParam;
		} else if(param != null) {
			return URLDecoder.decode(param, MiscUtils.DEFAULT_ENCODING);
		}
		return defaultValue;
	}
	
	/** Retrieve the user parameter.
	 * @param requestParam The parameter if it exists in the POST data.
	 * @param param The parameter if it exists in the URL.
	 * @param name The name of this parameter.
	 * @param startTime The time this execution was started.
	 * @return The value of this parameter, or null if it is not set. */
	private String retrieveValue(final String requestParam, final String param, final String name,
			final long startTime)
	{
		def result;
		if(requestParam != null) {
			result = requestParam;
		} else if(param != null) {
			result = URLDecoder.decode(param, MiscUtils.DEFAULT_ENCODING);
		} else {
			error(400, "Missing required parameter '" + name + "'.", startTime);
			return null;
		}
		
		if(result.isEmpty( )) {
			error(400, "Parameter '" + name + "' is empty.", startTime);
		}
		return result;
	}
	
	/**
	 * Returns the current URL.
	 * @param request 	The HTTP request
	 * @return	The current URL
	 */
	public String getCurrentUrl(HttpServletRequest request){
		StringBuilder sb = new StringBuilder()
		int fromIndex = 7;
		if(configAccessService.getAsString("grails.server.protocol").equals("https")) {fromIndex = 8;}
		sb << request.getRequestURL().substring(0,request.getRequestURL().indexOf("/", fromIndex))
		sb << request.getAttribute("javax.servlet.forward.request_uri")
		if(request.getAttribute("javax.servlet.forward.query_string")){
			sb << "?"
			sb << request.getAttribute("javax.servlet.forward.query_string")
		}
		return sb.toString();
	}
	
	private InputStream callExternalUrl(def apiKey, String URL) {
		Proxy httpProxy = null;
		if(connectorsConfigAccessService.isProxyDefined()) {
			String proxyHost = connectorsConfigAccessService.getProxyIp(); //replace with your proxy server name or IP
			int proxyPort = connectorsConfigAccessService.getProxyPort(); //your proxy server port
			SocketAddress addr = new InetSocketAddress(proxyHost, proxyPort);
			httpProxy = new Proxy(Proxy.Type.HTTP, addr);
		}
		
		if(httpProxy!=null) {
			long startTime = System.currentTimeMillis();
			log.info ("[" + apiKey + "] " + "Proxy request: " + URL);
			URL url = new URL(URL);
			//Pass the Proxy instance defined above, to the openConnection() method
			URLConnection urlConn = url.openConnection(httpProxy);
			urlConn.connect();
			log.info ("[" + apiKey + "] " + "Proxy resolved in (" + (System.currentTimeMillis()-startTime) + "ms)");
			return urlConn.getInputStream();
		} else {
			log.info ("[" + apiKey + "] " + "No proxy request: " + URL);
			return new URL(URL).openStream();
		}
	}
	
	/**
	 * Creates a JSON message for the response.
	 * @param apiKey	The API key of the client that issued the request
	 * @param status	The status of the response
	 * @param message	The message of the response
	 * @param startTime	The start time to calculate the duration
	 * @return The JSON message
	 */
	public returnMessage(def apiKey, def status, def message, def startTime) {
		log.info("[" + apiKey + "] " + message);
		return JSON.parse('{"status":"' + status + '","message":"' + message +
			'","duration": "' + (System.currentTimeMillis()-startTime) + 'ms", ' + '}');
	}
	
	/** Generate an error with the specified details.
	 * @param code The HTTP error code to use.
	 * @param message The error message to report to the user.
	 * @param startTime The time the web service call was initiated. */
	private void error(final int code, final String message, final long startTime) {
		log.error(message);
		render(
			status: code,
			text: returnMessage("", "nocontent", message.replace("\"", "\\\""), startTime),
			contentType: "text/json",
			encoding: "UTF-8"
		);
	}
}
