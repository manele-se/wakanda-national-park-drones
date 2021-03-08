const MAP_STATE_URL = 'http://localhost:8080/api/mapstate';
const SIGNIN_URL = 'http://localhost:8080/api/signin';
let map;
const markers = { };
const tandemLines = { };

function handleMapObjectWithPartner(key, info) {
    if (info.partner in markers) {
        const partnerMarker = markers[info.partner];
        const partnerPosition = partnerMarker.getPosition();

        console.log('Add or update a line from ' + position + ' to ' + partnerPosition);
        if (key in tandemLines) {
            // Line already exists!
            let line = tandemLines[key];
            const path = line.getPath();
            // Update start and end positions
            // First: Add the new start and end positions to the polyline
            path.push(new google.maps.LatLng(position));
            path.push(partnerPosition);
            // Second: Remove the old start and end positions
            path.removeAt(0);
            path.removeAt(0);
        }
        else {
            // Create a new line
            let line = new google.maps.Polyline({
                path: [
                    position,
                    partnerPosition
                ],
                strokeColor: '#FFFF00',
                strokeWeight: 2,
                strokeOpacity: 1.0
            });
            line.setMap(map);
            tandemLines[key] = line;
        }
    }
}

function handleMapObject(key, info) {
    // Get a lat/lng position for Google Maps API
    const position = { lat: info.latitude, lng: info.longitude }
    let marker;

    if (key in markers) {
        console.log(`Updating position of ${key}`);
        markers[key].setPosition(position);
    }
    else {
        console.log(`Adding ${key}`);
        const size = info.markerSize;
        markers[key] = new google.maps.Marker({
            position,
            map,
            title: key,
            icon: {
                url: info.markerUrl,
                anchor: new google.maps.Point(size / 2, size / 2),
                scaledSize: new google.maps.Size(size, size)
            },
            zIndex: info.zindex
        });
    }

    if (info.partner) {
        handleMapObjectWithPartner(key, info);
    }
}

async function update() {
    // Get all map objects from the API
    try {
        const response = await fetch(MAP_STATE_URL);
        const json = await response.json();

        // Loop over all object keys in the data from the server
        for (var key in json) {
            // Get the info about this map object
            const info = json[key];
            handleMapObject(key, info);
        }

        window.setTimeout(update, 500);
    }
    catch (e) {
        console.error(e);
        window.setTimeout(update, 5000);
    }

}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: -1.9353, lng: 34.743 },
        zoom: 15,
        mapTypeId: 'satellite'
    });

    update();
}

// Handle login form
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
