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
	void setForegroundBlack();				//  30
	void setForegroundRed();				//  31
	void setForegroundGreen();				//  32
	void setForegroundYellow();				//  33
	void setForegroundBlue();				//  34
	void setForegroundMagenta();			//  35
	void setForegroundCyan();				//  36
	void setForegroundWhite();				//  37
	void setForegroundDefault(); 			//  39
	void setBackgroundBlack();				//  40
	void setBackgroundRed();				//  41
	void setBackgroundGreen();				//  42
	void setBackgroundYellow();				//  43
	void setBackgroundBlue(); 				//  44
	void setBackgroundMagenta();			//  45
	void setBackgroundCyan();				//  46
	void setBackgroundWhite();				//  47
	void setBackgroundDefault();			//  49
	
}
