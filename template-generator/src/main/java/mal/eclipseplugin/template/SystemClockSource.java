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
package mal.eclipseplugin.template;

public enum SystemClockSource {
	
	INTERNAL("Internal", "MAL_HSPEC_SYSTEM_CLK_SRC_INTERNAL"),
	EXTERNAL("External", "MAL_HSPEC_SYSTEM_CLK_SRC_EXTERNAL"),
	AUTO("Auto", "MAL_HSPEC_SYSTEM_CLK_SRC_AUTO");
	
	private final String label;
	private final String value;
	
	private SystemClockSource(String label, String value) {
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getValue() {
		return value;
	}
	
}
