package org.fore;

public class LL {

    /**
     * @param args
     */
    public static void main(String[] args) {

		System.out.println(IsPowerOfTwo(4132));
		System.out.println(IsPowerOfTwo(5847));
		System.out.println(IsPowerOfTwo(256));
		System.out.println(IsPowerOfTwo(0));

    }
   static boolean IsPowerOfTwo(long x)
    {
        return (x != 0) && ((x & (x - 1)) == 0);
    }
}
