package org.jboss.tools.seam.forge.console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {

    private boolean closed = false;
    private ConsolePartitioner partitioner;
    
    ConsoleOutputStream(ConsolePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public synchronized boolean isClosed() {
        return closed;
    }
    
    public synchronized void close() throws IOException {
        if(closed) {
            throw new IOException("Output Stream is closed"); 
        }
        closed = true;
        partitioner = null;
    }

    public void flush() throws IOException {
        if(closed) {
            throw new IOException("Output Stream is closed"); 
        }
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        if(closed) {
            throw new IOException("Output Stream is closed"); 
        }
        notifyParitioner(new String(b, off, len));
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        write(new byte[] {(byte)b}, 0, 1);
    }    

    public synchronized void write(String str) throws IOException {
        if(closed) {
            throw new IOException("Output Stream is closed"); 
        }
        notifyParitioner(str);
    }
    
    private void notifyParitioner(String str) throws IOException {
        try {
            partitioner.streamAppended(this, str);
        } catch (IOException e) {
            if (!closed) {
                close();
            }
            throw e;
        }
    }

}
