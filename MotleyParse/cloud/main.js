
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
//Parse.Cloud.define("hello", function(request, response) {
	//"https://foursquare.com/oauth2/access_token ?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE"


//	response.success("Hello world!");
//});



// Custom Foursquare webhook

var express = require('express');
var app = express();

// Global app configuration section
//app.use(express.json());
//app.use(express.bodyParser());

app.use(express.urlencoded());

var fsqVersion = "20140515";


app.post('/checkin',  function(req, res) {
	//console.log(req);
	//res.send(200);
	// if (req.body.secret === "HE3FH0MMDRCRW5W2K31RDXNYJ2JHWCVZNHZ505U20TMUIQKW") {
	var checkin = JSON.parse(req.body.checkin);
	var userId = checkin.user.id;
	var venueId = checkin.venue.id;
	var venueName = checkin.venue.name;
	var venueCategory = checkin.venue.categories.name;
	// for (cat in categories) {

	// }
	// console.log("user is "+ userId +" and they checked into "+venueName);
	

	var FoursquareUser = Parse.Object.extend("FoursquareUser");
	var query = new Parse.Query(FoursquareUser);
	query.equalTo("foursquareUserId", userId);

	query.find({
		success: function(results) {
			var object = results[0];
			var deviceId = object.get("deviceId");
			console.log("Sending push notification to device "+deviceId);
			Parse.Push.send({
				channels: [ "user_"+deviceId ],
			  	data: {
			    	alert: "New cards available!",
			    	title: "Motley"
			    }
			}, 
			 	{ success: function() {
			    	console.log("Push successful")
		  		},
			  	error: function(error) {
			    	console.log("Nope. DENIED. asldfkasdf")
		  		}
			});
		},
		error: function(error) {
			alert("Error: " + error.code + " " + error.message);
		}
	});
		

	// Use Parse JavaScript SDK to create a new message and save it.
	// var Translation = Parse.Object.extend("Translation");
	// var translation = new Translation();
	// translation.save({ eng: req.body.eng }).then(
	// 	function(translation) {
	// 		res.send('Success'); }, 
	// 	function(error) {
	// 		res.status(500);
	// 		res.send('Error'); }
	// );
});
 
app.listen();
