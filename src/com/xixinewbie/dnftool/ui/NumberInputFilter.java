package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.util.S;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class NumberInputFilter extends DocumentFilter {
    
    private int maxValue;
    
    public NumberInputFilter(int maxValue) {
        this.maxValue = Math.max(0, maxValue);
    }
    
    @Override
    public void insertString(FilterBypass fb, int offset, String text,
                             AttributeSet attr) throws BadLocationException {
        
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, text);
        if (S.isNumber(sb.toString())) {
            if (maxValue > 0 && S.intValue(sb.toString()) > maxValue) {
                fb.replace(0, doc.getLength(), "", attr);
                offset = 0;
                text = String.valueOf(maxValue);
            }
            super.insertString(fb, offset, text, attr);
        }
    }
    
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
                        AttributeSet attr) throws BadLocationException {
        
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);
        
        if (S.isNumber(sb.toString())) {
            if (maxValue > 0 && S.intValue(sb.toString()) > maxValue) {
                fb.replace(0, doc.getLength(), "", attr);
                offset = 0;
                text = String.valueOf(maxValue);
            }
            super.replace(fb, offset, length, text, attr);
        }
    }
    
    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        super.remove(fb, offset, length);
    }
}
