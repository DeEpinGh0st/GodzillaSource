package org.mozilla.javascript;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;













final class NativeDate
  extends IdScriptableObject
{
  static final long serialVersionUID = -8307438915861678966L;
  private static final Object DATE_TAG = "Date"; private static final String js_NaN_date_str = "Invalid Date"; private static final double HalfTimeDomain = 8.64E15D; private static final double HoursPerDay = 24.0D; private static final double MinutesPerHour = 60.0D; private static final double SecondsPerMinute = 60.0D; private static final double msPerSecond = 1000.0D; private static final double MinutesPerDay = 1440.0D; private static final double SecondsPerDay = 86400.0D; private static final double SecondsPerHour = 3600.0D; private static final double msPerDay = 8.64E7D; private static final double msPerHour = 3600000.0D; private static final double msPerMinute = 60000.0D; private static final int MAXARGS = 7; private static final int ConstructorId_now = -3; private static final int ConstructorId_parse = -2; private static final int ConstructorId_UTC = -1; private static final int Id_constructor = 1; private static final int Id_toString = 2; private static final int Id_toTimeString = 3; private static final int Id_toDateString = 4; private static final int Id_toLocaleString = 5; private static final int Id_toLocaleTimeString = 6; private static final int Id_toLocaleDateString = 7; private static final int Id_toUTCString = 8; private static final int Id_toSource = 9; private static final int Id_valueOf = 10; private static final int Id_getTime = 11; private static final int Id_getYear = 12; private static final int Id_getFullYear = 13; private static final int Id_getUTCFullYear = 14; private static final int Id_getMonth = 15; private static final int Id_getUTCMonth = 16; private static final int Id_getDate = 17;
  private static final int Id_getUTCDate = 18;
  private static final int Id_getDay = 19;
  private static final int Id_getUTCDay = 20;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeDate obj = new NativeDate();
    
    obj.date = ScriptRuntime.NaN;
    obj.exportAsJSClass(47, scope, sealed);
  }
  private static final int Id_getHours = 21; private static final int Id_getUTCHours = 22; private static final int Id_getMinutes = 23; private static final int Id_getUTCMinutes = 24; private static final int Id_getSeconds = 25; private static final int Id_getUTCSeconds = 26; private static final int Id_getMilliseconds = 27; private static final int Id_getUTCMilliseconds = 28; private static final int Id_getTimezoneOffset = 29; private static final int Id_setTime = 30; private static final int Id_setMilliseconds = 31; private static final int Id_setUTCMilliseconds = 32; private static final int Id_setSeconds = 33; private static final int Id_setUTCSeconds = 34; private static final int Id_setMinutes = 35; private static final int Id_setUTCMinutes = 36; private static final int Id_setHours = 37; private static final int Id_setUTCHours = 38; private static final int Id_setDate = 39; private static final int Id_setUTCDate = 40; private static final int Id_setMonth = 41; private static final int Id_setUTCMonth = 42; private static final int Id_setFullYear = 43; private static final int Id_setUTCFullYear = 44; private static final int Id_setYear = 45; private static final int Id_toISOString = 46; private static final int Id_toJSON = 47; private static final int MAX_PROTOTYPE_ID = 47; private static final int Id_toGMTString = 8; private static TimeZone thisTimeZone; private static double LocalTZA; private static DateFormat timeZoneFormatter; private static DateFormat localeDateTimeFormatter; private static DateFormat localeDateFormatter; private static DateFormat localeTimeFormatter; private double date;
  
  private NativeDate() {
    if (thisTimeZone == null) {

      
      thisTimeZone = TimeZone.getDefault();
      LocalTZA = thisTimeZone.getRawOffset();
    } 
  }


  
  public String getClassName() {
    return "Date";
  }


  
  public Object getDefaultValue(Class<?> typeHint) {
    if (typeHint == null)
      typeHint = ScriptRuntime.StringClass; 
    return super.getDefaultValue(typeHint);
  }

  
  double getJSTimeValue() {
    return this.date;
  }


  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    addIdFunctionProperty(ctor, DATE_TAG, -3, "now", 0);
    
    addIdFunctionProperty(ctor, DATE_TAG, -2, "parse", 1);
    
    addIdFunctionProperty(ctor, DATE_TAG, -1, "UTC", 7);
    
    super.fillConstructorProperties(ctor);
  }

  
  protected void initPrototypeId(int id)
  {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 7; s = "constructor"; break;
      case 2: arity = 0; s = "toString"; break;
      case 3: arity = 0; s = "toTimeString"; break;
      case 4: arity = 0; s = "toDateString"; break;
      case 5: arity = 0; s = "toLocaleString"; break;
      case 6: arity = 0; s = "toLocaleTimeString"; break;
      case 7: arity = 0; s = "toLocaleDateString"; break;
      case 8: arity = 0; s = "toUTCString"; break;
      case 9: arity = 0; s = "toSource"; break;
      case 10: arity = 0; s = "valueOf"; break;
      case 11: arity = 0; s = "getTime"; break;
      case 12: arity = 0; s = "getYear"; break;
      case 13: arity = 0; s = "getFullYear"; break;
      case 14: arity = 0; s = "getUTCFullYear"; break;
      case 15: arity = 0; s = "getMonth"; break;
      case 16: arity = 0; s = "getUTCMonth"; break;
      case 17: arity = 0; s = "getDate"; break;
      case 18: arity = 0; s = "getUTCDate"; break;
      case 19: arity = 0; s = "getDay"; break;
      case 20: arity = 0; s = "getUTCDay"; break;
      case 21: arity = 0; s = "getHours"; break;
      case 22: arity = 0; s = "getUTCHours"; break;
      case 23: arity = 0; s = "getMinutes"; break;
      case 24: arity = 0; s = "getUTCMinutes"; break;
      case 25: arity = 0; s = "getSeconds"; break;
      case 26: arity = 0; s = "getUTCSeconds"; break;
      case 27: arity = 0; s = "getMilliseconds"; break;
      case 28: arity = 0; s = "getUTCMilliseconds"; break;
      case 29: arity = 0; s = "getTimezoneOffset"; break;
      case 30: arity = 1; s = "setTime"; break;
      case 31: arity = 1; s = "setMilliseconds"; break;
      case 32: arity = 1; s = "setUTCMilliseconds"; break;
      case 33: arity = 2; s = "setSeconds"; break;
      case 34: arity = 2; s = "setUTCSeconds"; break;
      case 35: arity = 3; s = "setMinutes"; break;
      case 36: arity = 3; s = "setUTCMinutes"; break;
      case 37: arity = 4; s = "setHours"; break;
      case 38: arity = 4; s = "setUTCHours"; break;
      case 39: arity = 1; s = "setDate"; break;
      case 40: arity = 1; s = "setUTCDate"; break;
      case 41: arity = 2; s = "setMonth"; break;
      case 42: arity = 2; s = "setUTCMonth"; break;
      case 43: arity = 3; s = "setFullYear"; break;
      case 44: arity = 3; s = "setUTCFullYear"; break;
      case 45: arity = 1; s = "setYear"; break;
      case 46: arity = 0; s = "toISOString"; break;
      case 47: arity = 1; s = "toJSON"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(DATE_TAG, id, s, arity); } public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    String dataStr, toISOString;
    Scriptable o;
    Object tv, toISO;
    double year;
    String msg;
    Object result;
    if (!f.hasTag(DATE_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case -3:
        return ScriptRuntime.wrapNumber(now());

      
      case -2:
        dataStr = ScriptRuntime.toString(args, 0);
        return ScriptRuntime.wrapNumber(date_parseString(dataStr));

      
      case -1:
        return ScriptRuntime.wrapNumber(jsStaticFunction_UTC(args));



      
      case 1:
        if (thisObj != null)
          return date_format(now(), 2); 
        return jsConstructor(args);


      
      case 47:
        toISOString = "toISOString";
        
        o = ScriptRuntime.toObject(cx, scope, thisObj);
        tv = ScriptRuntime.toPrimitive(o, ScriptRuntime.NumberClass);
        if (tv instanceof Number) {
          double d = ((Number)tv).doubleValue();
          if (d != d || Double.isInfinite(d)) {
            return null;
          }
        } 
        toISO = ScriptableObject.getProperty(o, "toISOString");
        if (toISO == NOT_FOUND) {
          throw ScriptRuntime.typeError2("msg.function.not.found.in", "toISOString", ScriptRuntime.toString(o));
        }

        
        if (!(toISO instanceof Callable)) {
          throw ScriptRuntime.typeError3("msg.isnt.function.in", "toISOString", ScriptRuntime.toString(o), ScriptRuntime.toString(toISO));
        }


        
        result = ((Callable)toISO).call(cx, scope, o, ScriptRuntime.emptyArgs);
        
        if (!ScriptRuntime.isPrimitive(result)) {
          throw ScriptRuntime.typeError1("msg.toisostring.must.return.primitive", ScriptRuntime.toString(result));
        }
        
        return result;
    } 




    
    if (!(thisObj instanceof NativeDate))
      throw incompatibleCallError(f); 
    NativeDate realThis = (NativeDate)thisObj;
    double t = realThis.date;
    
    switch (id) {
      
      case 2:
      case 3:
      case 4:
        if (t == t) {
          return date_format(t, id);
        }
        return "Invalid Date";
      
      case 5:
      case 6:
      case 7:
        if (t == t) {
          return toLocale_helper(t, id);
        }
        return "Invalid Date";
      
      case 8:
        if (t == t) {
          return js_toUTCString(t);
        }
        return "Invalid Date";
      
      case 9:
        return "(new Date(" + ScriptRuntime.toString(t) + "))";
      
      case 10:
      case 11:
        return ScriptRuntime.wrapNumber(t);
      
      case 12:
      case 13:
      case 14:
        if (t == t) {
          if (id != 14) t = LocalTime(t); 
          t = YearFromTime(t);
          if (id == 12) {
            if (cx.hasFeature(1)) {
              if (1900.0D <= t && t < 2000.0D) {
                t -= 1900.0D;
              }
            } else {
              t -= 1900.0D;
            } 
          }
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 15:
      case 16:
        if (t == t) {
          if (id == 15) t = LocalTime(t); 
          t = MonthFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 17:
      case 18:
        if (t == t) {
          if (id == 17) t = LocalTime(t); 
          t = DateFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 19:
      case 20:
        if (t == t) {
          if (id == 19) t = LocalTime(t); 
          t = WeekDay(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 21:
      case 22:
        if (t == t) {
          if (id == 21) t = LocalTime(t); 
          t = HourFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 23:
      case 24:
        if (t == t) {
          if (id == 23) t = LocalTime(t); 
          t = MinFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 25:
      case 26:
        if (t == t) {
          if (id == 25) t = LocalTime(t); 
          t = SecFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 27:
      case 28:
        if (t == t) {
          if (id == 27) t = LocalTime(t); 
          t = msFromTime(t);
        } 
        return ScriptRuntime.wrapNumber(t);
      
      case 29:
        if (t == t) {
          t = (t - LocalTime(t)) / 60000.0D;
        }
        return ScriptRuntime.wrapNumber(t);
      
      case 30:
        t = TimeClip(ScriptRuntime.toNumber(args, 0));
        realThis.date = t;
        return ScriptRuntime.wrapNumber(t);
      
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
        t = makeTime(t, args, id);
        realThis.date = t;
        return ScriptRuntime.wrapNumber(t);
      
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
        t = makeDate(t, args, id);
        realThis.date = t;
        return ScriptRuntime.wrapNumber(t);

      
      case 45:
        year = ScriptRuntime.toNumber(args, 0);
        
        if (year != year || Double.isInfinite(year)) {
          t = ScriptRuntime.NaN;
        } else {
          if (t != t) {
            t = 0.0D;
          } else {
            t = LocalTime(t);
          } 
          
          if (year >= 0.0D && year <= 99.0D) {
            year += 1900.0D;
          }
          double day = MakeDay(year, MonthFromTime(t), DateFromTime(t));
          
          t = MakeDate(day, TimeWithinDay(t));
          t = internalUTC(t);
          t = TimeClip(t);
        } 
        
        realThis.date = t;
        return ScriptRuntime.wrapNumber(t);
      
      case 46:
        if (t == t) {
          return js_toISOString(t);
        }
        msg = ScriptRuntime.getMessage0("msg.invalid.date");
        throw ScriptRuntime.constructError("RangeError", msg);
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }

















  
  private static double Day(double t) {
    return Math.floor(t / 8.64E7D);
  }


  
  private static double TimeWithinDay(double t) {
    double result = t % 8.64E7D;
    if (result < 0.0D)
      result += 8.64E7D; 
    return result;
  }

  
  private static boolean IsLeapYear(int year) {
    return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
  }




  
  private static double DayFromYear(double y) {
    return 365.0D * (y - 1970.0D) + Math.floor((y - 1969.0D) / 4.0D) - Math.floor((y - 1901.0D) / 100.0D) + Math.floor((y - 1601.0D) / 400.0D);
  }


  
  private static double TimeFromYear(double y) {
    return DayFromYear(y) * 8.64E7D;
  }

  
  private static int YearFromTime(double t) {
    int lo = (int)Math.floor(t / 8.64E7D / 366.0D) + 1970;
    int hi = (int)Math.floor(t / 8.64E7D / 365.0D) + 1970;


    
    if (hi < lo) {
      int temp = lo;
      lo = hi;
      hi = temp;
    } 






    
    while (hi > lo) {
      int mid = (hi + lo) / 2;
      if (TimeFromYear(mid) > t) {
        hi = mid - 1; continue;
      } 
      lo = mid + 1;
      if (TimeFromYear(lo) > t) {
        return mid;
      }
    } 
    
    return lo;
  }

  
  private static double DayFromMonth(int m, int year) {
    int day = m * 30;
    
    if (m >= 7) { day += m / 2 - 1; }
    else if (m >= 2) { day += (m - 1) / 2 - 1; }
    else { day += m; }
    
    if (m >= 2 && IsLeapYear(year)) day++;
    
    return day;
  }


  
  private static int DaysInMonth(int year, int month) {
    if (month == 2)
      return IsLeapYear(year) ? 29 : 28; 
    return (month >= 8) ? (31 - (month & 0x1)) : (30 + (month & 0x1));
  }



  
  private static int MonthFromTime(double t) {
    int mstart, year = YearFromTime(t);
    int d = (int)(Day(t) - DayFromYear(year));
    
    d -= 59;
    if (d < 0) {
      return (d < -28) ? 0 : 1;
    }
    
    if (IsLeapYear(year)) {
      if (d == 0)
        return 1; 
      d--;
    } 

    
    int estimate = d / 30;
    
    switch (estimate) { case 0:
        return 2;
      case 1: mstart = 31; break;
      case 2: mstart = 61; break;
      case 3: mstart = 92; break;
      case 4: mstart = 122; break;
      case 5: mstart = 153; break;
      case 6: mstart = 184; break;
      case 7: mstart = 214; break;
      case 8: mstart = 245; break;
      case 9: mstart = 275; break;
      case 10: return 11;
      default: throw Kit.codeBug(); }

    
    return (d >= mstart) ? (estimate + 2) : (estimate + 1);
  }

  
  private static int DateFromTime(double t) {
    int mdays, mstart, year = YearFromTime(t);
    int d = (int)(Day(t) - DayFromYear(year));
    
    d -= 59;
    if (d < 0) {
      return (d < -28) ? (d + 31 + 28 + 1) : (d + 28 + 1);
    }
    
    if (IsLeapYear(year)) {
      if (d == 0)
        return 29; 
      d--;
    } 


    
    switch (d / 30) { case 0:
        return d + 1;
      case 1: mdays = 31; mstart = 31; break;
      case 2: mdays = 30; mstart = 61; break;
      case 3: mdays = 31; mstart = 92; break;
      case 4: mdays = 30; mstart = 122; break;
      case 5: mdays = 31; mstart = 153; break;
      case 6: mdays = 31; mstart = 184; break;
      case 7: mdays = 30; mstart = 214; break;
      case 8: mdays = 31; mstart = 245; break;
      case 9: mdays = 30; mstart = 275; break;
      case 10:
        return d - 275 + 1;
      default: throw Kit.codeBug(); }
    
    d -= mstart;
    if (d < 0)
    {
      d += mdays;
    }
    return d + 1;
  }


  
  private static int WeekDay(double t) {
    double result = Day(t) + 4.0D;
    result %= 7.0D;
    if (result < 0.0D)
      result += 7.0D; 
    return (int)result;
  }

  
  private static double now() {
    return System.currentTimeMillis();
  }





  
  private static double DaylightSavingTA(double t) {
    if (t < 0.0D) {
      int year = EquivalentYear(YearFromTime(t));
      double day = MakeDay(year, MonthFromTime(t), DateFromTime(t));
      t = MakeDate(day, TimeWithinDay(t));
    } 
    Date date = new Date((long)t);
    if (thisTimeZone.inDaylightTime(date)) {
      return 3600000.0D;
    }
    return 0.0D;
  }








  
  private static int EquivalentYear(int year) {
    int day = (int)DayFromYear(year) + 4;
    day %= 7;
    if (day < 0) {
      day += 7;
    }
    if (IsLeapYear(year)) {
      switch (day) { case 0:
          return 1984;
        case 1: return 1996;
        case 2: return 1980;
        case 3: return 1992;
        case 4: return 1976;
        case 5: return 1988;
        case 6: return 1972; }
    
    } else {
      switch (day) { case 0:
          return 1978;
        case 1: return 1973;
        case 2: return 1985;
        case 3: return 1986;
        case 4: return 1981;
        case 5: return 1971;
        case 6: return 1977; }

    
    } 
    throw Kit.codeBug();
  }

  
  private static double LocalTime(double t) {
    return t + LocalTZA + DaylightSavingTA(t);
  }

  
  private static double internalUTC(double t) {
    return t - LocalTZA - DaylightSavingTA(t - LocalTZA);
  }


  
  private static int HourFromTime(double t) {
    double result = Math.floor(t / 3600000.0D) % 24.0D;
    if (result < 0.0D)
      result += 24.0D; 
    return (int)result;
  }


  
  private static int MinFromTime(double t) {
    double result = Math.floor(t / 60000.0D) % 60.0D;
    if (result < 0.0D)
      result += 60.0D; 
    return (int)result;
  }


  
  private static int SecFromTime(double t) {
    double result = Math.floor(t / 1000.0D) % 60.0D;
    if (result < 0.0D)
      result += 60.0D; 
    return (int)result;
  }


  
  private static int msFromTime(double t) {
    double result = t % 1000.0D;
    if (result < 0.0D)
      result += 1000.0D; 
    return (int)result;
  }


  
  private static double MakeTime(double hour, double min, double sec, double ms) {
    return ((hour * 60.0D + min) * 60.0D + sec) * 1000.0D + ms;
  }


  
  private static double MakeDay(double year, double month, double date) {
    year += Math.floor(month / 12.0D);
    
    month %= 12.0D;
    if (month < 0.0D) {
      month += 12.0D;
    }
    double yearday = Math.floor(TimeFromYear(year) / 8.64E7D);
    double monthday = DayFromMonth((int)month, (int)year);
    
    return yearday + monthday + date - 1.0D;
  }

  
  private static double MakeDate(double day, double time) {
    return day * 8.64E7D + time;
  }

  
  private static double TimeClip(double d) {
    if (d != d || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY || Math.abs(d) > 8.64E15D)
    {


      
      return ScriptRuntime.NaN;
    }
    if (d > 0.0D) {
      return Math.floor(d + 0.0D);
    }
    return Math.ceil(d + 0.0D);
  }











  
  private static double date_msecFromDate(double year, double mon, double mday, double hour, double min, double sec, double msec) {
    double day = MakeDay(year, mon, mday);
    double time = MakeTime(hour, min, sec, msec);
    double result = MakeDate(day, time);
    return result;
  }



  
  private static double date_msecFromArgs(Object[] args) {
    double[] array = new double[7];


    
    for (int loop = 0; loop < 7; loop++) {
      if (loop < args.length) {
        double d = ScriptRuntime.toNumber(args[loop]);
        if (d != d || Double.isInfinite(d)) {
          return ScriptRuntime.NaN;
        }
        array[loop] = ScriptRuntime.toInteger(args[loop]);
      }
      else if (loop == 2) {
        array[loop] = 1.0D;
      } else {
        array[loop] = 0.0D;
      } 
    } 


    
    if (array[0] >= 0.0D && array[0] <= 99.0D) {
      array[0] = array[0] + 1900.0D;
    }
    return date_msecFromDate(array[0], array[1], array[2], array[3], array[4], array[5], array[6]);
  }


  
  private static double jsStaticFunction_UTC(Object[] args) {
    return TimeClip(date_msecFromArgs(args));
  }









  
  private static double parseISOString(String s) {
    int ERROR = -1;
    int YEAR = 0, MONTH = 1, DAY = 2;
    int HOUR = 3, MIN = 4, SEC = 5, MSEC = 6;
    int TZHOUR = 7, TZMIN = 8;
    int state = 0;
    
    int[] values = { 1970, 1, 1, 0, 0, 0, 0, -1, -1 };
    int yearlen = 4, yearmod = 1, tzmod = 1;
    int i = 0, len = s.length();
    if (len != 0) {
      char c = s.charAt(0);
      if (c == '+' || c == '-') {
        
        i++;
        yearlen = 6;
        yearmod = (c == '-') ? -1 : 1;
      } else if (c == 'T') {
        
        i++;
        state = 3;
      } 
    } 
    label126: while (state != -1) {
      int m = i + ((state == 0) ? yearlen : ((state == 6) ? 3 : 2));
      if (m > len) {
        state = -1;
        
        break;
      } 
      int value = 0;
      for (; i < m; i++) {
        char c1 = s.charAt(i);
        if (c1 < '0' || c1 > '9') { state = -1; break label126; }
         value = 10 * value + c1 - 48;
      } 
      values[state] = value;
      
      if (i == len) {
        
        switch (state) {
          case 3:
          case 7:
            state = -1;
            break;
        } 
        break;
      } 
      char c = s.charAt(i++);
      if (c == 'Z') {
        
        values[7] = 0;
        values[8] = 0;
        switch (state) {
          case 4:
          case 5:
          case 6:
            break;
        } 
        state = -1;

        
        break;
      } 
      
      switch (state) {
        case 0:
        case 1:
          state = (c == '-') ? (state + 1) : ((c == 'T') ? 3 : -1);
          break;
        case 2:
          state = (c == 'T') ? 3 : -1;
          break;
        case 3:
          state = (c == ':') ? 4 : -1;
          break;

        
        case 7:
          if (c != ':')
          {
            i--;
          }
          state = 8;
          break;
        case 4:
          state = (c == ':') ? 5 : ((c == '+' || c == '-') ? 7 : -1);
          break;
        case 5:
          state = (c == '.') ? 6 : ((c == '+' || c == '-') ? 7 : -1);
          break;
        case 6:
          state = (c == '+' || c == '-') ? 7 : -1;
          break;
        case 8:
          state = -1;
          break;
      } 
      if (state == 7)
      {
        tzmod = (c == '-') ? -1 : 1;
      }
    } 


    
    if (state != -1 && i == len) {

      
      int year = values[0], month = values[1], day = values[2];
      int hour = values[3], min = values[4], sec = values[5], msec = values[6];
      int tzhour = values[7], tzmin = values[8];
      if (year <= 275943 && month >= 1 && month <= 12 && day >= 1 && day <= DaysInMonth(year, month) && hour <= 24 && (hour != 24 || (min <= 0 && sec <= 0 && msec <= 0)) && min <= 59 && sec <= 59 && tzhour <= 23 && tzmin <= 59) {











        
        double date = date_msecFromDate((year * yearmod), (month - 1), day, hour, min, sec, msec);
        
        if (tzhour != -1)
        {



          
          date -= (tzhour * 60 + tzmin) * 60000.0D * tzmod;
        }
        
        if (date >= -8.64E15D && date <= 8.64E15D) {
          return date;
        }
      } 
    } 
    return ScriptRuntime.NaN;
  }

  
  private static double date_parseString(String s) {
    double d = parseISOString(s);
    if (d == d) {
      return d;
    }
    
    int year = -1;
    int mon = -1;
    int mday = -1;
    int hour = -1;
    int min = -1;
    int sec = -1;
    char c = Character.MIN_VALUE;
    char si = Character.MIN_VALUE;
    int i = 0;
    int n = -1;
    double tzoffset = -1.0D;
    char prevc = Character.MIN_VALUE;
    int limit = 0;
    boolean seenplusminus = false;
    
    limit = s.length();
    while (i < limit) {
      c = s.charAt(i);
      i++;
      if (c <= ' ' || c == ',' || c == '-') {
        if (i < limit) {
          si = s.charAt(i);
          if (c == '-' && '0' <= si && si <= '9') {
            prevc = c;
          }
        } 
        continue;
      } 
      if (c == '(') {
        int depth = 1;
        while (i < limit) {
          c = s.charAt(i);
          i++;
          if (c == '(') {
            depth++; continue;
          }  if (c == ')' && 
            --depth <= 0)
            break; 
        } 
        continue;
      } 
      if ('0' <= c && c <= '9') {
        n = c - 48;
        while (i < limit && '0' <= (c = s.charAt(i)) && c <= '9') {
          n = n * 10 + c - 48;
          i++;
        } 







        
        if (prevc == '+' || prevc == '-')
        
        { seenplusminus = true;

          
          if (n < 24) {
            n *= 60;
          } else {
            n = n % 100 + n / 100 * 60;
          }  if (prevc == '+')
            n = -n; 
          if (tzoffset != 0.0D && tzoffset != -1.0D)
            return ScriptRuntime.NaN; 
          tzoffset = n; }
        else if (n >= 70 || (prevc == '/' && mon >= 0 && mday >= 0 && year < 0))
        
        { 
          
          if (year >= 0)
            return ScriptRuntime.NaN; 
          if (c <= ' ' || c == ',' || c == '/' || i >= limit)
          { year = (n < 100) ? (n + 1900) : n; }
          else
          { return ScriptRuntime.NaN; }  }
        else if (c == ':')
        { if (hour < 0)
          { hour = n; }
          else if (min < 0)
          { min = n; }
          else
          { return ScriptRuntime.NaN; }  }
        else if (c == '/')
        { if (mon < 0)
          { mon = n - 1; }
          else if (mday < 0)
          { mday = n; }
          else
          { return ScriptRuntime.NaN; }  }
        else { if (i < limit && c != ',' && c > ' ' && c != '-')
            return ScriptRuntime.NaN; 
          if (seenplusminus && n < 60) {
            if (tzoffset < 0.0D)
            { tzoffset -= n; }
            else
            { tzoffset += n; } 
          } else if (hour >= 0 && min < 0) {
            min = n;
          } else if (min >= 0 && sec < 0) {
            sec = n;
          } else if (mday < 0) {
            mday = n;
          } else {
            return ScriptRuntime.NaN;
          }  }
         prevc = Character.MIN_VALUE; continue;
      }  if (c == '/' || c == ':' || c == '+' || c == '-') {
        prevc = c; continue;
      } 
      int st = i - 1;
      while (i < limit) {
        c = s.charAt(i);
        if (('A' > c || c > 'Z') && ('a' > c || c > 'z'))
          break; 
        i++;
      } 
      int letterCount = i - st;
      if (letterCount < 2) {
        return ScriptRuntime.NaN;
      }



      
      String wtb = "am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;";




      
      int index = 0;
      int wtbOffset = 0; while (true) {
        int wtbNext = wtb.indexOf(';', wtbOffset);
        if (wtbNext < 0)
          return ScriptRuntime.NaN; 
        if (wtb.regionMatches(true, wtbOffset, s, st, letterCount))
          break; 
        wtbOffset = wtbNext + 1;
        index++;
      } 
      if (index < 2) {



        
        if (hour > 12 || hour < 0)
          return ScriptRuntime.NaN; 
        if (index == 0) {
          
          if (hour == 12)
            hour = 0; 
          continue;
        } 
        if (hour != 12)
          hour += 12;  continue;
      } 
      index -= 2; if (index < 7)
        continue; 
      index -= 7; if (index < 12) {
        
        if (mon < 0) {
          mon = index; continue;
        } 
        return ScriptRuntime.NaN;
      } 
      
      index -= 12;
      
      switch (index) { case 0:
          tzoffset = 0.0D; continue;
        case 1: tzoffset = 0.0D; continue;
        case 2: tzoffset = 0.0D; continue;
        case 3: tzoffset = 300.0D; continue;
        case 4: tzoffset = 240.0D; continue;
        case 5: tzoffset = 360.0D; continue;
        case 6: tzoffset = 300.0D; continue;
        case 7: tzoffset = 420.0D; continue;
        case 8: tzoffset = 360.0D; continue;
        case 9: tzoffset = 480.0D; continue;
        case 10: tzoffset = 420.0D; continue; }
       Kit.codeBug();
    } 


    
    if (year < 0 || mon < 0 || mday < 0)
      return ScriptRuntime.NaN; 
    if (sec < 0)
      sec = 0; 
    if (min < 0)
      min = 0; 
    if (hour < 0) {
      hour = 0;
    }
    double msec = date_msecFromDate(year, mon, mday, hour, min, sec, 0.0D);
    if (tzoffset == -1.0D) {
      return internalUTC(msec);
    }
    return msec + tzoffset * 60000.0D;
  }


  
  private static String date_format(double t, int methodId) {
    StringBuilder result = new StringBuilder(60);
    double local = LocalTime(t);




    
    if (methodId != 3) {
      appendWeekDayName(result, WeekDay(local));
      result.append(' ');
      appendMonthName(result, MonthFromTime(local));
      result.append(' ');
      append0PaddedUint(result, DateFromTime(local), 2);
      result.append(' ');
      int year = YearFromTime(local);
      if (year < 0) {
        result.append('-');
        year = -year;
      } 
      append0PaddedUint(result, year, 4);
      if (methodId != 4) {
        result.append(' ');
      }
    } 
    if (methodId != 4) {
      append0PaddedUint(result, HourFromTime(local), 2);
      result.append(':');
      append0PaddedUint(result, MinFromTime(local), 2);
      result.append(':');
      append0PaddedUint(result, SecFromTime(local), 2);


      
      int minutes = (int)Math.floor((LocalTZA + DaylightSavingTA(t)) / 60000.0D);

      
      int offset = minutes / 60 * 100 + minutes % 60;
      if (offset > 0) {
        result.append(" GMT+");
      } else {
        result.append(" GMT-");
        offset = -offset;
      } 
      append0PaddedUint(result, offset, 4);
      
      if (timeZoneFormatter == null) {
        timeZoneFormatter = new SimpleDateFormat("zzz");
      }

      
      if (t < 0.0D) {
        int equiv = EquivalentYear(YearFromTime(local));
        double day = MakeDay(equiv, MonthFromTime(t), DateFromTime(t));
        t = MakeDate(day, TimeWithinDay(t));
      } 
      result.append(" (");
      Date date = new Date((long)t);
      synchronized (timeZoneFormatter) {
        result.append(timeZoneFormatter.format(date));
      } 
      result.append(')');
    } 
    return result.toString();
  }


  
  private static Object jsConstructor(Object[] args) {
    NativeDate obj = new NativeDate();


    
    if (args.length == 0) {
      obj.date = now();
      return obj;
    } 

    
    if (args.length == 1) {
      double date; Object arg0 = args[0];
      if (arg0 instanceof Scriptable) {
        arg0 = ((Scriptable)arg0).getDefaultValue(null);
      }
      if (arg0 instanceof CharSequence) {
        
        date = date_parseString(arg0.toString());
      } else {
        
        date = ScriptRuntime.toNumber(arg0);
      } 
      obj.date = TimeClip(date);
      return obj;
    } 
    
    double time = date_msecFromArgs(args);
    
    if (!Double.isNaN(time) && !Double.isInfinite(time)) {
      time = TimeClip(internalUTC(time));
    }
    obj.date = time;
    
    return obj;
  }

  
  private static String toLocale_helper(double t, int methodId) {
    DateFormat formatter;
    switch (methodId) {
      case 5:
        if (localeDateTimeFormatter == null) {
          localeDateTimeFormatter = DateFormat.getDateTimeInstance(1, 1);
        }

        
        formatter = localeDateTimeFormatter;
        break;
      case 6:
        if (localeTimeFormatter == null) {
          localeTimeFormatter = DateFormat.getTimeInstance(1);
        }
        
        formatter = localeTimeFormatter;
        break;
      case 7:
        if (localeDateFormatter == null) {
          localeDateFormatter = DateFormat.getDateInstance(1);
        }
        
        formatter = localeDateFormatter; break;
      default:
        throw new AssertionError();
    } 
    
    synchronized (formatter) {
      return formatter.format(new Date((long)t));
    } 
  }

  
  private static String js_toUTCString(double date) {
    StringBuilder result = new StringBuilder(60);
    
    appendWeekDayName(result, WeekDay(date));
    result.append(", ");
    append0PaddedUint(result, DateFromTime(date), 2);
    result.append(' ');
    appendMonthName(result, MonthFromTime(date));
    result.append(' ');
    int year = YearFromTime(date);
    if (year < 0) {
      result.append('-'); year = -year;
    } 
    append0PaddedUint(result, year, 4);
    result.append(' ');
    append0PaddedUint(result, HourFromTime(date), 2);
    result.append(':');
    append0PaddedUint(result, MinFromTime(date), 2);
    result.append(':');
    append0PaddedUint(result, SecFromTime(date), 2);
    result.append(" GMT");
    return result.toString();
  }
  
  private static String js_toISOString(double t) {
    StringBuilder result = new StringBuilder(27);
    
    int year = YearFromTime(t);
    if (year < 0) {
      result.append('-');
      append0PaddedUint(result, -year, 6);
    } else if (year > 9999) {
      append0PaddedUint(result, year, 6);
    } else {
      append0PaddedUint(result, year, 4);
    } 
    result.append('-');
    append0PaddedUint(result, MonthFromTime(t) + 1, 2);
    result.append('-');
    append0PaddedUint(result, DateFromTime(t), 2);
    result.append('T');
    append0PaddedUint(result, HourFromTime(t), 2);
    result.append(':');
    append0PaddedUint(result, MinFromTime(t), 2);
    result.append(':');
    append0PaddedUint(result, SecFromTime(t), 2);
    result.append('.');
    append0PaddedUint(result, msFromTime(t), 3);
    result.append('Z');
    return result.toString();
  }

  
  private static void append0PaddedUint(StringBuilder sb, int i, int minWidth) {
    if (i < 0) Kit.codeBug(); 
    int scale = 1;
    minWidth--;
    if (i >= 10) {
      if (i < 1000000000) {
        while (true) {
          int newScale = scale * 10;
          if (i < newScale)
            break;  minWidth--;
          scale = newScale;
        } 
      } else {
        
        minWidth -= 9;
        scale = 1000000000;
      } 
    }
    while (minWidth > 0) {
      sb.append('0');
      minWidth--;
    } 
    while (scale != 1) {
      sb.append((char)(48 + i / scale));
      i %= scale;
      scale /= 10;
    } 
    sb.append((char)(48 + i));
  }




  
  private static void appendMonthName(StringBuilder sb, int index) {
    String months = "JanFebMarAprMayJunJulAugSepOctNovDec";
    
    index *= 3;
    for (int i = 0; i != 3; i++) {
      sb.append(months.charAt(index + i));
    }
  }

  
  private static void appendWeekDayName(StringBuilder sb, int index) {
    String days = "SunMonTueWedThuFriSat";
    index *= 3;
    for (int i = 0; i != 3; i++)
      sb.append(days.charAt(index + i)); 
  }
  
  private static double makeTime(double date, Object[] args, int methodId) {
    int maxargs;
    double hour, min, sec, msec, lorutime;
    if (args.length == 0)
    {







      
      return ScriptRuntime.NaN;
    }

    
    boolean local = true;
    switch (methodId) {
      case 32:
        local = false;
      
      case 31:
        maxargs = 1;
        break;
      
      case 34:
        local = false;
      
      case 33:
        maxargs = 2;
        break;
      
      case 36:
        local = false;
      
      case 35:
        maxargs = 3;
        break;
      
      case 38:
        local = false;
      
      case 37:
        maxargs = 4;
        break;
      
      default:
        throw Kit.codeBug();
    } 
    
    boolean hasNaN = false;
    int numNums = (args.length < maxargs) ? args.length : maxargs;
    assert numNums <= 4;
    double[] nums = new double[4]; int i;
    for (i = 0; i < numNums; i++) {
      double d = ScriptRuntime.toNumber(args[i]);
      if (d != d || Double.isInfinite(d)) {
        hasNaN = true;
      } else {
        nums[i] = ScriptRuntime.toInteger(d);
      } 
    } 


    
    if (hasNaN || date != date) {
      return ScriptRuntime.NaN;
    }
    
    i = 0; int stop = numNums;


    
    if (local) {
      lorutime = LocalTime(date);
    } else {
      lorutime = date;
    } 
    if (maxargs >= 4 && i < stop) {
      hour = nums[i++];
    } else {
      hour = HourFromTime(lorutime);
    } 
    if (maxargs >= 3 && i < stop) {
      min = nums[i++];
    } else {
      min = MinFromTime(lorutime);
    } 
    if (maxargs >= 2 && i < stop) {
      sec = nums[i++];
    } else {
      sec = SecFromTime(lorutime);
    } 
    if (maxargs >= 1 && i < stop) {
      msec = nums[i++];
    } else {
      msec = msFromTime(lorutime);
    } 
    double time = MakeTime(hour, min, sec, msec);
    double result = MakeDate(Day(lorutime), time);
    
    if (local) {
      result = internalUTC(result);
    }
    return TimeClip(result);
  }
  
  private static double makeDate(double date, Object[] args, int methodId) {
    int maxargs;
    double year, month, lorutime;
    if (args.length == 0) {
      return ScriptRuntime.NaN;
    }

    
    boolean local = true;
    switch (methodId) {
      case 40:
        local = false;
      
      case 39:
        maxargs = 1;
        break;
      
      case 42:
        local = false;
      
      case 41:
        maxargs = 2;
        break;
      
      case 44:
        local = false;
      
      case 43:
        maxargs = 3;
        break;
      
      default:
        throw Kit.codeBug();
    } 
    
    boolean hasNaN = false;
    int numNums = (args.length < maxargs) ? args.length : maxargs;
    assert 1 <= numNums && numNums <= 3;
    double[] nums = new double[3]; int i;
    for (i = 0; i < numNums; i++) {
      double d = ScriptRuntime.toNumber(args[i]);
      if (d != d || Double.isInfinite(d)) {
        hasNaN = true;
      } else {
        nums[i] = ScriptRuntime.toInteger(d);
      } 
    } 

    
    if (hasNaN) {
      return ScriptRuntime.NaN;
    }
    
    i = 0; int stop = numNums;




    
    if (date != date) {
      if (maxargs < 3) {
        return ScriptRuntime.NaN;
      }
      lorutime = 0.0D;
    
    }
    else if (local) {
      lorutime = LocalTime(date);
    } else {
      lorutime = date;
    } 
    
    if (maxargs >= 3 && i < stop) {
      year = nums[i++];
    } else {
      year = YearFromTime(lorutime);
    } 
    if (maxargs >= 2 && i < stop) {
      month = nums[i++];
    } else {
      month = MonthFromTime(lorutime);
    } 
    if (maxargs >= 1 && i < stop) {
      day = nums[i++];
    } else {
      day = DateFromTime(lorutime);
    } 
    double day = MakeDay(year, month, day);
    double result = MakeDate(day, TimeWithinDay(lorutime));
    
    if (local) {
      result = internalUTC(result);
    }
    return TimeClip(result);
  }






  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 6:
        c = s.charAt(0);
        if (c == 103) { X = "getDay"; id = 19; break; }
         if (c == 116) { X = "toJSON"; id = 47; }  break;
      case 7:
        switch (s.charAt(3)) { case 'D':
            c = s.charAt(0);
            if (c == 103) { X = "getDate"; id = 17; break; }
             if (c == 115) { X = "setDate"; id = 39; }  break;
          case 'T':
            c = s.charAt(0);
            if (c == 103) { X = "getTime"; id = 11; break; }
             if (c == 115) { X = "setTime"; id = 30; }  break;
          case 'Y':
            c = s.charAt(0);
            if (c == 103) { X = "getYear"; id = 12; break; }
             if (c == 115) { X = "setYear"; id = 45; }  break;
          case 'u':
            X = "valueOf"; id = 10; break; }  break;
      case 8:
        switch (s.charAt(3)) { case 'H':
            c = s.charAt(0);
            if (c == 103) { X = "getHours"; id = 21; break; }
             if (c == 115) { X = "setHours"; id = 37; }  break;
          case 'M':
            c = s.charAt(0);
            if (c == 103) { X = "getMonth"; id = 15; break; }
             if (c == 115) { X = "setMonth"; id = 41; }  break;
          case 'o':
            X = "toSource"; id = 9; break;
          case 't': X = "toString"; id = 2; break; }  break;
      case 9:
        X = "getUTCDay"; id = 20; break;
      case 10: c = s.charAt(3);
        if (c == 77) {
          c = s.charAt(0);
          if (c == 103) { X = "getMinutes"; id = 23; break; }
           if (c == 115) { X = "setMinutes"; id = 35; }
           break;
        }  if (c == 83) {
          c = s.charAt(0);
          if (c == 103) { X = "getSeconds"; id = 25; break; }
           if (c == 115) { X = "setSeconds"; id = 33; }
           break;
        }  if (c == 85) {
          c = s.charAt(0);
          if (c == 103) { X = "getUTCDate"; id = 18; break; }
           if (c == 115) { X = "setUTCDate"; id = 40; } 
        }  break;
      case 11:
        switch (s.charAt(3)) { case 'F':
            c = s.charAt(0);
            if (c == 103) { X = "getFullYear"; id = 13; break; }
             if (c == 115) { X = "setFullYear"; id = 43; }  break;
          case 'M':
            X = "toGMTString"; id = 8; break;
          case 'S': X = "toISOString"; id = 46; break;
          case 'T': X = "toUTCString"; id = 8; break;
          case 'U': c = s.charAt(0);
            if (c == 103) {
              c = s.charAt(9);
              if (c == 114) { X = "getUTCHours"; id = 22; break; }
               if (c == 116) { X = "getUTCMonth"; id = 16; }
               break;
            }  if (c == 115) {
              c = s.charAt(9);
              if (c == 114) { X = "setUTCHours"; id = 38; break; }
               if (c == 116) { X = "setUTCMonth"; id = 42; } 
            }  break;
          case 's':
            X = "constructor"; id = 1; break; }  break;
      case 12:
        c = s.charAt(2);
        if (c == 68) { X = "toDateString"; id = 4; break; }
         if (c == 84) { X = "toTimeString"; id = 3; }  break;
      case 13:
        c = s.charAt(0);
        if (c == 103) {
          c = s.charAt(6);
          if (c == 77) { X = "getUTCMinutes"; id = 24; break; }
           if (c == 83) { X = "getUTCSeconds"; id = 26; }
           break;
        }  if (c == 115) {
          c = s.charAt(6);
          if (c == 77) { X = "setUTCMinutes"; id = 36; break; }
           if (c == 83) { X = "setUTCSeconds"; id = 34; } 
        }  break;
      case 14:
        c = s.charAt(0);
        if (c == 103) { X = "getUTCFullYear"; id = 14; break; }
         if (c == 115) { X = "setUTCFullYear"; id = 44; break; }
         if (c == 116) { X = "toLocaleString"; id = 5; }  break;
      case 15:
        c = s.charAt(0);
        if (c == 103) { X = "getMilliseconds"; id = 27; break; }
         if (c == 115) { X = "setMilliseconds"; id = 31; }  break;
      case 17:
        X = "getTimezoneOffset"; id = 29; break;
      case 18: c = s.charAt(0);
        if (c == 103) { X = "getUTCMilliseconds"; id = 28; break; }
         if (c == 115) { X = "setUTCMilliseconds"; id = 32; break; }
         if (c == 116) {
          c = s.charAt(8);
          if (c == 68) { X = "toLocaleDateString"; id = 7; break; }
           if (c == 84) { X = "toLocaleTimeString"; id = 6; }
        
        }  break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }
}
