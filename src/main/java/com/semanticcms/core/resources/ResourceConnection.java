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

import com.aoindustries.lang.NullArgumentException;
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
abstract public class ResourceConnection implements Closeable {

	protected final Resource resource; // TODO: Worth having this reference back to resource?

	public ResourceConnection(Resource resource) {
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
	 * @throws  IllegalStateException  is already closed
	 */
	abstract public boolean exists() throws IOException, IllegalStateException;

	/**
	 * Gets the length of this resource or {@code -1} if unknown.
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  is already closed
	 */
	abstract public long getLength() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Gets the last modified time of this resource or {@code 0} if unknown.
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  is already closed
	 */
	abstract public long getLastModified() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Opens this resource for reading.  The stream may only be opened once per connection.
	 * <p>
	 * When requiring reading the stream more than once, please use {@link #getResourceFile()}
	 * to fetch the resource once, then perform direct file I/O on the local file.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  if already closed or the stream has already been accessed
	 */
	abstract public InputStream getInputStream() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Gets a {@link File} for this resource.  When the resource exists locally, this will
	 * be a direct reference to the resource.  When the resource exists remotely or is otherwise
	 * not directly accessible, this may require fetching the resource contents into a temporary
	 * file.  To allow prompt cleanup of any temporary files, call {@link ResourceFile#close()}
	 * when done with the file.
	 * <p>
	 * Use this when having a {@link File} is a hard requirement, and not merely a convenience
	 * or optimization.  Use {@link #getFile()} when a {@link File} is optional.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 * @throws  IllegalStateException  is already closed
	 *
	 * @see  #getFile()
	 * @see  #close()
	 */
	abstract public ResourceFile getResourceFile() throws IOException, FileNotFoundException, IllegalStateException;

	/**
	 * Closes access to this resource.
	 * Also closes any {@link InputStream} or {@link ResourceFile} opened with this resource.
	 *
	 * @throws  IOException  if I/O error occurs
	 *
	 * @see  #getResourceFile()
	 */
	@Override
	abstract public void close() throws IOException;
}
