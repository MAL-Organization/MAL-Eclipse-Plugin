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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	private static final String OUTPUT_FOLDER = "target/templates/";
	private static final String SCRIPTS_FOLDER = "ldscripts/";

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

	private static ComplexArray appendOptionSet(Template template, String condition, String type) {
		// Add if statement
		If ifStatement = template.addNewIf();
		ifStatement.setCondition(condition);
		// Add process element
		Process process = ifStatement.addNewProcess();
		process.setType(type);
		// Add simple element
		Simple simple = process.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("projectName");
		simple.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("$(projectName)");
		simple.setValue(valueString);
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
		appendOtionPath(element, "");
	}
	
	private static void appendOtionPath(Element element, String path) {
		Simple pathValue = element.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("path");
		pathValue.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(path);
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
		for (String option : options) {
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

	private static If appendSourceFolder(Template template, String condition, String path) {
		// Add if statement
		If ifStatement = template.addNewIf();
		ifStatement.setCondition(condition);
		// Add process element
		Process process = ifStatement.addNewProcess();
		process.setType(EclipseOptionType.CREATE_SOURCE_FOLDER.getType());
		// Add simple element
		Simple simple = process.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("projectName");
		simple.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("$(projectName)");
		simple.setValue(valueString);
		// Add path
		Simple pathValue = process.addNewSimple();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("path");
		pathValue.setName(nameString);
		valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(path);
		pathValue.setValue(valueString);

		return ifStatement;
	}
	
	private static If appendFile(Template template, String condition, String source, String target) {
		// Add if statement
		If ifStatement = template.addNewIf();
		ifStatement.setCondition(condition);
		// Add process element
		Process process = ifStatement.addNewProcess();
		process.setType(EclipseOptionType.ADD_FILES.getType());
		// Add simple element
		Simple simple = process.addNewSimple();
		XmlString nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("projectName");
		simple.setName(nameString);
		XmlString valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("$(projectName)");
		simple.setValue(valueString);
		// Add complex array
		ComplexArray complexArray = process.addNewComplexArray1();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("files");
		complexArray.setName(nameString);
		// Add element
		Element element = complexArray.addNewElement();
		// Add source
		Simple sourceSimple = element.addNewSimple();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("source");
		sourceSimple.setName(nameString);
		valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(source);
		sourceSimple.setValue(valueString);
		// Add target
		Simple targetSimple = element.addNewSimple();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("target");
		targetSimple.setName(nameString);
		valueString = XmlString.Factory.newInstance();
		valueString.setStringValue(target);
		targetSimple.setValue(valueString);
		// Add target
		Simple replaceableSimple = element.addNewSimple();
		nameString = XmlString.Factory.newInstance();
		nameString.setStringValue("replaceable");
		replaceableSimple.setName(nameString);
		valueString = XmlString.Factory.newInstance();
		valueString.setStringValue("true");
		replaceableSimple.setValue(valueString);	

		return ifStatement;
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
		XmlCursor cursor = template.newCursor();
		cursor.toNextToken();
		cursor.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		cursor.insertAttributeWithValue(
				new QName("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation"),
				"TemplateDescriptorSchema.xsd");
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
		systemClockSourceProperty.setId(MalProperty.SYSTEM_CLOCK_SOURCE.getId());
		systemClockSourceProperty.setLabel("System Clock Source");
		systemClockSourceProperty.setDescription("The source of the system clock.");
		systemClockSourceProperty.setType(Property.Type.SELECT);
		systemClockSourceProperty.setMandatory(true);
		systemClockSourceProperty.setDefault(SystemClockSource.values()[0].getValue());
		for (SystemClockSource source : SystemClockSource.values()) {
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
		// Add mal version property
		Property malVersionProperty = propertyGroup.addNewProperty();
		malVersionProperty.setId(MalProperty.VERSION.getId());
		malVersionProperty.setLabel("MAL Library Version");
		malVersionProperty.setDescription("The version of MAL to use. Note if MAVEN is selected, you will have to select the maven version in the POM.");
		malVersionProperty.setType(Property.Type.SELECT);
		malVersionProperty.setMandatory(true);
		malVersionProperty.setDefault("MAVEN");
		Item item = malVersionProperty.addNewItem();
		item.setLabel("Maven");
		item.setValue("MAVEN");
		// Add target options
		for (Mcu mcu : mcus) {
			// Start target options
			String condition = String.format("$(%s)==%s", mcuProperty.getId(), mcu.getValue());
			ComplexArray targetOptionSet = appendOptionSet(template, condition, EclipseOptionType.STRING.getType());
			// Add MCU family
			appendOption(targetOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family",
					mcu.getFamily().getValue());
		}
		// Start optimization options
		ComplexArray optimizationOptionSet = appendOptionSet(template, "1==1", EclipseOptionType.BOOLEAN.getType());
		// Set freestanding option
		appendOption(optimizationOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding",
				true);
		// Set no move loop variant
		appendOption(optimizationOptionSet,
				"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.nomoveloopinvariants", true);
		// Start linker options
		ComplexArray linkerOptionSet = appendOptionSet(template, "1==1", EclipseOptionType.BOOLEAN.getType());
		// Set use new nano lib option
		appendOption(linkerOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano", true);
		// Add preprocessor defined symbols
		ComplexArray commonSymbolsOptionSet = appendOptionSet(template, "1==1",
				EclipseOptionType.APPEND_STRING_LIST.getType());
		// Add target system clock
		String targetSystemClockOption = String.format("MAL_TARGET_SYSTEM_CLOCK=$(%s)",
				MalProperty.SYSTEM_CLOCK.getId());
		appendOption(commonSymbolsOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.defs",
				Arrays.asList(targetSystemClockOption));
		// Add target system clock source
		String targetSystemClockSourceOption = String.format("MAL_TARGET_SYSTEM_CLOCK_SRC=$(%s)",
				MalProperty.SYSTEM_CLOCK_SOURCE.getId());
		appendOption(commonSymbolsOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.defs",
				Arrays.asList(targetSystemClockSourceOption));
		// Add external clock frequency
		String externalClockFrequencyOption = String.format("MAL_EXTERNAL_CLOCK_FREQUENCY=$(%s)",
				MalProperty.EXTERNAL_CLOCK.getId());
		appendOption(commonSymbolsOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.defs",
				Arrays.asList(externalClockFrequencyOption));
		// Add MCU specific symbols
		for (Mcu mcu : mcus) {
			String condition = String.format("$(%s)==%s", MalProperty.MCU.getId(), mcu.getValue());
			ComplexArray mcuOptionSet = appendOptionSet(template, condition,
					EclipseOptionType.APPEND_STRING_LIST.getType());
			// Add each symbol
			for (String symbol : mcu.getSymbols()) {
				appendOption(mcuOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.defs", Arrays.asList(symbol));
			}
		}
		// Add include paths
		List<String> paths = new ArrayList<String>();
		// Add src folder
		paths.add("\"${workspace_loc:/$(projectName)/src}\"");
		// Add mal headers
		paths.add("\"${workspace_loc:/$(projectName)/mal/headers}\"");
		paths.add("\"${workspace_loc:/$(projectName)/mal/headers/src}\"");
		for (Mcu mcu : mcus) {
			String condition = String.format("$(%s)==%s", MalProperty.MCU.getId(), mcu.getValue());
			ComplexArray mcuOptionSet = appendOptionSet(template, condition,
					EclipseOptionType.APPEND_STRING_LIST.getType());
			// Create full list
			List<String> allPaths = new ArrayList<String>(paths);
			allPaths.addAll(mcu.getIncludePaths());
			// Add paths
			appendOption(mcuOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.include.paths", allPaths);
		}
		// Add linker scripts
		for (Mcu mcu : mcus) {
			List<File> scripts = mcu.getLinkerScripts();
			if (scripts.size() > 0) {
				String condition = String.format("$(%s)==%s", MalProperty.MCU.getId(), mcu.getValue());
				ComplexArray mcuOptionSet = appendOptionSet(template, condition,
						EclipseOptionType.STRING_LIST.getType());
				// Create list of script names
				List<String> scriptNames = new ArrayList<String>();
				for (File script : scripts) {
					scriptNames.add(script.getName());
				}
				// Add names
				appendOption(mcuOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.linker.scriptfile",
						scriptNames);
			}
		}
		// Add libraries
		// libmal is mandatory
		List<String> libs = new ArrayList<String>();
		libs.add("mal");
		for (Mcu mcu : mcus) {
			// Add MCU installed libraries
			libs.addAll(mcu.getInstalledLibraries());
			// Add MCU libraries
			for (File lib : mcu.getLibraries()) {
				String filename = lib.getName();
				// Remove .a extension
				filename = filename.replaceAll(".a", "");
				// Remove first lib
				filename = filename.replaceFirst("lib", "");
				libs.add(filename);
			}
			// Create option set
			String condition = String.format("$(%s)==%s", MalProperty.MCU.getId(), mcu.getValue());
			ComplexArray mcuOptionSet = appendOptionSet(template, condition, EclipseOptionType.STRING_LIST.getType());
			// Add every lib
			appendOption(mcuOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.libs", libs);
		}
		// Add library search paths
		ComplexArray libSearchPathOptionSet = appendOptionSet(template, "1==1",
				EclipseOptionType.APPEND_STRING_LIST.getType());
		List<String> searchPaths = Arrays.asList("\"${workspace_loc:/$(projectName)/ldscripts}\"",
				"\"${workspace_loc:/$(projectName)/libs}\"");
		appendOption(libSearchPathOptionSet, "ilg.gnuarmeclipse.managedbuild.cross.option.*.linker.paths", searchPaths);
		// Add MCU source folders
		for (Mcu mcu: mcus) {
			// Create option set
			String condition = String.format("$(%s)==%s", MalProperty.MCU.getId(), mcu.getValue());
			for (String folder: mcu.getSourceFolders()) {
				appendSourceFolder(template, condition, folder);
			}
			// Add MCU files
			File outputFolder = new File(OUTPUT_FOLDER + mcu.getValue() + "/");
			for (TemplateSourceFile file: mcu.getSourceFiles()) {
				File destinationFolder = new File(outputFolder.getAbsolutePath() + "/" + file.destinationFolder);
				destinationFolder.mkdirs();
				// Create file for destination path
				File destinationFile = new File(destinationFolder.getAbsolutePath() + "/" + file.sourceFile.getName());
				// Copy file to output
				try {
					Files.copy(file.sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Add file to template
				String sourceFilePath = mcu.getValue() + "/" + file.destinationFolder + file.sourceFile.getName();
				String destinationFilePath = file.destinationFolder + file.sourceFile.getName();
				appendFile(template, condition, sourceFilePath, destinationFilePath);
			}
			for (File script: mcu.getLinkerScripts()) {
				File destinationFolder = new File(outputFolder.getAbsolutePath() + "/" + SCRIPTS_FOLDER);
				destinationFolder.mkdirs();
				// Create file for destination path
				File destinationFile = new File(destinationFolder.getAbsolutePath() + "/" + script.getName());
				// Copy file to output
				try {
					Files.copy(script.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Add file to template
				String sourceFilePath = mcu.getValue() + "/" + SCRIPTS_FOLDER + script.getName();
				String destinationFilePath = SCRIPTS_FOLDER + script.getName();
				appendFile(template, condition, sourceFilePath, destinationFilePath);
			}
		}
		// Add MAL source folder
		appendSourceFolder(template, "1==1", "mal");
		// Add MAL source code
		File sourceFile = new File(TemplateGenerator.class.getClassLoader().getResource("mal/mal_config.c").getFile());
		File targetFolder = new File(OUTPUT_FOLDER + "mal/");
		targetFolder.mkdirs();
		File targetFile = new File(targetFolder.getAbsolutePath() + "/" + sourceFile.getName());
		try {
			Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String filePath = "mal/" + sourceFile.getName();
		appendFile(template, "1==1", filePath, filePath);
		// Add main
		appendSourceFolder(template, "1==1", "src");
		sourceFile = new File(TemplateGenerator.class.getClassLoader().getResource("mal/src/main.c").getFile());
		targetFolder = new File(OUTPUT_FOLDER + "mal/src/");
		targetFolder.mkdirs();
		targetFile = new File(targetFolder.getAbsolutePath() + "/" + sourceFile.getName());
		try {
			Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String sourceFilePath = "mal/src/" + sourceFile.getName();
		String destinationFilePath = "src/" + sourceFile.getName();
		appendFile(template, "1==1", sourceFilePath, destinationFilePath);

		File result = new File(OUTPUT_FOLDER + "mal-template.xml");
		try {
			templateDocument.save(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
