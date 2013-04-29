package org.fore.impl;

public class BufferError extends Throwable
{

	private static final long serialVersionUID = 1L;

	public BufferError()
	{
		super();
	}

	public BufferError(String msg)
	{
		super(msg);
	}

	public BufferError(String msg, Throwable throwable)
	{
		super(msg, throwable);
	}

	public BufferError(Throwable throwable)
	{
		super(throwable);
	}

}
