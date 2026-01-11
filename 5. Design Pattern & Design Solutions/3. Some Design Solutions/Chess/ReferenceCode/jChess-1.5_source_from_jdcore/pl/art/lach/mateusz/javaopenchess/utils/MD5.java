package pl.art.lach.mateusz.javaopenchess.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;






















public class MD5
{
  private static final Logger LOG = Logger.getLogger(MD5.class);
  
  public MD5() {}
  
  public static String encrypt(String str)
  {
    try
    {
      MessageDigest message = MessageDigest.getInstance("MD5");
      message.update(str.getBytes(), 0, str.length());
      return new BigInteger(1, message.digest()).toString(16);
    }
    catch (NoSuchAlgorithmException ex)
    {
      LOG.error("NoSuchAlgorithmException: " + ex); }
    return null;
  }
}
