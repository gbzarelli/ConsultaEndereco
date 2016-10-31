package zarelli.biff.consultaendereco.screens.consult;

import java.util.List;

import zarelli.biff.consultaendereco.MyApplication;
import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.ads.FragmentAds;
import zarelli.biff.consultaendereco.process.RecognizeSpeech;
import zarelli.biff.consultaendereco.process.RecognizeSpeech.CallbackRecognizeSpeech;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragConsult extends FragmentAds implements
		OnCheckedChangeListener, OnClickListener, CallbackRecognizeSpeech {
	private RadioGroup radioGroup;
	private ImageButton imageButton;
	private EditText address;
	private RecognizeSpeech reconhecimentoFala;
	private String backupAddres, backupZipCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		backupAddres = "";
		backupZipCode = "";
	}

	@Override
	public View myOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_find_address, null);
		radioGroup = (RadioGroup) view.findViewById(R.id.frag_find_radiogroup);
		imageButton = (ImageButton) view.findViewById(R.id.frag_find_speak);
		address = (EditText) view.findViewById(R.id.frag_find_edit);
		(view.findViewById(R.id.frag_find_bt)).setOnClickListener(this);

		address.clearFocus();

		imageButton.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(this);

		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.frag_find_rb1:
			imageButton.setVisibility(View.VISIBLE);
			backupZipCode = address.getText().toString();
			address.setText(backupAddres);
			address.setInputType(InputType.TYPE_CLASS_TEXT);
			address.setHint(R.string.find_address_hint_address);
			break;
		case R.id.frag_find_rb2:
			imageButton.setVisibility(View.GONE);
			backupAddres = address.getText().toString();
			address.setText(backupZipCode);
			address.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			address.setHint(R.string.find_address_hint_zipcode);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frag_find_speak:
			reconhecimentoFala = new RecognizeSpeech(this, this, true);
			reconhecimentoFala.startCapture(getString(R.string.capture_speak),
					110);
			break;
		case R.id.frag_find_bt:
			Intent it = new Intent(getActivity(), ScreenResult.class);
			String value = address.getText().toString().trim();
			if (value.length() > 0) {
				switch (radioGroup.getCheckedRadioButtonId()) {
				case R.id.frag_find_rb1:
					it.putExtra(ScreenResult.IT_ADDRESS, value);
					break;
				case R.id.frag_find_rb2:
					it.putExtra(ScreenResult.IT_ZIPCODE, value);
					break;
				}
				startActivity(it);
			} else {
				address.setError(getString(R.string.find_address_enter_register));
			}
		}
	}

	@Override
	public void onDestroyView() {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(address.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		super.onDestroyView();
	}

	@Override
	public void retorno(String action, List<String> retornos, int selecionado,
			int intent_request) {
		try {
			if (action.compareTo(ACTION_SUCESSO) == 0) {
				address.setText(retornos.get(selecionado));
			}
		} catch (Exception e) {
			Log.e(MyApplication.TAG, "ret", e);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (reconhecimentoFala != null) {
			reconhecimentoFala.setActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
