package com.example.dabsampleapp;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.util.Log;

public class ChManagerDb {
	private static final String TAG = "DABSampleApp";
	private static final int CHDATA_STR_SIZE = 20;

	public int op_mode; // Operation mode 1 : DAB, 2 : DMB
	public int history; // Previous Channel Flag
	public int preset; // Preset Number : Sorting Key
	public int freq_no; // Frequence Block No. (Ensemble Id) : Sorting Key
	public int eid;
	public int sid; // Service Id : Sorting Key
	public int subchid; // SubChannel Id
	public int s_pd; // Service Type (Program Service / Data Service )
	public int s_ps; // Primary / Secondary Service
	public int s_tmid; // Tmid ex) Stream Audio, Stream Data
	public int ch_num_ui; // Channel Number ( in UI )
	public String el; // Ensemble Label
	public String sl; // Service Label
	public String sc; // Service component Label
	public int el_flag; // Ensemble Label charater field flag
	public int sl_flag; // Service Label chararter field flag
	public int scl_flag; // Service Component Label chararter field flag
	public int scid; // service componet id 12bits
	public int DG_flag; // Data_Group flag
	public int DSCTy; // data service component type
	public int userapptype; // user application type 3:BWS, 4:TPEG
	public int pack_addr;
	public int storeType; // 0 : SDRAM, 1 : FLASH
	public int sync_pid; // ui and system sync for channel db
	public int start_addr; // the first Capacity Unit (CU) of the sub-channel
	public int table_index; // an index
	public int option; // two options
	public int protection_level; // protection level
	public int subch_size; // subchannel size
	public int fec_scheme; // forward error correction

	public ChManagerDb() {

	}

	public ChManagerDb(int op_mode, int history, int preset, int freq_no,
			int eid, int sid, int subchid, int s_pd, int s_ps, int s_tmid,
			int ch_num_ui, String el, String sl, String sc, int el_flag,
			int sl_flag, int scl_flag, int scid, int DG_flag, int DSCTy,
			int userapptype, int pack_addr, int storeType, int sync_pid,
			int start_addr, int table_index, int option, int protection_level,
			int subch_size, int fec_scheme) {
		this.op_mode = op_mode;
		this.history = history;
		this.preset = preset;
		this.freq_no = freq_no;
		this.eid = eid;
		this.sid = sid;
		this.subchid = subchid;
		this.s_pd = s_pd;
		this.s_ps = s_ps;
		this.s_tmid = s_tmid;
		this.ch_num_ui = ch_num_ui;
		this.el = el;
		this.sl = sl;
		this.sc = sc;
		this.el_flag = el_flag;
		this.sl_flag = sl_flag;
		this.scl_flag = scl_flag;
		this.scid = scid;
		this.DG_flag = DG_flag;
		this.DSCTy = DSCTy;
		this.userapptype = userapptype;
		this.pack_addr = pack_addr;
		this.storeType = storeType;
		this.sync_pid = sync_pid;
		this.start_addr = start_addr;
		this.table_index = table_index;
		this.option = option;
		this.protection_level = protection_level;
		this.subch_size = subch_size;
		this.fec_scheme = fec_scheme;
	}

	// get
	public int GetOpMode() {
		return op_mode;
	}

	public int GetHistory() {
		return history;
	}

	public int GetPreset() {
		return preset;
	}

	public int GetFreqNo() {
		return freq_no;
	}

	public int GetEid() {
		return eid;
	}

	public int GetSid() {
		return sid;
	}

	public int GetSubchid() {
		return subchid;
	}

	public int GetSpd() {
		return s_pd;
	}

	public int GetSps() {
		return s_ps;
	}

	public int GetStmid() {
		return s_tmid;
	}

	public int GetChNumUi() {
		return ch_num_ui;
	}

	public String GetEl() {
		return el;
	}

	public String GetSl() {
		return sl;
	}

	public String GetSc() {
		return sc;
	}

	public int GetElFlag() {
		return el_flag;
	}

	public int GetSlFlag() {
		return sl_flag;
	}

	public int GetSclFlag() {
		return scl_flag;
	}

	public int GetScid() {
		return scid;
	}

	public int GetDgFlag() {
		return DG_flag;
	}

	public int GetDSCTy() {
		return DSCTy;
	}

	public int GetUserapptype() {
		return userapptype;
	}

	public int GetPackAddr() {
		return pack_addr;
	}

	public int GetStoreType() {
		return storeType;
	}

	public int GetSyncPid() {
		return sync_pid;
	}

	public int GetStartAddr() {
		return start_addr;
	}

	public int GetTableIndex() {
		return table_index;
	}

	public int GetOption() {
		return option;
	}

	public int GetProtectionLevel() {
		return protection_level;
	}

	public int GetSubchSize() {
		return subch_size;
	}

	public int GetFecScheme() {
		return fec_scheme;
	}

