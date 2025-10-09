import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FavoriteOffer from './favorite-offer';
import FavoriteOfferDetail from './favorite-offer-detail';
import FavoriteOfferUpdate from './favorite-offer-update';
import FavoriteOfferDeleteDialog from './favorite-offer-delete-dialog';

const FavoriteOfferRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FavoriteOffer />} />
    <Route path="new" element={<FavoriteOfferUpdate />} />
    <Route path=":id">
      <Route index element={<FavoriteOfferDetail />} />
      <Route path="edit" element={<FavoriteOfferUpdate />} />
      <Route path="delete" element={<FavoriteOfferDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FavoriteOfferRoutes;
