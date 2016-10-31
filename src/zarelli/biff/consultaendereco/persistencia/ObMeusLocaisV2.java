/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zarelli.biff.consultaendereco.persistencia;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author guilherme
 */
public class ObMeusLocaisV2 implements Serializable {

    private ArrayList<ObLocais> meus_locais;

    public ObMeusLocaisV2() {
        this(new ArrayList<ObLocais>());
    }

    public ObMeusLocaisV2(ArrayList<ObLocais> meus_locais) {
        this.meus_locais = meus_locais;
    }

    public ArrayList<ObLocais> getMeus_locais() {
        return meus_locais;
    }

    public void setMeus_locais(ArrayList<ObLocais> meus_locais) {
        this.meus_locais = meus_locais;
    }

    public boolean addItem(ObLocais s) {
        if (!meus_locais.contains(s)) {
            meus_locais.add(0,s);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeItem(ObLocais s) {
        return meus_locais.remove(s);
    }
}
