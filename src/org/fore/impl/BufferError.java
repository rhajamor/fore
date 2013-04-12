package org.fore.impl;

public class BufferError extends Throwable {

	public BufferError() {
		super();
	}

	public BufferError(String msg) {
		super(msg);
	}

	public BufferError(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public BufferError(Throwable throwable) {
		super(throwable);
	}

}
