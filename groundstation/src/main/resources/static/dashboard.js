const MAP_STATE_URL = 'http://localhost:8080/api/mapstate';
const SIGNIN_URL = 'http://localhost:8080/api/signin';
let map;
const markers = { };

async function update() {
    // Get all map objects from the API
    const response = await fetch(MAP_STATE_URL);
    const json = await response.json();

    // The server returns this structure - one object for each drone (or ranger, or fire, or ...):
    // {
    //   "7": {
    //      "latitude": 123.456,
    //      "longitude": -1.42,
    //      "markerUrl": "//maps.google.com/mapfiles/kml/pal2/icon54.png"
    //   },
    //   "13": {
    //      "latitude": 23.456,
    //      "longitude": 7.42,
    //      "markerUrl": "//maps.google.com/mapfiles/ms/micons/firedept.png"
    //   }
    // }

    // Loop over all object keys in the data from the server
    for (var key in json) {
        // Get the info about this map object
        const info = json[key];

        // Get a lat/lng position for Google Maps API
        const position = { lat: info.latitude, lng: info.longitude }

        if (key in markers) {
            console.log(`Updating position of ${key}`);
            markers[key].setPosition(position);
        }
        else {
            console.log(`Adding ${key}`);
            markers[key] = new google.maps.Marker({
                position,
                map,
                title: key,
                icon: info.markerUrl
            });
        }
    }

    window.setTimeout(update, 1000);
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: -1.93, lng: 34.76 },
        zoom: 14,
        mapTypeId: 'satellite'
    });

    update();
}

const form = document.querySelector('#login > form');
form.addEventListener('submit', async function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch(SIGNIN_URL, {
        method: 'POST',
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `username=${username}&password=${password}`
    });
    const result = await response.text();
    if (result === 'signedIn') {
        document.body.className = 'showmap';
        document.getElementById('loginerror').textContent = "";
    }
    else {
        document.getElementById('loginerror').textContent = "ACCESS DENIED!";
    }
});
