package org.jboss.tools.forge.console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {

    private boolean closed = false;
    private Console console;
    
    ConsoleOutputStream(Console console) {
        this.console = console;
    }

    public synchronized boolean isClosed() {
        return closed;
    }
    
    public synchronized void close() throws IOException {
        if(closed) {
            throw new IOException("Output Stream is closed"); 
        }
        closed = true;
        console = null;
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
        notifyPartitioner(new String(b, off, len));
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
        notifyPartitioner(str);
    }
    
    private void notifyPartitioner(String str) throws IOException {
    	console.appendString(str);
    }

}
