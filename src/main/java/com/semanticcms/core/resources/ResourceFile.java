/*
 * semanticcms-core-resources - Redistributable sets of SemanticCMS resources.
 * Copyright (C) 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-core-resources.
 *
 * semanticcms-core-resources is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-core-resources is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-core-resources.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.core.resources;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Access to a {@link Resource} in {@link File} form.
 * <p>
 * Not thread safe.
 * </p>
 *
 * @see  ResourceRef
 */
public interface ResourceFile extends Closeable {

	/**
	 * Gets the file or {@code null} if already closed.
	 */
	File getFile();

	/**
	 * Closes this resource file, releasing any underlying system resources.
	 */
	@Override
	void close() throws IOException;
}
