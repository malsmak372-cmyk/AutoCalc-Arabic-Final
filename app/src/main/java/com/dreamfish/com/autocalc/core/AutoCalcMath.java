package com.dreamfish.com.autocalc.core;

import java.math.BigDecimal;
import java.util.Random;

public class AutoCalcMath {

  public AutoCalcMath(AutoCalc autoCalc) {
    this.autoCalc = autoCalc;
  }

  private AutoCalc autoCalc = null;


  public static double PI = 3.1415926535897932;

  
  public BigDecimal d2g(BigDecimal d) {
    return d.multiply(BigDecimal.valueOf(Math.PI)).divide(BigDecimal.valueOf(180d), autoCalc.getNumberScale(), BigDecimal.ROUND_HALF_UP);
  }
  
  public BigDecimal g2d(BigDecimal g) {
    return g.multiply(BigDecimal.valueOf(180d)).divide(BigDecimal.valueOf(Math.PI), autoCalc.getNumberScale(), BigDecimal.ROUND_HALF_UP);
  }
  
  public BigDecimal log(BigDecimal b, BigDecimal a) {
    return BigDecimal.valueOf(Math.log(b.doubleValue()) / Math.log(a.doubleValue()));
  }
  
  public Long rand(Long min, Long max) {
    return min + ((new Random().nextLong() * (max - min)));
  }
  
  public BigDecimal fact(BigDecimal d) throws AutoCalcInfiniteException {
    //
    if(d.compareTo(BigDecimal.valueOf(10000)) >= 0)
      throw new AutoCalcInfiniteException();

    BigDecimal sum = BigDecimal.valueOf(1);
    BigDecimal step = BigDecimal.valueOf(1);
    BigDecimal i = BigDecimal.valueOf(1);
    for (; i.compareTo(d) < 0; i = i.add(step))
      sum = sum.multiply(i);
    return sum;
  }
}
