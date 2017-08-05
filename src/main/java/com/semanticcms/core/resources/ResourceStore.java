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

/**
 * Gets {@link Resource resources} given their paths.
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
	 * Gets a {@link Resource} for the given path.
	 * The resource may or may not {@link Resource#exists() exist}.
	 */
	Resource getResource(String path);
}
