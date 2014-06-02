Motley


Todos:
- Cite Duolingo PhD paper (awesome read) in foodnotes for UAP paper
- edit BroadcastReceiver paragraph under 'Android application' to the following

When an Android phone receives a push payload, it looks up all the BroadcastReceivers registered on the device. A BroadcastReceiver utilizes an intent filter so that it only responds to predefined types of notifications. This intent is found in the “action” field in the payload of a push, and tells the Motley BroadcastReceiver that this notification contains data that it should read. Upon receiving a push matching its intent filter, the Receiver parses out the JSON payload to get a JSON array of JSON objects, each of which contains the necessary information to create a card. This information is passed into the Motley data model by creating PhraseCards, which are in turn added to an ArrayAdapter containing the array of PhraseCards which make up the hand. The BroadcastReceiver then calls a data model listener, which rerenders the view of the cards in the app to show the new PhraseCards.

- add section on utilizing word frequency to get better words....
- update cards to link to the actual tip the word is from?
- REALLY NEED TO DO PoS tagging. Will need to create index.html landing page for Motley on Parse
