package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;
import java.util.Arrays;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class MatrixActivity extends Activity {
	
	//TODO: Find eigenvalues/eigenvectors of the matrix.
	
	ArrayList<GraphNodeEntity> nodes;
	ArrayList<GraphEdgeEntity> edges;
	
	int n = 0; // Cache the size of the matrix
	Integer[][] rawMatrix;
	Matrix jamaMatrix;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matrix);
		
		// Get data passed from MainActivity
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		n = (Integer) intent.getExtras().get("size");
		
		
		Object[] tmpArr = (Object[]) bundle.getSerializable("matrix");
		
		rawMatrix = new Integer[n][n];
		
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				rawMatrix[i][j] = (Integer) ((Object[])tmpArr[i])[j];
			}
		}
		
		jamaMatrix = new Matrix(matrixToDoubles(rawMatrix));
		
		// TODO: For testing, delete later
//		nodes = new ArrayList<GraphNodeEntity>();
//		edges = new ArrayList<GraphEdgeEntity>();
//		
//		GraphNodeEntity node1 = new GraphNodeEntity();
//		GraphNodeEntity node2 = new GraphNodeEntity();
//		GraphNodeEntity node3 = new GraphNodeEntity();
//		
//		GraphEdgeEntity edge12 = new GraphEdgeEntity(node1, node2, null);
//		GraphEdgeEntity edge13 = new GraphEdgeEntity(node1, node3, null);
//		GraphEdgeEntity edge23 = new GraphEdgeEntity(node2, node3, null);
//		
//		nodes.add(node1);
//		nodes.add(node2);
//		nodes.add(node3);
//		
//		edges.add(edge12);
//		edges.add(edge13);
//		edges.add(edge23);
//		
		// END OF TEST
		
		WebView matrixTextView = (WebView) findViewById(R.id.matrixText);
		WebView eigenVectorsView = (WebView) findViewById(R.id.eigenVectorsText);
		WebView eigenValuesTextView = (WebView) findViewById(R.id.eigenValuesText);
		
		matrixTextView.loadData(matrixToHtmlString(rawMatrix),"text/html","utf-8");
		eigenVectorsView.loadData(matrixToHtmlString(jamaMatrix.eig().getV().getArray()),"text/html","utf-8");
		eigenValuesTextView.loadData(Arrays.toString(jamaMatrix.eig().getRealEigenvalues()),"text/html","utf-8");
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
