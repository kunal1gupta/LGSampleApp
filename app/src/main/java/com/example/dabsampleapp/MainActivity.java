package com.example.dabsampleapp;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.lge.broadcast.tdmb.Dmb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback{

	private View mFullScreen;
	private View mChlistScreen_Port;
	private View mBottomButtons;
	private Button mSearchButton;
	private ListView mChListView;
	private ImageView mSlsImageView;
	private TextView mDlsTextBottom;
	private FrameLayout mDlsTextCenterFrame;
	private TextView mDlsTextCenter;

	private SurfaceView mPreview;
	private SurfaceHolder mHolder;

	private LinearLayout mProgressLayout;
	private ProgressBar mProgressloading;
	private TextView mLoadingChannel;

	private Button mFicButton;
	private Button mDataGroupButton;

	public ArrayList<String> chList;
	public ArrayList<ChManagerDb> chMngrDbList;
	public int mPosition;

	public static Bitmap bitmap = null;
	public static String dlsStr = null;

	public ArrayAdapter<String> mAdapter;
	public static final int CHANNEL_SEARCH_DIALOG = 0;

	public static final String TAG = "DABSampleApp";

	private Context mContext;
	private AudioManager mAudioMgr;

	public static Dmb DmbManager = null;
	private final EventCallback mInitEventCallback = new EventCallback();
	private final SignalCallback mFindSignalCallback = new SignalCallback();
	private final DataCallback mDataCallback = new DataCallback();
	private final VideoCallback mVideoCallback = new VideoCallback();
	private final AudioCallback mAudioCallback = new AudioCallback();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.activity_main);

		mContext = this;
		mPosition = -1;

		mFullScreen = ((ViewStub)findViewById(R.id.tv_screen)).inflate();
		mChlistScreen_Port = ((ViewStub)findViewById(R.id.ch_list_port)).inflate();
		mBottomButtons = ((ViewStub)findViewById(R.id.bottom_buttons)).inflate();

		mSearchButton = (Button) findViewById(R.id.search_button);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(CHANNEL_SEARCH_DIALOG);
			}
		});

		mSlsImageView = (ImageView)mFullScreen.findViewById(R.id.sls_image);
		mDlsTextBottom = (TextView)mFullScreen.findViewById(R.id.dls_text_bottom);
		mDlsTextCenterFrame = (FrameLayout)mFullScreen.findViewById(R.id.only_dls_backgroud);
		mDlsTextCenter = (TextView)mFullScreen.findViewById(R.id.only_dls_text);

		// channel listview
		mChListView = (ListView)findViewById(R.id.dmbchannellist_chlistview);
		mChListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPosition = position;
				int opMode = chMngrDbList.get(position).GetOpMode();

				dlsStr = null;
				bitmap = null;
				setOptionalDataShow();

				if(opMode == Utils.OP_MODE_DAB) {
					setCurrentRadioChannel(position);
				}
				else {
					setCurrentTVChannel(position);
				}
			}
		});;

		// getFIC
		mFicButton = (Button) findViewById(R.id.fic_button);
		mFicButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mPosition == -1)
					return;

				dataDMB(Utils.DMB_FIC_RECEIVED, 1);
				ChManagerDb chMngrDb = chMngrDbList.get(mPosition);
				getFic(chMngrDb.GetFreqNo());
			}
		});

		// getDataGroup
		mDataGroupButton = (Button) findViewById(R.id.datagroup_button);
		mDataGroupButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mPosition == -1) {
					return;
				}

				dataDMB(Utils.DMB_PAD_DATAGROUP_RECEIVED, 1);
				dataDMB(Utils.DMB_PACKET_DATAGROUP_RECEIVED, 1);
				ChManagerDb chMngrDb = chMngrDbList.get(mPosition);
				selectDMB(chMngrDb.GetOpMode(), chMngrDb.GetFreqNo(), chMngrDb.GetSubchid(), chMngrDb.GetDSCTy(), chMngrDb.GetSubchSize(), chMngrDb.GetFecScheme());
				getDataType(1);
			}
		});

		// surfaceview
		mPreview = (SurfaceView)findViewById(R.id.surface);
		mHolder = mPreview.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mPreview.setVisibility(View.VISIBLE);

		// progress layout
		mProgressLayout = (LinearLayout)mFullScreen.findViewById(R.id.progress_layout);
		mProgressloading = (ProgressBar)mFullScreen.findViewById(R.id.progress_large);
		mLoadingChannel = (TextView)mFullScreen.findViewById(R.id.Text_loadingChannel);

		mAudioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		openDMB();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if(id == CHANNEL_SEARCH_DIALOG) {
			String[] list = new String[Utils.FREQ_BLOCK_EU.length];
			for(int i=0; i<Utils.FREQ_BLOCK_EU.length; i++){
				list[i] = String.valueOf(Utils.FREQ_BLOCK_EU[i]);
			}

			builder.setTitle(R.string.select_freq_block)
			.setItems(list, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "FreqBlock = " + Utils.FREQ_BLOCK_EU[which]);
					mPosition = -1;
					setModeChSearching(which);
				}
			});
		}
		return builder.create();
	}

	public void setModeChSearching(int freqIndex) {
		initDMB(Utils.OP_MODE_ENSQUERY, 1);
		findDMB(Utils.FREQ_BLOCK_EU[freqIndex]);
	}



	public void processChannelSignalReceived(int dataSize, byte[] data) {
		Log.d(TAG, "processChannelSignalReceived()");

		ChManagerDb ChMngrDb = new ChManagerDb();
		chList = new ArrayList<String>();
		chMngrDbList = new ArrayList<ChManagerDb>();
		int receiveChNum = dataSize / Utils.sCHDATA_SIZE;

		Log.d(TAG, "receiveChNum = " + receiveChNum);

		for (int i = 0; i < receiveChNum; i++) {
			byte[] destData = new byte[Utils.sCHDATA_SIZE];
			int srcPos = i * Utils.sCHDATA_SIZE;
			System.arraycopy(data, srcPos, destData, 0,Utils.sCHDATA_SIZE);
			ChMngrDb = ChManagerDb.getChDbFromData(destData);
			if (ChMngrDb.op_mode == Utils.OP_MODE_DAB
					|| ChMngrDb.op_mode == Utils.OP_MODE_DMB	
					|| ChMngrDb.op_mode == Utils.OP_MODE_VR
					|| ChMngrDb.op_mode == Utils.OP_MODE_DABP
					|| ChMngrDb.op_mode == Utils.OP_MODE_DATA) {
				chMngrDbList.add(ChMngrDb);
				chList.add(ChMngrDb.sl);
			}
		}
		for(int i=0; i<chList.size(); i++) {
			Log.d("TDMB channel list", chList.get(i));
		}
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chList);
		mChListView.setAdapter(mAdapter);
		mChListView.setVisibility(View.VISIBLE);

		dataDMB(Utils.DMB_CHANNEL_SIGNAL_RECEIVED, 0);
		exitDMB();
	}

	public void setCurrentRadioChannel(int position) {
		Log.d(TAG, "setCurrentRadioChannel position : " + position);

		ChManagerDb chMngrDb = chMngrDbList.get(position);
		Log.d(TAG, "setCurrentRadioChannel name : " + chList.get(position));

		initDMB(Utils.OP_MODE_DAB, 1);
		selectDMB(chMngrDb.GetOpMode(), chMngrDb.GetFreqNo(), chMngrDb.GetSubchid(), chMngrDb.GetDSCTy(), chMngrDb.GetSubchSize(), chMngrDb.GetFecScheme());
		dataDMB(Utils.DMB_DLS_RECEIVED, 1);
		dataDMB(Utils.DMB_SLS_RECEIVED, 1);
		
		if (mPreview != null) {
			mProgressLayout.setVisibility(View.VISIBLE);
			mProgressloading.setVisibility(View.VISIBLE);
			mLoadingChannel.setVisibility(View.VISIBLE);
			setDimensionDMB(mPreview.getLeft(), mPreview.getTop(), mPreview.getWidth(), mPreview.getHeight(), 90, mHolder);
		}
	}

	public void setCurrentTVChannel(int position) {
		Log.d(TAG, "setCurrentTVChannel position =" + position);

		ChManagerDb chMngrDb = chMngrDbList.get(position);

		initDMB(Utils.OP_MODE_DMB, 1);
		selectDMB(chMngrDb.GetOpMode(), chMngrDb.GetFreqNo(), chMngrDb.GetSubchid(), chMngrDb.GetDSCTy(), chMngrDb.GetSubchSize(), chMngrDb.GetFecScheme());

		if (mPreview != null) {
			mProgressLayout.setVisibility(View.VISIBLE);
			mProgressloading.setVisibility(View.VISIBLE);
			mLoadingChannel.setVisibility(View.VISIBLE);
			setDimensionDMB(mPreview.getLeft(), mPreview.getTop(), mPreview.getWidth(), mPreview.getHeight(), 90, mHolder);
		}
	}

	public void processSSTriggeringMode(byte[] mSlsRawData){
		Log.d(TAG, "processSSTriggeringMode()");
		bitmap = decodeSLS(mSlsRawData);
		setOptionalDataShow();
	}

	public void processDLSReceiveDone(byte[] data) {
		Log.d(TAG, "processDLSReceiveDone()");
		dlsStr = decodeDLS(data);
		setOptionalDataShow();
	}

	public void processServiceOK() {
		Log.d(TAG, "processServiceOK()");

		mProgressLayout.setVisibility(View.GONE);
		mProgressloading.setVisibility(View.GONE);
		mLoadingChannel.setVisibility(View.GONE);
	}	

	public void processErrorSetAutoChannel() {
		Log.i(TAG, "processErrorSetAutoChannel()");
		Toast.makeText(getApplicationContext(), "No channel has been scanned.", Toast.LENGTH_SHORT).show(); 
		if (chList != null && chList.size() > 0){
			chList.clear();
			mAdapter.notifyDataSetChanged();
			mProgressLayout.setVisibility(View.VISIBLE);
		}
	}

	public static Bitmap decodeSLS(byte[] data) {
		Log.d(TAG, "decodeSLS() called ");
		// IMG TYPE , IMG length
		if (data == null) {
			bitmap = null;
			return null;
		}
		ByteBuffer byteBuff = ByteBuffer.allocate(data.length);

		byteBuff.put(data);
		byteBuff.order(ByteOrder.LITTLE_ENDIAN);
		byteBuff.rewind();

		int imgSet = byteBuff.getInt();
		Log.d(TAG, "imgSet = " + imgSet);

		int imgLength = byteBuff.getInt();
		Log.d(TAG, "imgLength = " + imgLength);

		// SLS IMG DECODE
		byte[] destData = new byte[imgLength];
		System.arraycopy(data, Utils.SLS_DATA_POS, destData, 0, destData.length);

		bitmap = BitmapFactory.decodeByteArray(destData, 0, destData.length);

		return bitmap;
	}

	public static String decodeDLS(byte[] data) {
		Log.d(TAG, "decodeDLS()");
		if (data == null) {
			dlsStr = null;
			return null;
		}
		// CHAR SET (UNICODE OR KSC5601), string length
		ByteBuffer byteBuff = ByteBuffer.allocate(data.length);

		byteBuff.put(data);
		byteBuff.order(ByteOrder.LITTLE_ENDIAN);
		byteBuff.rewind();

		int charSet = byteBuff.getInt();
		Log.d(TAG, "charSet = " + charSet);

		int strLength = byteBuff.getInt();
		Log.d(TAG, "strLength = " + strLength);

		// DLS STRING 
		byte[] destData = new byte[strLength];
		System.arraycopy(data, Utils.DLS_DATA_POS, destData, 0, destData.length);
		try {
			if (charSet == Utils.KSX1001_WANSUNG) {
				dlsStr = new String(destData, 0, strLength, Utils.KSC5601);
			} else if (charSet == Utils.KSX1005_1_UNICODE
					|| charSet == Utils.ISO_8859) {

				dlsStr = new String(destData, 0, strLength, "ISO-8859-1");

			} else if (charSet == Utils.EBU_LATIN) {
				dlsStr = "";
				String byteArr = "";
				for (byte temp : destData) {
					byteArr = byteArr + " " + String.format("%02x", temp);
					dlsStr = dlsStr + EBUChar.EBU_SET[EBUChar.getRow(temp)][EBUChar.getCel(temp)];
				}
			} else if (charSet == Utils.UTF8) {
				dlsStr = new String(destData, 0, strLength, "UTF8");
			} 


		} catch (java.io.UnsupportedEncodingException e) {
			dlsStr = new String("unsupported DLS string");
			e.printStackTrace();
		}
		Log.d(TAG, "dls = " + dlsStr);
		return dlsStr;
	}

	protected void setOptionalDataShow() {
		Log.d(TAG, "setOptionalDataShow");

		mProgressLayout.setVisibility(View.GONE);
		mProgressloading.setVisibility(View.GONE);
		mLoadingChannel.setVisibility(View.GONE);

		if (bitmap != null) {
			mSlsImageView.setImageBitmap(bitmap);
			mSlsImageView.setVisibility(View.VISIBLE);
			if (dlsStr != null) {
				mDlsTextBottom.setText(dlsStr);
				mDlsTextBottom.setVisibility(View.VISIBLE);
				mDlsTextCenterFrame.setVisibility(View.GONE);
			}
		} else {
			if (dlsStr != null) {
				mDlsTextCenterFrame.setVisibility(View.VISIBLE);
				mDlsTextCenter.setVisibility(View.VISIBLE);
				mDlsTextCenter.setText(dlsStr);
				mDlsTextBottom.setVisibility(View.GONE);
			} else {
				mSlsImageView.setVisibility(View.GONE);
				mDlsTextCenter.setVisibility(View.GONE);
				mDlsTextCenterFrame.setVisibility(View.GONE);
				mDlsTextBottom.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");
		if (mPreview != null) {
			setDimensionDMB(mPreview.getLeft(), mPreview.getTop(), mPreview.getWidth(), mPreview.getHeight(), 90, mHolder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged()");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed()");
		exitDMB();
		releaseDMB();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setDMBVolume(0);
		exitDMB();
		releaseDMB();
	}

	private final class EventCallback implements com.lge.broadcast.tdmb.Dmb.EventCallback {
		public void onEvent(int event, Dmb dmb) {
			Log.i(TAG, "EventCallback, event = " + event);
			switch (event) {
			case Utils.DMB_ERROR_SET_AUTO_SEARCHING:
				Log.i(TAG, "DMB_ERROR_SET_AUTO_SEARCHING");
				processErrorSetAutoChannel();
				break;
			case Utils.DMB_SRV_OK:
				Log.i(TAG, "DMB_SRV_OK");
				processServiceOK();
				break;
			case Utils.DMB_ERROR_SET_CHANNEL:
				Log.i(TAG, "DMB_ERROR_SET_CHANNEL");
			default:
				break;
			}
		}
	}


	private final class SignalCallback implements com.lge.broadcast.tdmb.Dmb.SignalCallback {
		public void onSignal(int lock, Dmb dmb) {
			Log.v(TAG, "find->SignalCallback has received, lock = " + lock);
			if (lock == Utils.DMB_SIG_LOCK) {
				Log.v(TAG, "TDMB_EVENT_SET_AUTOCH_OK");
				dataDMB(Utils.DMB_CHANNEL_SIGNAL_RECEIVED, 1);
			} else {
				Log.v(TAG, "TDMB_EVENT_ERR_SET_AUTOCH");
				processErrorSetAutoChannel();
			}
		}
	}

	private final class DataCallback implements com.lge.broadcast.tdmb.Dmb.DataCallback {
		public void onData(int type, int size, byte[] data, Dmb mDmb) {

			if (type == Utils.DMB_CHANNEL_SIGNAL_RECEIVED) {
				Log.d(TAG, "DMB_CHANNEL_SIGNAL_RECEIVED");
				Toast.makeText(getApplicationContext(), "channels found", Toast.LENGTH_SHORT).show();
				processChannelSignalReceived(size, data);
			} 
			else if (type == Utils.DMB_DLS_RECEIVED) {
				Log.d(TAG, "DMB_DLS_RECEIVED");
				processDLSReceiveDone(data);
			} else if (type == Utils.DMB_SLS_RECEIVED) {
				Log.d(TAG, "DMB_SLS_RECEIVED");
				processSSTriggeringMode(data);
			}
			else if (type == Utils.DMB_FIC_RECEIVED) {
				Log.d(TAG, "FIC length = " + data.length);
			}
			else if (type == Utils.DMB_PAD_DATAGROUP_RECEIVED) {
				Log.d(TAG, "PAD DataGroup length = " + data.length);
			}
			else if (type == Utils.DMB_PACKET_DATAGROUP_RECEIVED) {
				Log.d(TAG, "PACKET DataGroup length = " + data.length);
			}
		}
	}

	private final class VideoCallback implements com.lge.broadcast.tdmb.Dmb.VideoCallback {
		public void onVideo(int idr, int frames, Dmb mDmb) {
			Log.v(TAG, "VideoCallback has received, event = ");
		}
	}

	private final class AudioCallback implements com.lge.broadcast.tdmb.Dmb.AudioCallback {
		public void onAudio(int sf, int frames, Dmb mDmb) {
			Log.v(TAG, "AudioCallback has received, event = ");
		}
	}

	public void openDMB() {
		Log.d(TAG, "requestAudioFocus");
		mAudioMgr.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {

			@Override
			public void onAudioFocusChange(int focusChange) {
				if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
					Log.d(TAG, "AudioFocus: received AUDIOFOCUS_GAIN");
					setDMBVolume(1);
				}
			}
		},
		AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		Log.d(TAG, "openDMB");
		DmbManager = Dmb.open();
	}

	public void initDMB(int opMode, int liveFlag) {
		
		Log.d(TAG, "initDMB");
		if (DmbManager != null) {
			if (opMode == Utils.OP_MODE_ENSQUERY) {
				DmbManager.exit();
			}
			DmbManager.init(opMode, liveFlag, 1, mInitEventCallback);
		}
	}

	public void findDMB(int freq) {
		Log.d(TAG, "findDMB, freq : " + freq);
		if (DmbManager != null) {
			DmbManager.find(freq, mFindSignalCallback);
		}
	}

	public void dataDMB(int dataType, int onOff) {
		Log.d(TAG, "dataDMB() has called..dataType = " + dataType + ", onOff : " + onOff);
		if (DmbManager != null) {
			DmbManager.data(dataType, onOff, mDataCallback);
		}
	}

	public void selectDMB(int opmode, int freq, int subChId, int dscty, int subChSize, int fec_scheme) {
		Log.d(TAG, "selectDMB() " + opmode + " " + freq + " " + subChId + " " + dscty + " "
				+ subChSize);
		if (DmbManager != null) {
			if (dscty == -1) {
				DmbManager.select(opmode, freq, subChId, mVideoCallback, mAudioCallback);
			} else {
				DmbManager.select(opmode, freq, subChId, dscty, subChSize, fec_scheme, mVideoCallback,
						mAudioCallback);
			}
		}
	}

	public void setDimensionDMB(int left, int top, int width, int height,
			int degree, SurfaceHolder holder) {
		if (DmbManager != null) {
			Log.d(TAG, "setDimensionDMB() has called");
			DmbManager.setVideoDimension(left, top, width, height, degree, holder);
		}
	}

	public void releaseDMB() {
		Log.d(TAG, "releaseDMB()");
		if (DmbManager != null) {
			DmbManager.release();
		}
		DmbManager = null;
	}

	public void exitDMB() {
		Log.d(TAG, "exitDMB()");
		if (DmbManager != null) {
			DmbManager.exit();
		}
	}

	public void setDMBVolume(int volume) {
		Log.d(TAG, "DMB setAudioVolume = " + volume);
		if (DmbManager != null) {
			DmbManager.setAudio(volume, 0);
		}
	}

	public void getFic(int freq) {
		Log.d(TAG, "getFic(), freq : " + freq);
		if (DmbManager != null) {
			DmbManager.getFic(freq);
		}
	}

	public void getDataType(int type) {
		if (DmbManager != null) {
			DmbManager.getDataGroup(type);
		}
	}
}
