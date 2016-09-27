package bob.commons;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import bob.commons.XmlFile.XmlValue;

/**
 * Prüft die Klasse {@link XmlFile}.
 * 
 * @author maik@btmx.net
 *
 */
public class XmlFileTest {

	/** Konstante f�r Schl�ssel */
	private static final String KEY = "junit.1";

	@Test
	public void testCreateResource() throws FileNotFoundException {
		final XmlFile x = XmlFile.createResource("/resources/demoxmlfile.xml");
		Assert.assertNotNull("Ressource nicht vorhanden?", x);
		final XmlValue value = x.getValue(KEY);
		Assert.assertNotNull("Schl�ssel unbekannt?", value);
		Assert.assertEquals(KEY, value.getId());
		Assert.assertEquals("erste Zeile und zweite Zeile", value.getContent());
		Assert.assertEquals("Kommentar zum Testeintrag", value.getDesc());
	}

	@Test(expected = FileNotFoundException.class)
	public void testCreateResource_Datei_nicht_vorhanden() throws FileNotFoundException {
		XmlFile.createResource("/file/not/found.xml");
	}

	@Test
	public void testCreateResource_leerer_Testeintrag() throws FileNotFoundException {
		final XmlFile x = XmlFile.createResource("/resources/demoxmlfile.xml");
		final XmlValue value = x.getValue("junit.2");
		Assert.assertEquals("", value.getContent());
	}

	@Test
	public void testCreateResource_unbekannter_Schluessel() throws FileNotFoundException {
		final XmlFile x = XmlFile.createResource("/resources/demoxmlfile.xml");
		final XmlValue value = x.getValue("junit.xxx");
		Assert.assertNull(value);
	}

	@Test
	public void testCreateResource_korrupte_Datei() throws FileNotFoundException {
		final XmlFile x = XmlFile.createResource("/resources/demoxmlfile_corrupt.xml");
		Assert.assertNotNull("Ressource nicht vorhanden?", x);
		final XmlValue value = x.getValue("xxx");
		Assert.assertNull(value);
	}

}
