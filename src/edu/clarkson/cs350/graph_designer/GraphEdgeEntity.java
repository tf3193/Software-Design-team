package edu.clarkson.cs350.graph_designer;

import java.io.Serializable;

import org.metalev.multitouch.controller.MultiTouchEntity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * This class represents an Edge on the graph
 * @author lee
 *
 */
public class GraphEdgeEntity extends MultiTouchEntity implements Serializable {
	GraphNodeEntity node1;
	GraphNodeEntity node2;
	
	private static Paint paint;
	
	public GraphEdgeEntity(GraphNodeEntity node1, GraphNodeEntity node2, Paint p) {
		this.node1 = (GraphNodeEntity) node1;
		this.node2 = (GraphNodeEntity) node2;
		paint = p;
	}
	
	public GraphNodeEntity getNode1() {
		return node1;
	}
	
	public GraphNodeEntity getNode2() {
		return node2;
	}
	
	public void setNode1(GraphNodeEntity node1) {
		this.node1 = node1;
	}
	
	public void setNode2(GraphNodeEntity node2) {
		this.node2 = node2;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(node1.getCenterX(), node1.getCenterY(),
						node2.getCenterX(), node2.getCenterY(),
						paint);
	}

	@Override
	public void load(Context context, float startMidX, float startMidY) {
		
	}

	@Override
	public void unload() {
		
	}
	
	@Override
	public String toString() {
		if (node1 == null || node2 == null){
			return "[xxx-xxx]";
		}
		return String.format("[%s-%s]", node1.toString(), node2.toString());
	}
}
