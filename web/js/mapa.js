var map;
var idInfoBoxAberto;
var infoBox = [];

function initialize() {	
    var latlng = new google.maps.LatLng(-30.032217, -51.215083);
	
    var options = {
        zoom: 16,
	center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("mapa"), options);
    carregarPontos();
}

initialize();

function abrirInfoBox(id, marker) {
	if (typeof(idInfoBoxAberto) == 'number' && typeof(infoBox[idInfoBoxAberto]) == 'object') {
		infoBox[idInfoBoxAberto].close();
	}

	infoBox[id].open(map, marker);
	idInfoBoxAberto = id;
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