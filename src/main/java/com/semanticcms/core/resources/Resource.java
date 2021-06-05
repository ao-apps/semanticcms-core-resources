/*
 * semanticcms-core-resources - Redistributable sets of SemanticCMS resources.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.lang.NullArgumentException;
import com.aoapps.net.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An abstract handle used to access the contents of a resource within a book.
 * Resources may be long-lived, whereas {@link ResourceConnection} should be short-lived.
 * Resources are thread-safe, whereas {@link ResourceConnection} are not.
 *
 * TODO: interface + abstract base, or default interface methods once on Java 1.8?
 */
abstract public class Resource {

	protected final ResourceStore store; // TODO: Worth having this reference back to store?
	protected final Path path;

	public Resource(ResourceStore store, Path path) {
		this.store = NullArgumentException.checkNotNull(store, "store");
		this.path = NullArgumentException.checkNotNull(path, "path");
	}

	/**
	 * The equality of a resource is based on equality of both
	 * {@link #getStore() store} and {@link #getPath() path}.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Resource)) return false;
		Resource other = (Resource)obj;
		return
			store.equals(other.store)
			&& path.equals(other.path)
		;
	}

	/**
	 * The hash code of a resource is based on equality of both
	 * {@link #getStore() store} and {@link #getPath() path}.
	 */
	@Override
	public int hashCode() {
		return store.hashCode() * 31 + path.hashCode();
	}

	/**
	 * Generated from {@link ResourceStore#toString()} and {@link #path}.
	 * <p>
	 * <b>Implementation Note:</b><br>
	 * When the {@link ResourceStore#toString()} ends with ":",
	 * concatenates {@link ResourceStore#toString()} and {@link #path}.
	 * Otherwise, concatenates {@link ResourceStore#toString()}, {@code '!'}, and {@link #path}.
	 * </p>
	 */
	@Override
	public String toString() {
		String storeString = store.toString();
		if(storeString.endsWith(":")) {
			return storeString + path;
		} else {
			return storeString + '!' + path;
		}
	}

	/**
	 * The {@link ResourceStore} that provides this resource.
	 */
	public ResourceStore getStore() {
		return store;
	}

	/**
	 * The path that refers to this resource.
	 */
	public Path getPath() {
		return path;
	}

	// TODO: Snapshot option, to ensure consistency between exists, length, reading data, and repeatable reads, or at least help performance
	//       on consecutive operations?

	/**
	 * Checks if this resource exists.
	 * <p>
	 * This method opens and closes non-local-file resources.
	 * If consecutive operations will be done on the resource, use {@link #open()}
	 * to obtain a {@link ResourceConnection}.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 *
	 * @see  #isFilePreferred()
	 * @see  #getFile()
	 * @see  #open()
	 * @see  ResourceConnection#exists()
	 */
	public boolean exists() throws IOException {
		File file = isFilePreferred() ? getFile() : null;
		if(file != null) {
			return file.exists();
		} else {
			try (ResourceConnection conn = open()) {
				return conn.exists();
			}
		}
	}

	/**
	 * Gets the length of this resource or {@code -1} if unknown.
	 * <p>
	 * This method opens and closes non-local-file resources.
	 * If consecutive operations will be done on the resource, use {@link #open()}
	 * to obtain a {@link ResourceConnection}.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 *
	 * @see  #isFilePreferred()
	 * @see  #getFile()
	 * @see  #open()
	 * @see  ResourceConnection#getLength()
	 */
	public long getLength() throws IOException, FileNotFoundException {
		File file = isFilePreferred() ? getFile() : null;
		if(file != null) {
			if(!file.exists()) throw new FileNotFoundException(file.getPath());
			// TODO: Handle 0 as unknown to convert to -1: Files.readAttributes
			return file.length();
		} else {
			try (ResourceConnection conn = open()) {
				return conn.getLength();
			}
		}
	}

	/**
	 * Gets the last modified time of this resource or {@code 0} if unknown.
	 * <p>
	 * This method opens and closes non-local-file resources.
	 * If consecutive operations will be done on the resource, use {@link #open()}
	 * to obtain a {@link ResourceConnection}.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 *
	 * @see  #isFilePreferred()
	 * @see  #getFile()
	 * @see  #open()
	 * @see  ResourceConnection#getLastModified()
	 */
	public long getLastModified() throws IOException, FileNotFoundException {
		File file = isFilePreferred() ? getFile() : null;
		if(file != null) {
			if(!file.exists()) throw new FileNotFoundException(file.getPath());
			return file.lastModified();
		} else {
			try (ResourceConnection conn = open()) {
				return conn.getLastModified();
			}
		}
	}

	/**
	 * Opens this resource for reading.
	 * <p>
	 * This method opens non-local-file resources and closes the resource when the stream is closed.
	 * If consecutive operations will be done on the resource, use {@link #open()}
	 * to obtain a {@link ResourceConnection}.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 * @throws  FileNotFoundException  if resource does not exist (see {@link #exists()})
	 *
	 * @see  #isFilePreferred()
	 * @see  #getFile()
	 * @see  #open()
	 * @see  ResourceConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException, FileNotFoundException {
		File file = isFilePreferred() ? getFile() : null;
		if(file != null) {
			return new FileInputStream(file);
		} else {
			boolean closeNow = true;
			final ResourceConnection conn = open();
			try {
				InputStream in = new FilterInputStream(conn.getInputStream()) {
					@Override
					public void close() throws IOException {
						try {
							super.close();
						} finally {
							conn.close();
						}
					}
				};
				closeNow = false;
				return in;
			} finally {
				if(closeNow) conn.close();
			}
		}
	}

	/**
	 * There is a chance for the {@link ResourceConnection} implementation to outperform direct file I/O,
	 * this flag determines whether performance-sensitive API usage should prefer direct
	 * file I/O (when available) or prefer resource connections.
	 *
	 * @return  {@code true} when file I/O (via {@link #getFile()}) will generally perform better,
	 *          or {@code false} when resource connection (via @{link #open()}) should be preferred.
	 *
	 * @throws  IOException  if I/O error occurs
	 */
	abstract public boolean isFilePreferred() throws IOException;

	/**
	 * Tries to get a local {@link File} for this resource.  When the resource exists locally,
	 * this will be a direct reference to the resource.  When the resource exists remotely or
	 * is otherwise not directly accessible, this will return {@code null}.
	 * <p>
	 * The file may or may not {@link File#exists() exist}.
	 * </p>
	 * <p>
	 * Use this when having a {@link File} is a convenience or optimization, and not a hard
	 * requirement.  Use {@link ResourceConnection#getFile()} when a {@link File} is required.
	 * </p>
	 *
	 * @throws  IOException  if I/O error occurs
	 *
	 * @see  ResourceConnection#getFile()
	 */
	abstract public File getFile() throws IOException;

	/**
	 * Opens a connection to this resource.
	 * The connection must be {@link ResourceConnection#close() closed} when no longer needed.
	 */
	abstract public ResourceConnection open() throws IOException;
}
