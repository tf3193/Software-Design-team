package edu.clarkson.cs350.graph_designer;

import java.io.Serializable;

import org.metalev.multitouch.controller.MultiTouchEntity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;


/**
 * This class represents a Node on the graph.
 * @author lee
 *
 */
public class GraphNodeEntity extends MultiTouchEntity implements Serializable {
	
	private static final int RADIUS = 70; //px
	private Paint paint;
	
	public GraphNodeEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GraphNodeEntity(float x, float y, Paint p) {
		super.mCenterX = x;
		super.mCenterY = y;
		
		paint = new Paint();
		paint.setColor(p.getColor());
		paint.setStrokeWidth(p.getStrokeWidth());
		paint.setStyle(p.getStyle());
		paint.setAntiAlias(p.isAntiAlias());
	}

	@Override
	public void draw(Canvas canvas) {
		//Log.d("cs350-graph", "GraphNodeEntity draw(canvas) called");
		float x = this.getCenterX();
		float y = this.getCenterY();
		canvas.drawCircle(x,y,RADIUS,paint);
		String str = "("+x+","+y+")";
		int size = str.length();
		Paint difpaint = new Paint();
		difpaint.setStrokeWidth(paint.getStrokeWidth());
		difpaint.setColor(Color.WHITE);
		difpaint.setStyle(paint.getStyle());
		difpaint.setAntiAlias(paint.isAntiAlias());
		canvas.drawText(str, x-RADIUS, y-RADIUS, difpaint);
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
	
	public void setColor(int color){
		paint.setColor(color);
	}
	
	@Override
	public boolean containsPoint(float touchX, float touchY) {
		float x_delta = touchX - mCenterX;
		float y_delta = touchY - mCenterY;
		
		return (x_delta*x_delta) + (y_delta*y_delta) <= (RADIUS*RADIUS);
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
		GraphNodeEntity obj = new GraphNodeEntity();
		obj.mCenterX = this.mCenterX;
		obj.mCenterY = this.mCenterY;
		obj.paint = this.paint;
		return obj;
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
