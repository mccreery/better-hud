package tk.nukeduck.hud.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Ints;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Version implements Comparable<Version>, IMessage {
	/** All versions are greater than this version */
	public static final Version ZERO = new Version();

	protected byte[] parts;
	protected int precision;

	public Version() {
		this(new int[0]);
	}

	public Version(String string) {
		this(getParts(string));
	}

	public Version(int... parts) {
		this.parts = new byte[parts.length];

		for(int i = 0; i < parts.length; i++) {
			int part = parts[i] > 255 ? 255 : parts[i];
			this.parts[i] = (byte)part;
		}
		precision = countSignificant(this.parts);
	}

	/** @return The number of significant parts in {@code parts} */
	private static int countSignificant(byte[] parts) {
		int i;
		for(i = parts.length - 1; i >= 0 && parts[i] == 0; --i);

		// i now contains the index of the least non-zero (significant) part
		return i + 1;
	}

	public int getPart(int i) {
		return i < parts.length ? Byte.toUnsignedInt(parts[i]) : 0;
	}
	public int getMajor() {return getPart(0);}
	public int getMinor() {return getPart(0);}
	public int getPatch() {return getPart(0);}
	public int getPrecision() {return parts.length;}

	@Override
	public int compareTo(Version other) {
		for(int i = 0; i < parts.length && i < other.parts.length; i++) {
			int compare = Integer.compare(parts[i], other.parts[i]);
			if(compare != 0) return compare;
		}

		// More specific > less specific
		return parts.length - other.parts.length;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof Version) {
			return compareTo((Version)other) == 0;
		} else if(other instanceof String) {
			return equals(new Version((String)other));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(parts);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		buf.readBytes(parts = new byte[buf.readInt()]);
		precision = countSignificant(parts);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(parts.length);
		buf.writeBytes(parts);
	}

	/** The number of parts to pad the result of {@link #toString()} to */
	public static final int MIN_PRECISION = 2;

	/** {@code pad} defaults to {@link #MIN_PRECISION}
	 * @see #toString(int) */
	@Override
	public String toString() {
		return toString(MIN_PRECISION);
	}

	/** @return The string representation of this version,
	 * padded to at least {@code pad} parts, for example:
	 *
	 * <p>{@code new Version().toString(2)} results in {@code "0.0"}<br>
	 * {@code new Version("1.2.3").toString(0)} results in {@code "1.2.3"} */
	public String toString(int pad) {
		int precision = Math.max(pad, parts.length);
		StringBuilder builder = new StringBuilder(precision * 4);

		for(int i = 0; i < pad || i < parts.length; i++) {
			builder.append(getPart(i)).append('.');
		}
		builder.setLength(builder.length() - 1);

		return builder.toString();
	}

	protected static int[] getParts(String string) {
		List<Integer> parts = new ArrayList<Integer>((string.length() + 1) / 2);

		int dot;
		for(String tail = string; !tail.isEmpty(); tail = tail.substring(dot + 1)) {
			dot = tail.indexOf('.');
			if(dot == -1) dot = tail.length();

			try {
				int part = Integer.parseUnsignedInt(tail.substring(0, dot));
				parts.add(part);
			} catch(NumberFormatException e) {
				System.err.printf("Version string \"%s\" contained an invalid number \"%s\"\n", string, tail.substring(0, dot));
			}
		}
		return Ints.toArray(parts);
	}
}
