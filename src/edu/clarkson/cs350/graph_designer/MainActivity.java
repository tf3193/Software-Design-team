package edu.clarkson.cs350.graph_designer;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    int clicked = 0;
	private GraphView graphView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        graphView = new GraphView(this);
		setContentView(R.layout.activity_main);
		
        //setContentView(graphView);
    }

    public void clickFunc(View view){
    	clicked = 1;
    	setContentView(graphView);
    }
 
    //TODO this is very crude still needs to be edited was just a temp to see how this worked.
    @Override
    public void onBackPressed(){
         if(clicked == 1){
        	 clicked = 0;
             setContentView(R.layout.activity_main);
             //Change activity to previous view
         }
         else 
             super.onBackPressed();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
