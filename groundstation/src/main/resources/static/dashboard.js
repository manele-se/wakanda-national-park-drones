const url = 'http://localhost:8080/api/mapstate';
let map;
const markers = { };

async function update() {
    // Get all map objects from the API
    const response = await fetch(url);
    const json = await response.json();

    // The server returns this structure - one object for each drone (or ranger, or fire, or ...):
    // {
    //   "7": {
    //      "latitude": 123.456,
    //      "longitude": -1.42
    //   },
    //   "13": {
    //      "latitude": 23.456,
    //      "longitude": 7.42
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
                title: key
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
