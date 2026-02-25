import './category-offers.scss';

import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Link as RouterLink, useLocation, useParams } from 'react-router-dom';

interface IOfferShortDTO {
  id?: number;
  name?: string;
  rating?: number;
}

const OFFERS_PAGE_SIZE = 5000;

const OfferTypeOffers = () => {
  const { offerTypeId } = useParams<'offerTypeId'>();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [offers, setOffers] = useState<IOfferShortDTO[]>([]);

  const offerTypeName = useMemo(() => {
    const params = new URLSearchParams(location.search);
    return params.get('name') ?? 'Selected offer type';
  }, [location.search]);

  useEffect(() => {
    if (!offerTypeId) return;

    const parsedOfferTypeId = Number(offerTypeId);
    if (!Number.isFinite(parsedOfferTypeId)) {
      setOffers([]);
      setError('Invalid offer type id.');
      return;
    }

    const loadOffers = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get<IOfferShortDTO[]>(
          `api/offers?page=0&size=${OFFERS_PAGE_SIZE}&sort=id,desc&offertypeId.equals=${parsedOfferTypeId}`,
        );
        setOffers(response.data ?? []);
      } catch {
        setOffers([]);
        setError('Could not load offers for this offer type right now.');
      } finally {
        setLoading(false);
      }
    };

    loadOffers();
  }, [offerTypeId]);

  const normalizedOffers = useMemo(
    () =>
      offers
        .filter(offer => offer.id)
        .map(offer => ({
          id: offer.id,
          name: offer.name ?? `Offer #${offer.id}`,
          rating: offer.rating,
        })),
    [offers],
  );

  return (
    <section className="category-offers-page">
      <div className="category-offers-header">
        <h1>{offerTypeName}</h1>
        <p>Offers for the selected offer type.</p>
        <RouterLink to="/" className="back-home-link">
          Back to home
        </RouterLink>
      </div>

      <section className="offer-search-results">
        <header className="offer-search-results-header">
          <h2>Available offers</h2>
          <span>{normalizedOffers.length} results</span>
        </header>

        {loading ? <div className="category-offers-state compact">Loading offers...</div> : null}
        {!loading && error ? <div className="category-offers-state error compact">{error}</div> : null}
        {!loading && !error && normalizedOffers.length === 0 ? (
          <div className="category-offers-state compact">No offers found for this offer type.</div>
        ) : null}

        {!loading && !error && normalizedOffers.length > 0 ? (
          <div className="offer-results-grid">
            {normalizedOffers.map(offer => (
              <RouterLink key={offer.id} to={`/offer/${offer.id}`} className="offer-result-card">
                <h3>{offer.name}</h3>
                <div className="offer-result-meta">
                  <span className="star">★</span>
                  <span>{typeof offer.rating === 'number' ? offer.rating.toFixed(1) : 'New'}</span>
                </div>
              </RouterLink>
            ))}
          </div>
        ) : null}
      </section>
    </section>
  );
};

export default OfferTypeOffers;
