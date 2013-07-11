package org.jboss.tools.forge.aesh.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class AeshOutputStream extends OutputStream {
	
	public static final AeshOutputStream STD_OUT = new AeshOutputStream();
	public static final AeshOutputStream STD_ERR = new AeshOutputStream();
	
	public static interface StreamListener {
		public void charAppended(char c);
	}
	
	private ArrayList<StreamListener> listeners = new ArrayList<AeshOutputStream.StreamListener>();

	public void addStreamListener(StreamListener listener) {
		listeners.add(listener);
	}
	
	public void removeStreamListener(StreamListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void write(int i) throws IOException {
		for (StreamListener listener : listeners) {
			listener.charAppended((char)i);
		}
	}
	
	public void write(byte[] b) throws IOException {
		super.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}

}
