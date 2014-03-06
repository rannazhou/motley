package edu.mit.motley;

import java.util.ArrayList;

public class APIResponse {
	ArrayList <FacebookPost> data;

    public class FacebookPost {
        public String message;
        public String created_time;
    }
	
}
