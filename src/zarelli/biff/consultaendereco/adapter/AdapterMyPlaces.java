package zarelli.biff.consultaendereco.adapter;

import java.util.ArrayList;
import java.util.List;

import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.persistencia.ObLocais;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class AdapterMyPlaces extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<ObLocais> obMeusLocaisV2;
	private ArrayList<ObLocais> obMeusLocaisV2_exibidos;

	public AdapterMyPlaces(Context context, ArrayList<ObLocais> obMeusLocaisV2) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.obMeusLocaisV2 = obMeusLocaisV2;
		this.obMeusLocaisV2_exibidos = obMeusLocaisV2;
	}

	@Override
	public int getCount() {
		return obMeusLocaisV2_exibidos.size();
	}

	@Override
	public ObLocais getItem(int arg0) {
		return obMeusLocaisV2_exibidos.get(arg0);

	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ObLocais obLocais = getItem(arg0);
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.simple_text_view, null);
		}
		String obs = "";
		if (obLocais.getObs().trim().length() > 0) {
			obs = "\nObs: " + obLocais.getObs();
		}
		String item = obLocais.getNome() + " - " + obLocais.getTelefone()
				+ "\n" + obLocais.getLocal() + obs;
		((TextView) arg1).setText(item);
		return arg1;
	}

	public Filter getFilter() {
		Filter filter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence filtro) {
				FilterResults results = new FilterResults();
				// se não foi realizado nenhum filtro insere todos os itens.
				if (filtro == null || filtro.length() <= 1) {
					results.count = obMeusLocaisV2.size();
					results.values = obMeusLocaisV2;
				} else {
					// cria um array para armazenar os objetos filtrados.
					List<ObLocais> itens_filtrados = new ArrayList<ObLocais>();

					// percorre toda lista verificando se contem a palavra do
					// filtro na descricao do objeto.
					for (int i = 0; i < obMeusLocaisV2.size(); i++) {
						ObLocais data = obMeusLocaisV2.get(i);

						filtro = filtro.toString().toLowerCase();
						String condicao = data.toString().toLowerCase();

						if (condicao.contains(filtro.toString().toLowerCase())) {
							// se conter adiciona na lista de itens filtrados.
							itens_filtrados.add(data);
						}
					}
					// Define o resultado do filtro na variavel FilterResults
					results.count = itens_filtrados.size();
					results.values = itens_filtrados;
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					Filter.FilterResults results) {
				obMeusLocaisV2_exibidos = (ArrayList<ObLocais>) results.values; // Valores
				// filtrados.
				notifyDataSetChanged(); // Notifica a lista de alteração
			}

		};
		return filter;
	}
}
