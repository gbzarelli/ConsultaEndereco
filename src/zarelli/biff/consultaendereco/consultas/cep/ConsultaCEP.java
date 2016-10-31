/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zarelli.biff.consultaendereco.consultas.cep;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author guilherme
 */
public class ConsultaCEP {

    public String getEndereco(String cep) throws Exception {

        String path = "http://cep.republicavirtual.com.br/web_cep.php?cep=" + cep + "&formato=xml";

        StringBuilder resposta = new StringBuilder();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.connect();

        BufferedReader dis = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "ISO-8859-1"));
        StringBuilder xml = new StringBuilder();
        String linha;

        while ((linha = dis.readLine()) != null) {
            xml.append(linha);
        }

        int resultado = 0;
        int start = xml.toString().indexOf("<resultado>") + "<resultado>".length();
        int end = xml.toString().indexOf("</resultado>");
        String retorno = xml.toString().substring(start, end);
        resultado = Integer.parseInt(retorno);
        System.out.println(resultado);
        if (resultado > 0) {
            start = xml.toString().indexOf("<tipo_logradouro>") + "<tipo_logradouro>".length();
            end = xml.toString().indexOf("</tipo_logradouro>");

            resposta.append(xml.toString().substring(start, end)).append(" ");

            start = xml.toString().indexOf("<logradouro>") + "<logradouro>".length();
            end = xml.toString().indexOf("</logradouro>");
            resposta.append(xml.toString().substring(start, end)).append(", ");

            start = xml.toString().indexOf("<cidade>") + "<cidade>".length();
            end = xml.toString().indexOf("</cidade>");
            resposta.append(xml.toString().substring(start, end)).append(" - ");

            start = xml.toString().indexOf("<uf>") + "<uf>".length();
            end = xml.toString().indexOf("</uf>");
            resposta.append(xml.toString().substring(start, end)).append(", ");
            resposta.append(cep).append(", Brasil");
        }

        return resposta.toString();


    }
}
