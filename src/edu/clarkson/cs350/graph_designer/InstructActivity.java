package edu.clarkson.cs350.graph_designer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InstructActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instruct);
	}

    public void mainMenuFunc(View view){
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
}
