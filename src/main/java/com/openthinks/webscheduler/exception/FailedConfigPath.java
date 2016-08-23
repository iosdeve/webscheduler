/**   
 *  Licensed to the Apache Software Foundation (ASF) under one
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
 *
* @Title: FailedConfigPath.java 
* @Package com.openthinks.webscheduler.exception 
* @Description: TODO
* @author dailey.yet@outlook.com  
* @date Aug 23, 2016
* @version V1.0   
*/
package com.openthinks.webscheduler.exception;

/**
 * @author dailey.yet@outlook.com
 *
 */
public class FailedConfigPath extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 861573268432194580L;

	/**
	 * 
	 */
	public FailedConfigPath() {
	}

	/**
	 * @param message
	 */
	public FailedConfigPath(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FailedConfigPath(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FailedConfigPath(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public FailedConfigPath(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
