package org.jboss.tools.forge.core.io;

import java.io.IOException;
import java.io.InputStream;

public class ForgeInputStream extends InputStream {

    private byte[] input = new byte[0];
    private int outPointer = 0;
    private int size = 0;
    private boolean eofSent = false;
    private boolean closed = false;

    public synchronized int read() throws IOException {
        waitForData();
        if (available() == -1) { 
            return -1;
        }
        
        byte b = input[outPointer];
        outPointer++;
        if (outPointer == input.length) {
            outPointer = 0;
        }
        size -= 1;
        return b;
    }
    
    private void waitForData() {
        while (size == 0 && !closed) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }
    
    public synchronized void appendData(String text) {
        input = text.getBytes();
        size = text.length();
        outPointer = 0;
        notifyAll();
    }
    
    public int available() throws IOException {
        if (closed && eofSent) {
            throw new IOException("Input Stream Closed"); 
        } else if (size == 0) {
            if (!eofSent) {
                eofSent = true;
                return -1;
            } 
            throw new IOException("Input Stream Closed"); 
        }
        
        return size;
    }
    
    public synchronized void close() throws IOException {
        if(closed) {
            throw new IOException("Input Stream Closed"); 
        }
        closed = true;
        notifyAll();
    }

}
