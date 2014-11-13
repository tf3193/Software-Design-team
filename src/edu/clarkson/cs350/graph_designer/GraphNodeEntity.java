package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

import org.metalev.multitouch.controller.MultiTouchEntity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.SuperscriptSpan;
import android.util.Log;


/**
 * This class represents a Node on the graph.
 * @author lee
 *
 */
public class GraphNodeEntity extends MultiTouchEntity {
	private ArrayList<GraphNodeEntity> connectedNodes = new ArrayList<GraphNodeEntity>();

	private static final int RADIUS = 50; //px
	private static Paint paint;
	
	public GraphNodeEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GraphNodeEntity(float x, float y, Paint p) {
		super.mCenterX = x;
		super.mCenterY = y;
		paint = p;
	}

	@Override
	public void draw(Canvas canvas) {
		//Log.d("cs350-graph", "GraphNodeEntity draw(canvas) called");
		
		canvas.drawCircle(this.getCenterX(),
				  this.getCenterY(),
				  RADIUS, paint);
	}
	
	public void setX(float x){
		this.mCenterX = x;
	}
	public void setY(float y){
		this.mCenterY = y;
	}
	public void setXY(float x, float y){
		this.mCenterX = x;
		this.mCenterY = y;
	}

	@Override
	public void load(Context context, float startMidX, float startMidY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int base = super.hashCode();
		base += mCenterX*mCenterY;
		base += 13;
		return base;
	}
	
	@Override
	public String toString() {
		return String.format("(%f, %f)", mCenterX, mCenterY);
	}
}
