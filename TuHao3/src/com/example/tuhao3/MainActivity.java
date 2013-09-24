package com.example.tuhao3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private Button btn;
	private static Handler handler;
	public ListView listView;
	public TextView textView;
	public Button backBtn;
	private TextView tokenText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn = (Button)findViewById(R.id.button1);
        this.btn.setOnClickListener(this);
        this.listView = (ListView)findViewById(R.id.timelineitems);
        tokenText = (TextView)findViewById(R.id.token);
        tokenText.setText("2.00W7chNCLpAAUE17d839abe9KxnWUC");
        this.listView.setOnItemClickListener(new OnItemClickListener() {
//        	on
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		Log.d("sswilliam", "clicked");
        		HashMap<String, String> item = (HashMap<String, String>)((SimpleAdapter)arg0.getAdapter()).getItem(arg2);
        		Log.d("sswilliam", "content "+item.get("content") +" id "+item.get("id"));
        		final String statusID = item.get("id");
        		btn.setVisibility(View.INVISIBLE);
        		listView.setVisibility(View.INVISIBLE);
        		backBtn.setVisibility(View.VISIBLE);
        		textView.setVisibility(View.VISIBLE);
        		textView.setText("");
        		textView.append("status id: "+statusID+"\n");
        		
        		new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String access_token = tokenText.getText().toString();
						String id = statusID;
						Timeline tm = new Timeline();
						tm.client.setToken(access_token);
						int total = -1;
						try {
							JSONObject allIds = tm.getRepostTimelineIds(id);
							total = allIds.getInt("total_number");
						} catch (Exception e) {
							e.printStackTrace();
							getHandler().sendMessage(getTextEidtMessage("[ERR]"+e.getMessage()+"\n"));
							return;
						}
						if(total == -1){
							getHandler().sendMessage(getTextEidtMessage("No reply found\n"));
							return;
						}
						getHandler().sendMessage(getTextEidtMessage("Total repost: "+total+"\n"));
						
						int page = total/20+2;
						ArrayList<User> totalUser = new ArrayList<User>();
						for(int i = 1;i<page;i++){
							getHandler().sendMessage(getTextEidtMessage("Processing page"+i+"\n"));
							Timeline tempTM = new Timeline();
							tm.client.setToken(access_token);
							try {
								StatusWapper status = tm.getRepostTimeline(id, new Paging(i));
								List<Status> allStatus = status.getStatuses();

								getHandler().sendMessage(getTextEidtMessage("--repost in page"+allStatus.size()+"\n"));
								for(int j = 0;j<allStatus.size();j++){
									Status curStatus = allStatus.get(j);
									User user = new User();
									user.id = curStatus.getUser().getId();
									user.name = curStatus.getUser().getName();
									user.displayName = curStatus.getUser().getScreenName();
									if(!totalUser.contains(user)){
										totalUser.add(user);
									}
//									user.displayName = curStatus.getUser().get
									
									
								}

								getHandler().sendMessage(getTextEidtMessage("--user size"+totalUser.size()+"\n"));
								
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
								getHandler().sendMessage(getTextEidtMessage("Fetch Repost Error page "+i+"\n"));
							}
						}

						getHandler().sendMessage(getTextEidtMessage("-----------------------------------\n"));
						getHandler().sendMessage(getTextEidtMessage("get all user number:"+totalUser.size()+"\n"));
						Collections.shuffle(totalUser);
						Collections.shuffle(totalUser);
						Collections.shuffle(totalUser);
						for(int i = 0;i<totalUser.size();i++){
							getHandler().sendMessage(getTextEidtMessage(i+" "+totalUser.get(i).id+"\n"));
							getHandler().sendMessage(getTextEidtMessage("--"+totalUser.get(i).name+"\n"));
//							getHandler().sendMessage(getTextEidtMessage("--"+totalUser.get(i).displayName+"\n"));
						}
						
					}
				}).start();
        	}
		});
        handler = new MYHandler(this);
        this.backBtn = (Button)findViewById(R.id.backBtn);
        this.backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backBtn.setVisibility(View.INVISIBLE);
				btn.setVisibility(View.VISIBLE);
				textView.setVisibility(View.INVISIBLE);
				listView.setVisibility(View.VISIBLE);
			}
		});
        this.textView = (TextView)findViewById(R.id.repoInfo);
    }
    public Handler getHandler(){
    	return handler;
    }
    public Message getTextEidtMessage(String info){
    	Message msg = Message.obtain();
    	Bundle b = new Bundle();
    	b.putString("bname", "app");
    	b.putString("msg", ""+info);
    	msg.setData(b);
    	return msg;
    }

    public static class MYHandler extends Handler{
    	private MainActivity act;
    	public MYHandler(MainActivity act){
    		this.act = act;
    	}
    	public void handleMessage(android.os.Message msg) {
    		String bnameString = msg.getData().getString("bname");
    		if("updateStatus".equals(bnameString)){
//    			for()
        		ArrayList<HashMap<String, String>> finalList = new ArrayList<HashMap<String,String>>();
    			ArrayList<String> contesnts = msg.getData().getStringArrayList("content");
    			ArrayList<String> ids = msg.getData().getStringArrayList("ids");
    			for(int i = 0;i<contesnts.size();i++){
        			HashMap<String, String> item = new HashMap<String, String>();
        			item.put("content", contesnts.get(i));
        			item.put("id", ids.get(i));
        			finalList.add(item);
    			}
    			SimpleAdapter adapter = new SimpleAdapter(act, finalList, R.layout.statusitem, new String[]{"content"}, new int[]{R.id.title});
    			act.listView.setAdapter(adapter);
    			
    		}
    		if("app".equals(bnameString)){
    			act.textView.append(msg.getData().getString("msg"));
    		}
    	};
    }
    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	if(v == this.btn){
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String access_token = tokenText.getText().toString();
		    		Timeline tm = new Timeline();
		    		tm.client.setToken(access_token);
//		    		ListAdapter listAdapter = SimpleAdapter()
		    		try {
		    			StatusWapper status = 
//		    					tm.getUserTimeline();
		    			tm.getUserTimelineByUid("2034753852");
			    		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			    		ArrayList<String> content = new ArrayList<String>();
			    		ArrayList<String> ids = new ArrayList<String>();
		    			for(Status s : status.getStatuses()){
		    				content.add(s.getText());
		    				ids.add(s.getId());
//		    		
		    			}
//		    			new SimpleAdapter(context, data, resource, from, to)
		    			Message message = Message.obtain();
		    			Bundle b = new Bundle();
		    			b.putString("bname", "updateStatus");
		    			b.putStringArrayList("content", content);
		    			b.putStringArrayList("ids", ids);
		    			message.setData(b);
		    			getHandler().sendMessage(message);
		    			
//		    			System.out.println(status.getNextCursor());
//		    			System.out.println(status.getPreviousCursor());
//		    			System.out.println(status.getTotalNumber());
//		    			System.out.println(status.getHasvisible());
		    		} catch (WeiboException e) {
		    			e.printStackTrace();
		    		}
				}
			}).start();
    		
    	}
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
