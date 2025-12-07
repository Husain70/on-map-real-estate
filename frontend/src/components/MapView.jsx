import React, { useMemo } from 'react';
import Map, { Marker } from 'react-map-gl/maplibre';
import 'maplibre-gl/dist/maplibre-gl.css';

const defaultView = {
  longitude: 46.6753,
  latitude: 24.7136,
  zoom: 7,
};

const mapStyle = 'https://basemaps.cartocdn.com/gl/positron-gl-style/style.json';

export function MapView({ transactions }) {
  const markers = useMemo(() => transactions || [], [transactions]);

  return (
    <div className="map-container">
      <Map
        initialViewState={defaultView}
        mapStyle={mapStyle}
        style={{ width: '100%', height: '100%' }}
        dragRotate={false}
        touchZoomRotate={false}
        attributionControl={true}
      >
        {markers.map((tx) => (
          <Marker
            key={tx.id}
            longitude={tx.longitude}
            latitude={tx.latitude}
            anchor="bottom"
          >
            <div
              className="tx-marker"
              title={`${tx.city} • ${tx.type} • ${tx.price.toLocaleString()}`}
            />
          </Marker>
        ))}
      </Map>
    </div>
  );
}
