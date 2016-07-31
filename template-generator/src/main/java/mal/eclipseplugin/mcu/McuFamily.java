/**
 * @author Olivier Allaire
 * @copyright Copyright (c) 2015 Olivier Allaire
 * @par This file is part of MAL.
 *
 * MAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package mal.eclipseplugin.mcu;

public enum McuFamily {
	
	CORTEX_M0("ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.mcpu.cortex-m0");
	
	private final String value;
	
	private McuFamily(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
