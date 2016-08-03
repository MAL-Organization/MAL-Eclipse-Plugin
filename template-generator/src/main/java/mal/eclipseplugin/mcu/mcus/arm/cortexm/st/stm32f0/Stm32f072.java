package mal.eclipseplugin.mcu.mcus.arm.cortexm.st.stm32f0;

import java.util.List;

public abstract class Stm32f072 extends Stm32f0 {

	@Override
	public List<String> getSymbols() {
		List<String> symbols = super.getSymbols();
		// Add stdperiph library family subset symbol
		symbols.add("STM32F072");
		return symbols;
	}

}
