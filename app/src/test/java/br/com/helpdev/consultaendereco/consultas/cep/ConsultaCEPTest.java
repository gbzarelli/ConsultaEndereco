package br.com.helpdev.consultaendereco.consultas.cep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import org.junit.Test;

public class ConsultaCEPTest {
    @Test
    public void usesBrasilApiWhenViaCepReportsNotFound() throws Exception {
        ConsultaCEP consulta = new ConsultaCEP(new ConsultaCEP.ApiClient() {
            public String viaCep(String cep) { return "{\"erro\":true}"; }
            public String brasilApi(String cep) {
                return "{\"cep\":\"14090-420\",\"street\":\"Rua Exemplo\",\"neighborhood\":\"Centro\",\"city\":\"Ribeirao Preto\",\"state\":\"SP\"}";
            }
        });
        assertEquals("Rua Exemplo, Centro, Ribeirao Preto - SP, 14090-420, Brasil",
                consulta.getEndereco("14090-420"));
    }

    @Test
    public void returnsEmptyForUnknownCepWhenBothProvidersReportNoAddress() throws Exception {
        ConsultaCEP consulta = new ConsultaCEP(new ConsultaCEP.ApiClient() {
            public String viaCep(String cep) { return "{\"erro\":true}"; }
            public String brasilApi(String cep) { return "{}"; }
        });
        assertEquals("", consulta.getEndereco("00000000"));
    }

    @Test
    public void fallsBackWhenViaCepIsUnavailable() throws Exception {
        ConsultaCEP consulta = new ConsultaCEP(new ConsultaCEP.ApiClient() {
            public String viaCep(String cep) throws IOException { throw new IOException("offline"); }
            public String brasilApi(String cep) {
                return "{\"cep\":\"14090-420\",\"city\":\"Ribeirao Preto\",\"state\":\"SP\"}";
            }
        });
        assertEquals("Ribeirao Preto - SP, 14090-420, Brasil", consulta.getEndereco("14090420"));
    }

    @Test
    public void reportsNetworkFailureOnlyWhenBothProvidersFail() throws Exception {
        ConsultaCEP consulta = new ConsultaCEP(new ConsultaCEP.ApiClient() {
            public String viaCep(String cep) throws IOException { throw new IOException("via unavailable"); }
            public String brasilApi(String cep) throws IOException { throw new IOException("brasil unavailable"); }
        });
        try {
            consulta.getEndereco("14090420");
            fail("Expected the provider failure to reach the UI error state");
        } catch (IOException expected) {
            assertEquals("brasil unavailable", expected.getMessage());
        }
    }
}
