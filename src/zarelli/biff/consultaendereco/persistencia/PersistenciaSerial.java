package zarelli.biff.consultaendereco.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @author Guilherme Biff Zarelli
 */
public class PersistenciaSerial<T> {

	private File file;

	public PersistenciaSerial(File file) {
		this.file = file;
	}

	public boolean persistir(T objeto) throws Exception {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(objeto);
		oos.close();
		fos.close();
		return true;
	}

	public T pegarObjeto() throws Exception {
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		@SuppressWarnings("unchecked")
		T ob = (T) ois.readObject();
		fis.close();
		ois.close();
		return ob;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
