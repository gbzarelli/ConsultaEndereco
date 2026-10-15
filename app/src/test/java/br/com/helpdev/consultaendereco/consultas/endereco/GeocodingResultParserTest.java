package br.com.helpdev.consultaendereco.consultas.endereco;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeocodingResultParserTest {
    @Test
    public void formatsSingleAddressLine() {
        assertEquals("Rua das Flores, 10", GeocodingResultParser.formatAddressLines(
                Arrays.asList(" Rua das Flores, 10 ")));
    }

    @Test
    public void formatsEveryAddressLineAndSkipsNullOrBlankLines() {
        assertEquals("Rua das Flores, Centro, Curitiba",
                GeocodingResultParser.formatAddressLines(
                        Arrays.asList("Rua das Flores", null, " ", "Centro", "Curitiba")));
    }

    @Test
    public void parsesNominatimDisplayNamesAndIgnoresEmptyValues() throws Exception {
        List<String> results = GeocodingResultParser.parseNominatim(
                "[{\"display_name\":\"Rua A, Centro\"},{\"display_name\":\" \"},{}]");

        assertEquals(Arrays.asList("Rua A, Centro"), results);
    }
}
