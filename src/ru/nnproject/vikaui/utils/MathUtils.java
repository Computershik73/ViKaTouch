package ru.nnproject.vikaui.utils;

public class MathUtils {


	public static int lerp(final int start, final int target, final int mul, final int div) {
		return start + ((target - start) * mul / div);
	}

	public static int clamp(final int val, final int min, final int max) {
		return Math.max(Math.min(val, max), min);
	}
	
}
