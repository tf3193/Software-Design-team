package edu.clarkson.cs350.graph_designer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
    }

    public void makeGraphFunc(View view){
    	Intent intent = new Intent(this, GraphActivity.class);
    	startActivity(intent);
    }
    
    public void instructFunc(View view){
    	Intent intent = new Intent(this, InstructActivity.class);
    	startActivity(intent);
    }
}
