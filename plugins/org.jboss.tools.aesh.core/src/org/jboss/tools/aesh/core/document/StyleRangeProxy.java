package org.jboss.tools.aesh.core.document;

public interface StyleRangeProxy {
	
	void resetToNormal();					//   0
	void setBoldOn();						//   1
	void setFaintOn();						//   2
	void setItalicOn();						//   3
	void setUnderlineSingle();				//   4
	void setImageNegative();				//   7
	void setCrossedOut();					//   9
	void setBoldOrFaintOff();				//  22
	void setItalicOff();					//  23
	void setUnderlineNone();				//  24
	void setImagePositive();				//  27
	void setNotCrossedOut();				//  29
	void setForegroundDefault(); 			//  39
	void setBackgroundRed();				//  41
	void setBackgroundBlue(); 				//  44
	void setBackgroundWhite();				//  47
	void setBackgroundDefault();			//  49
	
}
