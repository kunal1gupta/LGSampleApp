package com.example.dabsampleapp;

public class Utils {

	// frequency block
	public static final int FREQ_BLOCK_EU[] = { 136, 135, 134, 133, 132, 131, 129, 124, 123, 122,
		121, 119, 114, 113, 112, 111, 109, 104, 103, 102, 101, 94, 93, 92, 91, 84, 83, 82, 81,
		74, 73, 72, 71, 64, 63, 62, 61, 54, 53, 52, 51 };
	
    /*
     * msg type in signalCallback
     */
    public static final int DMB_SIG_LOCK = 0x0101;
    public static final int DMB_SIG_UNLOCK = 0x0102;

    /*
     * msg type in normal eventCallback
     */
    public static final int DMB_SRV_OK = 0x0801;
    public static final int DMB_SRV_INFO_UPDATE = 0x0811;
    public static final int DMB_PLAY_READY_SRV = 0x0812;

    /*
     * msg type in error eventCallback
     * must be sync DmbService.h
     */
    public static final int DMB_ERROR_SET_CHANNEL = 0x0C01;
    public static final int DMB_ERROR_SET_AUTO_SEARCHING = 0x0C0d;
    public static final int DMB_ERROR_RECEVIE_CHANNEL_SI = 0x0C0e;

    /*
     * msg type in dataCallback
     */
    public static final int DMB_DLS_RECEIVED = 0x1001;
    public static final int DMB_SLS_RECEIVED = 0x1002;
    public static final int DMB_CHANNEL_SIGNAL_RECEIVED = 0x1004;
    public static final int DMB_FIC_RECEIVED = 0x1040;
    public static final int DMB_PAD_DATAGROUP_RECEIVED = 0x1080;
    public static final int DMB_PACKET_DATAGROUP_RECEIVED = 0x1100;

    //OP MODE
    public static final int OP_MODE_DAB = 1;
    public static final int OP_MODE_DMB = 2;
    public static final int OP_MODE_VR = 3;
   	public static final int OP_MODE_DATA = 4;
   	public static final int OP_MODE_TPEG = 5;
    public static final int OP_MODE_DABP = 10;
    public static final int OP_MODE_ENSQUERY = 6;


    //TDMB_SRV_CHAR_SET
    public static final int KSX1005_1_UNICODE = 0x04;
    public static final int KSX1001_WANSUNG = 0x06;
    public static final int EBU_LATIN = 0x00;
    public static final int ISO_8859 = 0x04;
    public static final int UCS2 = 0x06;
    public static final int UTF8 = 0x0f;
    
    // CHDATA
	public static int sCHDATA_SIZE = 172;

	public static final int DLS_DATA_POS = 8;
	public static final int SLS_DATA_POS = 8;
	public static final String KSC5601 = "KSC5601";
}
