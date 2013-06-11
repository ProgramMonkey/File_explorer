<<<<<<< HEAD
package com.explorer;

import static com.mime.MIME.MIME_MapTable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.file.R;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private TextView currentDir;// ç”¨äºæ˜¾ç¤ºæ–‡ä»¶å¤¹çš„é¢ç›®å½•ï¼Œè½½å…¥ç›¸åº”çš„æ–‡ä»¶å¤¹ï¼Œä¸ºæ–°å»ºçš„æ–‡ä»¶å¤¹å»ºç«‹ç›¸åº”çš„ç›®å½•
	private Button btnC;// æ‰‹æœºå†…å­˜ ç”¨äºå®šä½æ ¹ç›®å½•
	private Button btnE;// å­˜å‚¨å¡ ç”¨äºå®šä½æ ¹ç›®å½•
	private ListView listView; // æ˜¾ç¤ºç›¸åº”æŒ‰æ–‡ä»¶å¤¹
	private File rootDir; // æ ¹ç›®å½•æ–‡ä»¶å¤¹
	private File copyPath; // å½“æ‰§è¡Œå¤åˆ¶ã€ç²˜è´´ç­‰å·¥ä½œæ—¶ï¼Œå°†åŸåœ°å€å­˜å‚¨åˆ°copyPathä¸­è®°å½•
	private String flag;// ç”¨äºè®°å½•æ‰§è¡Œçš„æ“ä½œï¼ŒåŒ…æ‹¬å¤åˆ¶ã€å‰ªåˆ‡ç­‰
	private String startFilePath;// å½“æ‰§è¡Œç²˜è´´ç­‰å·¥ä½œæ—¶ï¼Œè®°å½•åŸåœ°å€
	private String desFilePath;// å½“æ‰§è¡Œç²˜è´´ç­‰å·¥ä½œæ—¶è®°å½•ç›®çš„åœ°å€
	private ProgressDialog progressDialog;// ç”¨äºå¤åˆ¶æ—¶çš„è¿›åº¦æ¡
	private int currentLen = 0;// ç”¨äºè®°å½•å¤åˆ¶å½“å‰çš„æ–‡ä»¶ï¼ˆå¤¹ï¼‰æ•°
	private long totaLength = 0;// ç”¨äºè®°å½•æ€»å…±è¦å¤åˆ¶çš„æ–‡ä»¶ï¼ˆå¤¹ï¼‰æ•°
	private Handler messageHandler; // ä¸»è¦æ¥å—å­çº¿ç¨‹å‘é€çš„æ•°æ®, å¹¶ç”¨æ­¤æ•°æ®é…åˆä¸»çº¿ç¨‹æ›´æ–°UI.
	private static String Name = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(Name, "level1 onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentDir = (TextView) findViewById(R.id.currentDir);
		// fileName = (TextView) findViewById(R.id.name);
		btnC = (Button) findViewById(R.id.btnC);// æ‰‹æœºå†…å­˜æŒ‰é’®çš„å£°æ˜
		btnE = (Button) findViewById(R.id.btnE);// å­˜å‚¨å¡æŒ‰é’®çš„å£°æ˜
		btnC.setOnClickListener(this);// ä¸ºæ‰‹æœºå†…å­˜æŒ‰é’®è®¾ç½®ç›‘å¬å™¨
		btnE.setOnClickListener(this);// ä¸ºå­˜å‚¨å¡æŒ‰é’®è®¾ç½®ç›‘å¬å™¨
		listView = (ListView) findViewById(R.id.listView);// æ–‡ä»¶åˆ—è¡¨çš„æ™Ÿæ•
		listView.setOnItemClickListener(this);// ä¸ºæ¯ä¸ªæ–‡ä»¶ï¼ˆå¤¹ï¼‰è®¾ç½®çŸ­æŒ‰çš„ç›‘å¬å™¨
		listView.setOnItemLongClickListener(this);// ä¸ºæ¯ä¸ªæ–‡ä»¶ï¼ˆå¤¹ï¼‰è®¾ç½®é•¿æŒ‰çš„ç›‘å¬å™¨
		// å¾—åˆ°å½“å‰çº¿ç¨‹çš„Looperå®ä¾‹ï¼Œç”±äºå½“å‰çº¿ç¨‹æ˜¯UIçº¿ç¨‹ä¹Ÿå¯ä»¥é€šè¿‡Looper.getMainLooper()å¾—åˆ°
		messageHandler = new MessageHandler(Looper.myLooper());

		// è®¾ç½®æ ¹ç›®å½•

		if (Environment.getExternalStorageState().equals(//åˆ¤æ–­æ˜¯å¦æŒ‚è½½SDå¡
				Environment.MEDIA_MOUNTED)) {
			Log.v(Name,
					"level2 Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getExternalStorageDirectory();
		} else {
			Log.v(Name,
					"level2 !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getRootDirectory();
		}
		loadFiles(rootDir);
	}

	// è‡ªå®šä¹‰Handler
	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.v(Name, "test initial rootdir"
					+ currentDir.getText().toString());
			loadFiles(new File(currentDir.getText().toString()));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		Log.v(Name, "level1 onKeyDown");
		// TODO Auto-generated method stub
		Map<String, Object> map = (Map<String, Object>) this.listView.getItemAtPosition(0);
		final File file = (File) map.get("file");
		final String str = (String)map.get("name");
		//å¦‚æœä¸æ˜¯æ ¹ç›®å½•ï¼Œåˆ™è¿”å›ä¸Šä¸€å±‚ç›®å½•
		if(str.equals("ä¸Šä¸€çº§ç›®å½•"))
		{
			Log.v(Name, "level2 notrootDir");
			loadFiles(file);
			return true;
		}
		else//å¦‚æœä¸ºæ ¹ç›®å½•ï¼Œåˆ™é€€å‡º
		{
			Log.v(Name, "level2 isrootDir");
			return super.onKeyDown(keyCode, event);

		} 
	}



	@Override
	//åˆå§‹åŒ–èœå•ï¼Œåªä¼šåœ¨ç¬¬ä¸€æ¬¡åˆå§‹åŒ–èœå•æ—¶è°ƒç”¨
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Name, "level1 onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Name, "level1 onOptionsItemSelected");

		// å¦‚æœé€‰æ‹©çš„æ˜¯æ–°å»ºæ–‡ä»¶å¤¹é€‰é¡¹
		if (item.getItemId() == R.id.newFile) {
			Log.v(Name, "level2 item.getItemId() == R.id.newFile");
			LayoutInflater factory = LayoutInflater.from(MainActivity.this);
			final View view = factory.inflate(R.layout.rename, null);
			AlertDialog d = new AlertDialog.Builder(MainActivity.this)
					.setCancelable(true)
					.setMessage("æ–‡ä»¶å¤¹å")
					.setView(view)
					.setPositiveButton("ç¡®å®š",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Log.v(Name, "level3 onClick");
									String dirName = ((EditText) view
											.findViewById(R.id.rename))
											.getText().toString();
									String newFile = currentDir.getText()
											.toString() + "/" + dirName;
									if (new File(newFile).exists()) {
										Log.v(Name,
												"level 4 File(newFile).exits");
										Toast.makeText(MainActivity.this,
												"æ–‡ä»¶å¤¹å·²å­˜åœ¨", Toast.LENGTH_LONG)
												.show();
										return;
									}
									File f = new File(currentDir.getText()
											.toString(), dirName);
									f.mkdir();
									loadFiles(new File(currentDir.getText().toString()));
								}
							}).create();
			d.show();
		} else if (item.getItemId() == R.id.about) {
			Log.v(Name, "level2 item.getItemId() == R.id.about");
			Dialog d = new AlertDialog.Builder(MainActivity.this)
					.setTitle("æ–‡ä»¶æµè§ˆå™¨1.0beta").setMessage("æœ¬ç¨‹åºç”±ææ´ªç¥¥ èµµå²©åˆ¶ä½œ")
					.setPositiveButton("ç¡®å®š", null).create();
			d.show();
		} else if (item.getItemId() == R.id.exit) {
			Log.v(Name, "level2 item.getItemId() == R.id.exit");
			MainActivity.this.finish();
		}
		return true;
	}

	/**
	 * åŠ è½½å½“å‰æ–‡ä»¶å¤¹åˆ—è¡¨
	 * 
	 */
	public void loadFiles(File dir) {
		Log.v(Name, "level1 loadFiles");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// å£°æ˜ä¸€ä¸ªMapæ•°ç»„ï¼Œç”¨æ¥å­˜å‚¨æ–‡ä»¶ï¼ˆå¤¹ï¼‰çš„æ˜¾ç¤ºä¿¡æ¯

		// å¦‚æœç›®å½•ä¸ä¸ºç©ºçš„åŒ–ï¼Œåˆ™æ˜¾ç¤ºç›¸åº”çš„æ–‡ä»¶ï¼ˆå¤¹ï¼‰ä¿¡æ¯
		if (dir != null) {
			Log.v(Name, "level2 dir!= NULL");
			// å¦‚æœä¸æ˜¯æ ¹ç›®å½•çš„è¯ï¼Œåˆ™ä¸º ä¸Šçº§ç›®å½•åœ¨ListViewçš„æœ€ä¸Šæ–¹ç•™ä¸€ä¸ªæ¥å£
			if (!dir.getAbsolutePath().equals(rootDir.getAbsolutePath())) {
				Log.v(Name,
						"level3 !dir.getAbsolutePath().equals(rootDir.getAbsolutePath())");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("file", dir.getParentFile());
				map.put("name", "ä¸Šä¸€çº§ç›®å½•");
				map.put("img", R.drawable.folder);
				list.add(map);
			}
			// è®¾ç½®æ˜¾ç¤ºç›®å½•
			currentDir.setText(dir.getAbsolutePath());
			File[] files = dir.listFiles();
			sortFiles(files);

			// ä¸ºæ¯è·Ÿæ–‡ä»¶ï¼ˆå¤¹ï¼‰çš„æ˜¾ç¤ºåšå‡†å¤‡ï¼Œå…ˆå°†ä¿¡æ¯å­˜å‚¨èµ·æ¥

			if (files != null) {
				Log.v(Name, "level2 files!=NULL");
				for (File f : files) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("file", f);
					map.put("name", f.getName());
					map.put("img",
							f.isDirectory() ? R.drawable.folder
									: (f.getName().toLowerCase()
											.endsWith(".zip") ? R.drawable.zip
											: R.drawable.text));
					list.add(map);
				}
			}

		} else {// å¦‚æœç›®å½•ä¸å­˜åœ¨åˆ™æç¤ºé”™è¯¯
			Log.v(Name, "level2 Files == NULL");
			Toast.makeText(this, "ç›®å½•ä¸æ­£ç¡®ï¼Œè¯·è¾“å…¥æ­£ç¡®çš„ç›®å½•!", Toast.LENGTH_LONG).show();
		}
		// æ˜¾ç¤ºæ–‡ä»¶ï¼ˆå¤¹ï¼‰ä¿¡æ¯
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.item,
				new String[] { "name", "img" }, new int[] { R.id.name,
						R.id.icon });
		// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
	}

	/**
	 * æ’åºæ–‡ä»¶åˆ—è¡¨
	 * 
	 */
	private void sortFiles(File[] files) {
		Log.v(Name, "level1 sortFiles");
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && file2.isDirectory()) {
					Log.v(Name,
							"level2 file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				if (file2.isDirectory()) {
					Log.v(Name,
							"level2 !file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				return -1;
			}
		});
	}

	/**
	 * æ‰“å¼€æ–‡ä»¶
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		Log.v(Name, "level1 openFile");
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// è®¾ç½®intentçš„Actionå±æ€§
		intent.setAction(Intent.ACTION_VIEW);
		// è·å–æ–‡ä»¶fileçš„MIMEç±»å‹
		String type = getMIMEType(file);
		// è®¾ç½®intentçš„dataå’ŒTypeå±æ€§ã€‚
		intent.setDataAndType(Uri.fromFile(file), type);
		// è·³è½¬
		startActivity(intent);

	}

	/**
	 * æ ¹æ®æ–‡ä»¶åç¼€åè·å¾—å¯¹åº”çš„MIMEç±»å‹ã€‚
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		Log.v(Name, "level1 getMIMEType");
		String type = "*/*";
		String fName = file.getName();
		// è·å–åç¼€åå‰çš„åˆ†éš”ç¬¦"."åœ¨fNameä¸­çš„ä½ç½®ã€‚
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			Log.v(Name, "level2 dotIndex<0");
			return type;
		}
		/* è·å–æ–‡ä»¶çš„åç¼€å */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "") {
			Log.v(Name, "level2 end ==  ");
			return type;
		}
		// åœ¨MIMEå’Œæ–‡ä»¶ç±»å‹çš„åŒ¹é…è¡¨ä¸­æ‰¾åˆ°å¯¹åº”çš„MIMEç±»å‹ã€‚
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	@Override
	public void onClick(View v) {
		Log.v(Name, "level1 onClick");
		if (v.getId() == R.id.btnC) {
			rootDir = Environment.getRootDirectory();
			loadFiles(rootDir);
		} else if (v.getId() == R.id.btnE) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				rootDir = Environment.getExternalStorageDirectory();
				loadFiles(rootDir);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.v(Name, "level1 onItemClick");
		// è·å–æ–‡ä»¶ï¼ˆå¤¹ï¼‰çš„å¼•ç”¨
		Map<String, Object> map = (Map<String, Object>) parent
				.getItemAtPosition(position);
		final File file = (File) map.get("file");
		// å¦‚æœæ˜¯æ–‡ä»¶å¤¹çš„è¯ï¼Œåˆ™è¿›å…¥æ­¤æ–‡ä»¶å¤¹ï¼Œæ˜¾ç¤ºæ­¤æ–‡ä»¶å¤¹çš„å†…å®¹
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			try {
				loadFiles(file);
			} catch (Exception e) {// è‹¥é‡åˆ°æƒé™ä¸è¶³çš„æƒ…å†µï¼Œåˆ™å¼¹å‡ºè­¦å‘Š
				Toast.makeText(this, "æƒé™ä¸è¶³", Toast.LENGTH_SHORT).show();
			}
		} else {// å¦‚è¿‡æ˜¯æ–‡ä»¶ï¼Œåˆ™é€‰æ‹©ç›¸åº”åº”ç”¨æ‰“å¼€æ­¤æ–‡ä»¶
			openFile(file);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(Name, "level1 onItemLongClick");
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		final File file = (File) map.get("file");
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("æ“ä½œ")
				.setItems(new String[] { "å¤åˆ¶", "å‰ªåˆ‡", "ç²˜è´´", "å‘é€", "é‡å‘½å", "åˆ é™¤", "å±æ€§" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {

								case 0: // å¤åˆ¶
									Log.v(Name, "level2 copy");
									copyPath = new File(file.getAbsolutePath());
									flag = "copy";
									break;
								case 1: // å‰ªåˆ‡
									Log.v(Name, "level2 cut");
									copyPath = new File(file.getAbsolutePath());
									flag = "cut";
									break;
								case 2: // ç²˜è´´
									Log.v(Name, "level2 paste");
									final String startPath = copyPath.getAbsolutePath();
									final String desPath = currentDir.getText().toString() + "/" + copyPath.getName();
									File[] files = new File(currentDir.getText().toString()).listFiles();
									for (File file : files) {
										if (copyPath.getName().equals(file.getName())) {
											Toast.makeText(MainActivity.this, "æ­¤æ–‡ä»¶/æ–‡ä»¶å¤¹å·²å­˜åœ¨", Toast.LENGTH_SHORT).show();
											return;
										}
									}

									int length = (int) (getLength(copyPath) / (1024));
									progressDialog = new ProgressDialog(MainActivity.this);
									progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									progressDialog.setMessage("æ­£åœ¨å¤åˆ¶");
									progressDialog.setMax(length);
									progressDialog.setProgress(0);
									progressDialog.setCancelable(false);
									progressDialog.show();

									new Thread() {
										public void run() {
											copy(startPath, desPath);
											//é€šè¿‡Messageå¯¹è±¡å‘åŸçº¿ç¨‹ä¼ é€’ä¿¡æ¯
											Message message = Message.obtain();
											messageHandler.sendMessage(message);
											progressDialog.dismiss();
											// å¦‚æœä¸ºå‰ªåˆ‡åˆ™åˆ é™¤å¯¹åº”æ–‡ä»¶/æ–‡ä»¶å¤¹
											if ("cut".equals(flag)) {
												if (copyPath.isFile()) {
													copyPath.delete();
												} else {
													delete(copyPath);
												}
											}
										}
									}.start();
									break;
								case 3: // å‘é€
									Log.v(Name, "level2 send");
									break;
								case 4: // é‡å‘½å
									Log.v(Name, "level2 newName");
									LayoutInflater factory = LayoutInflater.from(MainActivity.this);
									final View view = factory.inflate(R.layout.rename, null);
									((EditText) view.findViewById(R.id.rename)).setText(file.getName());
									AlertDialog d = new AlertDialog.Builder(MainActivity.this).setCancelable(true)
											.setMessage("æ–°æ–‡ä»¶å").setView(view)
											.setPositiveButton("ç¡®å®š", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													String newName = ((EditText) view.findViewById(R.id.rename))
															.getText().toString();
													String newFile = currentDir.getText().toString() + "/" + newName;
													if (new File(newFile).exists()) {
														Toast.makeText(MainActivity.this, "æ–‡ä»¶åé‡å¤", Toast.LENGTH_LONG)
																.show();
														return;
													}
													file.renameTo(new File(newFile));
												}
											}).create();
									d.show();
									loadFiles(new File(currentDir.getText().toString()));
									break;
								case 5: // åˆ é™¤
									Log.v(Name, "level2 delete");
									AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
											.setMessage("ç¡®å®è¦åˆ é™¤" + file.getName() + "å—?").setCancelable(true)
											.setPositiveButton("ç¡®å®š", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													if (file.isFile()) {
														Log.v(Name, "level3 isFile");
														file.delete();
													} else {
														Log.v(Name, "level3 !isFile");
														delete(file);
													}
													loadFiles(new File(currentDir.getText().toString()));
												};
											}).create();
									ad.show();
			
									break;
								case 6: // æ˜¾ç¤ºå±æ€§
									String[] attr = new String[] {"1", "2", "3", "4"};

									attr[0] = "æ–‡ä»¶åï¼š\n"+file.getName();
									attr[1] = "å¤§å°ï¼š"+String.valueOf(getLength(file))+"Byte";
									attr[2] = "ä¿®æ”¹æ—¶é—´ï¼š\n"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(file.lastModified());
									attr[3] = "ç»å¯¹è·¯å¾„ï¼š\n"+file.getAbsolutePath();
									
									new AlertDialog.Builder(MainActivity.this).setTitle("å±æ€§ä¿¡æ¯").setItems(
											attr, null).setNegativeButton("ç¡®å®š", null).show();
									break;

								default:
									break;
								}
