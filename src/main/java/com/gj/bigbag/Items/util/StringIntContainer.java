package com.gj.bigbag.Items.util;

public class StringIntContainer
{
    public String str;
    public int integer;

    public StringIntContainer (String str, int integer)
    {
        this.str = str;
        this.integer = integer;
    }
    @Override
    public int hashCode()
    {
        return str.hashCode() + integer;
    }
    @Override
    public String toString()
    {
        return str + ("-" + integer);
    }

    public boolean Equals(String s, int i)
    {
        return str.equals(i) & integer == i;
    }

    public boolean equals(StringIntContainer c)
    {
        return str.equals(c.str) & integer == c.integer;
    }

    public static StringIntContainer Parse(String str)
    {
        int breakingPoint = str.lastIndexOf('-');

        return new StringIntContainer(
                str.substring(0, breakingPoint),
                Integer.parseInt(str.substring(breakingPoint + 1, str.length()))
        );
    }
}
