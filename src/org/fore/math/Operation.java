/**
 * 
 */
package org.fore.math;

/**
 * @author RH030971
 *
 */
public enum Operation {

	MUL('*'), SUB('-'), ADD('+'), DIV('/');

	private Operation(char operation) {
		this.operation = operation;
	}

	private char operation;

	public char getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return String.valueOf(operation);
	}

	public static Operation valueOf(char op) {
		switch (op) {
		case '+':
			return ADD;
		case '-':
			return SUB;
		case '*':
			return MUL;
		case '/':
			return DIV;
		default:
			throw new UnsupportedOperationException(String.valueOf(op));
		}
	}
}
