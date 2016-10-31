package zarelli.biff.consultaendereco.persistencia;

import java.io.File;

import android.content.Context;

public class ManagerMyPlaces {

	private ObMeusLocaisV2 obMeusLocaisV2;
	private PersistenciaSerial<ObMeusLocaisV2> obSerial;
	private static ManagerMyPlaces managerMyPlaces;

	public static ManagerMyPlaces getInstance(Context context) {
		if (managerMyPlaces == null) {
			managerMyPlaces = new ManagerMyPlaces(context);
		}
		return managerMyPlaces;
	}

	private ManagerMyPlaces(Context context) {
		File file = context.getFileStreamPath("obmeuslocaisv2.obj");
		obSerial = new PersistenciaSerial<ObMeusLocaisV2>(file);
		obMeusLocaisV2 = null;
	}

	public ObMeusLocaisV2 getMyPlaces() throws Exception {
		if (obMeusLocaisV2 == null) {
			obMeusLocaisV2 = new ObMeusLocaisV2();

			obMeusLocaisV2 = obSerial.pegarObjeto();
			if (obMeusLocaisV2 == null) {
				obMeusLocaisV2 = new ObMeusLocaisV2();
				obSerial.persistir(obMeusLocaisV2);
			}
		}
		return obMeusLocaisV2;
	}

	public void remover(ObLocais ob) throws Exception {
		obMeusLocaisV2.removeItem(ob);
	}

	public void persistir() throws Exception {
		obSerial.persistir(obMeusLocaisV2);
	}

}
