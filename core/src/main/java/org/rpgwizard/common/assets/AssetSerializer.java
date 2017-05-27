/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.io.IOException;
import java.util.Comparator;

/**
 * Provides an interface for serializing game assets.
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public interface AssetSerializer {

	class PriorityComparator implements Comparator<AssetSerializer> {

		@Override
		public int compare(AssetSerializer t, AssetSerializer t1) {
			int priorityCompare = t.priority() - t1.priority();
			if (priorityCompare == 0) {
				if (t.equals(t1)) {
					return 0;
				} else {
					return t.getClass().getSimpleName()
							.compareTo(t1.getClass().getSimpleName());
				}
			} else {
				return priorityCompare;
			}
		}

	}

	;

	/**
	 * Gets the priority of the serializer. Serializers sort in ascending order,
	 * so serializers with lower numbers take priority over serializers with
	 * higher numbers. If two serializers have the same priority, sorting
	 * between them is arbitrary. If you want to guarantee that one serializer
	 * comes before another, you must assign it a lower priority number.
	 *
	 * @return the priority of the serializer; lower numbers come first
	 *         (serializers are sorted in ascending order)
	 */
	int priority();

	boolean serializable(final AssetDescriptor descriptor);

	boolean deserializable(final AssetDescriptor descriptor);

	void serialize(AssetHandle handle) throws IOException, AssetException;

	void deserialize(AssetHandle handle) throws IOException, AssetException;

}
