require("dotenv").config();
const twit = require('./twit');

//Function to post tweet
function tweeet(aux) {
  twit.post('statuses/update', { status: 'Test number '+ aux}, function (err, data, response) {
    console.log(data)
  });

}

tweeet(20);


setTimeout(tweeet.bind(null, 22), 5000);


