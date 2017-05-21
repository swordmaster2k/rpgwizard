/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public final class ByteBufferHelper {

	public static final Charset LEGACY_CHARSET = Charset.forName("US-ASCII");

	public static String getTerminatedString(final ByteBuffer buffer) {
		final StringBuilder builder = new StringBuilder();
		while (buffer.hasRemaining()) {
			final int ch = buffer.get();
			if (ch == '\0') {
				break;
			}
			builder.append((char) ch);
		}
		return builder.toString();
	}

	/**
	 * Writes a null-terminated string into a byte buffer with the specified
	 * encoding.
	 *
	 * @param buffer
	 *            buffer to write string into
	 * @param str
	 *            string to write into the buffer, if null a single null byte is
	 *            written
	 * @param charset
	 *            character set to use as encoding, if null the platform
	 *            character set is used.
	 */
	public static void putTerminatedString(final ByteBuffer buffer, String str,
			Charset charset) {
		if (buffer == null)
			throw new NullPointerException();
		if (charset == null)
			charset = Charset.defaultCharset();
		if (buffer.remaining() < 1)
			throw new IllegalStateException("buffer is full");
		if (str != null) {
			final byte[] encoded = str.getBytes(charset);
			if (buffer.remaining() < encoded.length + 1)
				throw new IllegalStateException(
						"string length exceeds remaining buffer length");
			buffer.put(encoded);
		}
		buffer.put((byte) 0);
	}

	/**
	 * Converts a String to bytes using the legacy character set and returns a
	 * new buffer containing the String's bytes, with its position at 0, ready
	 * to write.
	 *
	 * @param s
	 *            a String to create a buffer for
	 * @return a new buffer with the String's bytes
	 */
	public static ByteBuffer getBuffer(String s) {
		return ByteBuffer.wrap(s.getBytes(LEGACY_CHARSET));
	}

	/**
	 * Gets an unsigned Int from the buffer, Java by default treats all bytes as
	 * signed.
	 * 
	 * @param buffer
	 * @return
	 */
	public static int getUnsignedInt(ByteBuffer buffer) {
		return buffer.get() & 0xff;
	}

}
