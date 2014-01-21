package stimim.solver;

public class GemUtil {
	public static final int N_COLOR = BaseGem.values().length;
	public static final int BASE_GEM_MASK = 0x000F;
	public static final int ENHANCED_MARK = 0x20;
	public static final int WEATHERED_MARK = 0x10;
	public static final int NOTHING = -1;
	public static final int UNKNOWN = N_COLOR;

	public static final int toInt(BaseGem gem, boolean isEnhanced,
			boolean isWeathered) {
		int retval = gem.ordinal();
		if (isEnhanced) {
			retval |= ENHANCED_MARK;
		}
		if (isWeathered) {
			retval |= WEATHERED_MARK;
		}
		return retval;
	}

	public static final boolean isEnhanced(int x) {
		return (x & ENHANCED_MARK) != 0;
	}

	public static final boolean isWeathered(int x) {
		return (x & WEATHERED_MARK) != 0;
	}

	public static final BaseGem getBaseGem(int x) {
		if (isNothing(x) || isUnknown(x)) {
			return null;
		}
		return BaseGem.values()[x & BASE_GEM_MASK];
	}

	public static final boolean isUnknownOrNothing(int x) {
		return isNothing(x) || isUnknown(x);
	}

	public static final boolean isNothing(int x) {
		return x == NOTHING;
	}

	public static final boolean isUnknown(int x) {
		return x == UNKNOWN;
	}

	public static final char toChar(int x) {
		if (isNothing(x)) {
			return '_';
		}
		if (isUnknown(x)) {
			return '?';
		}
		return getBaseGem(x).toChar(isEnhanced(x), isWeathered(x));
	}

	public enum BaseGem {
		FIRE('f'), GRASS('g'), WATER('w'), LIGHT('l'), DARK('d'), HEART('h');

		private final char symbol;

		BaseGem(char symbol) {
			this.symbol = symbol;
		}

		public final char toChar(boolean isEnhanced, boolean isWeathered) {
			char retval = symbol;

			if (isWeathered) {
				retval = (char) ((retval - 'a') + 'ⓐ');
			}

			if (isEnhanced) {
				retval = Character.toUpperCase(retval);
			} else {
				retval = Character.toLowerCase(retval);
			}

			return retval;
		}
	}
}