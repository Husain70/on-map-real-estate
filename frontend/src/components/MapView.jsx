import React, { useMemo } from 'react';
import { GoogleMap, Marker, useJsApiLoader } from '@react-google-maps/api';

const defaultCenter = { lat: 24.7136, lng: 46.6753 };

function MapCanvas({ apiKey, transactions }) {
  const { isLoaded, loadError } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: apiKey,
  });

  const markers = useMemo(() => transactions || [], [transactions]);

  if (loadError) {
    return (
      <div className="panel map-skeleton">
        Google Maps failed to load: {loadError?.message || 'check your API key and network'}.
      </div>
    );
  }

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

export function MapView({ transactions }) {
  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
  const missingOrPlaceholder =
    !apiKey || /google_maps_api_key|your_google|replace_with/i.test(apiKey);

  if (missingOrPlaceholder) {
    return (
      <div className="panel map-skeleton">
        Add a valid <code>VITE_GOOGLE_MAPS_API_KEY</code> in <code>.env</code> to enable the map.
      </div>
    );
  }

  return <MapCanvas apiKey={apiKey} transactions={transactions} />;
}
