package edu.clarkson.cs350.graph_designer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class GraphActivity extends ActionBarActivity {
	
	private GraphView graphView;
	private Thread qwalkThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		graphView = new GraphView(this);
		setContentView(graphView);
	}
	
	@Override
	protected void onPause() {
		if (qwalkThread != null){
			qwalkThread.interrupt();
		}
		super.onPause();
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
		if (id == R.id.toggleDelete){
			if(!graphView.getqwalkIsRunning()){
				item.setChecked(!item.isChecked());
				graphView.setDeleteMode(item.isChecked());
			}
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
				graphView.stop_qwalk();
				qwalkThread.interrupt();
				item.setTitle("Quantum Walk");
				return true;
			}
			
			final Runnable r = new Runnable() {
				@Override
				public void run() {
					graphView.calculateMatrix();
					graphView.qwalk_me();
				}
			};
			
			qwalkThread = new Thread(r);
			qwalkThread.start();
			item.setTitle("Stop Walk");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
