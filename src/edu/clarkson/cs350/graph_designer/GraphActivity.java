package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class GraphActivity extends Activity {
	
	private GraphView graphView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		graphView = new GraphView(this);
		setContentView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.matrixItem){
			Intent intent = new Intent(this, MatrixActivity.class);
			Bundle bundle = new Bundle();
			
			graphView.calculateMatrix();
			double[][] matrix = graphView.getAdjMatrix();
			bundle.putSerializable("matrix", matrix);
			intent.putExtras(bundle);
			intent.putExtra("size", matrix.length);
			
			startActivity(intent);
			return true;
		}
		if (id == R.id.qwalk){
			if (item.getTitle().equals("Stop Walk")){
				Log.d("cs350-thread", "Stopped walk");
				graphView.stop_qwalk();
				return true;
			}
			
			final Runnable r = new Runnable() {
				@Override
				public void run() {
					Log.d("cs350-thread", "Thread is running");
					graphView.calculateMatrix();
					graphView.qwalk_me();
				}
			};
			
			new Thread(r).start();
//			
//			graphView.calculateMatrix();
//			graphView.qwalk_me();
			item.setTitle("Stop Walk");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
