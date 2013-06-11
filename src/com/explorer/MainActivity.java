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
	private TextView currentDir;// 用于显示文件夹的额目录，载入相应的文件夹，为新建的文件夹建立相应的目录
	private Button btnC;// 手机内存 用于定位根目录
	private Button btnE;// 存储卡 用于定位根目录
	private ListView listView; // 显示相应按文件夹
	private File rootDir; // 根目录文件夹
	private File copyPath; // 当执行复制、粘贴等工作时，将原地址存储到copyPath中记录
	private String flag;// 用于记录执行的操作，包括复制、剪切等
	private String startFilePath;// 当执行粘贴等工作时，记录原地址
	private String desFilePath;// 当执行粘贴等工作时记录目的地址
	private ProgressDialog progressDialog;// 用于复制时的进度条
	private int currentLen = 0;// 用于记录复制当前的文件（夹）数
	private long totaLength = 0;// 用于记录总共要复制的文件（夹）数
	private Handler messageHandler; // 主要接受子线程发送的数据, 并用此数据配合主线程更新UI.
	private static String Name = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(Name, "level1 onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentDir = (TextView) findViewById(R.id.currentDir);
		// fileName = (TextView) findViewById(R.id.name);
		btnC = (Button) findViewById(R.id.btnC);// 手机内存按钮的声明
		btnE = (Button) findViewById(R.id.btnE);// 存储卡按钮的声明
		btnC.setOnClickListener(this);// 为手机内存按钮设置监听器
		btnE.setOnClickListener(this);// 为存储卡按钮设置监听器
		listView = (ListView) findViewById(R.id.listView);// 文件列表的晟敏
		listView.setOnItemClickListener(this);// 为每个文件（夹）设置短按的监听器
		listView.setOnItemLongClickListener(this);// 为每个文件（夹）设置长按的监听器
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		messageHandler = new MessageHandler(Looper.myLooper());

		// 设置根目录

		if (Environment.getExternalStorageState().equals(//判断是否挂载SD卡
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

	// 自定义Handler
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
		//如果不是根目录，则返回上一层目录
		if(str.equals("上一级目录"))
		{
			Log.v(Name, "level2 notrootDir");
			loadFiles(file);
			return true;
		}
		else//如果为根目录，则退出
		{
			Log.v(Name, "level2 isrootDir");
			return super.onKeyDown(keyCode, event);

		} 
	}



	@Override
	//初始化菜单，只会在第一次初始化菜单时调用
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Name, "level1 onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Name, "level1 onOptionsItemSelected");

		// 如果选择的是新建文件夹选项
		if (item.getItemId() == R.id.newFile) {
			Log.v(Name, "level2 item.getItemId() == R.id.newFile");
			LayoutInflater factory = LayoutInflater.from(MainActivity.this);
			final View view = factory.inflate(R.layout.rename, null);
			AlertDialog d = new AlertDialog.Builder(MainActivity.this)
					.setCancelable(true)
					.setMessage("文件夹名")
					.setView(view)
					.setPositiveButton("确定",
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
												"文件夹已存在", Toast.LENGTH_LONG)
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
					.setTitle("文件浏览器1.0beta").setMessage("本程序由李洪祥 赵岩制作")
					.setPositiveButton("确定", null).create();
			d.show();
		} else if (item.getItemId() == R.id.exit) {
			Log.v(Name, "level2 item.getItemId() == R.id.exit");
			MainActivity.this.finish();
		}
		return true;
	}

	/**
	 * 加载当前文件夹列表
	 * 
	 */
	public void loadFiles(File dir) {
		Log.v(Name, "level1 loadFiles");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// 声明一个Map数组，用来存储文件（夹）的显示信息

		// 如果目录不为空的化，则显示相应的文件（夹）信息
		if (dir != null) {
			Log.v(Name, "level2 dir!= NULL");
			// 如果不是根目录的话，则为 上级目录在ListView的最上方留一个接口
			if (!dir.getAbsolutePath().equals(rootDir.getAbsolutePath())) {
				Log.v(Name,
						"level3 !dir.getAbsolutePath().equals(rootDir.getAbsolutePath())");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("file", dir.getParentFile());
				map.put("name", "上一级目录");
				map.put("img", R.drawable.folder);
				list.add(map);
			}
			// 设置显示目录
			currentDir.setText(dir.getAbsolutePath());
			File[] files = dir.listFiles();
			sortFiles(files);

			// 为每跟文件（夹）的显示做准备，先将信息存储起来

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

		} else {// 如果目录不存在则提示错误
			Log.v(Name, "level2 Files == NULL");
			Toast.makeText(this, "目录不正确，请输入正确的目录!", Toast.LENGTH_LONG).show();
		}
		// 显示文件（夹）信息
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.item,
				new String[] { "name", "img" }, new int[] { R.id.name,
						R.id.icon });
		// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
	}

	/**
	 * 排序文件列表
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
	 * 打开文件
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		Log.v(Name, "level1 openFile");
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		// 跳转
		startActivity(intent);

	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		Log.v(Name, "level1 getMIMEType");
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			Log.v(Name, "level2 dotIndex<0");
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "") {
			Log.v(Name, "level2 end ==  ");
			return type;
		}
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
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
		// 获取文件（夹）的引用
		Map<String, Object> map = (Map<String, Object>) parent
				.getItemAtPosition(position);
		final File file = (File) map.get("file");
		// 如果是文件夹的话，则进入此文件夹，显示此文件夹的内容
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			try {
				loadFiles(file);
			} catch (Exception e) {// 若遇到权限不足的情况，则弹出警告
				Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
			}
		} else {// 如过是文件，则选择相应应用打开此文件
			openFile(file);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(Name, "level1 onItemLongClick");
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		final File file = (File) map.get("file");
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("操作")
				.setItems(new String[] { "复制", "剪切", "粘贴", "发送", "重命名", "删除", "属性" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {

								case 0: // 复制
									Log.v(Name, "level2 copy");
									copyPath = new File(file.getAbsolutePath());
									flag = "copy";
									break;
								case 1: // 剪切
									Log.v(Name, "level2 cut");
									copyPath = new File(file.getAbsolutePath());
									flag = "cut";
									break;
								case 2: // 粘贴
									Log.v(Name, "level2 paste");
									final String startPath = copyPath.getAbsolutePath();
									final String desPath = currentDir.getText().toString() + "/" + copyPath.getName();
									File[] files = new File(currentDir.getText().toString()).listFiles();
									for (File file : files) {
										if (copyPath.getName().equals(file.getName())) {
											Toast.makeText(MainActivity.this, "此文件/文件夹已存在", Toast.LENGTH_SHORT).show();
											return;
										}
									}

									int length = (int) (getLength(copyPath) / (1024));
									progressDialog = new ProgressDialog(MainActivity.this);
									progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									progressDialog.setMessage("正在复制");
									progressDialog.setMax(length);
									progressDialog.setProgress(0);
									progressDialog.setCancelable(false);
									progressDialog.show();

									new Thread() {
										public void run() {
											copy(startPath, desPath);
											//通过Message对象向原线程传递信息
											Message message = Message.obtain();
											messageHandler.sendMessage(message);
											progressDialog.dismiss();
											// 如果为剪切则删除对应文件/文件夹
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
								case 3: // 发送
									Log.v(Name, "level2 send");
									break;
								case 4: // 重命名
									Log.v(Name, "level2 newName");
									LayoutInflater factory = LayoutInflater.from(MainActivity.this);
									final View view = factory.inflate(R.layout.rename, null);
									((EditText) view.findViewById(R.id.rename)).setText(file.getName());
									AlertDialog d = new AlertDialog.Builder(MainActivity.this).setCancelable(true)
											.setMessage("新文件名").setView(view)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													String newName = ((EditText) view.findViewById(R.id.rename))
															.getText().toString();
													String newFile = currentDir.getText().toString() + "/" + newName;
													if (new File(newFile).exists()) {
														Toast.makeText(MainActivity.this, "文件名重复", Toast.LENGTH_LONG)
																.show();
														return;
													}
													file.renameTo(new File(newFile));
												}
											}).create();
									d.show();
									loadFiles(new File(currentDir.getText().toString()));
									break;
								case 5: // 删除
									Log.v(Name, "level2 delete");
									AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
											.setMessage("确实要删除" + file.getName() + "吗?").setCancelable(true)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
								case 6: // 显示属性
									String[] attr = new String[] {"1", "2", "3", "4"};

									attr[0] = "文件名：\n"+file.getName();
									attr[1] = "大小："+String.valueOf(getLength(file))+"Byte";
									attr[2] = "修改时间：\n"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(file.lastModified());
									attr[3] = "绝对路径：\n"+file.getAbsolutePath();
									
									new AlertDialog.Builder(MainActivity.this).setTitle("属性信息").setItems(
											attr, null).setNegativeButton("确定", null).show();
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
	 * 返回删除文件后的空文件夹列表
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
	 * 获取文件/文件夹大小
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
	 * 复制功能，startFilePath为文件源路径，desFilePath为目的路径
	 * */
	public boolean copy(String startFilePath, String desFilePath) {
		Log.v(Name, "level1 copy");
		currentLen = 0;
		totaLength = 0;
		this.startFilePath = startFilePath;
		this.desFilePath = desFilePath;

		// 判断是否返回成功的变量
		boolean copyFinished = false;

		File startFile = new File(startFilePath);
		File desFile = new File(desFilePath);

		// 如果源文件是个文件
		if (startFile.isFile()) {
			Log.v(Name, "level2 startFIle.isFile");
			copyFinished = this.copySingleFile(startFile, desFile);

			// 如果源文件是个文件夹，就需要递归复制
		} else {
			Log.v(Name, "level2 !delFile.isFile()");
			// 如果目标文件夹是源文件夹的一个子目录的情况，拒绝复制，因为会造成无限循环
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
	 * 复制单个文件，如果复制多个文件可以递归调用
	 */
	private boolean copySingleFile(File startSingleFile, File desSingleFile) {

		Log.v(Name, "level1 copySingleFile");
		boolean rightCopy = false;

		// -------从源文件中输入内存入byte中，在将byte写入目标文件--------------------
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
			// 刷新缓冲区
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

		// 判断源文件和目标文件大小是否相同，如果相同证明复制成功
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
	 * 递归复制文件夹，因为文件夹下不止一个文件，里面可能有文件或文件夹， 因此需要调用递归方法
	 * 
	 * @param startFolder
	 *            = 需要复制的文件夹
	 * @param desFolder
	 *            = 复制目的地的文件夹
	 * @return = true 表示成功，false 表示失败
	 */

	public boolean copyFolder(File startFolder, File desFolder) {

		Log.v(Name, "level1  copyFolder");
		boolean rightCopy = false;

		rightCopy = this.recursionCopy(startFolder, desFolder);

		return rightCopy;
	}

	/**
	 * 复制文件夹函数，此函数是个递归，会复制文件夹下的所有文件
	 * 
	 * @param recFileFolder
	 *            = 需要拷贝的文件夹或子文件夹
	 * @param recDesFolder
	 *            = 拷贝的目的文件夹或子文件夹，
	 * @return = true表示成功， false表示失败
	 */
	private boolean recursionCopy(File recFileFolder, File recDesFolder) {

		Log.v(Name, "level1 recursionCopy");
		File desFolder = recDesFolder;

		// 因为目的文件夹或子文件夹不存在，需要创建
		desFolder.mkdir();

		// 此为需要拷贝的文件夹下的所有文件列表（其中有文件和文件夹）
		File[] files = recFileFolder.listFiles();

		// 如果是个空文件夹
		if (files.length == 0)
			return true;

		/*
		 * 将文件夹下所有文件放入for循环，如果是文件，那么调用copySingleFile()拷贝文件， 如果是文件夹，那么递归调用此函数
		 */
		for (File thisFile : files) {

			// 如果此文件是个文件，那么直接调用单个文件复制命令复制文件
			if (thisFile.isFile()) {
				// 得到此文件的新位置地址
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());

				boolean rightCopy = this.copySingleFile(thisFile, new File(
						desContentFilePath));

				// 如果复制失败，就跳出循环停止复制
				if (!rightCopy)
					return false;

				// 如果是个文件夹
			} else {

				/*
				 * 此函数是为了得到目的文件夹的地址， 如：源文件夹为：D:/yingzi/text (其中text文件夹下有另一个文件夹
				 * second : D:/yingzi/text/second) 目标位置为：E:/level1/text
				 * 那么此second文件夹在目标地址的位置就是 E:/level1/text/second
				 */
				String desContentFilePath = this.getSubFilePath(startFilePath,
						desFilePath, thisFile.getAbsolutePath());
				// 递归的调用此函数，确保函数都被复制完全
				boolean rightCopy = recursionCopy(thisFile, new File(
						desContentFilePath));
				if (!rightCopy)
					return false;
			}

		}
		return true;
	}

	/**
	 * 此函数是为了得到目的文件夹的地址， 如：源文件夹为：D:/yingzi/text (其中text文件夹下有另一个文件夹 second :
	 * D:/yingzi/text/second) 目标位置为：E:/level1/text 那么此second文件夹在目标地址的位置就是
	 * E:/level1/text/second 此方法中 startFolderPath = D:/yingzi/text (源文件夹) ；
	 * desFolderPath = E:/level1/text (目标位置)； currentFilePath =
	 * D:/yingzi/text/second(需要复制的子文件夹) 返回值为： E:/level1/text/second
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