//								Toast.makeText(MainActivity.this, "" + which, Toast.LENGTH_LONG).show();
							}
						}).create();
		dialog.show();
		/*
		 * Intent data=new Intent(); data.putExtra("path",
		 * file.getAbsolutePath()); this.setResult(RESULT_OK,data);
		 * this.finish();
		 */
		return true;
	}

	/**
	 * è¿”å›åˆ é™¤æ–‡ä»¶åçš„ç©ºæ–‡ä»¶å¤¹åˆ—è¡¨
	 * */
	public void delete(File delFile) {
		Log.v(Name, "level1 delete");
		if (delFile.isFile()) {
			Log.v(Name, "level2 delFile.isFile()");
			delFile.delete();
		} else if (delFile.isDirectory()) {
			Log.v(Name, "level2 delFile.isDirectory()");
			File[] files = delFile.listFiles();
			for (File file : files) {
				file.delete();
			}
			delFile.delete();
		}

		return;
	}

	/**
	 * è·å–æ–‡ä»¶/æ–‡ä»¶å¤¹å¤§å°
	 * */
	private long getLength(File file) {
		Log.v(Name, "level1 getLength");
		totaLength = 0;
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			File[] files = file.listFiles();
			for (File file2 : files) {
				totaLength = totaLength + getLength(file2);
			}
		} else {
			Log.v(Name, "level2 !file.isDirectory");
			totaLength = totaLength + file.length();
		}
		return totaLength;
	}

	/**
	 * å¤åˆ¶åŠŸèƒ½ï¼ŒstartFilePathä¸ºæ–‡ä»¶æºè·¯å¾„ï¼ŒdesFilePathä¸ºç›®çš„è·¯å¾„
	 * */
	public boolean copy(String startFilePath, String desFilePath) {
		Log.v(Name, "level1 copy");
		currentLen = 0;
		totaLength = 0;
		this.startFilePath = startFilePath;
		this.desFilePath = desFilePath;

		// åˆ¤æ–­æ˜¯å¦è¿”å›æˆåŠŸçš„å˜é‡
		boolean copyFinished = false;

		File startFile = new File(startFilePath);
		File desFile = new File(desFilePath);

		// å¦‚æœæºæ–‡ä»¶æ˜¯ä¸ªæ–‡ä»¶
		if (startFile.isFile()) {
			Log.v(Name, "level2 startFIle.isFile");
			copyFinished = this.copySingleFile(startFile, desFile);

			// å¦‚æœæºæ–‡ä»¶æ˜¯ä¸ªæ–‡ä»¶å¤¹ï¼Œå°±éœ€è¦é€’å½’å¤åˆ¶
		} else {
			Log.v(Name, "level2 !delFile.isFile()");
			// å¦‚æœç›®æ ‡æ–‡ä»¶å¤¹æ˜¯æºæ–‡ä»¶å¤¹çš„ä¸€ä¸ªå­ç›®å½•çš„æƒ…å†µï¼Œæ‹’ç»å¤åˆ¶ï¼Œå› ä¸ºä¼šé€ æˆæ— é™å¾ªç¯
			if (desFilePath.startsWith(startFilePath)) {
				Log.v(Name, "level3 startWith");
				System.out.println("error copy");
				return false;
			} else {
				Log.v(Name, "level3 !startWith");
				copyFinished = this.copyFolder(startFile, desFile);
			}
		}
		return copyFinished;
	}

	/**
	 * å¤åˆ¶å•ä¸ªæ–‡ä»¶ï¼Œå¦‚æœå¤åˆ¶å¤šä¸ªæ–‡ä»¶å¯ä»¥é€’å½’è°ƒç”¨
	 */
	private boolean copySingleFile(File startSingleFile, File desSingleFile) {

		Log.v(Name, "level1 copySingleFile");
		boolean rightCopy = false;

		// -------ä»æºæ–‡ä»¶ä¸­è¾“å…¥å†…å­˜å…¥byteä¸­ï¼Œåœ¨å°†byteå†™å…¥ç›®æ ‡æ–‡ä»¶--------------------
		FileInputStream singleFileInputStream = null;
		DataInputStream singleDataInputStream = null;
		FileOutputStream singleFileOutputStream = null;
		DataOutputStream singleDataOutputStream = null;

		try {

			singleFileInputStream = new FileInputStream(startSingleFile);

			singleDataInputStream = new DataInputStream(
					new BufferedInputStream(singleFileInputStream));

			singleFileOutputStream = new FileOutputStream(desSingleFile);

			singleDataOutputStream = new DataOutputStream(
					new BufferedOutputStream(singleFileOutputStream));

			byte[] b = new byte[1024];

			int len = -1;
			while ((len = singleDataInputStream.read(b)) != -1) {
				currentLen = currentLen + len;
				singleDataOutputStream.write(b, 0, len);
				progressDialog.setProgress(currentLen / (1024));
			}
			// åˆ·æ–°ç¼“å†²åŒº
			singleDataOutputStream.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (singleDataInputStream != null)
					singleDataInputStream.close();
				if (singleDataOutputStream != null)
					singleDataOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// åˆ¤æ–­æºæ–‡ä»¶å’Œç›®æ ‡æ–‡ä»¶å¤§å°æ˜¯å¦ç›¸åŒï¼Œå¦‚æœç›¸åŒè¯æ˜å¤åˆ¶æˆåŠŸ
		if (startSingleFile.length() == desSingleFile.length()) {
			Log.v(Name, "level2 copy_success");
			rightCopy = true;
		} else {
			Log.v(Name, "level2 copy_failed");
			rightCopy = false;
		}
		return rightCopy;

	}

	/**
	 * é€’å½’å¤åˆ¶æ–‡ä»¶å¤¹ï¼Œå› ä¸ºæ–‡ä»¶å¤¹ä¸‹ä¸æ­¢ä¸€ä¸ªæ–‡ä»¶ï¼Œé‡Œé¢å¯èƒ½æœ‰æ–‡ä»¶æˆ–æ–‡ä»¶å¤¹ï¼Œ å› æ­¤éœ€è¦è°ƒç”¨é€’å½’æ–¹æ³•
	 * 
	 * @param startFolder
	 *            = éœ€è¦å¤åˆ¶çš„æ–‡ä»¶å¤¹
	 * @param desFolder
	 *            = å¤åˆ¶ç›®çš„åœ°çš„æ–‡ä»¶å¤¹
	 * @return = true è¡¨ç¤ºæˆåŠŸï¼Œfalse è¡¨ç¤ºå¤±è´¥
	 */

	public boolean copyFolder(File startFolder, File desFolder) {

		Log.v(Name, "level1  copyFolder");
		boolean rightCopy = false;

		rightCopy = this.recursionCopy(startFolder, desFolder);

		return rightCopy;
	}

	/**
	 * å¤åˆ¶æ–‡ä»¶å¤¹å‡½æ•°ï¼Œæ­¤å‡½æ•°æ˜¯ä¸ªé€’å½’ï¼Œä¼šå¤åˆ¶æ–‡ä»¶å¤¹ä¸‹çš„æ‰€æœ‰æ–‡ä»¶
	 * 
	 * @param recFileFolder
	 *            = éœ€è¦æ‹·è´çš„æ–‡ä»¶å¤¹æˆ–å­æ–‡ä»¶å¤¹
	 * @param recDesFolder
	 *            = æ‹·è´çš„ç›®çš„æ–‡ä»¶å¤¹æˆ–å­æ–‡ä»¶å¤¹ï¼Œ
	 * @return = trueè¡¨ç¤ºæˆåŠŸï¼Œ falseè¡¨ç¤ºå¤±è´¥
	 */
	private boolean recursionCopy(File recFileFolder, File recDesFolder) {

		Log.v(Name, "level1 recursionCopy");
		File desFolder = recDesFolder;

		// å› ä¸ºç›®çš„æ–‡ä»¶å¤¹æˆ–å­æ–‡ä»¶å¤¹ä¸å­˜åœ¨ï¼Œéœ€è¦åˆ›å»º
		desFolder.mkdir();

		// æ­¤ä¸ºéœ€è¦æ‹·è´çš„æ–‡ä»¶å¤¹ä¸‹çš„æ‰€æœ‰æ–‡ä»¶åˆ—è¡¨ï¼ˆå…¶ä¸­æœ‰æ–‡ä»¶å’Œæ–‡ä»¶å¤¹ï¼‰
		File[] files = recFileFolder.listFiles();

		// å¦‚æœæ˜¯ä¸ªç©ºæ–‡ä»¶å¤¹
		if (files.length == 0)
			return true;

		/*
		 * å°†æ–‡ä»¶å¤¹ä¸‹æ‰€æœ‰æ–‡ä»¶æ”¾å…¥forå¾ªç¯ï¼Œå¦‚æœæ˜¯æ–‡ä»¶ï¼Œé‚£ä¹ˆè°ƒç”¨copySingleFile()æ‹·è´æ–‡ä»¶ï¼Œ å¦‚æœæ˜¯æ–‡ä»¶å¤¹ï¼Œé‚£ä¹ˆé€’å½’è°ƒç”¨æ­¤å‡½æ•°
		 */
		for (File thisFile : files) {

			// å¦‚æœæ­¤æ–‡ä»¶æ˜¯ä¸ªæ–‡ä»¶ï¼Œé‚£ä¹ˆç›´æ¥è°ƒç”¨å•ä¸ªæ–‡ä»¶å¤åˆ¶å‘½ä»¤å¤åˆ¶æ–‡ä»¶
			if (thisFile.isFile()) {
				// å¾—åˆ°æ­¤æ–‡ä»¶çš„æ–°ä½ç½®åœ°å€
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());

				boolean rightCopy = this.copySingleFile(thisFile, new File(
						desContentFilePath));

				// å¦‚æœå¤åˆ¶å¤±è´¥ï¼Œå°±è·³å‡ºå¾ªç¯åœæ­¢å¤åˆ¶
				if (!rightCopy)
					return false;

				// å¦‚æœæ˜¯ä¸ªæ–‡ä»¶å¤¹
			} else {

				/*
				 * æ­¤å‡½æ•°æ˜¯ä¸ºäº†å¾—åˆ°ç›®çš„æ–‡ä»¶å¤¹çš„åœ°å€ï¼Œ å¦‚ï¼šæºæ–‡ä»¶å¤¹ä¸ºï¼šD:/yingzi/text (å…¶ä¸­textæ–‡ä»¶å¤¹ä¸‹æœ‰å¦ä¸€ä¸ªæ–‡ä»¶å¤¹
				 * second : D:/yingzi/text/second) ç›®æ ‡ä½ç½®ä¸ºï¼šE:/level1/text
				 * é‚£ä¹ˆæ­¤secondæ–‡ä»¶å¤¹åœ¨ç›®æ ‡åœ°å€çš„ä½ç½®å°±æ˜¯ E:/level1/text/second
				 */
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());
				// é€’å½’çš„è°ƒç”¨æ­¤å‡½æ•°ï¼Œç¡®ä¿å‡½æ•°éƒ½è¢«å¤åˆ¶å®Œå…¨
				boolean rightCopy = recursionCopy(thisFile, new File(
						desContentFilePath));
				if (!rightCopy)
					return false;
			}

		}
		return true;
	}

	/**
	 * æ­¤å‡½æ•°æ˜¯ä¸ºäº†å¾—åˆ°ç›®çš„æ–‡ä»¶å¤¹çš„åœ°å€ï¼Œ å¦‚ï¼šæºæ–‡ä»¶å¤¹ä¸ºï¼šD:/yingzi/text (å…¶ä¸­textæ–‡ä»¶å¤¹ä¸‹æœ‰å¦ä¸€ä¸ªæ–‡ä»¶å¤¹ second :
	 * D:/yingzi/text/second) ç›®æ ‡ä½ç½®ä¸ºï¼šE:/level1/text é‚£ä¹ˆæ­¤secondæ–‡ä»¶å¤¹åœ¨ç›®æ ‡åœ°å€çš„ä½ç½®å°±æ˜¯
	 * E:/level1/text/second æ­¤æ–¹æ³•ä¸­ startFolderPath = D:/yingzi/text (æºæ–‡ä»¶å¤¹) ï¼›
	 * desFolderPath = E:/level1/text (ç›®æ ‡ä½ç½®)ï¼› currentFilePath =
	 * D:/yingzi/text/second(éœ€è¦å¤åˆ¶çš„å­æ–‡ä»¶å¤¹) è¿”å›å€¼ä¸ºï¼š E:/level1/text/second
	 */
	private String getSubFilePath(String startFolderPath, String desFolderPath,
			String currentFilePath) {

		Log.v(Name, "level1 getSubFilePath");

		String currentDesFilePath = null;

		int i = startFolderPath.length();

		// int j = desFolderPath.lastIndexOf("/");

		// String subDirPath = startFolderPath.substring(0, i);
		// String subDesPath = desFolderPath.substring(0, j);

		currentDesFilePath = desFolderPath + "/"
				+ currentFilePath.substring(i + 1);

		return currentDesFilePath;

	}

}
=======
package com.explorer;

