package org.jboss.tools.seam.forge.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.IConsoleConstants;

public class ConsoleInputStream extends InputStream {

    private byte[] input = new byte[100];
    private int inPointer = 0;
    private int outPointer = 0;
    private int size = 0;
    private boolean eofSent = false;
    private boolean closed = false;
    private Console console;
    private Color color;
    private int fontStyle = SWT.NORMAL;
    
    private char c = (char)-1;

    ConsoleInputStream(Console console) {
        this.console = console;
    }
    
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
//    	while (c != -1 && !closed) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }
    
    public synchronized void appendData(String text) {
    	String encoding = console.getEncoding();
        byte[] newData;
        if (encoding!=null)
			try {
				newData = text.getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				newData = text.getBytes();	
			}
		else
        	newData = text.getBytes();
        
        while(input.length-size < newData.length) {
            growArray();
        }
        
        if (size == 0) { //inPointer == outPointer
            System.arraycopy(newData, 0, input, 0, newData.length);
            inPointer = newData.length;
            size = newData.length;
            outPointer = 0;
        } else if (inPointer < outPointer || input.length - inPointer > newData.length) {
            System.arraycopy(newData, 0, input, inPointer, newData.length);
            inPointer += newData.length;
            size += newData.length;
        } else {
            System.arraycopy(newData, 0, input, inPointer, input.length-inPointer);
            System.arraycopy(newData, input.length-inPointer, input, 0, newData.length-(input.length-inPointer));
            inPointer = newData.length-(input.length-inPointer);
            size += newData.length;
        }
        
        if (inPointer == input.length) {
            inPointer = 0;
        }
        notifyAll();
    }
    
    private void growArray() {
        byte[] newInput = new byte[input.length+1024];
        if (outPointer < inPointer) {
            System.arraycopy(input, outPointer, newInput, 0, size);
        } else {
            System.arraycopy(input, outPointer, newInput, 0, input.length-outPointer);
            System.arraycopy(input, 0, newInput, input.length-outPointer, inPointer);
        }
        outPointer = 0;
        inPointer = size;
        input = newInput;
        newInput = null;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int newFontStyle) {
        if (newFontStyle != fontStyle) {
            int old = fontStyle;
            fontStyle = newFontStyle;
            console.firePropertyChange(this, IConsoleConstants.P_FONT_STYLE, new Integer(old), new Integer(fontStyle));
        }
    }
    
    public void setColor(Color newColor) {
        Color old = color;
        if (old == null || !old.equals(newColor)) {
            color = newColor;
            console.firePropertyChange(this, IConsoleConstants.P_STREAM_COLOR, old, newColor);
        }
    }
    
    public Color getColor() {
        return color;
    }
    
    public int available() throws IOException {
        if (closed && eofSent) {
            throw new IOException("Input Stream Closed"); //$NON-NLS-1$
        } else if (size == 0) {
            if (!eofSent) {
                eofSent = true;
                return -1;
            } 
            throw new IOException("Input Stream Closed"); //$NON-NLS-1$
        }
        
        return size;
    }
    
    public synchronized void close() throws IOException {
        if(closed) {
            throw new IOException("Input Stream Closed"); //$NON-NLS-1$
        }
        closed = true;
        notifyAll();
        console.streamClosed(this);
    }

}
