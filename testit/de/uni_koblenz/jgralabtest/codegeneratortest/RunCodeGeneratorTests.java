package de.uni_koblenz.jgralabtest.codegeneratortest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses( { CodeListTest.class, CodeSnippetTest.class,
		ImportCodeSnippetTest.class })
public class RunCodeGeneratorTests {

}