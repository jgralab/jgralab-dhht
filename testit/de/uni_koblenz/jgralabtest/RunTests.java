/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2008 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.uni_koblenz.jgralabtest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.uni_koblenz.jgralabtest.coretest.RunCoreTests;
import de.uni_koblenz.jgralabtest.greql2test.RunGreql2Tests;
import de.uni_koblenz.jgralabtest.schematest.RunSchemaTests;
import de.uni_koblenz.jgralabtest.tg2schemagraphtest.Tg2SchemagraphTest;

/**
 * @author ist@uni-koblenz.de
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { RunCoreTests.class, RunSchemaTests.class,
		RunGreql2Tests.class, Tg2SchemagraphTest.class })
public class RunTests {
}