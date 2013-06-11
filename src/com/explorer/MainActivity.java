package com.explorer;

import static com.mime.MIME.MIME_MapTable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private TextView currentDir;//������ʾ�ļ��еĶ�Ŀ¼��������Ӧ���ļ��У�Ϊ�½����ļ��н�����Ӧ��Ŀ¼
	private Button btnC;//�ֻ��ڴ� ���ڶ�λ��Ŀ¼
	private Button btnE;//�洢�� ���ڶ�λ��Ŀ¼
	private ListView listView; // ��ʾ��Ӧ���ļ���
	private File rootDir; //��Ŀ¼�ļ���
	private File copyPath; //��ִ�и��ơ�ճ���ȹ���ʱ����ԭ��ַ�洢��copyPath�м�¼
	private String flag;//���ڼ�¼ִ�еĲ������������ơ����е�
	private String startFilePath;//��ִ��ճ���ȹ���ʱ����¼ԭ��ַ
	private String desFilePath;//��ִ��ճ���ȹ���ʱ��¼Ŀ�ĵ�ַ
	private ProgressDialog progressDialog;//���ڸ���ʱ�Ľ�����
	private int currentLen = 0;//���ڼ�¼���Ƶ�ǰ���ļ����У���
	private long totaLength = 0;//���ڼ�¼�ܹ�Ҫ���Ƶ��ļ����У���
	private Handler messageHandler; // ��Ҫ�������̷߳��͵�����, ���ô�����������̸߳���UI.
	private static String Name = "MainActivity";  
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(Name, "level1 onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentDir = (TextView) findViewById(R.id.currentDir);
		// fileName = (TextView) findViewById(R.id.name);
		btnC = (Button) findViewById(R.id.btnC);//�ֻ��ڴ水ť������
		btnE = (Button) findViewById(R.id.btnE);//�洢����ť������
		btnC.setOnClickListener(this);//Ϊ�ֻ��ڴ水ť���ü�����
		btnE.setOnClickListener(this);//Ϊ�洢����ť���ü�����
		listView = (ListView) findViewById(R.id.listView);//�ļ��б������
		listView.setOnItemClickListener(this);//Ϊÿ���ļ����У����ö̰��ļ�����
		listView.setOnItemLongClickListener(this);//Ϊÿ���ļ����У����ó����ļ�����
		//�õ���ǰ�̵߳�Looperʵ�������ڵ�ǰ�߳���UI�߳�Ҳ����ͨ��Looper.getMainLooper()�õ�
		messageHandler = new MessageHandler(Looper.myLooper());
		
		//���ø�Ŀ¼
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//�ж��鼮�Ƿ����SD��
			Log.v(Name, "level2 Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getExternalStorageDirectory();
		} else {
			Log.v(Name, "level2 !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)");
			rootDir = Environment.getRootDirectory();
		}
		loadFiles(rootDir);
	}

	//�Զ���Handler
	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.v(Name, "test initial rootdir" + currentDir.getText().toString());
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
		//������Ǹ�Ŀ¼���򷵻���һ��Ŀ¼
		if(str.equals("��һ��Ŀ¼"))
		{
			Log.v(Name, "level2 notrootDir");
			loadFiles(file);
			return true;
		}
		else//���Ϊ��Ŀ¼�����˳�
		{
			Log.v(Name, "level2 isrootDir");
			return super.onKeyDown(keyCode, event);

		} 
	}



	@Override
	//��ʼ���˵���ֻ���ڵ�һ�γ�ʼ���˵�ʱ����
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Name, "level1 onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Name, "level1 onOptionsItemSelected");
		
		//���ѡ������½��ļ���ѡ��
		if (item.getItemId() == R.id.newFile) {
			Log.v(Name, "level2 item.getItemId() == R.id.newFile");
			LayoutInflater factory = LayoutInflater.from(MainActivity.this);
			final View view = factory.inflate(R.layout.rename, null);
			AlertDialog d = new AlertDialog.Builder(MainActivity.this).setCancelable(true).setMessage("�ļ�����")
					.setView(view).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.v(Name, "level3 onClick");
							String dirName = ((EditText) view.findViewById(R.id.rename)).getText().toString();
							String newFile = currentDir.getText().toString() + "/" + dirName;
							if (new File(newFile).exists()) {
								Log.v(Name, "level 4 File(newFile).exits");
								Toast.makeText(MainActivity.this, "�ļ����Ѵ���", Toast.LENGTH_LONG).show();
								return;
							}
							File f = new File(currentDir.getText().toString(), dirName);
							f.mkdir();
						}
					}).create();
			d.show();
			
			loadFiles(new File(currentDir.getText().toString()));
		} else if (item.getItemId() == R.id.about) {
			Log.v(Name, "level2 item.getItemId() == R.id.about");
			Dialog d = new AlertDialog.Builder(MainActivity.this).setTitle("�ļ������1.0beta").setMessage("������������� ��������")
					.setPositiveButton("ȷ��", null).create();
			d.show();
		} else if (item.getItemId() == R.id.exit) {
			Log.v(Name, "level2 item.getItemId() == R.id.exit");
			MainActivity.this.finish();
		}
		return true;
	}

	/**
	 * ���ص�ǰ�ļ����б�
	 * */
	public void loadFiles(File dir) {
		Log.v(Name, "level1 loadFiles");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//����һ��Map���飬�����洢�ļ����У�����ʾ��Ϣ
		
		//���Ŀ¼��Ϊ�յĶ񻯣�����ʾ��Ӧ���ļ����У���Ϣ
		if (dir != null) {
			Log.v(Name, "level2 dir!= NULL");
			//������Ǹ�Ŀ¼�Ļ�����Ϊ �ϼ�Ŀ¼��ListView�����Ϸ���һ���ӿ�
			if (!dir.getAbsolutePath().equals(rootDir.getAbsolutePath())) {
				Log.v(Name, "level3 !dir.getAbsolutePath().equals(rootDir.getAbsolutePath())");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("file", dir.getParentFile());
				map.put("name", "��һ��Ŀ¼");
				map.put("img", R.drawable.folder);
				list.add(map);
			}
			//������ʾĿ¼
			currentDir.setText(dir.getAbsolutePath());
			File[] files = dir.listFiles();
			sortFiles(files);
			
			//Ϊÿ���ļ����У�����ʾ��׼�����Ƚ���Ϣ�洢����
			
			if (files != null) {
				Log.v(Name, "level2 files!=NULL");
				for (File f : files) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("file", f);
					map.put("name", f.getName());
					map.put("img", f.isDirectory() ? R.drawable.folder
							: (f.getName().toLowerCase().endsWith(".zip") ? R.drawable.zip : R.drawable.text));
					list.add(map);
				}
			}

		} else {//���Ŀ¼����������ʾ����
			Log.v(Name, "level2 Files == NULL");
			Toast.makeText(this, "Ŀ¼����ȷ����������ȷ��Ŀ¼!", Toast.LENGTH_LONG).show();
		}
		//��ʾ�ļ����У���Ϣ
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.item, new String[] { "name", "img" }, new int[] {
				R.id.name, R.id.icon });
		// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
	}

	/**
	 * �����ļ��б�
	 * */
	private void sortFiles(File[] files) {
		Log.v(Name, "level1 sortFiles");
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && file2.isDirectory())
				{
					Log.v(Name, "level2 file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				if (file2.isDirectory())
				{
					Log.v(Name, "level2 !file1.isDirectory() && file2.isDirectory()");
					return 1;
				}
				return -1;
			}
		});
	}

	/**
	 * ���ļ�
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		Log.v(Name, "level1 openFile");
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// ����intent��Action����
		intent.setAction(Intent.ACTION_VIEW);
		// ��ȡ�ļ�file��MIME����
		String type = getMIMEType(file);
		// ����intent��data��Type���ԡ�
		intent.setDataAndType(Uri.fromFile(file),type);
		// ��ת
		startActivity(intent);

	}

	/**
	 * �����ļ���׺����ö�Ӧ��MIME���͡�
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		Log.v(Name, "level1 getMIMEType");
		String type = "*/*";
		String fName = file.getName();
		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			Log.v(Name, "level2 dotIndex<0");
			return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
		{
			Log.v(Name, "level2 end ==  ");
			return type;
		}
		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�
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
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				rootDir = Environment.getExternalStorageDirectory();
				loadFiles(rootDir);
			}
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(Name, "level1 onItemClick");
		//��ȡ�ļ����У�������
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		final File file = (File) map.get("file");
		//������ļ��еĻ����������ļ��У���ʾ���ļ��е�����
		if (file.isDirectory()) {
			Log.v(Name, "level2 file.isDirectory");
			try {
				loadFiles(file);
			} catch (Exception e) {//������Ȩ�޲����������򵯳�����
				Toast.makeText(this, "Ȩ�޲���", Toast.LENGTH_SHORT).show();
			}
		} else {//������ļ�����ѡ����ӦӦ�ô򿪴��ļ�
			openFile(file);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(Name, "level1 onItemLongClick");
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		final File file = (File) map.get("file");
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("����")
				.setItems(new String[] { "����", "����", "ճ��", "����", "������", "ɾ��", "����" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {

								case 0:
									Log.v(Name, "level2 copy");
									copyPath = new File(file.getAbsolutePath());
									flag = "copy";
									break;
								case 1:
									Log.v(Name, "level2 cut");
									copyPath = new File(file.getAbsolutePath());
									flag = "cut";
									break;
								case 2:
									Log.v(Name, "level2 paste");
									final String startPath = copyPath.getAbsolutePath();
									final String desPath = currentDir.getText().toString() + "/" + copyPath.getName();
									File[] files = new File(currentDir.getText().toString()).listFiles();
									for (File file : files) {
										if (copyPath.getName().equals(file.getName())) {
											Toast.makeText(MainActivity.this, "���ļ�/�ļ����Ѵ���", Toast.LENGTH_SHORT).show();
											return;
										}
									}

									int length = (int) (getLength(copyPath) / (1024));
									progressDialog = new ProgressDialog(MainActivity.this);
									progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									progressDialog.setMessage("���ڸ���");
									progressDialog.setMax(length);
									progressDialog.setProgress(0);
									progressDialog.setCancelable(false);
									progressDialog.show();

									new Thread() {
										public void run() {
											copy(startPath, desPath);
											//ͨ��Message������ԭ�̴߳�����Ϣ
											Message message = Message.obtain();
											messageHandler.sendMessage(message);
											progressDialog.dismiss();
											// ���Ϊ������ɾ����Ӧ�ļ�/�ļ���
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
								case 3:
									Log.v(Name, "level2 send");
									break;
								case 4:
									Log.v(Name, "level2 newName");
									LayoutInflater factory = LayoutInflater.from(MainActivity.this);
									final View view = factory.inflate(R.layout.rename, null);
									((EditText) view.findViewById(R.id.rename)).setText(file.getName());
									AlertDialog d = new AlertDialog.Builder(MainActivity.this).setCancelable(true)
											.setMessage("���ļ���").setView(view)
											.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													String newName = ((EditText) view.findViewById(R.id.rename))
															.getText().toString();
													String newFile = currentDir.getText().toString() + "/" + newName;
													if (new File(newFile).exists()) {
														Toast.makeText(MainActivity.this, "�ļ����ظ�", Toast.LENGTH_LONG)
																.show();
														return;
													}
													file.renameTo(new File(newFile));
												}
											}).create();
									d.show();
									loadFiles(new File(currentDir.getText().toString()));
									break;
								case 5:
									Log.v(Name, "level2 delete");
									AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
											.setMessage("ȷʵҪɾ��" + file.getName() + "��?").setCancelable(true)
											.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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

								default:
									break;
								}
								Toast.makeText(MainActivity.this, "" + which, Toast.LENGTH_LONG).show();
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
	 * ����ɾ���ļ���Ŀ��ļ����б�
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

		return ;
	}

	/**
	 * ��ȡ�ļ�/�ļ��д�С
	 * */
	private long getLength(File file) {
		Log.v(Name, "level1 getLength");
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
	 * ���ƹ��ܣ�startFilePathΪ�ļ�Դ·����desFilePathΪĿ��·��
	 * */
	public boolean copy(String startFilePath, String desFilePath) {
		Log.v(Name, "level1 copy");
		currentLen = 0;
		totaLength = 0;
		this.startFilePath = startFilePath;
		this.desFilePath = desFilePath;

		// �ж��Ƿ񷵻سɹ��ı���
		boolean copyFinished = false;

		File startFile = new File(startFilePath);
		File desFile = new File(desFilePath);

		// ���Դ�ļ��Ǹ��ļ�
		if (startFile.isFile()) {
			Log.v(Name,"level2 startFIle.isFile");
			copyFinished = this.copySingleFile(startFile, desFile);

			// ���Դ�ļ��Ǹ��ļ��У�����Ҫ�ݹ鸴��
		} else {
			Log.v(Name, "level2 !delFile.isFile()");
			// ���Ŀ���ļ�����Դ�ļ��е�һ����Ŀ¼��������ܾ����ƣ���Ϊ���������ѭ��
			if (desFilePath.startsWith(startFilePath)) {
				Log.v(Name, "level3 startWith");
				System.out.println("error copy");
				return false;
			} else
			{
				Log.v(Name, "level3 !startWith");
				copyFinished = this.copyFolder(startFile, desFile);
			}
		}
		return copyFinished;
	}

	/**
	 * ���Ƶ����ļ���������ƶ���ļ����Եݹ����
	 */
	private boolean copySingleFile(File startSingleFile, File desSingleFile) {

		Log.v(Name, "level1 copySingleFile");
		boolean rightCopy = false;

		// -------��Դ�ļ��������ڴ���byte�У��ڽ�byteд��Ŀ���ļ�--------------------
		FileInputStream singleFileInputStream = null;
		DataInputStream singleDataInputStream = null;
		FileOutputStream singleFileOutputStream = null;
		DataOutputStream singleDataOutputStream = null;

		try {

			singleFileInputStream = new FileInputStream(startSingleFile);

			singleDataInputStream = new DataInputStream(new BufferedInputStream(singleFileInputStream));

			singleFileOutputStream = new FileOutputStream(desSingleFile);

			singleDataOutputStream = new DataOutputStream(new BufferedOutputStream(singleFileOutputStream));

			byte[] b = new byte[1024];

			int len = -1;
			while ((len = singleDataInputStream.read(b)) != -1) {
				currentLen = currentLen + len;
				singleDataOutputStream.write(b, 0, len);
				progressDialog.setProgress(currentLen / (1024));
			}
			// ˢ�»�����
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

		// �ж�Դ�ļ���Ŀ���ļ���С�Ƿ���ͬ�������֤ͬ�����Ƴɹ�
		if (startSingleFile.length() == desSingleFile.length())
		{
			Log.v(Name, "level2 copy_success");
			rightCopy = true;
		}
		else
		{
			Log.v(Name, "level2 copy_failed");
			rightCopy = false;
		}
		return rightCopy;

	}

	/**
	 * �ݹ鸴���ļ��У���Ϊ�ļ����²�ֹһ���ļ�������������ļ����ļ��У� �����Ҫ���õݹ鷽��
	 * 
	 * @param startFolder
	 *            = ��Ҫ���Ƶ��ļ���
	 * @param desFolder
	 *            = ����Ŀ�ĵص��ļ���
	 * @return = true ��ʾ�ɹ���false ��ʾʧ��
	 */

	public boolean copyFolder(File startFolder, File desFolder) {

		Log.v(Name, "level1  copyFolder");
		boolean rightCopy = false;

		rightCopy = this.recursionCopy(startFolder, desFolder);

		return rightCopy;
	}

	/**
	 * �����ļ��к������˺����Ǹ��ݹ飬�Ḵ���ļ����µ������ļ�
	 * 
	 * @param recFileFolder
	 *            = ��Ҫ�������ļ��л����ļ���
	 * @param recDesFolder
	 *            = ������Ŀ���ļ��л����ļ��У�
	 * @return = true��ʾ�ɹ��� false��ʾʧ��
	 */
	private boolean recursionCopy(File recFileFolder, File recDesFolder) {

		Log.v(Name, "level1 recursionCopy");
		File desFolder = recDesFolder;

		// ��ΪĿ���ļ��л����ļ��в����ڣ���Ҫ����
		desFolder.mkdir();

		// ��Ϊ��Ҫ�������ļ����µ������ļ��б��������ļ����ļ��У�
		File[] files = recFileFolder.listFiles();

		// ����Ǹ����ļ���
		if (files.length == 0)
			return true;

		/*
		 * ���ļ����������ļ�����forѭ����������ļ�����ô����copySingleFile()�����ļ��� ������ļ��У���ô�ݹ���ô˺���
		 */
		for (File thisFile : files) {

			// ������ļ��Ǹ��ļ�����ôֱ�ӵ��õ����ļ�����������ļ�
			if (thisFile.isFile()) {
				// �õ����ļ�����λ�õ�ַ
				String desContentFilePath = this.getSubFilePath(startFilePath, desFilePath, thisFile.getAbsolutePath());

				boolean rightCopy = this.copySingleFile(thisFile, new File(desContentFilePath));

				// �������ʧ�ܣ�������ѭ��ֹͣ����
				if (!rightCopy)
					return false;

				// ����Ǹ��ļ���
			} else {

				/*
				 * �˺�����Ϊ�˵õ�Ŀ���ļ��еĵ�ַ�� �磺Դ�ļ���Ϊ��D:/yingzi/text (����text�ļ���������һ���ļ���
				 * second : D:/yingzi/text/second) Ŀ��λ��Ϊ��E:/level1/text
				 * ��ô��second�ļ�����Ŀ���ַ��λ�þ��� E:/level1/text/second
				 */
				String desContentFilePath = this.getSubFilePath(startFilePath, desFilePath, thisFile.getAbsolutePath());
				// �ݹ�ĵ��ô˺�����ȷ����������������ȫ
				boolean rightCopy = recursionCopy(thisFile, new File(desContentFilePath));
				if (!rightCopy)
					return false;
			}

		}
		return true;
	}

	/**
	 * �˺�����Ϊ�˵õ�Ŀ���ļ��еĵ�ַ�� �磺Դ�ļ���Ϊ��D:/yingzi/text (����text�ļ���������һ���ļ��� second :
	 * D:/yingzi/text/second) Ŀ��λ��Ϊ��E:/level1/text ��ô��second�ļ�����Ŀ���ַ��λ�þ���
	 * E:/level1/text/second �˷����� startFolderPath = D:/yingzi/text (Դ�ļ���) ��
	 * desFolderPath = E:/level1/text (Ŀ��λ��)�� currentFilePath =
	 * D:/yingzi/text/second(��Ҫ���Ƶ����ļ���) ����ֵΪ�� E:/level1/text/second
	 */
	private String getSubFilePath(String startFolderPath, String desFolderPath, String currentFilePath) {

		Log.v(Name, "level1 getSubFilePath");
		
		String currentDesFilePath = null;

		int i = startFolderPath.length();

		// int j = desFolderPath.lastIndexOf("/");

		// String subDirPath = startFolderPath.substring(0, i);
		// String subDesPath = desFolderPath.substring(0, j);

		currentDesFilePath = desFolderPath + "/" + currentFilePath.substring(i + 1);

		return currentDesFilePath;

	}

}
