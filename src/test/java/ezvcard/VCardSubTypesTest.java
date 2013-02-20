package ezvcard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

/*
 Copyright (c) 2012, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * @author Michael Angstadt
 */
public class VCardSubTypesTest {
	@Test
	public void getFirst() {
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.put("NUMBERS", "1");
		subTypes.put("NUMBERS", "2");
		subTypes.put("NUMBERS", "3");

		assertEquals("1", subTypes.getFirst("NUMBERS"));
	}

	@Test
	public void put() {
		//names are case insensitive
		//values should retain their case
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.put("test1", "OnE");
		subTypes.put("TEST2", "TwO");
		subTypes.put("test3", "three");
		subTypes.put("tESt3", "trois");
		subTypes.put("tesT3", "three");

		assertEquals(Arrays.asList("OnE"), subTypes.get("tESt1"));
		assertEquals(Arrays.asList("TwO"), subTypes.get("test2"));
		assertEquals(Arrays.asList("three", "trois", "three"), subTypes.get("TEST3"));
	}

	@Test
	public void remove() {
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.put("NUMBERS", "1");
		subTypes.put("NUMBERS", "2");
		subTypes.put("NUMBERS", "3");
		subTypes.remove("nUMBERS", "2");

		assertEquals(Arrays.asList("1", "3"), subTypes.get("NUMBERS"));
	}

	@Test
	public void removeAll() {
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.put("NUMBERS", "1");
		subTypes.put("NUMBERS", "2");
		subTypes.put("NUMBERS", "3");
		subTypes.removeAll("NuMBERs");

		assertTrue(subTypes.get("NUMBERS").isEmpty());
	}

	@Test
	public void getPref() {
		VCardSubTypes subTypes = new VCardSubTypes();

		subTypes.replace("PREF", "1");
		assertEquals(Integer.valueOf(1), subTypes.getPref());

		subTypes.replace("PREF", "invalid");
		assertNull(subTypes.getPref());
	}

	@Test
	public void setPref() {
		VCardSubTypes subTypes = new VCardSubTypes();

		try {
			subTypes.setPref(-1);
			fail();
		} catch (IllegalArgumentException e) {
			//should be thrown
		}

		try {
			subTypes.setPref(101);
			fail();
		} catch (IllegalArgumentException e) {
			//should be thrown
		}

		subTypes.setPref(1);
		assertEquals("1", subTypes.getFirst("PREF"));
	}

	/**
	 * Make sure it handles GEO values correctly.
	 */
	@Test
	public void geo() {
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.setGeo(-10.98887888, 20.12344111);

		//make sure it builds the correct text value
		{
			String expected = "geo:-10.988879,20.123441"; //it should round to 6 decimal places
			String actual = subTypes.getFirst("GEO");
			assertEquals(expected, actual);
		}

		//make sure it unmarshals the text value correctly
		{
			double[] expected = new double[] { -10.988879, 20.123441 };
			double[] actual = subTypes.getGeo();
			assertArrayEquals(expected, actual, .00001);
		}
	}

	/**
	 * Make sure it handles PID values correctly.
	 */
	@Test
	public void pid() {
		VCardSubTypes subTypes = new VCardSubTypes();
		subTypes.addPid(1);
		subTypes.addPid(2, 1);

		//make sure it builds the correct string values
		assertEquals(Arrays.asList("1", "2.1"), subTypes.get("PID"));

		//make sure it unmarshals the string values correctly
		Iterator<Integer[]> it = subTypes.getPids().iterator();
		Integer[] pid = it.next();
		assertEquals(Integer.valueOf(1), pid[0]);
		assertNull(pid[1]);
		pid = it.next();
		assertEquals(Integer.valueOf(2), pid[0]);
		assertEquals(Integer.valueOf(1), pid[1]);
		assertFalse(it.hasNext());
	}

	@Test
	public void getIndex() {
		VCardSubTypes subTypes = new VCardSubTypes();

		subTypes.replace("INDEX", "1");
		assertEquals(Integer.valueOf(1), subTypes.getIndex());

		subTypes.replace("INDEX", "invalid");
		assertNull(subTypes.getIndex());
	}

	@Test
	public void setIndex() {
		VCardSubTypes subTypes = new VCardSubTypes();

		try {
			subTypes.setIndex(-1);
			fail();
		} catch (IllegalArgumentException e) {
			//should be thrown
		}

		try {
			subTypes.setIndex(0);
			fail();
		} catch (IllegalArgumentException e) {
			//should be thrown
		}

		subTypes.setIndex(1);
		assertEquals("1", subTypes.getFirst("INDEX"));
	}
}
