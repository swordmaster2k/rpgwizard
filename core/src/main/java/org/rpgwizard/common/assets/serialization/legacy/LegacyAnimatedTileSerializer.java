/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization.legacy;

import org.rpgwizard.common.assets.AbstractAssetSerializer;
import org.rpgwizard.common.assets.AnimatedTile;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.io.ByteBufferHelper;
import org.rpgwizard.common.io.Paths;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 * @author Chris Hutchinson <chris@cshutchinson.com>
 * @author Joel Moore (based on existing binary save)
 */
public class LegacyAnimatedTileSerializer extends AbstractAssetSerializer {

	private static final String HEADER_MAGIC = "RPGTLKIT TILEANIM";
	private static final int HEADER_VERSION_MAJOR = 2;
	private static final int HEADER_VERSION_MINOR = 0;

	@Override
	public int priority() {
		return 1; // not our first choice
	}

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI().toString());
		return ext.endsWith(CoreProperties
				.getFullExtension("toolkit.animatedtile.extension.legacy"));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return this.serializable(descriptor);
	}

	@Override
  public void serialize(AssetHandle handle) throws IOException, AssetException {

    try (final WritableByteChannel channel = handle.write()) {
      
      final AnimatedTile tile = (AnimatedTile) handle.getAsset();
      final ByteBuffer buffer = ByteBuffer.allocate(32);
      buffer.order(ByteOrder.LITTLE_ENDIAN);

      channel.write(ByteBufferHelper.getBuffer(HEADER_MAGIC));
      
      buffer.putShort((short) HEADER_VERSION_MAJOR);
      buffer.putShort((short) HEADER_VERSION_MINOR);
      
      List<AnimatedTile.Frame> frames = tile.getFrames();
      int duration = 0;
      if (frames.size() > 0) {
        duration = frames.get(0).getDuration();
      }
      buffer.putInt(duration);
      buffer.putInt(frames.size());
      
      buffer.flip();
      channel.write(buffer);
      buffer.compact();

      for (AnimatedTile.Frame f : frames) {
        channel.write(ByteBufferHelper.getBuffer(f.getFrameTarget()));
      }
    }
  }
	@Override
  public void deserialize(AssetHandle handle) throws IOException, AssetException {

    try (final ReadableByteChannel channel = handle.read()) {

      final ByteBuffer buffer
          = ByteBuffer.allocateDirect((int) handle.size());

      channel.read(buffer);

      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.position(0);

      final String header = ByteBufferHelper.getTerminatedString(buffer);
      final int versionMajor = buffer.getShort();
      final int versionMinor = buffer.getShort();

      checkVersion(header, versionMajor, versionMinor);

      final AnimatedTile tile = new AnimatedTile(handle.getDescriptor());

      final int fps = buffer.getInt();     // frame duration (ms)
      final int count = buffer.getInt();   // frame count

      for (int i = 0; i < count; i++) {
        final String frameTarget = ByteBufferHelper.getTerminatedString(buffer);
        if (frameTarget != null && frameTarget.length() > 0) {
          final AnimatedTile.Frame frame = tile.new Frame(
              AssetDescriptor.parse("file:///" + frameTarget), frameTarget);
          frame.setDuration(fps);
          tile.getFrames().add(frame);
        }
      }
      handle.setAsset(tile);
    }
  }
	protected void checkVersion(String header, int major, int minor)
			throws AssetException {
		if (!HEADER_MAGIC.equals(header) || HEADER_VERSION_MAJOR > major
				|| HEADER_VERSION_MINOR > minor) {
			throw new AssetException("unsupported file version");
		}
	}

}
