var versionParam = "?v=20140515"; // REQUIRED FOR ALL REQUESTS
var userlessReqParams = "&client_id=TTCJXDW5NEQGVG3B0POP3NWWWRSDTHQAF1HVN4J1M0UJUWVC&client_secret=24VLRLQCZIMQB1FN3HVJZGXOUEDRBPTUKNRMZ1QOVTVHHIOK";

// var lexer = require('cloud/lexer.js');
// var lexicon = require('cloud/lexicon.js');
// var postagger = require('cloud/POSTagger.js');

var express = require('express');
var app = express();

// Global app configuration section
app.use(express.urlencoded());

app.post('/checkin',  function(req, res) {
	// if (req.body.secret === "HE3FH0MMDRCRW5W2K31RDXNYJ2JHWCVZNHZ505U20TMUIQKW") {
	var checkin = JSON.parse(req.body.checkin);
	var userId = checkin.user.id;
	var venueId = checkin.venue.id;
	var venueName = checkin.venue.name;
	var categories = checkin.venue.categories;
	var categoryNames = []
	for (var i=0; i<categories.length; i++) {
		categoryNames.push(categories[i].name.toLowerCase());

		var nameWords = categories[i].name.toLowerCase().split(/\s/);
		if (nameWords.length>1)
			categoryNames.push.apply(categoryNames, nameWords);
	}

	//console.log("user is "+ userId +" and they checked into "+venueName);
	var promises = [];

	var FoursquareUser = Parse.Object.extend("FoursquareUser");
	var userQuery = new Parse.Query(FoursquareUser);
	userQuery.equalTo("foursquareUserId", userId);
	promises.push(userQuery.first());

	var tipsToTranslations = Parse.Cloud.httpRequest({ url: 'https://api.foursquare.com/v2/venues/'+venueId+'/tips'+versionParam+userlessReqParams+"&sort=popular"}).then(function(httpResponse) {
		//res.send(200);
		
		var tips = httpResponse.data.response.tips;
		var words = categoryNames;
		for (var i=0; i<Math.min(tips.count, 5); i++) {
			var tokens = tips.items[i].text.split(/[\s,.!-:"@#$%^&*()]+/);
			for (var j=0; j<tokens.length; j++) {
				var token = tokens[j];
				if (token.length>3 && words.indexOf(token)<0)
					words.push(token.toLowerCase());
			}
			// var tipText = new Lexer().lex(tips.items[i].text);
			// var taggedWords = new POSTagger().tag(tipText);
			// for (i in taggedWords) {
			//     var taggedWord = taggedWords[i];
			//     var word = taggedWord[0];
			//     var tag = taggedWord[1];
			    
			//     if (word.length > 1 && tag.substr(0, 2) == 'NN') {
			//     	words.push(word);
			//     }
			// }
		}
		console.log(words);
		var Translation = Parse.Object.extend("Translation");
		var translationQuery = new Parse.Query(Translation);
		translationQuery.containedIn("eng", words);
		return translationQuery.find();
	},
	function(httpResponse) {
		console.error('Request failed with response code ' + httpResponse.status);
	});
	promises.push(tipsToTranslations);

	Parse.Promise.when(promises).then(function(user, translations){
		var deviceId = user.get("deviceId");
		//console.log(deviceId);
		//console.log(translations);
		pairs =[{
			// id: translations[0].get("def_id"),
			eng: translations[0].get("eng"),
			fr: translations[0].get("fr")
		}];
		
		//this is a terrible hack...
		alreadyHave = [translations[0].get("eng")];

		for (var i =1; i<translations.length; i++) {
			var englishword = translations[i].get("eng");
			if (alreadyHave.indexOf(englishword)<0) {
				alreadyHave.push(englishword);
				pairs.push({
					// id: translations[i].get("def_id"),
					eng: englishword,
					fr: translations[i].get("fr")
				});
			}
		}
		//console.log(JSON.stringify(pairs));
		return Parse.Push.send({
			channels: [ "user_"+deviceId ],
		  	data: {
		  		alert: "New cards available!",
		     	title: "Motley",
		    	// message: "New cards available!",
		    	// header: "Motley",
		    	action: "edu.mit.motley.NEW_CARDS",
		    	venue: venueName,
				link: "http://foursquare.com/venue/"+venueId,
		    	cards: pairs
		    }
		});
	}).then(function() {
		console.log("Sent push notification to device!");
		res.send(200);
	});
});
 
app.listen();
