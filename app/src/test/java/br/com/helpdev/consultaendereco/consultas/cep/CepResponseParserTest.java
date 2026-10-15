package br.com.helpdev.consultaendereco.consultas.cep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CepResponseParserTest {
    @Test
    public void normalizesFormattedAndUnformattedCep() {
        assertEquals("14090420", CepResponseParser.normalize("14090-420"));
        assertEquals("14090420", CepResponseParser.normalize("14090420"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCepWithWrongLength() {
        CepResponseParser.normalize("1234");
    }

    @Test
    public void parsesViaCepAddress() {
        assertEquals("Rua Exemplo, Centro, Ribeirao Preto - SP, 14090-420, Brasil",
                CepResponseParser.fromViaCep("""
                        {"cep":"14090-420","logradouro":"Rua Exemplo","bairro":"Centro","localidade":"Ribeirao Preto","uf":"SP"}
                        """, "14090420"));
    }

    @Test
    public void parsesBrasilApiAddressWhenViaCepHasNoStreet() {
        assertEquals("Av. Independencia, Centro, Ribeirao Preto - SP, 14090-420, Brasil",
                CepResponseParser.fromBrasilApi("""
                        {"cep":"14090-420","street":"Av. Independencia","neighborhood":"Centro","city":"Ribeirao Preto","state":"SP"}
                        """, "14090420"));
    }

    @Test
    public void recognizesNotFoundResponsesAsEmpty() {
        assertNull(CepResponseParser.fromViaCep("{\"erro\":true}", "00000000"));
        assertNull(CepResponseParser.fromBrasilApi("{}", "00000000"));
    }
}
