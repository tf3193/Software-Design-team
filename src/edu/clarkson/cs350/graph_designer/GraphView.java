package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
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

/*
 * Known Bugs:
 * TODO: Random nodes created when dragging an existing node
 * 			Seems to be because the multi-touch library doesn't move the Node object to your finger.
 * TODO: If the user just taps with two fingers, a node is created at the midpoint between them.
 */

public class GraphView extends View implements
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

	private static final float SCREEN_MARGIN = 100;

	private GraphNodeEntity currentDragNode = new GraphNodeEntity(-99, -99,
			mLinePaintTouchPointCircle);

	private boolean dragging = false;

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
		// nodes.add(new GraphNodeEntity(100, 100, mLinePaintTouchPointCircle));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*
		 * This method is called any time the screen needs to be re-drawn (when
		 * invalidate() is called).
		 */
		// Log.d("cs350-graph", "nodes: " + nodes.toString());

		super.onDraw(canvas);
		canvas.drawColor(Color.BLUE);
		// Draw every node in the "nodes" ArrayList
		for (GraphNodeEntity x : nodes) {
			x.draw(canvas);
		}
		for (GraphEdgeEntity x : edges) {
			x.draw(canvas);
		}

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
			if (nodeUnderFinger1 != null && nodeUnderFinger2 != null) {
				GraphEdgeEntity newEdge = new GraphEdgeEntity(nodeUnderFinger1,
						nodeUnderFinger2, mLinePaintTouchPointCircle);
				edges.add(newEdge);
				newEdge.draw(canvas);
			}
		} 
		else if (numPoints == 1) {
			if (getDraggableObjectAtPoint(currTouchPoint) == null && !dragging) {
				Log.d("cs350-graph", "not removing");
				// draw a new node
				currentDragNode.setXY(xs[0], ys[0]);
				currentDragNode.draw(canvas);
			}
			
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP
				&& currentDragNode.getCenterX() >= 0 && !dragging) {
			// If the touch action is lifting their finger
			// and "currentDragNode" has changed
			Log.d("cs350-graph",
					"Adding new node " + currentDragNode.toString()
							+ " to nodes list");
			nodes.add(currentDragNode);
			currentDragNode = new GraphNodeEntity(-99, -99,
					mLinePaintTouchPointCircle);
		}
		
		invalidate();
		return multiTouchController.onTouchEvent(event);
	}

	@Override
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo touchPoint) {
		Log.d("cs350-graph", "GraphView getDraggableObjectAtPoint called");
		currTouchPoint.set(touchPoint);
		//invalidate();

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
		return obj.containsPoint(touchPoint.getX(), touchPoint.getY());
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
			dragging = true;
			/*
			 * nodes.remove(obj); nodes.add((GraphNodeEntity) obj);
			 */
		} else {
			// Drag stops
			dragging = false;
		}
		invalidate();
	}

	public ArrayList<GraphNodeEntity> getNodes() {
		return nodes;
	}
	public ArrayList<GraphEdgeEntity> getEdges() {
		return edges;
	}
}
