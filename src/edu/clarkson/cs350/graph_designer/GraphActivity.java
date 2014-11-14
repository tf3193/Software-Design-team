package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
			
			ArrayList<GraphNodeEntity> nodes = graphView.getNodes();
			ArrayList<GraphEdgeEntity> edges = graphView.getEdges();
			
			int n = nodes.size();
			Integer[][] matrix = new Integer[n][n];
			
			// Fill with zeros first
			for (int i=0; i<n; i++){
				for (int j=0; j<n; j++){
					matrix[i][j] = 0;
				}
			}
			
			// Set appropriate cells to 1
			for (GraphEdgeEntity x : edges){
				int i = nodes.indexOf(x.getNode1());
				int j = nodes.indexOf(x.getNode2());
				
				matrix[i][j] = 1;
				matrix[j][i] = 1; // Necessary for bi-directional graphs
			}
			
			bundle.putSerializable("matrix", matrix);
			intent.putExtras(bundle);
			intent.putExtra("size", n);
			
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
