package zarelli.biff.consultaendereco.process;

import java.util.ArrayList;
import java.util.List;

import zarelli.biff.consultaendereco.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;

/**
 * 
 * @author Guilherme Biff Zarelli
 */
public class RecognizeSpeech {

	public interface CallbackRecognizeSpeech {

		public static final String ACTION_SUCESSO = "sucesso";
		public static final String ACTION_NAO_SUPORTADO = "nao_suportado";

		public void retorno(String action, List<String> retornos, int selecionado,
				int intent_request);
	}

	/*
	 * 
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (reconhecimentoFala != null) {
	 * reconhecimentoFala.setActivityResult(requestCode, resultCode, data); }
	 * super.onActivityResult(requestCode, resultCode, data); }
	 */

	private List<String> retorno_speech;
	private int resultado_selecionado, intent_request;
	private Activity activity;
	private Fragment frag;
	private CallbackRecognizeSpeech listener;
	private boolean alertas;

	public RecognizeSpeech(Activity activity,
			CallbackRecognizeSpeech listener, boolean alertas) {
		this.activity = activity;
		this.listener = listener;
		this.alertas = alertas;
	}

	public RecognizeSpeech(Fragment frag,
			CallbackRecognizeSpeech listener, boolean alertas) {
		this(frag.getActivity(), listener, alertas);
		this.frag = frag;
	}

	public void startCapture(String descricao, int intent_request) {
		this.intent_request = intent_request;

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, descricao);

		if (isIntentAvailable()) {
			if (frag == null) {
				activity.startActivityForResult(intent, intent_request);
			} else {
				frag.startActivityForResult(intent, intent_request);
			}
		} else {
			if (alertas) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(activity.getString(R.string.recognizerspeech_alert_fail));
				builder.setMessage(activity.getString(R.string.recognizerspeech_alert_not_support));
				builder.setPositiveButton("OK", null);
				AlertDialog alerta = builder.create();
				alerta.show();
			}
			if (listener != null) {
				List<String> lista = new ArrayList<String>();
				lista.add(activity.getString(R.string.recognizerspeech_alert_not_support));
				listener.retorno(
						CallbackRecognizeSpeech.ACTION_NAO_SUPORTADO, lista,
						0, intent_request);
			}
		}
	}

	private void retornoRecognizeSpeech() {
		CharSequence[] lista = new CharSequence[retorno_speech.size()];
		for (int i = 0; i < retorno_speech.size(); i++) {
			lista[i] = retorno_speech.get(i).toString();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.recognizerspeech_alert_what));
		builder.setSingleChoiceItems(lista, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						resultado_selecionado = arg1;
					}
				});
		builder.setPositiveButton("Confirmar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listener != null) {
							listener.retorno(
									CallbackRecognizeSpeech.ACTION_SUCESSO,
									retorno_speech, resultado_selecionado,
									intent_request);
						}
					}
				});

		builder.create().show();
	}

	public void setActivityResult(int request_code, int result_code, Intent data) {
		System.out.println("called");
		if (request_code == intent_request && result_code == Activity.RESULT_OK) {
			retorno_speech = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (alertas) {
				retornoRecognizeSpeech();
			} else {
				if (listener != null) {
					listener.retorno(CallbackRecognizeSpeech.ACTION_SUCESSO,
							retorno_speech, 0, intent_request);
				}
			}
		}
	}

	private boolean isIntentAvailable() {
		return activity
				.getPackageManager()
				.queryIntentActivities(
						new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
						PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}
}
