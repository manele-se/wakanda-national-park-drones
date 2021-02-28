const url = 'http://localhost:8080/api/mapstate';
let map;
const markers = { };

async function update() {
    const response = await fetch(url);
    const json = await response.json();
    const existingMarkerKeys = new Set(Object.keys(markers));
    console.log({existingMarkerKeys});

    for (var key in json) {
        if (key in markers) {
            console.log(`Updating position of ${key}`);
            existingMarkerKeys.delete(key);
        }
        else {
            const info = json[key];
            const position = { lat: info.latitude, lng: info.longitude }
            markers[key] = new google.maps.Marker({
                position,
                map,
                title: key}
            );
            console.log(`Adding ${key}`);
        }
    }

    console.log('Markers to remove', existingMarkerKeys);
    for (var keyToRemove of existingMarkerKeys) {
        const markerToRemove = markers[keyToRemove];
        markerToRemove.setMap(null);
        delete markers[keyToRemove];
    }

    window.setTimeout(update, 5000);
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: -1.93, lng: 34.76 },
        zoom: 8,
        mapTypeId: 'satellite'
    });

    update();
}
