package com.itiniu.iticrawler.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by falk.e on 03/03/14.
 */
public class CloseableByteArrayOutputStream extends ByteArrayOutputStream {

    private boolean closed = false;
    private int wroteCount = 0;
    private int wroteToCount = 0;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void close()
    {
        this.closed = true;
    }

    public boolean isClosed()
    {
        return this.closed;
    }

    @Override
    public void write(int b)
    {
        this.lock.lock();

        try{
            this.wroteCount++;
            super.write(b);
        } finally {
            this.lock.unlock();
        }


    }

    @Override
    public void write(byte[] b, int off, int len)
    {
        this.wroteCount++;
        super.write(b,off,len);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {

        this.lock.lock();
        try
        {
            this.wroteToCount++;
            super.writeTo(os);
        }finally
        {
            this.lock.unlock();
        }

    }

    public int getWroteCount()
    {
        return this.wroteCount;
    }

    public int getWroteToCount()
    {
        return this.wroteCount;
    }

}
