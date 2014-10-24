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
package org.annotopia.grails.connectors.model

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class Connector {

	protected static final int NAME_MAX_SIZE = 255;
	protected static final int DESCRIPTION_MAX_SIZE = 1024;
	
	String ver;
	String name;	
	String description;

	static constraints = {
		ver (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		name (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		description (nullable: true, blank: true, maxSize:DESCRIPTION_MAX_SIZE)
	}
}