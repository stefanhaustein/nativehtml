package org.kobjects.nativehtml.android;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import org.kobjects.nativehtml.css.Css;
import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.css.CssUnit;
import org.kobjects.nativehtml.layout.Layout;


class AndroidCss {
    static int getTextStyle(CssStyleDeclaration style) {
      int flags = 0;
      if (style.get(CssProperty.FONT_WEIGHT, CssUnit.NUMBER) > 600) {
        flags |= Typeface.BOLD;
      }
      if (style.getEnum(CssProperty.FONT_STYLE) == CssEnum.ITALIC) {
        flags |= Typeface.ITALIC;
      }
      return flags;
    }

    static Typeface getTypeface(CssStyleDeclaration style) {
      int flags = getTextStyle(style);
      if (!style.isSet(CssProperty.FONT_FAMILY)) {
        return Typeface.defaultFromStyle(flags);
      }
      return Typeface.create(getFontFamilyName(style), flags);
    }

    static int getPaintFlags(CssStyleDeclaration style) {
      switch (style.getEnum(CssProperty.TEXT_DECORATION)) {
        case UNDERLINE:
          return Paint.UNDERLINE_TEXT_FLAG;
        case LINE_THROUGH:
          return Paint.STRIKE_THRU_TEXT_FLAG;
        default:
          return 0;
      }
    }

    static String getFontFamilyName(CssStyleDeclaration style) {
      if (!style.isSet(CssProperty.FONT_FAMILY)) {
        return "";
      }
      String fontFamily = Css.identifierToLowerCase(style.getString(CssProperty.FONT_FAMILY));
      int cut = fontFamily.lastIndexOf(',');
      return fontFamily.substring(cut + 1).trim();
    }

    static int directiveToMeasureSpec(Layout.Directive directive) {
        switch (directive) {
            case MINIMUM:
                return View.MeasureSpec.AT_MOST;
            case STRETCH:
                return View.MeasureSpec.EXACTLY;
            case FIT_CONTENT:
                return View.MeasureSpec.UNSPECIFIED;
            default:
                throw new IllegalArgumentException();
        }
    }


}
