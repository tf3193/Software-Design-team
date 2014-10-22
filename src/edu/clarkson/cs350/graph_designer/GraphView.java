package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

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

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.metalev.multitouch.controller.MultiTouchEntity;

public class GraphView extends View
						implements MultiTouchObjectCanvas<MultiTouchEntity>{

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
	private int mUIMode = UI_MODE_ROTATE;
	
	private MultiTouchController<MultiTouchEntity> multiTouchController = 
	        new MultiTouchController<MultiTouchEntity>(this);
	
	private ArrayList<GraphNodeEntity> nodes = new ArrayList<GraphNodeEntity>();
	
	private PointInfo currTouchPoint = new PointInfo();
	private Paint mLinePaintTouchPointCircle = new Paint();
	
    private static final float SCREEN_MARGIN = 100;
    
    private static float RADIUS = 50;
    
    private int width, height, displayWidth, displayHeight;
    
    //-------
	
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
	
	private void init(Context context){
		Resources res = context.getResources();
		
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.BLACK);
		
		DisplayMetrics metrics = res.getDisplayMetrics();
        this.displayWidth = res.getConfiguration().orientation == 
            Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
            metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
        this.displayHeight = res.getConfiguration().orientation == 
            Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
            metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
        
            nodes.add(new GraphNodeEntity(100, 100, mLinePaintTouchPointCircle));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("cs350-graph", "GraphView onDraw(canvas) called");
		
		for (GraphNodeEntity x : nodes){
			x.draw(canvas);
		}
		
		float[] xs = currTouchPoint.getXs();
		float[] ys = currTouchPoint.getYs();
		float[] pressures = currTouchPoint.getPressures();
		int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
		
		if (numPoints == 2){
			// draw line connecting the two nodes
		} else if (numPoints == 1){
			// draw a new node
			GraphNodeEntity tmp = new GraphNodeEntity(xs[0], ys[0], mLinePaintTouchPointCircle); 
			nodes.add(tmp);
			tmp.draw(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("cs350-graph", "GraphView onTouchEvent called");
		invalidate();
		return multiTouchController.onTouchEvent(event);
	}

	@Override
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo touchPoint) {
		float x = touchPoint.getX(), y = touchPoint.getY();
		for (GraphNodeEntity gne : nodes){
			if (gne.containsPoint(x, y)){
				return gne;
			}
		}
		return null;
	}

	@Override
	public boolean pointInObjectGrabArea(PointInfo touchPoint,
			MultiTouchEntity obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getPositionAndScale(MultiTouchEntity obj,
			PositionAndScale objPosAndScaleOut) {
		
		objPosAndScaleOut.set(obj.getCenterX(), obj.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(obj.getScaleX() + obj.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, obj.getScaleX(), obj.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, obj.getAngle());
	}

	@Override
	public boolean setPositionAndScale(MultiTouchEntity obj,
			PositionAndScale newObjPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = ((GraphNodeEntity)obj).setPos(newObjPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}

	@Override
	public void selectObject(MultiTouchEntity obj, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (obj != null){
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
