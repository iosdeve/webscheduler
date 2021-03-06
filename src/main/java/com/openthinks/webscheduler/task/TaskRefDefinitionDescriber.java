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
* @Title: TaskRefDefinitionDescriber.java 
* @Package com.openthinks.webscheduler.model.task 
* @Description: TODO
* @author dailey.yet@outlook.com  
* @date Jul 25, 2016
* @version V1.0   
*/
package com.openthinks.webscheduler.task;

import com.openthinks.libs.utilities.InstanceUtilities;
import com.openthinks.webscheduler.model.task.ITaskRef;

/**
 * Description for {@link ITaskRef}, include the type class of {@link ITaskRef} , description details and required or not for associated {@link ITaskDefinition}
 * @author dailey.yet@outlook.com
 *
 */
public class TaskRefDefinitionDescriber {
	private Class<? extends ITaskRef> taskRefClass;
	private StringBuilder description;
	private boolean required;

	public <T extends ITaskRef> TaskRefDefinitionDescriber(Class<T> taskRefClass) {
		super();
		this.taskRefClass = taskRefClass;
		this.description = new StringBuilder();
		this.required = false;
	}

	public <T extends ITaskRef> TaskRefDefinitionDescriber(Class<T> taskRefClass, String description) {
		super();
		this.taskRefClass = taskRefClass;
		this.description = new StringBuilder(description);
		this.required = false;
	}

	/**
	 * get the class for this {@link ITaskRef}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ITaskRef> Class<T> getTaskRefClass() {
		return (Class<T>) this.taskRefClass;
	}

	/**
	 * get standard {@link ITaskRef} configure items example
	 * @return String
	 */
	public String getDescription() {
		return description.toString();
	}

	public void setDescription(String description) {
		this.description = new StringBuilder(description);
	}

	public <T extends ITaskRef> void setTaskRefClass(Class<T> taskRefClass) {
		this.taskRefClass = taskRefClass;
	}

	/**
	 * this task definition configuration is required or not
	 * @return boolean
	 */
	public boolean isRequired() {
		return required;
	}

	public TaskRefDefinitionDescriber setRequired(boolean required) {
		this.required = required;
		return this;
	}

	public TaskRefDefinitionDescriber push(String itemDesc) {
		description.append(itemDesc);
		description.append("\r\n");
		return this;
	}

	/**
	 * instance {@link ITaskRef} by {@link #getTaskRefClass()}
	 * @return ITaskRef or null when instance error
	 */
	public <T extends ITaskRef> T createTaskRef() {
		T taskRef;
		try {
			taskRef = InstanceUtilities.create(getTaskRefClass(), null);
		} catch (Exception e) {
			taskRef = null;
		}
		return taskRef;
	}
}
