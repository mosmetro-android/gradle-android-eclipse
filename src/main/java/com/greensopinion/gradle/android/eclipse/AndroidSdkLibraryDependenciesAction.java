/**
 * Copyright 2017 David Green
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greensopinion.gradle.android.eclipse;

import java.lang.reflect.InvocationTargetException;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.Library;
import org.gradle.plugins.ide.eclipse.model.internal.FileReferenceFactory;

public class AndroidSdkLibraryDependenciesAction implements Action<Classpath> {
	private final Project project;

	public AndroidSdkLibraryDependenciesAction(Project project) {
		this.project = project;
	}

	@Override
	public void execute(Classpath classpath) {
		Log.log().info("Adding Android SDK classpath entry");
		classpath.getEntries().add(androidSdkEntry());
	}

	private ClasspathEntry androidSdkEntry() {
		FileReferenceFactory fileReferenceFactory = new FileReferenceFactory();
		Object compileSdkVersion = getAndroidProperty("compileSdkVersion");
		Object sdkDirectory = getAndroidProperty("sdkDirectory");
		Library library = new Library(
				fileReferenceFactory.fromPath(sdkDirectory + "/platforms/" + compileSdkVersion + "/android.jar"));
		library.setSourcePath(fileReferenceFactory.fromPath(sdkDirectory + "/sources/" + compileSdkVersion));
		return library;
	}

	private Object getAndroidProperty(String key) {
		Object android = project.property("android");
		String getter = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
		try {
			return android.getClass().getMethod(getter).invoke(android);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Cannot get '" + key + "' property of 'android'.", e);
		}
	}
}
