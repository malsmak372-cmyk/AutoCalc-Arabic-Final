package com.dreamfish.com.autocalc.core;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static com.dreamfish.com.autocalc.core.AutoCalc.*;

public class AutoCalcTools {


  public AutoCalcTools(AutoCalc autoCalc) {
    this.autoCalc = autoCalc;
  }

  private AutoCalc autoCalc = null;

  public String numberToScientificNotationStr(BigDecimal n) throws AutoCalcException, AutoCalcInfiniteException {
    return n.stripTrailingZeros().toString().replace("+", "").replace("E", "×10^");
  }
  
  public String numberToStr(double n) throws AutoCalcException, AutoCalcInfiniteException {
    return numberToStr(doubleToBigDecimal(n));
  }
  
  public String numberToStr(BigDecimal n) {
    BigDecimal n2 = n.stripTrailingZeros().setScale(autoCalc.getNumberScale(), BigDecimal.ROUND_HALF_UP);
    switch (autoCalc.getBcMode()) {
      case BC_MODE_DEC:
        return n2.toPlainString();
      case BC_MODE_BIN:
        return Long.toBinaryString(n2.longValue());
      case BC_MODE_OCT:
        return Long.toOctalString(n2.longValue());
      case BC_MODE_HEX:
        return Long.toHexString(n2.longValue());
    }
    return "";
  }
  
  public String numberToStr(long n) {
    switch (autoCalc.getBcMode()) {
      case BC_MODE_DEC:
        return String.valueOf(n);
      case BC_MODE_BIN:
        return Long.toBinaryString(n);
      case BC_MODE_OCT:
        return Long.toOctalString(n);
      case BC_MODE_HEX:
        return Long.toHexString(n);
    }
    return "";
  }
  
  public BigDecimal strToNumber(String str) throws AutoCalcException {

    try {
      double result;
      if (str.startsWith(" ") || str.endsWith(" ")) str = str.trim();

      if (str.endsWith("b") && !str.startsWith("0x")) result = Long.valueOf(str.substring(0, str.length() - 1), 2);
      else if (str.startsWith("0b")) result = Long.valueOf(str.substring(2), 2);
      else if (str.startsWith("0x")) result = Long.valueOf(str.substring(2), 16);
      else if (str.startsWith("0o")) result = Long.valueOf(str.substring(2), 8);
      else if (str.endsWith("o")) result = Long.valueOf(str.substring(0, str.length() - 1), 8);
      else if (str.endsWith("h")) result = Long.valueOf(str.substring(0, str.length() - 1), 16);
      else if (autoCalc.getBcMode() == BC_MODE_BIN) result = Long.valueOf(str, 2);
      else if (autoCalc.getBcMode() == BC_MODE_OCT) result = Long.valueOf(str, 8);
      else if (autoCalc.getBcMode() == BC_MODE_HEX) result = Long.valueOf(str, 16);
      else return new BigDecimal(str);

      return BigDecimal.valueOf(result);
    } catch (NumberFormatException e) {
      throw new AutoCalcException("意外的：" + str);
    }
  }
  
  public boolean isNumber(String str) {
    if ("".equals(str)) return false;
    if (str.startsWith(" ") || str.endsWith(" ")) str = str.trim();
    if (str.endsWith("b") || str.startsWith("0b") || autoCalc.getBcMode() == BC_MODE_BIN) {
      if(str.endsWith("b")) str = str.substring(0, str.length() - 1);
      if(str.startsWith("0b")) str = str.substring(2);

      return Pattern.matches("-?[0-1]*(\\.?)[0-1]*", str);
    }
    else if (str.endsWith("o") || str.startsWith("0o") || autoCalc.getBcMode() == BC_MODE_OCT) {
      if(str.endsWith("o")) str = str.substring(0, str.length() - 1);
      if(str.startsWith("0o")) str = str.substring(2);

      return Pattern.matches("-?[0-7]*(\\.?)[0-7]*", str);
    }
    else if (str.endsWith("h") || str.startsWith("0x") || autoCalc.getBcMode() == BC_MODE_HEX) {

      if(str.endsWith("h")) str = str.substring(0, str.length() - 1);
      if(str.startsWith("0x")) str = str.substring(2);

      return Pattern.matches("-?([0-9]|[a-f]|[A-F])*(\\.?)([0-9]|[a-f]|[A-F])*", str);
    }
    else if (str.endsWith("d") || autoCalc.getBcMode() == BC_MODE_DEC) {

      if(str.endsWith("d")) str = str.substring(0, str.length() - 1);
      return Pattern.matches("-?[0-9]*(\\.?)[0-9]*", str);
    }
    return Pattern.matches("-?[0-9]*(\\.?)[0-9]*", str);
  }
  
  public BigDecimal strToNumber(StringBuilder stringBuilder) throws AutoCalcException {
    return strToNumber(stringBuilder.toString());
  }
  
  public boolean isNumber(StringBuilder stringBuilder) {
    return isNumber(stringBuilder.toString());
  }

  public BigDecimal doubleToBigDecimal(Double d) throws AutoCalcException, AutoCalcInfiniteException {
    if(d.isNaN()) throw new AutoCalcException("计算出错");
    if(d.isInfinite()) throw new AutoCalcInfiniteException();
    return BigDecimal.valueOf(d);
  }

  
  public String getNumberStrRadix(String str) {
    if (str.startsWith(" ") || str.endsWith(" ")) str = str.trim();
    if ((str.endsWith("b") || str.startsWith("0b") && !str.startsWith("0x"))) return "b";
    else if (str.endsWith("o") || str.startsWith("0o")) return "o";
    else if (str.endsWith("h") || str.startsWith("0x")) return "h";
    return "d";
  }

  
  public boolean checkNumberRange(BigDecimal n, String type) {
    switch (type) {
      case "int":
        return  n.compareTo(BigDecimal.valueOf(Integer.MIN_VALUE)) > 0 &&  n.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) < 0;
      case "long":
        return  n.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) > 0 &&  n.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) < 0;
      case "double":
        return  n.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) > 0 &&  n.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0;
    }
    return true;
  }
  
  public void checkNumberRangeAndThrow(BigDecimal n, String type) throws AutoCalcInfiniteException {
    if(!checkNumberRange(n, type))
      throw new AutoCalcInfiniteException();
  }
}

