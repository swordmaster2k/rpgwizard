/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

/**
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public interface AssetHandleResolver {

  /**
   * Determines if the asset handle resolver can resolve a handle
   * for the specified asset descriptor.
   **
   * @param descriptor asset descriptor instance
   * @return true if resolvable, false otherwise
   * @throws NullPointerException if (descriptor == null)
   */
  boolean resolvable(AssetDescriptor descriptor)
    throws NullPointerException;

  /**
   * Resolves an asset handle for the specified descriptor.
   *
   * @param descriptor asset descriptor instance
   * @return asset handle if resolved, null otherwise
   * @throws NullPointerException if (descriptor == null)
   */
  AssetHandle resolve(AssetDescriptor descriptor)
    throws NullPointerException;

}
