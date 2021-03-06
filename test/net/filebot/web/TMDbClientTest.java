package net.filebot.web;

import static net.filebot.CachedResource.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.filebot.Cache;
import net.filebot.CacheType;
import net.filebot.CachedResource;

public class TMDbClientTest {

	static TMDbClient db = new TMDbClient("66308fb6e3fd850dde4c7d21df2e8306", false);

	@Test
	public void searchByName() throws Exception {
		List<Movie> result = db.searchMovie("Serenity", Locale.CHINESE);
		Movie movie = result.get(0);

		assertEquals("冲出宁静号", movie.getName());
		assertEquals(2005, movie.getYear());
		assertEquals(-1, movie.getImdbId());
		assertEquals(16320, movie.getTmdbId());
	}

	@Test
	public void searchByNameWithYearShortName() throws Exception {
		List<Movie> result = db.searchMovie("Up 2009", Locale.ENGLISH);
		Movie movie = result.get(0);

		assertEquals("Up", movie.getName());
		assertEquals(2009, movie.getYear());
		assertEquals(-1, movie.getImdbId());
		assertEquals(14160, movie.getTmdbId());
	}

	@Test
	public void searchByNameWithYearNumberName() throws Exception {
		List<Movie> result = db.searchMovie("9 (2009)", Locale.ENGLISH);
		Movie movie = result.get(0);

		assertEquals("9", movie.getName());
		assertEquals(2009, movie.getYear());
		assertEquals(-1, movie.getImdbId());
		assertEquals(12244, movie.getTmdbId());
	}

	@Test
	public void searchByNameGerman() throws Exception {
		List<Movie> result = db.searchMovie("Die Gelbe Hölle", Locale.GERMAN);
		Movie movie = result.get(0);

		assertEquals("Die gelbe Hölle", movie.getName());
		assertEquals(1958, movie.getYear());
		assertEquals("[Die gelbe Hölle (1958), The Camp on Blood Island (1958)]", movie.getEffectiveNames().toString());
	}

	@Test
	public void searchByNameMexican() throws Exception {
		List<Movie> result = db.searchMovie("Suicide Squad", new Locale("es", "MX"));
		Movie movie = result.get(0);

		assertEquals("Escuadrón suicida", movie.getName());
		assertEquals(2016, movie.getYear());
		assertEquals(-1, movie.getImdbId());
		assertEquals(297761, movie.getTmdbId());
	}

	@Test
	public void searchByIMDB() throws Exception {
		Movie movie = db.getMovieDescriptor(new Movie(418279), Locale.ENGLISH);

		assertEquals("Transformers", movie.getName());
		assertEquals(2007, movie.getYear(), 0);
		assertEquals(418279, movie.getImdbId(), 0);
		assertEquals(1858, movie.getTmdbId(), 0);
	}

	@Test
	public void getMovieInfo() throws Exception {
		MovieInfo movie = db.getMovieInfo(new Movie(418279), Locale.ENGLISH, true);

		assertEquals("Transformers", movie.getName());
		assertEquals("2007-06-27", movie.getReleased().toString());
		assertEquals("PG-13", movie.getCertification());
		assertEquals("[es, en]", movie.getSpokenLanguages().toString());
		assertEquals("Shia LaBeouf", movie.getActors().get(0));
		assertEquals("Michael Bay", movie.getDirector());
	}

	@Test
	public void getAlternativeTitles() throws Exception {
		Map<String, List<String>> titles = db.getAlternativeTitles(16320); // Serenity

		assertEquals("[宁静号]", titles.get("HK").toString());
	}

	@Test
	public void getArtwork() throws Exception {
		Artwork a = db.getArtwork(16320, "backdrops", Locale.ROOT).get(0);
		assertEquals("[backdrops, 1920x1080]", a.getTags().toString());
		assertEquals("http://image.tmdb.org/t/p/original/mQPg3iZyztfzFNwrW40nCUtXy2l.jpg", a.getUrl().toString());
	}

	@Test
	public void getPeople() throws Exception {
		Person p = db.getMovieInfo("16320", Locale.ENGLISH, true).getCrew().get(0);
		assertEquals("Nathan Fillion", p.getName());
		assertEquals("Mal", p.getCharacter());
		assertEquals(null, p.getJob());
		assertEquals(null, p.getDepartment());
		assertEquals("0", p.getOrder().toString());
		assertEquals("http://image.tmdb.org/t/p/original/B7VTVtnKyFk0AtYjEbqzBQlPec.jpg", p.getImage().toString());
	}

	@Test
	public void discoverPeriod() throws Exception {
		Movie m = db.discover(LocalDate.parse("2014-09-15"), LocalDate.parse("2014-10-22"), Locale.ENGLISH).get(0);

		assertEquals("John Wick", m.getName());
		assertEquals(2014, m.getYear());
		assertEquals(245891, m.getTmdbId());
	}

	@Test
	public void discoverBestOfYear() throws Exception {
		Movie m = db.discover(2015, Locale.ENGLISH).get(0);

		assertEquals("Mad Max: Fury Road", m.getName());
		assertEquals(2015, m.getYear());
		assertEquals(76341, m.getTmdbId());
	}

	@Ignore
	@Test
	public void floodLimit() throws Exception {
		for (Locale it : Locale.getAvailableLocales()) {
			List<Movie> results = db.searchMovie("Serenity", it);
			assertEquals(16320, results.get(0).getTmdbId());
		}
	}

	@Ignore
	@Test
	public void etag() throws Exception {
		Cache cache = Cache.getCache("test", CacheType.Persistent);
		Cache etagStorage = Cache.getCache("etag", CacheType.Persistent);
		CachedResource<String, byte[]> resource = cache.bytes("http://devel.squid-cache.org/old_projects.html#etag", URL::new).fetch(fetchIfNoneMatch(etagStorage::get, etagStorage::put)).expire(Duration.ZERO);
		assertArrayEquals(resource.get(), resource.get());
	}

}
