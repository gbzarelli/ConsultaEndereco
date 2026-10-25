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

    @Test
    public void initialSearchScreenShowsGovernmentIndependenceNoticeWithoutDirectSourceLink()
            throws Exception {
        String strings = new String(Files.readAllBytes(findPortugueseStrings().toPath()),
                StandardCharsets.UTF_8);
        assertTrue("The initial search screen needs a concise independence notice",
                strings.contains("Aplicativo independente, sem vínculo com o Governo do Brasil"));

        String searchLayout = new String(Files.readAllBytes(findInitialSearchLayout().toPath()),
                StandardCharsets.UTF_8);
        assertTrue("The initial search screen must display the independence notice",
                searchLayout.contains("@string/consulta_aviso_independencia"));
        assertTrue("The initial search screen must identify the disclosure section",
                searchLayout.contains("@string/consulta_lb_transparencia"));
        assertTrue("The official source link remains in About, not on the initial screen",
                !searchLayout.contains("buscacepinter.correios.com.br"));
    }

    @Test
    public void disclosureMenuItemNamesSourcesInBothLanguages() throws Exception {
        String portugueseStrings = new String(Files.readAllBytes(findPortugueseStrings().toPath()),
                StandardCharsets.UTF_8);
        String defaultStrings = new String(Files.readAllBytes(findDefaultStrings().toPath()),
                StandardCharsets.UTF_8);

        assertTrue("The Portuguese drawer must make the transparency page discoverable",
                portugueseStrings.contains("<item>Transparência e fontes</item>"));
        assertTrue("The default drawer must make the transparency page discoverable",
                defaultStrings.contains("<item>Transparency and sources</item>"));
    }

    @Test
    public void releaseIdentityIsPreparedForPolicyRemediationVersionFive() throws Exception {
        String buildFile = new String(Files.readAllBytes(findFile("app/build.gradle").toPath()),
                StandardCharsets.UTF_8);
        String defaultStrings = new String(Files.readAllBytes(findDefaultStrings().toPath()),
                StandardCharsets.UTF_8);

        assertTrue("The resubmission must use a new Play version code",
                buildFile.contains("versionCode 5"));
        assertTrue("The identity guard must accept the remediation version",
                buildFile.contains("android.defaultConfig.versionCode != 5"));
        assertTrue("The About screen must show the remediation release version",
                defaultStrings.contains("<string name=\"version\">v3.0.4</string>"));
    }

    private File findPortugueseStrings() {
        return findFile("res/values-pt/strings.xml");
    }

    private File findDefaultStrings() {
        return findFile("res/values/strings.xml");
    }

    private File findAboutLayout() {
        return findFile("res/layout/frag_about.xml");
    }

    private File findInitialSearchLayout() {
        return findFile("res/layout/frag_find_address.xml");
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
