// Copyright 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.kobjects.nativehtml.css;

import java.net.URI;
import java.util.LinkedHashMap;

public class CssStyleDeclaration {
  /**
   * CSS DPI constant.
   */
  public static final int DPI = 96;

  public static final float FONT_WEIGHT_NORMAL = 400;
  public static final float FONT_WEIGHT_BOLD = 700;

  private static final int[] TOP_LEVEL = new int[0];

  private static final CssStyleDeclaration EMPTY_STYLE = new CssStyleDeclaration();

  // values() has to create a defensive copy each time, so we cache them here.
  private static final CssUnit[] CSS_UNITS = CssUnit.values();
  private static final CssProperty[] CSS_PROPERTIES = CssProperty.values();
  private static final CssEnum[] CSS_ENUM_VALUES = CssEnum.values();

  private static final LinkedHashMap<String, CssProperty> NAME_TO_PROPERTY_MAP = new LinkedHashMap<>();
  private static final LinkedHashMap<String, CssEnum> NAME_TO_VALUE_MAP = new LinkedHashMap<>();
  private static final LinkedHashMap<String, CssUnit> NAME_TO_UNIT_MAP = new LinkedHashMap<>();

  static {
    for (CssProperty property : CSS_PROPERTIES) {
      NAME_TO_PROPERTY_MAP.put(Css.cssName(property.name()), property);
    }
    for (CssEnum value : CSS_ENUM_VALUES) {
      NAME_TO_VALUE_MAP.put(Css.cssName(value.name()), value);
    }
    for (CssUnit unit : CSS_UNITS) {
      NAME_TO_UNIT_MAP.put(unit == CssUnit.PERCENT ? "%" : Css.cssName(unit.name()), unit);
    }
  }
  
  public static CssStyleDeclaration fromString(String s) {
	  CssStyleDeclaration decl = new CssStyleDeclaration();
	  decl.read(null, s);
	  return decl;
  }
  

  private float[] values;
  private byte[] units;
  String backgroundImage;
  String fontFamily;

  /**
   * Specificity of this style, setFrom by the corresponding selector in the
   * stylesheet parse method.
   */
  int specificity;

  /**
   * Position of this style declaration inside the style sheet
   */
  int position;

  /**
   * Nested import positions
   */
  int[] nesting = TOP_LEVEL;

  /**
   * Compares the specificity of this style to s2 and returns the difference.
   */
  public int compareSpecificity(CssStyleDeclaration s2) {
    if (specificity > s2.specificity) {
      return 1;
    } else if (specificity < s2.specificity) {
      return -1;
    } else {
      int min = Math.min(nesting.length, s2.nesting.length);
      for (int i = 0; i < min; i++) {
        if (nesting[i] > s2.nesting[i]) {
          return 1;
        } else if (nesting[i] < s2.nesting[i]) {
          return -1;
        }
      }
      int n1 = min + 1 < nesting.length ? nesting[min + 1] : position;
      int n2 = min + 1 < s2.nesting.length ? s2.nesting[min + 1] : s2.position;
      return n1 - n2;
    }
  }

  public float get(CssProperty property, CssUnit unit) {
    return get(property, unit, 0);
  }

