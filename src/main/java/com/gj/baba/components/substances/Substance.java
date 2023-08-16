package com.gj.baba.components.substances;

import java.util.ArrayList;

public class Substance
{
    //Static part of the class below.
    static boolean initialized = false;
    private static ArrayList<Substance> substancesIndexed = new ArrayList<Substance>(20);
    public static int PLASMA_ID;
    public static int OXYGEN_ID;
    public static void InitializeSubstances()
    {
        if(initialized) throw new RuntimeException("FUCK YOU, DO NOT USE THIS METHOD, ARE YOU STUPID?");
        initialized = true;

        PLASMA_ID = IndexSubstance(new SubstancePlasma());

        OXYGEN_ID = IndexSubstance(new SubstanceOxygen());
    }

    private static int IndexSubstance(Substance sub)
    {
        int toReturn = substancesIndexed.size();

        sub.setID(toReturn);
        substancesIndexed.add(sub);
        return toReturn;
    }

    public static Substance getSubstance(int substanceID)
    {
        return substancesIndexed.get(substanceID).cloneSelf();
    }

    //Object part of the class below.
    private boolean indexSet = false;
    private int index;
    /**
     * Moles multiplied by 100.
     */
    public int moles = 0;
    /**
     * Temperature in celcius multiplied by 100.
     */
    public int temperatureC = 0;

    protected Substance ()
    {

    }

    /**
     * returns volume * 100.
     */
    public long getPressure(int volume)
    {
        long n = this.moles;
        long r = getGasConstant();
        long t = temperatureC;
        long v = volume;

        return (n * r * t) / v;
    }

    //Original standard atmosphere temperature for the base gas constant.
    public int getGasConstant()
    {
        return 831;
    }

    private void setID(int i)
    {
        if(indexSet) throw new RuntimeException("Can't set index more than 1 time.");

        indexSet = true;
        index = i;
    }

    public int getID()
    {
        if(!indexSet) throw new RuntimeException("Can't get index that hasn't been set.");
        return index;
    }

    protected Substance instantiate()
    {
        return new Substance();
    }

    public void copyContentsTo(Substance substance)
    {
        substance.indexSet = indexSet;
        substance.index = index;
        substance.moles = moles;
        substance.temperatureC = temperatureC;
    }

    private Substance cloneSelf()
    {
        Substance self = instantiate();
        copyContentsTo(self);
        return self;
    }
}
