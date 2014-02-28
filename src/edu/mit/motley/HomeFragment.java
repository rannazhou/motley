package edu.mit.motley;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	private static final String TAG = "HomeFragment";
    
	@Override
	public void onCreate(Bundle savedBundleInstance) {
		super.onCreate(savedBundleInstance);
		(new CardMethods()).retrieveStatus();
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		
//		(new CardMethods()).retrieveStatus();
//		
//		//inflater.inflate(R.layout.home, null);
//		//TextView text = (TextView) this.getActivity().findViewById(R.id.translation_text);
//		
//		return container;
//	}
}
