package com.libs.zxing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.DecodeActivity;
import com.fanxi.zeronews.util.RGBLuminanceSource;
import com.fanxi.zeronews.util.Size;
import com.fanxi.zeronews.util.UiUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.decode.BeepManager;
import com.google.zxing.client.android.decode.CaptureActivityHandler;
import com.google.zxing.client.android.decode.InactivityTimer;
import com.google.zxing.client.android.decode.ViewfinderView;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private InactivityTimer inactivityTimer;
	private String characterSet;
	private BeepManager beepManager;
	private static final int REQUEST_CODE = 100;
	final static String profix1 = "?appid=";
	final static String profix2 = "-title=";
	final static String action = "muzhiwan.action.detail";
	final static String bundle_key = "detail";
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	ImageView opreateView;
	private String photo_path;
	private ImageButton button_function;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
	public Handler getHandler() {
		return handler;
	}
	public CameraManager getCameraManager() {
		return cameraManager;
	}
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);
		button_function = (ImageButton) findViewById(R.id.button_function);
		button_function.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				//打开手机中的相册
//				Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
//		        innerIntent.setType("image/*");
//		        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//		        startActivityForResult(wrapperIntent, REQUEST_CODE);
				Intent intentFromGallery = new Intent();
				// 设置文件类型
				intentFromGallery.setType("image/*");
				intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		opreateView = (ImageView) findViewById(R.id.button_openorcloseClick);
		opreateView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cameraManager != null) {
					Config.KEY_FRONT_LIGHT = !Config.KEY_FRONT_LIGHT;
					if (Config.KEY_FRONT_LIGHT == true) {
						opreateView
								.setImageResource(R.drawable.mzw_camera_close);
					} else {
						opreateView
								.setImageResource(R.drawable.mzw_camera_open);
					}
					cameraManager.getConfigManager().initializeTorch(
							cameraManager.getCamera().getParameters(), false);
					onPause();
					onResume();
				}
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==CODE_GALLERY_REQUEST){
			Log.e("TAG", "进入结果");
//			switch (requestCode) {
//			case CODE_GALLERY_REQUEST:
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				if(cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();
				Log.e("TAG", "关闭cursor");
				QRCodeReader codeReader=new QRCodeReader();
				Bitmap bitmap=BitmapFactory.decodeFile(photo_path);
				byte[] array=BitmapToArray(bitmap);
				int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                Log.e("TAG", "开启线程");
//				LuminanceSource source=new PlanarYUVLuminanceSource(array, bitmap.getWidth(), bitmap.getHeight(), 0, 0, bitmap.getWidth(), bitmap.getHeight(),false);
                RGBLuminanceSource source = new RGBLuminanceSource(pixels, new Size(width, height));
//				BinaryBitmap bitmap=BitmapFactory.decodeFile(photo_path);
				Binarizer binarizer=new HybridBinarizer(source);
				BinaryBitmap binaryBitmap=new BinaryBitmap(binarizer);
				try {
					Result result = codeReader.decode(binaryBitmap);
					 if(handler == null) {
							savedResultToShow = result;
						} else {
							if (result != null){
								savedResultToShow = result;
							}
							if (savedResultToShow != null) {
								Message message = Message.obtain(handler,
										R.id.decode_succeeded, savedResultToShow);
								handler.sendMessage(message);
							}
							savedResultToShow = null;
						}
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ChecksumException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			
//				new Thread(){
//					public void run() {
//						
//						try {
//							
//							URL url=new URL(result.toString());
//							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//							connection.connect();
//							int code= connection.getResponseCode();
//							if(code==200){
//								
//							}else{
//								System.out.println("返回码错误:"+code+"=="+result.toString());
//							}
//							System.out.println("结果吗:"+result.toString());
////							Toast.makeText(getApplicationContext(), "code"+decode, 0).show();
//						} catch (NotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (ChecksumException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (FormatException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (MalformedURLException e) {
//							e.printStackTrace();
//							//网页未找到
////							System.out.println("网页未找到:"+result.toString().trim());
////							Intent intent=new Intent(CaptureActivity.this,DecodeActivity.class);
////							intent.putExtra("decode", result.toString().trim());
////							startActivity(intent);
////							finish();
//							Message  msg=new Message();
//							msg.what=0;
//							msg.obj=result;
//							mHandler.sendMessage(msg);
//						} catch (IOException e) {
//							e.printStackTrace();
//							//IO异常
//							System.out.println("IO异常"+result.toString());
//						}
//					};
//				}.start();
//				break;
//			default:
//				break;
//			}
		}
	}
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), "扫描结果码为"+msg, 0).show();
				break;

			default:
				break;
			}
		};
	};
	public byte[] BitmapToArray(Bitmap bmp){
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
	    byte[] byteArray = stream.toByteArray();
	    return byteArray;
	}
	@Override
	protected void onResume() {
		super.onResume();
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();

		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// restartPreviewAfterDelay(0L);
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(
				this, rawResult);
		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			// drawResultPoints(barcode, rawResult);
			viewfinderView.drawResultBitmap(barcode);
		}
		String text = rawResult.getText();
		Toast.makeText(getApplicationContext(),"您的扫描结果为:"+ text, 0).show();
		if(text.contains("http")){
			Intent intent= new Intent();          
	        intent.setAction("android.intent.action.VIEW");      
	        Uri content_url = Uri.parse(text);     
	        intent.setData(content_url);    
	        startActivity(intent);  
		}else{
			Toast.makeText(getApplicationContext(),"您的扫描结果为:"+ text, 0).show();
		}
		Log.e(TAG, "result-->" + text);
	}
	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, viewfinderView,
						decodeFormats, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			Toast.makeText(this, R.string.camera_problem, Toast.LENGTH_SHORT)
					.show();
			finish();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			Toast.makeText(this, R.string.framwork_problem, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
}
