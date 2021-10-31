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

import com.aoapps.net.Path;

/**
 * Gets {@link Resource resources} given their paths.
 * <p>
 * When needing to refer to a set of resources with a singular noun, use "store",
 * which is contrasted with "repository" used for <code>Pages</code>.
 * </p>
 *
 * @see  Resource
 */
public interface ResourceStore {

	/**
	 * Stores should provide a meaningful toString implementation, which makes
	 * sense with the path following.
	 *
	 * @see  Resource#toString()
	 */
	@Override
	String toString();

	/**
	 * Checks if the store is currently available.  A store that is
	 * unavailable is likely going to throw exceptions.  Tools are encouraged
	 * to handle unavailable store gracefully, when possible.
	 */
	boolean isAvailable();

	/**
	 * Gets a {@link Resource} for the given path.
	 * The resource may or may not {@link Resource#exists() exist}.
	 */
	Resource getResource(Path path);
}
