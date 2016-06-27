package org.eclipse.flux.core.woot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WootTest {

	private static final String TEST_STRING = "hello world!";
	private WString site = new WString(1, 1);

	@Test
	public void testIntegrationInsert() {
		site = generateWootString(TEST_STRING);

		assertEquals(TEST_STRING, site.value().toString());
	}

	private WString generateWootString(String testString) {
		char[] s = testString.toCharArray();

		int i = 0;

		for (char element : s) {
			site.generateIns(i++, element);
		}

		return site;
	}

	@Test
	public void testGenerateDelele() {
		site = generateWootString(TEST_STRING);

		site.generateDel(8);

		assertEquals("hello wold!", site.value().toString());
		System.out.println("====================");
		System.out.println(site.value());
		System.out.println("====================");
	}
	
	@Test
	public void testGenerateDelToUnavailablePosition() {
		site = generateWootString(TEST_STRING);
		site.generateDel(50);
		
		System.out.println("====================");
		System.out.println(site.value());
		System.out.println("====================");
		assertEquals("hello world!", site.value().toString());
	}

	@Test
	public void testGenerateInsertToPosition() {
		site = generateWootString(TEST_STRING);
		site.generateIns(5, 'F');
		
		System.out.println("====================");
		System.out.println(site.value());
		System.out.println("====================");
		assertEquals("helloF world!", site.value().toString());
	}

}
