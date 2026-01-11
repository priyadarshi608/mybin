package org.jdesktop.application;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;






















class MnemonicText
{
  private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
  
  private MnemonicText() {}
  
  public static void configure(Object paramObject, String paramString)
  {
    String str = paramString;
    int i = -1;
    int j = 0;
    
    int k = mnemonicMarkerIndex(paramString, '&');
    if (k == -1) {
      k = mnemonicMarkerIndex(paramString, '_');
    }
    if (k != -1) {
      str = str.substring(0, k) + str.substring(k + 1);
      i = k;
      StringCharacterIterator localStringCharacterIterator = new StringCharacterIterator(paramString, k);
      j = mnemonicKey(localStringCharacterIterator.next());
    }
    if ((paramObject instanceof Action)) {
      configureAction((Action)paramObject, str, j, i);
    }
    else if ((paramObject instanceof AbstractButton)) {
      configureButton((AbstractButton)paramObject, str, j, i);
    }
    else if ((paramObject instanceof JLabel)) {
      configureLabel((JLabel)paramObject, str, j, i);
    }
    else {
      throw new IllegalArgumentException("unrecognized target type " + paramObject);
    }
  }
  
  private static int mnemonicMarkerIndex(String paramString, char paramChar) {
    if ((paramString == null) || (paramString.length() < 2)) return -1;
    StringCharacterIterator localStringCharacterIterator = new StringCharacterIterator(paramString);
    int i = 0;
    while (i != -1) {
      i = paramString.indexOf(paramChar, i);
      if (i != -1) {
        localStringCharacterIterator.setIndex(i);
        int j = localStringCharacterIterator.previous();
        localStringCharacterIterator.setIndex(i);
        int k = localStringCharacterIterator.next();
        int m = (j == 39) && (k == 39) ? 1 : 0;
        boolean bool = Character.isWhitespace(k);
        if ((m == 0) && (!bool) && (k != 65535)) {
          return i;
        }
      }
      if (i != -1) i++;
    }
    return -1;
  }
  




  private static int mnemonicKey(char paramChar)
  {
    int i = paramChar;
    if ((i >= 97) && (i <= 122)) {
      i -= 32;
    }
    return i;
  }
  





  private static void configureAction(Action paramAction, String paramString, int paramInt1, int paramInt2)
  {
    paramAction.putValue("Name", paramString);
    if (paramInt1 != 0) paramAction.putValue("MnemonicKey", Integer.valueOf(paramInt1));
    if (paramInt2 != -1) paramAction.putValue("SwingDisplayedMnemonicIndexKey", Integer.valueOf(paramInt2));
  }
  
  private static void configureButton(AbstractButton paramAbstractButton, String paramString, int paramInt1, int paramInt2) {
    paramAbstractButton.setText(paramString);
    if (paramInt1 != 0) paramAbstractButton.setMnemonic(paramInt1);
    if (paramInt2 != -1) paramAbstractButton.setDisplayedMnemonicIndex(paramInt2);
  }
  
  private static void configureLabel(JLabel paramJLabel, String paramString, int paramInt1, int paramInt2) {
    paramJLabel.setText(paramString);
    if (paramInt1 != 0) paramJLabel.setDisplayedMnemonic(paramInt1);
    if (paramInt2 != -1) paramJLabel.setDisplayedMnemonicIndex(paramInt2);
  }
}
