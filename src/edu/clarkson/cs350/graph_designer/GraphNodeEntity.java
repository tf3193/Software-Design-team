package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

import org.metalev.multitouch.controller.MultiTouchEntity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;


/**
 * This class represents a Node on the graph.
 * @author lee
 *
 */
public class GraphNodeEntity extends MultiTouchEntity {
	private ArrayList<GraphNodeEntity> connectedNodes = new ArrayList<GraphNodeEntity>();

	private final int RADIUS = 50; //px
	private Paint paint;
	
	public GraphNodeEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GraphNodeEntity(float x, float y, Paint p) {
		this.mCenterX = x;
		this.mCenterY = y;
		paint = p;
	}

	@Override
	public void draw(Canvas canvas) {
		Log.d("cs350-graph", "GraphNodeEntity draw(canvas) called");
		
		canvas.drawCircle(this.getCenterX(),
				  this.getCenterY(),
				  RADIUS, paint);
	}

	@Override
	public void load(Context context, float startMidX, float startMidY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

}
