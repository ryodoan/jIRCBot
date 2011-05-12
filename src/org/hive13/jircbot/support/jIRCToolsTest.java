/**
 * 
 */
package org.hive13.jircbot.support;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author vincentp
 *
 */
public class jIRCToolsTest {

	public final String TEST_STRING = "REMOVETHIS This is a test\n\n\n\tOf a string   removethis   \n With multiple issues\t\t\n\t that will" +
									  " need to be cleaned up: Behold, the LOD. RemoveThis";
	public final String TEST_URL 	= "http://www.google.com/index.html";
	public final String TEST_URL2	= "http://www.theorionbelt.com/index.html";
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.hive13.jircbot.support.jIRCTools#replaceAll(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testReplaceAll() {
		String testStr = TEST_STRING;
		testStr = jIRCTools.replaceAll(testStr, "\\n", ""); // Remove newlines from the string
		assertTrue("Found newline in testStr at:" + testStr.indexOf("\n"), testStr.indexOf("\n") == -1);

		testStr = TEST_STRING; // Reset the test
		testStr = jIRCTools.replaceAll(testStr, "  ", ""); // Remove duplicate spaces
		assertTrue("Found multiple spaces at:" + testStr.indexOf("  "), testStr.indexOf("  ") == -1);

		testStr = TEST_STRING; // Reset the test
		testStr = jIRCTools.replaceAll(testStr, "RemOveThIs", ""); // Case insensitive text remove
		assertTrue("Found occurance of RemoveThis at: " + testStr.indexOf("RemoveThis"), testStr.indexOf("RemoveThis") == -1);
		
		testStr = TEST_STRING; // Reset the test
		testStr = jIRCTools.replaceAll(testStr, "(ReMoVeThIs|\\n|  )", "");
		assertTrue("Regex: Found newline in testStr at:" + testStr.indexOf("\n"), testStr.indexOf("\n") == -1);
		assertTrue("Regex: Found multiple spaces at:" + testStr.indexOf("  "), testStr.indexOf("  ") == -1);
		assertTrue("Regex: Found occurance of RemoveThis at: " + testStr.indexOf("RemoveThis"), testStr.indexOf("RemoveThis") == -1);
	}

	/**
	 * Test method for {@link org.hive13.jircbot.support.jIRCTools#generateShortURL(java.lang.String)}.
	 */
	@Test
	public void testGenerateShortURLString() {
		String expectedShort = "http://bit.ly/mhet5U"; // This value could change, but as long as this test is run regularly it should work.
		String shortURL = jIRCTools.generateShortURL(TEST_URL);
		assertEquals(expectedShort, shortURL);		
	}

	/**
	 * Test method for {@link org.hive13.jircbot.support.jIRCTools#findURLTitle(java.lang.String)}.
	 */
	@Test
	public void testfindURLTitleString() {
		String urlTitle_Expected = "Google";

		String urlTitle = jIRCTools.findURLTitle(TEST_URL); // I would imagine that this would be cached by bit.ly already.
		assertEquals(urlTitle_Expected, urlTitle);
		
		urlTitle = jIRCTools.findURLTitle(TEST_URL + "#" + 
				Calendar.getInstance().getTimeInMillis()); // This however, should force a cache miss on bit.ly
		assertEquals(urlTitle_Expected, urlTitle);
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		urlTitle = jIRCTools.findURLTitle(TEST_URL2 + "#" +
				Calendar.getInstance().getTimeInMillis()); // Page w/ no title and a forced bit.ly cache miss.
		long stopTime = Calendar.getInstance().getTimeInMillis();
		assertEquals("", urlTitle);
		assertTrue("findURLTitle returned in less than 5s.  It should have timed out.", stopTime-startTime > 5000);
		
		// TODO: Need to test URL's with spaces, unicode, UTF, new lines, etc..
	}
}