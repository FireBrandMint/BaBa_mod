package com.gj.baba.components.substances;

public class SubstanceOxygen extends Substance
{
    @Override
    protected Substance instantiate() {
        return new SubstanceOxygen();
    }
}
