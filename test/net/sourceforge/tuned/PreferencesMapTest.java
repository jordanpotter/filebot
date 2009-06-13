
package net.sourceforge.tuned;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sourceforge.filebot.web.Episode;
import net.sourceforge.tuned.PreferencesMap.SerializableAdapter;
import net.sourceforge.tuned.PreferencesMap.SimpleAdapter;


public class PreferencesMapTest {
	
	private static Preferences root;
	private static Preferences strings;
	private static Preferences numbers;
	private static Preferences temp;
	private static Preferences sequence;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		root = Preferences.userRoot().node("junit-test");
		
		strings = root.node("strings");
		strings.put("1", "Firefly");
		strings.put("2", "Roswell");
		strings.put("3", "Angel");
		strings.put("4", "Dead like me");
		strings.put("5", "Babylon");
		
		numbers = root.node("numbers");
		numbers.putInt("M", 4);
		numbers.putInt("A", 5);
		numbers.putInt("X", 2);
		
		sequence = root.node("sequence");
		sequence.putInt("1", 1);
		sequence.putInt("2", 2);
		sequence.putInt("3", 3);
		
		temp = root.node("temp");
	}
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		root.removeNode();
	}
	

	@Test
	public void get() {
		Map<String, String> stringMap = PreferencesMap.map(strings);
		
		assertEquals("Firefly", stringMap.get("1"));
	}
	

	@Test
	public void put() {
		Map<String, String> stringMap = PreferencesMap.map(temp);
		
		stringMap.put("key", "snake");
		
		assertEquals("snake", temp.get("key", null));
	}
	

	@Test
	public void remove() throws Exception {
		Map<String, Integer> map = PreferencesMap.map(numbers, SimpleAdapter.forClass(Integer.class));
		
		map.remove("A");
		
		assertFalse(Arrays.asList(numbers.keys()).contains("A"));
	}
	

	@Test
	public void clear() throws Exception {
		Map<String, Integer> map = PreferencesMap.map(temp, SimpleAdapter.forClass(Integer.class));
		
		map.put("X", 42);
		
		map.clear();
		
		assertTrue(temp.keys().length == 0);
	}
	

	@Test
	public void containsKey() {
		temp.put("name", "kaya");
		
		Map<String, String> map = PreferencesMap.map(temp);
		
		assertTrue(map.containsKey("name"));
	}
	

	@Test
	public void values() {
		
		Map<String, Integer> map = PreferencesMap.map(sequence, SimpleAdapter.forClass(Integer.class));
		
		Collection<Integer> list = map.values();
		
		assertTrue(list.contains(1));
		assertTrue(list.contains(2));
		assertTrue(list.contains(3));
	}
	

	@Test
	public void containsValue() {
		Map<String, String> map = PreferencesMap.map(strings);
		
		assertTrue(map.containsValue("Firefly"));
	}
	

	@Test
	public void entrySet() {
		Map<String, Integer> map = PreferencesMap.map(numbers, SimpleAdapter.forClass(Integer.class));
		
		for (Entry<String, Integer> entry : map.entrySet()) {
			Integer v = entry.getValue();
			entry.setValue(v + 1);
		}
		
		assertEquals(5, numbers.getInt("M", -1));
	}
	

	@Test(expected = NumberFormatException.class)
	public void adapterException() {
		PreferencesMap.map(strings, SimpleAdapter.forClass(Integer.class)).values();
	}
	

	@Test
	public void containsKeyWithObjectKey() throws Exception {
		Map<String, String> map = PreferencesMap.map(strings);
		
		assertFalse(map.containsKey(new Object()));
	}
	

	public void getWithObjectKey() throws Exception {
		Map<String, String> map = PreferencesMap.map(strings);
		
		assertEquals(null, map.get(new Object()));
	}
	

	@Test
	public void serializableAdapter() {
		Map<String, Episode> map = PreferencesMap.map(temp, new SerializableAdapter<Episode>());
		
		Episode episode = new Episode("8 Simple Rules", 1, 1, "Pilot");
		
		map.put("episode", episode);
		
		assertEquals(episode.toString(), map.get("episode").toString());
	}
}
