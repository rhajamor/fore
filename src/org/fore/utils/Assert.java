/**
 * 
 */
package org.fore.utils;

/**
 * @author Riadh HAJ AMOR
 *
 */
public class Assert {

	static void throwAssertionError(String description) {
		throw new RuntimeException(description);
	}

	public static void isTrue(boolean expr, String description) {
		if (!expr)
			throwAssertionError(description);
	}

	public static void notTrue(boolean expr, String description) {
		if (expr)
			throwAssertionError(description);
	}

	public static <T> void isEquals(T o, T b, String description) {
		if (!o.equals(b))
			throwAssertionError(description);
	}

	public static void isTrue(boolean expr) {
		if (!expr)
			throwAssertionError("Assertion failed");
	}

	public static void notTrue(boolean expr) {
		if (expr)
			throwAssertionError("Assertion failed");
	}

	public static <T> void isEquals(T o, T b) {
		if (!o.equals(b))
			throwAssertionError("Not equals !");
	}

}
