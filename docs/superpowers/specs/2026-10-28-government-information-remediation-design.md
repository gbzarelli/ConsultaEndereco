# Consulta Endereço government-information remediation design

## Intent
Resolve the repeated Google Play Misleading Claims rejection while preserving the app as an independent private utility. The review evidence still quotes the previous pt-BR description without a source or an independence disclaimer; the new submission must make the required information unambiguous in every reviewer-visible surface.

## Goals
- State independence from the Brazilian Government, Correios, and public entities prominently in the pt-BR listing and in the app.
- Identify both the direct Correios Busca CEP page and an official Ministry of Communications reference that describes Busca CEP.
- Keep the direct Correios URL out of the initial search screen, as explicitly requested by the user.
- Provide a reviewer-visible screen capture of the in-app source and disclaimer screen.
- Release a new production AAB with versionCode 6 / versionName 3.0.5.

## Non-goals
- Claim affiliation, authorization, endorsement, or operation by any government entity.
- Change the app package, ads integration, maps integration, or CEP-search behavior.
- Appeal the current decision before a fully replaced source/disclaimer submission is reviewed.

## Approaches considered
1. **Resubmit the existing draft unchanged.** It leaves the stale-evidence discrepancy unresolved and risks another rejection.
2. **Appeal immediately.** The policy guidance for unaffiliated apps asks for a correction and resubmission; the present evidence is insufficient to show that the reviewer evaluated the current listing.
3. **Replace the disclosure copy, add a second official source, make the in-app disclosure more discoverable, and submit a new artifact.** This creates a clear new review target and directly implements each required remedy. This is the selected approach.

## Design
- The initial screen retains a visible Portuguese independence notice without a Correios URL and names the disclosure destination “Fontes oficiais e aviso de independência”.
- The disclosure page links to the direct Correios Busca CEP page and the Ministry of Communications reference. Its heading and body explicitly say the app is private and independent.
- The Play full description begins with uppercase-independent-status and official-source sections before product features. It uses `https://buscacepinter.correios.com.br/app/endereco/index.php` and `https://www.gov.br/mcom/pt-br/acesso-a-informacao/dicionario.pdf` as sources.
- The privacy page uses the same two sources and exact independent-app claim.
- A Play screenshot captures the disclosure screen. Existing screenshots stay unless the new capture replaces an existing policy-unhelpful asset.
- Version 6 is built, signed, emulator-checked, uploaded, and reviewed after the site policy is live.

## Constraints
- Application ID and namespace remain `br.com.helpdev.consultaendereco`.
- The initial screen cannot directly display a Correios URL.
- The HelpDev page is released on `main` with the next annotated `v*` tag.
- The final Play submission still requires the user’s action-time confirmation.
