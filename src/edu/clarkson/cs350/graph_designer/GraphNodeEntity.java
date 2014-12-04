package edu.clarkson.cs350.graph_designer;

import java.io.Serializable;

import org.metalev.multitouch.controller.MultiTouchEntity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


/**
 * This class represents a Node on the graph.
 * @author lee
 *
 */
public class GraphNodeEntity extends MultiTouchEntity implements Serializable {
	
	private static final int RADIUS = 70; //px
	private Paint paint;
	private int index;
	
	public GraphNodeEntity() {
		super();
	}

	public GraphNodeEntity(float x, float y, Paint p, int ind) {
		super.mCenterX = x;
		super.mCenterY = y;
		paint = new Paint();
		paint.setColor(p.getColor());
		paint.setStrokeWidth(p.getStrokeWidth());
		paint.setStyle(p.getStyle());
		paint.setAntiAlias(p.isAntiAlias());
		index = ind;
	}

	@Override
	public void draw(Canvas canvas) {
		float x = this.getCenterX();
		float y = this.getCenterY();
		canvas.drawCircle(x,y,RADIUS,paint);
		String str = ""+index;
		Paint drawpaint = new Paint();
		drawpaint.setTextSize(20);
		drawpaint.setColor(Color.BLACK);
		canvas.drawText(str, x-RADIUS, y-RADIUS, drawpaint);
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
	
	public void setIndex(int ind){
		index = ind;
	}
	
	@Override
	public boolean containsPoint(float touchX, float touchY) {
		float x_delta = touchX - mCenterX;
		float y_delta = touchY - mCenterY;
		
		return (x_delta*x_delta) + (y_delta*y_delta) <= (RADIUS*RADIUS);
	}

	@Override
	public void load(Context context, float startMidX, float startMidY) {

	}

	@Override
	public void unload() {

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
