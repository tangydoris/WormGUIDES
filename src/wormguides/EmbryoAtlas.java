package wormguides;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.wormguides.wormguides.R;
import org.wormguides.embryoatlas10.Camera;
import org.wormguides.embryoatlas10.Arcball;
import org.wormguides.embryoatlas10.GLUTsphere.SolidSphere;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings.TextSize;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;


public class EmbryoAtlas extends Activity { 
	public Handler handler = new Handler();
	public int screenRight;
	public int screenBottom;
	public  TextView dimBox;
	public TextView timeBox;
	public TextView timeBox2;
	public TextView rateBox;
	public TextView tagBox;
	public ImageView infoView;
	public FrameLayout.LayoutParams tagBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
	public FrameLayout.LayoutParams infoViewParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
	public String pickedCellName;

	public GLSurfaceView mGLSurfaceView;
	private TextView shareView;
	private TextView zoomRotatePanView;
	private TextView findEditView;
	private TextView animationView;
	private TextView cellID_axesView;
	private boolean firstCreate = true;
	boolean wholeTimeCourseLoaded;
	boolean splashTimeCourseLoaded;
	public EditText editBox;
	public EditText savableEditBox;

	public  String lastEditString = "";
	public TextView clockBox;
	protected String searchText;
	protected boolean searchThreadProcessing;
	boolean timeTap;
	int tTap;
	public final String PREFS_NAME = "MyPrefsFile";
	static EmbryoAtlas instance;

	Rect clockRect = new Rect();
	HttpClient httpclient;
	HttpGet request;
	HttpResponse response;
	public TextView resultBox;
	TextView result;
	protected float xDown;
	protected float yDown;
	ObjectOutputStream objStream;
	protected String resultString;
	private TextView controlsView;
	public ImageView playFBox;
	Rect playFRect = new Rect();
	public ImageView playRBox;
	Rect playRRect = new Rect();
	float screenDensity;
	public TextView helpBox;
	SharedPreferences settings;
	Rect helpRect = new Rect();
	private final int ID_UP     = 1;
	private final int ID_DOWN   = 2;
	private final int ID_SEARCH = 3;
	private final int ID_INFO   = 4;
	private final int ID_ERASE  = 5;	
	private final int ID_OK     = 6;
	private final int ID_WORMBASE     = 7;
	private final int ID_TEXTPRESSO     = 8;
	private final int ID_WORMATLAS     = 9;
	private final int ID_GOOGLE     = 10;
	private final int ID_FIND     = 11;

