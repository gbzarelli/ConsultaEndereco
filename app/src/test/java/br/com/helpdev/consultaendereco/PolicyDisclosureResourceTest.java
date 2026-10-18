package br.com.helpdev.consultaendereco;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Test;

public class PolicyDisclosureResourceTest {
    @Test
    public void portugueseAboutScreenDisclosesOfficialCepSourceAndIndependence() throws Exception {
        File stringsFile = findPortugueseStrings();
        String strings = new String(Files.readAllBytes(stringsFile.toPath()), StandardCharsets.UTF_8);

        assertTrue("The About screen must link to the official Correios CEP source",
                strings.contains("https://buscacepinter.correios.com.br/app/cep/index.php"));
        assertTrue("The About screen must state that the app is independent of government entities",
                strings.contains("não representa, não é afiliado nem é operado pelo Governo do Brasil"));

        String aboutLayout = new String(Files.readAllBytes(findAboutLayout().toPath()), StandardCharsets.UTF_8);
        assertTrue("The About screen must display the government independence disclosure",
                aboutLayout.contains("@string/sobre_aviso_independencia"));
        assertTrue("The About screen must display the CEP source link",
                aboutLayout.contains("@string/sobre_fontes_cep"));
    }

    private File findPortugueseStrings() {
        return findFile("res/values-pt/strings.xml");
    }

    private File findAboutLayout() {
        return findFile("res/layout/frag_about.xml");
    }

    private File findFile(String path) {
        File[] candidates = {
                new File(path),
                new File("../" + path)
        };
        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return candidate;
            }
        }
        throw new AssertionError("Unable to find " + path);
    }
}
