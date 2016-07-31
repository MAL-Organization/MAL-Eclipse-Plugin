package mal.eclipseplugin.mcu.mcus.st.stm32f0;

import mal.eclipseplugin.mcu.Mcu;
import mal.eclipseplugin.mcu.McuFamily;

public abstract class Stm32f0 implements Mcu {

	public McuFamily getFamily() {
		return McuFamily.CORTEX_M0;
	}

}