  public float get(CssProperty property, CssUnit requestUnit, float base) {
    int id = property.ordinal();
    float value;
    if (values != null && id < values.length && units[id] != 0) {
      value = values[id];
    } else {
      switch (property) {
        case BORDER_TOP_WIDTH:
        case BORDER_BOTTOM_WIDTH:
        case BORDER_LEFT_WIDTH:
        case BORDER_RIGHT_WIDTH:
          value = CssEnum.MEDIUM.ordinal();
          break;
        case BOTTOM:
        case HEIGHT:
        case LEFT:
        case RIGHT:
        case TABLE_LAYOUT:
        case TOP:
        case WIDTH:
          value = CssEnum.AUTO.ordinal();
          break;
        case BACKGROUND_COLOR:
          value = 0x000ffffff;  // Transparent white
          break;
        case DISPLAY:
          value = CssEnum.INLINE.ordinal();
          break;
        case FONT_SIZE:
          value = 12;  // 12pt
          break;
        case FONT_WEIGHT:
          value = FONT_WEIGHT_NORMAL;
          break;
        case LINE_HEIGHT:
          value = 100;
          break;
        case LIST_STYLE_TYPE:
          value = CssEnum.DISC.ordinal();
          break;
        case POSITION:
          value = CssEnum.STATIC.ordinal();
          break;
        case BACKGROUND_REPEAT:
          value = CssEnum.REPEAT.ordinal();
          break;
        default:
          switch (getUnit(property)) {
            case ENUM:
              value = CssEnum.NONE.ordinal();
              break;
            case ARGB:
              value = 0x0ff000000;
              break;
            default:
              value = 0;
          }
      }
    }

    CssUnit valueUnit = getUnit(property);
    if (valueUnit == requestUnit) {
      return value;
    }

    if (valueUnit == CssUnit.ENUM && requestUnit == CssUnit.ARGB) {
      switch (CSS_ENUM_VALUES[(int) value]) {
        case WHITE: return 0xffffffff;
        case SILVER: return 0xffc0c0c0;
        case GRAY: return 0xff808080;
        case RED: return 0x0ffff0000;
        case MAROON: return 0xff800000;
        case YELLOW: return 0xffffff00;
        case OLIVE: return 0xff808000;
        case LIME: return 0xff00ff00;
        case GREEN: return 0xff008000;
        case AQUA: return 0xff00ffff;
        case TEAL: return 0xff008080;
        case BLUE: return 0xff0000ff;
        case NAVY: return 0xff000080;
        case FUCHSIA: return 0xffff00ff;
        case PURPLE: return 0xff800080;
        default:
          return 0xff000000;
      }
    }

    // Convert value to PX

    switch (valueUnit) {
      case PERCENT:
        value = base * value / 100;
        break;
      case PX:
      case NUMBER:
        break;  // No conversion
      case ENUM:
        if (value == CssEnum.NONE.ordinal()) {
          // 0 = 0
        } else if (id >= CssProperty.BORDER_TOP_WIDTH.ordinal()
            && id <= CssProperty.BORDER_LEFT_WIDTH.ordinal()) {
          if (value == CssEnum.THIN.ordinal()) {
            value = 1;
          } else if (value == CssEnum.THICK.ordinal()){
            value = 3;
          } else {
            value = 2;
          }
        } else {
          System.err.println("CssStyleDeclaration: Can't convert enum " + value + " to " + requestUnit + " for " + property);
          value = 0;
        }
        break;
      case EM:
        value *= get(CssProperty.FONT_SIZE, CssUnit.PX);
        break;
      case EX:
        value *= get(CssProperty.FONT_SIZE, CssUnit.PX) / 2;
        break;
      case IN:
        value *= DPI;
        break;
      case CM:
        value *= DPI / 2.54f;
        break;
      case MM:
        value *= DPI / 25.4f;
        break;
      case PT:
        value *= DPI / 72;
        break;
      case PC:
        value *= DPI / 6;
        break;
      default:
        value = 0;
        System.err.println("CssStyleDeclaration: Can't convert enum " + value + " to " + requestUnit + " for " + property);
    }

    // Convert value in PX to requested unit.

    switch (requestUnit) {
      case EM:
        return value / get(CssProperty.FONT_SIZE, CssUnit.PX);
      case EX:
        return value / get(CssProperty.FONT_SIZE, CssUnit.PX) / 2f;
      case IN:
        return value / DPI;
      case CM:
        return value * 2.54f / DPI;
      case MM:
        return value * 25.4f / DPI;
      case PT:
        return value * 72 / DPI;
      case PC:
        return value * 6 / DPI;
      default: // PX, NONE
        return  value;
    }
  }

  public float getPx(CssProperty property, float base) {
	  return get(property, CssUnit.PX, base);
  }
  
  public int getBackgroundReferencePoint(CssProperty property, int containerLength, int imageLength) {
    float percent;
    switch(getUnit(property)) {
      case ENUM:
        switch(getEnum(property)) {
          case TOP:
          case LEFT:
            return 0;
          case CENTER:
            percent = 50;
            break;
          case RIGHT:
          case BOTTOM:
            percent = 100;
            break;
          default:
            return 0;
        }
        break;
      case PERCENT:
        percent = get(property, CssUnit.PERCENT);
        break;
      default:
        return Math.round(get(property, CssUnit.PX));
    }
    return Math.round((containerLength - imageLength) * percent / 100F);
  }

