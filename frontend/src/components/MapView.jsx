import React, { useMemo } from 'react';
import { GoogleMap, Marker, useJsApiLoader } from '@react-google-maps/api';

const defaultCenter = { lat: 24.7136, lng: 46.6753 };

export function MapView({ transactions }) {
  const { isLoaded } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: import.meta.env.VITE_GOOGLE_MAPS_API_KEY || '',
  });

  const markers = useMemo(() => transactions || [], [transactions]);

  if (!isLoaded) {
    return <div className="panel map-skeleton">Loading map…</div>;
  }

  return (
    <GoogleMap
      mapContainerClassName="map-container"
      center={defaultCenter}
      zoom={7}
      options={{
        disableDefaultUI: true,
        styles: [],
        gestureHandling: 'greedy',
      }}
    >
      {markers.map((tx) => (
        <Marker
          key={tx.id}
          position={{ lat: tx.latitude, lng: tx.longitude }}
          title={`${tx.city} • ${tx.type} • ${tx.price.toLocaleString()}`}
        />
      ))}
    </GoogleMap>
  );
}
