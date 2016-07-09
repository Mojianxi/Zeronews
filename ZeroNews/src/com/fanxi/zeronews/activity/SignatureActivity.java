package com.fanxi.zeronews.activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.util.PrefUtils;
public class SignatureActivity extends BaseActivity implements OnClickListener {
	private Button btn_save;
	private EditText et_signature;
	private ImageView iv_back;
	private String signature;
	private String signature1;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		signature1=PrefUtils.getString(this, "signature", "");
		setContentView(R.layout.activity_signature);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setVisibility(View.VISIBLE);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		et_signature = (EditText) findViewById(R.id.et_signature);
		et_signature.setText(signature1);
		btn_save.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			signature = et_signature.getText().toString();
			PrefUtils.setString(this, "signature", signature);
			finish();
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
			break;
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
			break;
		}
	}
}