  public int getColor(CssProperty property) {
    return (int) get(property, CssUnit.ARGB);
  }

  public CssEnum getEnum(CssProperty property) {
    return CSS_ENUM_VALUES[(int) get(property, CssUnit.ENUM)];
  }

  public String getString(CssProperty property) {
    if (!isSet(property)) {
      return null;
    }
    if (property == CssProperty.BACKGROUND_IMAGE) {
      return backgroundImage;
    } else if (property == CssProperty.FONT_FAMILY) {
      return fontFamily;
    }
    CssUnit unit = getUnit(property);
    switch (unit) {
      case ARGB:
        return '#' + Integer.toString((getColor(property) & 0x0ffffff) | 0x01000000, 16).substring(1);
      case ENUM:
        return Css.cssName(getEnum(property).name());
      default:
        StringBuilder buf = new StringBuilder();
        float value = get(property, unit);
        if (value == (int) value) {
          buf.append((int) value);
        } else {
          buf.append(value);
        }
        if (unit == CssUnit.PERCENT) {
          buf.append("%");
        } else if (unit != CssUnit.NUMBER) {
          buf.append(Css.cssName(unit.name()));
        }
        return buf.toString();
    }
  }

  CssUnit getUnit(CssProperty property) {
    int id = property.ordinal();
    if (units != null && id < units.length && units[id] != 0) {
      return CSS_UNITS[units[id]];
    }
    switch(property) {
      case BACKGROUND_POSITION_X:
      case BACKGROUND_POSITION_Y:
      case LINE_HEIGHT:
        return CssUnit.PERCENT;
      case BACKGROUND_COLOR:
      case COLOR:
      case BORDER_TOP_COLOR:
      case BORDER_RIGHT_COLOR:
      case BORDER_BOTTOM_COLOR:
      case BORDER_LEFT_COLOR:
        return CssUnit.ARGB;
      case MARGIN_TOP:
      case MARGIN_RIGHT:
      case MARGIN_BOTTOM:
      case MARGIN_LEFT:
      case PADDING_TOP:
      case PADDING_RIGHT:
      case PADDING_BOTTOM:
      case PADDING_LEFT:
      case FONT_WEIGHT:
        return CssUnit.NUMBER;
      case FONT_SIZE:
        return CssUnit.PT;
      default:
        return CssUnit.ENUM;
    }
  }

  /**
   * Inherit values from the given style. Needs to be called before a style can
   * be used in order to get resolve INHERIT values and percentages for font-size and
   * line-height.
   *
   * @param from the style to inherit from
   */
  public void inherit(CssStyleDeclaration from) {
    if (from == null) {
      from = EMPTY_STYLE;
    }

    int max = Math.max(values == null ? 0 : values.length, from.values == null ? 0 : from.values.length);

    for (int id = 0; id < max; id++) {
      CssProperty property = CSS_PROPERTIES[id];
      if (getUnit(property) == CssUnit.ENUM && getEnum(property) == CssEnum.INHERIT) {
        if (property == CssProperty.BACKGROUND_IMAGE) {
          backgroundImage = from.backgroundImage;
        } else if (property == CssProperty.FONT_FAMILY) {
          fontFamily = from.fontFamily;
        } else {
          CssUnit unit = from.getUnit(property);
          set(property, from.get(property, unit), unit);
        }
      } else if (getUnit(property) == CssUnit.PERCENT) {
        if (property == CssProperty.FONT_SIZE) {
          CssUnit unit = from.getUnit(property);
          set(property, from.get(property, unit) * get(property, CssUnit.PERCENT) / 100.0f, unit);
        } else if (property == CssProperty.LINE_HEIGHT) {
          CssUnit unit = getUnit(CssProperty.FONT_SIZE);
          set(property, get(CssProperty.FONT_SIZE, unit) * get(property, CssUnit.PERCENT) / 100.0f, unit);
        }
      } else if (from.isSet(property) && id < CssProperty.TEXT_PROPERTY_COUNT
          && property != CssProperty.BACKGROUND_COLOR
          && property != CssProperty.DISPLAY) {
        // Can't simply be merged with explicit inherit at the top because the percent rules
        // have higher priority.
        if (property == CssProperty.FONT_FAMILY) {
          fontFamily = from.fontFamily;
        }
        CssUnit unit = from.getUnit(property);
        set(property, from.get(property, unit), unit);
      }
    }
  }

