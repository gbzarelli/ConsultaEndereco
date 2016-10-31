package zarelli.biff.consultaendereco.persistencia;

import java.io.Serializable;

/**
 * @author guilherme
 *
 */
/**
 * @author guilherme
 *
 */
public class ObLocais implements Serializable {

	private String nome;
	private String local;
	private String telefone;
	private String obs;

	public ObLocais(String nome, String local, String telefone, String obs) {
		super();
		this.nome = nome;
		this.local = local;
		this.telefone = telefone;
		this.obs = obs;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	@Override
	public String toString() {
		return "ObLocais [nome=" + nome + ", local=" + local + ", telefone="
				+ telefone + ", obs=" + obs + "]";
	}
	
}