import static com.mime.MIME.MIME_MapTable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.file.R;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private TextView currentDir;// ÓÃÓÚÏÔÊ¾ÎÄ¼ş¼ĞµÄ¶îÄ¿Â¼£¬ÔØÈëÏàÓ¦µÄÎÄ¼ş¼Ğ£¬ÎªĞÂ½¨µÄÎÄ¼ş¼Ğ½¨Á¢ÏàÓ¦µÄÄ¿Â¼
	private Button btnC;// ÊÖ»úÄÚ´æ ÓÃÓÚ¶¨Î»¸ùÄ¿Â¼
	private Button btnE;// ´æ´¢¿¨ ÓÃÓÚ¶¨Î»¸ùÄ¿Â¼
	private ListView listView; // ÏÔÊ¾ÏàÓ¦°´ÎÄ¼ş¼Ğ
	private File rootDir; // ¸ùÄ¿Â¼ÎÄ¼ş¼Ğ
	private File copyPath; // µ±Ö´ĞĞ¸´ÖÆ¡¢Õ³ÌùµÈ¹¤×÷Ê±£¬½«Ô­µØÖ·´æ´¢µ½copyPathÖĞ¼ÇÂ¼
	private String flag;// ÓÃÓÚ¼ÇÂ¼Ö´ĞĞµÄ²Ù×÷£¬°üÀ¨¸´ÖÆ¡¢¼ôÇĞµÈ
	private String startFilePath;// µ±Ö´ĞĞÕ³ÌùµÈ¹¤×÷Ê±£¬¼ÇÂ¼Ô­µØÖ·
	private String desFilePath;// µ±Ö´ĞĞÕ³ÌùµÈ¹¤×÷Ê±¼ÇÂ¼Ä¿µÄµØÖ·
	private ProgressDialog progressDialog;// ÓÃÓÚ¸´ÖÆÊ±µÄ½ø¶ÈÌõ
	private int currentLen = 0;// ÓÃÓÚ¼ÇÂ¼¸´ÖÆµ±Ç°µÄÎÄ¼ş£¨¼Ğ£©Êı
	private long totaLength = 0;// ÓÃÓÚ¼ÇÂ¼×Ü¹²Òª¸´ÖÆµÄÎÄ¼ş£¨¼Ğ£©Êı
	private Handler messageHandler; // Ö÷Òª½ÓÊÜ×ÓÏß³Ì·¢ËÍµÄÊı¾İ, ²¢ÓÃ´ËÊı¾İÅäºÏÖ÷Ïß³Ì¸üĞÂUI.
	private static String Name = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(Name, "level1 onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentDir = (TextView) findViewById(R.id.currentDir);
		// fileName = (TextView) findViewById(R.id.name);
		btnC = (Button) findViewById(R.id.btnC);// ÊÖ»úÄÚ´æ°´Å¥µÄÉùÃ÷
		btnE = (Button) findViewById(R.id.btnE);// ´æ´¢¿¨°´Å¥µÄÉùÃ÷
		btnC.setOnClickListener(this);// ÎªÊÖ»úÄÚ´æ°´Å¥ÉèÖÃ¼àÌıÆ÷
		btnE.setOnClickListener(this);// Îª´æ´¢¿¨°´Å¥ÉèÖÃ¼àÌıÆ÷
		listView = (ListView) findViewById(R.id.listView);// ÎÄ¼şÁĞ±íµÄêÉÃô
		listView.setOnItemClickListener(this);// ÎªÃ¿¸öÎÄ¼ş£¨¼Ğ£©ÉèÖÃ¶Ì°´µÄ¼àÌıÆ÷
		listView.setOnItemLongClickListener(this);// ÎªÃ¿¸öÎÄ¼ş£¨¼Ğ£©ÉèÖÃ³¤°´µÄ¼àÌıÆ÷
		// µÃµ½µ±Ç°Ïß³ÌµÄLooperÊµÀı£¬ÓÉÓÚµ±Ç°Ïß³ÌÊÇUIÏß³ÌÒ²¿ÉÒÔÍ¨¹ıLooper.getMainLooper()µÃµ½
		messageHandler = new MessageHandler(Looper.myLooper());

		// ÉèÖÃ¸ùÄ¿Â¼

		if (Environment.getExternalStorageState().equals(//ÅĞ¶ÏÊÇ·ñ¹ÒÔØSD¿¨
				Environment.MEDIA_MOUNTED)) {
			Log.v(Name,
					"level2 Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getExternalStorageDirectory();
		} else {
			Log.v(Name,
					"level2 !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getRootDirectory();
		}
		loadFiles(rootDir);
	}

	// ×Ô¶¨ÒåHandler
	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.v(Name, "test initial rootdir"
					+ currentDir.getText().toString());
			loadFiles(new File(currentDir.getText().toString()));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		Log.v(Name, "level1 onKeyDown");
		// TODO Auto-generated method stub
		Map<String, Object> map = (Map<String, Object>) this.listView.getItemAtPosition(0);
		final File file = (File) map.get("file");
		final String str = (String)map.get("name");
		//Èç¹û²»ÊÇ¸ùÄ¿Â¼£¬Ôò·µ»ØÉÏÒ»²ãÄ¿Â¼
		if(str.equals("ÉÏÒ»¼¶Ä¿Â¼"))
		{
			Log.v(Name, "level2 notrootDir");
			loadFiles(file);
			return true;
		}
		else//Èç¹ûÎª¸ùÄ¿Â¼£¬ÔòÍË³ö
		{
			Log.v(Name, "level2 isrootDir");
			return super.onKeyDown(keyCode, event);

		} 
	}



	@Override
	//³õÊ¼»¯²Ëµ¥£¬Ö»»áÔÚµÚÒ»´Î³õÊ¼»¯²Ëµ¥Ê±µ÷ÓÃ
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Name, "level1 onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Name, "level1 onOptionsItemSelected");

		// Èç¹ûÑ¡ÔñµÄÊÇĞÂ½¨ÎÄ¼ş¼ĞÑ¡Ïî
		if (item.getItemId() == R.id.newFile) {
			Log.v(Name, "level2 item.getItemId() == R.id.newFile");
			LayoutInflater factory = LayoutInflater.from(MainActivity.this);
			final View view = factory.inflate(R.layout.rename, null);
			AlertDialog d = new AlertDialog.Builder(MainActivity.this)
					.setCancelable(true)
					.setMessage("ÎÄ¼ş¼ĞÃû")
					.setView(view)
					.setPositiveButton("È·¶¨",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Log.v(Name, "level3 onClick");
									String dirName = ((EditText) view
											.findViewById(R.id.rename))
											.getText().toString();
									String newFile = currentDir.getText()
											.toString() + "/" + dirName;
									if (new File(newFile).exists()) {
										Log.v(Name,
												"level 4 File(newFile).exits");
										Toast.makeText(MainActivity.this,
												"ÎÄ¼ş¼ĞÒÑ´æÔÚ", Toast.LENGTH_LONG)
												.show();
										return;
									}
									File f = new File(currentDir.getText()
											.toString(), dirName);
									f.mkdir();
								}
							}).create();
			d.show();

			loadFiles(new File(currentDir.getText().toString()));
		} else if (item.getItemId() == R.id.about) {
			Log.v(Name, "level2 item.getItemId() == R.id.about");
			Dialog d = new AlertDialog.Builder(MainActivity.this)
					.setTitle("ÎÄ¼şä¯ÀÀÆ÷1.0beta").setMessage("±¾³ÌĞòÓÉÀîºéÏé ÕÔÑÒÖÆ×÷")
					.setPositiveButton("È·¶¨", null).create();
			d.show();
		} else if (item.getItemId() == R.id.exit) {
			Log.v(Name, "level2 item.getItemId() == R.id.exit");
			MainActivity.this.finish();
		}
		return true;
	}

	/**
	 * ¼ÓÔØµ±Ç°ÎÄ¼ş¼ĞÁĞ±í
	 * 
	 */
	public void loadFiles(File dir) {
		Log.v(Name, "level1 loadFiles");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// ÉùÃ÷Ò»¸öMapÊı×é£¬ÓÃÀ´´æ´¢ÎÄ¼ş£¨¼Ğ£©µÄÏÔÊ¾ĞÅÏ¢

		// Èç¹ûÄ¿Â¼²»Îª¿ÕµÄ»¯£¬ÔòÏÔÊ¾ÏàÓ¦µÄÎÄ¼ş£¨¼Ğ£©ĞÅÏ¢
		if (dir != null) {
			Log.v(Name, "level2 dir!= NULL");
			// Èç¹û²»ÊÇ¸ùÄ¿Â¼µÄ»°£¬ÔòÎª ÉÏ¼¶Ä¿Â¼ÔÚListViewµÄ×îÉÏ·½ÁôÒ»¸ö½Ó¿Ú
			if (!dir.getAbsolutePath().equals(rootDir.getAbsolutePath())) {
				Log.v(Name,
						"level3 !dir.getAbsolutePath().equals(rootDir.getAbsolutePath())");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("file", dir.getParentFile());
				map.put("name", "ÉÏÒ»¼¶Ä¿Â¼");
				map.put("img", R.drawable.folder);
				list.add(map);
			}
			// ÉèÖÃÏÔÊ¾Ä¿Â¼
			currentDir.setText(dir.getAbsolutePath());
			File[] files = dir.listFiles();
			sortFiles(files);

			// ÎªÃ¿¸úÎÄ¼ş£¨¼Ğ£©µÄÏÔÊ¾×ö×¼±¸£¬ÏÈ½«ĞÅÏ¢´æ´¢ÆğÀ´

			if (files != null) {
				Log.v(Name, "level2 files!=NULL");
				for (File f : files) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("file", f);
					map.put("name", f.getName());
					map.put("img",
							f.isDirectory() ? R.drawable.folder
									: (f.getName().toLowerCase()
											.endsWith(".zip") ? R.drawable.zip
											: R.drawable.text));
					list.add(map);
				}
			}

		} else {// Èç¹ûÄ¿Â¼²»´æÔÚÔòÌáÊ¾´íÎó
			Log.v(Name, "level2 Files == NULL");
			Toast.makeText(this, "Ä¿Â¼²»ÕıÈ·£¬ÇëÊäÈëÕıÈ·µÄÄ¿Â¼!", Toast.LENGTH_LONG).show();
		}
		// ÏÔÊ¾ÎÄ¼ş£¨¼Ğ£©ĞÅÏ¢
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.item,
				new String[] { "name", "img" }, new int[] { R.id.name,
						R.id.icon });
		// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
	}

	/**
	 * ÅÅĞòÎÄ¼şÁĞ±í
	 * 
	 */
	private void sortFiles(File[] files) {
		Log.v(Name, "level1 sortFiles");
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && file2.isDirectory()) {
					Log.v(Name,
							"level2 file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				if (file2.isDirectory()) {
					Log.v(Name,
							"level2 !file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				return -1;
			}
		});
	}

	/**
	 * ´ò¿ªÎÄ¼ş
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		Log.v(Name, "level1 openFile");
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// ÉèÖÃintentµÄActionÊôĞÔ
		intent.setAction(Intent.ACTION_VIEW);
		// »ñÈ¡ÎÄ¼şfileµÄMIMEÀàĞÍ
		String type = getMIMEType(file);
		// ÉèÖÃintentµÄdataºÍTypeÊôĞÔ¡£
		intent.setDataAndType(Uri.fromFile(file), type);
		// Ìø×ª
		startActivity(intent);

	}

	/**
	 * ¸ù¾İÎÄ¼şºó×ºÃû»ñµÃ¶ÔÓ¦µÄMIMEÀàĞÍ¡£
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		Log.v(Name, "level1 getMIMEType");
		String type = "*/*";
		String fName = file.getName();
		// »ñÈ¡ºó×ºÃûÇ°µÄ·Ö¸ô·û"."ÔÚfNameÖĞµÄÎ»ÖÃ¡£
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			Log.v(Name, "level2 dotIndex<0");
			return type;
		}
		/* »ñÈ¡ÎÄ¼şµÄºó×ºÃû */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "") {
			Log.v(Name, "level2 end ==  ");
			return type;
		}
		// ÔÚMIMEºÍÎÄ¼şÀàĞÍµÄÆ¥Åä±íÖĞÕÒµ½¶ÔÓ¦µÄMIMEÀàĞÍ¡£
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	@Override
	public void onClick(View v) {
		Log.v(Name, "level1 onClick");
		if (v.getId() == R.id.btnC) {
			rootDir = Environment.getRootDirectory();
			loadFiles(rootDir);
		} else if (v.getId() == R.id.btnE) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				rootDir = Environment.getExternalStorageDirectory();
				loadFiles(rootDir);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.v(Name, "level1 onItemClick");
		// »ñÈ¡ÎÄ¼ş£¨¼Ğ£©µÄÒıÓÃ
		Map<String, Object> map = (Map<String, Object>) parent
				.getItemAtPosition(position);
		final File file = (File) map.get("file");
		// Èç¹ûÊÇÎÄ¼ş¼ĞµÄ»°£¬Ôò½øÈë´ËÎÄ¼ş¼Ğ£¬ÏÔÊ¾´ËÎÄ¼ş¼ĞµÄÄÚÈİ
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			try {
				loadFiles(file);
			} catch (Exception e) {// ÈôÓöµ½È¨ÏŞ²»×ãµÄÇé¿ö£¬Ôòµ¯³ö¾¯¸æ
				Toast.makeText(this, "È¨ÏŞ²»×ã", Toast.LENGTH_SHORT).show();
			}
		} else {// Èç¹ıÊÇÎÄ¼ş£¬ÔòÑ¡ÔñÏàÓ¦Ó¦ÓÃ´ò¿ª´ËÎÄ¼ş
			openFile(file);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(Name, "level1 onItemLongClick");
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		final File file = (File) map.get("file");
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("²Ù×÷")
				.setItems(new String[] { "¸´ÖÆ", "¼ôÇĞ", "Õ³Ìù", "·¢ËÍ", "ÖØÃüÃû", "É¾³ı", "ÊôĞÔ" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {

								case 0: // ¸´ÖÆ
									Log.v(Name, "level2 copy");
									copyPath = new File(file.getAbsolutePath());
									flag = "copy";
									break;
								case 1: // ¼ôÇĞ
									Log.v(Name, "level2 cut");
									copyPath = new File(file.getAbsolutePath());
									flag = "cut";
									break;
								case 2: // Õ³Ìù
									Log.v(Name, "level2 paste");
									final String startPath = copyPath.getAbsolutePath();
									final String desPath = currentDir.getText().toString() + "/" + copyPath.getName();
									File[] files = new File(currentDir.getText().toString()).listFiles();
									for (File file : files) {
										if (copyPath.getName().equals(file.getName())) {
											Toast.makeText(MainActivity.this, "´ËÎÄ¼ş/ÎÄ¼ş¼ĞÒÑ´æÔÚ", Toast.LENGTH_SHORT).show();
											return;
										}
									}

									int length = (int) (getLength(copyPath) / (1024));
									progressDialog = new ProgressDialog(MainActivity.this);
									progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									progressDialog.setMessage("ÕıÔÚ¸´ÖÆ");
									progressDialog.setMax(length);
									progressDialog.setProgress(0);
									progressDialog.setCancelable(false);
									progressDialog.show();

									new Thread() {
										public void run() {
											copy(startPath, desPath);
											//Í¨¹ıMessage¶ÔÏóÏòÔ­Ïß³Ì´«µİĞÅÏ¢
											Message message = Message.obtain();
											messageHandler.sendMessage(message);
											progressDialog.dismiss();
											// Èç¹ûÎª¼ôÇĞÔòÉ¾³ı¶ÔÓ¦ÎÄ¼ş/ÎÄ¼ş¼Ğ
											if ("cut".equals(flag)) {
												if (copyPath.isFile()) {
													copyPath.delete();
												} else {
													delete(copyPath);
												}
											}
										}
									}.start();
									break;
								case 3: // ·¢ËÍ
									Log.v(Name, "level2 send");
									break;
								case 4: // ÖØÃüÃû
									Log.v(Name, "level2 newName");
									LayoutInflater factory = LayoutInflater.from(MainActivity.this);
									final View view = factory.inflate(R.layout.rename, null);
									((EditText) view.findViewById(R.id.rename)).setText(file.getName());
									AlertDialog d = new AlertDialog.Builder(MainActivity.this).setCancelable(true)
											.setMessage("ĞÂÎÄ¼şÃû").setView(view)
											.setPositiveButton("È·¶¨", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													String newName = ((EditText) view.findViewById(R.id.rename))
															.getText().toString();
													String newFile = currentDir.getText().toString() + "/" + newName;
													if (new File(newFile).exists()) {
														Toast.makeText(MainActivity.this, "ÎÄ¼şÃûÖØ¸´", Toast.LENGTH_LONG)
																.show();
														return;
													}
													file.renameTo(new File(newFile));
												}
											}).create();
									d.show();
									loadFiles(new File(currentDir.getText().toString()));
									break;
								case 5: // É¾³ı
									Log.v(Name, "level2 delete");
									AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
											.setMessage("È·ÊµÒªÉ¾³ı" + file.getName() + "Âğ?").setCancelable(true)
											.setPositiveButton("È·¶¨", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													if (file.isFile()) {
														Log.v(Name, "level3 isFile");
														file.delete();
													} else {
														Log.v(Name, "level3 !isFile");
														delete(file);
													}
													loadFiles(new File(currentDir.getText().toString()));
												};
											}).create();
									ad.show();
			
									break;
								case 6: // ÏÔÊ¾ÊôĞÔ
									String[] attr = new String[] {"1", "2", "3", "4"};

									attr[0] = "ÎÄ¼şÃû£º\n"+file.getName();
									attr[1] = "´óĞ¡£º"+String.valueOf(getLength(file))+"Byte";
									attr[2] = "ĞŞ¸ÄÊ±¼ä£º\n"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(file.lastModified());
									attr[3] = "¾ø¶ÔÂ·¾¶£º\n"+file.getAbsolutePath();
									
									new AlertDialog.Builder(MainActivity.this).setTitle("ÊôĞÔĞÅÏ¢").setItems(
											attr, null).setNegativeButton("È·¶¨", null).show();
									break;

								default:
									break;
								}