	public static ChManagerDb getChDbFromData(byte[] data) {
		ChManagerDb ChMngrDb = new ChManagerDb();

		Log.d(TAG, "ChMngrDb data length = " + data.length);

		ByteBuffer byteBuff = ByteBuffer.allocate(data.length);

		byte[] elByteArray = new byte[CHDATA_STR_SIZE];
		byte[] slByteArray = new byte[CHDATA_STR_SIZE];
		byte[] scByteArray = new byte[CHDATA_STR_SIZE];

		byteBuff.put(data);
		byteBuff.order(ByteOrder.LITTLE_ENDIAN);
		byteBuff.rewind();

		ChMngrDb.op_mode = byteBuff.getInt(); //1
		Log.d(TAG, "ChMngrDb.op_mode = " + ChMngrDb.op_mode);

		ChMngrDb.history = byteBuff.getInt(); //2

		ChMngrDb.preset = byteBuff.getInt(); //3

		ChMngrDb.freq_no = byteBuff.getInt();//4
		Log.d(TAG, "ChMngrDb.freq_no = " + ChMngrDb.freq_no);

		ChMngrDb.eid = byteBuff.getInt();//5

		ChMngrDb.sid = byteBuff.getInt();//6

		ChMngrDb.subchid = byteBuff.getInt();//7
		Log.d(TAG, "ChMngrDb.subchid = " + ChMngrDb.subchid);

		ChMngrDb.s_pd = byteBuff.getInt();//8

		ChMngrDb.s_ps = byteBuff.getInt();//9

		ChMngrDb.s_tmid = byteBuff.getInt();//10

		ChMngrDb.ch_num_ui = byteBuff.getInt();//11

		ChMngrDb.el_flag = byteBuff.getInt();//12

		ChMngrDb.sl_flag = byteBuff.getInt();//13

		ChMngrDb.scl_flag = byteBuff.getInt();//14

		ChMngrDb.scid = byteBuff.getInt();//15

		ChMngrDb.DG_flag = byteBuff.getInt();//16

		ChMngrDb.DSCTy = byteBuff.getInt();//17

		ChMngrDb.userapptype = byteBuff.getInt();//18

		ChMngrDb.pack_addr = byteBuff.getInt();//19

		ChMngrDb.storeType = byteBuff.getInt();//20

		ChMngrDb.sync_pid = byteBuff.getInt();//21

		ChMngrDb.start_addr = byteBuff.getInt();//22

		ChMngrDb.table_index = byteBuff.getInt();//23

		ChMngrDb.option = byteBuff.getInt();//24

		ChMngrDb.protection_level = byteBuff.getInt();//25

		ChMngrDb.subch_size = byteBuff.getInt();//26

		elByteArray = getTempByteArray(byteBuff);
		slByteArray = getTempByteArray(byteBuff);
		scByteArray = getTempByteArray(byteBuff);
		int chSet = byteBuff.getInt();

		Log.d(TAG, "ChMngrDb chSet = " + chSet);

		ChMngrDb.fec_scheme = byteBuff.getInt();
		Log.d(TAG, "ChMngrDb fec_scheme = " + ChMngrDb.fec_scheme);

		ChMngrDb.el = getStringOfData(chSet, elByteArray);
		Log.d(TAG, "ChMngrDb.el = " + ChMngrDb.el);

		ChMngrDb.sl = getStringOfData(chSet, slByteArray);
		Log.d(TAG, "ChMngrDb.sl = " + ChMngrDb.sl);

		ChMngrDb.sc = getStringOfData(chSet, scByteArray);

		return ChMngrDb;
	}

	public static byte[] getTempByteArray(ByteBuffer byteBuff) {
		int i = 0;

		byte[] byteArray = new byte[CHDATA_STR_SIZE];

		for (i = 0; i < CHDATA_STR_SIZE; i++) {
			byteArray[i] = byteBuff.get();
		}
		return byteArray;
	}

	public static char[] byteToCharArray(byte[] data) {
		char[] chars = new char[data.length / 2];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char)(((data[(i * 2)] & 0xff) << 8) + (data[(i * 2 + 1)] & 0xff));
		}
		return chars;
	}

	public static String getStringOfData(int chSet, byte[] byteArray) {
		char[] charArray = new char[CHDATA_STR_SIZE / 2];

		String resultStr = "";

			if (chSet == Utils.EBU_LATIN) {
				String byteArr = "";
				for (byte temp : byteArray) {
					byteArr = byteArr + String.format("%02x", temp) + " ";
					resultStr = resultStr
							+ EBUChar.EBU_SET[EBUChar.getRow(temp)][EBUChar.getCel(temp)];
				}

				resultStr = resultStr.trim();

			} else if (chSet == Utils.UCS2) {
				charArray = byteToCharArray(byteArray);
				resultStr = new String(charArray).trim();
			} else if (chSet == Utils.UTF8) {
				String byteArr = "";
				for (byte temp : byteArray) {
					byteArr = byteArr + String.format("%02x", temp) + " ";
				}

				try {
					resultStr = new String(byteArray, "UTF8").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		return resultStr;
	}
}
