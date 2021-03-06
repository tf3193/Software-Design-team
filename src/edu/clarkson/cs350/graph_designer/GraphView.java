package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.metalev.multitouch.controller.MultiTouchEntity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GraphView extends SurfaceView implements
		MultiTouchObjectCanvas<MultiTouchEntity> {
	
	private static final boolean DEBUG = true;
	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
	private int mUIMode = UI_MODE_ROTATE;

	private MultiTouchController<MultiTouchEntity> multiTouchController = new MultiTouchController<MultiTouchEntity>(
			this);

	private ArrayList<GraphNodeEntity> nodes = new ArrayList<GraphNodeEntity>();
	private ArrayList<GraphEdgeEntity> edges = new ArrayList<GraphEdgeEntity>();

	private PointInfo currTouchPoint = new PointInfo();
	private Paint mLinePaintTouchPointCircle = new Paint();

	private boolean dragging = false;
	private boolean deleteMode = false;

	private int displayWidth, displayHeight;
	private boolean qwalkIsRunning = false;
	
	private double[][] matrix;
	private final Resources res = getResources();
	private final Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.graphbackground);
	
	
	// -------

	public GraphView(Context context) {
		this(context, null);
	}

	public GraphView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void resetindicies(){
		int size = nodes.size();
		for(int i=0; i<size; i++){
			GraphNodeEntity g = nodes.get(i);
			g.setIndex(i+1);
		}
	}
	
	private void init(Context context) {
		Resources res = context.getResources();

		// Set up the colors and line width of the circles and lines
		mLinePaintTouchPointCircle.setColor(Color.rgb(0, 139, 139));
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.FILL);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundResource(R.drawable.graphbackground);
		
		DisplayMetrics metrics = res.getDisplayMetrics();
		this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.max(metrics.widthPixels, metrics.heightPixels) : Math.min(
				metrics.widthPixels, metrics.heightPixels);
		this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.min(metrics.widthPixels, metrics.heightPixels) : Math.max(
				metrics.widthPixels, metrics.heightPixels);

		// Manually adding nodes/edges to test
		nodes.add(new GraphNodeEntity(100, 100, mLinePaintTouchPointCircle, nodes.size()+1));
		nodes.add(new GraphNodeEntity(500, 100, mLinePaintTouchPointCircle, nodes.size()+1));
		nodes.add(new GraphNodeEntity(300, 300, mLinePaintTouchPointCircle, nodes.size()+1));
		
		edges.add(new GraphEdgeEntity(nodes.get(0), nodes.get(1), mLinePaintTouchPointCircle));
		edges.add(new GraphEdgeEntity(nodes.get(0), nodes.get(2), mLinePaintTouchPointCircle));
		edges.add(new GraphEdgeEntity(nodes.get(1), nodes.get(2), mLinePaintTouchPointCircle));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*
		 * This method is called any time the screen needs to be re-drawn (when
		 * invalidate() is called).
		 */

		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		Rect rect = new Rect(0,0,this.displayWidth,this.displayHeight);
		canvas.drawBitmap(bitmap, null, rect, null);
		// Draw every node in the "nodes" ArrayList
		// Set xs and ys to arrays of points, one for each finger
		// Set numPoints to the number of fingers touching the screen
		float[] xs = currTouchPoint.getXs();
		float[] ys = currTouchPoint.getYs();
		int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);

		if (numPoints == 2) {
			// draw line connecting the two nodes
			GraphNodeEntity nodeUnderFinger1 = null;
			GraphNodeEntity nodeUnderFinger2 = null;
			for (GraphNodeEntity gne : nodes) {
				for (int i = 0; i < xs.length; i++) {
					if (gne.containsPoint(xs[i], ys[i])) {
						if (nodeUnderFinger1 == null)
							nodeUnderFinger1 = gne;
						else
							nodeUnderFinger2 = gne;
					}
				}
			}
			if (nodeUnderFinger1 != null && nodeUnderFinger2 != null && !qwalkIsRunning) {
				int edgeexist = checkforedge(nodeUnderFinger1, nodeUnderFinger2);
				if(edgeexist != -1){
					edges.remove(edgeexist);
				}else{
					GraphEdgeEntity newEdge = new GraphEdgeEntity(nodeUnderFinger1,
							nodeUnderFinger2, mLinePaintTouchPointCircle);
					edges.add(newEdge);
				}
			}
		} 
		for (GraphNodeEntity x : nodes) {
			x.draw(canvas);
		}
		for (GraphEdgeEntity x : edges) {
			x.draw(canvas);
		}
	}
	
	//Checks to see if an edge already exists between two nodes and returns index
	public int checkforedge(GraphNodeEntity n1, GraphNodeEntity n2){
		int size = edges.size();
		for(int i = 0; i<size; i++){
			if((edges.get(i).getNode1() == n1 && edges.get(i).getNode2() == n2) ||
					(edges.get(i).getNode1() == n2 && edges.get(i).getNode2() == n1)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP
				&& !dragging) {
			if(!qwalkIsRunning && !deleteMode && nodes.size() < 15){
				nodes.add(new GraphNodeEntity(event.getX(), event.getY(), mLinePaintTouchPointCircle, nodes.size()+1));
			}
		}
		invalidate();
		return multiTouchController.onTouchEvent(event);
	}

	@Override
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);

		float x = touchPoint.getX(), y = touchPoint.getY();
		for (GraphNodeEntity gne : nodes) {
			if (gne.containsPoint(x, y) && !qwalkIsRunning) {
				dragging = true;
				return gne;
			}
		}
		dragging = false;
		return null;
	}

	@Override
	public boolean pointInObjectGrabArea(PointInfo touchPoint,
			MultiTouchEntity obj) {
		return obj.containsPoint(touchPoint.getX(), touchPoint.getY());
	}

	@Override
	public void getPositionAndScale(MultiTouchEntity obj,
			PositionAndScale objPosAndScaleOut) {

		objPosAndScaleOut.set(obj.getCenterX(), obj.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(obj.getScaleX() + obj.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, obj.getScaleX(),
				obj.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
				obj.getAngle());
	}

	@Override
	public boolean setPositionAndScale(MultiTouchEntity obj,
			PositionAndScale newObjPosAndScale, PointInfo touchPoint) {

		currTouchPoint.set(touchPoint);
		
		if (currTouchPoint.getNumTouchPoints() == 1){
			((GraphNodeEntity) obj).setX(touchPoint.getX());
			((GraphNodeEntity) obj).setY(touchPoint.getY());
			invalidate();
		}

		return true;
	}

	@Override
	public void selectObject(MultiTouchEntity obj, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (obj != null) {
			if (deleteMode){
				nodes.remove((GraphNodeEntity) obj);
				
				for (int i=0; i<edges.size(); i++){
					if (edges.get(i).getNode1() == (GraphNodeEntity)obj || edges.get(i).getNode2() == (GraphNodeEntity)obj){
						edges.remove(i);
						i--;
					}
				}
				resetindicies();
			}
		}
		invalidate();
	}

	public ArrayList<GraphNodeEntity> getNodes() {
		return nodes;
	}
	public ArrayList<GraphEdgeEntity> getEdges() {
		return edges;
	}
	
	public boolean isDeleteMode(){
		return deleteMode;
	}
	public void setDeleteMode(boolean a){
		deleteMode = a;
	}
	
	public void calculateMatrix(){
		int n = nodes.size();
		matrix = new double[n][n];
		
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
			
			matrix[i][j] = 1.0;
			matrix[j][i] = 1.0; // Necessary for bi-directional graphs
		}
	}
	public double[][] getAdjMatrix(){
		return matrix;
	}
	
	public boolean getqwalkIsRunning(){
		return qwalkIsRunning;
	}

	public void qwalk_me(){
		qwalkIsRunning = true;
		
		int n = nodes.size();
    	double t = 0.0;
    	double t_delta = 0.01;
    	
    	Complex[][] c = new Complex[n][n];
    	
        for (int i = 0; i<n; i++){
        	for (int j = 0; j<n; j++){
            	c[i][j] = new Complex(matrix[i][j]);
            }
        }
        
        Complex[] ampl = new Complex[n];
    	double[] prob = new double[n];
    	
    	for (int i=0; i<n; i++){
    		if (i==0){
    			prob[i]=1.0;
    			ampl[i]=new Complex(1.0);
    		}else{
    			prob[i]=0.0;
    			ampl[i]=new Complex(0.0);
    		}
    	}
        int[] my_color = new int[n];
        
        while (qwalkIsRunning){
        	QuantumWalk.setN(n);
        	Array2DRowFieldMatrix<Complex> U = QuantumWalk.qwalk(MatrixUtils.createRealMatrix(matrix), t);
        	
        	ampl = U.getColumn(0); //ampl=[U[i][0] for i in range(num_rows)]
        	for (int i = 0; i<n; i++){
        		prob[i] = (ampl[i].multiply(ampl[i].conjugate())).getReal();
        		my_color[i] = (int)(prob[i]*255); // Use as red component of RGB color
        		nodes.get(i).setColor(Color.rgb(my_color[i], 0, 0)); // Set the color of the node
        		
        		// Redraw the display on the UI thread
        		((Activity) this.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						invalidate();	
					}
				});
        		
        	}
        	
        	t += t_delta;
        	
        	// Sleep to slow down the walk
        	try {
				Thread.sleep(1,0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	public void stop_qwalk(){
		qwalkIsRunning = false;
	}
}
