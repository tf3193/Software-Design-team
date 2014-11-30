package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class MatrixActivity extends Activity {
	ArrayList<GraphNodeEntity> nodes;
	ArrayList<GraphEdgeEntity> edges;
	
	int n = 0; // Cache the size of the matrix
	double[][] rawMatrix;
	RealMatrix realMatrix;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matrix);
		
		// Get data passed from MainActivity
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		Object[] tmpArr = (Object[]) bundle.getSerializable("matrix");
		n = intent.getIntExtra("size", 0);
		
		rawMatrix = new double[n][n];
		
		Log.d("cs350-matrix", "N: " + n);
		Log.d("cs350-matrix", "Matrix2: " + Arrays.deepToString(tmpArr));
		for (int i=0; i<n; i++){
			Log.d("cs350-matrix", "i "+i);
			for (int j=0; j<n; j++){
				Log.d("cs350-matrix", "j "+j);
				rawMatrix[i][j] = ((double[])((Object[])tmpArr)[i])[j];
			}
		}
		Log.d("cs350-matrix", "Matrix after: " + Arrays.deepToString(rawMatrix));
		
		realMatrix = MatrixUtils.createRealMatrix(rawMatrix);

		WebView matrixTextView = (WebView) findViewById(R.id.matrixText);
		WebView eigenVectorsView = (WebView) findViewById(R.id.eigenVectorsText);
		WebView eigenValuesTextView = (WebView) findViewById(R.id.eigenValuesText);
		
		EigenDecomposition eig = new EigenDecomposition(realMatrix);
		
		matrixTextView.loadData(matrixToHtmlString(rawMatrix),"text/html","utf-8");
		eigenVectorsView.loadData(matrixToHtmlString(eig.getV().getData()),"text/html","utf-8");
		eigenValuesTextView.loadData(Arrays.toString(eig.getRealEigenvalues()),"text/html","utf-8");
	}
	
	private void populateMatrix(){
		int n = nodes.size();
		int[][] matrix = new int[n][n];
		
		for (GraphEdgeEntity x : edges){
			int i = nodes.indexOf(x.getNode1());
			int j = nodes.indexOf(x.getNode2());
			
			matrix[i][j] = 1;
			matrix[j][i] = 1; // Necessary for bi-directional graphs
		}
	}
	
	private String matrixToHtmlString(Integer[][] rawMatrix2){
		String s = "<table border=1>";
		
		for (int i=0; i < n; i++){
			s += "<tr>";
			for (int j=0; j < n; j++){
				s += "<td>" + rawMatrix2[i][j] + "</td>";
			}
			s += "</tr>";
		}
		
		return s;
	}
	
	private String matrixToHtmlString(double[][] rawMatrix2){
		String s = "<table border=1>";
		
		for (int i=0; i < n; i++){
			s += "<tr>";
			for (int j=0; j < n; j++){
				s += "<td>" + rawMatrix2[i][j] + "</td>";
			}
			s += "</tr>";
		}
		
		return s;
	}
	
	private double[][] matrixToDoubles(Integer[][] rawMatrix2){
		double[][] dblM = new double[n][n];
		
		for (int i=0; i < n; i++){
			for (int j=0; j < n; j++){
				dblM[i][j] = (double) rawMatrix2[i][j];
			}
		}
		
		return dblM;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.matrix, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
