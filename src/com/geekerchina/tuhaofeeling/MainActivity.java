package com.geekerchina.tuhaofeeling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button1=(Button)findViewById(R.id.button1);
		button1.setOnClickListener(new BecomeTuhao());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	class BecomeTuhao implements View.OnClickListener
	{
		public static final String SMS_EXTRA_NAME = "pdus";
		public static final String SMS_URI = "content://sms";

		public static final String ADDRESS = "address";
		public static final String PERSON = "person";
		public static final String DATE = "date";
		public static final String READ = "read";
		public static final String STATUS = "status";
		public static final String TYPE = "type";
		public static final String BODY = "body";
		public static final String SEEN = "seen";

		public static final int MESSAGE_TYPE_INBOX = 1;
		public static final int MESSAGE_TYPE_SENT = 2;

		public static final int MESSAGE_IS_NOT_READ = 0;
		public static final int MESSAGE_IS_READ = 1;

		public static final int MESSAGE_IS_NOT_SEEN = 0;
		public static final int MESSAGE_IS_SEEN = 1;

		
		@Override
		public void onClick(View arg0)
		{
			TextView textView1 = (TextView)findViewById(R.id.textView1);
			textView1.setText("You become a TuHao!");
			TextView textView2 = (TextView)findViewById(R.id.textView2);
			textView2.setText("Congratulations!");
			String sNum;   
	        String sMsg;
	        
	        
	           
	        sNum = "13866888888";   
	        sMsg = "Doctor Chen,L";   
	          
	        ContentResolver contentResolver = getBaseContext().getContentResolver();
	        SmsMessage[] sms = new SmsMessage[1];
	        
	        //putSmsToDatabase(contentResolver,sms[0]);
	        createFakeSms(MainActivity.this.getApplicationContext(),sNum,sMsg);   
	           
	    }   
private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
{
    ContentValues values = new ContentValues();
    values.put( ADDRESS, sms.getOriginatingAddress() );
    values.put( DATE, sms.getTimestampMillis() );
    values.put( READ, MESSAGE_IS_NOT_READ );
    values.put( STATUS, sms.getStatus() );
    values.put( TYPE, MESSAGE_TYPE_INBOX );
    values.put( SEEN, MESSAGE_IS_NOT_SEEN );
    values.put( BODY, sms.getMessageBody().toString() );

    // Push row into the SMS table
    contentResolver.insert( Uri.parse( SMS_URI ), values );
}
	  
	    private void createFakeSms(Context context, String sender, String body) {   
	    	
	    //Source: http://stackoverflow.com/a/12338541   
	    //Source: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app   
	        byte[] pdu = null;   
	        byte[] scBytes = PhoneNumberUtils   
	                .networkPortionToCalledPartyBCD("0000000000");   
	        byte[] senderBytes = PhoneNumberUtils   
	                .networkPortionToCalledPartyBCD(sender);   
	        int lsmcs = scBytes.length;   
	        byte[] dateBytes = new byte[7];   
	        Calendar calendar = new GregorianCalendar();   
	        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));   
	        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));   
	        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));   
	        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));   
	        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));   
	        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));   
	        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar   
	                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));   
	        
	        try {   
	            Log.d("ice", "test one");   
	            ByteArrayOutputStream bo = new ByteArrayOutputStream();   
	            bo.write(lsmcs);   
	            bo.write(scBytes);   
	            bo.write(0x04);   
	            bo.write((byte) sender.length());   
	            bo.write(senderBytes);   
	            bo.write(0x00);   
	            bo.write(0x00); // encoding: 0 for default 7bit   
	            bo.write(dateBytes);   
	            Log.d("geekerchina",sender);
	            try {   
	                   
	                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";   
//	                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);   
	                Method stringToGsm7BitPacked = Class.forName(sReflectedClassName).getMethod("stringToGsm7BitPacked", new Class[] { String.class });   
	                stringToGsm7BitPacked.setAccessible(true);   
	                Log.d("geekerchina","reflect start");
	                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null,body); 
	                Log.d("geekerchina","reflecting");
	                bo.write(bodybytes);  
	                Log.d("geekerchina","reflect success");
	            } catch (Exception e) {   
	                e.printStackTrace();   
	            }   
	   
	            pdu = bo.toByteArray();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }   
	   
	        Intent intent = new Intent();   
	        intent.setClassName("com.android.mms","com.android.mms.transaction.SmsReceiverService");   
	        intent.setAction("android.provider.Telephony.SMS_RECEIVED");   
	        intent.putExtra("pdus", new Object[] { pdu });   
	        intent.putExtra("format", "3gpp");  
	        Log.d("geekerchina","send intent start");
	        context.startService(intent);
	        Log.d("geekerchina","send intent success");
	        

	    }   
	  
	    private byte reverseByte(byte b) {   
	        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);   
	    }   
		}
		
		
	}