  public boolean isBlock() {
    CssEnum display = getEnum(CssProperty.DISPLAY);
    return display == CssEnum.BLOCK || display == CssEnum.TABLE || display == CssEnum.LIST_ITEM;
  }

  public boolean isLenghtFixed(CssProperty property) {
	    switch (getUnit(property)) {
	      case CM:
	      case EM:
	      case EX:
	      case IN:
	      case MM:
	      case NUMBER:
	      case PC:
	      case PX:
	      case PT:
	        return true;
	      default: 
	       	return false;
	    }
  }
  
  public boolean isLengthFixedOrPercent(CssProperty property) {
     return getUnit(property) == CssUnit.PERCENT || isLenghtFixed(property);
  }

  /**
   * Reads a style declaration from a string.
   */
  public void read(URI url, String def) {
    CssTokenizer ct = new CssTokenizer(url, def);
    read(ct);
  }

  /**
   * Reads a style declaration from a CSS tokenizer.
   */
  void read(CssTokenizer tokenizer) {
    while (tokenizer.ttype != CssTokenizer.TT_EOF && tokenizer.ttype != '}') {
      if (tokenizer.ttype == CssTokenizer.TT_IDENT) {
        String name = tokenizer.sval;
        CssProperty idObj = NAME_TO_PROPERTY_MAP.get(name);
        if (idObj == null) {
          tokenizer.debug("unrecognized property");
        }
        tokenizer.nextToken(false);
        if (tokenizer.ttype != ':') {
          continue;
        }
        tokenizer.nextToken(false);

        int pos = 0;
        loop : while (true) {
          switch (tokenizer.ttype) {
            case CssTokenizer.TT_HASH:
              setColor(idObj, '#' + tokenizer.sval, pos);
              break;

            case CssTokenizer.TT_DIMENSION:
              set(idObj, tokenizer.nval,
                  NAME_TO_UNIT_MAP.get(tokenizer.sval), pos);
              break;

            case CssTokenizer.TT_NUMBER:
              set(idObj, tokenizer.nval, CssUnit.NUMBER, pos);
              break;

            case CssTokenizer.TT_PERCENTAGE:
              set(idObj, tokenizer.nval, CssUnit.PERCENT, pos);
              break;

            case CssTokenizer.TT_IDENT:
              CssEnum value = NAME_TO_VALUE_MAP.get(tokenizer.sval);
              if (value != null) {
                set(idObj, value.ordinal(), CssUnit.ENUM, pos);
              } else if (idObj == CssProperty.FONT || idObj == CssProperty.FONT_FAMILY) {
                fontFamily = (fontFamily == null ? "" : fontFamily) + tokenizer.sval;
              } else {
                tokenizer.debug("Unrecognized value '" + value + "' for property " + name);
              }
              break;

            case CssTokenizer.TT_URI:
              if (idObj == CssProperty.BACKGROUND || idObj == CssProperty.BACKGROUND_IMAGE) {
                backgroundImage = tokenizer.sval;
              }
              break;

            case ',':
            case CssTokenizer.TT_STRING:
              if (idObj == CssProperty.FONT || idObj == CssProperty.FONT_FAMILY) {
                fontFamily = (fontFamily == null ? "" : fontFamily) + tokenizer.sval;
              }
              break;

            default:
              break loop;
          }
          pos++;
          tokenizer.nextToken(false);
        }
      }

      // handle !important
      if (tokenizer.ttype == '!') {
        tokenizer.nextToken(false);
        if (tokenizer.ttype == CssTokenizer.TT_IDENT &&
            "important".equals(tokenizer.sval)) {
          specificity = Css.SPECIFICITY_IMPORTANT;
          tokenizer.nextToken(false);
        }
      }

      // skip trailing trash
      while (tokenizer.ttype != CssTokenizer.TT_EOF
          && tokenizer.ttype != ';' && tokenizer.ttype != '}') {
        tokenizer.debug("skipping");
        tokenizer.nextToken(false);
      }
      while (tokenizer.ttype == ';') {
        tokenizer.nextToken(false);
      }
    }
  }


