package zarelli.biff.consultaendereco.uteis;

import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.persistencia.ManagerMyPlaces;
import zarelli.biff.consultaendereco.persistencia.ObLocais;
import zarelli.biff.consultaendereco.screens.edit.ScreenEditMyPlace;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AlertDialogFavority extends AlertDialog.Builder {
	private Activity activity;
	private ObLocais ob;
	private CheckBox checkBox;
	private ManagerMyPlaces managerMyPlaces;
	private OnCheckedChangeListener onCheckedChangeListener;
	private boolean isChecked;

	public AlertDialogFavority(Activity activity, ObLocais ob,
			CheckBox checkBox, OnCheckedChangeListener onCheckedChangeListener,
			boolean isChecked) {
		super(activity);
		this.activity = activity;
		this.ob = ob;
		this.checkBox = checkBox;
		this.onCheckedChangeListener = onCheckedChangeListener;
		this.isChecked = isChecked;

		managerMyPlaces = ManagerMyPlaces.getInstance(activity);
		construir();
	}

	private void construir() {
		setCancelable(false);
		setTitle(R.string.app_name);
		setIcon(R.drawable.ic_launcher);
		if (isChecked) {
			setMessage(R.string.alert_edit_favorite);
			setPositiveButton(R.string.alert_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent it = new Intent(activity,
									ScreenEditMyPlace.class);
							it.putExtra(ScreenEditMyPlace.IT_OBLOCAL, ob);
							activity.startActivity(it);
						}
					});
			setNegativeButton(R.string.alert_no,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								managerMyPlaces.getMyPlaces().addItem(ob);
								managerMyPlaces.persistir();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		} else {
			setMessage(R.string.alert_remove_favorite);
			setPositiveButton(R.string.alert_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								managerMyPlaces.getMyPlaces().removeItem(ob);
								managerMyPlaces.persistir();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			setNegativeButton(R.string.alert_no,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBox.setOnCheckedChangeListener(null);
							checkBox.setChecked(true);
							checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
						}
					});
		}
	}
}
