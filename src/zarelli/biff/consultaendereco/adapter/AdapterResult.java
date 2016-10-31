package zarelli.biff.consultaendereco.adapter;

import java.util.ArrayList;

import zarelli.biff.consultaendereco.MyApplication;
import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.persistencia.ManagerMyPlaces;
import zarelli.biff.consultaendereco.persistencia.ObLocais;
import zarelli.biff.consultaendereco.uteis.AlertDialogFavority;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterResult extends BaseAdapter implements OnClickListener,
		OnCheckedChangeListener {
	private ArrayList<String> itens;
	private LayoutInflater layoutInflater;
	private ManagerMyPlaces managerMyPlaces;
	private Activity activity;

	public AdapterResult(Activity activity, ArrayList<String> itens) {
		this.activity = activity;
		this.itens = itens;
		layoutInflater = LayoutInflater.from(activity);
		managerMyPlaces = ManagerMyPlaces.getInstance(activity);
	}

	@Override
	public int getCount() {
		return itens.size();
	}

	@Override
	public String getItem(int arg0) {
		return itens.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ItemHelper itemHelper = new ItemHelper();
		if (arg1 == null) {
			arg1 = layoutInflater.inflate(R.layout.item_result, null);
			itemHelper.local = (TextView) arg1
					.findViewById(R.id.item_result_txv);
			itemHelper.local.setText(getItem(arg0));
			itemHelper.maps = (ImageView) arg1
					.findViewById(R.id.item_result_location);
			itemHelper.maps.setTag(arg0);
			itemHelper.maps.setOnClickListener(this);

			itemHelper.favorite = (CheckBox) arg1
					.findViewById(R.id.item_result_star);

			try {
				boolean sucess = false;
				for (ObLocais ob : managerMyPlaces.getMyPlaces()
						.getMeus_locais()) {
					if (getItem(arg0).equalsIgnoreCase((ob.getLocal().trim()))) {
						itemHelper.favorite.setChecked(true);
						itemHelper.favorite.setTag(ob);
						sucess = true;
						break;
					} else {
						itemHelper.favorite.setChecked(false);
					}
				}
				if (!sucess) {
					itemHelper.favorite.setTag(new ObLocais("", getItem(arg0),
							"", ""));
				}
			} catch (Exception e) {
				Log.e(MyApplication.TAG, "getObLocais", e);
			}

			itemHelper.favorite.setOnCheckedChangeListener(this);
			arg1.setTag(itemHelper);
		} else {
			itemHelper = (ItemHelper) arg1.getTag();
		}
		return arg1;
	}

	private class ItemHelper {
		TextView local;
		CheckBox favorite;
		ImageView maps;
	}

	@Override
	public void onClick(View v) {
		try {
			String local = getItem((Integer) v.getTag());
			Uri uri = Uri.parse("geo:0,0?q=" + local);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			activity.startActivity(it);
		} catch (Exception e) {
			Log.e(MyApplication.TAG, "openMaps", e);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CheckBox checkBox = (CheckBox) buttonView;
		new AlertDialogFavority(activity, (ObLocais) checkBox.getTag(),
				checkBox, this, isChecked).show();
	}

}
