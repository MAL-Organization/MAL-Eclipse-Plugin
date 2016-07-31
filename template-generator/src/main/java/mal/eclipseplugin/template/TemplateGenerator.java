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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlString;
import org.reflections.Reflections;

import mal.eclipseplugin.mcu.Mcu;
import noNamespace.ComplexArrayDocument.ComplexArray;
import noNamespace.ComplexArrayDocument.ComplexArray.Element;
import noNamespace.ProcessDocument.Process;
import noNamespace.SimpleArrayDocument.SimpleArray;
import noNamespace.SimpleDocument.Simple;
import noNamespace.TemplateDocument;
import noNamespace.TemplateDocument.Template;
import noNamespace.TemplateDocument.Template.If;
import noNamespace.TemplateDocument.Template.PropertyGroup;
import noNamespace.TemplateDocument.Template.PropertyGroup.Property;
import noNamespace.TemplateDocument.Template.PropertyGroup.Property.Item;

public class TemplateGenerator {

	private static List<Mcu> getMcus() {
		List<Mcu> mcus = new ArrayList<Mcu>();
		Reflections reflections = new Reflections("mal.eclipseplugin.mcu.mcus");
		for (Class<? extends Mcu> mcuClass : reflections.getSubTypesOf(Mcu.class)) {
			// Make sure it is not abstract
			if (Modifier.isAbstract(mcuClass.getModifiers())) {
				continue;
			}
			// Add instance
			try {
				mcus.add(mcuClass.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return mcus;
	}
	
	private static ComplexArray appendOptionSet(Template template, String condition) {
		// Add if statement
		If ifStatement = template.addNewIf();
		ifStatement.setCondition(condition);
		// Add process element
		Process process = ifStatement.addNewProcess();
		process.setType("org.eclipse.cdt.managedbuilder.core.SetMBSStringOptionValue");
		// Add simple element
		Simple simple = process.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("projectName");
		simple.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("$(projectName)");
		simple.setName(valueString);
		// Add complex array
		ComplexArray complexArray = process.addNewComplexArray1();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("resourcePaths");
		complexArray.setName(nameString);
		
		return complexArray;
	}
	
	private static Element appendOptionId(ComplexArray array, String id) {
		// Add element
		Element element = array.addNewElement();
		// Add ID
		Simple simpleId = element.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("id");
		simpleId.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(id);
		simpleId.setValue(valueString);
		
		return element;
	}
	
	private static void appendOtionPath(Element element) {
		Simple pathValue = element.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("path");
		pathValue.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("");
		pathValue.setValue(valueString);
	}
	
	private static Element appendOption(ComplexArray array, String id, String value) {
		// Set ID
		Element element = appendOptionId(array, id);
		// Add value
		Simple simpleValue = element.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("value");
		simpleValue.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(value);
		simpleValue.setValue(valueString);
		// Add path
		appendOtionPath(element);
		
		return element;
	}
	
	private static Element appendOption(ComplexArray array, String id, Boolean value) {
		return appendOption(array, id, value.toString());
	}
	
	private static Element appendOption(ComplexArray array, String id, List<String> options) {
		// Set ID
		Element element = appendOptionId(array, id);
		// Add values
		SimpleArray simpleValues = element.addNewSimpleArray1();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("values");
		simpleValues.setName(nameString);
		for (String option: options) {
			noNamespace.SimpleArrayDocument.SimpleArray.Element optionElement;
			optionElement = simpleValues.addNewElement();
			XmlString valueString = XmlString.Factory.newInstance();
			valueString.setStringValue(option);
			optionElement.setValue(valueString);
		}
		// Add path
		appendOtionPath(element);
		
		return element;
	}

	public static void main(String[] args) {
		List<Mcu> mcus = getMcus();
		// Create document with common parts
		TemplateDocument templateDocument = TemplateDocument.Factory.newInstance();
		Template template = templateDocument.addNewTemplate();
		template.setType("ProjTempl");
		template.setVersion("1");
		template.setRevision("0");
		template.setSupplier("MAL-Organization");
		template.setAuthor("MAL-Organization");
		template.setId("malProject");
		template.setLabel("MAL Project");
		template.setDescription("This is a CDT template project to use the MCU abstraction layer (MAL) library.");
		// Add xsi namespace
		XmlCursor cursor= template.newCursor();
		cursor.toNextToken();
		cursor.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		cursor.insertAttributeWithValue(new QName("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation"), "TemplateDescriptorSchema.xsd");
		cursor.dispose();
		// Create property group
		PropertyGroup propertyGroup = template.addNewPropertyGroup();
		propertyGroup.setId("malPropertyGroup");
		propertyGroup.setLabel("MAL Properties");
		propertyGroup.setDescription("MAL properties.");
		propertyGroup.setType("PAGES-ONLY");
		// Add MCU property
		Property mcuProperty = propertyGroup.addNewProperty();
		mcuProperty.setId(MalProperty.MCU.getId());
		mcuProperty.setLabel("MCU");
		mcuProperty.setDescription("Select the target MCU.");
		mcuProperty.setType(Property.Type.SELECT);
		mcuProperty.setMandatory(true);
		if (mcus.size() > 0) {
			mcuProperty.setDefault(mcus.get(0).getLabel());
		}
		// Add all available MCUs
		for (Mcu mcu : mcus) {
			Item item = mcuProperty.addNewItem();
			item.setLabel(mcu.getLabel());
			item.setValue(mcu.getValue());
		}
		// Add system clock property
		Property systemClockProperty = propertyGroup.addNewProperty();
		systemClockProperty.setId(MalProperty.SYSTEM_CLOCK.getId());
		systemClockProperty.setLabel("System Clock Frequency");
		systemClockProperty.setDescription("This will be the target frequency (Hz) for the MCU system clock.");
		systemClockProperty.setType(Property.Type.INPUT);
		systemClockProperty.setMandatory(true);
		systemClockProperty.setPattern("[0-9]*");
		systemClockProperty.setDefault("0");
		// Add system clock source property
		Property systemClockSourceProperty = propertyGroup.addNewProperty();
		systemClockSourceProperty.setId("sysClkSrcProperty");
		systemClockSourceProperty.setLabel(MalProperty.SYSTEM_CLOCK_SOURCE.getId());
		systemClockSourceProperty.setDescription("The source of the system clock.");
		systemClockSourceProperty.setType(Property.Type.SELECT);
		systemClockSourceProperty.setMandatory(true);
		systemClockSourceProperty.setDefault(SystemClockSource.values()[0].getValue());
		for (SystemClockSource source: SystemClockSource.values()) {
			Item item = systemClockSourceProperty.addNewItem();
			item.setLabel(source.getLabel());
			item.setValue(source.getValue());
		}
		// Add external clock frequency property
		Property externalClockFrequencyProperty = propertyGroup.addNewProperty();
		externalClockFrequencyProperty.setId(MalProperty.EXTERNAL_CLOCK.getId());
		externalClockFrequencyProperty.setLabel("External Clock Frequency");
		externalClockFrequencyProperty.setDescription("The frequency of the external crystal or oscillator.");
		externalClockFrequencyProperty.setType(Property.Type.INPUT);
		externalClockFrequencyProperty.setMandatory(false);
		externalClockFrequencyProperty.setPattern("[0-9]*");
		externalClockFrequencyProperty.setDefault("0");
		// Add target options
		for (Mcu mcu: mcus) {
			// Start target options
			String condition = String.format("$(%s)==%s", mcuProperty.getId(), mcu.getValue());
			ComplexArray targetOptionSet = appendOptionSet(template, condition);
			// Add MCU family
			appendOption(targetOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family", mcu.getFamily().getValue());
		}
		// Start optimization options
		ComplexArray optimizationOptionSet = appendOptionSet(template, "1==1");
		// Set freestanding option
		appendOption(optimizationOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding", true);
		// Set no move loop variant
		appendOption(optimizationOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.optimization.nomoveloopinvariants", true);
		// Start linker options
		ComplexArray linkerOptionSet = appendOptionSet(template, "1==1");
		// Set use new nano lib option
		appendOption(linkerOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano", true);
		// Add preprocessor defined symbols
		ComplexArray commonSymbolsOptionSet = appendOptionSet(template, "1==1");
		// Add target system clock
		String targetSystemClockOption = String.format("MAL_TARGET_SYSTEM_CLOCK=$(%s)", MalProperty.SYSTEM_CLOCK.getId());
		
		File result = new File("test.xml");
		try {
			templateDocument.save(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
