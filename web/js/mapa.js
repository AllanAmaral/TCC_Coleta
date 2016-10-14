var map;
var idInfoBoxAberto;
var infoBox = [];
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();

function initialize() {	
    directionsDisplay = new google.maps.DirectionsRenderer();
    var latlng = new google.maps.LatLng(-30.032217, -51.215083);
	
    var options = {
        zoom: 16,
	center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("mapa"), options);
    directionsDisplay.setMap(map);
    directionsDisplay.setPanel(document.getElementById("trajeto-texto"));
    carregarPontos();
    carregarInfoLixeiras();
}

initialize();

function abrirInfoBox(id, marker) {
	if (typeof(idInfoBoxAberto) == 'number' && typeof(infoBox[idInfoBoxAberto]) == 'object') {
		infoBox[idInfoBoxAberto].close();
	}

	infoBox[id].open(map, marker);
	idInfoBoxAberto = id;
}

function desenharRota(latitude_A, longitude_A, latitude_B, longitude_B, wayPoint){

    directionsPanel = document.getElementById("directionsPanel");
    //Limpa o painel de qualquer html
    directionsPanel.innerHTML = "";

    var directionsDisplay = new google.maps.DirectionsRenderer({
        'map': map,
        'preserveViewport': true,
        'draggable': true,
        'hideRouteList': true
    });

    directionsDisplay.setPanel(directionsPanel);

    var request = {
        origin: new google.maps.LatLng(latitude_A, longitude_A),
        destination: new google.maps.LatLng(latitude_B, longitude_B),
        travelMode: google.maps.DirectionsTravelMode.DRIVING,
        provideRouteAlternatives: true,
        drivingOptions: {
        departureTime: new Date(Date.now()),
        trafficModel: "pessimistic"
                        }
    };

    directionsService.route(request, function (response, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
        }
    }); 


    marker[0].setPosition(null);
    marker[1].setPosition(null);
        google.maps.event.addListener(directionsDisplay, 'directions_changed', function () {
        var total = 0;
        var tempo = 0;
        var velocidade_Media = 0;
        var myroute = (directionsDisplay.getDirections()).routes[0];
        for (var i = 0; i < myroute.legs.length; i++) {
                total += myroute.legs[i].distance.value;
        }
        total = total / 1000;
        tempo = (((myroute.legs[0].duration.value)/60)/60);

        alert(myroute.legs[0].start_location + "\n" + myroute.legs[0].end_location);

        for(var x = 0; x < myroute.legs.length; x++){

        }
        percorre[0]  = myroute.legs[0].steps[0].end_location;
        percorre[1]  = myroute.legs[0].steps[1].end_location;
        percorre[2]  = myroute.legs[0].steps[2].end_location;

        alert(percorre[0] +"\n"+ percorre[1]) +"\n"+ percorre[2];

      alert("A velocidade media nesse trecho é de: "+(total/tempo));
        velocidade = (total/tempo).toString();

    }); 
        google.maps.event.addListener(map, 'click', function(event) {
            if(marker_contador == 0){
                marker[0].setPosition(event.latLng);
            }
            if(marker_contador == 1){
                marker[1].setPosition(event.latLng);
                latitude_A = marker[0].getPosition().lat();
                longitude_A = marker[0].getPosition().lng();
                latitude_B = marker[1].getPosition().lat();
                longitude_B = marker[1].getPosition().lng();

                desenharRota(latitude_A, longitude_A, latitude_B, longitude_B);
            }
            if(marker_contador < 2){
                marker_contador++;
            }

    });
}

function carregarInfoLixeiras() {
    $.getJSON('js/arestas.json', function(arestas) {
        
        $.each(arestas, function(index, aresta) {
            var service = new google.maps.DistanceMatrixService();
            var transito = new Object();
            transito.IdOrigem = aresta.IdOrigem;
            transito.TipoOrigem = aresta.TipoOrigem;
            transito.IdDestino = aresta.IdDestino;
            transito.TipoDestino = aresta.TipoDestino;
            
            service.getDistanceMatrix(
              {
                  origins: new google.maps.LatLng(aresta.OrigemLatitude, aresta.OrigemLongitude),
                  destinations: new google.maps.LatLng(aresta.DestinoLatitude, aresta.DestinoLongitude),
                  travelMode: google.maps.TravelMode.DRIVING,
                  unitSystem: google.maps.UnitSystem.METRIC
              }, function(response, status) {
                if (status !== google.maps.DistanceMatrixStatus.OK) {
                    transito.Transito = status;
                    alert('Error was: ' + status);
                } else {
                    transito.Transito = response.rows[0].elements[0].duration.text;
                    alert('Error was: ' + response.rows[0].elements[0].duration.text);
                }
               }
           );
           transito.toJSON = function(key)
           {
               var replacement = new Object();
               for (var val in this)
               {
                   if (typeof (this[val]) === 'string')
                       replacement[val] = this[val].toUpperCase();
                   else
                       replacement[val] = this[val];
               }
               return replacement;
           };
           
           var jsonText = JSON.stringify(transito);
           System.IO.File.WriteAllText('js/infolixeiras.json', jsonText);
        });
    });
}

function carregarPontos() {   
    $.getJSON('js/pontos.json', function(pontos) {

        var latlngbounds = new google.maps.LatLngBounds();

        $.each(pontos, function(index, ponto) {

            switch (ponto.Cor) {
                case 1:
                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(ponto.Latitude, ponto.Longitude),
                        title: "ID: " + ponto.Id 
                                + "\n" + ponto.Descricao,
                        map: map,
                        icon: 'resources/img/lixeira-vermelha-icon.png'
                    });
                    break;
                case 2:
                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(ponto.Latitude, ponto.Longitude),
                        title: "ID: " + ponto.Id 
                                + "\n" + ponto.Descricao,
                        map: map,
                        icon: 'resources/img/lixeira-amarela-icon.png'
                    });
                    break;
                case 3:
                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(ponto.Latitude, ponto.Longitude),
                        title: "ID: " + ponto.Id 
                                + "\n" + ponto.Descricao,
                        map: map,
                        icon: 'resources/img/lixeira-azul-icon.png'
                    });
                    break;
            }

            var myOptions = {
                content: "<p>" + ponto.Descricao + "</p>",
                pixelOffset: new google.maps.Size(-150, 0)
            };

            infoBox[ponto.Id] = new InfoBox(myOptions);
            infoBox[ponto.Id].marker = marker;

            infoBox[ponto.Id].listener = google.maps.event.addListener(marker, 'click', function (e) {
                    abrirInfoBox(ponto.Id, marker);
            });

            latlngbounds.extend(marker.position);

        });

        map.fitBounds(latlngbounds);

    });
	
}

carregarPontos();