/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import de.uni_koblenz.jgralab.GraphIOException;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class CodeGenerator {

	/**
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	protected enum GenerationCycle {
		// FIXME The order here matters! CLASSONLY must be last!
		ABSTRACT, MEMORYBASED, DISTRIBUTED, DISKBASED, PROXIES, CLASSONLY;

		protected static List<GenerationCycle> filter(
				CodeGeneratorConfiguration config) {
			List<GenerationCycle> out = new ArrayList<GenerationCycle>();
			out.add(ABSTRACT);
			out.add(MEMORYBASED);
			out.add(DISTRIBUTED);
			out.add(DISKBASED);
			out.add(PROXIES);
			out.add(CLASSONLY);
			return out;
		}

		/**
		 * 
		 * @return
		 */
		protected boolean isMembasedImpl() {
			return this == MEMORYBASED;
		}
		
		
		/**
		 * 
		 * @return
		 */
		protected boolean isDistributedImpl() {
			return this == DISKBASED;
		}
		
		/**
		 * 
		 * @return
		 */
		protected boolean isDiskbasedImpl() {
			return this == DISKBASED;
		}
		
		
		/**
		 * 
		 * @return
		 */
		protected boolean isImplementationVariant() {
			return this == DISKBASED || this == MEMORYBASED || this == DISTRIBUTED;
		}


		/**
		 * 
		 * @return
		 */
		protected boolean isProxies() {
			return this == PROXIES;
		}
		
		/**
		 * 
		 * @return
		 */
		protected boolean isAbstract() {
			return this == ABSTRACT;
		}

		/**
		 * 
		 * @return
		 */
		protected boolean isClassOnly() {
			return this == CLASSONLY;
		}

	}

	private final List<GenerationCycle> cycles;

	private int cycleCount = 0;

	private static Logger logger = Logger.getLogger(CodeGenerator.class
			.getName());

	protected CodeList rootBlock;

	private final ImportCodeSnippet imports;

	protected String schemaRootPackageName;

	protected CodeGeneratorConfiguration config;

	protected GenerationCycle currentCycle;

	/**
	 * Creates a {@link CodeGenerator} for a single class.
	 * 
	 * @param schemaRootPackageName
	 *            the name of the root package of the schema, for instance
	 *            de.uni_koblenz.jgralab.greql2
	 * @param packageName
	 *            the name of the package the class is located in, for instance
	 *            comprehensions Out of the three parameters, the CodeGenerator
	 *            calculates the name
	 *            schemaRootPackageName.packageName.implementationName, in the
	 *            example
	 *            "de.uni_koblenz.jgralab.greql2.comprehension.Bagcomprehension"
	 *            for the interface and possibly
	 *            schemaRootPackageName.impl.packageName.implementationName, in
	 *            the example
	 *            "de.uni_koblenz.jgralab.greql2.impl.comprehension.Bagcomprehension"
	 *            for the default implementation class
	 * @param config
	 *            The {@link CodeGeneratorConfiguration} to be used when
	 *            generating code.
	 */
	public CodeGenerator(String schemaRootPackageName, String packageName,
			CodeGeneratorConfiguration config) {
		this.schemaRootPackageName = schemaRootPackageName;
		this.config = config;
		rootBlock = new CodeList(null);
		rootBlock.setVariable("jgPackage", "de.uni_koblenz.jgralab");
		rootBlock.setVariable("jgImplPackage", "de.uni_koblenz.jgralab.impl");
		rootBlock.setVariable("jgDiskImplPackage", "de.uni_koblenz.jgralab.impl.disk");
		rootBlock.setVariable("jgDistributedImplPackage", "de.uni_koblenz.jgralab.impl.memdistributed");
		rootBlock.setVariable("jgMemImplPackage", "de.uni_koblenz.jgralab.impl.mem");
		rootBlock.setVariable("jgSchemaPackage","de.uni_koblenz.jgralab.schema");
		rootBlock.setVariable("jgSchemaImplPackage", "de.uni_koblenz.jgralab.schema.impl");
		

		if ((packageName != null) && !packageName.equals("")) {
			rootBlock.setVariable("schemaPackage", schemaRootPackageName + "."	+ packageName);
			rootBlock.setVariable("schemaMemImplPackage", schemaRootPackageName + ".impl.mem." + packageName);
			rootBlock.setVariable("schemaDistributedImplPackage", schemaRootPackageName + ".impl.memdistributed." + packageName);
			rootBlock.setVariable("schemaDiskImplPackage", schemaRootPackageName + ".impl.disk." + packageName);
		} else {
			rootBlock.setVariable("schemaPackage", schemaRootPackageName);
			rootBlock.setVariable("schemaMemImplPackage", schemaRootPackageName + ".impl.mem");
			rootBlock.setVariable("schemaDistributedImplPackage", schemaRootPackageName + ".impl.memdistributed");
			rootBlock.setVariable("schemaDiskImplPackage", schemaRootPackageName + ".impl.disk");
		}
		rootBlock.setVariable("isClassOnly", "false");
		rootBlock.setVariable("isImplementationClassOnly", "false");
		rootBlock.setVariable("isAbstractClass", "false");

		imports = new ImportCodeSnippet();
		cycles = GenerationCycle.filter(config);
	}

	protected abstract CodeBlock createHeader();

	protected abstract CodeBlock createBody();

	protected CodeBlock createFooter() {
		return new CodeSnippet("}");
	}

	public static CodeBlock createDisclaimer() {
		return new CodeSnippet("/*",
				" * This code was generated automatically.",
				" * Do NOT edit this file, changes will be lost.",
				" * Instead, change and commit the underlying schema.", " */");
	}

	/**
	 * writes the source code to location path+fileName,
	 * 
	 * @param pathPrefix
	 *            the path where the java source code is to be
	 * @param fileName
	 *            the filename of the java source code including .java
	 * @param aPackage
	 * @throws GraphIOException
	 */
	public void writeCodeToFile(String pathPrefix, String fileName,
			String aPackage) throws GraphIOException {
		aPackage = aPackage.replace(".", File.separator);

		File dir = new File(pathPrefix + aPackage);

		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new GraphIOException("'" + dir.getAbsolutePath()
						+ "' exists but is not a directory");
			}
		} else {
			if (!dir.mkdirs()) {
				throw new GraphIOException(
						"Couldn't create directory hierachy for '" + dir + "'.");
			}
		}

		File outputFile = null;
		try {
			outputFile = new File(dir.getAbsolutePath() + File.separator
					+ fileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write(rootBlock.getCode());
			bw.close();
		} catch (IOException e) {
			throw new GraphIOException("Unable to create file "
					+ outputFile.getAbsolutePath(), e);
		}
	}
	
	


	public void createFiles(String pathPrefix) throws GraphIOException {
		// String className = rootBlock.getVariable("className");
		String simpleClassName = rootBlock.getVariable("simpleClassName");
		String schemaPackage = rootBlock.getVariable("schemaPackage");
		String simpleImplClassName = rootBlock.getVariable("simpleImplClassName");
		String simpleProxyClassName =  simpleClassName + "Proxy";
		String schemaImplPackage = "";

		logger.finer("createFiles(\"" + pathPrefix + "\")");
		logger.finer(" - simpleClassName=" + simpleClassName);
		logger.finer(" - schemaPackage=" + schemaPackage);
		logger.finer(" - simpleImplClassName=" + simpleImplClassName);

		currentCycle = getNextCycle();
		while (currentCycle != null) {
			createCode();
			if (currentCycle.isAbstract()) {
				logger.finer("Creating interface for class: "+ simpleClassName);
				logger.finer("Writing file to: " + pathPrefix + "/"	+ schemaPackage);
			}
			if (currentCycle.isImplementationVariant() || currentCycle.isProxies()) {
				if (currentCycle.isMembasedImpl()) {
					schemaImplPackage = rootBlock.getVariable("schemaMemImplPackage");
				} 
				if (currentCycle.isDistributedImpl()) {
					schemaImplPackage = rootBlock.getVariable("schemaDistributedImplPackage");
				} 
				if (currentCycle.isDiskbasedImpl()) {
					schemaImplPackage = rootBlock.getVariable("schemaDiskImplPackage");
				} 
				logger.finer(" - schemaImplPackage="	+ schemaImplPackage);	
				if (currentCycle.isImplementationVariant()) {
					writeCodeToFile(pathPrefix, simpleImplClassName + ".java",	schemaImplPackage);
				} else if (hasProxySupport()) {
					writeCodeToFile(pathPrefix, simpleProxyClassName + ".java",	schemaImplPackage);
				}
			} else {
				writeCodeToFile(pathPrefix, simpleClassName + ".java", schemaPackage);
			}
			currentCycle = getNextCycle();
		}
	}

	
	
	protected boolean hasProxySupport() {
		return false;
	}
	
	/**
	 * creates the generated code string for a class
	 */
	public void createCode() {
		imports.clear();
		rootBlock.clear();
		rootBlock.addNoIndent(createDisclaimer());
		rootBlock.addNoIndent(createPackageDeclaration());
		CodeBlock header = createHeader();
		CodeBlock body = createBody();
		CodeBlock footer = createFooter();
		rootBlock.addNoIndent(imports);
		rootBlock.addNoIndent(header);
		rootBlock.addNoIndent(body);
		rootBlock.addNoIndent(footer);
	}

	protected CodeBlock createPackageDeclaration() {
		CodeSnippet code = new CodeSnippet(true);

		if (rootBlock.getVariable("isClassOnly").equals("true")) {
			code.add("package #schemaPackage#;");
		} else {
			switch (currentCycle) {
			case ABSTRACT:
				code.add("package #schemaPackage#;");
				break;
			case MEMORYBASED:
				code.add("package #schemaMemImplPackage#;");
				rootBlock.setVariable("usedJgImplPackage", rootBlock.getVariable("jgMemImplPackage"));
				break;
			case DISKBASED:
			case PROXIES:	
				code.add("package #schemaDiskImplPackage#;");
				rootBlock.setVariable("usedJgImplPackage", rootBlock.getVariable("jgDiskImplPackage"));
				break;
			case CLASSONLY:
				code.add("package #schemaPackage#;");
				break;
			}
		}
		return code;
	}

	protected void addImports(String... importPackages) {
		imports.add(importPackages);
	}

	/**
	 * Transforms the given String into a CamelCase String
	 */
	public static String camelCase(String aString) {
		if (aString.length() < 1) {
			return aString;
		}
		if (aString.length() < 2) {
			return aString.toUpperCase();
		}
		return aString.substring(0, 1).toUpperCase() + aString.substring(1);
	}

	/**
	 * @param aString
	 *            some String
	 * @return the string with " quoted as \"
	 */
	public static String stringQuote(String aString) {
		return aString.replaceAll("\"", Matcher.quoteReplacement("\\\""));
	}

	/**
	 * Returns {@code JavaSourceFromString}s from the generated code.
	 * 
	 * @return a Vector of {@code JavaSourceFromString}s from the generated code
	 */
	public Vector<JavaSourceFromString> createJavaSources() {
		String className = rootBlock.getVariable("simpleClassName");
		String implClassName = rootBlock.getVariable("simpleImplClassName");
		String proxyClassName = rootBlock.getVariable("proxyClassName");
		Vector<JavaSourceFromString> javaSources = new Vector<JavaSourceFromString>(2);

		currentCycle = getNextCycle();
		while (currentCycle != null) {
			createCode();
			if (currentCycle.isImplementationVariant()) {
				javaSources.add(new JavaSourceFromString(implClassName,	rootBlock.getCode()));
			} else if (currentCycle.isProxies()) {
				javaSources.add(new JavaSourceFromString(proxyClassName,	rootBlock.getCode()));
			} else {	
				javaSources.add(new JavaSourceFromString(className, rootBlock.getCode()));
			}
			currentCycle = getNextCycle();
		}
		return javaSources;
	}

	/**
	 * FIXME loop over cycles instead of counter variable and direct access.
	 * This takes a certain order of ENUM values into account! See other fixme!
	 * 
	 * @return The next matching {@link GenerationCycle}.
	 */
	protected GenerationCycle getNextCycle() {
		// end of generation cycle
		if (cycleCount >= cycles.size()) {
			// cycleCount = 0;
			return null;
		}

		GenerationCycle currentCycle = cycles.get(cycleCount);

		// currentCycle = cycles[cycleCount];

		// abstract classes should only have generation cycle ABSTRACT
		if (rootBlock.getVariable("isAbstractClass").equals("true")
				&& !currentCycle.isAbstract()) {
			cycleCount = 0;
			return null;
		}

		// if it is an implementation class only no abstract classes and
		// interfaces
		// should be generated
		if (rootBlock.getVariable("isImplementationClassOnly").equals("true")
				&& currentCycle.isAbstract()) {
			cycleCount++;
			return getNextCycle();
		}

		// if class only (schema, factory, enum), then only generation cycle
		// CLASSONLY is valid
		if (rootBlock.getVariable("isClassOnly").equals("true")) {
			cycleCount = GenerationCycle.CLASSONLY.ordinal() + 1;
			return GenerationCycle.CLASSONLY;
		}

		// if not class only, then skip generation cycle CLASSONLY
		if (rootBlock.getVariable("isClassOnly").equals("false")
				&& currentCycle.isClassOnly()) {
			cycleCount = 0;
			return null;
		}

		cycleCount++;
		return currentCycle;
	}
}
