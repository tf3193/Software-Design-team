package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.metalev.multitouch.controller.MultiTouchEntity;

public class GraphView extends View implements
		MultiTouchObjectCanvas<MultiTouchEntity> {

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
	private int mUIMode = UI_MODE_ROTATE;

	private MultiTouchController<MultiTouchEntity> multiTouchController = new MultiTouchController<MultiTouchEntity>(
			this);

	private ArrayList<GraphNodeEntity> nodes = new ArrayList<GraphNodeEntity>();

	private PointInfo currTouchPoint = new PointInfo();
	private Paint mLinePaintTouchPointCircle = new Paint();

	private static final float SCREEN_MARGIN = 100;

	private GraphNodeEntity currentDrag = new GraphNodeEntity(0,0,mLinePaintTouchPointCircle);
	
	private int width, height, displayWidth, displayHeight;

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

	private void init(Context context) {
		Resources res = context.getResources();
		
		// Set up the colors and line width of the circles and lines
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.BLACK);

		DisplayMetrics metrics = res.getDisplayMetrics();
		this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.max(metrics.widthPixels, metrics.heightPixels) : Math.min(
				metrics.widthPixels, metrics.heightPixels);
		this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.min(metrics.widthPixels, metrics.heightPixels) : Math.max(
				metrics.widthPixels, metrics.heightPixels);
		
		// Manually adding a node to test
		//nodes.add(new GraphNodeEntity(100, 100, mLinePaintTouchPointCircle));
				
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/* This method is called any time the screen needs to be re-drawn
		 * (when invalidate() is called).
		 */
		Log.d("cs350-graph", "onDraw called");
		Log.d("cs350-cur", "nodes: " + nodes.toString());
		
		super.onDraw(canvas);
		canvas.drawColor(Color.BLUE);
		// Draw every node in the "nodes" ArrayList
		for (GraphNodeEntity x : nodes) {
			x.draw(canvas);
		}
		
		// Set xs and ys to arrays of points covered by the user's finger
		// Set numPoints to the number of fingers touching the screen
		float[] xs = currTouchPoint.getXs();
		float[] ys = currTouchPoint.getYs();
		int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);

		/*if (numPoints == 2) {
			// draw line connecting the two nodes
		} else if (numPoints == 1) {
			// draw a new node
			Log.d("cs350-graph", "Creating node at " + xs[0] + "," + ys[0]);
			currentDrag.setXY(xs[0], ys[0]);
			currentDrag.draw(canvas);
		}*/
		if (numPoints == 2) {
			// draw line connecting the two nodes
			} else if (numPoints == 1) {

			GraphNodeEntity movent = (GraphNodeEntity) getDraggableObjectAtPoint(currTouchPoint);
			if(movent == null){// draw a new node
			Log.d("cs350-graph", "Creating node at " + xs[0] + "," + ys[0]);
			currentDrag.setXY(xs[0], ys[0]);
			currentDrag.draw(canvas);
			}else{
			Log.d("cs350-graph", "Moving node to " + xs[0] + "," + ys[0]);
			movent.setXY(xs[0], ys[0]);
			movent.draw(canvas);
			}
			}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP){
			// If the touch action is lifting their finger
			nodes.add(currentDrag);
			currentDrag = new GraphNodeEntity(0,0,mLinePaintTouchPointCircle);
		}
		return multiTouchController.onTouchEvent(event);
	}

	@Override
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		invalidate();
		
		float x = touchPoint.getX(), y = touchPoint.getY();
		for (GraphNodeEntity gne : nodes) {
			if (gne.containsPoint(x, y)) {
				return gne;
			}
		}
		return null;
	}

	@Override
	public boolean pointInObjectGrabArea(PointInfo touchPoint,
			MultiTouchEntity obj) {
		Log.d("cs350-graph", "GraphView pointInObjectGrabArea called");
		return false;
	}

	@Override
	public void getPositionAndScale(MultiTouchEntity obj,
			PositionAndScale objPosAndScaleOut) {
		Log.d("cs350-graph", "GraphView getPositionAndScale called");
		
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
		Log.d("cs350-graph", "GraphView setPositionAndScale called");
		
		currTouchPoint.set(touchPoint);
		boolean ok = ((GraphNodeEntity) obj).setPos(newObjPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}

	@Override
	public void selectObject(MultiTouchEntity obj, PointInfo touchPoint) {
		Log.d("cs350-graph", "GraphView selectObject called");
		currTouchPoint.set(touchPoint);
		if (obj != null) {
			// Received a valid node object,
			// push it to the top of the stack
			nodes.remove(obj);
			nodes.add((GraphNodeEntity) obj);
		} else {
			// Drag stops
		}
		invalidate();
	}

}