	public QuickAction quickAction;
	public QuickAction quickWeb;
	public QuickAction quickAddFind;
	protected boolean longPressInLabelBox;
	protected String pickedPartName;
	protected String savableSearchString;
	protected Spannable formattedLegendText;
	public CheckBox lineageColorBackgroundCB; 
	Hashtable<String, String> legendColorHashTable = new Hashtable<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settings = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS);
		mGLSurfaceView = new TouchSurfaceView(this);
		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
		mGLSurfaceView.setRenderer(TouchSurfaceView.mRenderer);
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mGLSurfaceView.requestRender();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenRight = metrics.widthPixels;
		screenBottom = metrics.heightPixels;
		screenDensity = metrics.scaledDensity;

		ActionItem wormbaseItem 	= new ActionItem(ID_WORMBASE, "Worm\nBase", getResources().getDrawable(R.drawable.wormbaselogo));
		ActionItem textpressoItem 	= new ActionItem(ID_TEXTPRESSO, "Text-\npresso", getResources().getDrawable(R.drawable.textpressologo));
		ActionItem wormatlasItem 	= new ActionItem(ID_WORMATLAS, "Worm\nAtlas", getResources().getDrawable(R.drawable.wormatlaslogo));
		ActionItem googleItem 	= new ActionItem(ID_GOOGLE, "Google", getResources().getDrawable(R.drawable.googlelogo));

		ActionItem webSearchItem 	= new ActionItem(ID_SEARCH, "Web\nSearch", getResources().getDrawable(R.drawable.weblogo));
		ActionItem addLabelItem 	= new ActionItem(ID_FIND, "Add\nLabel", getResources().getDrawable(R.drawable.plusicon));
		ActionItem eraseItem 	= new ActionItem(ID_ERASE, "Erase\nLabels", getResources().getDrawable(R.drawable.menu_eraser));
		ActionItem paintCellsItem 		= new ActionItem(ID_OK, "Paint\nCells", getResources().getDrawable(R.drawable.paintbrushicon));

		//use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
		eraseItem.setSticky(true);
		addLabelItem.setSticky(true);

		quickAction = new QuickAction(this, QuickAction.HORIZONTAL, true);
		quickWeb = new QuickAction(this, QuickAction.HORIZONTAL, false);
		quickAddFind = new QuickAction(this, QuickAction.HORIZONTAL, false);

		//add action items into QuickAction
		quickWeb.addActionItem(wormbaseItem);
		quickWeb.addActionItem(textpressoItem);
		quickWeb.addActionItem(wormatlasItem);
		quickWeb.addActionItem(googleItem);
		quickAction.addActionItem(webSearchItem);
		//        quickAction.addActionItem(infoItem);
		quickAction.addActionItem(addLabelItem);
		quickAction.addActionItem(eraseItem);
		quickAction.addActionItem(paintCellsItem);

		//Set listener for action item clicked
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickAction.getActionItem(pos);

				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_SEARCH) {
					//					Toast.makeText(getApplicationContext(), "Let's do some search action", Toast.LENGTH_SHORT).show();
					quickWeb.show(quickAction.getAnchor(),
							quickAction.getLocation()[0],
							quickAction.getLocation()[1],
							quickAction.nameView.getText().toString().replace("<", "").replace(">", "").replaceAll("#........", "")
							.replaceAll("\n.*","").trim(), quickAction.pickedColor);
				} else if (actionId == ID_FIND) {
					if (editBox.getText().toString().startsWith("Labels...")) {
						editBox.setText("");
						savableEditBox.setText("");
					} else {
						editBox.getText().append(", ");
						savableEditBox.getText().append(", ");
					}
					editBox.setText(editBox.getText().append(quickAction.nameView.getText().toString().replaceAll("\n.*","").trim()
							+quickAction.nameSuffix
							+(quickAction.pickedColor != 0xff000000?" #"+Integer.toHexString(quickAction.pickedColor):"")));
					savableEditBox.setText(savableEditBox.getText().append(quickAction.nameView.getText().toString().replaceAll("\n.*","").trim()
							+quickAction.nameSuffix
							+(quickAction.pickedColor != 0xff000000?" #"+Integer.toHexString(quickAction.pickedColor):"")));
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				} else if (actionId == ID_ERASE) {
					editBox.setText(" ");
					savableEditBox.setText(" ");
					EmbryoAtlas.instance.lastEditString = "";
					((TouchSurfaceView) mGLSurfaceView).runSearch();
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				} else if (actionId == ID_OK) {
					((TouchSurfaceView) mGLSurfaceView).runSearch();
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				} else {
					Toast.makeText(getApplicationContext(), actionItem.getTitle().replaceAll("-?\n", "") + " selected", Toast.LENGTH_SHORT).show();
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				}
			}
		});

		//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
		//by clicking the area outside the dialog.
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
				//				Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
				((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
			}
		});

		quickWeb.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickWeb.getActionItem(pos);

				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_WORMBASE) {
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
					Intent browserIntent = 
							new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wormbase.org/db/get?name=" + quickWeb.nameView.getText().toString().trim() + ";class=Anatomy_term"));
					startActivity(browserIntent);
				} else if (actionId == ID_TEXTPRESSO) {
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
					Intent browserIntent = 
							new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.textpresso.org/cgi-bin/celegans/query?textstring=" + quickWeb.nameView.getText().toString().trim()));
					startActivity(browserIntent);
				} else if (actionId == ID_WORMATLAS) {
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
					Intent browserIntent = 
							new Intent(Intent.ACTION_VIEW, Uri.parse("http://wormatlas.org/search_results.html?cx=016220512202578422943%3Amikvfhp2nri&cof=FORID%3A10&ie=UTF-8&q="
									+ quickWeb.nameView.getText().toString().trim() + "&siteurl=wormatlas.org%252F"));
					startActivity(browserIntent);
				} else if (actionId == ID_GOOGLE) {
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
					Intent browserIntent = 
							new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + quickWeb.nameView.getText().toString().trim()));
					startActivity(browserIntent);
				} else {
					Toast.makeText(getApplicationContext(), actionItem.getTitle().replaceAll("-?\n", "") + " selected", Toast.LENGTH_SHORT).show();
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				}
			}
		});

		quickWeb.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
				//				Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
				((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
			}
		});


		quickAddFind.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickAction.getActionItem(pos);

				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_SEARCH) {
					//					Toast.makeText(getApplicationContext(), "Let's do some search action", Toast.LENGTH_SHORT).show();
					quickAddFind.show(quickAction.getAnchor(),
							quickAction.getLocation()[0],
							quickAction.getLocation()[1],
							quickAction.getName(), quickAction.pickedColor);
				} else if (actionId == ID_FIND) {
					if (editBox.getText().toString().startsWith("Labels...")) {
						editBox.setText("");
						savableEditBox.setText("");
					}
					editBox.setText(editBox.getText()
							.append((editBox.getText().toString() ==""?"":",")+pickedCellName.trim()+"<>"));
					savableEditBox.setText(savableEditBox.getText()
							.append((savableEditBox.getText().toString() ==""?"":",")+pickedCellName.trim()+"<>"));
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				} else {
					Toast.makeText(getApplicationContext(), actionItem.getTitle().replaceAll("-?\n", "") + " selected", Toast.LENGTH_SHORT).show();
					((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
				}
			}
		});

		//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
		//by clicking the area outside the dialog.
		quickAddFind.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
				//				Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
				((ViewGroup)EmbryoAtlas.instance.getInstance().mGLSurfaceView.getParent()).removeView(EmbryoAtlas.instance.infoView);
			}
		});


		clockBox = new TextView(getBaseContext()) {
		};
		clockBox.setBackgroundColor(0x44000000);
		//		clockBox.setBackgroundResource(R.drawable.popup);
		clockBox.setText("");
		addContentView(clockBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams clockBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		clockBoxParams.gravity = Gravity.TOP;
		clockBoxParams.leftMargin = (int) (screenRight-150*screenDensity); //Your X coordinate

		clockBoxParams.topMargin = 65; //Your Y coordinate
		clockBox.setLayoutParams(clockBoxParams);		
		EmbryoAtlas.instance.clockBox.getHitRect(EmbryoAtlas.instance.clockRect);

		timeBox = new TextView(getBaseContext()) {
		};
		timeBox.setText(" < - Time + > ");
		//		timeBox.setBackgroundColor(0x33ffffff);
		timeBox.setBackgroundResource(R.drawable.popup);

		//		addContentView(timeBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBoxParams.gravity = Gravity.TOP;
		timeBoxParams.leftMargin = screenRight/10- timeBox.getWidth()/2; //Your X coordinate
		timeBoxParams.topMargin = 0; //Your Y coordinate
		timeBox.setLayoutParams(timeBoxParams);	
		timeBox.setVisibility(View.INVISIBLE);

		timeBox2 = new TextView(getBaseContext()) {
		};
		timeBox2.setText(" < - Time + > ");
		//		timeBox2.setBackgroundColor(0x33ffffff);
		timeBox2.setBackgroundResource(R.drawable.popup);
		addContentView(timeBox2, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));;
		FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBox2Params.gravity = Gravity.TOP;
		timeBox2Params.leftMargin = screenRight/10- timeBox2.getWidth()/2; //Your X coordinate
		timeBox2Params.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		timeBox2.setLayoutParams(timeBox2Params);		

		tagBox = new TextView(getBaseContext()) {
		};
		tagBox.setText("");
		addContentView(tagBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));;
		FrameLayout.LayoutParams tagBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		tagBoxParams.gravity = Gravity.TOP;
		tagBoxParams.leftMargin = screenRight/10- tagBox.getWidth()/2; //Your X coordinate
		tagBoxParams.topMargin = screenBottom-75; //Your Y coordinate
		tagBox.setLayoutParams(tagBoxParams);		
		
		String[] legendColorLines = ColorLegend_Chisholm.miniLegend.split("\n");
		for (String legendColorLine:legendColorLines) {
			String cellName = legendColorLine.split(",")[0];
			String cellColorCode = legendColorLine.split(",")[1];
			legendColorHashTable.put(cellName, cellColorCode);
		}
		lineageColorBackgroundCB = new CheckBox(getBaseContext());
		lineageColorBackgroundCB.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mGLSurfaceView.onTouchEvent(null);
			}
		});
		lineageColorBackgroundCB.setText("ABa,ABp,P/Z,E,MS,C,D", BufferType.SPANNABLE);
		Spannable s = (Spannable)EmbryoAtlas.instance.lineageColorBackgroundCB.getText();
		String[] cellNames = EmbryoAtlas.instance.lineageColorBackgroundCB.getText().toString().split(",");
		for (String cellName:cellNames) {
			s.setSpan(new ForegroundColorSpan(Color.parseColor(
					"#ff"
							+(Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(3,5))*255/31+4).length()==1?
									"0":"")
									+Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(3,5))*255/31+4)
									+(Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(5,7))*255/63+2).length()==1?
											"0":"")
											+Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(5,7))*255/63+2)
											+(Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(7,9))*255/31+4).length()==1?
													"0":"")
													+Integer.toHexString(parseHexToInt(legendColorHashTable.get(cellName).substring(7,9))*255/31+4)
					)
					), 
					s.toString().indexOf(cellName), 
					s.toString().indexOf(cellName)+cellName.length(), 
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
		}
		addContentView(lineageColorBackgroundCB, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams linColCBParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		linColCBParams.gravity = Gravity.TOP;
		linColCBParams.leftMargin = 70; //Your X coordinate
		linColCBParams.topMargin = 0; //Your Y coordinate
		lineageColorBackgroundCB.setLayoutParams(linColCBParams);		
		lineageColorBackgroundCB.setBackgroundColor(0x44000000);
		
		editBox = new EditText(getBaseContext()) {
			@Override
			public boolean onTouchEvent(MotionEvent e) {
				super.onTouchEvent(e);
				return false;
			}
		};

		savableEditBox = new EditText(getBaseContext()) {
			@Override
			public boolean onTouchEvent(MotionEvent e) {
				super.onTouchEvent(e);
				return false;
			}
		};
		savableEditBox.setEnabled(false);
		savableEditBox.setVisibility(View.INVISIBLE);

		editBox.setText("Labels...");
		savableEditBox.setText("Labels...");
		EmbryoAtlas.instance.savableSearchString = "";
		EmbryoAtlas.instance.longPressInLabelBox = false;
		EmbryoAtlas.instance.editBox.setClickable(true);
		EmbryoAtlas.instance.editBox.setFocusable(true);
		EmbryoAtlas.instance.editBox.setSelected(false);
		editBox.setInputType(InputType.TYPE_CLASS_TEXT
				|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
				|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		savableEditBox.setInputType(InputType.TYPE_CLASS_TEXT
				|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
				|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		addContentView(savableEditBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addContentView(editBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams editBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		editBoxParams.gravity = Gravity.TOP;
		editBoxParams.leftMargin = 70; //Your X coordinate
		editBoxParams.topMargin = 50; //Your Y coordinate
		if (editBox.getWidth() > (int) (screenRight-205*screenDensity)) 
			editBoxParams.width= (int) (screenRight-205*screenDensity);
		editBox.setLayoutParams(editBoxParams);		
		editBox.setBackgroundColor(0x44000000);
		//		editBox.setBackgroundResource(R.drawable.popup);
		editBox.setTextColor(0xffffffff);
		savableEditBox.setLayoutParams(editBoxParams);		
		savableEditBox.setBackgroundColor(0x44000000);
		//		editBox.setBackgroundResource(R.drawable.popup);
		savableEditBox.setTextColor(0xffffffff);

		dimBox = new TextView(getBaseContext()) {
		};
		dimBox.setText("   ^\n   +\n Dim\n   -\n   v");
		//		dimBox.setBackgroundColor(0x33ffffff);
		dimBox.setBackgroundResource(R.drawable.popup);
		addContentView(dimBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams dimBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		dimBoxParams.gravity = Gravity.TOP;
		dimBoxParams.leftMargin = 0; //Your X coordinate
		dimBoxParams.topMargin = (int) (screenBottom*0.37); //Your Y coordinate
		dimBox.setLayoutParams(dimBoxParams);		
		dimBox.setVisibility(View.INVISIBLE);

		rateBox = new TextView(getBaseContext()) {
		};
		//		rateBox.setBackgroundColor(0x33ffffff);
		rateBox.setBackgroundResource(R.drawable.popup);
		rateBox.setPadding(5, 0, 3, 0);
		rateBox.setText("  ^\n  +\nFPS\n  -\n  v");
		addContentView(rateBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams rateBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		rateBoxParams.gravity = Gravity.TOP;
		rateBoxParams.leftMargin = (int) (screenRight - 50 * screenDensity); //Your X coordinate
		rateBoxParams.topMargin = (int) (screenBottom*0.37); //Your Y coordinate
		rateBox.setLayoutParams(rateBoxParams);		
		rateBox.setVisibility(View.INVISIBLE);

		resultBox = new TextView(getBaseContext());
		resultBox.setText("preview loaded...loading full set...");
		addContentView(resultBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams resultBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		resultBoxParams.gravity = Gravity.TOP;
		resultBoxParams.leftMargin = 70; //Your X coordinate
		resultBoxParams.topMargin = (int) (screenBottom-85*screenDensity); //Your Y coordinate
		resultBox.setLayoutParams(resultBoxParams);	

		helpBox = new TextView(getBaseContext()) {
		};
		helpBox.setText("Tap Here\nFor Help");
		//		addContentView(helpBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams helpBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		helpBoxParams.gravity = Gravity.TOP;
		helpBoxParams.leftMargin = (int) (screenRight-130*screenDensity); //Your X coordinate
		helpBoxParams.topMargin = (int) (90*screenDensity); //Your Y coordinate
		helpBox.setLayoutParams(helpBoxParams);		
		helpBox.getHitRect(helpRect);
		helpBox.setVisibility(View.INVISIBLE);

		playFBox = new ImageView(getBaseContext()) {
		};
		playFBox.setImageResource(R.drawable.playstepfwd);
		addContentView(playFBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playFBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playFBoxParams.gravity = Gravity.TOP;
		playFBoxParams.leftMargin = (int) (screenRight-32*screenDensity); //Your X coordinate
		playFBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playFBox.setLayoutParams(playFBoxParams);		
		playFBox.getHitRect(playFRect);
		playFBox.setVisibility(View.VISIBLE);

		playRBox = new ImageView(getBaseContext()) {
		};
		playRBox.setImageResource(R.drawable.playsteprev);
		addContentView(playRBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playRBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playRBoxParams.gravity = Gravity.TOP;
		playRBoxParams.leftMargin = (int) (0); //Your X coordinate
		playRBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playRBox.setLayoutParams(playRBoxParams);		
		playRBox.getHitRect(playRRect);
		playRBox.setVisibility(View.VISIBLE);

		infoView = new ImageView(getInstance());

		infoView.setImageDrawable(getResources().getDrawable(R.drawable.menu_info));


		if (firstCreate ) { 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); 
			firstCreate = false; 
		}
	}
			
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		//		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	    
		menu.add("Help Topics:");
		menu.add("\"Controls, Overview\"");
		menu.add("\"Zoom, Rotate & Pan\"");
		menu.add("\"Find & Re-Color Cells\"");
		menu.add("\"Animation\"");
		menu.add("\"Cell IDs and Axes\"");
		menu.add("\"Share/Collaborate\"");
		menu.add("Help via email...");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String command = (String) item.getTitle() ;
		if (command== "Help via email...") {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
					"mailto","support@wormguides.org", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help with EmbryoAtlas");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));		
		} else {

			ScrollView scroller = new ScrollView(this);
			controlsView = new TextView(this);
			controlsView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					((ScrollView)v.getParent()).setVisibility(View.INVISIBLE);
					((ViewGroup)v.getParent().getParent()).removeView((ScrollView)v.getParent());
					return;
				}
			});
			scroller.addView(controlsView);
			controlsView.setBackgroundColor(0xddffffff);
			controlsView.setClickable(true);
			if (command== "\"Controls, Overview\"" ){
				controlsView.setText("Touchscreen Controls, Overview:\n\n" + 
						"All functions can be accessed by " + 
						"touchscreen gestures on the " + 
						"main view screen.\n\n" + 
						"Touch-Drag the image to rotate.\n" + 
						"Pinch to zoom the image in or out.\n" + 
						"Pinch-Drag to translate the image.\n" +
						"\n" + 
						"Tap a cell to identify or web-search it.\n" + 
						"Press-hold a cell to specify its " + 
						"lineage in the Labels... field.\n" +
						"\n" + 
						"Play buttons and Time sliders control animation.\n" + 
						"Rate slider adjusts animation speed.\n" + 
						"Dim slider fades opacity of unspecified cells.\n" + 
						"Tap the clock/counter to show/hide axes.\n" +
						"\n" + 
						"Type queries in the Labels... field " + 
						"to hilite/color lineages, parts " + 
						"and/or gene-expression patterns.\n" +
						"\n" + 
						"Press-hold the clock/counter to " + 
						"save/share the current scene.");
			}
			if (command== "\"Zoom, Rotate & Pan\""){
				controlsView.setText("Zoom, Rotate & Pan Gestures:\n\n" +
						"Use a 2-finger pinch gesture to zoom out or into the embryo. \n\n" + 
						"Dragging during a pinch gesture pans/translates the model in the plane of the screen.\n\n" +
						"Touch-drag with one finger to rotate the embryo. \nRotation is in an \"arcball\" mode. Drag across the center of the image to roll the embryo around an axis in the plane of the screen. " +
						"Drag around the edges of the image to spin the embryo around the Z axis.\n" +
						"");
			}
			if (command== "\"Find & Re-Color Cells\""){
				controlsView.setText("Find & Re-Color Cells:\n\n" +
						"The Labels... field is where you can specify which cells to show, color differently, or dim to create a customized scene of the embryo model.\n" + 
						"\n" + 
						"If you simply press-hold on a nucleus in the image, its name and special characters for lineage tracing are automatically entered in the Labels... field. You can string together a collection of traced lineages by repeatedly touch-holding different cells from any point in 4D space.\n" +
						"\n" + 
						"To execute a Find, check that the text cursor is active within the Labels... field, then press-hold the screen anywhere outside the Labels... field.\n" +
						"\n" +
						"When the Find is complete, the Labels... field serves as a color legend for the specified cells or lineages.\n" + 
						"All unspecified cells can be faded or revealed using the Dim slider on the left edge of the screen." +
						"\n\n" +
						"Any specified lineage may be re-colored by typing the name of a color after the cell name and special characters, e.g." +
						"\n\t\tABara<> red, Caa> gold, Earp< periwinkle\n" +
						"The < symbol specifies that Find should show all ancestors of the named cell." +
						"The > symbol includes descendent cells in th Find." +
						"\n\n" +
						"To Find using the names/descriptions of cells and organs found in Sulston & White's parts list of larvae/adults, type a word (or word fragment) in the Labels... field, appending -n (name exclusive) or -d (name and desciption) to set the scope of the Find, e.g.\n" +
						"\t\tmuscle-d< blue, AIY-n< tangerine, hyp-n< chartreuse \n" +
						"The distinct queries of a compound Find must be separated by commas or carriage returns.\n" +
						"\n" +
						"To show a gene-expression pattern as recorded in WormBase, type the properly formatted gene name, e.g.\n" +
						"\t\tpha-4 rose, hlh-1> skyblue\n" +
						"Without < or >, only the cells seen to express are shown. \nWith <, ancestors of expressors are also shown.  \nWith >, descendents of expressors are included." +
						"\n\n"  +
						"Finds including genes require an active web connection to complete." +
						"\n" +
						"A compound Find can take several minutes to complete.  Ensure that the device does not sleep during processing of a Find." +
						"\n" +
						"The details of a specified scene are automatically saved, to speed up reloading of the same scene the next time you run EmbryoAtlas.instance." +
						"\n\n" +
						"To return the embryo to the default founder-lineage color scheme with no cells dimmed, erase the text in the Labels... field, then press-hold outside the field to run a blank Find."
						);
			}
			if (command== "\"Animation\""){
				controlsView.setText("Animation:" +
						"\n\n" +
						"Drag the Time slider on either the top or bottom edge of the screen to move manually through time." +
						"\n\n" +
						"Press-hold the PlayFWD> button to begin running animation forward through time." +
						"\n" +
						"Press-hold the Play<REV button to begin time-animation in reverse." +
						"\n\n" +
						"Adjust the frame rate of running animation by moving the Rate slider along the right edge of the screen." +
						"\n\n" +
						"You can dynamically Rotate, Pan and/or Zoom the embryo model while animation is running." + 
						"\n\n"+
						"Tapping the image will stop the running animation.");
			}
			if (command== "\"Cell IDs and Axes\""){
				controlsView.setText("Cell IDs & Axes:" +
						"\n\n" +
						"Tap on a nucleus to see a label showing its lineage name.\n" + 
						"\n" +
						"A single tap will create the label as a web link to the WormBase page for that cell. \n" +
						"Simply tap on the label to see that page in your device's web browser." +
						"\n\n" +
						"Double-tap on the nucleus to generate a web link to a TextPresso search of the cell's name against the C.elegans literature." +
						"\n\n" +
						"Axes:\n" +
						"Tap once on the clock/counter to toggle on or toggle off display of A-P/D-V/R-L axis labels on nuclei in the model." +
						"\n" +
						"Each nucleus is labeled individually to ensure that cells' true orientations are apparent at any zoom or angle of view.");
			}
			if (command== "\"Share/Collaborate\""){
				controlsView.setText("Share & Collaborate:" +
						"\n\n" +
						"Press-hold the clock/counter to share the current scene as an automatically generated web link, e.g.\n\n" +
						"http://wormguides.org/set/muscle-d&lt;+ruby/phar-d&lt;+grass/e&gt;+purple/pha-4&lt;&gt;+tangerine/AI-n&lt;+gold/ABalapap&lt;&gt;/ABarppaa&lt;&gt;/pal-1&lt;&gt;+hotpink/hlh-1&lt;&gt;+blue/ABalpap&lt;&gt;/view/time=175/rX=-0.6518577/rY=0.026162295/rZ=-0.10595421/tX=-17.83722/tY=-40.971558/scale=1.4853133/dim=0.31314474/\n\n" + 
						"When a collaborator or reader clicks the link in a text or email from you, " +
						"EmbryoAtlas will automatically launch and position the model in space/time, " +
						"and replicate any Find or color specifications that you had in effect when you sent the link. " +
						"In this way, you and your audience can share precisely the same viewing experience and insights");
			}
			controlsView.setVisibility(View.VISIBLE);
			addContentView(scroller, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			FrameLayout.LayoutParams scrollerParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
			scrollerParams.gravity = Gravity.TOP;
			scrollerParams.leftMargin = 70; //Your X coordinate
			scrollerParams.topMargin = 100; //Your Y coordinate
			scrollerParams.rightMargin= 70;
			scrollerParams.bottomMargin= 70;
			scroller.setLayoutParams(scrollerParams);
			scroller.setVerticalScrollBarEnabled(true);
		}
		return super.onOptionsItemSelected(item);

	}


	public static EmbryoAtlas getInstance() {
		return instance;
	}

	public static String serialize(Serializable obj, FileOutputStream outputStream)  {
		if (obj == null) return "nope";
		try {
			ObjectOutputStream objStream = new ObjectOutputStream(outputStream);
			objStream.writeObject(obj);
			objStream.close();
			outputStream.close();
			return ("yep");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Object deserialize(FileInputStream inputStream) throws IOException {
		if (inputStream == null) return null;

		try {
			ObjectInputStream objStream = new ObjectInputStream(inputStream);
			try {
				return objStream.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}


	@Override
	protected void onNewIntent (Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	void sendURL(TouchSurfaceView tsv, String sendableSearchString) {
		SharedPreferences.Editor editor = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit();
		String url = "http://fsbill.cam.uchc.edu/wormguides/testurlscript?/set/";
		editor.putString("findString", EmbryoAtlas.instance.savableEditBox.getText().toString());

		url = url + sendableSearchString.replaceAll("\nPainting Cells\\-\\-\\-", "").replaceAll("/", "'").replaceAll("\n+", "/").replaceAll(", *", "/")
				.replaceAll(" +", "+").replace("<", "&lt;").replace(">", "&gt;").replaceAll("#", "%23") + "/";
		editor.putInt("time", TouchSurfaceView.t);
		url = url + "view/";
		url = url + "time=" + TouchSurfaceView.t + "/";
		editor.putFloat("RotX", (float) ((TouchSurfaceView)tsv).arcball.getRotation().getX());
		url = url + "rX=" + (float) ((TouchSurfaceView)tsv).arcball.getRotation().getX() + "/";
		editor.putFloat("RotY", (float) ((TouchSurfaceView)tsv).arcball.getRotation().getY());
		url = url + "rY=" + (float) ((TouchSurfaceView)tsv).arcball.getRotation().getY() + "/";
		editor.putFloat("RotZ", (float) ((TouchSurfaceView)tsv).arcball.getRotation().getZ());
		url = url + "rZ=" + (float) ((TouchSurfaceView)tsv).arcball.getRotation().getZ() + "/";
		editor.putFloat("TranslateX", (float) TouchSurfaceView.mRenderer.mTranslateX);
		url = url + "tX=" + (float) TouchSurfaceView.mRenderer.mTranslateX + "/";
		editor.putFloat("TranslateY", (float) TouchSurfaceView.mRenderer.mTranslateY);
		url = url + "tY=" + (float) TouchSurfaceView.mRenderer.mTranslateY + "/";
		editor.putFloat("ScaleFactor", (float) ((TouchSurfaceView)tsv).newFactor);
		url = url + "scale=" + (float) ((TouchSurfaceView)tsv).newFactor + "/";
		editor.putFloat("DimLevel", (float) TouchSurfaceView.dimLevel);
		url = url + "dim=" + (float) TouchSurfaceView.dimLevel + "/";
		editor.putBoolean("showLineageColors", TouchSurfaceView.mRenderer.showLineageColors);
		url = url + "showLinCol=" + TouchSurfaceView.mRenderer.showLineageColors + "/";
		editor.commit();
		String url2 = url.replace("fsbill.cam.uchc.edu", "scene.wormguides.org");

		Intent intention = new Intent(Intent.ACTION_SEND);
		intention.setType("text/plain");
		intention.putExtra(Intent.EXTRA_SUBJECT, "Sharing a scene from WormGUIDES.");
		intention.putExtra(Intent.EXTRA_TEXT, url2);
		startActivity(Intent.createChooser(intention, "Share a Scene"));
		intention = null;
	}


	@Override
	protected void onStop(){
		super.onStop();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(mGLSurfaceView);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenRight = metrics.widthPixels;
		screenBottom = metrics.heightPixels;

		((ViewGroup)mGLSurfaceView.getParent()).removeView(clockBox);
		addContentView(clockBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams clockBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		clockBoxParams.gravity = Gravity.TOP;
		clockBoxParams.leftMargin = (int) (screenRight-150*screenDensity); //Your X coordinate
		clockBoxParams.topMargin = 65; //Your Y coordinate

		clockBox.setLayoutParams(clockBoxParams);		
		EmbryoAtlas.instance.clockBox.getHitRect(EmbryoAtlas.instance.clockRect);
		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(lineageColorBackgroundCB);
		addContentView(lineageColorBackgroundCB, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams linColCBParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		linColCBParams.gravity = Gravity.TOP;
		linColCBParams.leftMargin = 70; //Your X coordinate
		linColCBParams.topMargin = 0; //Your Y coordinate
		lineageColorBackgroundCB.setLayoutParams(linColCBParams);		
		lineageColorBackgroundCB.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mGLSurfaceView.onTouchEvent(null);
			}
		});
		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(editBox);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(savableEditBox);
		addContentView(savableEditBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addContentView(editBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams editBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		editBoxParams.gravity = Gravity.TOP;
		editBoxParams.leftMargin = 70; //Your X coordinate
		editBoxParams.topMargin = 50; //Your Y coordinate
		if (editBox.getWidth() > (int) (screenRight-205*screenDensity)) 
			editBoxParams.width= (int) (screenRight-205*screenDensity);
		editBox.setLayoutParams(editBoxParams);		
		savableEditBox.setLayoutParams(editBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(timeBox);
		//		addContentView(timeBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBoxParams.gravity = Gravity.TOP;
		timeBoxParams.leftMargin = TouchSurfaceView.t*(8*screenRight/10)/(TouchSurfaceView.times-1)+screenRight/10 - timeBox.getWidth()/2; //Your X coordinate
		timeBoxParams.topMargin = 0; //Your Y coordinate
		timeBox.setLayoutParams(timeBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(timeBox2);
		addContentView(timeBox2, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBox2Params.gravity = Gravity.TOP;
		timeBox2Params.leftMargin = TouchSurfaceView.t*(8*screenRight/10)/(TouchSurfaceView.times-1)+screenRight/10 - timeBox2.getWidth()/2; //Your X coordinate
		timeBox2Params.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		timeBox2.setLayoutParams(timeBox2Params);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(dimBox);
		addContentView(dimBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams dimBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		dimBoxParams.gravity = Gravity.TOP;
		dimBoxParams.leftMargin = 0; //Your X coordinate
		dimBoxParams.topMargin = (int) (screenBottom/10 + (1-TouchSurfaceView.dimLevel)*screenBottom*0.8 - dimBox.getHeight()/2); //Your Y coordinate
		if (dimBoxParams.topMargin > screenBottom*0.82- dimBox.getHeight()/2) 
			dimBoxParams.topMargin = (int) (screenBottom*0.82- dimBox.getHeight()/2); //Your Y coordinate
		dimBox.setLayoutParams(dimBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(rateBox);
		addContentView(rateBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams rateBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		rateBoxParams.gravity = Gravity.TOP;
		rateBoxParams.leftMargin = (int) (screenRight - 50 * screenDensity); //Your X coordinate
		rateBoxParams.topMargin = (int) (screenBottom/10 + (TouchSurfaceView.delay*screenBottom/2000) - rateBox.getHeight()/2); //Your Y coordinate
		rateBox.setLayoutParams(rateBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(resultBox);
		addContentView(resultBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams resultBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		resultBoxParams.gravity = Gravity.TOP;
		resultBoxParams.leftMargin = 70; //Your X coordinate
		resultBoxParams.topMargin = (int) (screenBottom-85*screenDensity); //Your Y coordinate
		resultBox.setLayoutParams(resultBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(tagBox);
		addContentView(tagBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams tagBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		tagBoxParams.gravity = Gravity.TOP;
		tagBoxParams.leftMargin = 70; //Your X coordinate
		tagBoxParams.topMargin = 100; //Your Y coordinate
		tagBox.setText("");
		tagBox.setLayoutParams(tagBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(helpBox);
		//		addContentView(helpBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams helpBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		helpBoxParams.gravity = Gravity.TOP;
		helpBoxParams.leftMargin = (int) (screenRight-130*screenDensity); //Your X coordinate
		helpBoxParams.topMargin = (int) (90 *screenDensity); //Your Y coordinate
		helpBox.setLayoutParams(helpBoxParams);		
		helpBox.getHitRect(helpRect);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(playFBox);
		addContentView(playFBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playFBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playFBoxParams.gravity = Gravity.TOP;
		playFBoxParams.leftMargin = (int) (screenRight-32*screenDensity); //Your X coordinate
		playFBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playFBox.setLayoutParams(playFBoxParams);		
		playFBox.getHitRect(playFRect);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(playRBox);
		addContentView(playRBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playRBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playRBoxParams.gravity = Gravity.TOP;
		playRBoxParams.leftMargin = (int) (0); //Your X coordinate
		playRBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playRBox.setLayoutParams(playRBoxParams);		
		playRBox.getHitRect(playRRect);
	}

	private int parseHexToInt(String hex) {
		int value = 0;
		try {value=Integer.parseInt(hex,16);}
		catch(Exception e) { }
		return value;
	}

	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(mGLSurfaceView);

		mGLSurfaceView.onResume();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenRight = metrics.widthPixels;
		screenBottom = metrics.heightPixels;


		((ViewGroup)mGLSurfaceView.getParent()).removeView(clockBox);
		addContentView(clockBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams clockBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		clockBoxParams.gravity = Gravity.TOP;
		clockBoxParams.leftMargin = (int) (screenRight-150*screenDensity); //Your X coordinate

		clockBoxParams.topMargin = 65; //Your Y coordinate

		clockBox.setLayoutParams(clockBoxParams);		
		EmbryoAtlas.instance.clockBox.getHitRect(EmbryoAtlas.instance.clockRect);
				
		((ViewGroup)mGLSurfaceView.getParent()).removeView(lineageColorBackgroundCB);
		addContentView(lineageColorBackgroundCB, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams linColCBParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		linColCBParams.gravity = Gravity.TOP;
		linColCBParams.leftMargin = 70; //Your X coordinate
		linColCBParams.topMargin = 0; //Your Y coordinate
		lineageColorBackgroundCB.setLayoutParams(linColCBParams);		
		lineageColorBackgroundCB.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mGLSurfaceView.onTouchEvent(null);
			}
		});

		((ViewGroup)mGLSurfaceView.getParent()).removeView(editBox);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(savableEditBox);
		addContentView(savableEditBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addContentView(editBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams editBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		editBoxParams.gravity = Gravity.TOP;
		editBoxParams.leftMargin = 70; //Your X coordinate
		editBoxParams.topMargin = 50; //Your Y coordinate
		if (editBox.getWidth() > screenRight-205*screenDensity) 
			editBoxParams.width= (int) (screenRight-205*screenDensity);
		editBox.setLayoutParams(editBoxParams);		
		savableEditBox.setLayoutParams(editBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(timeBox);
		//		addContentView(timeBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBoxParams.gravity = Gravity.TOP;
		timeBoxParams.leftMargin = TouchSurfaceView.t*(8*screenRight/10)/(TouchSurfaceView.times-1)+screenRight/10 - timeBox.getWidth()/2; //Your X coordinate
		timeBoxParams.topMargin = 0; //Your Y coordinate
		timeBox.setLayoutParams(timeBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(timeBox2);
		addContentView(timeBox2, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		timeBox2Params.gravity = Gravity.TOP;
		timeBox2Params.leftMargin = TouchSurfaceView.t*(8*screenRight/10)/(TouchSurfaceView.times-1)+screenRight/10 - timeBox2.getWidth()/2; //Your X coordinate
		timeBox2Params.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		timeBox2.setLayoutParams(timeBox2Params);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(dimBox);
		addContentView(dimBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams dimBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		dimBoxParams.gravity = Gravity.TOP;
		dimBoxParams.leftMargin = 0; //Your X coordinate
		dimBoxParams.topMargin = (int) (screenBottom/10 + (1-TouchSurfaceView.dimLevel)*screenBottom*0.8 - dimBox.getHeight()/2); //Your Y coordinate
		if (dimBoxParams.topMargin > EmbryoAtlas.instance.screenBottom*0.82- dimBox.getHeight()/2) 
			dimBoxParams.topMargin = (int) (EmbryoAtlas.instance.screenBottom*0.82- dimBox.getHeight()/2); //Your Y coordinate
		dimBox.setLayoutParams(dimBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(rateBox);
		addContentView(rateBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams rateBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		rateBoxParams.gravity = Gravity.TOP;
		rateBoxParams.leftMargin = (int) (screenRight - 50 * screenDensity); //Your X coordinate
		rateBoxParams.topMargin = (int) (screenBottom/10 + (TouchSurfaceView.delay*screenBottom/2000) - rateBox.getHeight()/2); //Your Y coordinate
		rateBox.setLayoutParams(rateBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(resultBox);
		addContentView(resultBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams resultBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		resultBoxParams.gravity = Gravity.TOP;
		resultBoxParams.leftMargin = 70; //Your X coordinate
		resultBoxParams.topMargin = (int) (screenBottom-85*screenDensity); //Your Y coordinate
		resultBox.setLayoutParams(resultBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(tagBox);
		addContentView(tagBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams tagBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		tagBoxParams.gravity = Gravity.TOP;
		tagBoxParams.leftMargin = 70; //Your X coordinate
		tagBoxParams.topMargin = 100; //Your Y coordinate
		tagBox.setText("");
		tagBox.setLayoutParams(tagBoxParams);		
		((ViewGroup)mGLSurfaceView.getParent()).removeView(helpBox);
		//		addContentView(helpBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams helpBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		helpBoxParams.gravity = Gravity.TOP;
		helpBoxParams.leftMargin = (int) (screenRight-130*screenDensity); //Your X coordinate
		helpBoxParams.topMargin = (int) (90 *screenDensity); //Your Y coordinate
		helpBox.setLayoutParams(helpBoxParams);		
		helpBox.getHitRect(helpRect);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(playFBox);
		addContentView(playFBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playFBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playFBoxParams.gravity = Gravity.TOP;
		playFBoxParams.leftMargin = (int) (screenRight-32*screenDensity); //Your X coordinate
		playFBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playFBox.setLayoutParams(playFBoxParams);		
		playFBox.getHitRect(playFRect);
		((ViewGroup)mGLSurfaceView.getParent()).removeView(playRBox);
		addContentView(playRBox, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams playRBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
		playRBoxParams.gravity = Gravity.TOP;
		playRBoxParams.leftMargin = (int) (0); //Your X coordinate
		playRBoxParams.topMargin = (int) (screenBottom-64*screenDensity); //Your Y coordinate
		playRBox.setLayoutParams(playRBoxParams);		
		playRBox.getHitRect(playRRect);

		// Restore preferences

		TouchSurfaceView.tFull = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getInt("time", 0);
		TouchSurfaceView.t = TouchSurfaceView.tFull;
		if (TouchSurfaceView.times == TouchSurfaceView.splashTimes) {
			TouchSurfaceView.t = TouchSurfaceView.tFull*TouchSurfaceView.splashTimes/TouchSurfaceView.fullTimes;
		}
		float rX = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("RotX", 0f);
		float rY = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("RotY", 0f);
		float rZ = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("RotZ", 0f);
		((TouchSurfaceView)mGLSurfaceView).arcball.setRotation(rX,rY,rZ);
		TouchSurfaceView.mRenderer.mTranslateX = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("TranslateX", 0f);
		TouchSurfaceView.mRenderer.mTranslateY = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("TranslateY", 0f);
		((TouchSurfaceView)mGLSurfaceView).lastScaleFactor = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("ScaleFactor", 1f);
		((TouchSurfaceView)mGLSurfaceView).scaleFactor = 1f;
		TouchSurfaceView.dimLevel = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("DimLevel", 0.5f);
		System.out.println("les="+EmbryoAtlas.instance.lastEditString);
		if (EmbryoAtlas.instance.lastEditString == null || EmbryoAtlas.instance.lastEditString.length() < 1)
			EmbryoAtlas.instance.lastEditString = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getString("findString", "");
		System.out.println("les_sp="+EmbryoAtlas.instance.lastEditString);
		if (EmbryoAtlas.instance.lastEditString == null || EmbryoAtlas.instance.lastEditString.length() < 1)
			try {
				EmbryoAtlas.instance.lastEditString = (String)deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
						+"/Android/data/org.wormguides.wormguides/files/lastSearchString.ead"));
				System.out.println("les_ead="+EmbryoAtlas.instance.lastEditString);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		System.out.println("les_final="+EmbryoAtlas.instance.lastEditString);
		searchText = EmbryoAtlas.instance.lastEditString;
		TouchSurfaceView.mRenderer.showLineageColors = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getBoolean("showLineageColors", false);
		lineageColorBackgroundCB.setChecked(TouchSurfaceView.mRenderer.showLineageColors);


		if (getIntent() != null) {
			String view = getIntent().getStringExtra("viewString");
			if (view != null && view != "" ) {
				rX = (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getX();
				rY = (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getY();
				rZ = (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getZ();

				for (String viewChunk:view.split("\\/")){
					if (viewChunk.startsWith("time")) {
						TouchSurfaceView.tFull = Integer.parseInt(viewChunk.split("=")[1]);
						TouchSurfaceView.t = TouchSurfaceView.tFull;
						if (TouchSurfaceView.times == TouchSurfaceView.splashTimes) {
							TouchSurfaceView.t = TouchSurfaceView.tFull*TouchSurfaceView.splashTimes/TouchSurfaceView.fullTimes;						
						}
					}
					if (viewChunk.startsWith("rX"))
						rX = Float.parseFloat(viewChunk.split("=")[1]);
					if (viewChunk.startsWith("rY"))
						rY = Float.parseFloat(viewChunk.split("=")[1]);
					if (viewChunk.startsWith("rZ"))
						rZ = Float.parseFloat(viewChunk.split("=")[1]);
					((TouchSurfaceView)mGLSurfaceView).arcball.setRotation(rX,rY,rZ);
					if (viewChunk.startsWith("tX"))
						TouchSurfaceView.mRenderer.mTranslateX = Float.parseFloat(viewChunk.split("=")[1]);
					if (viewChunk.startsWith("tY"))
						TouchSurfaceView.mRenderer.mTranslateY = Float.parseFloat(viewChunk.split("=")[1]);
					if (viewChunk.startsWith("scale")) {
						((TouchSurfaceView)mGLSurfaceView).lastScaleFactor = Float.parseFloat(viewChunk.split("=")[1]);
						((TouchSurfaceView)mGLSurfaceView).scaleFactor = 1f;
					}
					if (viewChunk.startsWith("dim"))
						TouchSurfaceView.dimLevel = Float.parseFloat(viewChunk.split("=")[1]);
					if (viewChunk.startsWith("showLinCol")) {
						TouchSurfaceView.mRenderer.showLineageColors = viewChunk.split("=")[1].contains("true");
						lineageColorBackgroundCB.setChecked(TouchSurfaceView.mRenderer.showLineageColors);
					}
				}
			}
			if (getIntent().getStringExtra("searchString") != "") {
				searchText = getIntent().getStringExtra("searchString");
				EmbryoAtlas.instance.savableSearchString = searchText;

			}
			setIntent(null);
		}

		
		if (searchText != null && searchText.trim().length()>0) { 
			while (!splashTimeCourseLoaded){

				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (TouchSurfaceView.times == TouchSurfaceView.splashTimes) {
				editBox.setText(searchText);
				savableEditBox.setText(searchText);
				((TouchSurfaceView) mGLSurfaceView).runSearch();
			}
			new Thread(new Runnable() {
				public void run(){
					while (!wholeTimeCourseLoaded){

						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							editBox.setText(searchText);
							savableEditBox.setText(searchText);
							((TouchSurfaceView) mGLSurfaceView).runSearch();
						}
					});
				}
			}).start();
		}

	}


	@Override
	protected void onPause() {
		super.onPause();
		saveSettings();
	}

	void saveSettings() {
		SharedPreferences.Editor editor = getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit();
		if (savableEditBox != null) {
			editor.putString("findString", EmbryoAtlas.instance.savableEditBox.getText().toString());
			try {
				serialize(EmbryoAtlas.instance.savableEditBox.getText().toString(), 
						new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/lastSearchString.ead"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			editor.putInt("time", TouchSurfaceView.t);
			editor.putFloat("RotX", (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getX());
			editor.putFloat("RotY", (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getY());
			editor.putFloat("RotZ", (float) ((TouchSurfaceView)mGLSurfaceView).arcball.getRotation().getZ());
			editor.putFloat("TranslateX", (float) TouchSurfaceView.mRenderer.mTranslateX);
			editor.putFloat("TranslateY", (float) TouchSurfaceView.mRenderer.mTranslateY);
			editor.putFloat("ScaleFactor", (float) ((TouchSurfaceView)mGLSurfaceView).newFactor);
			editor.putFloat("DimLevel", (float) TouchSurfaceView.dimLevel);
			editor.putBoolean("showLineageColors", TouchSurfaceView.mRenderer.showLineageColors);
			
			editor.commit();
		}	
	}


	public  BufferedReader openUrl(String url){

		result = resultBox;

		// Try to connect using Apache HttpClient Library
		try{
			httpclient=new DefaultHttpClient();
			request=new HttpGet(url);
			response = httpclient.execute(request);
		}
		catch (Exception e){
			//Code to handle exception
		}
		String line = "";
		String page = "";

		// response code
		BufferedReader rd = null;
		try{
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			//			while ((line = rd.readLine()) != null) {
			//				page = page + line;
			//			}
		}    
		catch (Exception e){
			//Code to handle exception
		}
		return rd;
	}
}



class TouchSurfaceView extends GLSurfaceView {
	public static int tFull;
	public static int delay = 100;
	private Context context;
	private GestureDetector mGestureDetector;
	Arcball arcball = null;
	protected boolean fling;
	protected float flingX;
	protected float flingY;
	public boolean showAxes;
	public boolean renderingDone = false;
	protected boolean startAnimation;
	Hashtable<String, String> spanColors = new Hashtable<String, String>();

	public TouchSurfaceView(Context context) {
		super(context);
		this.context = context;
		setHapticFeedbackEnabled(true);
		mRenderer = new TouchSurfaceView.SceneRenderer();
		Camera camera = new Camera();
		arcball = new Arcball(camera);
		camera.resetView();
		arcball.getCamera().setOrtho(-1,1,-1,1,-1,1);

		mScaleDetector = new ScaleGestureDetector(context, new OnScaleGestureListener() {
			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
			}
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				lastScaleFactor = newFactor;
				return true;
			}
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.tagBox.setText("");
						EmbryoAtlas.instance.tagBox.clearFocus();
					}
				});
				scaleFactor = detector.getScaleFactor();
				return false;
			}
		});

		mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {


			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return super.onSingleTapUp(e);
			}

			@Override
			public void onLongPress(MotionEvent e) {
				float xf = e.getX();
				float yf = e.getY();
				int width = TouchSurfaceView.this.getWidth();
				int height = TouchSurfaceView.this.getHeight();
				Rect rect = new Rect();
				EmbryoAtlas.instance.editBox.getHitRect(rect);
				if (rect.contains((int)e.getX(), (int)e.getY())) {
					if (EmbryoAtlas.instance.editBox.hasFocus()) {
						EmbryoAtlas.instance.editBox.onTouchEvent(e);
					}else {
						EmbryoAtlas.instance.longPressInLabelBox = true;
						EmbryoAtlas.instance.formattedLegendText = (Spannable)EmbryoAtlas.instance.editBox.getText();
						EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.savableEditBox.getText().toString());
						EmbryoAtlas.instance.editBox.setEnabled(true);
						EmbryoAtlas.instance.editBox.setClickable(true);
						EmbryoAtlas.instance.editBox.setFocusable(true);
						EmbryoAtlas.instance.editBox.requestFocus();
						EmbryoAtlas.instance.editBox.setSelected(true);
						EmbryoAtlas.instance.editBox.onTouchEvent(e);
					}
					return;
				} else if (EmbryoAtlas.instance.editBox.hasFocus()) {
					if (!EmbryoAtlas.instance.editBox.getText().toString().trim().equals(EmbryoAtlas.instance.savableEditBox.getText().toString().trim())){
						EmbryoAtlas.instance.savableEditBox.setText(EmbryoAtlas.instance.editBox.getText().toString().trim());
						EmbryoAtlas.instance.editBox.clearFocus();
						runSearch();
						return;
					} else {
						EmbryoAtlas.instance.savableEditBox.setText(EmbryoAtlas.instance.editBox.getText().toString().trim());
						EmbryoAtlas.instance.editBox.clearFocus();
						runSearch();
						return;
					}
				} 

				else if (mRenderer.isAnimating) {
					mRenderer.setAnimating(false);
					return;
				} else if (EmbryoAtlas.instance.clockRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					String sendableSearchString = EmbryoAtlas.instance.savableEditBox.getText().toString();
					String[] savableLines = EmbryoAtlas.instance.savableSearchString.split(",");
					for (String line:savableLines) {
						String[] savableLineChunks = line.trim().split(" ");

						String complexColorName = "";
						for (int k=1;k<savableLineChunks.length;k++) {
							complexColorName = complexColorName + savableLineChunks[k].trim().toLowerCase();
						}

						if ( mRenderer.bigColorHashTable.get(complexColorName) == null) {
							Enumeration<String> keyEnum = mRenderer.bigColorHashTable.keys();
							while (keyEnum.hasMoreElements()) {
								String name = keyEnum.nextElement(); 
								if (name.toLowerCase().matches(complexColorName +".*")) {
									complexColorName = name.toLowerCase();
									//									break;
								}
								if (name.toLowerCase().matches(".*"+ complexColorName +".*")) {
									complexColorName = name.toLowerCase();
									//									break;
								}

							}
						} 
						if (mRenderer.bigColorHashTable.get(complexColorName) != null) {
							sendableSearchString = sendableSearchString.replace(complexColorName
									, mRenderer.bigColorHashTable.get(complexColorName)
									.toLowerCase().replace("#", "#ff").trim());
						}
					}

					EmbryoAtlas.instance.getInstance().sendURL(TouchSurfaceView.this, sendableSearchString);

				} else if (EmbryoAtlas.instance.helpRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.getInstance().openOptionsMenu();
						}
					});

				} else if (EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY())
						|| EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())){
					mRenderer.setAnimating(true);
					EmbryoAtlas.instance.tTap = (int) (Math.floor(((xf-width/10)/(8*width/10))*(times-1)));
					startAnimation = mRenderer.isAnimating;
					return;
				} else {
					if (mRenderer.isAnimating)
						mRenderer.setAnimating(false);
					else {

						final int x = (int) e.getX();
						final 
						int y = (int) e.getY();
						int w = 1;
						int h = 1;
						//					EmbryoAtlas.instance.editBox.setText("");
						int pickColorInt = mRenderer.readPixel(x, y, w, h, "");
						String pickColorText8888 = Integer.toHexString(pickColorInt);
						String pickColorText565R = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(2,4),16)>>3);
						if (pickColorText565R.length()==1)
							pickColorText565R = "0" +pickColorText565R;
						String pickColorText565G = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(4,6),16)>>2);
						if (pickColorText565G.length()==1)
							pickColorText565G = "0" +pickColorText565G;
						String pickColorText565B = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(6,8),16)>>3);
						if (pickColorText565B.length()==1)
							pickColorText565B = "0" +pickColorText565B;

						final String pickColorText565 = "#ff"
								+pickColorText565R
								+pickColorText565G
								+pickColorText565B;
						if (mRenderer.colorNameHashTable.get(pickColorText565)!=null) {
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									if (EmbryoAtlas.instance.editBox.getText().toString().startsWith("Labels...")) {
										EmbryoAtlas.instance.editBox.setText("");
										EmbryoAtlas.instance.savableEditBox.setText("");
									}
									EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText()
											.append((EmbryoAtlas.instance.editBox.getText().toString().length()==0?"":", ")
													+mRenderer.colorNameHashTable.get(pickColorText565)+"<>"));
									EmbryoAtlas.instance.savableEditBox.setText(EmbryoAtlas.instance.savableEditBox.getText()
											.append((EmbryoAtlas.instance.savableEditBox.getText().toString().length()==0?"":", ")
													+mRenderer.colorNameHashTable.get(pickColorText565)+"<>"));
								}
							});
						} else {
							EmbryoAtlas.instance.tagBox.setText("");
							EmbryoAtlas.instance.tagBox.clearFocus();
						}
						//						if (EmbryoAtlas.instance.editBox.getText().toString() != EmbryoAtlas.instance.lastEditString){
						//							Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
						//							s.setSpan(new StyleSpan(Typeface.NORMAL),
						//									0, 
						//									s.length(), 
						//									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
						//							s.setSpan(new StyleSpan(Typeface.ITALIC),
						//									0, 
						//									s.length(), 
						//									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
						//
						//						}
					}				
				}
			}


			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				super.onShowPress(e);
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				EmbryoAtlas.instance.xDown = e.getX();
				EmbryoAtlas.instance.yDown = e.getY();

				return super.onDown(e);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				float xf = e.getX();
				float yf = e.getY();
				int width = TouchSurfaceView.this.getWidth();
				int height = TouchSurfaceView.this.getHeight();
				// TODO Auto-generated method stub
				if (EmbryoAtlas.instance.helpRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.getInstance().openOptionsMenu();
						}
					});
					return false;

				} else if (mRenderer.isAnimating) {
					mRenderer.setAnimating(false);

				} else if (EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY()) || 
						EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())){
					if (EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY()) || 
							EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())) {
						EmbryoAtlas.instance.tTap = (int) (Math.floor(((xf-width/10)/(8*width/10))*(times-1)));
						mRenderer.setAnimating(true);
					}
					return true;

				} else {

					final int x = (int) e.getX();
					final 
					int y = (int) e.getY();
					int w = 1;
					int h = 1;
					//					EmbryoAtlas.instance.editBox.setText("");
					int pickColorInt = mRenderer.readPixel(x, y, w, h, "");
					String pickColorText8888 = Integer.toHexString(pickColorInt);
					String pickColorText565R = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(2,4),16)>>3);
					if (pickColorText565R.length()==1)
						pickColorText565R = "0" +pickColorText565R;
					String pickColorText565G = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(4,6),16)>>2);
					if (pickColorText565G.length()==1)
						pickColorText565G = "0" +pickColorText565G;
					String pickColorText565B = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(6,8),16)>>3);
					if (pickColorText565B.length()==1)
						pickColorText565B = "0" +pickColorText565B;

					final String pickColorText565 = "#ff"
							+pickColorText565R
							+pickColorText565G
							+pickColorText565B;
					if (mRenderer.colorNameHashTable.get(pickColorText565)!=null) {
						EmbryoAtlas.instance.pickedCellName = mRenderer.colorNameHashTable.get(pickColorText565);
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.infoView.clearFocus();

								EmbryoAtlas.instance.infoViewParams.gravity = Gravity.TOP;
								EmbryoAtlas.instance.infoViewParams.leftMargin = x - EmbryoAtlas.instance.infoView.getWidth()/2; //Your X coordinate
								EmbryoAtlas.instance.infoViewParams.topMargin = y - EmbryoAtlas.instance.infoView.getHeight()/2; //Your Y coordinate
								EmbryoAtlas.instance.infoView.setLayoutParams(EmbryoAtlas.instance.infoViewParams);	
								EmbryoAtlas.instance.getInstance().addContentView(EmbryoAtlas.instance.infoView, EmbryoAtlas.instance.infoViewParams);								
							}
						});
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.quickAction.show(EmbryoAtlas.instance.infoView,x,y, EmbryoAtlas.instance.pickedCellName, EmbryoAtlas.instance.quickAction.pickedColor);
							}
						});
					} else {
						EmbryoAtlas.instance.tagBox.setText("");
						EmbryoAtlas.instance.tagBox.clearFocus();
					}
					//					if (EmbryoAtlas.instance.editBox.getText().toString() != EmbryoAtlas.instance.lastEditString){
					//						Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
					//						s.setSpan(new StyleSpan(Typeface.NORMAL),
					//								0, 
					//								s.length(), 
					//								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
					//						s.setSpan(new StyleSpan(Typeface.ITALIC),
					//								0, 
					//								s.length(), 
					//								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
					//
					//					}
				}
				startAnimation = mRenderer.isAnimating;
				return mRenderer.isAnimating;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				float xf = e.getX();
				float yf = e.getY();
				int width = TouchSurfaceView.this.getWidth();
				int height = TouchSurfaceView.this.getHeight();
				// TODO Auto-generated method stub
				if (EmbryoAtlas.instance.helpRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.getInstance().openOptionsMenu();
						}
					});
					return false;

				} else if (mRenderer.isAnimating) {
					mRenderer.setAnimating(false);
				} else if (EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY()) || 
						EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())){
					EmbryoAtlas.instance.tTap = (int) (Math.floor(((xf-width/10)/(8*width/10))*(times-1)));
					mRenderer.setAnimating(true);
					startAnimation = mRenderer.isAnimating;
					return mRenderer.isAnimating;
				} 
				return (mRenderer.isAnimating);
			}



			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				float xf = e.getX();
				float yf = e.getY();
				int width = TouchSurfaceView.this.getWidth();
				int height = TouchSurfaceView.this.getHeight();
				Rect rect = new Rect();
				EmbryoAtlas.instance.editBox.getHitRect(rect);
				if (rect.contains((int)e.getX(), (int)e.getY())) {
					if (EmbryoAtlas.instance.editBox.hasFocus()) {
						EmbryoAtlas.instance.editBox.onTouchEvent(e);
						return false;
					} else {
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.quickAction.show(EmbryoAtlas.instance.editBox, EmbryoAtlas.instance.editBox.getLeft(), EmbryoAtlas.instance.editBox.getBottom(), "", 0xff000000);
							}
						});
						EmbryoAtlas.instance.longPressInLabelBox = false;
						return false;
					}
				} else if (EmbryoAtlas.instance.editBox.isSelected()){
					EmbryoAtlas.instance.savableSearchString = EmbryoAtlas.instance.editBox.getText().toString();
					EmbryoAtlas.instance.longPressInLabelBox = false;
					EmbryoAtlas.instance.editBox.setEnabled(false);
					EmbryoAtlas.instance.editBox.setClickable(false);
					EmbryoAtlas.instance.editBox.clearFocus();
				}

				if (EmbryoAtlas.instance.clockRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					showAxes = !showAxes;
					int oldT = t;
					if (!mRenderer.isAnimating)
						t++;
					renderingDone = false;
					requestRender();
					while(!renderingDone);
					t=oldT;
					requestRender();

					return false;

				} else if (EmbryoAtlas.instance.helpRect.contains((int)e.getX(), (int)e.getY())){
					// TODO Auto-generated method stub
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.getInstance().openOptionsMenu();
						}
					});
					return false;

				} else if (mRenderer.isAnimating)
					mRenderer.setAnimating(false);
				else if (yf < height/30 || yf > 9*height/10 
						|| EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY())
						|| EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())){
					if (((xf>width/10) && (xf<9*width/10))
							|| EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY())
							|| EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())) {
						EmbryoAtlas.instance.tTap = (int) (Math.floor(((xf-width/10)/(8*width/10))*(times-1)));
						if (EmbryoAtlas.instance.playFRect.contains((int)e.getX(), (int)e.getY())){
							if (t < times-1 )
								t++;
							else 
								t = 0;							
						}
						if (EmbryoAtlas.instance.playRRect.contains((int)e.getX(), (int)e.getY())){
							if (t > 0 )
								t--;
							else 
								t = times-1;							
						}
						final int heightFinal = height;

						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								timeBoxParams.gravity = Gravity.TOP;
								timeBoxParams.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox.getWidth()/2; //Your X coordinate
								timeBoxParams.topMargin = 0; //Your Y coordinate
								EmbryoAtlas.instance.timeBox.setLayoutParams(timeBoxParams);		
								FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								timeBox2Params.gravity = Gravity.TOP;
								timeBox2Params.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox2.getWidth()/2; //Your X coordinate
								timeBox2Params.topMargin = (int) (heightFinal - 35*EmbryoAtlas.instance.screenDensity); //Your Y coordinate
								EmbryoAtlas.instance.timeBox2.setLayoutParams(timeBox2Params);	
							}
						});
						requestRender();

					}
				} else {

					final int x = (int) e.getX();
					final 
					int y = (int) e.getY();
					int w = 1;
					int h = 1;

					final int pickColorInt = mRenderer.readPixel(x, y, w, h, "");
					String pickColorText8888 = Integer.toHexString(pickColorInt);
					String pickColorText565R = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(2,4),16)>>3);
					if (pickColorText565R.length()==1)
						pickColorText565R = "0" +pickColorText565R;
					String pickColorText565G = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(4,6),16)>>2);
					if (pickColorText565G.length()==1)
						pickColorText565G = "0" +pickColorText565G;
					String pickColorText565B = Integer.toHexString(Integer.parseInt(pickColorText8888.substring(6,8),16)>>3);
					if (pickColorText565B.length()==1)
						pickColorText565B = "0" +pickColorText565B;

					final String pickColorText565 = "#ff"
							+pickColorText565R
							+pickColorText565G
							+pickColorText565B;
					if (mRenderer.colorNameHashTable.get(pickColorText565)!=null) {
						EmbryoAtlas.instance.pickedCellName = mRenderer.colorNameHashTable.get(pickColorText565);
						EmbryoAtlas.instance.pickedPartName = mRenderer.cellPartslistHashTable.get(EmbryoAtlas.instance.pickedCellName.toLowerCase().trim());
						//						EmbryoAtlas.instance.pickedPartName = EmbryoAtlas.instance.pickedCellName.toLowerCase();


						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.infoView.clearFocus();

								EmbryoAtlas.instance.infoViewParams.gravity = Gravity.TOP;
								EmbryoAtlas.instance.infoViewParams.leftMargin = x - EmbryoAtlas.instance.infoView.getWidth()/2; //Your X coordinate
								EmbryoAtlas.instance.infoViewParams.topMargin = y - EmbryoAtlas.instance.infoView.getHeight()/2; //Your Y coordinate
								EmbryoAtlas.instance.infoView.setLayoutParams(EmbryoAtlas.instance.infoViewParams);	
								EmbryoAtlas.instance.getInstance().addContentView(EmbryoAtlas.instance.infoView, EmbryoAtlas.instance.infoViewParams);								
							}
						});
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.quickAction.show(EmbryoAtlas.instance.infoView,x,y, 
										EmbryoAtlas.instance.pickedCellName + (EmbryoAtlas.instance.pickedPartName!=null?("\n" + EmbryoAtlas.instance.pickedPartName).replaceAll("_\\d*",""):""), pickColorInt);
							}
						});
					} else {
						EmbryoAtlas.instance.tagBox.setText("");
						EmbryoAtlas.instance.tagBox.clearFocus();
					}
					//					if (EmbryoAtlas.instance.editBox.getText().toString() != EmbryoAtlas.instance.lastEditString){
					//						Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
					//						s.setSpan(new StyleSpan(Typeface.NORMAL),
					//								0, 
					//								s.length(), 
					//								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
					//						s.setSpan(new StyleSpan(Typeface.ITALIC),
					//								0, 
					//								s.length(), 
					//								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
					//
					//					}
				}
				return (mRenderer.isAnimating);
			}


		});

	}

	@Override public boolean onTrackballEvent(MotionEvent e) {
		//		mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
		//		mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
		//		requestRender();
		return true;
	}


	@Override public boolean onTouchEvent(MotionEvent e) {
		if (mRenderer.showLineageColors != EmbryoAtlas.instance.lineageColorBackgroundCB.isChecked()) {
			mRenderer.showLineageColors = EmbryoAtlas.instance.lineageColorBackgroundCB.isChecked();
			requestRender();
		}
		fling = false;
		if (EmbryoAtlas.instance.editBox.getWidth() > (int) (EmbryoAtlas.instance.screenRight-205*EmbryoAtlas.instance.screenDensity)) {
			FrameLayout.LayoutParams editBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
			editBoxParams.gravity = Gravity.TOP;
			editBoxParams.leftMargin = 70; //Your X coordinate
			editBoxParams.topMargin = 50; //Your Y coordinate
			if (EmbryoAtlas.instance.editBox.getWidth() > (int) (EmbryoAtlas.instance.screenRight-205*EmbryoAtlas.instance.screenDensity)) 
				editBoxParams.width= (int) (EmbryoAtlas.instance.screenRight-205*EmbryoAtlas.instance.screenDensity);
			EmbryoAtlas.instance.editBox.setLayoutParams(editBoxParams);		
			EmbryoAtlas.instance.savableEditBox.setLayoutParams(editBoxParams);		
		}

		if (!mRenderer.loaded || e == null)
			return false;
		mGestureDetector.onTouchEvent(e);
		if (startAnimation) {
			startAnimation = false;
			movieThread = new Thread(new Runnable() {
				@Override
				public void run() {
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.tagBox.setText("");
							EmbryoAtlas.instance.tagBox.clearFocus();
							if (EmbryoAtlas.instance.tTap > times/2) {
								EmbryoAtlas.instance.playFBox.setImageResource(R.drawable.pausestepfwd);
							} else {
								EmbryoAtlas.instance.playRBox.setImageResource(R.drawable.pausesteprev);
							}
							EmbryoAtlas.instance.rateBox.setVisibility(View.VISIBLE);
						}
					});

					long time, nextTime = System.currentTimeMillis();
					while (mRenderer.isAnimating) {
						time = System.currentTimeMillis();
						if (time<nextTime) {
							try {
								Thread.sleep((int)(nextTime-time));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else
							Thread.yield();
						nextTime += (long)(delay);
						if (EmbryoAtlas.instance.tTap > times/2) {
							if (t < times-1)
								t++;
							else 
								t = 0;
						}
						if (EmbryoAtlas.instance.tTap <= times/2) {
							if (t > 0)
								t--;
							else 
								t = times-1;
						}
						requestRender();
						int width = getWidth();
						int height = getHeight();
						final int heightFinal = height;
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								timeBoxParams.gravity = Gravity.TOP;
								timeBoxParams.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox.getWidth()/2; //Your X coordinate
								timeBoxParams.topMargin = 0; //Your Y coordinate
								EmbryoAtlas.instance.timeBox.setLayoutParams(timeBoxParams);		
								FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								timeBox2Params.gravity = Gravity.TOP;
								timeBox2Params.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox2.getWidth()/2; //Your X coordinate
								timeBox2Params.topMargin = (int) (heightFinal-35*EmbryoAtlas.instance.screenDensity); //Your Y coordinate
								EmbryoAtlas.instance.timeBox2.setLayoutParams(timeBox2Params);	
							}
						});
					}
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.playFBox.setImageResource(R.drawable.playstepfwd);
							EmbryoAtlas.instance.playRBox.setImageResource(R.drawable.playsteprev);
							EmbryoAtlas.instance.rateBox.setVisibility(View.INVISIBLE);
						}
					});
					Thread.currentThread().interrupt();
				}
			});
			movieThread.setPriority(Thread.MIN_PRIORITY);
			movieThread.start();
		} else {

			mScaleDetector.onTouchEvent(e);

			float x = e.getX();
			float y = e.getY();

			int width = this.getWidth();
			int height = this.getHeight();
			switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.tagBox.setText("");
						EmbryoAtlas.instance.tagBox.clearFocus();
					}
				});
				if (EmbryoAtlas.instance.clockRect.contains((int)x, (int)y) )
					return false;
				if (EmbryoAtlas.instance.yDown > EmbryoAtlas.instance.timeBox.getBottom() && EmbryoAtlas.instance.yDown < EmbryoAtlas.instance.timeBox2.getTop() && EmbryoAtlas.instance.xDown < EmbryoAtlas.instance.rateBox.getLeft() && EmbryoAtlas.instance.xDown > EmbryoAtlas.instance.dimBox.getRight()) {
					float dx = x - mPreviousX;
					float dy = y - mPreviousY;
					float dz = dx+dy;
					if (mScaleDetector.isInProgress()) {
						float pangain = 1.0f; //viewportW/(trackball.getSize().length()*trackball.getScale());
						mRenderer.mTranslateX += dx; 
						mRenderer.mTranslateY += dy; 

					} else {
						//						mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR/3;
						//						mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR/3;
						//						mRenderer.mAngleZ += dz * TOUCH_SCALE_FACTOR/3;
						//						mRenderer.currentAngleX += mRenderer.mAngleX;
						//						mRenderer.currentAngleX = mRenderer.currentAngleX%90;
						//						mRenderer.currentAngleY = mRenderer.currentAngleY%90;
						//						mRenderer.currentAngleZ = mRenderer.currentAngleZ%90;
						//
						float sizex = EmbryoAtlas.instance.screenRight;
						float sizey = EmbryoAtlas.instance.screenBottom;

						float zoomgain = 1.0f;
						float rotategain =1.0f;

						sizey=Math.abs(sizey);
						sizex=Math.abs(sizex);

						double oldx = ( ((2.0*mPreviousX) - sizex ) / sizex );
						double oldy = ( ((2.0*mPreviousY) - sizey ) / sizey );
						double newx = ( ((2.0*x) - sizex ) / sizex );
						double newy = ( ((2.0*y) - sizey ) / sizey );

						arcball.rotate_xy(oldx*rotategain,-oldy*rotategain,newx*rotategain,-newy*rotategain);
					}
				} else if ((EmbryoAtlas.instance.yDown < EmbryoAtlas.instance.timeBox.getBottom()) || EmbryoAtlas.instance.yDown > EmbryoAtlas.instance.timeBox2.getTop() && !EmbryoAtlas.instance.timeTap){
					if ((x>width/10) && (x<9*width/10)) {
						EmbryoAtlas.instance.tagBox.setText("");
						EmbryoAtlas.instance.tagBox.clearFocus();
						final int finalX = (int) x;
						final int heightFinal = height;
						t = (int) (Math.floor(((x-width/10)/(8*width/10))*(times-1)));
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
								timeBoxParams.gravity = Gravity.TOP;
								timeBox2Params.gravity = Gravity.TOP;
								timeBoxParams.leftMargin = finalX- EmbryoAtlas.instance.timeBox.getWidth()/2; //Your X coordinate
								timeBoxParams.topMargin = 0; //Your Y coordinate
								EmbryoAtlas.instance.timeBox.setLayoutParams(timeBoxParams);		
								timeBox2Params.leftMargin = finalX- EmbryoAtlas.instance.timeBox2.getWidth()/2; //Your X coordinate
								timeBox2Params.topMargin = (int) (heightFinal-35*EmbryoAtlas.instance.screenDensity); //Your Y coordinate
								EmbryoAtlas.instance.timeBox2.setLayoutParams(timeBox2Params);		
							}
						});							
						requestRender();

					}
				} else if (EmbryoAtlas.instance.xDown> EmbryoAtlas.instance.rateBox.getLeft()){
					if (y<height/10) 
						y=height/10;
					if (y>9*height/10) 
						y = 9*height/10;
					delay = (int) (((y-height/10)/height)*2000);		
					FrameLayout.LayoutParams rateBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
					rateBoxParams.gravity = Gravity.TOP;
					rateBoxParams.leftMargin = (int) (EmbryoAtlas.instance.screenRight - 40 * EmbryoAtlas.instance.screenDensity); //Your X coordinate
					rateBoxParams.topMargin = (int) (height/10 + (TouchSurfaceView.delay*height/2000) - EmbryoAtlas.instance.rateBox.getHeight()/2); //Your Y coordinate
					EmbryoAtlas.instance.rateBox.setLayoutParams(rateBoxParams);		

				} else if (EmbryoAtlas.instance.xDown< EmbryoAtlas.instance.dimBox.getRight()){
					if (y<height/10) 
						y=height/10;
					if (y>9*height/10) 
						y = 9*height/10;
					dimLevel = (float) (1f-((y-height/10)/(8*height/10)) );
					if (!EmbryoAtlas.instance.searchThreadProcessing){
						if (!EmbryoAtlas.instance.wholeTimeCourseLoaded) {
							if (mRenderer.dimList != null) {
								for (String name:mRenderer.dimList) {
									if (mRenderer.splash_nameCoordsHashTable.get(name) != null) {
										for (int[] i:mRenderer.splash_nameCoordsHashTable.get(name)) {
											for (int j=0;j<mRenderer.stacks/2;j++) {
												mRenderer.alpha.get(i[0])[i[1]][j]= (byte) (dimLevel*254);
											}
										}
									}
								}
							}
						} else {
							if (mRenderer.dimList != null) {
								for (String name:mRenderer.dimList) {
									if (mRenderer.nameCoordsHashTable.get(name) != null) {
										for (int[] i:mRenderer.nameCoordsHashTable.get(name)) {
											for (int j=0;j<mRenderer.stacks/2;j++) {
												mRenderer.alpha.get(i[0])[i[1]][j]= (byte) (dimLevel*254);
											}
										}
									}
								}
							}
						}
						FrameLayout.LayoutParams dimBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
						dimBoxParams.gravity = Gravity.TOP;
						dimBoxParams.leftMargin = 0; //Your X coordinate
						dimBoxParams.topMargin = (int) ((EmbryoAtlas.instance.screenBottom/10 + (1f-TouchSurfaceView.dimLevel)*EmbryoAtlas.instance.screenBottom*0.8 - EmbryoAtlas.instance.dimBox.getHeight()/2)); //Your Y coordinate
						if (dimBoxParams.topMargin > EmbryoAtlas.instance.screenBottom*0.82- EmbryoAtlas.instance.dimBox.getHeight()/2) 
							dimBoxParams.topMargin = (int) (EmbryoAtlas.instance.screenBottom*0.82- EmbryoAtlas.instance.dimBox.getHeight()/2); //Your Y coordinate
						EmbryoAtlas.instance.dimBox.setLayoutParams(dimBoxParams);		
					}
				}
				requestRender();
			}
			EmbryoAtlas.instance.timeTap = false;
			mPreviousX = x;
			mPreviousY = y;
		}
		return true;
	}

	/**
	 * 
	 */
	void runSearch() {
		if (!EmbryoAtlas.instance.editBox.getText().toString().contains("Painting Cells---")
				&& !EmbryoAtlas.instance.editBox.getText().toString().contains("Labels...")){
			EmbryoAtlas.instance.editBox.clearFocus();
			EmbryoAtlas.instance.searchText = EmbryoAtlas.instance.savableEditBox.getText().toString().trim();

			if (EmbryoAtlas.instance.searchText.length() == 0) {
				EmbryoAtlas.instance.editBox.setText("Labels...");
				mRenderer.loaded = false;
				mRenderer.reload(true);
				while (!mRenderer.loaded)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				requestRender();
				EmbryoAtlas.instance.dimBox.setVisibility(View.INVISIBLE);

				return;
			}
			if (EmbryoAtlas.instance.searchText.toLowerCase().contains("reset")) {
				mRenderer.loaded = false;
				mRenderer.reload(true);
				while (!mRenderer.loaded)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				requestRender();
				EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.searchText.replaceAll("(?i)reset", ""));
				return;
			}
			if (EmbryoAtlas.instance.searchText.trim().split("\n").length==1 && EmbryoAtlas.instance.searchText.startsWith("#")) {
				//
			} else {

				new Thread(new Runnable() {
					public void run() {

						////Rebuild loop, to make comparison.
						String[] searchLines = EmbryoAtlas.instance.searchText.split("[\n,]");
						String searchRebuild = EmbryoAtlas.instance.savableEditBox.getText().toString();
						EmbryoAtlas.instance.savableSearchString = searchRebuild;
						getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putString("findString", EmbryoAtlas.instance.savableEditBox.getText().toString()).apply();

						boolean needToSearch = !EmbryoAtlas.instance.lastEditString.toLowerCase().trim().equals(EmbryoAtlas.instance.savableEditBox.getText().toString().toLowerCase().trim())
								&& EmbryoAtlas.instance.wholeTimeCourseLoaded;

						if (needToSearch) {
							mRenderer.fullLoad = true;
							System.out.println("Yep");
						} else {
							System.out.println("Nope");
						}

						//reload on both splash and whole if new search
						if (!EmbryoAtlas.instance.lastEditString.toLowerCase().trim().equals(EmbryoAtlas.instance.savableEditBox.getText().toString().toLowerCase().trim())) {
							mRenderer.reload(false);
						}

						boolean traceBack =  false;
						boolean traceForward =  false;
						boolean part =  false;
						boolean strict =  false;
						boolean selfStripe =  false;

						EmbryoAtlas.instance.searchThreadProcessing = true;

						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								EmbryoAtlas.instance.editBox.setClickable(false);
								EmbryoAtlas.instance.editBox.setEnabled(false);
								EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText().toString()
										.replace("\nPainting Cells---","") + "\nPainting Cells---");
							}
						});
						//						String searchLinesCache = getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getString("searchLinesCache", "");
						//						String[] searchLinesCacheEntries = searchLinesCache.split("\n");
						//						String[] searchLines = EmbryoAtlas.instance.searchText.split("[\n,]");
						String searchConcat = "(";

						String naturalNames = "(";
						boolean[] geneLine = new boolean[searchLines.length];
						//						searchRebuild = "";

						////First loop, to build show and dim lists.
						if (needToSearch) {
							for (int l=0; l<searchLines.length; l++){
								String line = searchLines[l];
								String lineCellEntry = "";
								String lineTraceEntry = "";
								if (line.trim() == "")
									continue;
								searchConcat = searchConcat + "|(";
								geneLine[l] = false;
								traceBack = line.toLowerCase().matches(".*(\\<|trace|back|\\-(d|p)?(s|n)?(d|p)?(b|t|r)(d|p)?(s|n)?(d|p)?).*");
								traceForward = line.toLowerCase().matches(".*(\\>|ahead|forward|\\-(d|p)?(s|n)?(d|p)?(f|a)(d|p)?(s|n)?(d|p)?).*");
								part = line.toLowerCase().matches(".*(desc|part|\\-(b|t|r)?(s|n)?(b|t|r)?(d|p)(b|t|r)?(s|n)?(b|t|r)?).*");
								strict = line.toLowerCase().matches(".*(name|strict|\\-(b|t|r)?(d|p)?(b|t|r)?(s|n)(b|t|r)?(d|p)?(b|t|r)?).*");
								selfStripe = line.contains("$");
								if (strict)
									part = false;
								String lineWithMetas = line.replaceAll("(?<!\\.)\\*", "\\.\\*").trim();
								String lineStripped = lineWithMetas.toLowerCase().replaceAll("(\\$|\\<|\\>|ahead|name|desc|forward|back|part|trace|strict|\\-(d|p)?(s|n)?(d|p)?(b|t)(d|p)?(s|n)?(d|p)?|\\-(b|t|r)?(s|n)?(b|t|r)?(d|p)(b|t|r)?(s|n)?(b|t|r)?|\\-(b|t|r)?(d|p)?(b|t|r)?(s|n)(b|t|r)?(d|p)?(b|t|r)?)", "").trim();
								String[] lineChunks = lineStripped.trim().split(" ");
								String[] lineWithMetaChunks = lineWithMetas.trim().split(" ");							
								String nameWithMetaChunks = lineWithMetas.trim().split(" ")[0];


								////This code handles rebuilding the legend temporarily with colornames and # Codes old and new.
								if (lineChunks.length == 1) {
									String[] lineChunksNew = new String[2];
									lineChunksNew[0] = lineWithMetaChunks[0].trim();
									//								searchRebuild = searchRebuild + (searchRebuild ==""?"":", ") +  lineChunksNew[0];
									if (spanColors != null) {
										if (spanColors.get(nameWithMetaChunks.toLowerCase().trim()) != null) {
											lineChunksNew[1] = spanColors.get(nameWithMetaChunks.toLowerCase().trim()).trim();
											//										searchRebuild = searchRebuild+" "+ lineChunksNew[1];
										}
									}
								} else {
									//								searchRebuild = searchRebuild + (searchRebuild ==""?"":", ") +  line.trim();
								}

								final String finalRebuild = searchRebuild;
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.editBox.setText(finalRebuild+ "\nPainting Cells---");
									}
								});
								////

								if (lineChunks.length >= 1) {
									//								boolean isCached = false;
									//								for (String entry:searchLinesCacheEntries) {
									//									if (entry.startsWith(lineChunks[0].replace("*", "") + (strict?"-n":"") + (part?"-d":"") + (traceBack?"<":"")+(traceForward?">":"")+":")) {
									//										searchConcat = searchConcat + (searchConcat == "("?"":"|") + entry.split(":")[1];
									//										isCached = true;
									//									}
									//								}
									//								if (isCached)
									//									continue;  //on to next searchLine in for loop

									if (traceForward)
										lineChunks[0] = lineChunks[0].trim() + ".*";
									if ( (lineChunks[0].trim().split(" ")[0].toLowerCase().matches("\\D{3,5}-\\d{1,4}(\\.\\*)?")
											|| lineChunks[0].toLowerCase().matches("\\D{1}\\d{1,2}\\D{1,2}\\d{1,2}\\.\\d{1,2}(\\.\\*)?"))) {
										boolean track = lineChunks[0].contains("*");
										BufferedReader pageStream = EmbryoAtlas.instance.openUrl("http://www.wormbase.org/db/get?name="+ lineChunks[0].replace("*", "") + ";class=gene");
										if (pageStream!= null) {
											String firstQueryLine = "";
											String restString = "";
											try {
												while ((firstQueryLine = pageStream.readLine()) != null && restString == "") {
													if (firstQueryLine.contains("wname=\"expression\"")){
														String [] restChunks = pageStream.readLine().split("\"");
														restString= restChunks[1];
													}
												}
												pageStream.close();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											BufferedReader restPageStream = EmbryoAtlas.instance.openUrl("http://www.wormbase.org"+ restString);
											String wbGeneLine = "";
											lineCellEntry = lineChunks[0].replace("*", "") + ":(";
											lineTraceEntry = lineChunks[0].replace("*", "")+ (traceBack?"<":"")+ (traceForward?">":"") + ":(";
											try {
												while ((wbGeneLine = restPageStream.readLine()) != null) {
													String[] wbGeneLineChunks = wbGeneLine.split("[><]");
													if (wbGeneLineChunks.length > 1 && wbGeneLineChunks[1].split("\"").length > 1) {
														for (int i=0; i<wbGeneLineChunks.length; i++) {
															if (wbGeneLineChunks[i].startsWith("a href=\"/species/all/anatomy_term/")) {
																searchConcat = searchConcat + (searchConcat == "("?"":"|") + wbGeneLineChunks[i+1].toLowerCase().trim() + (track?".*":"");
																lineCellEntry = lineCellEntry + (searchConcat.endsWith("(")?"":"|") + wbGeneLineChunks[i+1].toLowerCase().trim();
																lineTraceEntry = lineTraceEntry + (searchConcat.endsWith("(")?"":"|") + wbGeneLineChunks[i+1].toLowerCase().trim() + (track?".*":"");
															}
														}
													}
												}
												pageStream.close();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										if (!mRenderer.fullLoad || pageStream!= null) {
											geneLine[l] = true;
										}
									}
									if (mRenderer.fullLoad &&!geneLine[l] && (part || strict)) {
										Enumeration<String> keyEnum = mRenderer.partslistCellHashTable.keys();
										lineCellEntry = lineChunks[0].replace("*", "") + (strict?"-n":"-d") + ":(";
										lineTraceEntry = lineChunks[0].replace("*", "") + (strict?"-n":"-d") + (traceBack?"<":"") + ":(";
										while (keyEnum.hasMoreElements()) {
											String name = keyEnum.nextElement();
											String strictName = name.split(":")[0];
											if (strict && strictName.matches(".*"+lineChunks[0].trim()+".*")) {
												searchConcat = searchConcat + (searchConcat == "("?"":"|") 
														+ mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
												lineCellEntry = lineCellEntry + (searchConcat.endsWith("(")?"":"|") + mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
												lineTraceEntry = lineTraceEntry + (searchConcat.endsWith("(")?"":"|") + mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
											}
											if (part && name.matches(".*"+lineChunks[0].trim()+".*")) {
												searchConcat = searchConcat + (searchConcat == "("?"":"|") 
														+ mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
												lineCellEntry = lineCellEntry + (searchConcat.endsWith("(")?"":"|") + mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
												lineTraceEntry = lineTraceEntry + (searchConcat.endsWith("(")?"":"|") + mRenderer.partslistCellHashTable.get(name).replace(".", "").replaceAll("[, ]", "|").toLowerCase().trim();
											}

										}
									}
									if (!(geneLine[l] || part || strict))
										searchConcat = searchConcat + (searchConcat == "("?"":"|") + lineChunks[0];
									String theseSearchConcat = searchConcat.substring(searchConcat.lastIndexOf("|(")+2);

									if (traceForward){
										Enumeration<String> keyEnum3 = mRenderer.nameCoordsHashTable.keys();
										while (keyEnum3.hasMoreElements()) {
											String name = keyEnum3.nextElement();
											if (name.toLowerCase().trim().matches(theseSearchConcat)
													&& !(theseSearchConcat.contains(name.toLowerCase().trim()+"|"))) {
												theseSearchConcat =	theseSearchConcat + (searchConcat == "("?"":"|") + name.toLowerCase().trim();										
												searchConcat = searchConcat + (searchConcat == "("?"":"|") + name.toLowerCase().trim();
											}
										}
									}

									if (traceBack){
										for (String thisSearchChunk:theseSearchConcat.replace(".*", "").split("\\|")) {
											//										System.out.println(thisSearchChunk);
											while (mRenderer.nameAncestorHashTable.containsKey(thisSearchChunk.toLowerCase())) {
												String mother = mRenderer.nameAncestorHashTable.get(thisSearchChunk.toLowerCase());
												//											System.out.println(thisSearchChunk +" "+ mother);
												if (!(theseSearchConcat.contains(mother.toLowerCase().trim()+"|"))) {
													theseSearchConcat =	theseSearchConcat + (searchConcat == "("?"":"|") + mother.toLowerCase().trim();										
													searchConcat = searchConcat + (searchConcat == "("?"":"|") + mother.toLowerCase().trim();
												}
												thisSearchChunk = mother;
											}
										}
									}

									if (selfStripe 
											/*|| lineChunks.length ==1 && spanColors.get(lineChunks[0].toLowerCase().replaceFirst("#........", "").trim())== null*/){
										naturalNames = naturalNames + theseSearchConcat;
									}
								}
								searchConcat = searchConcat + ")";
								lineCellEntry = lineCellEntry + ")";
								lineTraceEntry = lineTraceEntry + ")";
								//							searchLinesCache = searchLinesCache + "\n" + lineCellEntry + (lineCellEntry == lineTraceEntry?"":("\n" + lineTraceEntry));
							}
							////End of first loop.
						}
						//						getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putString("searchLinesCache", searchLinesCache).apply();
						searchConcat = searchConcat.replaceFirst("|", "") + ")";
						naturalNames = naturalNames  + ")";
						String[] searchConcatChunks = searchConcat.split("\\)\\|\\(");

						if (needToSearch) {
							if (mRenderer.fullLoad) {
								if (mRenderer.dimList == null) {
									mRenderer.dimList = new ArrayList<String>();
								}
								mRenderer.dimList.clear();
								mRenderer.dimList.addAll(mRenderer.nameCoordsHashTable.keySet());
								Enumeration<ArrayList<int[]>> nameCoordsHashTableElements = mRenderer.nameCoordsHashTable.elements();
								while (nameCoordsHashTableElements.hasMoreElements()) {
									for (int[] i:nameCoordsHashTableElements.nextElement()) {
										for (int j=0;j<mRenderer.stacks/2;j++) {
											mRenderer.alpha.get(i[0])[i[1]][j]= (byte) (dimLevel*255);
										}
									}
								}
								for (String chunk:searchConcatChunks) {
									//								System.out.println(chunk);
									String[] names = chunk.replace("(","").replace(")","").replace("||","").split("[|\\.\\*]");
									for (String name: names) {
										//									System.out.println(name);
										if (mRenderer.nameCoordsHashTable.get(name.trim().toLowerCase()) !=null) {
											//										System.out.println("found name");
											while (mRenderer.dimList.contains(name.trim().toLowerCase()))
												mRenderer.dimList.remove(name.trim().toLowerCase());
											for (int[] i:mRenderer.nameCoordsHashTable.get(name.trim().toLowerCase())) 
												for (int j=0;j<mRenderer.stacks/2;j++)
													mRenderer.alpha.get(i[0])[i[1]][j] = mRenderer.alpha2.get(i[0])[i[1]];
										}
									}
								}
								requestRender();
							}
						}


						////Second loop to color cells and text legend.
						EmbryoAtlas.instance.searchText = EmbryoAtlas.instance.editBox.getText().toString().replace("\nPainting Cells---", "").trim();
						searchLines = EmbryoAtlas.instance.searchText.split("[\n,]");
						for (int l=0; l<searchLines.length; l++){
							String line = searchLines[l];
							final String lineFinal = line;
							final String[] originaLineChunks = line.trim().split(" ");	
							traceBack = line.toLowerCase().matches(".*(\\<|trace|back|\\-(d|p)?(s|n)?(d|p)?(b|t|r)(d|p)?(s|n)?(d|p)?).*");
							traceForward = line.toLowerCase().matches(".*(\\>|ahead|forward|\\-(d|p)?(s|n)?(d|p)?(f|a)(d|p)?(s|n)?(d|p)?).*");
							part = line.toLowerCase().matches(".*(desc|part|\\-(b|t|r)?(s|n)?(b|t|r)?(d|p)(b|t|r)?(s|n)?(b|t|r)?).*");
							strict = line.toLowerCase().matches(".*(name|strict|\\-(b|t|r)?(d|p)?(b|t|r)?(s|n)(b|t|r)?(d|p)?(b|t|r)?).*");
							selfStripe = line.contains("$");
							if (strict)
								part = false;
							line = line.toLowerCase().replaceAll("(?<!\\.)\\*", "\\.\\*").trim();
							line = line.toLowerCase().replaceAll("(\\$|\\<|\\>|ahead|name|desc|forward|back|part|trace|strict|\\-(d|p)?(s|n)?(d|p)?(b|t)(d|p)?(s|n)?(d|p)?|\\-(b|t|r)?(s|n)?(b|t|r)?(d|p)(b|t|r)?(s|n)?(b|t|r)?|\\-(b|t|r)?(d|p)?(b|t|r)?(s|n)(b|t|r)?(d|p)?(b|t|r)?)", "").trim();
							final String[] lineChunks = line.trim().split(" ");	

							if ( (lineChunks[0].trim().split(" ")[0].toLowerCase().matches("\\D{3,5}-\\d{1,4}(\\.\\*)?")
									|| lineChunks[0].toLowerCase().matches("\\D{1}\\d{1,2}\\D{1,2}\\d{1,2}\\.\\d{1,2}(\\.\\*)?"))
									&& (!geneLine[l] && needToSearch) ) {
								continue; //move on to next line
							}

							////Native Colors
							if (lineChunks.length == 1 ) {
								if (!part && !strict && !geneLine[l]){
									if (mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", ""))!=null) {
										EmbryoAtlas.instance.handler.post(new Runnable(){
											public void run(){
												EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText(), BufferType.SPANNABLE);
												Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
												s.setSpan(new ForegroundColorSpan(Color.parseColor(
														"#ff"
																+(Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(3,5))*255/31+4).length()==1?
																		"0":"")
																		+Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(3,5))*255/31+4)
																		+(Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(5,7))*255/63+2).length()==1?
																				"0":"")
																				+Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(5,7))*255/63+2)
																				+(Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(7,9))*255/31+4).length()==1?
																						"0":"")
																						+Integer.toHexString(mRenderer.parseHexToInt(mRenderer.nameColorHashTable.get(lineChunks[0].toLowerCase().replaceAll("[\\.\\*]", "")).substring(7,9))*255/31+4)
														)), 
														s.toString().toLowerCase().indexOf(lineFinal.toLowerCase()), 
														s.toString().toLowerCase().indexOf(lineFinal.toLowerCase())+lineFinal.length(), 
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
											}
										});
									}
								}

								////Custom Colors
							} else {
								String complexColorName = "";
								for (int k=1;k<lineChunks.length;k++)
									complexColorName = complexColorName + lineChunks[k].trim().toLowerCase();


								////Hexcode Colors
								if (lineChunks[lineChunks.length-1].length() ==9 && lineChunks[lineChunks.length-1].startsWith("#")) {
									if (needToSearch) {
										String chunk = searchConcatChunks[l] ;
										//									System.out.println(chunk);
										String[] names = chunk.replace("(","").replace(")","").replace("||","").split("[|\\.\\*]");
										int timeRatio = fullTimes/splashTimes;

										for (String name: names) {
											if (mRenderer.nameCoordsHashTable.get(name) !=null) {
												for (int[] i:mRenderer.nameCoordsHashTable.get(name)) {
													if (!name.matches(naturalNames) && mRenderer.red.get(i[0])[i[1]][0] == mRenderer.red2.get(i[0])[i[1]] && mRenderer.green.get(i[0])[i[1]][0] == mRenderer.green2.get(i[0])[i[1]] && mRenderer.blue.get(i[0])[i[1]][0] == mRenderer.blue2.get(i[0])[i[1]] ) {
														int n=0;
														//															if (name.matches(naturalNames))
														//																n=1;
														for (int j=n;j<5;j++) {
															mRenderer.alpha.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(1,3));
															mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(3,5));
															mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(5,7));
															mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(7,9));
															if (i[0]%(fullTimes/splashTimes) ==0) {
																mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
															}
														} 
													} else 
														if (mRenderer.red.get(i[0])[i[1]][2] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][2] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][2] == mRenderer.blue.get(i[0])[i[1]][0] ) {
															for (int j=2;j<4;j++) {
																mRenderer.alpha.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(1,3));
																mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(3,5));
																mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(5,7));
																mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(7,9));
																if (i[0]%(fullTimes/splashTimes) ==0) {
																	mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																	mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																	mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																	mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																}
															} 
														} else 
															if (mRenderer.red.get(i[0])[i[1]][1] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][1] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][1] == mRenderer.blue.get(i[0])[i[1]][0] ) {
																for (int j=1;j<2;j++) {
																	mRenderer.alpha.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(1,3));
																	mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(3,5));
																	mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(5,7));
																	mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(7,9));
																	if (i[0]%(fullTimes/splashTimes) ==0) {
																		mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																		mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																		mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																		mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																	}
																} 														
															} else 
																if (mRenderer.red.get(i[0])[i[1]][3] == mRenderer.red.get(i[0])[i[1]][2] && mRenderer.green.get(i[0])[i[1]][3] == mRenderer.green.get(i[0])[i[1]][2] && mRenderer.blue.get(i[0])[i[1]][3] == mRenderer.blue.get(i[0])[i[1]][2] ) {
																	for (int j=3;j<4;j++) {
																		mRenderer.alpha.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(1,3));
																		mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(3,5));
																		mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(5,7));
																		mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(7,9));
																		if (i[0]%(fullTimes/splashTimes) ==0) {
																			mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																			mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																			mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																			mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																		}
																	} 															
																} else 
																	if (mRenderer.red.get(i[0])[i[1]][4] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][4] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][4] == mRenderer.blue.get(i[0])[i[1]][0] ) {
																		for (int j=4;j<5;j++) {
																			mRenderer.alpha.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(1,3));
																			mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(3,5));
																			mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(5,7));
																			mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(lineChunks[lineChunks.length-1].trim().toLowerCase().substring(7,9));
																			if (i[0]%(fullTimes/splashTimes) ==0) {
																				mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																				mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																				mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																				mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																			}
																		} 															
																	}
												}
											}
										}
									}
									//										requestRender();

									EmbryoAtlas.instance.handler.post(new Runnable(){
										public void run(){
											String searchText =EmbryoAtlas.instance.editBox.getText().toString().toLowerCase();
											int hexCodeIndex = searchText.indexOf("#");
											EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText().replace(hexCodeIndex, hexCodeIndex+9, ""), 
													BufferType.SPANNABLE);
											searchText =EmbryoAtlas.instance.editBox.getText().toString().toLowerCase();
											String lineFinalName = lineFinal.replaceFirst("#........", "").trim();
											Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
											s.setSpan(new ForegroundColorSpan(Color.parseColor(lineChunks[lineChunks.length-1])), 
													searchText.indexOf(lineFinalName.toLowerCase()), 
													searchText.indexOf(lineFinalName.toLowerCase())+lineFinalName.length(), 
													Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
											spanColors.put(lineFinalName, lineChunks[lineChunks.length-1]);
											//											EmbryoAtlas.instance.resultBox.setText(lineFinalName + " " +spanColors.get(lineFinalName));
										}
									});

									////Named Colors
								} else { //Complex color name
									if ( mRenderer.bigColorHashTable.get(complexColorName) == null) {
										Enumeration<String> keyEnum = mRenderer.bigColorHashTable.keys();
										while (keyEnum.hasMoreElements()) {
											String name = keyEnum.nextElement(); 
											if (name.toLowerCase().matches(complexColorName +".*")) {
												complexColorName = name;
												break;
											}
											if (name.toLowerCase().matches(".*"+ complexColorName +".*")) {
												complexColorName = name;
												break;
											}

										}
									} 
									final String complexColorNameFinal = complexColorName;
									if (mRenderer.bigColorHashTable.get(complexColorNameFinal) != null) {
										if (needToSearch) {
											String chunk = searchConcatChunks[l] ;
											//										System.out.println(chunk);
											String[] names = chunk.replace("(","").replace(")","").replace("||","").split("[|\\.\\*]");
											for (String name: names) {
												if (mRenderer.nameCoordsHashTable.get(name) !=null) {
													for (int[] i:mRenderer.nameCoordsHashTable.get(name)) {
														if (!name.matches(naturalNames) && mRenderer.red.get(i[0])[i[1]][0] == mRenderer.red2.get(i[0])[i[1]] && mRenderer.green.get(i[0])[i[1]][0] == mRenderer.green2.get(i[0])[i[1]] && mRenderer.blue.get(i[0])[i[1]][0] == mRenderer.blue2.get(i[0])[i[1]] ) {
															int n=0;
															for (int j=n;j<5;j++) {
																mRenderer.alpha.get(i[0])[i[1]][j]= (byte) 255;
																mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(1,3));
																mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(3,5));
																mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(5,7));
																if (i[0]%(fullTimes/splashTimes) ==0) {
																	mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																	mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																	mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																	mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																}
															} 
														} else 
															if (mRenderer.red.get(i[0])[i[1]][2] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][2] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][2] == mRenderer.blue.get(i[0])[i[1]][0] ) {
																for (int j=2;j<4;j++) {
																	mRenderer.alpha.get(i[0])[i[1]][j]= (byte) 255;
																	mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(1,3));
																	mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(3,5));
																	mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(5,7));
																	if (i[0]%(fullTimes/splashTimes) ==0) {
																		mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																		mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																		mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																		mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																	}
																} 
															} else 
																if (mRenderer.red.get(i[0])[i[1]][1] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][1] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][1] == mRenderer.blue.get(i[0])[i[1]][0] ) {
																	for (int j=1;j<2;j++) {
																		mRenderer.alpha.get(i[0])[i[1]][j]= (byte) 255;
																		mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(1,3));
																		mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(3,5));
																		mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(5,7));
																		if (i[0]%(fullTimes/splashTimes) ==0) {
																			mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																			mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																			mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																			mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																		}
																	} 														
																} else 
																	if (mRenderer.red.get(i[0])[i[1]][3] == mRenderer.red.get(i[0])[i[1]][2] && mRenderer.green.get(i[0])[i[1]][3] == mRenderer.green.get(i[0])[i[1]][2] && mRenderer.blue.get(i[0])[i[1]][3] == mRenderer.blue.get(i[0])[i[1]][2] ) {
																		for (int j=3;j<4;j++) {
																			mRenderer.alpha.get(i[0])[i[1]][j]= (byte) 255;
																			mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(1,3));
																			mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(3,5));
																			mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(5,7));
																			if (i[0]%(fullTimes/splashTimes) ==0) {
																				mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																				mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																				mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																				mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																			}
																		} 															
																	} else 
																		if (mRenderer.red.get(i[0])[i[1]][4] == mRenderer.red.get(i[0])[i[1]][0] && mRenderer.green.get(i[0])[i[1]][4] == mRenderer.green.get(i[0])[i[1]][0] && mRenderer.blue.get(i[0])[i[1]][4] == mRenderer.blue.get(i[0])[i[1]][0] ) {
																			for (int j=4;j<5;j++) {
																				mRenderer.alpha.get(i[0])[i[1]][j]= (byte) 255;
																				mRenderer.red.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(1,3));
																				mRenderer.green.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(3,5));
																				mRenderer.blue.get(i[0])[i[1]][j]= (byte) mRenderer.parseHexToInt(mRenderer.bigColorHashTable.get(complexColorNameFinal).substring(5,7));
																				if (i[0]%(fullTimes/splashTimes) ==0) {
																					mRenderer.splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.alpha.get(i[0])[i[1]][j];
																					mRenderer.splash_red.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.red.get(i[0])[i[1]][j];
																					mRenderer.splash_green.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.green.get(i[0])[i[1]][j];
																					mRenderer.splash_blue.get(i[0]/(fullTimes/splashTimes))[i[1]][j] = mRenderer.blue.get(i[0])[i[1]][j];
																				}
																			} 															
																		}
													}
												}
											}
										}
										//									requestRender();

										EmbryoAtlas.instance.handler.post(new Runnable(){
											public void run(){
												String lineFinalName = lineFinal.toLowerCase().split(" ")[0].trim();
												EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText()
														.replace(EmbryoAtlas.instance.editBox.getText().toString().indexOf(" "+originaLineChunks[1])
																, EmbryoAtlas.instance.editBox.getText().toString().indexOf(originaLineChunks[1]) + originaLineChunks[1].length()
																, "")
																, BufferType.SPANNABLE);
												EmbryoAtlas.instance.savableEditBox.setText(EmbryoAtlas.instance.savableEditBox.getText()
														.replace(EmbryoAtlas.instance.savableEditBox.getText().toString().indexOf(" "+originaLineChunks[1])
																, EmbryoAtlas.instance.savableEditBox.getText().toString().indexOf(originaLineChunks[1]) + originaLineChunks[1].length()
																, " "+complexColorNameFinal));
												Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
												s.setSpan(new ForegroundColorSpan(Color.parseColor(mRenderer.bigColorHashTable.get(complexColorNameFinal))), 
														s.toString().toLowerCase().indexOf(originaLineChunks[0].toLowerCase()), 
														s.toString().toLowerCase().indexOf(originaLineChunks[0].toLowerCase())+originaLineChunks[0].length(), 
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
												spanColors.put(lineFinalName, complexColorNameFinal);
											}
										});
									}
								}
							} 
						}
						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								if (EmbryoAtlas.instance.editBox.getText().toString().contains("\nPainting"))
									EmbryoAtlas.instance.editBox.setText(EmbryoAtlas.instance.editBox.getText().replace(EmbryoAtlas.instance.editBox.getText().toString().indexOf("\nPainting"),
											EmbryoAtlas.instance.editBox.getText().toString().length(), ""));
								EmbryoAtlas.instance.editBox.setClickable(false);
								EmbryoAtlas.instance.editBox.setEnabled(false);
								EmbryoAtlas.instance.searchThreadProcessing = false;
							}

						});							
						requestRender();

						EmbryoAtlas.instance.handler.post(new Runnable(){
							public void run(){
								if (EmbryoAtlas.instance.savableEditBox.getText().toString() == EmbryoAtlas.instance.lastEditString){
									Spannable s = (Spannable)EmbryoAtlas.instance.editBox.getText();
									s.setSpan(new StyleSpan(Typeface.NORMAL),
											0, 
											s.length(), 
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
									s.setSpan(new StyleSpan(Typeface.BOLD),
											0, 
											s.length(), 
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);											
								}
							}
						});

						if (needToSearch) {
							EmbryoAtlas.instance.lastEditString = EmbryoAtlas.instance.savableEditBox.getText().toString();

							if (mRenderer.fullLoad){
								new Thread(new Runnable() {
									public void run() {
										try {
											boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
											EmbryoAtlas.instance.handler.post(new Runnable(){
												public void run(){
													EmbryoAtlas.instance.saveSettings();
													EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
													getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
												}
											});
											EmbryoAtlas.instance.resultString = (
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_redTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_greenTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blueTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alphaTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead"))+

													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead"))+
													EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead"))
													);
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_redTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_red.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_greenTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_green.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blueTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blue.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alphaTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alpha.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));

											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha.ead"));
											new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead")
											.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));
											EmbryoAtlas.instance.handler.post(new Runnable(){
												public void run(){
													EmbryoAtlas.instance.resultBox.setText("");
												}
											});
											getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
										} catch (FileNotFoundException ex) {
											// TODO Auto-generated catch block
											ex.printStackTrace();
											EmbryoAtlas.instance.handler.post(new Runnable(){
												public void run(){
													EmbryoAtlas.instance.resultBox.setText("");
												}
											});
										}
										EmbryoAtlas.instance.resultString = ("");
									}
								}).start();
							}
							mRenderer.fullLoad = false;
						}
					}
				}).start();
			}
			EmbryoAtlas.instance.dimBox.setVisibility(View.VISIBLE);
		}

	}

	public class SceneRenderer implements GLSurfaceView.Renderer {
		String dataSet = "Chisholm_cell_lineage_20091122_E01_20C_Ventral_1";
		public boolean showLineageColors;
		protected ArrayList<String> dimList = new ArrayList<String>();
		protected boolean loaded;
		public int depth = 500;
		protected boolean isAnimating;
		ArrayList<float[]> x;
		ArrayList<float[]> y;
		ArrayList<float[]> z;
		ArrayList<float[]> r;
		ArrayList<Float> timePoints;
		ArrayList<byte[][]> red;
		ArrayList<byte[][]> green;
		ArrayList<byte[][]> blue;
		ArrayList<byte[][]> alpha;
		ArrayList<byte[]> red2;
		ArrayList<byte[]> green2;
		ArrayList<byte[]> blue2;
		ArrayList<byte[]> alpha2;
		private int grabX;
		private int grabY;
		private int grabW=1;;
		private int grabH=1;
		private boolean grabPixels;
		private String subColorString;
		Hashtable<String, ArrayList<int[]>> nameCoordsHashTable;
		Hashtable<String, ArrayList<int[]>> splash_nameCoordsHashTable;
		Hashtable<String, String> bigColorHashTable;
		Hashtable<String, String> partslistCellHashTable;
		Hashtable<String, String> cellPartslistHashTable;
		Hashtable<String, String> colorNameHashTable;
		Hashtable<String, String> nameColorHashTable;
		private boolean flatRender;
		private GLUTsphere gLUTsphere;
		private SolidSphere[] sph;
		private int lastT;
		private EGLConfig config;
		int stacks = 10;
		public float mAngleX;
		public float mAngleY;
		public float mAngleZ;
		public float currentAngleX;
		public float currentAngleY;
		public float currentAngleZ;
		public float mTranslateX;
		public float mTranslateY;
		public int pixel;
		boolean fullLoad;
		private ArrayList<float[]> splash_x;
		private ArrayList<float[]> splash_y;
		private ArrayList<float[]> splash_z;
		private ArrayList<float[]> splash_r;
		private ArrayList<Float> splash_timePoints;
		private ArrayList<byte[][]> splash_red;
		private ArrayList<byte[][]> splash_green;
		private ArrayList<byte[][]> splash_blue;
		private ArrayList<byte[][]> splash_alpha;
		private ArrayList<byte[]> splash_red2;
		private ArrayList<byte[]> splash_green2;
		private ArrayList<byte[]> splash_blue2;
		private ArrayList<byte[]> splash_alpha2;
		private ArrayList<float[]> whole_x;
		private ArrayList<float[]> whole_y;
		private ArrayList<float[]> whole_z;
		private ArrayList<float[]> whole_r;
		private ArrayList<Float> whole_timePoints;
		private ArrayList<byte[][]> whole_red;
		private ArrayList<byte[][]> whole_green;
		private ArrayList<byte[][]> whole_blue;
		private ArrayList<byte[][]> whole_alpha;
		private ArrayList<byte[]> whole_red2;
		private ArrayList<byte[]> whole_green2;
		private ArrayList<byte[]> whole_blue2;
		private ArrayList<byte[]> whole_alpha2;
		private Hashtable<String, ArrayList<int[]>> whole_nameCoordsHashTable;
//		private float xOffset = -4.2f;  //for ZD set, 
//		private float yOffset = -3.5f;  //for ZD set, 
//		private float zOffset = -1.65f;  //for ZD set, 
		private float xOffset = -3.8f;  //for Chisholm sets,
		private float yOffset = -1.8f;  //for Chisholm sets,
		private float zOffset = -2.0f;  //for Chisholm sets,
		private float zRotOffset = 0;
		private Hashtable<String, String> nameAncestorHashTable;  



		public SceneRenderer() {
			flatRender = false;
			grabPixels = false;
			x = new ArrayList<float[]>();
			y = new ArrayList<float[]>();
			z = new ArrayList<float[]>();
			r = new ArrayList<float[]>();
			timePoints = new ArrayList<Float>();
			red = new ArrayList<byte[][]>();
			green = new ArrayList<byte[][]>();
			blue = new ArrayList<byte[][]>();
			alpha = new ArrayList<byte[][]>();
			red2 = new ArrayList<byte[]>();
			green2 = new ArrayList<byte[]>();
			blue2 = new ArrayList<byte[]>();
			alpha2 = new ArrayList<byte[]>();
			bigColorHashTable = new Hashtable<String, String>();
			partslistCellHashTable = new Hashtable<String, String>();
			cellPartslistHashTable = new Hashtable<String, String>();

			colorNameHashTable = new Hashtable<String, String>();
			nameColorHashTable = new Hashtable<String, String>();
			nameCoordsHashTable = new Hashtable<String, ArrayList<int[]>>();
			nameAncestorHashTable = new Hashtable<String, String>();
			splash_x = new ArrayList<float[]>();
			splash_y = new ArrayList<float[]>();
			splash_z = new ArrayList<float[]>();
			splash_r = new ArrayList<float[]>();
			splash_timePoints = new ArrayList<Float>();
			splash_red = new ArrayList<byte[][]>();
			splash_green = new ArrayList<byte[][]>();
			splash_blue = new ArrayList<byte[][]>();
			splash_alpha = new ArrayList<byte[][]>();
			splash_red2 = new ArrayList<byte[]>();
			splash_green2 = new ArrayList<byte[]>();
			splash_blue2 = new ArrayList<byte[]>();
			splash_alpha2 = new ArrayList<byte[]>();
			splash_nameCoordsHashTable = new Hashtable<String, ArrayList<int[]>>();
			whole_x = new ArrayList<float[]>();
			whole_y = new ArrayList<float[]>();
			whole_z = new ArrayList<float[]>();
			whole_r = new ArrayList<float[]>();
			whole_timePoints = new ArrayList<Float>();
			whole_red = new ArrayList<byte[][]>();
			whole_green = new ArrayList<byte[][]>();
			whole_blue = new ArrayList<byte[][]>();
			whole_alpha = new ArrayList<byte[][]>();
			whole_red2 = new ArrayList<byte[]>();
			whole_green2 = new ArrayList<byte[]>();
			whole_blue2 = new ArrayList<byte[]>();
			whole_alpha2 = new ArrayList<byte[]>();
			whole_nameCoordsHashTable = new Hashtable<String, ArrayList<int[]>>();

			fullLoad = false;

			try {
				String savedVersionString = (String) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
						+"/Android/data/org.wormguides.wormguides/files/savedVersionString.ead"));
				if (savedVersionString == null || !savedVersionString.contains("version_1.1.37")) {
					fullLoad = true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fullLoad = true;
			}
			
			
			openSplashScene();

			EmbryoAtlas.instance.splashTimeCourseLoaded = true;
			EmbryoAtlas.instance.wholeTimeCourseLoaded = false;

			EmbryoAtlas.instance.handler.post(new Runnable(){
				public void run(){
					EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
				}
			});

			new Thread(new Runnable() {
				public void run() {
					openFullTimecourse();
				}
			}).start();
		}

		private void openSplashScene() {
			EmbryoAtlas.instance.splashTimeCourseLoaded = false;
			EmbryoAtlas.instance.wholeTimeCourseLoaded = false;
			times = splashTimes;
			int splashTime = 0;
			if (!fullLoad) {
				try {
					splash_nameCoordsHashTable = (Hashtable<String, ArrayList<int[]>>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_nameCoordsHashTable.ead"));
					if (splash_nameCoordsHashTable == null || splash_nameCoordsHashTable.size() == 0) {
						fullLoad = true;
						splash_nameCoordsHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					bigColorHashTable = (Hashtable<String, String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/bigColorHashTable.ead"));
					if (bigColorHashTable == null || bigColorHashTable.size() == 0) {
						fullLoad = true;
						bigColorHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					colorNameHashTable = (Hashtable<String, String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/colorNameHashTable.ead"));
					if (colorNameHashTable == null || colorNameHashTable.size() == 0) {
						fullLoad = true;
						colorNameHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					nameColorHashTable = (Hashtable<String, String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/nameColorHashTable.ead"));
					if (nameColorHashTable == null || nameColorHashTable.size() == 0) {
						fullLoad = true;
						nameColorHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					partslistCellHashTable = (Hashtable<String, String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/partslistCellHashTable.ead"));
					if (partslistCellHashTable == null || partslistCellHashTable.size() == 0) {
						fullLoad = true;
						partslistCellHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					cellPartslistHashTable = (Hashtable<String, String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/cellPartslistHashTable.ead"));
					if (cellPartslistHashTable == null || cellPartslistHashTable.size() == 0) {
						fullLoad = true;
						cellPartslistHashTable = null;
					} else {
						Enumeration<String> nameEnum = cellPartslistHashTable.keys();
						while (nameEnum.hasMoreElements()) {
							String thisName = nameEnum.nextElement();
							for (int ln=1; ln<=thisName.length(); ln++) {
								String mother = thisName.substring(0, ln-1);
								String daughter = thisName.substring(0, ln);
								nameAncestorHashTable.put(daughter, mother);
								//								System.out.println(daughter +" "+ mother);
							}
						}
						nameAncestorHashTable.put("c", "P2");
						nameAncestorHashTable.put("d", "P3");
						nameAncestorHashTable.put("e", "EMS");
						nameAncestorHashTable.put("ms", "EMS");
						nameAncestorHashTable.put("p4", "P3");
						nameAncestorHashTable.put("p3", "P2");
						nameAncestorHashTable.put("ems", "P1");
						nameAncestorHashTable.put("p2", "P1");
						nameAncestorHashTable.put("p1", "P0");
						nameAncestorHashTable.put("a", "P0");
						nameAncestorHashTable.remove("a");
						nameAncestorHashTable.remove("em");
						nameAncestorHashTable.remove("m");
						nameAncestorHashTable.remove("p");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_x = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_x.ead"));
					if (splash_x == null || splash_x.size() == 0) {
						fullLoad = true;
						splash_x = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_y = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_y.ead"));
					if (splash_y == null || splash_y.size() == 0) {
						fullLoad = true;
						splash_y = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_z = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_z.ead"));
					if (splash_z == null || splash_z.size() == 0) {
						fullLoad = true;
						splash_z = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_r = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_r.ead"));
					if (splash_r == null || splash_r.size() == 0) {
						fullLoad = true;
						splash_r = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_timePoints = (ArrayList<Float>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_timePoints.ead"));
					if (splash_timePoints == null || splash_timePoints.size() == 0) {
						fullLoad = true;
						splash_timePoints = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_red = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_red.ead"));
					if (splash_red == null || splash_red.size() == 0) {
						fullLoad = true;
						splash_red = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_green = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_green.ead"));
					if (splash_green == null || splash_green.size() == 0) {
						fullLoad = true;
						splash_green = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_blue = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_blue.ead"));
					if (splash_blue == null || splash_blue.size() == 0) {
						fullLoad = true;
						splash_blue = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_alpha = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_alpha.ead"));
					if (splash_alpha == null || splash_alpha.size() == 0) {
						fullLoad = true;
						splash_alpha = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_red2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_red2.ead"));
					if (splash_red2 == null || splash_red2.size() == 0) {
						fullLoad = true;
						splash_red2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_green2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_green2.ead"));
					if (splash_green2 == null || splash_green2.size() == 0) {
						fullLoad = true;
						splash_green2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_blue2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_blue2.ead"));
					if (splash_blue2 == null || splash_blue2.size() == 0) {
						fullLoad = true;
						splash_blue2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					splash_alpha2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/splash_alpha2.ead"));
					if (splash_alpha2 == null || splash_alpha2.size() == 0) {
						fullLoad = true;
						splash_alpha2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					dimList = (ArrayList<String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));
					if (dimList == null) {
						fullLoad = true;
						dimList = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
			}

			loaded = true;
			if (fullLoad) {
				if (bigColorHashTable == null || bigColorHashTable.size() ==0) {
					bigColorHashTable = new Hashtable<String, String>();
					String[] bigColorArray = (X11ColorsAndMore.colorNameCodes +"\n"+ X11ColorsAndMore.moreColorNameCodes).split("\n");
					for (String colorName:bigColorArray) {
						String[] chunks = colorName.split(",");
						bigColorHashTable.put(chunks[0].toLowerCase(), chunks[1].toUpperCase());
					}
					X11ColorsAndMore.colorNameCodes ="";
					X11ColorsAndMore.colorNameCodes = null;
					X11ColorsAndMore.moreColorNameCodes ="";
					X11ColorsAndMore.moreColorNameCodes =null;
					Arrays.fill(bigColorArray, "");
					bigColorArray = null;
					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(bigColorHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/bigColorHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}					


				if (partslistCellHashTable == null || partslistCellHashTable.size() ==0
						|| cellPartslistHashTable == null || cellPartslistHashTable.size() ==0) {
					partslistCellHashTable = new Hashtable<String, String>();
					cellPartslistHashTable = new Hashtable<String, String>();
					String[] partslistArray = PartsList.partslist.split("\n");
					int tick =0;
					for (String partsline:partslistArray) {
						String[] chunks = partsline.split("\t");
						if (chunks.length == 3) {
							partslistCellHashTable.put(chunks[0].toLowerCase() +":"+ chunks[2].toLowerCase()+"_"+tick, chunks[1]+".");
							cellPartslistHashTable.put(chunks[1].toLowerCase().trim().replace(".",""), chunks[0] +":"+ chunks[2]+"_"+tick);
						}
						tick++;
					}
					PartsList.partslist="";
					PartsList.partslist=null;
					Arrays.fill(partslistArray, "");			
					partslistArray = null;
					Enumeration<String> nameEnum = cellPartslistHashTable.keys();
					while (nameEnum.hasMoreElements()) {
						String thisName = nameEnum.nextElement();
						for (int ln=1; ln<=thisName.length(); ln++) {
							String mother = thisName.substring(0, ln-1);
							String daughter = thisName.substring(0, ln);
							nameAncestorHashTable.put(daughter, mother);
							//							System.out.println(daughter +" "+ mother);
						}
					}
					nameAncestorHashTable.put("c", "P2");
					nameAncestorHashTable.put("d", "P3");
					nameAncestorHashTable.put("e", "EMS");
					nameAncestorHashTable.put("ms", "EMS");
					nameAncestorHashTable.put("p4", "P3");
					nameAncestorHashTable.put("p3", "P2");
					nameAncestorHashTable.put("ems", "P1");
					nameAncestorHashTable.put("p2", "P1");
					nameAncestorHashTable.put("p1", "P0");
					nameAncestorHashTable.put("a", "P0");
					nameAncestorHashTable.remove("a");
					nameAncestorHashTable.remove("em");
					nameAncestorHashTable.remove("m");
					nameAncestorHashTable.remove("p");


					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(partslistCellHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/partslistCellHashTable.ead"))
										+ EmbryoAtlas.instance.serialize(cellPartslistHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/cellPartslistHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}

				if (colorNameHashTable == null || nameColorHashTable == null
						|| colorNameHashTable.size() ==0 || nameColorHashTable.size() ==0) {
					colorNameHashTable = new Hashtable<String, String>();
					nameColorHashTable = new Hashtable<String, String>();
					String[] colorLines = ColorLegend_Chisholm.legend.split("\n");
					for (int c=0; c<colorLines.length; c++) {
						colorNameHashTable.put(colorLines[c].split(",")[1].trim().toLowerCase(), colorLines[c].split(",")[0].trim());
						nameColorHashTable.put(colorLines[c].split(",")[0].trim().toLowerCase(), colorLines[c].split(",")[1].trim().toLowerCase());
					}
					ColorLegend_Chisholm.legend="";
					ColorLegend_Chisholm.legend=null;
					Arrays.fill(colorLines, "");
					colorLines = null;
					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(colorNameHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/colorNameHashTable.ead"))+
										EmbryoAtlas.instance.serialize(nameColorHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/nameColorHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}

				if (splash_x == null || splash_y == null || splash_z == null || splash_r == null 
						|| splash_red == null || splash_green == null || splash_blue == null || splash_alpha == null 
						|| splash_red2 == null || splash_green2 == null || splash_blue2 == null || splash_alpha2 == null
						|| splash_x.size() ==0 || splash_y.size() ==0
						|| splash_z.size() ==0 || splash_r.size() ==0 || splash_timePoints.size() == 0
						|| splash_red.size() ==0 || splash_green.size() ==0
						|| splash_blue.size() ==0 || splash_alpha.size() ==0
						|| splash_red2.size() ==0 || splash_green2.size() ==0
						|| splash_blue2.size() ==0 || splash_alpha2.size() ==0
						|| splash_nameCoordsHashTable == null || splash_nameCoordsHashTable.size() == 0 ) {
					splash_x = new ArrayList<float[]>();
					splash_y = new ArrayList<float[]>();
					splash_z = new ArrayList<float[]>();
					splash_r = new ArrayList<float[]>();
					splash_timePoints = new ArrayList<Float>();
					splash_red = new ArrayList<byte[][]>();
					splash_green = new ArrayList<byte[][]>();
					splash_blue = new ArrayList<byte[][]>();
					splash_alpha = new ArrayList<byte[][]>();
					splash_red2 = new ArrayList<byte[]>();
					splash_green2 = new ArrayList<byte[]>();
					splash_blue2 = new ArrayList<byte[]>();
					splash_alpha2 = new ArrayList<byte[]>();
					splash_nameCoordsHashTable = new Hashtable<String, ArrayList<int[]>>();
					for(int t =0; t<fullTimes;t=t+(fullTimes/times)) {
						//								"364,214,20.0,40,\n"+		kill this
						//								"239,214,17.0,29,ABp\n"+
						//								"322,179,17.0,33,EMS\n"+
						//								"219,133,18.0,31,P2\n"+
						//								"302,266,18.0,31,ABa\n"+
						//								"299,244,23.0,27,",			kill this
						String[] cleanUp = Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t].split("\n");
						ArrayList<String> cleanUpArrayList = new ArrayList<String>();
						for (String s:cleanUp ) {
							if (s.trim().matches(".+,.+,.+,.+,.+"))
								cleanUpArrayList.add(s);
						}
						String[] nucCoords = new String[cleanUpArrayList.size()];
						nucCoords = cleanUpArrayList.toArray(nucCoords);
						float[] xArray = new float[nucCoords.length];
						float[] yArray = new float[nucCoords.length];
						float[] zArray = new float[nucCoords.length];
						float[] rArray = new float[nucCoords.length];
						byte[][] redArray = new byte[nucCoords.length][stacks/2];
						byte[][] greenArray = new byte[nucCoords.length][stacks/2];
						byte[][] blueArray = new byte[nucCoords.length][stacks/2];
						byte[][] alphaArray = new byte[nucCoords.length][stacks/2];
						byte[] red2Array = new byte[nucCoords.length];
						byte[] green2Array = new byte[nucCoords.length];
						byte[] blue2Array = new byte[nucCoords.length];
						byte[] alpha2Array = new byte[nucCoords.length];
						for (int i=0;i<nucCoords.length;i++) {
							String[] coords;
							coords = nucCoords[i].split(",");
							if (coords.length == 5) {
								if (i==0)
									splash_timePoints.add(Float.parseFloat(coords[3]));
								if (true){
									xArray[i]= (Float.parseFloat(coords[0])/60 + xOffset)*2;  //FUDGIE FITTING TO CENTER EMBRYO
									yArray[i]= -(Float.parseFloat(coords[1])/60 + yOffset)*2;				
									zArray[i]= -((Float.parseFloat(coords[2])/10) + zOffset)*2;
									rArray[i]= Float.parseFloat(coords[3])*2/150;
									if (nameColorHashTable.containsKey(coords[4].trim().toLowerCase())){
										Arrays.fill(alphaArray[i], (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3)));
										Arrays.fill(redArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5))));
										Arrays.fill(greenArray[i], (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7))));
										Arrays.fill(blueArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9))));
										alpha2Array[i] = (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3));
										red2Array[i] = (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5)));
										green2Array[i] = (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7)));
										blue2Array[i] = (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9)));
									} else {
										nameColorHashTable.put(coords[4].trim().toLowerCase(), "#ff151515");
										Arrays.fill(alphaArray[i], (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3)));
										Arrays.fill(redArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5))));
										Arrays.fill(greenArray[i], (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7))));
										Arrays.fill(blueArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9))));
									}
								}

								int[] c = new int[2];
								c[0]=t/(fullTimes/splashTimes);
								c[1]=i;
								if (splash_nameCoordsHashTable.get(coords[4].trim().toLowerCase()) ==null) {
									splash_nameCoordsHashTable.put(coords[4].trim().toLowerCase(), new ArrayList<int[]>());
								}
								splash_nameCoordsHashTable.get(coords[4].trim().toLowerCase()).add(c);
							}
						}
						//						Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t]="";
						//						Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t]=null;
						Arrays.fill(nucCoords, "");
						nucCoords = null;

						splash_x.add(xArray);
						splash_y.add(yArray);
						splash_z.add(zArray);
						splash_r.add(rArray);
						splash_alpha.add(alphaArray);
						splash_red.add(redArray);
						splash_green.add(greenArray);
						splash_blue.add(blueArray);

						splash_alpha2.add(alpha2Array);
						splash_red2.add(red2Array);
						splash_green2.add(green2Array);
						splash_blue2.add(blue2Array);
						loaded = true;
						gLUTsphere = new GLUTsphere();
					}
					dimLevel = getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).getFloat("DimLevel", 0.5f);
					if (dimList != null) {
						for (String name:dimList) {
							if (nameCoordsHashTable.get(name) != null) {
								for (int[] i:nameCoordsHashTable.get(name)) {
									for (int j=0;j<stacks/2;j++) {
										if (splash_alpha != null)
											if (i[0]%(fullTimes/splashTimes) ==0) 
												splash_alpha.get(i[0]/(fullTimes/splashTimes))[i[1]][j]= (byte) (dimLevel*255);
									}
								}
							}
						}
					}
					x = splash_x;
					y = splash_y;				
					z = splash_z;
					r = splash_r;
					timePoints = splash_timePoints;
					red = splash_red;
					green = splash_green;
					blue = splash_blue;
					alpha = splash_alpha;
					red2 = splash_red2;
					green2 = splash_green2;
					blue2 = splash_blue2;
					alpha2 = splash_alpha2;
					nameCoordsHashTable = splash_nameCoordsHashTable;

					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();								
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(splash_nameCoordsHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_nameCoordsHashTable.ead"))+
										EmbryoAtlas.instance.serialize(splash_x, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_x.ead"))+
										EmbryoAtlas.instance.serialize(splash_y, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_y.ead"))+
										EmbryoAtlas.instance.serialize(splash_z, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_z.ead"))+
										EmbryoAtlas.instance.serialize(splash_r, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_r.ead"))+
										EmbryoAtlas.instance.serialize(splash_timePoints, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_timePoints.ead"))+
										EmbryoAtlas.instance.serialize(splash_red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_red.ead"))+
										EmbryoAtlas.instance.serialize(splash_green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_green.ead"))+
										EmbryoAtlas.instance.serialize(splash_blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blue.ead"))+
										EmbryoAtlas.instance.serialize(splash_alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alpha.ead"))+
										EmbryoAtlas.instance.serialize(splash_red2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_red2.ead"))+
										EmbryoAtlas.instance.serialize(splash_green2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_green2.ead"))+
										EmbryoAtlas.instance.serialize(splash_blue2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blue2.ead"))+
										EmbryoAtlas.instance.serialize(splash_alpha2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alpha2.ead"))+
										EmbryoAtlas.instance.serialize(dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
								}
							});
						}
					}).start();

				}
				fullLoad = false;
			} else {
				x = splash_x;
				y = splash_y;				
				z = splash_z;
				r = splash_r;
				timePoints = splash_timePoints;
				red = splash_red;
				green = splash_green;
				blue = splash_blue;
				alpha = splash_alpha;
				red2 = splash_red2;
				green2 = splash_green2;
				blue2 = splash_blue2;
				alpha2 = splash_alpha2;
				nameCoordsHashTable = splash_nameCoordsHashTable;
				t = t * fullTimes/splashTimes;
				TouchSurfaceView.times = times;

				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.resultBox.setText("");
					}
				});

			}
		}



		private void openFullTimecourse() {
			int times = fullTimes;
			EmbryoAtlas.instance.handler.post(new Runnable(){
				public void run(){
					EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
				}
			});
			if (!fullLoad) {
				try {
					whole_nameCoordsHashTable = (Hashtable<String, ArrayList<int[]>>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/nameCoordsHashTable.ead"));
					if (whole_nameCoordsHashTable == null || whole_nameCoordsHashTable.size() == 0) {
						fullLoad = true;
						whole_nameCoordsHashTable = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_x = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/x.ead"));
					if (whole_x == null || whole_x.size() == 0) {
						fullLoad = true;
						whole_x = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_y = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/y.ead"));
					if (whole_y == null || whole_y.size() == 0) {
						fullLoad = true;
						whole_y = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_z = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/z.ead"));
					if (whole_z == null || whole_z.size() == 0) {
						fullLoad = true;
						whole_z = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_r = (ArrayList<float[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/r.ead"));
					if (whole_r == null || whole_r.size() == 0) {
						fullLoad = true;
						whole_r = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_timePoints = (ArrayList<Float>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/whole_timePoints.ead"));
					if (whole_timePoints == null || whole_timePoints.size() == 0) {
						fullLoad = true;
						whole_timePoints = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_red = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/red.ead"));
					if (whole_red == null || whole_red.size() == 0) {
						fullLoad = true;
						whole_red = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_green = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/green.ead"));
					if (whole_green == null || whole_green.size() == 0) {
						fullLoad = true;
						whole_green = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_blue = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/blue.ead"));
					if (whole_blue == null || whole_blue.size() == 0) {
						fullLoad = true;
						whole_blue = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_alpha = (ArrayList<byte[][]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/alpha.ead"));
					if (whole_alpha == null || whole_alpha.size() == 0) {
						fullLoad = true;
						whole_alpha = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_red2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/red2.ead"));
					if (whole_red2 == null || whole_red2.size() == 0) {
						fullLoad = true;
						whole_red2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_green2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/green2.ead"));
					if (whole_green2 == null || whole_green2.size() == 0) {
						fullLoad = true;
						whole_green2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_blue2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/blue2.ead"));
					if (whole_blue2 == null || whole_blue2.size() == 0) {
						fullLoad = true;
						whole_blue2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					whole_alpha2 = (ArrayList<byte[]>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/alpha2.ead"));
					if (whole_alpha2 == null || whole_alpha2.size() == 0) {
						fullLoad = true;
						whole_alpha2 = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
				try {
					dimList = (ArrayList<String>) EmbryoAtlas.instance.deserialize(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));
					if (dimList == null) {
						fullLoad = true;
						dimList = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
			}
			loaded = true;
			if (fullLoad) {
				if (bigColorHashTable == null || bigColorHashTable.size() ==0) {
					bigColorHashTable = new Hashtable<String, String>();
					String[] bigColorArray = (X11ColorsAndMore.colorNameCodes +"\n"+ X11ColorsAndMore.moreColorNameCodes).split("\n");
					for (String colorName:bigColorArray) {
						String[] chunks = colorName.split(",");
						bigColorHashTable.put(chunks[0].toLowerCase(), chunks[1].toUpperCase());
					}
					X11ColorsAndMore.colorNameCodes ="";
					X11ColorsAndMore.colorNameCodes = null;
					X11ColorsAndMore.moreColorNameCodes ="";
					X11ColorsAndMore.moreColorNameCodes =null;
					Arrays.fill(bigColorArray, "");
					bigColorArray = null;
					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(bigColorHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/bigColorHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}					
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
					}
				});


				if (partslistCellHashTable == null || partslistCellHashTable.size() ==0
						|| cellPartslistHashTable == null || cellPartslistHashTable.size() ==0) {
					partslistCellHashTable = new Hashtable<String, String>();
					cellPartslistHashTable = new Hashtable<String, String>();
					String[] partslistArray = PartsList.partslist.split("\n");
					int tick =0;
					for (String partsline:partslistArray) {
						String[] chunks = partsline.split("\t");
						if (chunks.length == 3) {
							partslistCellHashTable.put(chunks[0].toLowerCase() +":"+ chunks[2].toLowerCase()+"_"+tick, chunks[1]+".");
							cellPartslistHashTable.put(chunks[1].toLowerCase().trim().replace(".",""), chunks[0] +":"+ chunks[2]+"_"+tick);
						}
						tick++;
					}
					PartsList.partslist="";
					PartsList.partslist=null;
					Arrays.fill(partslistArray, "");			
					partslistArray = null;
					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(partslistCellHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/partslistHashTable.ead"))
										+ EmbryoAtlas.instance.serialize(cellPartslistHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/cellPartslistHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
					}
				});

				if (colorNameHashTable == null || nameColorHashTable == null
						|| colorNameHashTable.size() ==0 || nameColorHashTable.size() ==0) {
					colorNameHashTable = new Hashtable<String, String>();
					nameColorHashTable = new Hashtable<String, String>();
					String[] colorLines = ColorLegend_Chisholm.legend.split("\n");
					for (int c=0; c<colorLines.length; c++) {
						colorNameHashTable.put(colorLines[c].split(",")[1].trim().toLowerCase(), colorLines[c].split(",")[0].trim());
						nameColorHashTable.put(colorLines[c].split(",")[0].trim().toLowerCase(), colorLines[c].split(",")[1].trim().toLowerCase());
					}
					ColorLegend_Chisholm.legend="";
					ColorLegend_Chisholm.legend=null;
					Arrays.fill(colorLines, "");
					colorLines = null;
					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(colorNameHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/colorNameHashTable.ead"))+
										EmbryoAtlas.instance.serialize(nameColorHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/nameColorHashTable.ead"))
										);
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
							}
							EmbryoAtlas.instance.resultString = ("");
							//							EmbryoAtlas.instance.handler.post(new Runnable(){
							//								public void run(){
							//									EmbryoAtlas.instance.resultBox.setText("");
							//								}
							//							});
						}
					}).start();
				}
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.resultBox.setText("preview loaded...loading full set...");
					}
				});
				if (whole_x == null || whole_y == null || whole_z == null || whole_r == null 
						|| whole_red == null || whole_green == null || whole_blue == null || whole_alpha == null 
						|| whole_red2 == null || whole_green2 == null || whole_blue2 == null || whole_alpha2 == null
						|| whole_x.size() ==0 || whole_y.size() ==0
						|| whole_z.size() ==0 || whole_r.size() ==0 || whole_timePoints.size() == 0
						|| whole_red.size() ==0 || whole_green.size() ==0
						|| whole_blue.size() ==0 || whole_alpha.size() ==0
						|| whole_red2.size() ==0 || whole_green2.size() ==0
						|| whole_blue2.size() ==0 || whole_alpha2.size() ==0
						|| whole_nameCoordsHashTable == null || whole_nameCoordsHashTable.size() == 0 ) {
					whole_x = new ArrayList<float[]>();
					whole_y = new ArrayList<float[]>();
					whole_z = new ArrayList<float[]>();
					whole_r = new ArrayList<float[]>();
					whole_timePoints = new ArrayList<Float>();					
					whole_red = new ArrayList<byte[][]>();
					whole_green = new ArrayList<byte[][]>();
					whole_blue = new ArrayList<byte[][]>();
					whole_alpha = new ArrayList<byte[][]>();
					whole_red2 = new ArrayList<byte[]>();
					whole_green2 = new ArrayList<byte[]>();
					whole_blue2 = new ArrayList<byte[]>();
					whole_alpha2 = new ArrayList<byte[]>();
					whole_nameCoordsHashTable = new Hashtable<String, ArrayList<int[]>>();
					for(int t =0; t<times;t++) {
						//						"364,214,20.0,40,\n"+		kill this
						//						"239,214,17.0,29,ABp\n"+
						//						"322,179,17.0,33,EMS\n"+
						//						"219,133,18.0,31,P2\n"+
						//						"302,266,18.0,31,ABa\n"+
						//						"299,244,23.0,27,",			kill this
						String[] cleanUp = Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t].split("\n");
						ArrayList<String> cleanUpArrayList = new ArrayList<String>();
						for (String s:cleanUp ) {
							if (s.trim().matches(".+,.+,.+,.+,.+"))
								cleanUpArrayList.add(s);
						}
						String[] nucCoords = new String[cleanUpArrayList.size()];
						nucCoords = cleanUpArrayList.toArray(nucCoords);
						float[] xArray = new float[nucCoords.length];
						float[] yArray = new float[nucCoords.length];
						float[] zArray = new float[nucCoords.length];
						float[] rArray = new float[nucCoords.length];
						byte[][] redArray = new byte[nucCoords.length][stacks/2];
						byte[][] greenArray = new byte[nucCoords.length][stacks/2];
						byte[][] blueArray = new byte[nucCoords.length][stacks/2];
						byte[][] alphaArray = new byte[nucCoords.length][stacks/2];
						byte[] red2Array = new byte[nucCoords.length];
						byte[] green2Array = new byte[nucCoords.length];
						byte[] blue2Array = new byte[nucCoords.length];
						byte[] alpha2Array = new byte[nucCoords.length];
						for (int i=0;i<nucCoords.length;i++) {
							String[] coords;
							coords = nucCoords[i].split(",");
							if (coords.length == 5) {
								if (i==0)
									whole_timePoints.add(Float.parseFloat(coords[3]));
								if (true){
									xArray[i]= (Float.parseFloat(coords[0])/60 + xOffset)*2;  //FUDGIE FITTING TO CENTER EMBRYO
									yArray[i]= -(Float.parseFloat(coords[1])/60 + yOffset)*2;				
									zArray[i]= -((Float.parseFloat(coords[2])/10) + zOffset)*2;
									rArray[i]= Float.parseFloat(coords[3])*2/150;
									if (nameColorHashTable.containsKey(coords[4].trim().toLowerCase())){
										Arrays.fill(alphaArray[i], (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3)));
										Arrays.fill(redArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5))));
										Arrays.fill(greenArray[i], (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7))));
										Arrays.fill(blueArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9))));
										alpha2Array[i] = (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3));
										red2Array[i] = (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5)));
										green2Array[i] = (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7)));
										blue2Array[i] = (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9)));
									} else {
										nameColorHashTable.put(coords[4].trim().toLowerCase(), "#ff151515");
										Arrays.fill(alphaArray[i], (byte) parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(1,3)));
										Arrays.fill(redArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(3,5))));
										Arrays.fill(greenArray[i], (byte) Math.ceil(2+(255/63)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(5,7))));
										Arrays.fill(blueArray[i], (byte) Math.ceil(4+(255/31)*parseHexToInt(nameColorHashTable.get(coords[4].trim().toLowerCase()).substring(7,9))));
									}
								}

								int[] c = new int[2];
								c[0]=t;
								c[1]=i;
								if (whole_nameCoordsHashTable.get(coords[4].trim().toLowerCase()) ==null) {
									whole_nameCoordsHashTable.put(coords[4].trim().toLowerCase(), new ArrayList<int[]>());
								}
								whole_nameCoordsHashTable.get(coords[4].trim().toLowerCase()).add(c);
							}
						}
						Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t]="";
						Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable[t]=null;
						Arrays.fill(nucCoords, "");
						nucCoords = null;

						whole_x.add(xArray);
						whole_y.add(yArray);
						whole_z.add(zArray);
						whole_r.add(rArray);
						whole_alpha.add(alphaArray);
						whole_red.add(redArray);
						whole_green.add(greenArray);
						whole_blue.add(blueArray);

						whole_alpha2.add(alpha2Array);
						whole_red2.add(red2Array);
						whole_green2.add(green2Array);
						whole_blue2.add(blue2Array);
						loaded = true;
						gLUTsphere = new GLUTsphere();
					}
					x = whole_x;
					y = whole_y;				
					z = whole_z;
					r = whole_r;
					timePoints = whole_timePoints;
					red = whole_red;
					green = whole_green;
					blue = whole_blue;
					alpha = whole_alpha;
					red2 = whole_red2;
					green2 = whole_green2;
					blue2 = whole_blue2;
					alpha2 = whole_alpha2;
					nameCoordsHashTable = whole_nameCoordsHashTable;

					t = tFull;
					//					t = t * fullTimes/splashTimes;
					EmbryoAtlas.instance.tTap = EmbryoAtlas.instance.tTap * fullTimes/splashTimes;
					TouchSurfaceView.times = times;

					EmbryoAtlas.instance.resultString = ("");
					EmbryoAtlas.instance.handler.post(new Runnable(){
						public void run(){
							EmbryoAtlas.instance.resultBox.setText("");
						}
					});
					EmbryoAtlas.instance.wholeTimeCourseLoaded = true;

					new Thread(new Runnable() {
						public void run() {
							try {
								boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
										getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
									}
								});
								EmbryoAtlas.instance.saveSettings();								
								EmbryoAtlas.instance.resultString = (
										EmbryoAtlas.instance.serialize(nameCoordsHashTable, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/nameCoordsHashTableTemp.ead"))+
										EmbryoAtlas.instance.serialize(x, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/xTemp.ead"))+
										EmbryoAtlas.instance.serialize(y, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/yTemp.ead"))+
										EmbryoAtlas.instance.serialize(z, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/zTemp.ead"))+
										EmbryoAtlas.instance.serialize(r, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/rTemp.ead"))+
										EmbryoAtlas.instance.serialize(timePoints, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/timePointsTemp.ead"))+
										EmbryoAtlas.instance.serialize(red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead"))+
										EmbryoAtlas.instance.serialize(green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead"))+
										EmbryoAtlas.instance.serialize(blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead"))+
										EmbryoAtlas.instance.serialize(alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead"))+
										EmbryoAtlas.instance.serialize(red2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red2Temp.ead"))+
										EmbryoAtlas.instance.serialize(green2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green2Temp.ead"))+
										EmbryoAtlas.instance.serialize(blue2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue2Temp.ead"))+
										EmbryoAtlas.instance.serialize(alpha2, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha2Temp.ead"))+
										EmbryoAtlas.instance.serialize(dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead"))
										);
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/nameCoordsHashTableTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/nameCoordsHashTable.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/xTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/x.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/yTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/y.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/zTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/z.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/rTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/r.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/timePointsTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/timePoints.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red2Temp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red2.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green2Temp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green2.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue2Temp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue2.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha2Temp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha2.ead"));
								new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead")
								.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));
								getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();
							} catch (FileNotFoundException ex) {
								// TODO Auto-generated catch block
								EmbryoAtlas.instance.handler.post(new Runnable(){
									public void run(){
										EmbryoAtlas.instance.resultBox.setText("");
									}
								});
								ex.printStackTrace();
							}
							EmbryoAtlas.instance.resultString = ("");
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									EmbryoAtlas.instance.resultBox.setText("");
								}
							});
						}
					}).start();

				}

				fullLoad = false;
				try {
					EmbryoAtlas.instance.resultString = 
							(EmbryoAtlas.instance.serialize("version_1.1.37", new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/savedVersionString.ead")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}

			} else {
				x = whole_x;
				y = whole_y;				
				z = whole_z;
				r = whole_r;
				timePoints = whole_timePoints;
				red = whole_red;
				green = whole_green;
				blue = whole_blue;
				alpha = whole_alpha;
				red2 = whole_red2;
				green2 = whole_green2;
				blue2 = whole_blue2;
				alpha2 = whole_alpha2;
				nameCoordsHashTable = whole_nameCoordsHashTable;
				t = tFull;
				TouchSurfaceView.t = TouchSurfaceView.tFull;
				//				t = t * fullTimes/splashTimes;
				EmbryoAtlas.instance.tTap = EmbryoAtlas.instance.tTap * fullTimes/splashTimes;
				TouchSurfaceView.times = times;
				EmbryoAtlas.instance.wholeTimeCourseLoaded = true;

				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						EmbryoAtlas.instance.resultBox.setText("");
					}
				});

				try {
					EmbryoAtlas.instance.resultString = 
							(EmbryoAtlas.instance.serialize("version_1.1.37", new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/savedVersionString.ead")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fullLoad = true;
				}
			}
			final int heightFinal = TouchSurfaceView.this.getHeight();
			EmbryoAtlas.instance.handler.post(new Runnable(){
				public void run(){
					FrameLayout.LayoutParams timeBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
					timeBoxParams.gravity = Gravity.TOP;
					timeBoxParams.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox.getWidth()/2; //Your X coordinate
					timeBoxParams.topMargin = 0; //Your Y coordinate
					EmbryoAtlas.instance.timeBox.setLayoutParams(timeBoxParams);		
					FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
					timeBox2Params.gravity = Gravity.TOP;
					timeBox2Params.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox2.getWidth()/2; //Your X coordinate
					timeBox2Params.topMargin = (int) (heightFinal-35*EmbryoAtlas.instance.screenDensity); //Your Y coordinate
					EmbryoAtlas.instance.timeBox2.setLayoutParams(timeBox2Params);	
				}
			});

			requestRender();

		}



		public void reload(Boolean eraseLastColors) {	
			for (int a=0; a<red2.size();a++) {
				for (int b=0; b<red.get(a).length; b++) {
					for (int j=0;j<mRenderer.stacks/2;j++){
						alpha.get(a)[b][j] = alpha2.get(a)[b];
						red.get(a)[b][j] = red2.get(a)[b];
						green.get(a)[b][j] = green2.get(a)[b];
						blue.get(a)[b][j] = blue2.get(a)[b];
					}
				}
			}
			for (int a=0; a<splash_red2.size();a++) {
				for (int b=0; b<splash_red.get(a).length; b++) {
					for (int j=0;j<mRenderer.stacks/2;j++){
						splash_alpha.get(a)[b][j] = splash_alpha2.get(a)[b];
						splash_red.get(a)[b][j] = splash_red2.get(a)[b];
						splash_green.get(a)[b][j] = splash_green2.get(a)[b];
						splash_blue.get(a)[b][j] = splash_blue2.get(a)[b];
					}
				}
			}

			if (dimList != null)
				dimList.clear();
			loaded = true;
			requestRender();
			if (eraseLastColors) {
				new Thread(new Runnable() {
					public void run() {
						try {
							boolean mkdirs = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/")).mkdirs();
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									EmbryoAtlas.instance.resultBox.setText("auto-saving scene settings...");
									getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", false).apply();
								}
							});
							EmbryoAtlas.instance.saveSettings();								
							EmbryoAtlas.instance.resultString = (
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_redTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_greenTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blueTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.splash_alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alphaTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead"))+

									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.red, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.green, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.blue, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.alpha, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead"))+
									EmbryoAtlas.instance.serialize(TouchSurfaceView.mRenderer.dimList, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead"))
									);
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_redTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_red.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_greenTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_green.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blueTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_blue.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alphaTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/splash_alpha.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));

							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/redTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/red.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/greenTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/green.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blueTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/blue.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alphaTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/alpha.ead"));
							new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimListTemp.ead")
							.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/org.wormguides.wormguides/files/dimList.ead"));
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									EmbryoAtlas.instance.resultBox.setText("");
								}
							});
							getContext().getSharedPreferences("EmbroAtlas_PrefsFile", Context.MODE_MULTI_PROCESS).edit().putBoolean("serializationComplete", true).apply();

						} catch (FileNotFoundException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
							EmbryoAtlas.instance.handler.post(new Runnable(){
								public void run(){
									EmbryoAtlas.instance.resultBox.setText("");
								}
							});
						}
						EmbryoAtlas.instance.resultString = ("");
					}
				}).start();
			}
		}

		public int readPixel(int x, int y, int w, int h, String searchText) {  
			this.grabX = x;
			this.grabY = y;
			this.grabW = w;
			this.grabH = h;
			//			this.subColorString = searchText;
			TouchSurfaceView.this.onPause();
			TouchSurfaceView.this.onResume();
			flatRender = true;
			grabPixels = true;
			requestRender();

			while (grabPixels == true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			TouchSurfaceView.this.onPause();
			TouchSurfaceView.this.onResume();
			this.grabPixels = false;
			flatRender = false;
			requestRender();
			return pixel;
		}

		public void setAnimating(boolean b) {
			isAnimating = b;
		}

		/**Delays 'msecs' milliseconds.*/
		public  void wait(int msecs) {
			try {Thread.sleep(msecs);}
			catch (InterruptedException e) { }
		}

		/** Pad 'n' with leading zeros to the specified number of digits. */
		public String pad(int n, int digits) {
			String str = ""+n;
			while (str.length()<digits)
				str = "0"+str;
			return str;
		}


		public void onDrawFrame(GL10 gl) {

			if ((int)t<times ) {
				int time = t;
				//				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				newFactor = lastScaleFactor * scaleFactor;

				gl.glTranslatef(mTranslateX/100, -mTranslateY/100, -10f/newFactor);

				gl.glRotatef((float) -(180*arcball.getRotation().getX()/Math.PI), 1f, 0f, 0f);
				gl.glRotatef((float) -(180*arcball.getRotation().getY()/Math.PI), 0f, 1f, 0f);
				zRotOffset = -(float) (Math.PI-0.8f);
				if (dataSet.startsWith("Chisholm"))
					zRotOffset = 0;
				gl.glRotatef((float) (180*zRotOffset/Math.PI), 0f, 0f, 1f); // Correction to horizontal
				gl.glRotatef((float) -(180*arcball.getRotation().getZ()/Math.PI), 0f, 0f, 1f);

				float sum = 0;
				int count = 0;
				float avgRad = 0.0f;

				for (int i = 1; i < r.get(time).length; i++) {
					sum = sum + r.get(time)[i];
					if (r.get(time)[i] !=0)
						count++;
				}
				
				avgRad = sum / count;
				if (dataSet.startsWith("Chisholm"))
					avgRad = (float) (1.5f/Math.pow(count, 1.0f/3.0f));
				
				if (flatRender) {
					gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
					gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);


					for (int i=0;i<x.get(time).length;i++) {
						if (!(x.get(time)[i]==0 && y.get(time)[i]==0 && z.get(time)[i]==0)) {
							gl.glPushMatrix();
							gl.glTranslatef(x.get(time)[i],y.get(time)[i],z.get(time)[i]);
							if ((((byte)alpha.get(time)[i][0]) & 0xFF) >5)
								GLUTsphere.glutFlatSphere(gl, avgRad, 10, 10, true, red2.get(time)[i], green2.get(time)[i], blue2.get(time)[i]);
							gl.glPopMatrix();
						}
					}
					if (grabPixels){ 
						grabW=1;
						grabH=1;
						int b[]=new int[grabW*grabH];
						int bt[]=new int[grabW*grabH];
						IntBuffer ib=IntBuffer.wrap(b);
						ib.position(0);
						//						EmbryoAtlas.instance.handler.post(new Runnable(){
						//							public void run(){
						//								EmbryoAtlas.instance.editBox.setText(""+GL10.GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES +" "+ GL10.GL_IMPLEMENTATION_COLOR_READ_TYPE_OES);
						//							}
						//						});
						gl.glReadPixels(grabX, TouchSurfaceView.this.getHeight()- grabY, grabW, grabH, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
						for(int i=0; i<grabH; i++)
						{//remember, that OpenGL bitmap is incompatible with Android bitmap
							//and so, some correction need.	 
							for(int j=0; j<grabW; j++)
							{
								int pix=b[i*grabW+j];
								int pb=(pix>>16)&0xff;
								int pr=(pix<<16)&0x00ff0000;
								int pix1=(pix&0xff00ff00) | pr | pb;
								bt[(grabH-i-1)*grabW+j]=pix1;
							}
						}                  
						pixel = bt[0];

						grabPixels = false;

					} 
				}

				if (!flatRender){
					gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
					gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
					gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

					//					if (t!=lastT)
					gLUTsphere = new GLUTsphere();
					lastT = t;
					sph = null;
					if (true) {

						sph = new GLUTsphere.SolidSphere[x.get(time).length];
						for (int i=0;i<x.get(time).length;i++) {						
							if ((((byte)alpha.get(time)[i][0]) & 0xFF) == 255) {
								if (!(x.get(time)[i]==0 && y.get(time)[i]==0 && z.get(time)[i]==0)) {
									gl.glPushMatrix();
									gl.glTranslatef(x.get(time)[i],y.get(time)[i],z.get(time)[i]);
									gl.glRotatef((float) (-180*zRotOffset/Math.PI), 0f, 0f, 1f); // Correction to horizontal
									sph[i] = gLUTsphere.new SolidSphere(gl, avgRad, 10, stacks, red.get(time)[i], green.get(time)[i], blue.get(time)[i], alpha.get(time)[i], showAxes);
									sph[i].draw();

									gl.glPopMatrix();
								}
							} 
						}
						for (int i=0;i<x.get(time).length;i++) {						
							if ((((byte)alpha.get(time)[i][0]) & 0xFF) < 255) {
								if (!(x.get(time)[i]==0 && y.get(time)[i]==0 && z.get(time)[i]==0)) {
									gl.glEnable(GL10.GL_BLEND);                                      
									gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
									gl.glEnable(GL10.GL_CULL_FACE);
									gl.glCullFace(GL10.GL_FRONT);
									gl.glPushMatrix();
									gl.glTranslatef(x.get(time)[i],y.get(time)[i],z.get(time)[i]);
									gl.glRotatef((float) (-180*zRotOffset/Math.PI), 0f, 0f, 1f); // Correction to horizontal
									if (showLineageColors)
										sph[i] = gLUTsphere.new SolidSphere(gl, avgRad, 10, stacks, red.get(time)[i], green.get(time)[i], blue.get(time)[i], alpha.get(time)[i], showAxes);
									else
										sph[i] = gLUTsphere.new SolidSphere(gl, avgRad, 10, stacks, new byte[]{100,100,100,100,100}, new byte[]{100,100,100,100,100}, new byte[]{100,100,100,100,100}, alpha.get(time)[i], showAxes);
									if ((((byte)alpha.get(time)[i][0]) & 0xFF) >10){
										sph[i].draw();
									}
									gl.glPopMatrix();
									gl.glDisable(GL10.GL_CULL_FACE);
								}
							}
						}

					}
					gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
					gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


				}
				EmbryoAtlas.instance.handler.post(new Runnable(){
					public void run(){
						if (dataSet.startsWith("Chisholm"))
							EmbryoAtlas.instance.clockBox.setText("min="+             
									Double.toString((double)timePoints.get(t)).substring(0, Double.toString((double)timePoints.get(t)).indexOf(".") +2)
									+ "\nnuclei="+x.get(t).length/*+(t>=180?"?":"")*/
									+ "\n"+(showAxes?"Ã ":"no ")+"axes");
						else
							EmbryoAtlas.instance.clockBox.setText("min="+             //*83/60 for old embryo
									Double.toString((double)t*(fullTimes/times)*75/60 + 40).substring(0, Double.toString((double)t*75/60 + 40).indexOf(".") +2)
									+ "\nnuclei="+x.get(t).length/*+(t>=180?"?":"")*/
									+ "\n"+(showAxes?"Ã ":"no ")+"axes");
						EmbryoAtlas.instance.clockBox.getHitRect(EmbryoAtlas.instance.clockRect);
						EmbryoAtlas.instance.helpBox.getHitRect(EmbryoAtlas.instance.helpRect);
						EmbryoAtlas.instance.playFBox.getHitRect(EmbryoAtlas.instance.playFRect);
						EmbryoAtlas.instance.playRBox.getHitRect(EmbryoAtlas.instance.playRRect);
					}
				});
				renderingDone = true;
			}
		}

		private float parseHexToFloat(String hex) {
			float value = 0f;
			try {value=Integer.parseInt(hex,16);}
			catch(Exception e) { }
			return value/255f;
		}

		private int parseHexToInt(String hex) {
			int value = 0;
			try {value=Integer.parseInt(hex,16);}
			catch(Exception e) { }
			return value;
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if (gl instanceof GL10)
				gl = (GL10) gl;
			gl.glViewport(0, 0, width, height);
			EmbryoAtlas.instance.screenRight = width;
			EmbryoAtlas.instance.screenBottom = height;
			final FrameLayout.LayoutParams timeBox2Params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
			timeBox2Params.gravity = Gravity.TOP;
			timeBox2Params.leftMargin = TouchSurfaceView.t*(8*EmbryoAtlas.instance.screenRight/10)/(TouchSurfaceView.times-1)+EmbryoAtlas.instance.screenRight/10 - EmbryoAtlas.instance.timeBox2.getWidth()/2; //Your X coordinate
			timeBox2Params.topMargin = (int) (EmbryoAtlas.instance.screenBottom-35*EmbryoAtlas.instance.screenDensity); //Your Y coordinate
			EmbryoAtlas.instance.handler.post(new Runnable(){
				public void run(){
					EmbryoAtlas.instance.timeBox2.setLayoutParams(timeBox2Params);
				}
			});		
			final FrameLayout.LayoutParams rateBoxParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
			rateBoxParams.gravity = Gravity.TOP;
			rateBoxParams.leftMargin = (int) (EmbryoAtlas.instance.screenRight - 40 * EmbryoAtlas.instance.screenDensity); //Your X coordinate
			rateBoxParams.topMargin = (int) (EmbryoAtlas.instance.screenBottom/10 + (TouchSurfaceView.delay*EmbryoAtlas.instance.screenBottom/2000) - EmbryoAtlas.instance.rateBox.getHeight()/2); //Your Y coordinate
			EmbryoAtlas.instance.handler.post(new Runnable(){
				public void run(){
					EmbryoAtlas.instance.rateBox.setLayoutParams(rateBoxParams);	
				}
			});


			float ratio = (float) width / height;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glTranslatef(0f, 0f, 0f);
			gl.glFrustumf(-1, 1, -1/ratio, 1/ratio, 1, 500);
			arcball.setCenter(new Vect3d(0f,0f,0f));
			arcball.setSize(new Vect3d(1f,1f,1f));
			double centerX = 0f;
			double centerY = 0f;
			double centerZ = 0f;
			double maxLength = 1f;
			arcball.getCamera().resetView();
			arcball.getCamera().setFrustum(-1, 1, -1/ratio, 1/ratio, 1, 500);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			if (gl instanceof GL10)
				gl = (GL10) gl;
			this.config = config;
			if (!flatRender) {
				gl.glDisable(GL10.GL_SCISSOR_TEST);
				gl.glDisable(GL10.GL_DITHER);
				gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
						GL10.GL_FASTEST);
				gl.glDisable(GL10.GL_CULL_FACE);

				gl.glClearColor(0,0,0,0);

				float mat_specular[] =
					{ 0.5f, 0f, 0.25f, 0f };
				float mat_shininess[] =
					{ 50.0f };
				float light_position[] =
					{ 0.5f, 0.5f, 1.0f, 0.0f };

				gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				gl.glShadeModel(GL10.GL_SMOOTH);

				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat_specular, 0);  
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, mat_specular, 0);  
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mat_shininess, 0);
				gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);

				gl.glEnable(GL10.GL_LIGHTING);
				gl.glEnable(GL10.GL_LIGHT0);
				gl.glEnable(GL10.GL_DEPTH_TEST);
				gl.glDepthFunc(GL10.GL_LESS);
			} else {
				gl.glEnable(GL10.GL_SCISSOR_TEST);
				gl.glScissor(grabX, TouchSurfaceView.this.getHeight()- grabY, grabW, grabH);
				gl.glDisable(GL10.GL_DITHER);
				gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
						GL10.GL_FASTEST);
				gl.glClearColor(0,0,0,0);
				gl.glEnable(GL10.GL_CULL_FACE);
				gl.glShadeModel(GL10.GL_SMOOTH);
				gl.glEnable(GL10.GL_DEPTH_TEST);
			}
		}

	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private final float TRACKBALL_SCALE_FACTOR = 6.0f;
	static SceneRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	private float mPreviousT;

	private ScaleGestureDetector mScaleDetector;
	public float scaleFactor = 1.0f;
	public float lastScaleFactor = 1.0f;
	public float newFactor = 1.0f;
	public static float dimLevel = 0.5f;
	public static int t = 0;
	//	public static int times = Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable.length;
	public static int times = Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable.length -1;
	public static int splashTimes = 10;
	public static int fullTimes = Chisholm_cell_lineage_20091122_E01_20C_Ventral_1.nucCoordTable.length -1;
	private Thread movieThread;


}