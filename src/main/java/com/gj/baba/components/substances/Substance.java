package com.gj.baba.components.substances;

import com.gj.baba.util.BabaUtil;

import java.util.ArrayList;

public class Substance
{
    //Static part of the class below.
    static boolean initialized = false;
    private static ArrayList<Substance> substancesIndexed = new ArrayList<Substance>(20);
    public static int PLASMA_ID;
    public static int OXYGEN_ID;
    public static int DEBUG_ID;
    public static void InitializeSubstances()
    {
        if(initialized) throw new RuntimeException("FUCK YOU, DO NOT USE THIS METHOD, ARE YOU STUPID?");
        initialized = true;

        DEBUG_ID = IndexSubstance(new Substance(), "debug");
        PLASMA_ID = IndexSubstance(new SubstancePlasma(), "plasma");
        OXYGEN_ID = IndexSubstance(new SubstanceOxygen(), "oxygen");
    }

    private static int IndexSubstance(Substance sub, String name)
    {
        int toReturn = substancesIndexed.size();

        sub.setID(toReturn);
        sub.setName(name);
        substancesIndexed.add(sub);
        return toReturn;
    }

    public static Substance getSubstance(int substanceID)
    {
        return substancesIndexed.get(substanceID).cloneSelf();
    }

    public static int getSubstanceCount()
    {
        return substancesIndexed.size();
    }

    public static final double gasConstant = 8.31;

    //Object part of the class below.
    private boolean indexSet = false;
    private int index;
    private String name;
    /**
     * Moles
     */
    protected float moles = 0;
    /**
     * Temperature in kelvin multiplied by 100.
     */
    protected float temperatureK = 0;

    double cachedPressure = 0;

    boolean updated = true;

    protected Substance ()
    {

    }

    public void addMolesToThis(Substance sub)
    {
        moles += sub.moles;

        updated = true;
    }

    public float getMoles()
    {
        return moles;
    }

    public float getTemperatureK ()
    {
        return temperatureK;
    }

    public void setMoles (float value)
    {
        value = value < 0f ? 0f : value;
        updated = updated || moles != value;

        moles = value;
    }

    public void setTemperatureK (float value)
    {
        value = value < 0f ? 0f : value;

        updated = updated || temperatureK != value;

        temperatureK = value;
    }

    /**
     * Return pressure * 100.
     * @param volume The volume in square meter.
     */
    public double getPressure(float volume)
    {
        if(volume <= 0f) throw new RuntimeException("Volume cannot be 0 or lower.");

        if(updated)
        {
            double n = this.moles;
            double r = gasConstant;
            double t = temperatureK;
            double v = volume;

            updated = false;

            double p = (n * r * t) / v;

            cachedPressure = p;

            return p;
        }

        return cachedPressure;
    }

    /**
     * Return kpa * 100.
     * * @param volume The volume in square meter * 100.
     */
    public double getKPA(float volume)
    {
        return getPressure(volume) * 0.001;
    }

    /**
     *
     * @param other
     * @param volumethis volume of this in m2
     * @param volumeOther volume of other substance in m2
     * @return transfer amount in moles
     */
    public double getTransferAmount(Substance other, float volumethis, float volumeOther)
    {
        double otherPressure = other.getKPA(volumeOther);

        return getTransferAmount(otherPressure, volumethis);
    }

    public double getTransferAmount(double pressureOther, float volumethis)
    {
        double thisPressure = getKPA(volumethis);
        double otherPressure = pressureOther;

        if(otherPressure >= thisPressure || thisPressure == 0.0) return 0.0;

        if(otherPressure == 0.0) return thisPressure;
        double d1 = thisPressure;
        double d2 = otherPressure;

        double transfer = (1.0 - Math.min(d2 / d1, 1.0)) * this.moles;

        if(transfer > this.moles) transfer = this.moles;

        return transfer;
    }

    public static double getTransferAmount(float molesThis, float kpaThis, float kpaOther)
    {
        double thisPressure = kpaThis;
        double otherPressure = kpaOther;

        if(otherPressure >= thisPressure || thisPressure == 0.0) return 0.0;

        if(otherPressure == 0.0) return thisPressure;
        double d1 = thisPressure;
        double d2 = otherPressure;

        double transfer = (1.0 - Math.min(d2 / d1, 1.0)) * molesThis;

        if(transfer > molesThis) transfer = molesThis;

        return transfer;
    }

    public static double getTransferPercentage (float molesThis, float kpaThis, float kpaOther)
    {
        if(molesThis == 0.0) return 0.0;
        if(kpaOther == 0.0) return molesThis;
        double amount = getTransferAmount(molesThis, kpaThis, kpaOther);

        return amount / molesThis;
    }

    public Substance slicePorcentage(double percentage)
    {
        Substance clone = this.cloneSelf();

        float result = (float)(this.moles * percentage);

        clone.moles = result;

        this.moles -= result;

        return clone;
    }

    /**
     *
     * @param other
     * @param volumethis volume of this in m2
     * @param volumeOther volume of other substance in m2
     * @return Did it transfer?
     */
    public boolean transferNaturallyTo(Substance other, float volumethis, float volumeOther)
    {
        if(other == null) return false;
        if(other.getMoles() == 0.0)
        {
            other.setMoles(this.getMoles());
            other.setTemperatureK(this.getTemperatureK());
            this.setMoles(0);
            this.setTemperatureK(0);
            return true;
        }

        double transfer = getTransferAmount(other, volumethis, volumeOther);

        if(transfer == 0.0) return false;

        double molesAmount = this.moles * (transfer / this.getKPA(volumethis));

        double finaltemp = finalMixtureHeat(
                (float)molesAmount, this.temperatureK, this.getGramsPerMole(),
                other.moles, other.temperatureK, other.getGramsPerMole()
        );

        this.setMoles((float)(this.moles - molesAmount));
        other.setMoles((float)(other.moles + molesAmount));
        other.setTemperatureK((float)(finaltemp + 273.15));

        return true;
    }

    public static double finalMixtureHeat(
            float molesThis, float heatThis, double gramMoleThis,
            float molesOther, float heatOther, double gramMoleOther
    )
    {
        double gramthis = molesThis * gramMoleThis;
        double f1this = (heatThis - 273.15) * gramthis;
        double gramother = molesOther * gramMoleOther;
        double f1other = (heatOther - 273.15) * gramother;

        double finaltemp = (f1this + f1other) / (gramthis + gramother);

        return finaltemp + 273.15;
    }

    public double getTransferPerSuperiorKPA()
    {
        return 0.1;
    }

    protected double heatCapacity ()
    {
        return 0.7;
    }

    public double getGramsPerMole()
    {
        return 28.96;
    }

    //CONSTANTS END

    public void Serialize(BabaUtil.IntSerializer buff)
    {
        buff.write(index);
        buff.write((int)((double)moles * 100.0));
        buff.write((int)((double)temperatureK * 100.0));
    }

    protected void deserialize (BabaUtil.IntDeserializer buff)
    {

    }

    public static Substance Deserialize(BabaUtil.IntDeserializer buff)
    {
        Substance sub = getSubstance(buff.read());
        sub.setMoles(buff.read() * 0.01f);
        sub.setTemperatureK(buff.read() * 0.01f);

        sub.deserialize(buff);
        return sub;
    }

    private void setName(String str)
    {
        name = str;
    }

    public String getName()
    {
        return name;
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
        substance.name = name;
        substance.moles = moles;
        substance.temperatureK = temperatureK;

        substance.updated = true;
    }

    public Substance cloneSelf()
    {
        Substance self = instantiate();
        copyContentsTo(self);
        return self;
    }
}
