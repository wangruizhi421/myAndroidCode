package com.rzteam.mobilesafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.rzteam.mobilesafe.utils.StreamUtils;

public class SplashActivity extends Activity {
	protected static final int CODE_UPDATE_DIALOG = 1;
	protected static final int CODE_URL_ERROR = 2;
	protected static final int CODE_NET_ERROR = 3;
	protected static final int CODE_JSON_ERROR = 4;
	//�������ļ�
	private String mVersionName;
	private int mVersionCode;
	private String mDes;
	private String mDownloadUrl;
	
	private TextView tv_version;

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "��������ַ����", Toast.LENGTH_SHORT).show();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "�������", Toast.LENGTH_SHORT).show();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "json��������", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("�汾��:"+getVersionName());
		
		//���汾
		checkVersion();
	}

	/***
	 * ��ȡ��ǰ�汾����
	 * @return
	 */
	private String getVersionName() {
		PackageManager manager = getPackageManager();
		try {
			//��ȡ����Ϣ
			PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	/***
	 * ��ȡ��ǰ�汾��
	 * @return
	 */
	private int getVersionCode() {
		PackageManager manager = getPackageManager();
		try {
			//��ȡ����Ϣ
			PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/***
	 * ���������汾
	 */
	private void checkVersion() {
		new Thread(){
			Message msg = Message.obtain();
			HttpURLConnection conn = null;
			@Override
			public void run() {
				try {
					URL url = new URL("http://10.0.2.2:8480/mobilesafe.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);//������Ӧ��ʱ
					conn.setReadTimeout(5000);//������Ӧ��ʱ
					conn.connect();
					int responseCode = conn.getResponseCode();
					if(responseCode == 200){
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.getStringFromStream(inputStream);
						//System.out.println(result);
						JSONObject jsonObj = new JSONObject(result);
						mVersionName = jsonObj.getString("versionName");
						mVersionCode = jsonObj.getInt("versionCode");
						mDes = jsonObj.getString("des");
						mDownloadUrl = jsonObj.getString("downloadUrl");
						//�жϵ�ǰ�汾�Ƿ���Ҫ����
						if(mVersionCode > getVersionCode()) {
							msg.what = CODE_UPDATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					// url�����쳣
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// ��������쳣
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = CODE_JSON_ERROR;
					// json�����쳣
					e.printStackTrace();
				}finally{
					mHandler.sendMessage(msg);
					if(conn != null){
						conn.disconnect();
					}
				}
			};
		}.start();
	}

	/***
	 * ���´���
	 */
	public void showUpdateDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("���°汾:"+mVersionName);
		builder.setMessage(mDes);
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("ȷ������");
			}
		});
		builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("�Ժ���˵");
			}
		});
		builder.show();
	}

}
