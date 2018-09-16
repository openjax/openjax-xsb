/* Copyright (c) 2006 OpenJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.openjax.xsb.helper.formatter;

public final class MethodModule extends FormatModule {
  @Override
  String format(final String formated, String token) {
    if (token.trim().lastIndexOf(";") != token.trim().length() - 1) {
      if (token.trim().indexOf("else") == -1 && token.trim().indexOf("else ") == -1 && ((token.trim().indexOf("static") == 0 && token.trim().length() == 6) || (token.trim().indexOf(" ") != -1 && token.trim().indexOf(" ") < token.trim().indexOf("(") - 1 && (token.trim().lastIndexOf(")") == token.length() - 1 || token.trim().lastIndexOf(")") < token.trim().lastIndexOf("throws"))))) {
        for (int i = 0; i < getDepth(); i++)
          token = TAB + token;

        if (getLastModule() instanceof CloseBracketModule || getLastModule() instanceof FieldModule)
          token = "\n\n" + token;
        else
          token = "\n" + token;
      }
    }

    return token;
  }
}