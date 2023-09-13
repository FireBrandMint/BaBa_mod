package com.gj.baba.components.substances;

public class SubstancePlasma extends Substance
{
    protected SubstancePlasma()
    {
        super();
    }

    @Override
    protected Substance instantiate()
    {
        return new SubstancePlasma();
    }
}