  public void setFrom(CssStyleDeclaration from) {
	if (from == null) {
		return;
	}
    if (from.values != null) {
      for (int i = 0; i < from.values.length; i++) {
        CssProperty property = CSS_PROPERTIES[i];
        if (from.isSet(property)) {
          CssUnit unit = from.getUnit(property);
          set(property, from.get(property, unit), unit);
        }
      }
    }
    if (from.backgroundImage != null) {
      backgroundImage = from.backgroundImage;
    }
    if (from.fontFamily != null) {
      fontFamily = from.fontFamily;
    }
  }


  public CssStyleDeclaration set(CssProperty property, float value, CssUnit unit) {
    int id = property.ordinal();
    if (id >= CssProperty.REGULAR_PROPERTY_COUNT) {
      // Multivalue / special property: default to 0.
      return set(property, value, unit, 0);
    }
    if (values == null || id >= values.length) {
      int newSize = id >= CssProperty.TEXT_PROPERTY_COUNT
          ? CssProperty.REGULAR_PROPERTY_COUNT : CssProperty.TEXT_PROPERTY_COUNT;
      float[] newValues = new float[newSize];
      byte[] newUnits = new byte[newSize];
      if (values != null) {
        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(units, 0, newUnits, 0, units.length);
      }
      values = newValues;
      units = newUnits;
    }
    values[id] = value;
    units[id] = (byte) unit.ordinal();
    return this;
  }

  public CssStyleDeclaration set(CssProperty property, float value, CssUnit unit, int multivaluePos) {
    if (property == null) {
      return this;
    }
    int multivalueStart = -1;
    switch (property) {
      case BORDER:
        if (unit == CssUnit.ARGB) {
          set(CssProperty.BORDER_COLOR, value, unit, 0);
        } else if (multivaluePos == 0) {
          set(CssProperty.BORDER_WIDTH, value, unit, 0);
        } else if (multivaluePos == 1) {
          set(CssProperty.BORDER_STYLE, value, unit, 0);
        } else if (multivaluePos == 3) {
          set(CssProperty.BORDER_COLOR, value, unit, 0);
        }
        break;
      case BACKGROUND:
        if (unit == CssUnit.ENUM && value == CssEnum.INHERIT.ordinal() && multivaluePos == 0) {
          setEnum(CssProperty.BACKGROUND_COLOR, CssEnum.INHERIT);
          setEnum(CssProperty.BACKGROUND_REPEAT, CssEnum.INHERIT);
          setEnum(CssProperty.BACKGROUND_POSITION_X, CssEnum.INHERIT);
          setEnum(CssProperty.BACKGROUND_POSITION_Y, CssEnum.INHERIT);
        } else if (unit == CssUnit.ARGB) {
          set(CssProperty.BACKGROUND_COLOR, value, unit);
        } else if (unit == CssUnit.ENUM
            && (value == CssEnum.NO_REPEAT.ordinal() || value == CssEnum.REPEAT.ordinal()
              || value == CssEnum.REPEAT_X.ordinal() || value == CssEnum.REPEAT_Y.ordinal())) {
          set(CssProperty.BACKGROUND_REPEAT, value, CssUnit.ENUM);
        } else if (unit == CssUnit.ENUM
            && (value == CssEnum.SCROLL.ordinal() || value == CssEnum.FIXED.ordinal())){
          // ignore attachment
        } else {
          if (!isSet(CssProperty.BACKGROUND_POSITION_X)) {
            set(CssProperty.BACKGROUND_POSITION_X, value, unit);
          }
          set(CssProperty.BACKGROUND_POSITION_Y, value, unit);
        }
        break;
      case BACKGROUND_POSITION:
        if (multivaluePos == 0) {
          set(CssProperty.BACKGROUND_POSITION_X, value, unit);
        }
        if (multivaluePos == 0 || multivaluePos == 1) {
          set(CssProperty.BACKGROUND_POSITION_Y, value, unit);
        }
        break;
      case LIST_STYLE:
        if (multivaluePos == 0 && unit == CssUnit.ENUM && value == CssEnum.INHERIT.ordinal()) {
          setEnum(CssProperty.LIST_STYLE_POSITION, CssEnum.INHERIT);
          setEnum(CssProperty.LIST_STYLE_TYPE, CssEnum.INHERIT);
        } else if (unit == CssUnit.ENUM && 
            (value == CssEnum.INSIDE.ordinal() || value == CssEnum.OUTSIDE.ordinal())) {
          set(CssProperty.LIST_STYLE_POSITION, value, unit);
        } else {
          set(CssProperty.LIST_STYLE_TYPE, value, unit);
        }
        break;
      case FONT:
        if (unit == CssUnit.NUMBER) {
          set(CssProperty.FONT_WEIGHT, value, unit);
        }
        break;
      case BORDER_COLOR:
        multivalueStart = CssProperty.BORDER_TOP_COLOR.ordinal();
        break;
      case BORDER_STYLE:
        multivalueStart = CssProperty.BORDER_TOP_STYLE.ordinal();
        break;
      case BORDER_WIDTH:
        multivalueStart = CssProperty.BORDER_TOP_WIDTH.ordinal();
        break;
      case PADDING:
        multivalueStart = CssProperty.PADDING_TOP.ordinal();
        break;
      case MARGIN:
        multivalueStart = CssProperty.MARGIN_TOP.ordinal();
        break;
      default:
        if (property.ordinal() < CssProperty.REGULAR_PROPERTY_COUNT) {
          set(property, value, unit);
        }
    }
    if (multivalueStart != -1) {
      for (int i = 0; i < 4; i += multivaluePos == 0 ? 1 : 2) {
        set(CSS_PROPERTIES[multivalueStart + i], value, unit);
      }
    }
    return this;
  }

