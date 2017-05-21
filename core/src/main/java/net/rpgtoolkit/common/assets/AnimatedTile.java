/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Animated tile asset.
 */
public class AnimatedTile extends AbstractAsset {

	/**
	 * Animated tile frame descriptor.
	 */
	public class Frame {

		private AssetDescriptor descriptor;
		private String frameTarget;
		private int duration;

		public Frame(AssetDescriptor descriptor, String frameTarget) {
			this.descriptor = descriptor;
			this.frameTarget = frameTarget;
			duration = 0;
		}

		public AssetDescriptor getDescriptor() {
			return this.descriptor;
		}

		public String getFrameTarget() {
			return frameTarget;
		}

		public void setFrameTarget(String frameTarget) {
			this.frameTarget = frameTarget;
		}

		public int getDuration() {
			return this.duration;
		}

		public void setDuration(int value) {
			this.duration = Math.max(0, value);
		}

	}

	private final List<Frame> frames;

	public AnimatedTile(AssetDescriptor descriptor) {
    super(descriptor);
    frames = new ArrayList<>();
  }
	/**
	 * Returns a list of animation frames associated with this animated tile.
	 *
	 * @return list of animation frames
	 */
	public List<Frame> getFrames() {
		return frames;
	}

}