//								Toast.makeText(MainActivity.this, "" + which, Toast.LENGTH_LONG).show();
							}
						}).create();
		dialog.show();
		/*
		 * Intent data=new Intent(); data.putExtra("path",
		 * file.getAbsolutePath()); this.setResult(RESULT_OK,data);
		 * this.finish();
		 */
		return true;
	}

	/**
	 * ·µ»ØÉ¾³ıÎÄ¼şºóµÄ¿ÕÎÄ¼ş¼ĞÁĞ±í
	 * */
	public void delete(File delFile) {
		Log.v(Name, "level1 delete");
		if (delFile.isFile()) {
			Log.v(Name, "level2 delFile.isFile()");
			delFile.delete();
		} else if (delFile.isDirectory()) {
			Log.v(Name, "level2 delFile.isDirectory()");
			File[] files = delFile.listFiles();
			for (File file : files) {
				file.delete();
			}
			delFile.delete();
		}

		return;
	}

	/**
	 * »ñÈ¡ÎÄ¼ş/ÎÄ¼ş¼Ğ´óĞ¡
	 * */
	private long getLength(File file) {
		Log.v(Name, "level1 getLength");
		totaLength = 0;
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			File[] files = file.listFiles();
			for (File file2 : files) {
				totaLength = totaLength + getLength(file2);
			}
		} else {
			Log.v(Name, "level2 !file.isDirectory");
			totaLength = totaLength + file.length();
		}
		return totaLength;
	}

	/**
	 * ¸´ÖÆ¹¦ÄÜ£¬startFilePathÎªÎÄ¼şÔ´Â·¾¶£¬desFilePathÎªÄ¿µÄÂ·¾¶
	 * */
	public boolean copy(String startFilePath, String desFilePath) {
		Log.v(Name, "level1 copy");
		currentLen = 0;
		totaLength = 0;
		this.startFilePath = startFilePath;
		this.desFilePath = desFilePath;

		// ÅĞ¶ÏÊÇ·ñ·µ»Ø³É¹¦µÄ±äÁ¿
		boolean copyFinished = false;

		File startFile = new File(startFilePath);
		File desFile = new File(desFilePath);

		// Èç¹ûÔ´ÎÄ¼şÊÇ¸öÎÄ¼ş
		if (startFile.isFile()) {
			Log.v(Name, "level2 startFIle.isFile");
			copyFinished = this.copySingleFile(startFile, desFile);

			// Èç¹ûÔ´ÎÄ¼şÊÇ¸öÎÄ¼ş¼Ğ£¬¾ÍĞèÒªµİ¹é¸´ÖÆ
		} else {
			Log.v(Name, "level2 !delFile.isFile()");
			// Èç¹ûÄ¿±êÎÄ¼ş¼ĞÊÇÔ´ÎÄ¼ş¼ĞµÄÒ»¸ö×ÓÄ¿Â¼µÄÇé¿ö£¬¾Ü¾ø¸´ÖÆ£¬ÒòÎª»áÔì³ÉÎŞÏŞÑ­»·
			if (desFilePath.startsWith(startFilePath)) {
				Log.v(Name, "level3 startWith");
				System.out.println("error copy");
				return false;
			} else {
				Log.v(Name, "level3 !startWith");
				copyFinished = this.copyFolder(startFile, desFile);
			}
		}
		return copyFinished;
	}

	/**
	 * ¸´ÖÆµ¥¸öÎÄ¼ş£¬Èç¹û¸´ÖÆ¶à¸öÎÄ¼ş¿ÉÒÔµİ¹éµ÷ÓÃ
	 */
	private boolean copySingleFile(File startSingleFile, File desSingleFile) {

		Log.v(Name, "level1 copySingleFile");
		boolean rightCopy = false;

		// -------´ÓÔ´ÎÄ¼şÖĞÊäÈëÄÚ´æÈëbyteÖĞ£¬ÔÚ½«byteĞ´ÈëÄ¿±êÎÄ¼ş--------------------
		FileInputStream singleFileInputStream = null;
		DataInputStream singleDataInputStream = null;
		FileOutputStream singleFileOutputStream = null;
		DataOutputStream singleDataOutputStream = null;

		try {

			singleFileInputStream = new FileInputStream(startSingleFile);

			singleDataInputStream = new DataInputStream(
					new BufferedInputStream(singleFileInputStream));

			singleFileOutputStream = new FileOutputStream(desSingleFile);

			singleDataOutputStream = new DataOutputStream(
					new BufferedOutputStream(singleFileOutputStream));

			byte[] b = new byte[1024];

			int len = -1;
			while ((len = singleDataInputStream.read(b)) != -1) {
				currentLen = currentLen + len;
				singleDataOutputStream.write(b, 0, len);
				progressDialog.setProgress(currentLen / (1024));
			}
			// Ë¢ĞÂ»º³åÇø
			singleDataOutputStream.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (singleDataInputStream != null)
					singleDataInputStream.close();
				if (singleDataOutputStream != null)
					singleDataOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ÅĞ¶ÏÔ´ÎÄ¼şºÍÄ¿±êÎÄ¼ş´óĞ¡ÊÇ·ñÏàÍ¬£¬Èç¹ûÏàÍ¬Ö¤Ã÷¸´ÖÆ³É¹¦
		if (startSingleFile.length() == desSingleFile.length()) {
			Log.v(Name, "level2 copy_success");
			rightCopy = true;
		} else {
			Log.v(Name, "level2 copy_failed");
			rightCopy = false;
		}
		return rightCopy;

	}

	/**
	 * µİ¹é¸´ÖÆÎÄ¼ş¼Ğ£¬ÒòÎªÎÄ¼ş¼ĞÏÂ²»Ö¹Ò»¸öÎÄ¼ş£¬ÀïÃæ¿ÉÄÜÓĞÎÄ¼ş»òÎÄ¼ş¼Ğ£¬ Òò´ËĞèÒªµ÷ÓÃµİ¹é·½·¨
	 * 
	 * @param startFolder
	 *            = ĞèÒª¸´ÖÆµÄÎÄ¼ş¼Ğ
	 * @param desFolder
	 *            = ¸´ÖÆÄ¿µÄµØµÄÎÄ¼ş¼Ğ
	 * @return = true ±íÊ¾³É¹¦£¬false ±íÊ¾Ê§°Ü
	 */

	public boolean copyFolder(File startFolder, File desFolder) {

		Log.v(Name, "level1  copyFolder");
		boolean rightCopy = false;

		rightCopy = this.recursionCopy(startFolder, desFolder);

		return rightCopy;
	}

	/**
	 * ¸´ÖÆÎÄ¼ş¼Ğº¯Êı£¬´Ëº¯ÊıÊÇ¸öµİ¹é£¬»á¸´ÖÆÎÄ¼ş¼ĞÏÂµÄËùÓĞÎÄ¼ş
	 * 
	 * @param recFileFolder
	 *            = ĞèÒª¿½±´µÄÎÄ¼ş¼Ğ»ò×ÓÎÄ¼ş¼Ğ
	 * @param recDesFolder
	 *            = ¿½±´µÄÄ¿µÄÎÄ¼ş¼Ğ»ò×ÓÎÄ¼ş¼Ğ£¬
	 * @return = true±íÊ¾³É¹¦£¬ false±íÊ¾Ê§°Ü
	 */
	private boolean recursionCopy(File recFileFolder, File recDesFolder) {

		Log.v(Name, "level1 recursionCopy");
		File desFolder = recDesFolder;

		// ÒòÎªÄ¿µÄÎÄ¼ş¼Ğ»ò×ÓÎÄ¼ş¼Ğ²»´æÔÚ£¬ĞèÒª´´½¨
		desFolder.mkdir();

		// ´ËÎªĞèÒª¿½±´µÄÎÄ¼ş¼ĞÏÂµÄËùÓĞÎÄ¼şÁĞ±í£¨ÆäÖĞÓĞÎÄ¼şºÍÎÄ¼ş¼Ğ£©
		File[] files = recFileFolder.listFiles();

		// Èç¹ûÊÇ¸ö¿ÕÎÄ¼ş¼Ğ
		if (files.length == 0)
			return true;

		/*
		 * ½«ÎÄ¼ş¼ĞÏÂËùÓĞÎÄ¼ş·ÅÈëforÑ­»·£¬Èç¹ûÊÇÎÄ¼ş£¬ÄÇÃ´µ÷ÓÃcopySingleFile()¿½±´ÎÄ¼ş£¬ Èç¹ûÊÇÎÄ¼ş¼Ğ£¬ÄÇÃ´µİ¹éµ÷ÓÃ´Ëº¯Êı
		 */
		for (File thisFile : files) {

			// Èç¹û´ËÎÄ¼şÊÇ¸öÎÄ¼ş£¬ÄÇÃ´Ö±½Óµ÷ÓÃµ¥¸öÎÄ¼ş¸´ÖÆÃüÁî¸´ÖÆÎÄ¼ş
			if (thisFile.isFile()) {
				// µÃµ½´ËÎÄ¼şµÄĞÂÎ»ÖÃµØÖ·
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());

				boolean rightCopy = this.copySingleFile(thisFile, new File(
						desContentFilePath));

				// Èç¹û¸´ÖÆÊ§°Ü£¬¾ÍÌø³öÑ­»·Í£Ö¹¸´ÖÆ
				if (!rightCopy)
					return false;

				// Èç¹ûÊÇ¸öÎÄ¼ş¼Ğ
			} else {

				/*
				 * ´Ëº¯ÊıÊÇÎªÁËµÃµ½Ä¿µÄÎÄ¼ş¼ĞµÄµØÖ·£¬ Èç£ºÔ´ÎÄ¼ş¼ĞÎª£ºD:/yingzi/text (ÆäÖĞtextÎÄ¼ş¼ĞÏÂÓĞÁíÒ»¸öÎÄ¼ş¼Ğ
				 * second : D:/yingzi/text/second) Ä¿±êÎ»ÖÃÎª£ºE:/level1/text
				 * ÄÇÃ´´ËsecondÎÄ¼ş¼ĞÔÚÄ¿±êµØÖ·µÄÎ»ÖÃ¾ÍÊÇ E:/level1/text/second
				 */
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());
				// µİ¹éµÄµ÷ÓÃ´Ëº¯Êı£¬È·±£º¯Êı¶¼±»¸´ÖÆÍêÈ«
				boolean rightCopy = recursionCopy(thisFile, new File(
						desContentFilePath));
				if (!rightCopy)
					return false;
			}

		}
		return true;
	}

	/**
	 * ´Ëº¯ÊıÊÇÎªÁËµÃµ½Ä¿µÄÎÄ¼ş¼ĞµÄµØÖ·£¬ Èç£ºÔ´ÎÄ¼ş¼ĞÎª£ºD:/yingzi/text (ÆäÖĞtextÎÄ¼ş¼ĞÏÂÓĞÁíÒ»¸öÎÄ¼ş¼Ğ second :
	 * D:/yingzi/text/second) Ä¿±êÎ»ÖÃÎª£ºE:/level1/text ÄÇÃ´´ËsecondÎÄ¼ş¼ĞÔÚÄ¿±êµØÖ·µÄÎ»ÖÃ¾ÍÊÇ
	 * E:/level1/text/second ´Ë·½·¨ÖĞ startFolderPath = D:/yingzi/text (Ô´ÎÄ¼ş¼Ğ) £»
	 * desFolderPath = E:/level1/text (Ä¿±êÎ»ÖÃ)£» currentFilePath =
	 * D:/yingzi/text/second(ĞèÒª¸´ÖÆµÄ×ÓÎÄ¼ş¼Ğ) ·µ»ØÖµÎª£º E:/level1/text/second
	 */
	private String getSubFilePath(String startFolderPath, String desFolderPath,
			String currentFilePath) {

		Log.v(Name, "level1 getSubFilePath");

		String currentDesFilePath = null;

		int i = startFolderPath.length();

		// int j = desFolderPath.lastIndexOf("/");

		// String subDirPath = startFolderPath.substring(0, i);
		// String subDesPath = desFolderPath.substring(0, j);

		currentDesFilePath = desFolderPath + "/"
				+ currentFilePath.substring(i + 1);

		return currentDesFilePath;

	}

}
>>>>>>> 916e6f2f6fac6bd36cb0c11dea381d31c552ee7a
