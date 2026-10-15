package br.com.helpdev.consultaendereco.screens.myplaces;

import br.com.helpdev.consultaendereco.MyApplication;
import br.com.helpdev.consultaendereco.R;
import br.com.helpdev.consultaendereco.adapter.AdapterMyPlaces;
import br.com.helpdev.consultaendereco.ads.FragmentAds;
import br.com.helpdev.consultaendereco.persistencia.ManagerMyPlaces;
import br.com.helpdev.consultaendereco.persistencia.ObLocais;
import br.com.helpdev.consultaendereco.persistencia.ObMeusLocaisV2;
import br.com.helpdev.consultaendereco.screens.edit.ScreenEditMyPlace;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FragMyPlaces extends FragmentAds implements
		AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
		DialogInterface.OnClickListener {

	private AdapterMyPlaces adapter;
	private ObMeusLocaisV2 obMeusLocais;
	private int index;
	private ManagerMyPlaces managerMyPlaces;

	@Override
	public View myOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_my_places, container, false);
		managerMyPlaces = ManagerMyPlaces.getInstance(getActivity());
		ListView lista = (ListView) view.findViewById(R.id.my_places_listview);

		try {
			obMeusLocais = managerMyPlaces.getMyPlaces();
			if (obMeusLocais.getMeus_locais().size() > 0) {
				adapter = new AdapterMyPlaces(getActivity(),
						obMeusLocais.getMeus_locais());
				lista.setAdapter(adapter);
				lista.setOnItemClickListener(this);
				lista.setOnItemLongClickListener(this);
			} else {
				view.findViewById(R.id.my_places_reg).setVisibility(
						View.VISIBLE);
			}

		} catch (Exception e) {
			Log.e(MyApplication.TAG, "error", e);
		}

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_view, menu);
		MenuItem searchItem = menu.findItem(R.id.search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		searchView.setQueryHint(getActivity().getString(R.string.search_hint));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				System.out.println(arg0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				if (adapter != null) {
					adapter.getFilter().filter(arg0);
				}
				return false;
			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		index = arg2;

		AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getActivity());
		alerBuilder.setItems(R.array.dialog_ops, this);
		alerBuilder.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Toast.makeText(getActivity(), R.string.toast_onclicklong,
				Toast.LENGTH_LONG).show();
		return false;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		try {
			ObLocais oblocal = managerMyPlaces.getMyPlaces().getMeus_locais()
					.get(index);
			switch (arg1) {
			case 0:
				if (oblocal.getTelefone().trim().length() > 5) {
					startActivity(new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + oblocal.getTelefone().trim())));
				} else {
					Toast.makeText(getActivity(), R.string.tel_nao_valido,
							Toast.LENGTH_LONG).show();
				}
				break;
			case 1:
				Intent it = new Intent(getActivity(), ScreenEditMyPlace.class);
				it.putExtra(ScreenEditMyPlace.IT_OBLOCAL, oblocal);
				it.putExtra(ScreenEditMyPlace.IT_ISEDIT, index);
				startActivityForResult(it, 1);
				break;
			case 2:
				try {
					obMeusLocais.removeItem(oblocal);
					managerMyPlaces.persistir();
					adapter.notifyDataSetChanged();
					if (obMeusLocais.getMeus_locais().size() <= 0) {
						getView().findViewById(R.id.my_places_reg)
								.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					Uri uri = Uri.parse("geo:0,0?q=" + oblocal.getLocal());
					it = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(it);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged();
	}

}
