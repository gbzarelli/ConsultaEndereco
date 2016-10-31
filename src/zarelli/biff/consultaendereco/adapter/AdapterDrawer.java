package zarelli.biff.consultaendereco.adapter;

import zarelli.biff.consultaendereco.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterDrawer extends BaseAdapter {

	private String[] itens;
	private LayoutInflater layoutInflater;

	public AdapterDrawer(Context context, String[] itens) {
		this.itens = itens;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return itens.length;
	}

	@Override
	public Object getItem(int position) {
		return itens[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemHelper itemHelper = new ItemHelper();
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.drawer_list_item,
					null);
			itemHelper.tv = (TextView) convertView
					.findViewById(R.id.drawer_item);
			convertView.setTag(itemHelper);
		} else {
			itemHelper = (ItemHelper) convertView.getTag();
		}

		itemHelper.tv.setText(itens[position]);
		return convertView;
	}

	private class ItemHelper {
		TextView tv;
	}
}