  public boolean isSet(CssProperty property) {
    if (property == CssProperty.BACKGROUND_IMAGE) {
      return backgroundImage != null;
    }
    if (property == CssProperty.FONT_FAMILY) {
      return fontFamily != null;
    }
    return values != null && values.length > property.ordinal() && units[property.ordinal()] != 0;
  }

  /**
   * Set a CSS color value.
   *
   * @param id field id (COLOR, ...)
   * @param color the color in the form #RGB or #RRGGBB or one of the 16 CSS
   *              color identifiers
   * @param pos index of the border-color value (0..3); 0 otherwise
   */
  public void setColor(CssProperty id, String color, int pos) {
    if (color.length() > 0 && color.charAt(0) == '#') {
      try {
        // #RGB or #RRGGBB hexadecimal color value
        int value = Integer.parseInt(color.substring(1), 16);
        if (color.length() == 4) {
          value = (value & 0x00f) | ((value & 0x0ff) << 4)
              | ((value & 0xff0) << 8) | ((value & 0xf00) << 12);
        }
        // setFrom with transparency opaque
        set(id, 0x0ff000000 | value, CssUnit.ARGB, pos);
      } catch (NumberFormatException e) {
        // ignore invalid colors
      }
    } else {
      CssEnum value = NAME_TO_VALUE_MAP.get(Css.identifierToLowerCase(color));
      if (value != null) {
        set(id, value.ordinal(), CssUnit.ENUM, pos);
      }
    }
  }


  public CssStyleDeclaration setEnum(CssProperty property, CssEnum value) {
    return set(property, value.ordinal(), CssUnit.ENUM);
  }


  public String toString() {
    StringBuilder sb = new StringBuilder();
    toString(null, sb);
    return sb.toString();
  }

  public void toString(String indent, StringBuilder sb) {
    if (values != null) {
      for (int id = 0; id < values.length; id++){
        CssProperty property = CSS_PROPERTIES[id];
        if (isSet(property)) {
          if (indent != null) {
        	  sb.append(indent);
          }
          sb.append(Css.cssName(property.name())).append(": ");
          sb.append(getString(property));
          sb.append(indent == null ? "; " : ";\n");
        }
      }
    }
    if (indent != null) {
    	sb.append("/* specifity: " + specificity + " */\n");
    }
  }

/*
  @Override
  public String getPropertyValue(String name) {
    throw new RuntimeException("NYI");
  }

  @Override
  public void setProperty(String name, String value) {
    throw new RuntimeException("NYI");
  }
  */
}
