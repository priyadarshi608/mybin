package pl.art.lach.mateusz.javaopenchess.core;



















public enum Squares
{
  SQ_A(0), 
  
  SQ_B(1), 
  
  SQ_C(2), 
  
  SQ_D(3), 
  
  SQ_E(4), 
  
  SQ_F(5), 
  
  SQ_G(6), 
  
  SQ_H(7), 
  
  SQ_1(7), 
  
  SQ_2(6), 
  
  SQ_3(5), 
  
  SQ_4(4), 
  
  SQ_5(3), 
  
  SQ_6(2), 
  
  SQ_7(1), 
  
  SQ_8(0);
  
  private int value;
  
  private Squares(int value)
  {
    this.value = value;
  }
  



  public int getValue()
  {
    return value;
  }
  



  public void setValue(int value)
  {
    this.value = value;
  }
}
