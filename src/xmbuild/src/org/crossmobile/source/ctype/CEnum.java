/* Copyright (c) 2011 by crossmobile.org
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrossMobile; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.crossmobile.source.ctype;

import java.util.List;
import org.crossmobile.source.utils.StringUtils;

public class CEnum extends CProcedural {

    private final List<String> values;
    private final boolean resetArgNames;

    public CEnum(String name, List<String> values, String original, String filename, boolean resetArgNames) {
        super(name, original, filename);
        this.values = values;
        this.resetArgNames = resetArgNames;
    }

    public boolean resetsArgNames() {
        return resetArgNames;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "[" + name + " " + values + "]";
    }

    public static void create(CLibrary parent, boolean isTypedef, String entry) {
        String original = entry;
        if (entry.startsWith("typedef"))
            entry = entry.substring(7).trim();
        if (entry.startsWith("extern"))
            entry = entry.substring(7).trim();
        if (entry.startsWith("enum"))
            entry = entry.substring(4).trim();
        if (entry.charAt(entry.length() - 1) == ';')
            entry = entry.substring(0, entry.length() - 1).trim();

        if (entry.indexOf('{') < 0) {
            if (isTypedef)
                CArgument.create(parent, parent, isTypedef, entry);
            else if (StringUtils.findFirstWord(entry) != entry.length())
                throw new RuntimeException("Unknown struct: " + original);
        } else {
            int begin = StringUtils.findFirstWord(entry);
            int end = StringUtils.findLastWord(entry);

            String corename = null;
            if (begin >= 0 && end >= 0) {
                corename = entry.substring(end);
                String secondary = entry.substring(0, begin);
                if (!corename.equals(secondary))
                    CType.registerTypedef(corename, secondary);
            } else if (begin >= 0)
                corename = entry.substring(0, begin);
            else if (end >= 0)
                corename = entry.substring(end);

            if (corename != null)
                CType.registerTypedef("int", corename);
        }
    }
}
