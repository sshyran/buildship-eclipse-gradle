/*******************************************************************************
 * Copyright (c) 2022 Gradle Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.buildship.core.internal;

/**
 * The used JVM is not supported by the current Gradle build.
 */
public class UnsupportedJavaVersionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedJavaVersionException(String message) {
        super(message);
    }
}
