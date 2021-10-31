/*
 * semanticcms-core-resources - Redistributable sets of SemanticCMS resources.
 * Copyright (C) 2017, 2021  AO Industries, Inc.
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
 * along with semanticcms-core-resources.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.semanticcms.core.resources;

import com.aoapps.lang.NullArgumentException;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An active connection to a resource.
 * ResourceConnections should be short-lived, whereas {@link Resource} may be long-lived.
 * ResourceConnections are not thread-safe, whereas {@link Resource} are.
 * <p>
 * TODO: interface + abstract base, or default interface methods once on Java 1.8?
 * </p>
 *
 * @see  Resource#open
 */
public abstract class ResourceConnection implements Closeable {

	protected final Resource resource; // TODO: Worth having this reference back to resource?

	protected ResourceConnection(Resource resource) {
		this.resource = NullArgumentException.checkNotNull(resource, "resource");
	}

	@Override
	public String toString() {
		return "Connection to " + resource.toString();
	}

	/**
	 * The {@link Resource} that provides this connection.
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Checks if this resource exists.
	 *
	 * @throws  IOException  if I/O error
	 * @throws  IllegalStateException  if already closed
	 */
	public abstract boolean exists() throws IOException, IllegalStateException;

	/**
	 * Gets the length of this resource or {@code -1} if unknown.
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  if already closed
	 */
	public abstract long getLength() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Gets the last modified time of this resource or {@code 0} if unknown.
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  if already closed
	 */
	public abstract long getLastModified() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Opens this resource for reading.  The stream may only be opened once per connection.
	 * May not get the stream if {@link #getFile()} has been called.
	 * <p>
	 * When requiring reading the stream more than once, please use {@link #getFile()}
	 * to fetch the resource once, then perform direct file I/O on the local file.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  if already closed, the stream has already been accessed,
	 *                                 or {@link #getFile()} has been accessed.
	 *
	 * @see  #getFile()
	 */
	public abstract InputStream getInputStream() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Gets a {@link File} for this resource.  This may be called multiple times
	 * and will get the same {@link File}.  May not get the file if
	 * {@link #getInputStream()} has been called.
	 * <p>
	 * When the resource exists locally, this will be a direct reference to the
	 * resource.  When the resource exists remotely or is otherwise not directly
	 * accessible, this may require fetching the resource contents into a
	 * temporary file.  Any temporary files will be deleted on {@link #close()}.
	 * </p>
	 * <p>
	 * Use this when having a {@link File} is a hard requirement, and not merely
	 * a convenience or optimization.  Use {@link Resource#getFile()} when a {@link File}
	 * is optional.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  if already closed or {@link #getInputStream()} has been accessed.
	 *
	 * @see  #getInputStream()
	 * @see  Resource#getFile()
	 * @see  #close()
	 */
	public abstract File getFile() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Closes access to this resource.
	 * Also closes any {@link InputStream} and removes any temporary {@link File} associated with this resource.
	 *
	 * @throws  IOException  if I/O error occurs
	 *
	 * @see  #getFile()
	 */
	@Override
	public abstract void close() throws IOException;
}
